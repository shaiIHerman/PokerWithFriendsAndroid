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
import com.shai.pokerwithfriendsandroid.screens.states.AuthState
import com.shai.pokerwithfriendsandroid.screens.states.LoginScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {

    private var _screenMode = MutableStateFlow<LoginScreenState>(LoginScreenState.Login)
    val screenMode: StateFlow<LoginScreenState> = _screenMode

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

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

    fun onForgotPassword() {
        _screenMode.value = LoginScreenState.ForgotPassword
    }

    fun onRegister() {
        _screenMode.value = LoginScreenState.Register
    }

    fun onLogin() {
        _screenMode.value = LoginScreenState.Login
    }

    // Login with Google
    fun signInWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        viewModelScope.launch {
            try {
                _authState.value = AuthState.SigningIn
                val result = authService.loginWithGoogle(credential)
                if (result?.uid != null) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error("Authentication failed")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message.toString())
            }
        }
    }

    fun initiateGoogleSignIn() {
        try {
            _signInClient.value?.signInIntent
        } catch (e: Exception) {
            _authState.value = AuthState.Error(e.message.toString())
        }
    }

    // Login with Email/Password
    fun loginWithEmail() = viewModelScope.launch {
        _authState.value = AuthState.SigningIn
        val user = authService.loginWithEmail(email.value!!, password.value!!)
        if (user != null) {
            _authState.value = AuthState.Authenticated
            Log.d("LoginViewModel", "Login successful")
        } else {
            _authState.value = AuthState.Error("Authentication failed")
            Log.e("LoginViewModel", "Login failed")
        }
    }

    fun signUpWithEmail() = viewModelScope.launch {
        _authState.value = AuthState.SigningIn
        val user = authService.signUpWithEmail(email.value!!, password.value!!)
        if (user != null) {
            _authState.value = AuthState.Authenticated
            Log.d("LoginViewModel", "Login successful")
        } else {
            _authState.value = AuthState.Error("Authentication failed")
            Log.e("LoginViewModel", "Login failed")
        }
    }
}
