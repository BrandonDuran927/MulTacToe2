@file:OptIn(ExperimentalMaterial3Api::class)

package com.brandon.tictactoe.home.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.brandon.tictactoe.common.HomeScreenRoute
import com.brandon.tictactoe.common.InGameScreenRoute
import kotlinx.coroutines.delay

fun NavGraphBuilder.homeNavGraph(
    navController: NavController,
    homeViewModel: HomeViewModel
) {
    composable<HomeScreenRoute> {
        val contentColor = if (isSystemInDarkTheme()) {
            Color.White
        } else {
            Color.Black
        }
        val state by homeViewModel.state.collectAsStateWithLifecycle()

        LaunchedEffect(state.navigationEvent) {
            when (val event = state.navigationEvent) {
                is NavigationEvent.ToInGame -> {
                    homeViewModel.onAction(HomeAction.OnResetState)
                    navController.navigate(InGameScreenRoute(roomID = event.roomID.toInt(), roomCreator = event.isCreator, p1Move = event.p1Move))
                }
                NavigationEvent.None -> { /* no-op */ }
            }
        }

        HomeScreen(
            stateUi = state,
            onAction = homeViewModel::onAction,
            contentColor = contentColor
        )
    }
}
