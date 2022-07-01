package com.example.a14firebase.models

import android.os.Parcelable
import com.google.firebase.firestore.ServerTimestamp
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class Note(
    var id: String = "",
    var user_id: String = "",
    val title: String = "",
    val description: String = "",
    val tags: MutableList<String> = arrayListOf(),
    @ServerTimestamp
    val date: Date = Date(),
    val imgUri: List<String> = arrayListOf()
) : Parcelable