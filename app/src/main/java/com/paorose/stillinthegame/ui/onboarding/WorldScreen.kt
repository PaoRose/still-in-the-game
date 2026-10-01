package com.paorose.stillinthegame.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.paorose.stillinthegame.data.Sport
import com.paorose.stillinthegame.ui.common.PrimaryButton
import com.paorose.stillinthegame.ui.common.StepHeader
import com.paorose.stillinthegame.ui.theme.Chalk
import com.paorose.stillinthegame.ui.theme.ChalkLine
import com.paorose.stillinthegame.ui.theme.ChalkRaised
import com.paorose.stillinthegame.ui.theme.CourtOrange
import com.paorose.stillinthegame.ui.theme.Midnight
import com.paorose.stillinthegame.ui.theme.MutedOnLight

@Composable
fun WorldScreen(selected: Sport?, onSelect: (Sport) -> Unit, onNext: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Chalk)
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        StepHeader(step = 1, total = 4, color = Midnight, track = ChalkLine)
        Spacer(Modifier.height(32.dp))
        Text("What's your\nworld?", style = MaterialTheme.typography.headlineLarge, color = Midnight)
        Spacer(Modifier.height(8.dp))
        Text(
            "Pick the place you want to stay connected to.",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedOnLight
        )
        Spacer(Modifier.height(24.dp))

        val sports = Sport.values().toList()
        sports.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { sport ->
                    SportCard(
                        sport = sport,
                        selected = sport == selected,
                        onClick = { onSelect(sport) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.weight(1f))
        if (selected == null) {
            Text(
                "Pick one to continue.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedOnLight,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
        PrimaryButton(
            text = "Next",
            onClick = onNext,
            enabled = selected != null,
            container = Midnight,
            content = Chalk,
            disabledContainer = ChalkLine,
            disabledContent = MutedOnLight
        )
    }
}

@Composable
private fun SportCard(sport: Sport, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bg = if (selected) CourtOrange else ChalkRaised
    val ink = Midnight
    Column(
        modifier = modifier
            .aspectRatio(1.1f)
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .border(1.dp, if (selected) CourtOrange else ChalkLine, RoundedCornerShape(20.dp))
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Canvas(Modifier.size(40.dp)) { drawSportGlyph(sport, ink) }
        Spacer(Modifier.height(12.dp))
        Text(sport.label, style = MaterialTheme.typography.titleMedium, color = ink)
    }
}

/** Simple line glyphs, so the app needs no icon library. */
private fun DrawScope.drawSportGlyph(sport: Sport, ink: Color) {
    val s = Stroke(width = 2.dp.toPx())
    val w = size.width
    val c = center
    val r = w * 0.42f
    when (sport) {
        Sport.VOLLEYBALL -> {
            drawCircle(ink, r, c, style = s)
            drawArc(ink, 200f, 120f, false, Offset(c.x - r * 1.6f, c.y - r * 0.4f), Size(r * 2.2f, r * 2.2f), style = s)
            drawArc(ink, 300f, 120f, false, Offset(c.x - r * 0.3f, c.y - r * 1.5f), Size(r * 2.2f, r * 2.2f), style = s)
        }
        Sport.BASKETBALL -> {
            drawCircle(ink, r, c, style = s)
            drawLine(ink, Offset(c.x - r, c.y), Offset(c.x + r, c.y), s.width)
            drawLine(ink, Offset(c.x, c.y - r), Offset(c.x, c.y + r), s.width)
            drawArc(ink, -60f, 120f, false, Offset(c.x - r * 2.1f, c.y - r), Size(r * 2f, r * 2f), style = s)
            drawArc(ink, 120f, 120f, false, Offset(c.x + r * 0.1f, c.y - r), Size(r * 2f, r * 2f), style = s)
        }
        Sport.FOOTBALL -> {
            drawRect(ink, Offset(w * 0.05f, w * 0.18f), Size(w * 0.9f, w * 0.64f), style = s)
            drawLine(ink, Offset(c.x, w * 0.18f), Offset(c.x, w * 0.82f), s.width)
            drawCircle(ink, w * 0.12f, c, style = s)
        }
        Sport.RUNNING -> {
            drawRoundRect(ink, Offset(w * 0.05f, w * 0.22f), Size(w * 0.9f, w * 0.56f), androidx.compose.ui.geometry.CornerRadius(w * 0.28f), style = s)
            drawRoundRect(ink, Offset(w * 0.2f, w * 0.36f), Size(w * 0.6f, w * 0.28f), androidx.compose.ui.geometry.CornerRadius(w * 0.14f), style = s)
        }
    }
}
