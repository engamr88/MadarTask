package org.qxteam.madartask.core.model.usecase

import org.qxteam.madartask.core.model.User
import org.qxteam.madartask.core.model.UserRepository

class DeleteUserUseCase(private val userRepository: UserRepository) {
    suspend operator fun invoke(user: User) {
        userRepository.deleteUser(user)
    }
}
