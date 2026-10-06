package com.krecsa.sublurk.di

import android.content.Context
import androidx.room.Room
import com.krecsa.sublurk.data.local.AppDb
import com.krecsa.sublurk.data.local.SubdomainDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDb(@ApplicationContext context: Context): AppDb =
        Room.databaseBuilder(context, AppDb::class.java, "sublurk.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideSubdomainDao(db: AppDb): SubdomainDao = db.subdomainDao()
}