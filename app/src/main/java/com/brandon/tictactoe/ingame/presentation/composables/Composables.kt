package com.brandon.tictactoe.ingame.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brandon.tictactoe.home.presentation.composables.GradientButton

@Composable
fun RequestReset(
    modifier: Modifier = Modifier,
    contentColor: Color,
    buttonGradient: Brush,
    message: String,
    onDismissRequestReset: () -> Unit,
    onConfirmApproveReset: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { /* no-op */ },
        confirmButton = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = message,
                    fontSize = 26.sp,
                    lineHeight = 30.sp,
                    color = contentColor,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    GradientButton(
                        text = "Reset",
                        gradient = buttonGradient,
                        modifier = Modifier
                            .padding(horizontal = 15.dp, vertical = 8.dp)
                            .width(125.dp),
                        textColor = contentColor,
                        textSize = 18.sp,
                        onClick = {
                            onConfirmApproveReset()
                        }
                    )
                    Spacer(Modifier.width(16.dp))
                    OutlinedButton(
                        onClick = {
                            onDismissRequestReset()
                        },
                        colors = buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(
                            text = "No",
                            color = contentColor,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    )
}

//TODO: Display the winner, play again, and exit in a composable
@Composable
fun PlayerWinner(
    modifier: Modifier = Modifier,
    contentColor: Color,
    buttonGradient: Brush,
    playerNumber: Int,
    onConfirmRequestPlayAgain: () -> Unit,
    onExitGame: () -> Unit,
    exitMessage: String = "Exit"
) {
    AlertDialog(
        onDismissRequest = { /* no-op */ },
        confirmButton = {
            Column(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = "Player $playerNumber won!",
                    fontSize = 26.sp,
                    lineHeight = 30.sp,
                    color = contentColor,
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    GradientButton(
                        text = "Play Again",
                        gradient = buttonGradient,
                        modifier = Modifier
                            .padding(horizontal = 15.dp, vertical = 8.dp)
                            .width(125.dp),
                        textColor = contentColor,
                        textSize = 18.sp,
                        onClick = {
                            onConfirmRequestPlayAgain()
                        }
                    )
                    Spacer(Modifier.width(16.dp))
                    OutlinedButton(
                        modifier = Modifier.padding(end = 14.dp),
                        onClick = {
                            onExitGame()
                        },
                        colors = buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(
                            text = exitMessage,
                            color = contentColor,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun LeaveRoom(
    contentColor: Color,
    buttonGradient: Brush,
    onDismissLeaveRoom: () -> Unit,
    onHomeScreen: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismissLeaveRoom()
        },
        confirmButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    text = "Do you want to leave the room?",
                    fontSize = 26.sp,
                    lineHeight = 30.sp,
                    color = contentColor
                )

                Spacer(Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    GradientButton(
                        text = "Yes",
                        gradient = buttonGradient,
                        modifier = Modifier
                            .padding(horizontal = 15.dp, vertical = 8.dp)
                            .width(125.dp),
                        textColor = contentColor,
                        textSize = 18.sp,
                        onClick = {
                            onHomeScreen()
                        }
                    )
                    Spacer(Modifier.width(16.dp))
                    OutlinedButton(
                        onClick = {
                            onDismissLeaveRoom()
                        },
                        colors = buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(
                            text = "No",
                            color = contentColor,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun ShowPlayerNumber(
    playerNumber: Int,
    contentColor: Color,
    onDismissLeaveRoom: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {
            onDismissLeaveRoom()
        },
        confirmButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    text = "You are player $playerNumber",
                    fontSize = 32.sp,
                    lineHeight = 30.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Spacer(Modifier.width(16.dp))
                    TextButton(
                        onClick = {
                            onDismissLeaveRoom()
                        },
                        colors = buttonColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Text(
                            text = "Close",
                            color = contentColor,
                            fontSize = 18.sp
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun AlertDialogLoading(
    exitButtonVisible: Boolean,
    onClick: () -> Unit,
    message: String
) {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(20.dp))
                Text(
                    modifier = Modifier.fillMaxWidth(),
                    text = message,
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(12.dp))
                if (exitButtonVisible) {
                    OutlinedButton(
                        colors = buttonColors(
                            containerColor = Color.Transparent
                        ),
                        onClick = {
                            onClick()
                        }
                    ) {
                        Text("Exit", color = Color.Black, fontSize = 18.sp)
                    }
                }
            }
        }
    )
}
