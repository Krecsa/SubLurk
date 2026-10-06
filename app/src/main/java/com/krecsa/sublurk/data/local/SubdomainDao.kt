package com.krecsa.sublurk.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SubdomainDao {

    @Query("SELECT subdomain FROM subdomains WHERE domain = :domain ORDER BY subdomain ASC")
    suspend fun getByDomain(domain: String): List<String>

    @Query("SELECT MAX(timestamp) FROM subdomains WHERE domain = :domain")
    suspend fun getLastTimestamp(domain: String): Long?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<SubdomainEntity>)

    @Query("DELETE FROM subdomains WHERE domain = :domain")
    suspend fun deleteByDomain(domain: String)

    @Query("DELETE FROM subdomains WHERE timestamp < :threshold")
    suspend fun deleteOlderThan(threshold: Long)
}