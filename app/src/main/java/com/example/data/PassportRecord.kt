package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "passports")
data class PassportRecord(
    @PrimaryKey val passportNumber: String,
    val fullName: String,
    val gender: String,
    val dateOfBirth: String,
    val nationality: String,
    val issueDate: String,
    val expiryDate: String,
    val address: String,
    val photoDrawableName: String, // e.g. "img_passport_john_doe"
    val isFlagged: Boolean,
    val flagReason: String = ""
)
