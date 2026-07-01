package org.qxteam.madartask.core.database.di

import org.koin.dsl.module
import org.qxteam.madartask.core.database.UserDatabase
import org.qxteam.madartask.core.database.UserRepositoryImpl
import org.qxteam.madartask.core.model.UserRepository

val databaseModule = module {
    single { UserDatabase.getDatabase(get()) }
    single { get<UserDatabase>().userDao() }
    single<UserRepository> { UserRepositoryImpl(get()) }
}
