package com.example.a14firebase.application

import android.app.Application
import com.example.a14firebase.repository.NoteRepository
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class NoteApplication: Application() {
    lateinit var noteRepository : NoteRepository
    private lateinit var realtimeDb: FirebaseDatabase
    private lateinit var firestoreDb: FirebaseFirestore
    override fun onCreate() {
        super.onCreate()
        initialize();
    }

    private fun initialize() {
        firestoreDb = Firebase.firestore
        realtimeDb = FirebaseDatabase.getInstance()
        noteRepository = NoteRepository(realtimeDb, firestoreDb)
    }

}