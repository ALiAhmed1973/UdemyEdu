package com.aliahmed1973.udemyedu.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeys(
    @PrimaryKey(autoGenerate = false)
    val trackingId: String,
    val prevKey: Int?,
    val nextKey: Int?)
