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

/** Pieces per season: the bar on the court screen fills and restarts every 12. */
const val FIELD_PIECES = 12

/** Season 1 rebuilds the court, season 2 brings the players one by one. */
const val TOTAL_STAGES = 24

private val STAGES = listOf(
    R.drawable.court_stage_00,
    R.drawable.court_stage_01,
    R.drawable.court_stage_02,
    R.drawable.court_stage_03,
    R.drawable.court_stage_04,
    R.drawable.court_stage_05,
    R.drawable.court_stage_06,
    R.drawable.court_stage_07,
    R.drawable.court_stage_08,
    R.drawable.court_stage_09,
    R.drawable.court_stage_10,
    R.drawable.court_stage_11,
    R.drawable.court_stage_12,
    R.drawable.court_stage_13,
    R.drawable.court_stage_14,
    R.drawable.court_stage_15,
    R.drawable.court_stage_16,
    R.drawable.court_stage_17,
    R.drawable.court_stage_18,
    R.drawable.court_stage_19,
    R.drawable.court_stage_20,
    R.drawable.court_stage_21,
    R.drawable.court_stage_22,
    R.drawable.court_stage_23,
    R.drawable.court_stage_24
)

/** After the full team is in, the ball moves around the court like a rally. */
private val PLAYS = listOf(
    R.drawable.court_play_1,
    R.drawable.court_play_2,
    R.drawable.court_play_3,
    R.drawable.court_play_4,
    R.drawable.court_play_5,
    R.drawable.court_play_6
)

@DrawableRes
private fun stage(n: Int): Int = STAGES[n.coerceIn(0, TOTAL_STAGES)]

/** The image for a given total: a court stage, or a rally position after day 24. */
@DrawableRes
private fun imageFor(total: Int): Int =
    if (total <= TOTAL_STAGES) stage(total) else PLAYS[(total - TOTAL_STAGES - 1) % PLAYS.size]

/**
 * The night court, painted as an illustration. The newest image fades in over
 * the previous one while [reveal] goes from 0 to 1.
 */
@Composable
fun CourtArt(total: Int, reveal: Float, modifier: Modifier = Modifier) {
    val now = total.coerceAtLeast(0)
    Box(modifier) {
        if (now > 0 && reveal < 1f) {
            Image(
                painterResource(imageFor(now - 1)), null,
                contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize()
            )
        }
        Image(
            painterResource(imageFor(now)), null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .alpha(if (now > 0) reveal else 1f)
                .scale(if (now > 0) 0.97f + 0.03f * reveal else 1f)
        )
    }
}
