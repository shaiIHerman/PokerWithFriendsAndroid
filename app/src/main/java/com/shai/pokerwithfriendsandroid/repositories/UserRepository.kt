package com.shai.pokerwithfriendsandroid.repositories

import com.google.firebase.auth.AuthCredential
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

    private var userCache: User? = null

    suspend fun registerNewUser(
        email: String, password: String, name: String
    ): ApiOperation<User?> {
        val authUser = authService.signUpWithEmail(email, password)
        return safeApiCall { register(authUser?.uid ?: "", name, email) }
    }

    private suspend fun register(uid: String, name: String, email: String): User? {
        val documentReference = fireStoreClient.getDocumentReference("users", uid)
        fireStoreClient.setUserDocumentData(documentReference, email, name)
        userCache = fireStoreClient.getDocument<User>(documentReference)
        return userCache
    }

    suspend fun loginUser(
        email: String,
        password: String,
    ): ApiOperation<User?> {
        val authUser = authService.loginWithEmail(email, password)
        return safeApiCall {
            userCache = fireStoreClient.getDocument<User>(
                collectionName = "users", docId = authUser?.uid ?: ""
            )
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
                userCache = userExists
                userCache
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
