package com.shai.pokerwithfriendsandroid.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shai.pokerwithfriendsandroid.auth.AuthService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authService: AuthService
) : ViewModel() {
    private val _isAuthenticated =
        MutableStateFlow<Boolean?>(null) // Initially null until we know the auth state
    val isAuthenticated: StateFlow<Boolean?> = _isAuthenticated

    fun checkAuthentication() = viewModelScope.launch {
        delay(1000)
        val currentUser = authService.getCurrentUser()
        _isAuthenticated.value = currentUser != null
    }
}
