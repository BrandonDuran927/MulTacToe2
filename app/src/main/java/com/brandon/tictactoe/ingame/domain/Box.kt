package com.brandon.tictactoe.ingame.domain

import androidx.compose.ui.graphics.Color

data class Box(
    val id: Int,
    val x: Float,
    val y: Float,
    val color: Color,
    val label: String
)