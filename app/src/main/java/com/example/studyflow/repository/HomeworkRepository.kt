package com.example.studyflow.repository

//firebase imports
import com.example.studyflow.database_cloud.Homework
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log

//handles data operations with firebase for homework

class HomeworkRepository {

        //creating database variable
        private val firebaseDataBase = FirebaseFirestore.getInstance()

    //function to add homework to the database
    fun addHomework(homework: Homework) {
        firebaseDataBase.collection("homework").document() //generates unique id - avoid overwriting data
            .set(homework)
            .addOnSuccessListener {
                Log.d("HomeworkRepository", "Homework added successfully")
            }
            .addOnFailureListener { exception ->
                Log.d("HomeworkRepository", "Failed to add homework", exception)

            }
    }

    //function to get homework from the database
    fun getHomework(onSuccess: (List<Homework>) -> Unit) {
        firebaseDataBase.collection("homework")
            .get()
            .addOnSuccessListener { result ->
                val homework = result.toObjects(Homework::class.java)
                onSuccess(homework)
            }
            .addOnFailureListener { exception ->
                Log.d("HomeworkRepository", "Failed to get homework", exception)
            }
    }

    //delete homework
    fun deleteHomework(homework: Homework, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        firebaseDataBase.collection("homework").document(homework.homeworkName)
            .delete()
            .addOnSuccessListener {
                Log.d("HomeworkRepository", "Homework deleted successfully")
                onSuccess()
        }
    }


}