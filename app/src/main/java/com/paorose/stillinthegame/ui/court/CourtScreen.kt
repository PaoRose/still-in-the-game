package com.paorose.stillinthegame.ui.court

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paorose.stillinthegame.data.Sport
import com.paorose.stillinthegame.ui.common.PrimaryButton
import com.paorose.stillinthegame.ui.theme.Chalk
import com.paorose.stillinthegame.ui.theme.CourtOrange
import com.paorose.stillinthegame.ui.theme.Midnight
import com.paorose.stillinthegame.ui.theme.MidnightRaised
import com.paorose.stillinthegame.ui.theme.MutedOnDark
import com.paorose.stillinthegame.ui.theme.RallyYellow
import com.paorose.stillinthegame.ui.theme.Sora

/** Short lines for each new piece, per sport. The peak moment of the app. */
private fun pieceLine(sport: Sport, piece: Int): String {
    val fourth = when (sport) {
        Sport.VOLLEYBALL -> "The net is up."
        Sport.BASKETBALL -> "The hoops are back."
        Sport.FOOTBALL -> "The goals are back."
        Sport.RUNNING -> "The finish line is back."
    }
    val fifth = if (sport == Sport.RUNNING) "Your spot on the start line is waiting." else "The ball is back in play."
    val lines = listOf(
        "The lines are back. Your place is still here.",
        "Halfway there. You're still part of this.",
        "Your markings are back on the ${sport.field}.",
        fourth,
        fifth,
        "Your teammates are here.",
        "The lights just came on.",
        "Full house. You never left the game."
    )
    return lines[(piece - 1).coerceIn(0, lines.lastIndex)]
}

@Composable
fun CourtScreen(
    sport: Sport,
    connected: Int,
    animateNewest: Boolean,
    isPlus: Boolean,
    onToday: () -> Unit,
    onPlus: () -> Unit,
    onSettings: () -> Unit
) {
    // Keyed on the count, so the new piece animates once the save has landed.
    val reveal = remember(connected) { Animatable(if (animateNewest) 0f else 1f) }
    LaunchedEffect(connected, animateNewest) {
        if (animateNewest) reveal.animateTo(1f, tween(1400, easing = FastOutSlowInEasing))
    }
    val pieces = connected.coerceAtMost(FIELD_PIECES)

    Column(
        Modifier
            .fillMaxSize()
            .background(Midnight)
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("STILL IN THE GAME", style = MaterialTheme.typography.labelSmall, color = MutedOnDark)
            Row {
                if (!isPlus) TextButton(onClick = onPlus) { Text("Plus", style = MaterialTheme.typography.labelLarge, color = RallyYellow) }
                TextButton(onClick = onSettings) { Text("Settings", style = MaterialTheme.typography.labelLarge, color = Chalk) }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("Your comeback\njourney", style = MaterialTheme.typography.headlineLarge, color = Chalk)
        Spacer(Modifier.height(16.dp))

        CourtCanvas(
            sport = sport,
            pieces = pieces,
            reveal = reveal.value,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(24.dp))
                .background(MidnightRaised)
                .padding(8.dp)
                .semantics { contentDescription = "Your ${sport.field}: $pieces of $FIELD_PIECES pieces rebuilt" }
        )

        Spacer(Modifier.height(16.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            // Big editorial number. It only ever counts up.
            Text(
                "%02d".format(connected),
                fontFamily = Sora,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 56.sp,
                lineHeight = 56.sp,
                color = CourtOrange
            )
            Spacer(Modifier.width(12.dp))
            Text(
                "WAYS YOU STAYED\nPART OF ${sport.label.uppercase()}",
                style = MaterialTheme.typography.labelSmall,
                color = Chalk,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Spacer(Modifier.height(8.dp))
        val line = when {
            connected == 0 -> "Every small action brings a piece back. Your first one is waiting."
            else -> pieceLine(sport, pieces)
        }
        Text(line, style = MaterialTheme.typography.bodyMedium, color = MutedOnDark)
        Spacer(Modifier.height(16.dp))
        PrimaryButton(text = "Today's connection", onClick = onToday)
    }
}
