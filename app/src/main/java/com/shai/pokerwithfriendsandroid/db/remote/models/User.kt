package com.shai.pokerwithfriendsandroid.db.remote.models

import com.google.firebase.firestore.DocumentReference

data class User(
    val name: String = "", val email: String = "", val tournaments: List<DocumentReference>? = null
)