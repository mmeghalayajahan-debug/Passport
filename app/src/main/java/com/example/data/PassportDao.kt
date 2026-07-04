package com.example.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PassportDao {
    @Query("SELECT * FROM passports")
    fun getAllPassports(): Flow<List<PassportRecord>>

    @Query("SELECT * FROM passports WHERE passportNumber = :number LIMIT 1")
    suspend fun getPassportByNumber(number: String): PassportRecord?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPassport(record: PassportRecord)

    @Query("UPDATE passports SET isFlagged = :flagged, flagReason = :reason WHERE passportNumber = :number")
    suspend fun updateFlagStatus(number: String, flagged: Boolean, reason: String)

    @Query("DELETE FROM passports WHERE passportNumber = :number")
    suspend fun deletePassport(number: String)
}
