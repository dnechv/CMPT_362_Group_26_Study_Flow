package com.example.studyflow.view_models

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.studyflow.database_cloud.Trip
import com.example.studyflow.repository.TripsRepository
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TransitViewModel : ViewModel() {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private var trips = listOf<Trip>()

    fun loadTrips() {
        if (auth.currentUser == null) return
        db.collection("trips").whereEqualTo("user_id", auth.currentUser!!.uid).get()
            .addOnSuccessListener { documents ->
                Log.d("TransitViewModel", "Loaded favourite trips (${documents.size()})")
                trips = documents.map {
                    Trip(
                        it.id,
                        it.getString("route") ?: "",
                        it.getLong("stop")?.toInt() ?: -1
                    )
                }
            }
    }

    fun getTrips(): List<Trip> {
        return trips
    }

    fun addFavouriteTrip(route: String, stop: Int) {
        if (auth.currentUser == null) return
        if (trips.any { trip -> trip.route == route && trip.stop == stop }) return
        db.collection("trips").add(
            hashMapOf(
                "route" to route,
                "stop" to stop.toLong(),
                "user_id" to auth.currentUser!!.uid
            )
        )
    }

    fun removeFavouriteTrip(route: String, stop: Int) {
        if (auth.currentUser == null) return
        if (!trips.any { trip -> trip.route == route && trip.stop == stop }) return
        db.collection("trips")
            .whereEqualTo("user_id", auth.currentUser!!.uid)
            .whereEqualTo("route", route)
            .whereEqualTo("stop", stop.toLong())
            .get()
            .addOnSuccessListener { documents ->
                documents.forEach {
                    db.collection("trips").document(it.id).delete()
                }
            }
    }
}