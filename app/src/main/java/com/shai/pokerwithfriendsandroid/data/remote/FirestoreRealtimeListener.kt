package com.shai.pokerwithfriendsandroid.data.remote

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.shai.pokerwithfriendsandroid.data.remote.models.WithId
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FirestoreRealtimeListener {

//    private val firestore = FirebaseFirestore.getInstance()
    val firestore = Firebase.firestore
    // Create a flow to listen for Firestore document updates
    inline fun <reified T> listenToDocumentChanges(documentPath: String): Flow<T?> {
        return callbackFlow {
            // Reference to the document
            val documentRef = firestore.collection("games").document(documentPath)

            // Add the snapshot listener
            val listenerRegistration: ListenerRegistration = documentRef.addSnapshotListener { snapshot, exception ->
                // If there was an error with the listener, close the flow with the exception
                if (exception != null) {
                    close(exception) // Close the flow with the exception
                    return@addSnapshotListener
                }

                // Check if snapshot exists and send the value to the flow
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.toObjectWithId()) // Emit the snapshot value
                } else {
                    trySend(null) // Emit null if document doesn't exist
                }
            }

            // Handle the cancellation of the flow by removing the listener
            awaitClose {
                listenerRegistration.remove() // Remove the listener when the flow is cancelled
            }
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
