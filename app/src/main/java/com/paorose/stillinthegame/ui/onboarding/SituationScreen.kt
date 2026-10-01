package com.paorose.stillinthegame.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.paorose.stillinthegame.data.Miss
import com.paorose.stillinthegame.data.Situation
import com.paorose.stillinthegame.ui.common.PrimaryButton
import com.paorose.stillinthegame.ui.common.StepHeader
import com.paorose.stillinthegame.ui.theme.Chalk
import com.paorose.stillinthegame.ui.theme.CourtOrange
import com.paorose.stillinthegame.ui.theme.ElectricBlue
import com.paorose.stillinthegame.ui.theme.Midnight
import com.paorose.stillinthegame.ui.theme.MidnightLine
import com.paorose.stillinthegame.ui.theme.MidnightRaised
import com.paorose.stillinthegame.ui.theme.MutedOnDark

@Composable
fun SituationScreen(
    situation: Situation?,
    misses: Set<Miss>,
    onSituation: (Situation) -> Unit,
    onToggleMiss: (Miss) -> Unit,
    onNext: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Midnight)
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            StepHeader(step = 2, total = 4, color = ElectricBlue, track = MidnightLine)
            Spacer(Modifier.height(28.dp))
            Text("Where are\nyou now?", style = MaterialTheme.typography.headlineLarge, color = Chalk)
            Spacer(Modifier.height(20.dp))

            Situation.values().forEach { s ->
                SituationCard(s, selected = s == situation, onClick = { onSituation(s) })
                Spacer(Modifier.height(10.dp))
            }

            Spacer(Modifier.height(18.dp))
            Text("What do you miss most?", style = MaterialTheme.typography.titleLarge, color = Chalk)
            Text("Pick as many as you like.", style = MaterialTheme.typography.bodyMedium, color = MutedOnDark)
            Spacer(Modifier.height(14.dp))

            Miss.values().toList().chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    row.forEach { m ->
                        MissChip(m, selected = m in misses, onClick = { onToggleMiss(m) }, modifier = Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
            Spacer(Modifier.height(12.dp))
        }

        if (situation == null) {
            Text(
                "Choose where you are now to continue.",
                style = MaterialTheme.typography.bodyMedium,
                color = MutedOnDark,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }
        PrimaryButton(text = "Next", onClick = onNext, enabled = situation != null)
    }
}

@Composable
private fun SituationCard(s: Situation, selected: Boolean, onClick: () -> Unit) {
    // Selected: Electric Blue with Midnight text (4.8:1). Never white on blue.
    val bg = if (selected) ElectricBlue else MidnightRaised
    val ink = if (selected) Midnight else Chalk
    val sub = if (selected) Midnight else MutedOnDark
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(bg)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Canvas(Modifier.size(28.dp)) {
            val st = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
            val w = size.width
            when (s) {
                // A pause, not a bandage: being out is a pause, not a diagnosis.
                Situation.CANT -> {
                    drawLine(ink, Offset(w * 0.35f, w * 0.2f), Offset(w * 0.35f, w * 0.8f), st.width, StrokeCap.Round)
                    drawLine(ink, Offset(w * 0.65f, w * 0.2f), Offset(w * 0.65f, w * 0.8f), st.width, StrokeCap.Round)
                }
                // An eye: watching, staying close.
                Situation.DIFFERENT -> {
                    drawOval(ink, Offset(w * 0.05f, w * 0.28f), androidx.compose.ui.geometry.Size(w * 0.9f, w * 0.44f), style = st)
                    drawCircle(ink, w * 0.11f, center)
                }
                // A sunrise: coming back.
                Situation.RETURNING -> {
                    drawArc(ink, 180f, 180f, false, Offset(w * 0.2f, w * 0.35f), androidx.compose.ui.geometry.Size(w * 0.6f, w * 0.6f), style = st)
                    drawLine(ink, Offset(w * 0.05f, w * 0.65f), Offset(w * 0.95f, w * 0.65f), st.width, StrokeCap.Round)
                    drawLine(ink, Offset(w * 0.5f, w * 0.05f), Offset(w * 0.5f, w * 0.2f), st.width, StrokeCap.Round)
                }
            }
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(s.title, style = MaterialTheme.typography.titleMedium, color = ink)
            Text(s.subtitle, style = MaterialTheme.typography.bodyMedium, color = sub)
        }
    }
}

@Composable
private fun MissChip(m: Miss, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val bg: Color = if (selected) CourtOrange else MidnightRaised
    val ink: Color = if (selected) Midnight else Chalk
    Column(
        modifier
            .heightIn(min = 64.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(bg)
            .border(1.dp, if (selected) CourtOrange else MidnightLine, RoundedCornerShape(16.dp))
            .clickable(role = Role.Checkbox, onClick = onClick)
            .padding(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(m.label, style = MaterialTheme.typography.bodyMedium, color = ink, textAlign = TextAlign.Center)
    }
}
