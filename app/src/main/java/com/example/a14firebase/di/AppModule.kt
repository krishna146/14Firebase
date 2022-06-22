package com.example.a14firebase.di

import android.content.Context
import android.content.SharedPreferences
import com.example.a14firebase.utils.SharedPrefConstants
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@InstallIn(SingletonComponent::class)
@Module
object AppModule {
    @Provides
    @Singleton
    fun providesSharedPref(@ApplicationContext context: Context) : SharedPreferences{
        return context.getSharedPreferences(SharedPrefConstants.LOCAL_SHARED_PREF, Context.MODE_PRIVATE)
    }
    @Provides
    @Singleton
    fun providesGson(): Gson{
        return Gson()
    }
}