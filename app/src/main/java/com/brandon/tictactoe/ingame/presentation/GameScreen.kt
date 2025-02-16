@file:OptIn(ExperimentalMaterial3Api::class)

package com.brandon.tictactoe.ingame.presentation

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brandon.tictactoe.common.MultactoeScaffold
import com.brandon.tictactoe.ingame.presentation.composables.AlertDialogLoading
import com.brandon.tictactoe.home.presentation.composables.GradientButton
import com.brandon.tictactoe.ingame.domain.Box
import com.brandon.tictactoe.ingame.presentation.composables.LeaveRoom
import com.brandon.tictactoe.ingame.presentation.composables.PlayBoard
import com.brandon.tictactoe.ingame.presentation.composables.PlayerWinner
import com.brandon.tictactoe.ingame.presentation.composables.RequestReset

@Composable
fun GameScreenCore(
    modifier: Modifier = Modifier,
    stateUi: GameStateUi,
    onAction: (GameAction) -> Unit,
    roomID: Int,
    roomCreator: Boolean,
    contentColor: Color
) {
    GameScreen(
        modifier = modifier,
        stateUi = stateUi,
        onAction = onAction,
        roomID = roomID,
        roomCreator = roomCreator,
        contentColor = contentColor
    )
}

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    stateUi: GameStateUi,
    onAction: (GameAction) -> Unit,
    roomID: Int,
    roomCreator: Boolean,
    contentColor: Color
) {
    val localContext = LocalContext.current
    val buttonGradient =
        Brush.horizontalGradient(listOf(Color.Blue.copy(0.3f), Color.Magenta.copy(0.3f)))
    val playerTurn = stateUi.isPlayerTurn
    val listOfMoves = stateUi.playerMoves
    val playerNumber = if (roomCreator) 1 else 2
    val playerSymbol = if (roomCreator) "X" else "O"
    var selectedBox by remember { mutableStateOf<Box?>(null) }


    LaunchedEffect(selectedBox) {
        selectedBox?.takeIf { playerTurn && it.label.isEmpty() }?.let {
            onAction(
                GameAction.OnMoveChange(
                    isPlayerCreator = roomCreator,
                    moveValue = playerNumber,
                    pos = selectedBox!!.id - 1
                )
            )
            selectedBox = selectedBox!!.copy(label = playerSymbol)
        }
    }

    if (stateUi.pendingRequestPlayAgain) {
        AlertDialogLoading(
            exitButtonVisible = true,
            onClick = {
                onAction(GameAction.OnHomeScreen(roomCreator, roomID))
            },
            message = "Waiting for the other player to agree to play again."
        )
    }

    if (stateUi.winner != 0) {
        PlayerWinner(
            contentColor = contentColor,
            buttonGradient = buttonGradient,
            playerNumber = stateUi.winner,
            onConfirmRequestPlayAgain = {
                onAction(GameAction.OnPlayAgain)
            },
            onExitGame = {
                onAction(GameAction.OnClickLeave)
            }
        )
    }

    if (stateUi.messageReset.isNotEmpty() && stateUi.thisPlayerReset == true) {
        Toast.makeText(localContext, stateUi.messageReset, Toast.LENGTH_SHORT).show()
        onAction(GameAction.OnEmptyMessageReset)
    }

    if (stateUi.isOtherPlayerRequestingReset) {
        RequestReset(
            contentColor = contentColor,
            buttonGradient = buttonGradient,
            message = "Player ${if (roomCreator) 2 else 1} is requesting a reset",
            onConfirmApproveReset = {
                onAction(GameAction.OnApproveResetRequestOrNot(true))
            },
            onDismissRequestReset = {
                onAction(GameAction.OnApproveResetRequestOrNot(false))
            }
        )
    }

    if (stateUi.isWaitingForChallenger) {
        onAction(GameAction.OnResetMoves)
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AlertDialogLoading(
                exitButtonVisible = true,
                onClick = {
                    onAction(GameAction.OnHomeScreen(roomCreator, roomID))
                },
                message = "Waiting for other players"
            )
        }
    }

    if (stateUi.pendingRequestReset) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AlertDialogLoading(
                exitButtonVisible = false,
                onClick = {/* no-op */},
                message = "Please wait for player ${if (roomCreator) 2 else 1} to accept reset"
            )
        }
    }

    if (stateUi.playerReset) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            RequestReset(
                contentColor = contentColor,
                buttonGradient = buttonGradient,
                message = "Are you sure you want to request a reset?",
                onDismissRequestReset = {
                    onAction(GameAction.OnResetRequest)
                },
                onConfirmApproveReset = {
                    onAction(GameAction.OnGameResetAttempt)
                }
            )
        }
    }

    if (stateUi.isLeave) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LeaveRoom(
                contentColor = contentColor,
                onDismissLeaveRoom = {
                    onAction(GameAction.OnResetLeave)
                },
                buttonGradient = buttonGradient,
                onHomeScreen = {
                    onAction(GameAction.OnHomeScreen(roomCreator, roomID))
                }
            )
        }
    }

    MultactoeScaffold(
        topBar = {
            TopAppBar(
                title = {
                    /* no-op */
                },
                navigationIcon = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        TextButton(
                            modifier = modifier
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(start = 10.dp),
                            onClick = {
                                onAction(GameAction.OnClickLeave)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = null,
                                tint = Color.Black
                            )
                            Spacer(Modifier.width(12.dp))
                            Text(
                                text = "Exit",
                                color = Color.Black,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Normal
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                text = "Room ID: $roomID",
                fontSize = 24.sp,
                textAlign = TextAlign.Center,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )

            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(top = 60.dp)
            ) {
                Text(
                    modifier = modifier.padding(start = 32.dp),
                    text = if (playerTurn) "Player ${if (playerNumber == 1) 1 else 2} ${if (playerNumber == 1) "\"X\"" else "\"O\""} : Turn" else "Player ${if (playerNumber == 1) 1 else 2} ${if (playerNumber == 1) "\"X\"" else "\"O\""}: Not yet",
                    fontSize = 30.sp,
                    textAlign = TextAlign.Start,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )

                PlayBoard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(1 / 1f)
                        .padding(vertical = 32.dp),
                    onSelectedBox = { box ->
                        selectedBox = box
                    },
                    selectedBox = selectedBox,
                    listOfMoves = listOfMoves,
                    isPlayerTurn = playerTurn
                )

                Text(
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(end = 32.dp),
                    text = if (playerTurn) "Player ${if (playerNumber == 1) 2 else 1} ${if (playerNumber == 1) "\"O\"" else "\"X\""}: Not yet" else "Player ${if (playerNumber == 1) 2 else 1} ${if (playerNumber == 1) "\"O\"" else "\"X\""}: Turn",
                    fontSize = 30.sp,
                    textAlign = TextAlign.End,
                    color = contentColor,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(50.dp))
            GradientButton(
                text = "RESET GAME",
                gradient = buttonGradient,
                modifier = Modifier
                    .wrapContentWidth()
                    .padding(horizontal = 50.dp, vertical = 8.dp),
                textColor = contentColor,
                textSize = 22.sp,
                onClick = {
                    onAction(GameAction.OnPlayerReset)
                }
            )
        }
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
    val backgroundColor = if (isSystemInDarkTheme()) {
        Color.Black
    } else {
        Color.White
    }
    GameScreenCore(
        modifier = Modifier.background(backgroundColor),
        contentColor = contentColor,
        roomID = 0,
        stateUi = GameStateUi(),
        onAction = {},
        roomCreator = false
    )
}