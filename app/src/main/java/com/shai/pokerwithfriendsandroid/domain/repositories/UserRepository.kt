package com.shai.pokerwithfriendsandroid.domain.repositories

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.shai.pokerwithfriendsandroid.auth.AuthService
import com.shai.pokerwithfriendsandroid.data.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.data.remote.models.RemoteUser
import com.shai.pokerwithfriendsandroid.domain.models.LocalUser
import com.shai.pokerwithfriendsandroid.utils.ApiOperation
import com.shai.pokerwithfriendsandroid.utils.safeApiCall
import com.shai.pokerwithfriendsandroid.viewmodels.TournamentData
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val authService: AuthService, private val fireStoreClient: FireStoreClient
) {

    fun getUserRef(): DocumentReference? = UserCache.getUserRef()
    fun getUser(): RemoteUser? = UserCache.getUser()


    suspend fun updateCurrentUser(firebaseUser: FirebaseUser): DocumentReference {
        val userRefCache = fireStoreClient.getUserDocumentReference(firebaseUser.uid)
        val userCache = fireStoreClient.getUserByDocReference(userRefCache)
        UserCache.updateUserCache(userRefCache, userCache!!)
        return userRefCache
    }

    suspend fun registerNewUser(
        email: String, password: String, name: String
    ): ApiOperation<RemoteUser?> {
        val authUser = authService.signUpWithEmail(email, password)
        return safeApiCall { register(authUser?.uid ?: "", name, email) }
    }

    private suspend fun register(uid: String, name: String, email: String): RemoteUser {
        val userRefCache = fireStoreClient.getUserDocumentReference( uid)
        fireStoreClient.setUserDocumentData(userRefCache, email, name)
        val userCache = fireStoreClient.getUserByDocReference(userRefCache)
        UserCache.updateUserCache(userRefCache, userCache!!)
        return userCache
    }

    suspend fun loginUser(
        email: String,
        password: String,
    ): ApiOperation<RemoteUser?> {
        val authUser = authService.loginWithEmail(email, password)
        return safeApiCall {
            val userCache = fireStoreClient.getUserById(userId = authUser?.uid ?: "")
            val userRefCache = fireStoreClient.getUserDocumentReference(authUser?.uid ?: "")
            UserCache.updateUserCache(userRefCache, userCache!!)
            userCache
        }
    }

    suspend fun loginWithGoogle(credential: AuthCredential): ApiOperation<RemoteUser?> {
        val authUser = authService.loginWithGoogle(credential)
        return safeApiCall {
            val userExists = fireStoreClient.getUserById(userId = authUser?.uid ?: "")
            if (userExists != null) {
                val userRefCache =
                    fireStoreClient.getUserDocumentReference(authUser?.uid ?: "")
                UserCache.updateUserCache(userRefCache, userExists)
                userExists
            } else {
                register(authUser?.uid ?: "", authUser?.displayName ?: "", authUser?.email ?: "")
            }
        }
    }

    suspend fun fetchUsersByName(searchQuery: String): ApiOperation<List<LocalUser>> {
        //todo: fix this id
        return safeApiCall {
            fireStoreClient.fetchUsersByName(searchQuery).map { LocalUser(it.name, it.email, "") }
        }
    }

    suspend fun createPlayer(name: String, email: String): ApiOperation<DocumentReference?> {
        return safeApiCall {
            fireStoreClient.addUserDocumentData(name, email)
        }
    }

    suspend fun getUserByEmail(email: String): ApiOperation<DocumentReference> {
        return safeApiCall {
            fireStoreClient.fetchUserByEmail(email)
        }
    }

    suspend fun fetchUsersByIds(strings: List<String>): ApiOperation<List<LocalUser>> {
        return safeApiCall {
            fireStoreClient.fetchUsersByIds(strings)
        }
    }
}

object UserCache {
    private var userCache: RemoteUser? = null
    private var userRefCache: DocumentReference? = null

    fun getUserRef(): DocumentReference? = userRefCache
    fun getUser(): RemoteUser? = userCache

    fun updateUserCache(userRef: DocumentReference, user: RemoteUser) {
        userRefCache = userRef
        userCache = user
    }

    fun clearCache() {
        userRefCache = null
        userCache = null
    }
}