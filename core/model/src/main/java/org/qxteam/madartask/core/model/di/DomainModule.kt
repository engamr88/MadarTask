package org.qxteam.madartask.core.model.di

import org.koin.dsl.module
import org.qxteam.madartask.core.model.usecase.DeleteUserUseCase
import org.qxteam.madartask.core.model.usecase.GetAllUsersUseCase
import org.qxteam.madartask.core.model.usecase.SaveUserUseCase

val domainModule = module {
    factory { SaveUserUseCase(get()) }
    factory { GetAllUsersUseCase(get()) }
    factory { DeleteUserUseCase(get()) }
}
