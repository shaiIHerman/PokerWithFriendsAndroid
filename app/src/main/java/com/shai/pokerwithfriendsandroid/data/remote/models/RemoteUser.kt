package com.shai.pokerwithfriendsandroid.data.remote.models

import com.google.firebase.firestore.DocumentReference
import com.shai.pokerwithfriendsandroid.domain.models.LocalUser

data class RemoteUser(
    override var id: String = "",
    val name: String = "",
    val email: String = "",
    val tournaments: List<DocumentReference>? = null
) : WithId {
    constructor() : this("", "", "", null)
}

fun RemoteUser.toLocalUser(): LocalUser {
    return LocalUser(id = id, name = name, email = email)
}