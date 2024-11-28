package com.shai.pokerwithfriendsandroid.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.GoogleAuthProvider
import com.shai.pokerwithfriendsandroid.auth.AuthService
import com.shai.pokerwithfriendsandroid.screens.states.LoginScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    private var _screenMode = MutableStateFlow<LoginScreenState>(LoginScreenState.Login)
    val screenMode: StateFlow<LoginScreenState> = _screenMode

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _confirmPassword = MutableLiveData("")
    val confirmPassword: LiveData<String> = _confirmPassword

    private val _signInClient = MutableLiveData(authService.getGoogleSignInClient())
    val googleSignInClient: LiveData<GoogleSignInClient> = _signInClient


    fun updateEmail(newEmail: String) {
        Log.d("LoginViewModel", "Updating email to: $newEmail")
        _email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        _password.value = newPassword
    }

    fun updateName(name: String) {
        _name.value = name
    }

    fun updateConfirmPassword(newPassword: String) {
        _confirmPassword.value = newPassword
    }

    val currentUser = authService.getCurrentUser()

    fun onForgotPassword() {
        _screenMode.update { return@update LoginScreenState.ForgotPassword }
    }

    fun onRegister() {
        _screenMode.update { return@update LoginScreenState.Register }
    }

    // Login with Google
    fun signInWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        viewModelScope.launch {
            try {
                // Perform Firebase sign-in using the Google account credentials
                val result = authService.loginWithGoogle(credential)

                // Check if sign-in was successful
                if (result?.uid != null) {
                    // Successfully authenticated
                    Log.d("LoginViewModel", "Google Sign-In successful")
//                    _authState.value = AuthState.Authenticated(result.user!!.email ?: "Unknown")
                } else {
                    // Authentication failed (although this shouldn't happen)
//                    _authState.value = AuthState.Error("Authentication failed")
                    Log.d("LoginViewModel", "Authentication failed")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Google Sign-In failed", e)
//                _authState.value = AuthState.Error(e.localizedMessage)
            }
        }
    }

    fun initiateGoogleSignIn() {
        try {
            // This launches the Google sign-in intent
            val signInIntent = _signInClient.value?.signInIntent
            Log.d("LoginViewModel", "Launching Google Sign-In intent")
//            _authState.value = AuthState.SigningIn
        } catch (e: Exception) {
            Log.e("LoginViewModel", "Error initiating Google Sign-In", e)
//            _authState.value = AuthState.Error(e.localizedMessage)
        }
    }

    // Login with Email/Password
    fun loginWithEmail() = viewModelScope.launch {
        val user = authService.loginWithEmail(email.value!!, password.value!!)
        if (user != null) {
            Log.d("LoginViewModel", "Login successful")
        } else {
            Log.d("LoginViewModel", "Login failed")
        }
    }
}
