package com.paorose.stillinthegame.ui.court

import com.paorose.stillinthegame.ui.theme.MidnightLine
import com.paorose.stillinthegame.ui.theme.ElectricBlue
import com.paorose.stillinthegame.ui.common.TextAction
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.border
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

/** Short lines for each new piece. The peak moment of the app. */
private fun pieceLine(piece: Int): String {
    val lines = listOf(
        "The ground is ready. Your place is still here.",
        "Your court has its color back.",
        "The lines are back.",
        "The posts are standing again.",
        "The net is up.",
        "The ball is back in play.",
        "There's a bench for you, right by the court.",
        "Trees around your court. It's starting to feel like home.",
        "The street lamp is on. Someone left the light on for you.",
        "Your bag and your bottle, ready when you are.",
        "Lights on. The court is glowing.",
        "The scoreboard is on. Your court is complete. Next season, your team arrives.",
        "Your setter just walked in.",
        "The other team's first player is warming up.",
        "Your outside hitter is here.",
        "Their outside hitter answers.",
        "Your middle blocker joined.",
        "Their middle blocker too. It's starting to feel like a match.",
        "Your opposite is on the court.",
        "Four of them across the net now.",
        "Your libero is here. Nothing gets past them.",
        "Their libero just walked in.",
        "Your other hitter is here. Your team is complete.",
        "Six on each side. Game on. You never left the game."
    )
    return lines[(piece - 1).coerceIn(0, lines.lastIndex)]
}

@Composable
fun CourtScreen(
    sport: Sport,
    connected: Int,
    day: Int,
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
    val pieces = connected.coerceAtMost(TOTAL_STAGES)

    Column(
        Modifier
            .fillMaxSize()
            .background(Midnight)
            .systemBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        // Top row, 76 tall.
        Row(
            Modifier
                .fillMaxWidth()
                .height(76.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Plus members see the "+" in the app name, like a membership badge.
            Text(
                androidx.compose.ui.text.buildAnnotatedString {
                    append("STILL IN THE GAME")
                    if (isPlus) {
                        pushStyle(androidx.compose.ui.text.SpanStyle(color = RallyYellow))
                        append("+")
                        pop()
                    }
                },
                style = MaterialTheme.typography.labelSmall,
                color = MutedOnDark
            )
            Row {
                if (!isPlus) TextAction("Plus", RallyYellow, onPlus)
                TextAction("Settings", Chalk, onSettings)
            }
        }
        Spacer(Modifier.height(4.dp))
        Spacer(Modifier.height(8.dp))
        Text(
            "Your comeback journey",
            style = MaterialTheme.typography.headlineLarge.copy(fontSize = 32.sp, lineHeight = 38.sp),
            color = Chalk
        )
        Spacer(Modifier.height(18.dp))

        // Hero card: the field of return, with a soft blue glow in the middle.
        val shape = RoundedCornerShape(24.dp)
        Box(
            Modifier
                .fillMaxWidth()
                .weight(1f)
                .heightIn(max = 430.dp)
                .clip(shape)
                .background(MidnightRaised)
                .border(1.dp, MidnightLine, shape)
                .background(Brush.radialGradient(listOf(ElectricBlue.copy(alpha = 0.12f), Color.Transparent)))
        ) {
            CourtArt(
                total = connected,
                reveal = reveal.value,
                modifier = Modifier
                    .fillMaxSize()
                    .semantics { contentDescription = "Your ${sport.field}: $pieces of $TOTAL_STAGES pieces" }
            )
        }

        Spacer(Modifier.height(12.dp))
        // One mark per piece. After the court is complete, a new season starts the bar again.
        val season = if (connected <= FIELD_PIECES) 1 else (connected - 1) / FIELD_PIECES + 1
        val inSeason = if (connected == 0) 0 else (connected - 1) % FIELD_PIECES + 1
        if (season > 1) {
            Text(
                "SEASON $season · $inSeason OF $FIELD_PIECES",
                style = MaterialTheme.typography.labelSmall,
                color = RallyYellow,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        Row(
            Modifier.fillMaxWidth().semantics { contentDescription = "$pieces of $FIELD_PIECES pieces" },
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            repeat(FIELD_PIECES) { i ->
                Box(
                    Modifier
                        .weight(1f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(if (i < inSeason) CourtOrange else MidnightLine)
                )
            }
        }
        Spacer(Modifier.height(12.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            // Big editorial number. It only ever counts up.
            Text(
                "%02d".format(connected),
                fontFamily = Sora,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 56.sp,
                lineHeight = 60.sp,
                color = CourtOrange
            )
            Spacer(Modifier.width(16.dp))
            Text(
                "ACTIVITIES\nCOMPLETED",
                style = MaterialTheme.typography.labelSmall,
                color = Chalk,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
        Spacer(Modifier.height(4.dp))
        val line = when {
            connected == 0 -> "Every small action brings a piece back. Your first one is waiting."
            connected > TOTAL_STAGES -> listOf(
                "Rally on. The ball is in your team's hands.",
                "Over the net. Their side now.",
                "Dig! They kept it alive.",
                "Set up for the spike.",
                "Low ball, great save.",
                "Long rally. Nobody is giving up, including you."
            )[(connected - TOTAL_STAGES - 1) % 6] + " Season $season."
            else -> pieceLine(pieces)
        }
        Text(line, style = MaterialTheme.typography.bodyMedium, color = MutedOnDark)
        Spacer(Modifier.height(16.dp))
        PrimaryButton(text = "Today's connection", onClick = onToday)
        Spacer(Modifier.height(32.dp))
    }
}
