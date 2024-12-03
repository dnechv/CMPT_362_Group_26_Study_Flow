package com.example.studyflow.repository

//firebase imports
import com.example.studyflow.database_cloud.Homework
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log
import com.example.studyflow.adapters.HomeworkAdapter

//handles data operations with firebase for homework

class HomeworkRepository {

    //creating database variable
    private val firebaseDataBase = FirebaseFirestore.getInstance()



    //gets firebase reference
    fun getFirestoreDatabaseReference(): FirebaseFirestore {

        return firebaseDataBase
    }




    //adds homework to the database
    fun addHomework(homework: Homework) : String {
        val docRef = firebaseDataBase.collection("homework").document()
        val hwID = homework.copy(id = docRef.id)
        docRef.set(hwID)

            .addOnSuccessListener {
                Log.d("HomeworkRepository", "Homework added successfully")

            }
            .addOnFailureListener { exception ->
                Log.d("HomeworkRepository", "Failed to add homework", exception)

            }
        return hwID.id
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
    fun deleteHomework(homework: Homework) {
        firebaseDataBase.collection("homework").document(homework.id)
            .delete()
            .addOnSuccessListener {
                Log.d("HomeworkRepository", "Homework deleted successfully")
        }
    }


    //get homework by course
    fun getSortedSpecificHomworks (chosenCourse : String,onSuccess: (List<Homework>) -> Unit) {


        firebaseDataBase.collection("homework")
            .orderBy("homeworkDueDateTimeInt")
            .whereEqualTo("courseId", chosenCourse )
            .get()


            .addOnSuccessListener { result ->
                val homework = result.toObjects(Homework::class.java)
                onSuccess(homework)


            }
            .addOnFailureListener { exception ->
                Log.d("HCTE", "Failed to get homework", exception)
            }

    }
}