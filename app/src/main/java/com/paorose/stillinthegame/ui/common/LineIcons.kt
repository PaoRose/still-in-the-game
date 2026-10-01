package com.paorose.stillinthegame.ui.common

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Line icons from the design assets (48x48 viewBox, 2.5 stroke, round caps). */
object LineIcons {
    val BASKETBALL = listOf(
        "M6.0 24.0A18.0 18.0 0 1 0 42.0 24.0A18.0 18.0 0 1 0 6.0 24.0Z",
        "M6 24h36",
        "M24 6v36",
        "M11 11c5 4 7 8 7 13s-2 9-7 13",
        "M37 11c-5 4-7 8-7 13s2 9 7 13"
    )
    val BELONGING = listOf(
        "M6.0 24.0A18.0 18.0 0 1 0 42.0 24.0A18.0 18.0 0 1 0 6.0 24.0Z",
        "M17 28c2 3 4 4 7 4s5-1 7-4",
        "M18 19v2",
        "M30 19v2"
    )
    val BENCH = listOf(
        "M6 22h36",
        "M8 28h32",
        "M12 28v10",
        "M36 28v10",
        "M10 22v-6h28v6"
    )
    val COMPETITION = listOf(
        "M15 8h18v8a9 9 0 0 1-18 0z",
        "M15 11H8a6 6 0 0 0 7 7",
        "M33 11h7a6 6 0 0 1-7 7",
        "M24 25v7",
        "M16 40h16",
        "M18 40l2-8h8l2 8"
    )
    val EYE = listOf(
        "M4 24s7-12 20-12 20 12 20 12-7 12-20 12S4 24 4 24z",
        "M19.0 24.0A5.0 5.0 0 1 0 29.0 24.0A5.0 5.0 0 1 0 19.0 24.0Z"
    )
    val FOOTBALL = listOf(
        "M7.0 10.0H41.0A2.0 2.0 0 0 1 43.0 12.0V36.0A2.0 2.0 0 0 1 41.0 38.0H7.0A2.0 2.0 0 0 1 5.0 36.0V12.0A2.0 2.0 0 0 1 7.0 10.0Z",
        "M24 10v28",
        "M19.0 24.0A5.0 5.0 0 1 0 29.0 24.0A5.0 5.0 0 1 0 19.0 24.0Z",
        "M5 18h6v12H5",
        "M43 18h-6v12h6"
    )
    val IMPROVING = listOf(
        "M8 38h32",
        "M11.0 26.0H17.0V38.0H11.0Z",
        "M21.0 18.0H27.0V38.0H21.0Z",
        "M31.0 10.0H37.0V38.0H31.0Z"
    )
    val PAUSE = listOf(
        "M16.0 10.0H18.0A2.0 2.0 0 0 1 20.0 12.0V36.0A2.0 2.0 0 0 1 18.0 38.0H16.0A2.0 2.0 0 0 1 14.0 36.0V12.0A2.0 2.0 0 0 1 16.0 10.0Z",
        "M30.0 10.0H32.0A2.0 2.0 0 0 1 34.0 12.0V36.0A2.0 2.0 0 0 1 32.0 38.0H30.0A2.0 2.0 0 0 1 28.0 36.0V12.0A2.0 2.0 0 0 1 30.0 10.0Z"
    )
    val PLAYING = listOf(
        "M24 40S6 30 6 18a9 9 0 0 1 18-3 9 9 0 0 1 18 3c0 12-18 22-18 22z"
    )
    val ROUTINE = listOf(
        "M10.0 10.0H38.0A3.0 3.0 0 0 1 41.0 13.0V37.0A3.0 3.0 0 0 1 38.0 40.0H10.0A3.0 3.0 0 0 1 7.0 37.0V13.0A3.0 3.0 0 0 1 10.0 10.0Z",
        "M7 18h34",
        "M16 6v8",
        "M32 6v8",
        "M15 27l5 5 11-10"
    )
    val RUNNING = listOf(
        "M17.0 12.0H31.0A12.0 12.0 0 0 1 43.0 24.0V24.0A12.0 12.0 0 0 1 31.0 36.0H17.0A12.0 12.0 0 0 1 5.0 24.0V24.0A12.0 12.0 0 0 1 17.0 12.0Z",
        "M18.0 18.0H30.0A6.0 6.0 0 0 1 36.0 24.0V24.0A6.0 6.0 0 0 1 30.0 30.0H18.0A6.0 6.0 0 0 1 12.0 24.0V24.0A6.0 6.0 0 0 1 18.0 18.0Z",
        "M36 12v6"
    )
    val SUNRISE = listOf(
        "M12 32a12 12 0 0 1 24 0",
        "M4 32h40",
        "M24 8v6",
        "M10 16l4 4",
        "M38 16l-4 4",
        "M10 40h28"
    )
    val TEAMMATES = listOf(
        "M11.0 17.0A6.0 6.0 0 1 0 23.0 17.0A6.0 6.0 0 1 0 11.0 17.0Z",
        "M28.0 19.0A5.0 5.0 0 1 0 38.0 19.0A5.0 5.0 0 1 0 28.0 19.0Z",
        "M6 38c1-7 5-11 11-11s10 4 11 11",
        "M28 29c2-2 3-2 5-2 5 0 8 4 9 10"
    )
    val VOLLEYBALL = listOf(
        "M6.0 24.0A18.0 18.0 0 1 0 42.0 24.0A18.0 18.0 0 1 0 6.0 24.0Z",
        "M24 6c-3 7-3 15 2 22",
        "M41 18c-7-1-14 2-18 8",
        "M10 36c6-4 10-6 16-6 6 0 9 2 12 4"
    )
}

@Composable
fun LineIcon(paths: List<String>, color: Color, size: Dp, modifier: Modifier = Modifier) {
    val parsed = remember(paths) { paths.map { PathParser().parsePathString(it).toPath() } }
    Canvas(modifier.size(size)) {
        val k = this.size.width / 48f
        scale(k, k, pivot = androidx.compose.ui.geometry.Offset.Zero) {
            parsed.forEach { p ->
                drawPath(p, color, style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }
    }
}
