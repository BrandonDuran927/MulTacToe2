package com.brandon.tictactoe.home.presentation

sealed interface HomeAction {
    data object OnJoin: HomeAction
    data object OnClickJoin : HomeAction
    data object OnRoomCreated: HomeAction
    data object OnResetRoomID: HomeAction
    data object OnResetJoin : HomeAction
    data object OnResetError : HomeAction
    data object OnResetState : HomeAction

    data class OnJoinRoomIdChange(val joinRoomId: String): HomeAction
}