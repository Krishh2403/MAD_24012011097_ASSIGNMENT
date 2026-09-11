package com.example.mad_24012011097_assignment

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "farm_logs")
data class FarmLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    @ColumnInfo(name = "owner_id")
    val ownerId: String = "",

    val cropName: String,
    val activityType: String,
    val date: String,
    val notes: String
)