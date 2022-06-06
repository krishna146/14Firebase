package com.example.a14firebase.models

import com.google.firebase.firestore.ServerTimestamp
import java.util.*

data class Note(
    var id: String = "",
    val text: String = "",
    @ServerTimestamp
    val date: Date = Date()
)
