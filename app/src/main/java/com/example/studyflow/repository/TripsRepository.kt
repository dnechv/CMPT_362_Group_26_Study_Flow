package com.example.studyflow.repository

import com.example.studyflow.database_cloud.Trip
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TripsRepository {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    fun getTrips(onSuccess: (List<Trip>) -> Unit, onFailure: () -> Unit) {
        if (auth.currentUser == null) {
            onFailure()
            return
        }
        db.collection("trips").whereEqualTo("user_id", auth.currentUser?.uid).get()
            .addOnSuccessListener { documents ->
                onSuccess(documents.map {
                    Trip(
                        it.id,
                        it.getString("route") ?: "",
                        it.getLong("stop")?.toInt() ?: -1
                    )
                })
            }.addOnFailureListener {
                onFailure()
            }
    }
}