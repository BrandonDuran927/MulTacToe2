package com.brandon.tictactoe.ingame.presentation

sealed class GameAction {
    data class OnHomeScreen(
        val isPlayerCreator: Boolean,
        val roomID: Int
    ) : GameAction()

    data class OnRoomIdCreatorChange(
        val isPlayerCreator: Boolean,
        val roomID: Int,
        val p1Move: Int
    ) : GameAction()

    data class OnMoveChange(
        val isPlayerCreator: Boolean,
        val moveValue: Int,
        val pos: Int
    ) : GameAction()

    data class OnApproveResetRequestOrNot(
        val hasConfirmRequestReset: Boolean
    ) : GameAction()

    data object OnEmptyMessageReset : GameAction()
    data object OnWaitingForChallenger : GameAction()
    data object OnGameResetAttempt : GameAction()
    data object OnPlayerReset : GameAction()
    data object OnClickLeave : GameAction()
    data object OnResetRequest : GameAction()
    data object OnResetLeave : GameAction()
    data object OnResetState : GameAction()
    data object OnPlayAgain : GameAction()
    data object OnResetMoves : GameAction()
}