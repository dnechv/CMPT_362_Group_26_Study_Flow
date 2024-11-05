package com.example.studyflow.repository

//firebase imports
import com.example.studyflow.database_cloud.Progress
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

//handles data operations with firebase for progress

class ProgressRepository {
    //creating database variable
    private val firebaseDataBase = FirebaseFirestore.getInstance()

    //function to add progress to the database
    fun addProgress(progress: Progress, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firebaseDataBase.collection("progress")
            .document() //generates unique id - avoid overwriting data
            .set(progress)
            .addOnSuccessListener {
                Log.d("ProgressRepository", "Progress added successfully")
                onSuccess()
            }
            .addOnFailureListener { exception ->
                Log.d("ProgressRepository", "Failed to add progress", exception)
                onFailure(exception)
            }
    }

    //function to get progress from the database
    fun getProgress(onSuccess: (List<Progress>) -> Unit) {
        firebaseDataBase.collection("progress")
            .get()
            .addOnSuccessListener { result ->
                val progress = result.toObjects(Progress::class.java)
                onSuccess(progress)
            }
            .addOnFailureListener { exception ->
                Log.d("ProgressRepository", "Failed to get progress", exception)
            }
    }

    //delete progress
    fun deleteProgress(progress: Progress, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firebaseDataBase.collection("progress").document(progress.courseId)
            .delete()
            .addOnSuccessListener {
                Log.d("ProgressRepository", "Progress deleted successfully")
                onSuccess()
            }


    }
}