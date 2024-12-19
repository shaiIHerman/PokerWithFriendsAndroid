package com.shai.pokerwithfriendsandroid.repositories

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.shai.pokerwithfriendsandroid.auth.AuthService
import com.shai.pokerwithfriendsandroid.db.remote.ApiOperation
import com.shai.pokerwithfriendsandroid.db.remote.FireStoreClient
import com.shai.pokerwithfriendsandroid.db.remote.models.User
import com.shai.pokerwithfriendsandroid.db.remote.safeApiCall
import javax.inject.Inject

class UserRepository @Inject constructor(
    private val authService: AuthService, private val fireStoreClient: FireStoreClient
) {

    fun getUserRef(): DocumentReference? = UserCache.getUserRef()
    fun getUser(): User? = UserCache.getUser()


    suspend fun updateCurrentUser(firebaseUser: FirebaseUser) : DocumentReference{
         val userRefCache = fireStoreClient.getDocumentReference(
            "users",
            firebaseUser.uid
        )
        val userCache = fireStoreClient.getDocument<User>(userRefCache!!)
        UserCache.updateUserCache(userRefCache, userCache!!)
        return userRefCache!!
    }

    suspend fun registerNewUser(
        email: String, password: String, name: String
    ): ApiOperation<User?> {
        val authUser = authService.signUpWithEmail(email, password)
        return safeApiCall { register(authUser?.uid ?: "", name, email) }
    }

    private suspend fun register(uid: String, name: String, email: String): User? {
        val userRefCache = fireStoreClient.getDocumentReference("users", uid)
        fireStoreClient.setUserDocumentData(userRefCache!!, email, name)
        val userCache = fireStoreClient.getDocument<User>(userRefCache!!)
        UserCache.updateUserCache(userRefCache, userCache!!)
        return userCache
    }

    suspend fun loginUser(
        email: String,
        password: String,
    ): ApiOperation<User?> {
        val authUser = authService.loginWithEmail(email, password)
        return safeApiCall {
            val userCache = fireStoreClient.getDocument<User>(
                collectionName = "users", docId = authUser?.uid ?: ""
            )
            val userRefCache = fireStoreClient.getDocumentReference("users", authUser?.uid ?: "")
            UserCache.updateUserCache(userRefCache, userCache!!)
            userCache
        }
    }

    suspend fun loginWithGoogle(credential: AuthCredential): ApiOperation<User?> {
        val authUser = authService.loginWithGoogle(credential)
        return safeApiCall {
            val userExists = fireStoreClient.getDocument<User>(
                collectionName = "users", docId = authUser?.uid ?: ""
            )
            if (userExists != null) {
                val userRefCache =
                    fireStoreClient.getDocumentReference("users", authUser?.uid ?: "")
                UserCache.updateUserCache(userRefCache, userExists)
                userExists
            } else {
                register(authUser?.uid ?: "", authUser?.displayName ?: "", authUser?.email ?: "")
            }
        }
    }

    suspend fun fetchUsersByName(searchQuery: String): ApiOperation<List<User>> {
        return safeApiCall {
            fireStoreClient.fetchUsersByName(searchQuery)
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
}
object UserCache {
    private var userCache: User? = null
    private var userRefCache: DocumentReference? = null

    fun getUserRef(): DocumentReference? = userRefCache
    fun getUser(): User? = userCache

    fun updateUserCache(userRef: DocumentReference, user: User) {
        userRefCache = userRef
        userCache = user
    }

    fun clearCache() {
        userRefCache = null
        userCache = null
    }
}