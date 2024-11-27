package com.shai.pokerwithfriendsandroid.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    val currentUser = authService.getCurrentUser()

    fun onForgotPassword() {
        _screenMode.update { return@update LoginScreenState.ForgotPassword }
    }

    fun onRegister() {
        _screenMode.update { return@update LoginScreenState.Register }
    }

    // Login with Google
    fun loginWithGoogle(idToken: String) {
        viewModelScope.launch {
            val user = authService.loginWithGoogle(idToken)
            if (user != null) {
                // Handle success (navigate to home screen)
            } else {
                // Handle failure (show error message)
            }
        }
    }

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
}
