package com.shai.pokerwithfriendsandroid.data.remote

import com.google.firebase.Timestamp
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shai.pokerwithfriendsandroid.data.remote.models.WithId
import kotlinx.coroutines.tasks.await

class FireStoreAPI {
    val firestore = Firebase.firestore

    /** Generic Queries **/

    fun getCollection(collectionName: String): CollectionReference {
        return firestore.collection(collectionName)
    }

    suspend inline fun <reified T> getDocument(documentReference: DocumentReference): T? {
        return documentReference.get().await().toObjectWithId()
    }

    suspend inline fun <reified T> getDocument(collectionName: String, docId: String, shouldExist: Boolean = true): T? {
        val documentSnapshot =  firestore.collection(collectionName).document(docId).get().await()
        if (!documentSnapshot.exists()) {
            if (shouldExist) {
                throw IllegalStateException("Document with ID $docId in collection $collectionName does not exist.")
            }
            return null
        }

        return documentSnapshot.toObjectWithId()
    }

    suspend inline fun <reified T> fetchUpdatedGamesForTournament(
        collectionName: String,
        filterFieldName: String,
        filterFieldValue: Any,
        knownGameIds: List<String>,
    ): List<T> {
        // Split knownGameIds into chunks of 10 to handle Firestore's limit
        val queryResults = mutableListOf<T>()

        if (knownGameIds.isEmpty()) {
            // If no knownGameIds, fetch all matching documents
            return firestore.collection(collectionName)
                .whereEqualTo(filterFieldName, filterFieldValue)
                .get()
                .await()
                .toObjectsWithIds()
        }

        knownGameIds.chunked(10).forEach { chunk ->
            val snapshot = firestore.collection(collectionName)
                .whereEqualTo(filterFieldName, filterFieldValue) // Filter by the tournament ID
                .whereNotIn(FieldPath.documentId(), chunk) // Exclude known game IDs
                .get()
                .await()

            queryResults.addAll(snapshot.toObjectsWithIds())
        }

        return queryResults
    }

    suspend fun getDocumentReference(collectionName: String, docId: String): DocumentReference {
        return firestore.collection(collectionName).document(docId)
    }

    /** Read Queries **/

    suspend inline fun <reified T> fetchCollectionItemsByLastUpdate(
        collectionName: String,
        lastSyncTimestamp: Timestamp,
        conditions: List<FirestoreCondition> = emptyList()
    ): List<T> {
        var query =
            firestore.collection(collectionName).whereGreaterThan("dateUpdated", lastSyncTimestamp)
        query = addConditionsToQuery(query, conditions)
        return query.get().await().toObjectsWithIds<T>()
    }


    suspend inline fun <reified T> fetchCollectionItemsByLastUpdate(
        documentReferences: List<DocumentReference>?,
        lastSyncTimestamp: Timestamp,
    ): List<T> {
        val validDocumentReferences = mutableListOf<T>()
        if (documentReferences == null) {
            return validDocumentReferences
        }
        for (docRef in documentReferences) {
            val documentSnapshot = docRef.get().await()
            val dateUpdated = documentSnapshot.getTimestamp("dateUpdated")
            if (dateUpdated != null && dateUpdated > lastSyncTimestamp) {
                validDocumentReferences.add(documentSnapshot.toObjectWithId())
            }
        }
        return validDocumentReferences
    }

    suspend inline fun <reified T> fetchCollectionItemsBySearchableToken(
        collectionName: String, normalizedQuery: String
    ): List<T> {
        return firestore.collection(collectionName).orderBy("searchable_token")
            .startAt(normalizedQuery).endAt("$normalizedQuery\uf8ff").get().await()
            .toObjectsWithIds()
    }

    suspend fun getDocumentReferenceWithEqualQuery(
        collectionName: String, field: String, value: String
    ): DocumentReference {
        return firestore.collection(collectionName).whereEqualTo(field, value).get()
            .await().documents[0].reference
    }

    /** Create Queries **/
    suspend fun createDocument(collectionName: String, data: Any): DocumentReference {
        return firestore.collection(collectionName).add(data).await()
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

    suspend fun updateDocumentWithReferences(
        collectionName: String,
        docId: String,
        field: String,
        value: FieldValue,
    ): Void? {
        return firestore.collection(collectionName).document(docId).update(
            field, value, "dateUpdated", FieldValue.serverTimestamp()
        ).await()
    }

    suspend fun updateDocumentWithData(
        collectionName: String, docId: String, fieldsToUpdate: Map<String, Any>
    ): Void? {
        return firestore.collection(collectionName).document(docId).update(
            fieldsToUpdate
        ).await()
    }

    /** Extensions & Helpers **/

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

    fun addConditionsToQuery(query: Query, conditions: List<FirestoreCondition>): Query {
        var localQuery = query
        conditions.forEach { condition ->
            localQuery = when (condition) {
                is FirestoreCondition.EqualTo -> {
                    localQuery.whereEqualTo(condition.field, condition.value)
                }

                is FirestoreCondition.GreaterThan -> {
                    localQuery.whereGreaterThan(condition.field, condition.value)
                }
                // Handle other conditions here as needed
            }
        }
        return localQuery
    }
}

sealed class FirestoreCondition {
    data class EqualTo(val field: String, val value: Any) : FirestoreCondition()
    data class GreaterThan(val field: String, val value: Any) : FirestoreCondition()
    // Add other conditions as needed, like whereLessThan, whereArrayContains, etc.
}