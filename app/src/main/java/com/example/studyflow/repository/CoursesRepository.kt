package com.example.studyflow.repository

//firebase imports
import com.example.studyflow.database_cloud.Courses
import com.google.firebase.firestore.FirebaseFirestore
import android.util.Log


//handles data operations with firebase for courses


class CoursesRepository {

    //creating database variable
    private val firebaseDataBase = FirebaseFirestore.getInstance()


    //gets firebase reference
    fun getFirestoreDatabaseReference(): FirebaseFirestore {

        return firebaseDataBase
    }

    //function to add course to the database
    fun addCourse(course: Courses) {

        //get a reference to the course with unique id
        val documentRef = firebaseDataBase.collection("courses").document()

        val courseWithId = course.copy(id = documentRef.id)

        documentRef.set(courseWithId)


            .addOnSuccessListener {


                Log.d("CoursesRepository", "Course added successfully: $courseWithId")



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
                val courses = result.map { document ->
                    val courseDays = when (val days = document["courseDays"]) {
                        is String -> days.split(", ").map { it.trim() }
                        is List<*> -> days.filterIsInstance<String>()
                        else -> emptyList()
                    }

                    //build the courses
                    Courses(

                        id = document.id,
                        courseCode = document.getString("courseCode") ?: "",
                        courseDays = courseDays,
                        courseDescription = document.getString("courseDescription") ?: "",
                        courseEndDate = document.getString("courseEndDate"),
                        courseEndTime = document.getString("courseEndTime") ?: "",
                        courseInstructor = document.getString("courseInstructor") ?: "",
                        courseInstructorEmail = document.getString("courseInstructorEmail") ?: "",
                        courseLocation = document.getString("courseLocation") ?: "",
                        courseName = document.getString("courseName") ?: "",
                        courseStartDate = document.getString("courseStartDate"),
                        courseStartTime = document.getString("courseStartTime") ?: "",
                        courseTerm = document.getString("courseTerm") ?: ""
                    )
                }
                onSuccess(courses)
            }
            .addOnFailureListener { exception ->
                Log.e("CoursesRepository", "Failed to get courses", exception)
            }
    }


    //update course
    fun updateCourse(course: Courses) {
        if (course.id.isNotEmpty()) {


            //get the course by id
            firebaseDataBase.collection("courses").document(course.id)

                //update the course
                .set(course)
                .addOnSuccessListener {


                    Log.d("CoursesRepository", "Course updated successfully: ${course.id}")
                }
                .addOnFailureListener { exception ->


                    Log.e("CoursesRepository", "failed to update", exception)
                }
        } else {


            Log.e("CoursesRepository", "no id. cant update.")
        }
    }


    //delete course
    fun deleteCourse(course: Courses) {

        //check if the course has an id
        if (course.id.isNotEmpty()) {

            //delete the course -> get the course by id
            firebaseDataBase.collection("courses").document(course.id)

                //delete the course
                .delete()

                //on success
                .addOnSuccessListener {
                    Log.d("CoursesRepository", "Course deleted successfully: ${course.id}")
                }

                //if fails
                .addOnFailureListener { exception ->
                    Log.e("CoursesRepository", "Failed to delete course", exception)
                }


        } else {


            Log.e("CoursesRepository", "Course ID is empty. Cannot delete.")
        }
    }
}
