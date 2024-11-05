package com.example.studyflow.database_cloud


//import for date
import java.util.Date

//contains data model for courses

data class Courses(
    val courseName: String = "",
    val courseCode: String = "",
    val courseTerm: String = "",
    val courseDescription: String = "",
    val courseInstructor: String = "",
    val courseInstructorEmail: String = "",
    val courseStartDate: Date? = null,
    val courseEndDate: Date? = null,
    val courseDays: String = "",
    val courseStartTime: String = "",
    val courseEndTime: String = "",
    val courseLocation: String = "",
)
