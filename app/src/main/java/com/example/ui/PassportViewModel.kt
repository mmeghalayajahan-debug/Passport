package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.PassportDatabase
import com.example.data.PassportRecord
import com.example.data.PassportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PassportViewModel(
    application: Application,
    private val repository: PassportRepository
) : AndroidViewModel(application) {

    // List of all registered passports for reference / quick selection
    val allPassports: StateFlow<List<PassportRecord>> = repository.allPassports
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Current search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResult = MutableStateFlow<PassportRecord?>(null)
    val searchResult: StateFlow<PassportRecord?> = _searchResult.asStateFlow()

    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    // Registration Form State
    val regPassportNumber = MutableStateFlow("")
    val regFullName = MutableStateFlow("")
    val regGender = MutableStateFlow("M") // default
    val regDOB = MutableStateFlow("1995-01-01")
    val regNationality = MutableStateFlow("United States")
    val regIssueDate = MutableStateFlow("2025-01-01")
    val regExpiryDate = MutableStateFlow("2035-01-01")
    val regAddress = MutableStateFlow("")
    val regPhotoName = MutableStateFlow("img_passport_john_doe") // default to john doe avatar
    val regIsFlagged = MutableStateFlow(false)
    val regFlagReason = MutableStateFlow("")

    private val _registrationSuccess = MutableStateFlow<String?>(null)
    val registrationSuccess: StateFlow<String?> = _registrationSuccess.asStateFlow()

    private val _registrationError = MutableStateFlow<String?>(null)
    val registrationError: StateFlow<String?> = _registrationError.asStateFlow()

    init {
        viewModelScope.launch {
            repository.seedDatabaseIfNeeded()
        }
    }

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
        // If query is cleared, reset search results
        if (query.trim().isEmpty()) {
            _searchResult.value = null
            _hasSearched.value = false
        }
    }

    fun searchPassport() {
        val query = _searchQuery.value.trim().uppercase()
        if (query.isEmpty()) return

        _isSearching.value = true
        _hasSearched.value = true

        viewModelScope.launch {
            val result = repository.getPassportByNumber(query)
            _searchResult.value = result
            _isSearching.value = false
        }
    }

    fun selectPassportForSearch(passportNumber: String) {
        _searchQuery.value = passportNumber
        searchPassport()
    }

    fun resetSearch() {
        _searchQuery.value = ""
        _searchResult.value = null
        _hasSearched.value = false
    }

    fun registerPassport() {
        val number = regPassportNumber.value.trim().uppercase()
        val name = regFullName.value.trim()
        val address = regAddress.value.trim()

        if (number.isEmpty() || name.isEmpty() || address.isEmpty()) {
            _registrationError.value = "All fields (Passport Number, Name, and Address) are required."
            return
        }

        viewModelScope.launch {
            // Check if passport number already exists
            val existing = repository.getPassportByNumber(number)
            if (existing != null) {
                _registrationError.value = "Passport number $number is already registered."
                return@launch
            }

            val newRecord = PassportRecord(
                passportNumber = number,
                fullName = name,
                gender = regGender.value,
                dateOfBirth = regDOB.value,
                nationality = regNationality.value,
                issueDate = regIssueDate.value,
                expiryDate = regExpiryDate.value,
                address = address,
                photoDrawableName = regPhotoName.value,
                isFlagged = regIsFlagged.value,
                flagReason = if (regIsFlagged.value) regFlagReason.value.ifEmpty { "Administrative block" } else ""
            )

            repository.insertPassport(newRecord)
            _registrationSuccess.value = "Passport $number successfully registered!"
            _registrationError.value = null

            // Reset form fields
            regPassportNumber.value = ""
            regFullName.value = ""
            regAddress.value = ""
            regIsFlagged.value = false
            regFlagReason.value = ""

            // Clear search and show the newly created passport
            _searchQuery.value = number
            _searchResult.value = newRecord
            _hasSearched.value = true
        }
    }

    fun toggleFlagStatus(number: String, isFlagged: Boolean, reason: String = "") {
        viewModelScope.launch {
            repository.updateFlagStatus(number, isFlagged, reason)
            // Refresh search result if we are viewing the modified passport
            if (_searchResult.value?.passportNumber?.uppercase() == number.uppercase()) {
                val updated = repository.getPassportByNumber(number)
                _searchResult.value = updated
            }
        }
    }

    fun deletePassport(number: String) {
        viewModelScope.launch {
            repository.deletePassport(number)
            if (_searchResult.value?.passportNumber?.uppercase() == number.uppercase()) {
                _searchResult.value = null
                _hasSearched.value = false
                _searchQuery.value = ""
            }
        }
    }

    fun clearNotifications() {
        _registrationSuccess.value = null
        _registrationError.value = null
    }
}

class PassportViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PassportViewModel::class.java)) {
            val database = PassportDatabase.getDatabase(application)
            val repository = PassportRepository(database.passportDao())
            return PassportViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
