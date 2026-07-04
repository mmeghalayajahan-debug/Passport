package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class PassportRepository(private val passportDao: PassportDao) {

    val allPassports: Flow<List<PassportRecord>> = passportDao.getAllPassports()

    suspend fun getPassportByNumber(number: String): PassportRecord? {
        return passportDao.getPassportByNumber(number.uppercase().trim())
    }

    suspend fun insertPassport(record: PassportRecord) {
        passportDao.insertPassport(record.copy(passportNumber = record.passportNumber.uppercase().trim()))
    }

    suspend fun updateFlagStatus(number: String, flagged: Boolean, reason: String) {
        passportDao.updateFlagStatus(number.uppercase().trim(), flagged, reason)
    }

    suspend fun deletePassport(number: String) {
        passportDao.deletePassport(number.uppercase().trim())
    }

    suspend fun seedDatabaseIfNeeded() {
        val currentList = passportDao.getAllPassports().first()
        if (currentList.isEmpty()) {
            val seedData = listOf(
                PassportRecord(
                    passportNumber = "US1234567",
                    fullName = "Johnathan Doe",
                    gender = "M",
                    dateOfBirth = "1985-05-12",
                    nationality = "United States",
                    issueDate = "2020-04-15",
                    expiryDate = "2030-04-14",
                    address = "1600 Amphitheatre Pkwy, Mountain View, CA 94043, USA",
                    photoDrawableName = "img_passport_john_doe",
                    isFlagged = false,
                    flagReason = ""
                ),
                PassportRecord(
                    passportNumber = "UK9876543",
                    fullName = "Jane Smith",
                    gender = "F",
                    dateOfBirth = "1992-09-24",
                    nationality = "United Kingdom",
                    issueDate = "2018-11-02",
                    expiryDate = "2028-11-01",
                    address = "10 Downing St, London SW1A 2AA, United Kingdom",
                    photoDrawableName = "img_passport_jane_smith",
                    isFlagged = false,
                    flagReason = ""
                ),
                PassportRecord(
                    passportNumber = "BL8888888",
                    fullName = "Marcus Kane",
                    gender = "M",
                    dateOfBirth = "1979-12-01",
                    nationality = "Canada",
                    issueDate = "2015-06-20",
                    expiryDate = "2025-06-19",
                    address = "455 Wellington St, Ottawa, ON K1A 0A9, Canada",
                    photoDrawableName = "img_passport_marcus_kane",
                    isFlagged = true,
                    flagReason = "REPORTED STOLEN. Flagged by INTERPOL Command Centre. Secure credential immediately and notify border authorities."
                )
            )
            for (record in seedData) {
                passportDao.insertPassport(record)
            }
        }
    }
}
