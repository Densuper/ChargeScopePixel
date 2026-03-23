package com.chargescopixel.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BatterySampleDao {
    @Insert
    suspend fun insert(sample: BatterySampleEntity)

    @Query("SELECT * FROM battery_samples WHERE sessionId = :sessionId ORDER BY timestampMs ASC")
    fun observeSamplesForSession(sessionId: Long): Flow<List<BatterySampleEntity>>

    @Query("SELECT * FROM battery_samples WHERE sessionId = :sessionId ORDER BY timestampMs ASC")
    suspend fun getSamplesForSessionOnce(sessionId: Long): List<BatterySampleEntity>

    @Query("SELECT * FROM battery_samples ORDER BY timestampMs DESC LIMIT :limit")
    fun observeLatestSamples(limit: Int): Flow<List<BatterySampleEntity>>

    @Query("SELECT * FROM battery_samples ORDER BY timestampMs DESC LIMIT :limit")
    suspend fun getLatestSamplesOnce(limit: Int): List<BatterySampleEntity>

    @Query("SELECT * FROM battery_samples ORDER BY timestampMs ASC")
    suspend fun getAllSamplesOnce(): List<BatterySampleEntity>
}
