package org.qxteam.madartask.core.model

import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getAllUsers(): Flow<List<User>>
    suspend fun saveUser(user: User)
    suspend fun deleteUser(user: User)
}
