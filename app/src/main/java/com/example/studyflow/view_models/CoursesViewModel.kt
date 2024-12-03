package com.example.studyflow.view_models

// Imports
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.studyflow.repository.CoursesRepository
import com.example.studyflow.database_cloud.Courses
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.studyflow.database_cloud.Homework
import com.example.studyflow.repository.HomeworkRepository
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.launch


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
    private val HwRep = HomeworkRepository()

    //livedata -> observe changes in the data
    private val _courses = MutableLiveData<List<Courses>>()
    val courses: LiveData<List<Courses>> get() = _courses

    //get courses from the database
    fun getCourses() {
        coursesRepository.getCourses { courses ->
            _courses.value = courses
        }
    }

//update course
fun updateCourse(course: Courses) {
    val courseId = course.id

    if (courseId.isNotEmpty()) {
        val firestoreReference = coursesRepository.getFirestoreDatabaseReference().collection("courses").document(courseId)

        firestoreReference.set(course)
            .addOnSuccessListener {
                Log.d("CoursesViewModel", "Course updated successfully!")
            }
            .addOnFailureListener { e ->
                Log.e("CoursesViewModel", "Failed to update course", e)
            }
    } else {
        Log.e("CoursesViewModel", "Cannot update course: Missing course ID")
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
        val courseID = course.id
        var Hws = listOf<Homework>()
        HwRep.getHomework { homework: List<Homework> ->
            Hws = homework
            Log.d("DHW", Hws.toString())
            for (homework in Hws) {
                Log.d("DHW1", homework.toString())
                if (homework.courseId == courseID) {
                    HwRep.deleteHomework(homework)
                    Log.d("DHW2", homework.toString())
                }
            }
        }

        coursesRepository.deleteCourse(course)
    }
}