package com.example.studyflow.database_cloud

//date import
import java.util.Date

//data model for courses
data class Courses(
    val courseCode: String = "",
    val courseDays: String = "",
    val courseDescription: String = "",
    val courseEndDate: Date? = null,
    val courseEndTime: String = "",
    val courseInstructor: String = "",
    val courseInstructorEmail: String = "",
    val courseLocation: String = "",
    val courseName: String = "",
    val courseStartDate: Date? = null,
    val courseStartTime: String = "",
    val courseTerm: String = ""
)