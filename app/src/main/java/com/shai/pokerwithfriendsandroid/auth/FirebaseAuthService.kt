package com.shai.pokerwithfriendsandroid.auth

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthService @Inject constructor(
    private val firebaseAuth: FirebaseAuth
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

//    override fun getGoogleSignInClient(): GoogleSignInClient {
//        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//            .requestIdToken("YOUR_WEB_CLIENT_ID")
//            .requestEmail()
//            .build()
//        return GoogleSignIn.getClient(context, gso) // You need to pass context here, consider making it injectable or passing in constructor
//    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
