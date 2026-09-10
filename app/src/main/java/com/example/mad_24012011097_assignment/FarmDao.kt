package com.example.mad_24012011097_assignment

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FarmDao {

    @Query("""
        SELECT * FROM farm_logs
        WHERE owner_id = :ownerId
        ORDER BY id DESC
    """)
    fun getLogsForOwner(ownerId: String): LiveData<List<FarmLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: FarmLog)

    @Delete
    suspend fun deleteLog(log: FarmLog)
}