package com.paorose.stillinthegame.ui.onboarding

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paorose.stillinthegame.R
import com.paorose.stillinthegame.ui.common.PrimaryButton
import com.paorose.stillinthegame.ui.theme.Chalk
import com.paorose.stillinthegame.ui.theme.CourtOrange
import com.paorose.stillinthegame.ui.theme.Midnight
import com.paorose.stillinthegame.ui.theme.MutedOnDark

@Composable
fun WelcomeScreen(onStart: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Midnight)
    ) {
        // Painted scene: the ball waiting on the gym floor, warm light, an athlete on the sideline.
        Image(
            painter = painterResource(R.drawable.welcome_hero),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomCenter,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            Modifier
                .fillMaxSize()
                .systemBarsPadding()
                .padding(horizontal = 24.dp)
        ) {
            Spacer(Modifier.height(56.dp))
            Text(
                text = buildAnnotatedString {
                    append("STILL\nIN THE\n")
                    withStyle(SpanStyle(color = CourtOrange)) { append("GAME.") }
                },
                style = MaterialTheme.typography.displayLarge.copy(letterSpacing = (-2).sp),
                color = Chalk
            )
            Spacer(Modifier.height(20.dp))
            Text(
                text = "DIFFERENT ROUTE, SAME YOU.",
                style = MaterialTheme.typography.labelSmall,
                color = MutedOnDark
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = "For athletes who can't play right now.",
                style = MaterialTheme.typography.bodyLarge,
                color = Chalk
            )
            Spacer(Modifier.height(16.dp))
            PrimaryButton(text = "Get started", onClick = onStart)
            Spacer(Modifier.height(32.dp))
        }
    }
}
