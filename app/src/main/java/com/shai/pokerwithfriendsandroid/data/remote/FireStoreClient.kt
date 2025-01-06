package com.shai.pokerwithfriendsandroid.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.shai.pokerwithfriendsandroid.data.remote.models.RemoteGame
import com.shai.pokerwithfriendsandroid.data.remote.models.RemoteTournament
import com.shai.pokerwithfriendsandroid.data.remote.models.RemoteUser
import com.shai.pokerwithfriendsandroid.domain.models.LocalUser
import com.shai.pokerwithfriendsandroid.domain.repositories.UserCache
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FireStoreClient @Inject constructor(private val fireStoreAPI: FireStoreAPI) {

    /** User Queries **/
    suspend fun fetchUsersByIds(userIds: List<String>): List<LocalUser> {
        val userCollection = fireStoreAPI.getCollection("users")

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

    suspend fun fetchUsersByName(searchQuery: String): List<RemoteUser> {
        val normalizedQuery = searchQuery.lowercase().trim()
        return fireStoreAPI.fetchCollectionItemsBySearchableToken("users", normalizedQuery)
    }

    suspend fun fetchUserByEmail(email: String): DocumentReference {
        return fireStoreAPI.getDocumentReferenceWithEqualQuery(
            collectionName = "users", field = "email", value = email
        )
    }

    suspend fun getUserDocumentReference(docId: String): DocumentReference {
        return fireStoreAPI.getDocumentReference(collectionName = "users", docId = docId)
    }

    suspend fun getUserByDocReference(userDocRef: DocumentReference): RemoteUser? {
        return fireStoreAPI.getDocument<RemoteUser>(userDocRef)
    }

    suspend fun getUserById(userId: String): RemoteUser? {
        return fireStoreAPI.getDocument<RemoteUser>(collectionName = "users", docId = userId)
    }

    private suspend fun updateUserWithTournamentReference(
        playerReference: DocumentReference, tournamentReference: DocumentReference
    ) {
        fireStoreAPI.updateDocumentWithReferences(
            collectionName = "users",
            docId = playerReference.id,
            field = "tournaments",
            value = FieldValue.arrayUnion(tournamentReference)
        )
    }

    /** Tournament Queries **/

    suspend fun fetchTournamentsByLastUpdateForUser(lastSyncTimestamp: Long?): List<RemoteTournament> {
        var firestoreTimestamp = Timestamp(0, 0)
        // Here we convert the lastSyncTimestamp to a Timestamp object compatible with Firestore
        if (lastSyncTimestamp != null) {
            val seconds = lastSyncTimestamp / 1000
            val nanoseconds = (lastSyncTimestamp % 1000) * 1000000
            firestoreTimestamp = Timestamp(seconds, nanoseconds.toInt())
        }

        val user = UserCache.getUser()

        return fireStoreAPI.fetchCollectionItemsByLastUpdate(
            documentReferences = user?.tournaments, lastSyncTimestamp = firestoreTimestamp
        )
    }

    suspend fun updateTournament(gameId: String, tournamentId: String): Void? {
        val gameRef = fireStoreAPI.getDocumentReference("games", gameId)
        return fireStoreAPI.updateDocumentWithReferences(
            collectionName = "tournaments",
            docId = tournamentId,
            field = "games",
            value = FieldValue.arrayUnion(gameRef)
        )
    }

    suspend fun createTournamentAndSyncUsers(data: HashMap<String, Any?>): String {
        val tournament = fireStoreAPI.createDocument("tournaments", data)
        val players = data["players"] as List<DocumentReference>
        val currentUser = UserCache.getUserRef()
        players.map {
            updateUserWithTournamentReference(it, tournament)
            if (it == currentUser) {
                val userCache = getUserByDocReference(currentUser)
                UserCache.updateUserCache(currentUser, userCache!!)
            }
        }
        // todo: this now has dual responsibility, consider splitting it up later
        return tournament.id
    }

    /** Game Queries **/

    suspend fun createGame(data: Any): String {
        return fireStoreAPI.createDocument("games", data).id
    }

    suspend fun getGameById(gameId: String): RemoteGame? {
        return fireStoreAPI.getDocument<RemoteGame>(collectionName = "games", docId = gameId)
    }

    suspend fun updatePlayerPositionsForGame(
        gameId: String, players: List<HashMap<String, Any>>, gameOver: Boolean
    ): Void? {
        val fieldsToUpdate = if (!gameOver) mapOf(
            "players" to players,
        ) else {
            mapOf(
                "players" to players,
                "active" to false,
            )
        }
        return fireStoreAPI.updateDocumentWithData(
            collectionName = "games", docId = gameId, fieldsToUpdate = fieldsToUpdate
        )
    }
}