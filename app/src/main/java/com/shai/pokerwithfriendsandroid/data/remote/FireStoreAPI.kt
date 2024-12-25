package com.shai.pokerwithfriendsandroid.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shai.pokerwithfriendsandroid.data.remote.models.WithId
import kotlinx.coroutines.tasks.await

class FireStoreAPI {
    val firestore = Firebase.firestore

    /** Read Queries **/
    suspend inline fun <reified T> fetchCollectionItemsByLastUpdate(
        collectionName: String, lastSyncTimestamp: Timestamp
    ): List<T> {
        return firestore.collection(collectionName)
            .whereGreaterThan("dateUpdated", lastSyncTimestamp).get().await().toObjectsWithIds<T>()
    }

    suspend inline fun <reified T> fetchCollectionItemsBySearchableToken(
        collectionName: String,
        normalizedQuery: String
    ): List<T> {
        return firestore.collection(collectionName).orderBy("searchable_token")
            .startAt(normalizedQuery).endAt("$normalizedQuery\uf8ff").get().await()
            .toObjectsWithIds()
    }

    /** Write Queries **/
    suspend fun addDataToCollection(
        collectionName: String,
        data: Any,
    ): DocumentReference? {
        return firestore.collection(collectionName).add(data).await()
    }

    suspend fun setDocumentData(documentReference: DocumentReference, data: Any) {
        documentReference.set(data).await()
    }

    /** Extension Helpers **/

    inline fun <reified T> QuerySnapshot.toObjectsWithIds(): List<T> {
        return this.documents.map {
            val obj = it.toObject(T::class.java)
            // If the object is of type WithId, we set the ID
            (obj as? WithId)?.id = it.id
            obj
                ?: throw IllegalArgumentException("Failed to parse document into ${T::class.java.name}")
        }
    }

    inline fun <reified T> DocumentSnapshot.toObjectWithId(): T {
        val obj = this.toObject(T::class.java)
        // Add the document ID to the object (assuming the object has an `id` field)
        (obj as? WithId)?.id = this.id
        obj ?: throw IllegalArgumentException("Failed to parse document into ${T::class.java.name}")
        return obj
    }
}