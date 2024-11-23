package com.shai.pokerwithfriendsandroid.auth

import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.android.gms.auth.api.signin.GoogleSignInClient

interface AuthService {
    suspend fun loginWithEmail(email: String, password: String): FirebaseUser?
//    suspend fun loginWithGoogle(idToken: String): FirebaseUser?
//    fun getGoogleSignInClient(): GoogleSignInClient
    fun getCurrentUser(): FirebaseUser?
    fun signOut()
}
