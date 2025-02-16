package com.brandon.tictactoe.ingame.presentation.composables

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.brandon.tictactoe.ingame.domain.Box

@Composable
fun PlayBoard(
    modifier: Modifier = Modifier,
    listOfMoves: List<Int>,
    onSelectedBox: (Box?) -> Unit,
    selectedBox: Box?,
    isPlayerTurn: Boolean
) {
    val myTextStyle = TextStyle.Default.copy(
        color = MaterialTheme.colorScheme.primary,
        fontSize = 70.sp,
        textAlign = TextAlign.Center,
    )
    val lineColor = myTextStyle.copy(color = MaterialTheme.colorScheme.onSurface).color
    val measurer = rememberTextMeasurer()
    val boxRange = 1..9

    val mutableBoxes = remember {
        mutableStateOf(listOf<Box>())
    }
    var currentBox by remember {
        mutableStateOf<Box?>(null)
    }
    var boxSize by remember {
        mutableStateOf(Size(0f, 0f))
    }
    var mutableTextStyle by remember {
        mutableStateOf(myTextStyle)
    }

    Canvas(
        modifier = modifier
            .fillMaxSize()
            .pointerInput(isPlayerTurn) {
                detectTapGestures { change ->
                    if (mutableBoxes.value.isNotEmpty() && boxSize.width != 0f && boxSize.height != 0f && isPlayerTurn) {
                        currentBox = selectedBox(
                            change = change,
                            boxes = mutableBoxes.value,
                            boxSize = boxSize
                        )
                        if (currentBox != null) {
                            onSelectedBox(currentBox!!)
                        }
                    }
                }
            }
    ) {
        boxSize = Size(size.width / 4f, size.width / 4f)
        mutableTextStyle = mutableTextStyle.copy(fontSize = (boxSize.width / 1.5f).toSp())

        var row2X = 0
        var row3X = 0
        var i = 0

        if (boxSize.width != 0f && boxSize.height != 0f && mutableBoxes.value.isEmpty()) {
            mutableBoxes.value = boxRange.map {
                val leftXAxis = (size.width / 2f) - (boxSize.width * 1.5f)
                var x = leftXAxis + (boxSize.width * i)
                val color =
                    if (i % 2 == 0) Color.Red.copy(alpha = 0.1f) else Color.Blue.copy(alpha = 0.1f)
                val y: Float = when (i) {
                    in 3..5 -> {
                        x = leftXAxis + (boxSize.width * row2X)
                        row2X += 1
                        boxSize.height + 20f * 2
                    }

                    in 6..8 -> {
                        x = leftXAxis + (boxSize.width * row3X)
                        row3X += 1
                        boxSize.height * 2 + (20f * 3)
                    }

                    else -> {
                        20f
                    }
                }
                i += 1
                Box(
                    id = i,
                    x = x,
                    y = y,
                    color = color,
                    label = ""
                )
            }
        }
        val lineOffsetReference = mutableBoxes.value.find { it.id == 1 }

        if (lineOffsetReference != null) {
            for (index in 0 until 2) {
                drawLine(
                    color = lineColor,
                    start = Offset(
                        x = lineOffsetReference.x,
                        y = (lineOffsetReference.y + (lineOffsetReference.x * 2)) + (index * (lineOffsetReference.x * 2) + 20f)
                    ),
                    end = Offset(
                        x = (lineOffsetReference.x + (boxSize.width * 3)),
                        y = (lineOffsetReference.y + boxSize.height) + (index * (boxSize.height) + 20f)
                    ),
                    strokeWidth = 15f
                )
            }
            for (index in 0 until 2) {
                drawLine(
                    color = lineColor,
                    start = Offset(
                        x = boxSize.width * 1.5f + (index * boxSize.width),
                        y = (lineOffsetReference.y)
                    ),
                    end = Offset(
                        x = lineOffsetReference.x + boxSize.height + (boxSize.height * index),
                        y = lineOffsetReference.y + boxSize.height * 3.15f
                    ),
                    strokeWidth = 15f
                )
            }
        }
        /*
           THIS IS WHERE THE MODIFICATION OF THE BOX HAPPENS
         */
        if (boxSize.width != 0f && boxSize.height != 0f) {
            //FIXME: Add a restriction when box is already occupied, it cannot be modified again; Fix leaving when the room creator does not exists
            val updatedBoxes = mutableBoxes.value.mapNotNull {
                if (it.id == selectedBox?.id) null else it
            }.toMutableList()

            selectedBox?.let {
                updatedBoxes.add(it)
            }

            mutableBoxes.value = updatedBoxes.sortedBy { it.id }.mapIndexed { index, box ->
                when (listOfMoves[index]) {
                    1 -> box.copy(label = "X")
                    2 -> box.copy(label = "O")
                    else -> box.copy(label = "")
                }
            }

            mutableBoxes.value.forEachIndexed { _, box ->
                val xAddition = (boxSize.width / 3.2f) - 10f
                val yAddition = (boxSize.width / 9f)

                drawText(
                    textLayoutResult = measurer.measure(
                        text = box.label,
                        style = mutableTextStyle
                    ),
                    topLeft = Offset(
                        x = box.x + xAddition,
                        y = box.y + yAddition
                    )
                )
                drawRect(
                    topLeft = Offset(
                        x = box.x,
                        y = box.y
                    ),
                    color = Color.Transparent,
                    size = boxSize
                )
            }
        }
    }
}

private fun selectedBox(
    change: Offset,
    boxes: List<Box>,
    boxSize: Size
): Box? {
    val box = boxes.firstOrNull { box ->
        val fWidth = box.x + boxSize.width
        val fHeight = box.y + boxSize.height

        change.x in box.x..fWidth && change.y in box.y..fHeight
    }
    return box
}


@Preview(showBackground = true)
@Composable
private fun Prev() {
    PlayBoard(
        onSelectedBox = {},
        selectedBox = null,
        listOfMoves = emptyList(),
        isPlayerTurn = true
    )
}