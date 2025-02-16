package com.brandon.tictactoe.ingame.presentation

import androidx.compose.runtime.Immutable

@Immutable
data class GameStateUi(
    val availableRooms: List<Int> = emptyList(),
    val playerMoves: List<Int> =
        listOf(
            0, 0, 0,
            0, 0, 0,
            0, 0, 0
        ),
    val roomID: Int? = null,
    val roomCreator: Boolean = false,
    val p1Move: Int = 0,
    val isWaitingForChallenger: Boolean = false,
    val isPlayerTurn: Boolean = false,
    val isLeave: Boolean = false,
    val isError: Boolean = false,
    val winner: Int = 0,

    // FOR PLAYER REQUESTING A RESET
    val playerReset: Boolean = false,
    val thisPlayerReset: Boolean? = null,
    val pendingRequestReset: Boolean = false,
    val messageReset: String = "",

    // FOR PLAYER REQUESTING A PLAY AGAIN
    val playerPlayAgain: Boolean = false,
    val playAgainIsActive: Boolean = false,
    val thisPlayerPlayAgain: Boolean? = null,
    val pendingRequestPlayAgain: Boolean = false,
    val messagePlayAgain: String = "",

    // FOR PLAYER RECEIVING A RESET REQUEST
    val approveResetRequest: Boolean = false,
    val isOtherPlayerRequestingReset: Boolean = false,

    // FOR PLAYER RECEIVING A PLAY AGAIN REQUEST
    val approvePlayAgainRequest: Boolean = false,
    val isOtherPlayerRequestingPlayAgain: Boolean = false,

    val navigationEvent: NavigationEvent = NavigationEvent.None
)

sealed class NavigationEvent {
    data object ToHome : NavigationEvent()
    data object None : NavigationEvent()
}
