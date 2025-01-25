package com.shai.pokerwithfriendsandroid.data.local.db.utils

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.shai.pokerwithfriendsandroid.domain.models.LocalGame

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromStringList(playerIds: List<String>): String {
        return playerIds.joinToString(",") // Store the list as a comma-separated string
    }

    @TypeConverter
    fun toStringList(playerIds: String): List<String> {
        return playerIds.split(",") // Convert the stored string back to a list
    }

    @TypeConverter
    fun fromPlayerPositionList(positions: List<LocalGame.PlayerPosition>?): String {
        return gson.toJson(positions)
    }

    @TypeConverter
    fun toPlayerPositionList(positionsJson: String): List<LocalGame.PlayerPosition>? {
        val type = object : TypeToken<List<LocalGame.PlayerPosition>>() {}.type
        return gson.fromJson(positionsJson, type)
    }
}
