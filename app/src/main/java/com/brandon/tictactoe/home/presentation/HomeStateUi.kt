package com.brandon.tictactoe.home.presentation

import androidx.compose.runtime.Immutable

@Immutable
data class HomeStateUi(
    val roomID: Int? = null,  // For room creation
    val joinRoomID: String = "", // For room joining
    val isLoading: Boolean = false,
    val isError: Boolean = false,
    val isJoin: Boolean = false,
    val isPlayerCreator: Boolean = false,
    val listOfRooms: List<Int> = emptyList(),
    val navigationEvent: NavigationEvent = NavigationEvent.None
)

sealed class NavigationEvent {
    data class ToInGame(val roomID: String, val isCreator: Boolean, val p1Move: Int) : NavigationEvent()
    data object None : NavigationEvent()
}

