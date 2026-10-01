package com.paorose.stillinthegame.ui.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.paorose.stillinthegame.ui.common.PrimaryButton
import com.paorose.stillinthegame.ui.theme.Chalk
import com.paorose.stillinthegame.ui.theme.CourtOrange
import com.paorose.stillinthegame.ui.theme.Midnight
import com.paorose.stillinthegame.ui.theme.MidnightLine
import com.paorose.stillinthegame.ui.theme.MutedOnDark

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Midnight)
    ) {
        // Faint court lines in the background: the place you're going back to.
        Canvas(Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height
            val top = h * 0.58f
            val stroke = 2.dp.toPx()
            drawLine(MidnightLine, Offset(w * 0.08f, top), Offset(w * 0.92f, top), stroke)
            drawLine(MidnightLine, Offset(w * 0.08f, top), Offset(-w * 0.1f, h), stroke)
            drawLine(MidnightLine, Offset(w * 0.92f, top), Offset(w * 1.1f, h), stroke)
            drawLine(MidnightLine, Offset(w * 0.02f, top + (h - top) * 0.45f), Offset(w * 0.98f, top + (h - top) * 0.45f), stroke)
            drawCircle(CourtOrange, radius = 18.dp.toPx(), center = Offset(w * 0.24f, top + (h - top) * 0.62f))
        }

        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 28.dp, vertical = 24.dp)
        ) {
            Spacer(Modifier.height(48.dp))
            Text(
                text = buildAnnotatedString {
                    append("STILL\nIN THE\n")
                    withStyle(SpanStyle(color = CourtOrange)) { append("GAME.") }
                },
                style = MaterialTheme.typography.displayLarge,
                color = Chalk
            )
            Spacer(Modifier.height(28.dp))
            Text(
                text = "DIFFERENT ROUTE,\nSAME YOU.",
                style = MaterialTheme.typography.labelSmall,
                color = MutedOnDark
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "For athletes who can't play right now.",
                style = MaterialTheme.typography.bodyMedium,
                color = Chalk,
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            )
            PrimaryButton(text = "Get started", onClick = onStart)
        }
    }
}
