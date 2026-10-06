package com.krecsa.sublurk.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "subdomains")
data class SubdomainEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val domain: String,
    val subdomain: String,
    val timestamp: Long,
)