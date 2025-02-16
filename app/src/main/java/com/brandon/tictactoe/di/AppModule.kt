package com.brandon.tictactoe.di

import com.brandon.tictactoe.core.data.FirebaseRemoteDataSourceImpl
import com.brandon.tictactoe.core.data.RepositoryImpl
import com.brandon.tictactoe.core.domain.FirebaseRemoteDataSource
import com.brandon.tictactoe.core.domain.Repository
import com.brandon.tictactoe.home.presentation.HomeViewModel
import com.brandon.tictactoe.ingame.presentation.GameViewModel
import com.google.firebase.database.FirebaseDatabase
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

val appModule = module {
    // single<Repository> { RepositoryImpl() }
    single {FirebaseDatabase.getInstance()}
    singleOf(::FirebaseRemoteDataSourceImpl).bind<FirebaseRemoteDataSource>()
    singleOf(::RepositoryImpl).bind<Repository>()

    viewModelOf(::HomeViewModel)
    viewModelOf(::GameViewModel)
}