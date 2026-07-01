package org.qxteam.madartask.core.model.usecase

import kotlinx.coroutines.flow.Flow
import org.qxteam.madartask.core.model.User
import org.qxteam.madartask.core.model.UserRepository

class GetAllUsersUseCase(private val userRepository: UserRepository) {
    operator fun invoke(): Flow<List<User>> {
        return userRepository.getAllUsers()
    }
}
