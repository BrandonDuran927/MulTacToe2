package com.brandon.tictactoe.common

import kotlinx.serialization.Serializable

@Serializable
data class InGameScreenRoute(val roomID: Int, val roomCreator: Boolean, val p1Move: Int)

@Serializable
data object HomeScreenRoute