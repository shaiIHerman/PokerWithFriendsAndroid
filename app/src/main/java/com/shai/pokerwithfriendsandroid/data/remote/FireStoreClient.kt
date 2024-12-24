package com.shai.pokerwithfriendsandroid.data.remote

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shai.pokerwithfriendsandroid.data.remote.models.RemoteTournament
import com.shai.pokerwithfriendsandroid.data.remote.models.RemoteUser
import com.shai.pokerwithfriendsandroid.domain.repositories.LocalUser
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

class FireStoreClient {

    val firestore = Firebase.firestore

    suspend fun fetchUsersByIds(userIds: List<String>): List<LocalUser> {
        val userCollection = firestore.collection("users")

        // Create a list of DocumentReferences from the userIds
        val documentReferences = userIds.map { userId -> userCollection.document(userId) }

        // Perform a batch fetch of the documents
        return coroutineScope {
            val deferredUsers = documentReferences.map { docRef ->
                async {
                    val snapshot = docRef.get().await()
                    val user = snapshot.toObject(RemoteUser::class.java)

                    // Only include in the list if the user is not null
                    if (user != null) {
                        // Map the User to LocalUser
                        LocalUser(name = user.name, email = user.email, id = docRef.id)
                    } else {
                        null
                    }
                }
            }

            // Collect all the results once all the async tasks are completed
            deferredUsers.awaitAll().filterNotNull() // Remove null entries
        }
    }


    suspend inline fun <reified T> getDocument(documentReference: DocumentReference): T? {
        return documentReference.get().await().toObject(T::class.java)
    }

    suspend inline fun <reified T> getDocument(collectionName: String, docId: String): T? {
        return firestore.collection(collectionName).document(docId).get().await()
            .toObject(T::class.java)
    }

    suspend fun getDocumentReference(collectionName: String, docId: String): DocumentReference {
        return firestore.collection(collectionName).document(docId)
    }

    //todo: consider making this a generic function also there's no need for a try catch here, because of the safeApiCall function
    suspend fun setUserDocumentData(
        documentReference: DocumentReference, email: String, name: String
    ) {
        // Validate inputs
        if (email.isEmpty() || name.isEmpty()) {
            throw IllegalArgumentException("Email or Name cannot be null or empty.")
        }

        Log.d("FirestoreClient", "Document Reference: ${documentReference.path}")
        val searchableToken = name.lowercase().trim()
        val userData = hashMapOf(
            "email" to email,
            "name" to name,
            "searchable_token" to searchableToken,
            "isAuthenticated" to true
        )

        try {
            documentReference.set(userData).await()
        } catch (e: Exception) {
            Log.e("FirestoreClient", "Error setting user data: ${e.message}", e)
        }
    }

    //todo: consider making this a generic function also there's no need for a try catch here, because of the safeApiCall function
    suspend fun addUserDocumentData(
        name: String, email: String
    ): DocumentReference? {
        var newEmail = email
        if (name.isEmpty()) {
            throw IllegalArgumentException("Name cannot be null or empty.")
        }

        if (newEmail.isEmpty()) {
            newEmail = "$name@$name.com"
        }

        val searchableToken = name.lowercase().trim()
        val userData = hashMapOf(
            "email" to newEmail,
            "name" to name,
            "searchable_token" to searchableToken,
            "isAuthenticated" to false
        )

        try {
            return firestore.collection("users").add(userData).await()
        } catch (e: Exception) {
            Log.e("FirestoreClient", "Error setting user data: ${e.message}", e)
            return null
        }
    }

    suspend fun createDocument(collectionName: String, data: Any): DocumentReference? {
        return firestore.collection(collectionName).add(data).await()
    }

    suspend fun fetchTournaments(lastSyncTimestamp: Long?): List<RemoteTournament> {
        var firestoreTimestamp = Timestamp(0, 0)
        // Here we convert the lastSyncTimestamp to a Timestamp object compatible with Firestore
        if (lastSyncTimestamp != null) {
            val seconds = lastSyncTimestamp / 1000
            val nanoseconds = (lastSyncTimestamp % 1000) * 1000000
            firestoreTimestamp = Timestamp(seconds, nanoseconds.toInt())
        }

        return firestore.collection("tournaments")
            .whereGreaterThan("dateUpdated", firestoreTimestamp).get().await()
            .toObjectsWithIds<RemoteTournament>()
//        return firestore.collection("tournaments")
//            .whereGreaterThan("dateUpdated", firestoreTimestamp).get().await().map {
//                val remoteTournament = it.toObject(RemoteTournament::class.java)
//                remoteTournament.id = it.id
//                remoteTournament
//            }
    }

    inline fun <reified T> QuerySnapshot.toObjectsWithIds(): List<T> {
        return this.documents.map {
            val obj = it.toObject(T::class.java)
            // Add the document ID to the object (assumes the object has an `id` field)
            obj?.apply {
                // Assuming your object has a `id` field, set the document ID here
                if (this is RemoteTournament) { // Replace with the type you're working with
                    this.id = it.id
                }
            }
                ?: throw IllegalArgumentException("Failed to parse document into ${T::class.java.name}")
        }
    }

    inline fun <reified T> DocumentSnapshot.toObjectWithId(): T? {
        val obj = this.toObject(T::class.java)
        // Add the document ID to the object (assuming the object has an `id` field)
        obj?.apply {
            if (this is RemoteTournament) { // Replace with your specific class type
                this.id = this@toObjectWithId.id
            }
        }
        return obj
    }

    suspend fun fetchUsersByName(searchQuery: String): List<RemoteUser> {
        val normalizedQuery = searchQuery.lowercase().trim()
        return firestore.collection("users").orderBy("searchable_token").startAt(normalizedQuery)
            .endAt("$normalizedQuery\uf8ff").get().await().map { document ->
                document.toObject(RemoteUser::class.java)
            }
    }

    suspend fun fetchUserByEmail(email: String): DocumentReference {
        return firestore.collection("users").whereEqualTo("email", email).get()
            .await().documents[0].reference
    }

    suspend fun updateTournament(gameId: DocumentReference?, tournamentId: String): Void? {
        return firestore.collection("tournaments").document(tournamentId).update(
            "games", FieldValue.arrayUnion(gameId), "dateUpdated", FieldValue.serverTimestamp()
        ).await()
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