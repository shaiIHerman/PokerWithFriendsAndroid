package com.shai.pokerwithfriendsandroid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.google.firebase.auth.FirebaseUser
import com.shai.pokerwithfriendsandroid.auth.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    val currentUser = authService.getCurrentUser()

    // Login with Email/Password
    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            val user = authService.loginWithEmail(email, password)
            if (user != null) {
                // Handle success (navigate to home screen)
            } else {
                // Handle failure (show error message)
            }
        }
    }

    // Login with Google
//    fun loginWithGoogle(idToken: String) {
//        viewModelScope.launch {
//            val user = authService.loginWithGoogle(idToken)
//            if (user != null) {
//                // Handle success (navigate to home screen)
//            } else {
//                // Handle failure (show error message)
//            }
//        }
//    }
}
