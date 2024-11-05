package com.example.studyflow.repository

//firebase imports
import com.example.studyflow.database_cloud.Courses
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log


//handles data operations with firebase for courses


class CoursesRepository {

    //creating database variable
    private val firebaseDataBase = FirebaseFirestore.getInstance()

    //function to add course to the database
    fun addCourse(course: Courses) {
        Log.d("CoursesRepository", "Attempting to add course: $course")
        firebaseDataBase.collection("courses").document() // unique id
            .set(course)
            .addOnSuccessListener {
                Log.d("CoursesRepository", "Course added successfully: $course")
            }
            .addOnFailureListener { exception ->
                Log.d("CoursesRepository", "Failed to add course", exception)
            }
    }


    //function to get courses from the database
    fun getCourses(onSuccess: (List<Courses>) -> Unit) {
        firebaseDataBase.collection("courses")
            .get()
            .addOnSuccessListener { result ->
                val courses = result.toObjects(Courses::class.java)
                onSuccess(courses)
            }
            .addOnFailureListener { exception ->
                Log.d("CoursesRepository", "Failed to get courses", exception)
            }
    }

    //delete course
    fun deleteCourse(course: Courses) {
        firebaseDataBase.collection("courses").document(course.courseName)
            .delete()
            .addOnSuccessListener {
                Log.d("CoursesRepository", "Course deleted successfully")
            }


    }
}
