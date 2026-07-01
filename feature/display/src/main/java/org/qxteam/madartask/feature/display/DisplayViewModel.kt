package org.qxteam.madartask.feature.display

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.qxteam.madartask.core.model.User
import org.qxteam.madartask.core.model.usecase.DeleteUserUseCase
import org.qxteam.madartask.core.model.usecase.GetAllUsersUseCase

data class DisplayUiState(
    val users: List<User> = emptyList(),
    val isLoading: Boolean = true
)

class DisplayViewModel(
    getAllUsersUseCase: GetAllUsersUseCase,
    private val deleteUserUseCase: DeleteUserUseCase
) : ViewModel() {

    val uiState: StateFlow<DisplayUiState> = getAllUsersUseCase()
        .map { usersList ->
            DisplayUiState(users = usersList, isLoading = false)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = DisplayUiState(isLoading = true)
        )

    fun deleteUser(user: User) {
        viewModelScope.launch {
            deleteUserUseCase(user)
        }
    }
}
