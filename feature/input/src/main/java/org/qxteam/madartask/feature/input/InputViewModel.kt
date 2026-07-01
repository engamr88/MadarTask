package org.qxteam.madartask.feature.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.qxteam.madartask.core.model.User
import org.qxteam.madartask.core.model.usecase.SaveUserUseCase

data class InputUiState(
    val name: String = "",
    val age: String = "",
    val jobTitle: String = "",
    val gender: String = "Male",
    val nameError: String? = null,
    val ageError: String? = null,
    val jobTitleError: String? = null,
    val isSaving: Boolean = false
)

sealed interface InputUiEvent {
    object SaveSuccess : InputUiEvent
    data class ShowError(val message: String) : InputUiEvent
}

class InputViewModel(private val saveUserUseCase: SaveUserUseCase) : ViewModel() {

    private val _uiState = MutableStateFlow(InputUiState())
    val uiState: StateFlow<InputUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<InputUiEvent>()
    val eventFlow: SharedFlow<InputUiEvent> = _eventFlow.asSharedFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    fun onAgeChange(age: String) {
        val filteredAge = age.filter { it.isDigit() }
        _uiState.update { it.copy(age = filteredAge, ageError = null) }
    }

    fun onJobTitleChange(jobTitle: String) {
        _uiState.update { it.copy(jobTitle = jobTitle, jobTitleError = null) }
    }

    fun onGenderChange(gender: String) {
        _uiState.update { it.copy(gender = gender) }
    }

    fun saveUser() {
        val currentState = _uiState.value
        val name = currentState.name.trim()
        val ageStr = currentState.age.trim()
        val jobTitle = currentState.jobTitle.trim()
        val gender = currentState.gender

        var hasError = false
        var nameErr: String? = null
        var ageErr: String? = null
        var jobTitleErr: String? = null

        if (name.isEmpty()) {
            nameErr = "Name cannot be empty"
            hasError = true
        }

        val age = ageStr.toIntOrNull()
        if (age == null || age <= 0 || age > 120) {
            ageErr = "Please enter a valid age (1-120)"
            hasError = true
        }

        if (jobTitle.isEmpty()) {
            jobTitleErr = "Job title cannot be empty"
            hasError = true
        }

        if (hasError) {
            _uiState.update {
                it.copy(
                    nameError = nameErr,
                    ageError = ageErr,
                    jobTitleError = jobTitleErr
                )
            }
            return
        }

        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            try {
                val user = User(
                    name = name,
                    age = age!!,
                    jobTitle = jobTitle,
                    gender = gender
                )
                saveUserUseCase(user)
                _eventFlow.emit(InputUiEvent.SaveSuccess)
                // Reset form on success
                _uiState.value = InputUiState()
            } catch (e: Exception) {
                _eventFlow.emit(InputUiEvent.ShowError(e.message ?: "Failed to save user"))
                _uiState.update { it.copy(isSaving = false) }
            }
        }
    }
}
