package com.krecsa.sublurk.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [SubdomainEntity::class],
    version = 1,
    exportSchema = false,
)
abstract class AppDb : RoomDatabase() {
    abstract fun subdomainDao(): SubdomainDao
}