package com.example.a14firebase.di

import com.example.a14firebase.repository.AuthRepository
import com.example.a14firebase.repository.AuthRepositoryImpl
import com.example.a14firebase.repository.NoteRepository
import com.example.a14firebase.repository.NoteRepositoryPrototype
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class RepositoryModule {
    @Provides
    @Singleton
    fun provideNoteRepository(database: FirebaseFirestore): NoteRepositoryPrototype{
        return  NoteRepository(database)
    }
    @Provides
    @Singleton
    fun provideAuthRepository(database: FirebaseFirestore, auth: FirebaseAuth): AuthRepository {
        return AuthRepositoryImpl(auth, database)
    }

}