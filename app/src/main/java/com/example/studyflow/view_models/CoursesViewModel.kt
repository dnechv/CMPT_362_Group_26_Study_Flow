package com.example.studyflow.view_models

// Imports
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.studyflow.repository.CoursesRepository
import com.example.studyflow.database_cloud.Courses
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

// Manages the flow of data between repository and UI for courses
class CoursesViewModel : ViewModel() {

    private val coursesRepository = CoursesRepository()

    // LiveData variable to observe changes
    private val _courses = MutableLiveData<List<Courses>>()
    val courses: LiveData<List<Courses>> get() = _courses

    // Function to get courses from the database
    fun getCourses() {
        coursesRepository.getCourses { courses ->
            _courses.value = courses
        }
    }

    // Function to add course to the database
    fun addCourse(course: Courses) {
        coursesRepository.addCourse(course) // No callbacks needed here
    }

    // Function to delete course from the database
    fun deleteCourse(course: Courses) {
        Log.d("CoursesViewModel", "Attempting to add course: $course")
        coursesRepository.deleteCourse(course) // No callbacks needed here
    }
}