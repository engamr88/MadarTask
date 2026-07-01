package org.qxteam.madartask.core.database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.qxteam.madartask.core.model.User
import org.qxteam.madartask.core.model.UserRepository

class UserRepositoryImpl(private val userDao: UserDao) : UserRepository {
    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsers().map { entities ->
            entities.map { it.toDomainModel() }
        }
    }

    override suspend fun saveUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.insertUser(UserEntity.fromDomainModel(user))
        }
    }

    override suspend fun deleteUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.deleteUser(UserEntity.fromDomainModel(user))
        }
    }
}
