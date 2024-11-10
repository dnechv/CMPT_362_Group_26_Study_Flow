package com.example.studyflow.view_models

// Imports
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.studyflow.repository.CoursesRepository
import com.example.studyflow.database_cloud.Courses
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


/*
Google Firebase Integration: Authentication via firebase, Cloud Storage, Cloud Storage Sync with Local Storage
Translink API for bus updates,
new tab layout design at the bottom of the screen,
Animation between switching fragments, Google AR Co
re.

 */

//manages the data for the courses
class CoursesViewModel : ViewModel() {

    //repository variable
    private val coursesRepository = CoursesRepository()

    //livedata -> observe changes in the data
    private val _courses = MutableLiveData<List<Courses>>()
    val courses: LiveData<List<Courses>> get() = _courses

    //get courses from the database
    fun getCourses() {
        coursesRepository.getCourses { courses ->
            _courses.value = courses
        }
    }



    //add course to the database + update the UI
    fun addCourse(course: Courses) {
        coursesRepository.addCourse(course)
        _courses.value = _courses.value?.plus(course)
    }

    //delete the course from database
    fun deleteCourse(course: Courses) {
        Log.d("CoursesViewModel", "Attempting to add course: $course")
        coursesRepository.deleteCourse(course)
    }
}