package com.shai.pokerwithfriendsandroid.db.remote.models

import com.google.firebase.firestore.DocumentReference
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val name: String = "", val tournaments: List<DocumentReference>? = null
)