package com.example.studyflow.database_cloud

//data class for tracking student progress in app

data class Progress(

    val courseId: String = "",
    val completedAssignments: Int = 0,
    val grade: Double = 0.0,
    val progressPercentage: Double = 0.0

)

