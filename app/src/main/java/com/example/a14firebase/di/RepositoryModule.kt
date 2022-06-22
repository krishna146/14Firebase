package com.example.a14firebase.di

import android.content.SharedPreferences
import com.example.a14firebase.repository.AuthRepository
import com.example.a14firebase.repository.AuthRepositoryImpl
import com.example.a14firebase.repository.NoteRepository
import com.example.a14firebase.repository.NoteRepositoryPrototype
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object RepositoryModule {
    @Provides
    @Singleton
    fun provideNoteRepository(database: FirebaseFirestore): NoteRepositoryPrototype {
        return NoteRepository(database)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(
        database: FirebaseFirestore,
        auth: FirebaseAuth,
        appPreferences: SharedPreferences,
        gson: Gson
    ): AuthRepository {
        return AuthRepositoryImpl(auth, database, appPreferences, gson)
    }

}