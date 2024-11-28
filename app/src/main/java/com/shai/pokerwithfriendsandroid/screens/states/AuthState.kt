package com.shai.pokerwithfriendsandroid.screens.states

sealed class AuthState {
    object Initial : AuthState()
    object SigningIn : AuthState()
    object Authenticated : AuthState()
    data class Error(val message: String) : AuthState()
}