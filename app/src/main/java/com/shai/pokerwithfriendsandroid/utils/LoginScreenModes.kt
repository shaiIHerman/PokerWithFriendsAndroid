package com.shai.pokerwithfriendsandroid.utils

    enum class LoginScreenModes {
        LOGIN, REGISTER, FORGOT_PASSWORD, RESET_PASSWORD;

        companion object {
            fun changeMode(screenMode: LoginScreenModes): LoginScreenModes {
                return if (screenMode == LOGIN) REGISTER
                else LOGIN
            }

            fun LoginScreenModes.isRegister(): Boolean {
                return this == REGISTER
            }

            fun LoginScreenModes.isForgetPassword(): Boolean {
                return this == FORGOT_PASSWORD
            }

            fun LoginScreenModes.isLogin(): Boolean {
                return this == LOGIN
            }

            fun LoginScreenModes.isResetPassword(): Boolean {
                return this == RESET_PASSWORD
            }
        }
}