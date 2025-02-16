package com.brandon.tictactoe.home.presentation.composables

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.brandon.tictactoe.home.presentation.HomeAction
import com.brandon.tictactoe.home.presentation.HomeStateUi

@Composable
fun GradientButton(
    text: String,
    gradient: Brush,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = { },
    textSize: TextUnit,
    textColor: Color
) {
    ElevatedButton(
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        onClick = { onClick() },
    ) {
        Box(
            modifier = Modifier
                .background(gradient)
                .then(modifier),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = text,
                fontSize = textSize,
                color = textColor
            )
        }
    }
}

@Composable
fun JoinGame(
    modifier: Modifier = Modifier,
    stateUi: HomeStateUi,
    onAction: (HomeAction) -> Unit,
    contentColor: Color,
    buttonGradient: Brush,
    onJoinRoom: () -> Unit,
    onDismissJoinGame: () -> Unit
) {
    val localContext = LocalContext.current

    AlertDialog(
        onDismissRequest = {
            onDismissJoinGame()
        },
        title = {
            Text(
                text = "Enter the room ID",
                fontSize = 26.sp,
                color = contentColor,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    modifier = Modifier.widthIn(280.dp),
                    value = stateUi.joinRoomID,
                    onValueChange = {
                        onAction(HomeAction.OnJoinRoomIdChange(it))
                    },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.Black,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedPlaceholderColor = Color.Black,
                    ),
                    placeholder = {
                        Text("Enter the room ID...")
                    },
                    shape = RoundedCornerShape(15.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        },
        confirmButton = {
            Row(
                modifier = modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.End
            ) {
                GradientButton(
                    text = "Enter",
                    gradient = buttonGradient,
                    modifier = Modifier
                        .padding(horizontal = 15.dp, vertical = 8.dp)
                        .width(125.dp),
                    textColor = contentColor,
                    textSize = 18.sp,
                    onClick = {
                        if (stateUi.joinRoomID.isNotEmpty()) {
                            onJoinRoom()
                        } else {
                            Toast.makeText(localContext, "Field cannot be empty", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                OutlinedButton(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    onClick = {
                        onDismissJoinGame()
                    }
                ) {
                    Text("Cancel", color = Color.Black, fontSize = 18.sp)
                }
            }
        }
    )
}

@Composable
fun AlertDialogLoading() {
    AlertDialog(
        onDismissRequest = {},
        confirmButton = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
                Spacer(Modifier.height(20.dp))
                Text(
                    text = "Loading, please wait",
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 22.sp
                )
            }
        }
    )
}


@Preview
@Composable
private fun Prev() {
    val buttonGradient =
        Brush.horizontalGradient(listOf(Color.Blue.copy(0.3f), Color.Magenta.copy(0.3f)))
    JoinGame(
        contentColor = Color.Black,
        buttonGradient = buttonGradient,
        onJoinRoom = {},
        onDismissJoinGame = {},
        stateUi = HomeStateUi(),
        onAction = {}
        )
}
