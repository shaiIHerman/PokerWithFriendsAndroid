package com.shai.pokerwithfriendsandroid.utils

import androidx.compose.ui.graphics.Color
import com.shai.pokerwithfriendsandroid.domain.models.GameStatus
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

fun String.validateName(): Pair<Boolean, String> {
    return when {
        this.isEmpty() -> {
            Pair(false, "Name can't be empty")
        }

        else -> Pair(true, "")
    }
}

fun Long.toStringDate(): String {
    val instant = Instant.ofEpochMilli(this)
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")
        .withZone(ZoneId.systemDefault()) // Use system default timezone
    return formatter.format(instant)
}

fun GameStatus.asColor(): Color {
    return when (this) {
        GameStatus.Active -> Color.Green
        GameStatus.Completed -> Color.Red
        GameStatus.Unknown -> Color.Yellow
    }
}