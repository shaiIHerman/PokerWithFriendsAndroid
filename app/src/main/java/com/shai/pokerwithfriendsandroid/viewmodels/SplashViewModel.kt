package com.shai.pokerwithfriendsandroid.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseUser
import com.shai.pokerwithfriendsandroid.auth.AuthService
import com.shai.pokerwithfriendsandroid.db.remote.safeApiCall
import com.shai.pokerwithfriendsandroid.repositories.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authService: AuthService, private val userRepository: UserRepository
) : ViewModel() {
    private val _isAuthenticated =
        MutableStateFlow<Boolean?>(null) // Initially null until we know the auth state
    val isAuthenticated: StateFlow<Boolean?> = _isAuthenticated

    fun checkAuthentication() = viewModelScope.launch {
        delay(1000)
        val currentUser = authService.getCurrentUser()
        Log.d("SplashViewModel", "User: ${currentUser?.email}")
        setUserCache(currentUser)
        _isAuthenticated.value = currentUser != null
    }

    private suspend fun setUserCache(currentUser: FirebaseUser?) {
        if (currentUser != null) {
            safeApiCall { userRepository.updateCurrentUser(currentUser) }.onFailure { //todo: handle failure
                Log.e("SplashViewModel", "Error setting user cache: ${it.message}")
            }.onSuccess { Log.d("SplashViewModel", "User cache set ${it.id}") }
        }
    }
}
