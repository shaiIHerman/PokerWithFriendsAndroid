package com.shai.pokerwithfriendsandroid.auth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthCredential

interface AuthService {
    suspend fun loginWithEmail(email: String, password: String): FirebaseUser?
    suspend fun loginWithGoogle(credential: AuthCredential): FirebaseUser?
//    fun getGoogleSignInClient(): GoogleSignInClient
    fun getCurrentUser(): FirebaseUser?
    fun signOut()
}
