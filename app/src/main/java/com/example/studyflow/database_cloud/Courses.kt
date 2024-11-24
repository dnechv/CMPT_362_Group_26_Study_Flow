package com.example.studyflow.database_cloud

//date import

//data model for courses
data class Courses(
    val id: String = "",

    val courseCode: String = "",

    val courseDays: List<String> = emptyList(),

    val courseDescription: String = "",

    val courseEndDate: String? = null,

    val courseEndTime: String = "",

    val courseInstructor: String = "",

    val courseInstructorEmail: String = "",

    val courseLocation: String = "",

    val courseName: String = "",

    val courseStartDate: String? = null,

    val courseStartTime: String = "",

    val courseTerm: String = ""
)