package com.paorose.stillinthegame.ui.today

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.paorose.stillinthegame.data.Activity
import com.paorose.stillinthegame.data.Sport
import com.paorose.stillinthegame.ui.common.PrimaryButton
import com.paorose.stillinthegame.ui.theme.Chalk
import com.paorose.stillinthegame.ui.theme.ChalkLine
import com.paorose.stillinthegame.ui.theme.ChalkRaised
import com.paorose.stillinthegame.ui.theme.Midnight
import com.paorose.stillinthegame.ui.theme.MutedOnLight

@Composable
fun TodayScreen(
    sport: Sport,
    activity: Activity,
    doneToday: Boolean,
    onDone: () -> Unit,
    onAnother: () -> Unit,
    onCourt: () -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .background(Chalk)
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(sport.label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MutedOnLight)
            TextButton(onClick = onCourt) {
                Text("Your ${sport.field}", style = MaterialTheme.typography.labelLarge, color = Midnight)
            }
        }

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(20.dp))
            if (doneToday) {
                Text("You showed up today.", style = MaterialTheme.typography.headlineLarge, color = Midnight)
                Spacer(Modifier.height(12.dp))
                Text(
                    "That counts. Your ${sport.field} has a new piece. Come back tomorrow for the next one.",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MutedOnLight
                )
            } else {
                // Midnight label on Chalk: orange text on Chalk fails contrast.
                Text(
                    "TODAY'S CONNECTION · ${activity.minutes} MIN",
                    style = MaterialTheme.typography.labelSmall,
                    color = Midnight
                )
                Spacer(Modifier.height(14.dp))
                Text(activity.titleFor(sport), style = MaterialTheme.typography.headlineMedium, color = Midnight)
                Spacer(Modifier.height(24.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .background(ChalkRaised)
                        .padding(18.dp)
                ) {
                    Column {
                        Text(activity.kind.label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MutedOnLight)
                        Spacer(Modifier.height(6.dp))
                        Text(activity.whyFor(sport), style = MaterialTheme.typography.bodyLarge, color = Midnight)
                    }
                }
                Spacer(Modifier.height(10.dp))
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(ChalkLine)
                )
            }
        }

        if (doneToday) {
            PrimaryButton(text = "See your ${sport.field}", onClick = onCourt, container = Midnight, content = Chalk)
        } else {
            PrimaryButton(text = "I did it", onClick = onDone, container = Midnight, content = Chalk)
            TextButton(
                onClick = onAnother,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
            ) {
                Text("Give me another one", style = MaterialTheme.typography.labelLarge, color = Midnight)
            }
        }
    }
}
