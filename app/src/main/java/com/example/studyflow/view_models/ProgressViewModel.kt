package com.example.studyflow.view_models



//imports
import androidx.lifecycle.ViewModel
import com.example.studyflow.repository.ProgressRepository
import com.example.studyflow.database_cloud.Progress

//import live data
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

//manages the flow of data between repository and UI for progress


class ProgressViewModel : ViewModel() {

    //creating repository variable
    private val progressRepository = ProgressRepository()

    //creating live data variable to observe changes
    private val _progress = MutableLiveData<List<Progress>>()

    //observing changes in the database
    val progress: LiveData<List<Progress>> get() = _progress


    //function to add progress to the database
    fun addProgress(progress: Progress, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        progressRepository.addProgress(progress, onSuccess, onFailure)
    }

    //function to get progress from the database
    fun getProgress() {
        progressRepository.getProgress { progress ->
            _progress.value = progress
        }
    }
}