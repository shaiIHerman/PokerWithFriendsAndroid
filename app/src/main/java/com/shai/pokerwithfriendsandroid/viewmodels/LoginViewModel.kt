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
import com.shai.pokerwithfriendsandroid.repositories.UserRepository
import com.shai.pokerwithfriendsandroid.screens.states.AuthState
import com.shai.pokerwithfriendsandroid.screens.states.LoginScreenState
import com.shai.pokerwithfriendsandroid.utils.PasswordValidator
import com.shai.pokerwithfriendsandroid.utils.validateEmail
import com.shai.pokerwithfriendsandroid.utils.validateName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authService: AuthService, private val userRepository: UserRepository
) : ViewModel() {

    private var _screenMode = MutableStateFlow<LoginScreenState>(LoginScreenState.Login)
    val screenMode: StateFlow<LoginScreenState> = _screenMode

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initial)
    val authState: StateFlow<AuthState> = _authState

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _isEmailValid = MutableLiveData(Pair(true, ""))
    val isEmailValid: LiveData<Pair<Boolean, String>> = _isEmailValid

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _isPasswordValid = MutableLiveData(Pair(true, ""))
    val isPasswordValid: LiveData<Pair<Boolean, String>> = _isPasswordValid

    private val _name = MutableLiveData("")
    val name: LiveData<String> = _name

    private val _isNameValid = MutableLiveData(Pair(true, ""))
    val isNameValid: LiveData<Pair<Boolean, String>> = _isNameValid

    private val _confirmPassword = MutableLiveData("")
    val confirmPassword: LiveData<String> = _confirmPassword

    private val _isPasswordConfirmValid = MutableLiveData(Pair(true, ""))
    val isPasswordConfirmValid: LiveData<Pair<Boolean, String>> = _isPasswordConfirmValid

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
    fun loginWithEmail() {
        _isEmailValid.value = _email.value?.validateEmail()
        if (_isEmailValid.value?.first == false) {
            return
        } else {
            viewModelScope.launch {
                _authState.value = AuthState.SigningIn
                userRepository.loginUser(email.value!!, password.value!!).onSuccess { user ->
                    _authState.value = AuthState.Authenticated
                    Log.d("LoginViewModel", "Login successful - ${user?.name}")
                }.onFailure {
                    _authState.value = AuthState.Error("Authentication failed")
                    Log.e("LoginViewModel", "Login failed - ${it.message}")
                }

            }
        }
    }

    fun signUpWithEmail() {
        // Validate fields
        val validations = listOf(
            ::validateEmail to _isEmailValid,
            ::validateName to _isNameValid,
            ::validatePassword to _isPasswordValid,
            ::validateConfirmPassword to _isPasswordConfirmValid
        )

        // Run all validations
        validations.forEach { (validate, _) ->
            validate()
        }

        // Check for validation errors
        if (hasValidationErrors()) {
            _authState.value = AuthState.Error("Authentication failed")
            Log.e("LoginViewModel", "Login failed")
            return
        }

        // Proceed with user registration
        viewModelScope.launch {
            _authState.value = AuthState.SigningIn
            userRepository.registerNewUser(
                email = _email.value!!, password = _password.value!!, name = _name.value!!
            ).onSuccess { user ->
                _authState.value = AuthState.Authenticated
                Log.d("LoginViewModel", "Login successful - ${user?.name}")
            }.onFailure {
                _authState.value = AuthState.Error("Authentication failed")
                Log.e("LoginViewModel", "Login failed - ${it.message}")
            }
        }
    }

    // Helper function to check for validation errors
    private fun hasValidationErrors(): Boolean {
        return listOf(
            _isEmailValid.value,
            _isPasswordConfirmValid.value,
            _isPasswordValid.value,
            _isNameValid.value
        ).any { it?.first == false || it == null }
    }

    private fun validateName() {
        _isNameValid.value = _name.value?.validateName()
    }

    private fun validateConfirmPassword() {
        _isPasswordConfirmValid.value =
            PasswordValidator().checkConfirmPassword(_password.value, confirmPassword.value)
    }

    fun validateEmail() {
        _isEmailValid.value = _email.value?.validateEmail()
    }

    fun validatePassword() {
        _isPasswordValid.value = PasswordValidator().validate(password.value!!)
    }
}
