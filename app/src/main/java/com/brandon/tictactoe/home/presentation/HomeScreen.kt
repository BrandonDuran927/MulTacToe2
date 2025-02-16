package com.brandon.tictactoe.home.presentation

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.brandon.tictactoe.R
import com.brandon.tictactoe.common.InGameScreenRoute
import com.brandon.tictactoe.home.presentation.composables.AlertDialogLoading
import com.brandon.tictactoe.home.presentation.composables.GradientButton
import com.brandon.tictactoe.home.presentation.composables.JoinGame

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onAction: (HomeAction) -> Unit,
    stateUi: HomeStateUi,
    contentColor: Color
) {
    val buttonGradient =
        Brush.horizontalGradient(listOf(Color.Blue.copy(0.3f), Color.Magenta.copy(0.3f)))
    val localContext = LocalContext.current
    var isClickable by remember { mutableStateOf(true) }

    if (stateUi.isLoading) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            AlertDialogLoading()
        }
    }

    if (stateUi.isJoin) {
        JoinGame(
            stateUi = stateUi,
            onAction = onAction,
            contentColor = contentColor,
            buttonGradient =
            Brush.horizontalGradient(listOf(Color.Blue.copy(0.2f), Color.Magenta.copy(0.2f))),
            onJoinRoom = {
                onAction(HomeAction.OnJoin)
            },
            onDismissJoinGame = {
                onAction(HomeAction.OnResetJoin)
            }
        )
    }

    if (stateUi.isError) {
        Toast.makeText(localContext, "Room does not exists/room is already full", Toast.LENGTH_SHORT).show()
        onAction(HomeAction.OnResetError)
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = if (isSystemInDarkTheme()) painterResource(R.drawable.multactoelogoblacktheme) else painterResource(
                R.drawable.multactoelogo
            ),
            contentDescription = "MulTacToe Logo",
            modifier = Modifier.size(115.dp)
        )
        Text(
            text = "MulTacToe",
            color = contentColor,
            fontSize = 35.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Challenge Your Friends Online",
            color = contentColor.copy(alpha = 0.7f),
            fontSize = 22.sp,
            fontWeight = FontWeight.Medium
        )
        Spacer(Modifier.height(50.dp))
        GradientButton(
            text = "JOIN",
            gradient = buttonGradient,
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 50.dp, vertical = 8.dp),
            textColor = contentColor,
            textSize = 22.sp,
            onClick = {
                onAction(HomeAction.OnClickJoin)
            }
        )
        GradientButton(
            text = "CREATE",
            gradient = buttonGradient,
            modifier = Modifier
                .wrapContentWidth()
                .padding(horizontal = 35.dp, vertical = 8.dp),
            textColor = contentColor,
            textSize = 22.sp,
            onClick = {
                if (isClickable) {
                    isClickable = false
                    onAction(HomeAction.OnRoomCreated)
                }
            }
        )
    }


}


@Preview(showBackground = true)
@Composable
private fun Prev() {
    val contentColor = if (isSystemInDarkTheme()) Color.Black else Color.White
//    HomeScreen(
//        modifier = Modifier.background(contentColor),
//        onAction = {},
//        stateUi = HomeStateUi(),
//        contentColor = contentColor
//    )
}