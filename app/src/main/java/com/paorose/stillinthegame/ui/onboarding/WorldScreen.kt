package com.paorose.stillinthegame.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.paorose.stillinthegame.data.Sport
import com.paorose.stillinthegame.ui.common.Hint
import com.paorose.stillinthegame.ui.common.LineIcon
import com.paorose.stillinthegame.ui.common.PrimaryButton
import com.paorose.stillinthegame.ui.common.StepHeader
import com.paorose.stillinthegame.ui.common.icon
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
            .padding(horizontal = 24.dp)
    ) {
        Spacer(Modifier.height(32.dp))
        StepHeader(step = 1, total = 4, color = Midnight, track = ChalkLine)
        Spacer(Modifier.height(28.dp))
        Text("What's your world?", style = MaterialTheme.typography.headlineLarge, color = Midnight)
        Spacer(Modifier.height(8.dp))
        Text(
            "Volleyball is open now. More sports are coming soon.",
            style = MaterialTheme.typography.bodyMedium,
            color = MutedOnLight
        )
        Spacer(Modifier.height(32.dp))

        Sport.values().toList().chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEach { sport ->
                    SportCard(sport, sport == selected, { onSelect(sport) }, Modifier.weight(1f))
                }
            }
            Spacer(Modifier.height(12.dp))
        }

        Spacer(Modifier.weight(1f))
        if (selected == null) Hint("Pick one to continue.", MutedOnLight)
        PrimaryButton(
            text = "Next",
            onClick = onNext,
            enabled = selected != null,
            container = Midnight,
            content = Chalk,
            disabledContainer = ChalkLine,
            disabledContent = MutedOnLight
        )
        Spacer(Modifier.height(32.dp))
    }
}

@Composable
private fun SportCard(sport: Sport, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val shape = RoundedCornerShape(20.dp)
    // Only volleyball has its illustrated court so far.
    val open = sport == Sport.VOLLEYBALL
    Column(
        modifier = modifier
            .height(160.dp)
            .clip(shape)
            .background(if (selected) CourtOrange else ChalkRaised)
            .border(1.dp, if (selected) CourtOrange else ChalkLine, shape)
            .alpha(if (open) 1f else 0.55f)
            .clickable(enabled = open, role = Role.RadioButton, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        LineIcon(sport.icon(), Midnight, 40.dp)
        Spacer(Modifier.height(12.dp))
        Text(sport.label, style = MaterialTheme.typography.labelLarge, color = Midnight)
        if (!open) {
            Spacer(Modifier.height(4.dp))
            Text("Coming soon", style = MaterialTheme.typography.bodySmall, color = MutedOnLight)
        }
    }
}
