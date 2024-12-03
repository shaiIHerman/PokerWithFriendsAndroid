package com.shai.pokerwithfriendsandroid.utils

fun String.validateEmail(): Pair<Boolean, String> {
    return when {
        this.isEmpty() -> {
            Pair(false, "Email can't be empty")
        }

        !android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches() -> {
            Pair(false, "Invalid email format")
        }

        else -> Pair(true, "")
    }
}