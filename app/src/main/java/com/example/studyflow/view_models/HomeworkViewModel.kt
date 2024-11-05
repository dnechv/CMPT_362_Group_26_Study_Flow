package com.example.studyflow.view_models

//imports
import androidx.lifecycle.ViewModel
import com.example.studyflow.repository.HomeworkRepository
import com.example.studyflow.database_cloud.Homework

//import live data
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

//manages the flow of data between repository and UI for homework

class HomeworkViewModel:ViewModel() {
    //creating repository variable
    private val homeworkRepository = HomeworkRepository()

    //creating live data variable to observe changes
    private val _homework = MutableLiveData<List<Homework>>()

    //observing changes in the database
    val homework: LiveData<List<Homework>> get() = _homework

    //function to add homework to the database
    fun addHomework(homework: Homework, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        homeworkRepository.addHomework(homework, onSuccess, onFailure)
    }

    //function to get homework from the database
    fun getHomework() {
        homeworkRepository.getHomework { homework ->
            _homework.value = homework
        }
    }

    //function to delete homework from the database
    fun deleteHomework(homework: Homework, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        homeworkRepository.deleteHomework(homework, onSuccess, onFailure)
    }




}