package org.qxteam.madartask.feature.input.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import org.qxteam.madartask.feature.input.InputViewModel

val inputModule = module {
    viewModel { InputViewModel(get()) }
}
