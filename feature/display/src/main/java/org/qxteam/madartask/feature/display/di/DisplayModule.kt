package org.qxteam.madartask.feature.display.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.qxteam.madartask.feature.display.DisplayViewModel

val displayModule = module {
    viewModel { DisplayViewModel(get(), get()) }
}
