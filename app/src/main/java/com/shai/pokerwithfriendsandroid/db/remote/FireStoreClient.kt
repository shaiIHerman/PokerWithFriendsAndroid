package com.shai.pokerwithfriendsandroid.db.remote

import android.util.Log
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shai.pokerwithfriendsandroid.db.local.models.Tournament
import com.shai.pokerwithfriendsandroid.db.remote.models.User
import kotlinx.coroutines.tasks.await

class FireStoreClient {

    val firestore = Firebase.firestore

    suspend fun getUsers(): ApiOperation<List<User>> {
        return safeApiCall {
            firestore.collection("users").get().await().map { document ->
                document.toObject(User::class.java)
            }
        }
    }

    suspend inline fun <reified T> getDocument(documentReference: DocumentReference): T? {
        return documentReference.get().await().toObject(T::class.java)
    }

    suspend inline fun <reified T> getDocument(collectionName: String, docId: String): T? {
        return firestore.collection(collectionName).document(docId).get().await()
            .toObject(T::class.java)
    }

    suspend fun setUserDocumentData(
        documentReference: DocumentReference, email: String, name: String
    ) {
        // Validate inputs
        if (email.isEmpty() || name.isEmpty()) {
            throw IllegalArgumentException("Email or Name cannot be null or empty.")
        }

        Log.d("FirestoreClient", "Document Reference: ${documentReference.path}")

        val userData = hashMapOf(
            "email" to email, "name" to name
        )

        try {
            documentReference.set(userData).await()
        } catch (e: Exception) {
            Log.e("FirestoreClient", "Error setting user data: ${e.message}", e)
        }
    }

    suspend fun createDocument(collectionName: String, data: Any): DocumentReference {
        return firestore.collection(collectionName).add(data).await()
    }

    fun getDocumentReference(collectionName: String, docId: String): DocumentReference {
        return firestore.collection(collectionName).document(docId)
    }

    suspend fun fetchTournaments(): List<Tournament> {
        // Imagine you're fetching data from Firebase Firestore
        // For example purposes, we'll return a mock list
        return listOf(
            Tournament(id = "3", name = "Spring Championship", gamesPlayed = 7)
        )
    }

    fun addTournament(tournament: Tournament) {

    }
}

suspend fun <T> safeApiCall(apiCall: suspend () -> T): ApiOperation<T> {
    return try {
        ApiOperation.Success(data = apiCall())
    } catch (e: Exception) {
        ApiOperation.Failure(e)
    }
}

sealed interface ApiOperation<T> {
    data class Success<T>(val data: T) : ApiOperation<T>
    data class Failure<T>(val exception: Exception) : ApiOperation<T>

    fun <R> mapSuccess(transform: (T) -> R): ApiOperation<R> {
        return when (this) {
            is Success -> Success(transform(data))
            is Failure -> Failure(exception)
        }
    }

    suspend fun onSuccess(block: suspend (T) -> Unit): ApiOperation<T> {
        if (this is Success) block(data)
        return this
    }

    fun onFailure(block: (Exception) -> Unit): ApiOperation<T> {
        if (this is Failure) block(exception)
        return this
    }
}