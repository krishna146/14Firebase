package com.example.a14firebase.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.a14firebase.models.Note
import com.example.a14firebase.models.SignupData
import com.example.a14firebase.utils.UiState
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class NoteRepository(
    private val database: FirebaseDatabase,
    private val firestoreDb: FirebaseFirestore
) : NoteRepositoryPrototype {
    private val TAG = "KRISHNA"
    private val userLiveData = MutableLiveData<SignupData>()
    val userData: LiveData<SignupData>
        get() = userLiveData

    //realtime DB
    //inserting data
    fun saveSignUpData(signupData: SignupData, userId: String) {
        database.reference.child("users").child(userId).setValue(signupData)

    }

    //fetching data
    override fun getUserData(userId: String) {
        //getting current user reference in realtimeDB
        val userReference = database.reference.child("users").child(userId)
        //getting user data from realtime DB
        userReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                // Get User object and use the values to update the UI
                var user = dataSnapshot.getValue<SignupData>()
                Log.d(TAG, dataSnapshot.toString())
                if (user != null) {
                    userLiveData.postValue(user)
                }

            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException())
            }
        })
    }

    //Firestore Database
//inserting data
    override fun insertNote(note: Note, result: (UiState<String>) -> Unit) {
//        note.id = Firebase.auth.currentUser!!.uid
//        firestoreDb.collection("note")
//            .add(note)
//            .addOnSuccessListener {
//                //callback function
//                result(UiState.Success(it.id))
//            }
//            .addOnFailureListener {
//                //callback function
//                result(UiState.Failure(it.localizedMessage.toString()))
//            }


        val document = firestoreDb.collection("notes").document()
        note.id = document.id
        document
            .set(note)
            .addOnSuccessListener {
                //callback function
                result(UiState.Success("Note has been created successfully"))
            }
            .addOnFailureListener {
                //callback function
                result(UiState.Failure(it.localizedMessage.toString()))
            }
    }

    //fetching data
    override fun getNotes(result: (UiState<List<Note>>) -> Unit) {
        firestoreDb.collection("notes")
            .get()
            .addOnSuccessListener {
                val notes = arrayListOf<Note>()
                it.forEach { document ->
                    //converting our document to Note java object
                    val note = document.toObject(Note::class.java)
                    notes.add(note)

                }
                //call back function
                result(UiState.Success(notes))

            }
            .addOnFailureListener {
                //callback function(result)
                result(UiState.Failure(it.localizedMessage.toString()))
            }
    }

    override fun updateNote(note: Note, result: (UiState<String>) -> Unit) {
        val document = firestoreDb.collection("notes").document(note.id)
        note.id = document.id
        document
            .set(note)
            .addOnSuccessListener {
                //callback function
                result(UiState.Success("Note has been update successfully"))
            }
            .addOnFailureListener {
                //callback function
                result(UiState.Failure(it.localizedMessage.toString()))
            }
    }

    override fun deleteNote(note: Note, result: (UiState<String>) -> Unit) {
        //getting document using its id
        val document = firestoreDb.collection("notes").document(note.id)
        note.id = document.id
        document
            .delete()
            .addOnSuccessListener {
                //callback function
                result(UiState.Success("Note has been deleted successfully"))
            }
            .addOnFailureListener {
                //callback function
                result(UiState.Failure(it.localizedMessage.toString()))
            }
    }
}