package com.shai.pokerwithfriendsandroid.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shai.pokerwithfriendsandroid.data.remote.models.RemoteTournament
import com.shai.pokerwithfriendsandroid.data.remote.models.RemoteUser
import com.shai.pokerwithfriendsandroid.domain.repositories.LocalUser
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FireStoreClient @Inject constructor(private val fireStoreAPI: FireStoreAPI) {

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

        val searchableToken = name.lowercase().trim()
        val userData = hashMapOf(
            "email" to email,
            "name" to name,
            "searchable_token" to searchableToken,
            "isAuthenticated" to true
        )
        fireStoreAPI.setDocumentData(documentReference = documentReference, data = userData)
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
        return fireStoreAPI.addDataToCollection("users", data = userData)
    }

    suspend fun createDocument(collectionName: String, data: Any): String {
        return firestore.collection(collectionName).add(data).await().id
    }

    /** Tournament Queries **/

    suspend fun fetchTournamentsByLastUpdate(lastSyncTimestamp: Long?): List<RemoteTournament> {
        var firestoreTimestamp = Timestamp(0, 0)
        // Here we convert the lastSyncTimestamp to a Timestamp object compatible with Firestore
        if (lastSyncTimestamp != null) {
            val seconds = lastSyncTimestamp / 1000
            val nanoseconds = (lastSyncTimestamp % 1000) * 1000000
            firestoreTimestamp = Timestamp(seconds, nanoseconds.toInt())
        }
        return fireStoreAPI.fetchCollectionItemsByLastUpdate("tournaments", firestoreTimestamp)
    }


    suspend fun fetchUsersByName(searchQuery: String): List<RemoteUser> {
        val normalizedQuery = searchQuery.lowercase().trim()
        return fireStoreAPI.fetchCollectionItemsBySearchableToken("users", normalizedQuery)
    }

    suspend fun fetchUserByEmail(email: String): DocumentReference {
        return firestore.collection("users").whereEqualTo("email", email).get()
            .await().documents[0].reference
    }

    suspend fun updateTournament(gameId: String, tournamentId: String): Void? {
        val gameRef = firestore.collection("games").document(gameId)
        return firestore.collection("tournaments").document(tournamentId).update(
            "games", FieldValue.arrayUnion(gameRef), "dateUpdated", FieldValue.serverTimestamp()
        ).await()
    }
}