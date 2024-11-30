package com.shai.pokerwithfriendsandroid.auth

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth, private val googleSignInClient: GoogleSignInClient
) : AuthService {

    override suspend fun loginWithEmail(email: String, password: String): FirebaseUser? {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            authResult.user
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun loginWithGoogle(credential: AuthCredential): FirebaseUser? {
        return try {
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            authResult.user
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun signUpWithEmail(email: String, password: String): FirebaseUser? {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            if (result.user != null) {
                result.user
            } else {
                null
            }
        } catch (e: FirebaseAuthException) {
            Log.e("AuthViewModel", "Firebase sign-up failed", e)
            null
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Sign-up failed", e)
            null
        }
    }

    override fun getGoogleSignInClient(): GoogleSignInClient = googleSignInClient

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
