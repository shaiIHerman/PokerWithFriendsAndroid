package com.shai.pokerwithfriendsandroid.db.remote

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shai.pokerwithfriendsandroid.db.remote.models.User
import com.shai.pokerwithfriendsandroid.repositories.AuthUser
import kotlinx.coroutines.tasks.await

class FireStoreClient {

    private val firestore = Firebase.firestore

    suspend fun getUsers(): ApiOperation<List<User>> {
        return safeApiCall {
            firestore.collection("users").get().await().map { document ->
                document.toObject(User::class.java)
            }
        }
    }

    suspend inline fun <reified T> getDocument(documentReference: DocumentReference): ApiOperation<T?> {
        return safeApiCall {
            documentReference.get().await().toObject(T::class.java)
        }
    }

    suspend fun setUserDocumentData(
        documentReference: DocumentReference, authUser: AuthUser, name: String
    ): Void {
        val userData = hashMapOf(
            "email" to authUser.email,
            "name" to name,  // Customize as needed
        )
        return documentReference.set(userData).await()
    }


    fun createDocument(collectionName: String, docId: String): DocumentReference {
        return firestore.collection(collectionName).document(docId)
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