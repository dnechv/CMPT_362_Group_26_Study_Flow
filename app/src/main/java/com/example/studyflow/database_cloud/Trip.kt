package com.example.studyflow.database_cloud

import com.google.firebase.firestore.DocumentId

data class Trip(
    @DocumentId val id: String = "",
    val route: String = "",
    val stop: Int = -1,
)
