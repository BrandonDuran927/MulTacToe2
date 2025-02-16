package com.brandon.tictactoe.ingame.presentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.brandon.tictactoe.common.HomeScreenRoute
import com.brandon.tictactoe.common.InGameScreenRoute

fun NavGraphBuilder.gameNavGraph(
    modifier: Modifier = Modifier,
    navController: NavController,
    gameViewModel: GameViewModel,
) {
    composable<InGameScreenRoute> {
        val roomID = it.toRoute<InGameScreenRoute>().roomID
        val roomCreator = it.toRoute<InGameScreenRoute>().roomCreator
        val p1Move = it.toRoute<InGameScreenRoute>().p1Move
        val contentColor = if (isSystemInDarkTheme()) {
            Color.White
        } else {
            Color.Black
        }
        val state by gameViewModel.state.collectAsState()

        DisposableEffect(Unit) {
            onDispose {
                gameViewModel.onAction(GameAction.OnResetState)
            }
        }

        //TODO: Leaving the room manually will cause a problem forcing the navigation to HOME SCREEN route and unable to join another room
        LaunchedEffect(key1 = state.navigationEvent) {
            when (state.navigationEvent) {
                NavigationEvent.ToHome -> {
                    gameViewModel.onAction(GameAction.OnResetState)
                    navController.navigate(HomeScreenRoute) {
                        popUpTo<InGameScreenRoute> {
                            inclusive = true
                        }
                    }
                }

                NavigationEvent.None -> Unit
            }
        }
        LaunchedEffect(true) {
            gameViewModel.onAction(GameAction.OnRoomIdCreatorChange(roomCreator, roomID, p1Move))
        }

        GameScreenCore(
            modifier = modifier,
            roomID = roomID,
            roomCreator = roomCreator,
            contentColor = contentColor,
            stateUi = state,
            onAction = gameViewModel::onAction
        )
    }
}

@PreviewLightDark
@Composable
private fun Prev() {
    val contentColor = if (isSystemInDarkTheme()) {
        Color.White
    } else {
        Color.Black
    }

    GameScreenCore(
        contentColor = contentColor,
        roomID = 0,
        stateUi = GameStateUi(),
        onAction = {},
        roomCreator = false
    )
}