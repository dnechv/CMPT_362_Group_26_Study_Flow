package com.example.studyflow.database_cloud


//import doe date
import java.util.Date

//data model for homework

data class Homework(
    val id: String = "",
    val homeworkName: String = "",
    val homeworkDescription: String = "",
    val courseName: String = "",
    val courseId: String = "",
    val homeworkDueDate: String = "",
    val homeworkDueTime: String = "",
    val homeworkDueDateTimeInt: Long = 0,
    val homeworkMark: String = ""
)
