package com.shai.pokerwithfriendsandroid.db.remote

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shai.pokerwithfriendsandroid.db.local.models.Tournament
import com.shai.pokerwithfriendsandroid.db.remote.models.RemoteTournament
import com.shai.pokerwithfriendsandroid.db.remote.models.User
import com.shai.pokerwithfriendsandroid.repositories.LocalUser
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

    suspend fun fetchUsersByIds(userIds: List<String>): List<LocalUser> {
        val userCollection = firestore.collection("users")

        // Create a list of DocumentReferences from the userIds
        val documentReferences = userIds.map { userId -> userCollection.document(userId) }

        // Perform a batch fetch of the documents
        return coroutineScope {
            val deferredUsers = documentReferences.map { docRef ->
                async {
                    val snapshot = docRef.get().await()
                    val user = snapshot.toObject(User::class.java)

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

    suspend fun getDocumentReference(collectionName: String, docId: String): DocumentReference{
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

    suspend fun fetchTournaments(lastSyncTimestamp: Long?): List<Tournament> {
        var firestoreTimestamp = Timestamp(0, 0)
        // Here we convert the lastSyncTimestamp to a Timestamp object compatible with Firestore
        if (lastSyncTimestamp != null) {
            val seconds = lastSyncTimestamp / 1000
            val nanoseconds = (lastSyncTimestamp % 1000) * 1000000
            firestoreTimestamp = Timestamp(seconds, nanoseconds.toInt())
        }

        val query =
            firestore.collection("tournaments").whereGreaterThan("dateUpdated", firestoreTimestamp)
                .get().await()

        return query.documents.map { document ->
            val remoteTournament = document.toObject(RemoteTournament::class.java)
            val name = remoteTournament?.name ?: ""
            val buyIn = remoteTournament?.buyIn ?: ""
            val dateCreated =
                remoteTournament?.dateCreated?.toDate()?.time ?: System.currentTimeMillis()
            val id = document.id
            val playerIds = remoteTournament?.players?.map { it.id } ?: emptyList()
            val gameIds = remoteTournament?.games?.map { it.id } ?: emptyList()
            val adminId = remoteTournament?.admin?.id ?: ""
            Log.d("FireStoreClient", "Document ID: $id")
            Tournament(
                id = id,
                name = name,
                gameIds = gameIds,
                dateCreated = dateCreated,
                buyIn = buyIn,
                playerIds = playerIds,
                adminId = adminId
            )
        }
    }

    suspend fun fetchUsersByName(searchQuery: String): List<User> {
        val normalizedQuery = searchQuery.lowercase().trim()
        return firestore.collection("users").orderBy("searchable_token").startAt(normalizedQuery)
            .endAt("$normalizedQuery\uf8ff").get().await().map { document ->
                document.toObject(User::class.java)
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