package com.example.a14firebase.di

import com.example.a14firebase.utils.FirebaseStorageConstants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object FirebaseModule {
    @Provides
    @Singleton
    fun providesFireStoreInstance(): FirebaseFirestore {
        return  FirebaseFirestore.getInstance()
    }
    @Provides
    @Singleton
    fun providesFirebaseStorageInstance(): StorageReference{
        return  FirebaseStorage.getInstance().getReference(FirebaseStorageConstants.ROOT_DIRECTORY)
    }
    @Provides
    @Singleton
    fun providesFirebaseAuthInstance(): FirebaseAuth{
        return FirebaseAuth.getInstance()
    }

}