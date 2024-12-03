package com.example.studyflow.view_models

//imports
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.studyflow.repository.HomeworkRepository
import com.example.studyflow.database_cloud.Homework

//import live data
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.studyflow.database_cloud.Courses
import com.google.firebase.database.FirebaseDatabase

//manages the flow of data between repository and UI for homework

class HomeworkViewModel:ViewModel() {
    //creating repository variable
    private val homeworkRepository = HomeworkRepository()

    //creating live data variable to observe changes
    private val _homework = MutableLiveData<List<Homework>>()

    //observing changes in the database
    val homework: LiveData<List<Homework>> get() = _homework

    //function to add homework to the database
    fun addHomework(homework: Homework) {

        // First, add it to the local list
        _homework.value = _homework.value?.plus(homework) ?: listOf(homework)

        // Then, push it to Firebase
        homeworkRepository.addHomework(homework)
    }

    //function to get homework from the database
    fun getHomework() {
        homeworkRepository.getHomework { homework ->
            _homework.value = homework

        }
    }

    //function to delete homework from the database
    fun deleteHomework(homework: Homework) {
        homeworkRepository.deleteHomework(homework)
    }

    //update hw
    fun updateHW(hw: Homework) {
        val HWId = hw.id

        //get reference to firebase
//        val databaseReference = FirebaseDatabase.getInstance().getReference("homework/$HWId")
//
//        //update the hw
//        databaseReference.setValue(hw)
//            .addOnSuccessListener {
//                Log.d("HomeworkViewModel", "Course updated successfully!")
//            }
//            .addOnFailureListener { e ->
//                Log.e("HomeworkViewModel", "Failed to update course", e)
//            }

        if (HWId.isNotEmpty()) {
            val firestoreReference = homeworkRepository.getFirestoreDatabaseReference().collection("homework").document(HWId)

            firestoreReference.set(hw)
                .addOnSuccessListener {
                    Log.d("HwViewModel", "Homework updated successfully!")
                }
                .addOnFailureListener { e ->
                    Log.e("HwViewModel", "Failed to update homework", e)
                }
        } else {
            Log.e("HwViewModel", "Cannot update homework: Missing course ID")
        }
    }




}