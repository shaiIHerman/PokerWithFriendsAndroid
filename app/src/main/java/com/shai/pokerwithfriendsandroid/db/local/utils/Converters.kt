package com.shai.pokerwithfriendsandroid.db.local.utils

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromStringList(playerIds: List<String>): String {
        return playerIds.joinToString(",") // Store the list as a comma-separated string
    }

    @TypeConverter
    fun toStringList(playerIds: String): List<String> {
        return playerIds.split(",") // Convert the stored string back to a list
    }
}
