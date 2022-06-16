package com.example.a14firebase.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class FirebaseModule {
    @Provides
    @Singleton
    fun providesFireStoreInstance(): FirebaseFirestore {
        return  FirebaseFirestore.getInstance()
    }
    @Provides
    @Singleton
    fun providesFirebaseRealtimeDBInstance(): FirebaseDatabase{
        return  FirebaseDatabase.getInstance()
    }
    @Provides
    @Singleton
    fun providesFirebaseAuthInstance(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }
}