package com.shai.pokerwithfriendsandroid.repositories

import com.google.firebase.auth.AuthCredential
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
        return safeApiCall {
            val documentReference =
                fireStoreClient.getDocumentReference("users", authUser?.uid ?: "")
            fireStoreClient.setUserDocumentData(documentReference, authUser?.email ?: "", name)
            userCache = fireStoreClient.getDocument<User>(documentReference)
            userCache
        }
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
                val documentReference =
                    fireStoreClient.getDocumentReference("users", authUser?.uid ?: "")
                fireStoreClient.setUserDocumentData(
                    documentReference, authUser?.email ?: "", authUser?.displayName ?: ""
                )
                userCache = fireStoreClient.getDocument<User>(documentReference)
                userCache
            }
        }
    }

    fun fetchAllCharactersByName(searchQuery: String): ApiOperation<List<User>> {
        TODO("Not yet implemented")
    }
}
