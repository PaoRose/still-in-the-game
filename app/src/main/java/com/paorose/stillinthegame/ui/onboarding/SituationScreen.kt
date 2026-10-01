package com.paorose.stillinthegame.ui.onboarding

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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paorose.stillinthegame.data.Miss
import com.paorose.stillinthegame.data.Situation
import com.paorose.stillinthegame.ui.common.Hint
import com.paorose.stillinthegame.ui.common.LineIcon
import com.paorose.stillinthegame.ui.common.PrimaryButton
import com.paorose.stillinthegame.ui.common.StepHeader
import com.paorose.stillinthegame.ui.common.icon
import com.paorose.stillinthegame.ui.theme.Chalk
import com.paorose.stillinthegame.ui.theme.CourtOrange
import com.paorose.stillinthegame.ui.theme.ElectricBlue
import com.paorose.stillinthegame.ui.theme.Inter
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
    onNext: () -> Unit,
    editing: Boolean = false
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Midnight)
            .systemBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(20.dp))
            if (!editing) StepHeader(step = 2, total = 2, color = ElectricBlue, track = MidnightLine)
            Spacer(Modifier.height(18.dp))
            Text("Where are you now?", style = MaterialTheme.typography.headlineLarge, color = Chalk)
            Spacer(Modifier.height(16.dp))

            Situation.values().forEach { s ->
                SituationCard(s, s == situation) { onSituation(s) }
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                Text(
                    "What do you miss most?",
                    style = MaterialTheme.typography.titleLarge,
                    color = Chalk,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    "Pick as many as you like.",
                    style = TextStyle(fontFamily = Inter, fontSize = 12.sp, lineHeight = 16.sp),
                    color = MutedOnDark
                )
            }
            Spacer(Modifier.height(10.dp))

            Miss.values().toList().chunked(3).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    row.forEach { m ->
                        MissChip(m, m in misses, { onToggleMiss(m) }, Modifier.weight(1f))
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            Spacer(Modifier.height(8.dp))
        }

        if (situation == null) Hint("Choose where you are now to continue.", MutedOnDark)
        PrimaryButton(text = if (editing) "Save" else "Next", onClick = onNext, enabled = situation != null)
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SituationCard(s: Situation, selected: Boolean, onClick: () -> Unit) {
    // Selected: Electric Blue with Midnight content (4.8:1). Never white on blue.
    val ink = if (selected) Midnight else Chalk
    val sub = if (selected) Midnight else MutedOnDark
    Row(
        Modifier
            .fillMaxWidth()
            .heightIn(min = 72.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(if (selected) ElectricBlue else MidnightRaised)
            .clickable(role = Role.RadioButton, onClick = onClick)
            .padding(start = 18.dp, end = 14.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LineIcon(s.icon(), ink, 28.dp)
        Spacer(Modifier.width(14.dp))
        Column {
            Text(s.title, style = MaterialTheme.typography.titleMedium.copy(lineHeight = 20.sp), color = ink)
            Text(s.subtitle, style = TextStyle(fontFamily = Inter, fontSize = 13.sp, lineHeight = 17.sp), color = sub)
        }
    }
}

@Composable
private fun MissChip(m: Miss, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val ink = if (selected) Midnight else Chalk
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier
            .heightIn(min = 68.dp)
            .clip(shape)
            .background(if (selected) CourtOrange else MidnightRaised)
            .border(1.dp, if (selected) CourtOrange else MidnightLine, shape)
            .clickable(role = Role.Checkbox, onClick = onClick)
            .padding(horizontal = 6.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LineIcon(m.icon(), ink, 26.dp)
        Spacer(Modifier.height(4.dp))
        Text(
            m.label,
            style = TextStyle(fontFamily = Inter, fontSize = 12.sp, lineHeight = 14.sp),
            color = ink,
            textAlign = TextAlign.Center
        )
    }
}
