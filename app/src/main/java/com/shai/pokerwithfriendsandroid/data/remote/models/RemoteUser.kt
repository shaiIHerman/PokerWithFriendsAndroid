package com.shai.pokerwithfriendsandroid.data.remote.models

import com.google.firebase.firestore.DocumentReference

data class RemoteUser(
    val name: String = "", val email: String = "", val tournaments: List<DocumentReference>? = null
)