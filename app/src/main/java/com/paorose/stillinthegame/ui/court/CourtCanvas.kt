package com.paorose.stillinthegame.ui.court

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.paorose.stillinthegame.R

/** Pieces in the current court. More stages and more sports come in later updates. */
const val FIELD_PIECES = 12

@DrawableRes
private fun stage(n: Int): Int = when (n.coerceIn(0, FIELD_PIECES)) {
    0 -> R.drawable.court_stage_00
    1 -> R.drawable.court_stage_01
    2 -> R.drawable.court_stage_02
    3 -> R.drawable.court_stage_03
    4 -> R.drawable.court_stage_04
    5 -> R.drawable.court_stage_05
    6 -> R.drawable.court_stage_06
    7 -> R.drawable.court_stage_07
    8 -> R.drawable.court_stage_08
    9 -> R.drawable.court_stage_09
    10 -> R.drawable.court_stage_10
    11 -> R.drawable.court_stage_11
    else -> R.drawable.court_stage_12
}

/**
 * The night court, painted as an illustration. The newest piece fades in over
 * the previous stage while [reveal] goes from 0 to 1.
 */
@Composable
fun CourtArt(pieces: Int, reveal: Float, modifier: Modifier = Modifier) {
    val built = pieces.coerceIn(0, FIELD_PIECES)
    Box(modifier) {
        if (built > 0 && reveal < 1f) {
            Image(
                painterResource(stage(built - 1)), null,
                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
            )
        }
        Image(
            painterResource(stage(built)), null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (built > 0) reveal else 1f)
                .scale(if (built > 0) 0.97f + 0.03f * reveal else 1f)
        )
    }
}
