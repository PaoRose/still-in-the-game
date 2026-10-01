package com.paorose.stillinthegame.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.paorose.stillinthegame.ui.theme.CourtOrange
import com.paorose.stillinthegame.ui.theme.Midnight
import com.paorose.stillinthegame.ui.theme.MidnightRaised
import com.paorose.stillinthegame.ui.theme.MutedOnDark

/** Primary action: 56 tall, radius 16, full width. Text on orange or blue is always Midnight. */
@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    container: Color = CourtOrange,
    content: Color = Midnight,
    // Disabled = solid and readable, never a faded version of the active color.
    disabledContainer: Color = MidnightRaised,
    disabledContent: Color = MutedOnDark
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(16.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = container,
            contentColor = content,
            disabledContainerColor = disabledContainer,
            disabledContentColor = disabledContent
        )
    ) {
        Text(text = "$text  →", style = MaterialTheme.typography.labelLarge)
    }
}

/** Centered hint shown 12 above a disabled button. */
@Composable
fun Hint(text: String, color: Color) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = color,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
    )
}

/** "01 / 04", 16 gap, 120 x 4 progress track. */
@Composable
fun StepHeader(step: Int, total: Int, color: Color, track: Color, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "%02d / %02d".format(step, total),
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
        Spacer(Modifier.width(16.dp))
        Box(
            Modifier
                .width(120.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(track)
        ) {
            Box(
                Modifier
                    .fillMaxWidth(step / total.toFloat())
                    .height(4.dp)
                    .background(color)
            )
        }
    }
}

/** Minimum 48 tall text action. */
@Composable
fun TextAction(text: String, color: Color, onClick: () -> Unit, modifier: Modifier = Modifier) {
    androidx.compose.material3.TextButton(onClick = onClick, modifier = modifier.heightIn(min = 48.dp)) {
        Text(text, style = MaterialTheme.typography.labelLarge, color = color)
    }
}
