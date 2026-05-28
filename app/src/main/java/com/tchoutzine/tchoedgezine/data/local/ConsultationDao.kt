package com.tchoutzine.tchoedgezine.data.local

import androidx.room.*
import com.tchoutzine.tchoedgezine.data.model.Consultation
import com.tchoutzine.tchoedgezine.data.model.ConsultationType
import kotlinx.coroutines.flow.Flow

@Dao
interface ConsultationDao {
    @Query("SELECT * FROM consultations ORDER BY timestampMs DESC")
    fun getAll(): Flow<List<Consultation>>

    @Query("SELECT * FROM consultations WHERE type = :type ORDER BY timestampMs DESC")
    fun getByType(type: ConsultationType): Flow<List<Consultation>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(consultation: Consultation): Long

    @Query("DELETE FROM consultations")
    suspend fun deleteAll()

    @Query("SELECT * FROM consultations WHERE synced = 0")
    suspend fun getUnsynced(): List<Consultation>

    @Query("UPDATE consultations SET synced = 1 WHERE id = :id")
    suspend fun markSynced(id: Int)
}
