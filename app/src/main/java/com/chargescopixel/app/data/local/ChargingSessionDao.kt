package com.chargescopixel.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ChargingSessionDao {
    @Insert
    suspend fun insert(session: ChargingSessionEntity): Long

    @Update
    suspend fun update(session: ChargingSessionEntity)

    @Query("SELECT * FROM charging_sessions WHERE endTimeMs IS NULL ORDER BY startTimeMs DESC LIMIT 1")
    suspend fun getOpenSession(): ChargingSessionEntity?

    @Query("SELECT * FROM charging_sessions ORDER BY startTimeMs DESC")
    fun observeAllSessions(): Flow<List<ChargingSessionEntity>>

    @Query("SELECT * FROM charging_sessions ORDER BY startTimeMs DESC LIMIT :limit")
    fun observeRecentSessions(limit: Int): Flow<List<ChargingSessionEntity>>

    @Query("SELECT * FROM charging_sessions ORDER BY startTimeMs DESC")
    suspend fun getAllSessionsOnce(): List<ChargingSessionEntity>
}
