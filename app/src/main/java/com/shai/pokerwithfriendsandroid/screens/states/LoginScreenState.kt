package com.shai.pokerwithfriendsandroid.screens.states

sealed class LoginScreenState {

    fun LoginScreenState.isLogin() = this == Login
    fun LoginScreenState.isRegister() = this == Register
    fun LoginScreenState.isForgot() = this == ForgotPassword

    object Login : LoginScreenState()
    object Register : LoginScreenState()
    object ForgotPassword : LoginScreenState()
}