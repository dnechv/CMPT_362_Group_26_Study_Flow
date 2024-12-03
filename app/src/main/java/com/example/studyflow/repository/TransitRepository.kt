package com.example.studyflow.repository

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.ArrayList

class TransitRepository {
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    fun getFavouriteStops(onSuccess: (List<Int>) -> Unit) {
        if (auth.currentUser == null) return
        Log.d("TransitRepository", "Getting favourite stops...")
        db.collection("stops").document(auth.currentUser!!.uid).get().addOnSuccessListener {
            onSuccess((it.get("favourite") as ArrayList<Long>?)?.map { stop -> stop.toInt() } ?: emptyList())
        }
    }

    fun addFavouriteStop(stopId: Int) {
        if (auth.currentUser == null) return
        db.collection("stops").document(auth.currentUser!!.uid).set(hashMapOf("active" to true))
        val ref = db.collection("stops").document(auth.currentUser!!.uid)
        ref.update("favourite", FieldValue.arrayUnion(stopId))
    }

    fun removeFavouriteStop(stopId: Int) {
        if (auth.currentUser == null) return
        db.collection("stops").document(auth.currentUser!!.uid).set(hashMapOf("active" to true))
        val ref = db.collection("stops").document(auth.currentUser!!.uid)
        ref.update("favourite", FieldValue.arrayRemove(stopId))
    }
}