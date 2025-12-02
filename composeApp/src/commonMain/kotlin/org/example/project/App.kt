package org.example.project

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberScrollable2DState
import androidx.compose.foundation.gestures.scrollable2D
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.displayCutoutPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberOverscrollEffect
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.constrainHeight
import androidx.compose.ui.unit.constrainWidth
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceIn
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMaxBy
import androidx.compose.ui.util.fastRoundToInt
import kotlin.random.Random

@Composable
fun App() {
    MaterialTheme {
        Surface {
            ScrollableContainer(
                modifier = Modifier
                    .displayCutoutPadding()
            ) { ColorGrid() }
        }
    }
}


@Composable
private fun ScrollableContainer(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var offset by remember { mutableStateOf(IntOffset.Zero) }

    var contentSize by remember { mutableStateOf(IntSize.Zero) }
    var viewportSize by remember { mutableStateOf(IntSize.Zero) }

    Layout(
        content = content,
        modifier = modifier
            .onSizeChanged { viewportSize = it }
            .scrollable2D(
                state = rememberScrollable2DState { delta ->
                    val scrollRangeX = contentSize.width - viewportSize.width
                    val scrollRangeY = contentSize.height - viewportSize.height

                    val oldX = offset.x
                    val oldY = offset.y

                    // Dragging right (positive delta) moves the viewport left (decreasing offset).
                    val newX = (oldX - delta.x).fastCoerceIn(0f, scrollRangeX.toFloat())
                    val newY = (oldY - delta.y).fastCoerceIn(0f, scrollRangeY.toFloat())

                    offset = IntOffset(newX.fastRoundToInt(), newY.fastRoundToInt())

                    // Return the amount of delta consumed (reverse the sign calculation)
                    Offset(oldX - newX, oldY - newY)
                },
            )
            .clipToBounds(),
    ) { measurables, constraints ->
        val placeables = measurables.fastMap { it.measure(Constraints()) }

        val maxWidth = placeables.fastMaxBy { it.width }?.width ?: 0
        val maxHeight = placeables.fastMaxBy { it.height }?.height ?: 0

        contentSize = IntSize(maxWidth, maxHeight)

        layout(constraints.constrainWidth(maxWidth), constraints.constrainHeight(maxHeight)) {
            placeables.fastForEach {
                it.place(-offset)
            }
        }
    }
}

@Composable
private fun ColorGrid() {
    Column {
        repeat(100) { row ->
            Row {
                repeat(100) { column ->
                    Column {
                        Square(row, column)
                    }
                }
            }
        }
    }
}

@Composable
private fun Square(row: Int, column: Int) = key(row to column) {
    val color = remember(row, column) {
        if (row == 0 || row == 99 || column == 0 || column == 99) {
            Color.Gray
        } else {
            Color(
                red = Random.nextInt(255),
                green = Random.nextInt(255),
                blue = Random.nextInt(255),
            )
        }
    }

    Box(
        modifier =
            Modifier.size(20.dp).background(color)
    )
}