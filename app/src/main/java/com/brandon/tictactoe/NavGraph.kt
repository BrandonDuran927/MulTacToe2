package com.brandon.tictactoe

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.brandon.tictactoe.common.HomeScreenRoute
import com.brandon.tictactoe.common.InGameScreenRoute
import com.brandon.tictactoe.home.presentation.HomeAction
import com.brandon.tictactoe.home.presentation.HomeViewModel
import com.brandon.tictactoe.home.presentation.homeNavGraph
import com.brandon.tictactoe.ingame.presentation.GameAction
import com.brandon.tictactoe.ingame.presentation.GameViewModel
import com.brandon.tictactoe.ingame.presentation.gameNavGraph
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun NavGraph(
    navController: NavHostController,
    homeViewModel: HomeViewModel = koinViewModel(),
    gameViewModel: GameViewModel = koinViewModel()
) {
    NavHost(navController = navController, startDestination = HomeScreenRoute) {
        homeNavGraph(
            navController = navController,
            homeViewModel = homeViewModel
        )

        gameNavGraph(
            gameViewModel = gameViewModel,
            navController = navController
        )
    }
}

