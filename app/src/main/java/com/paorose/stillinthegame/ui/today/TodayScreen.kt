package com.paorose.stillinthegame.ui.today

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.paorose.stillinthegame.R
import com.paorose.stillinthegame.data.Kind
import com.paorose.stillinthegame.ui.theme.Caveat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.paorose.stillinthegame.data.Activity
import com.paorose.stillinthegame.data.Sport
import com.paorose.stillinthegame.ui.common.PrimaryButton
import com.paorose.stillinthegame.ui.common.TextAction
import com.paorose.stillinthegame.ui.theme.Chalk
import com.paorose.stillinthegame.ui.theme.ChalkLine
import com.paorose.stillinthegame.ui.theme.ChalkRaised
import com.paorose.stillinthegame.ui.theme.CourtOrange
import com.paorose.stillinthegame.ui.theme.Midnight
import com.paorose.stillinthegame.ui.theme.MutedOnLight

@Composable
fun TodayScreen(
    sport: Sport,
    day: Int,
    onNextDay: () -> Unit,
    onPlus: () -> Unit,
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
    ) {
        // Top row, 72 tall: sport on the left, "Your court" on the right.
        Row(
            Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(start = 24.dp, end = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("DAY %02d · ${sport.label.uppercase()}".format(day), style = MaterialTheme.typography.labelSmall, color = Midnight)
            TextAction("Your ${sport.field}", Midnight, onCourt)
        }

        Column(
            Modifier
                .weight(1f)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (doneToday) DoneState(sport) else ActivityState(sport, activity)
        }

        Column(Modifier.padding(horizontal = 24.dp)) {
            if (doneToday) {
                PrimaryButton(text = "See your ${sport.field}", onClick = onCourt, container = Midnight, content = Chalk)
                Spacer(Modifier.height(4.dp))
                // For demos and judges: real users just come back tomorrow.
                TextAction("Want another one today? Get Plus", Midnight, onPlus, Modifier.fillMaxWidth())
                TextAction("Demo: jump to tomorrow", MutedOnLight, onNextDay, Modifier.fillMaxWidth())
            } else {
                PrimaryButton(text = "I did it", onClick = onDone, container = Midnight, content = Chalk)
                Spacer(Modifier.height(4.dp))
                TextAction("Give me another one", Midnight, onAnother, Modifier.fillMaxWidth())
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}

/** A handwritten note that sits on the illustration, like a coach's marker on a whiteboard. */
private fun noteFor(kind: Kind): String = when (kind) {
    Kind.WATCH -> "eyes on the game"
    Kind.LEARN -> "still learning"
    Kind.CONNECT -> "SAME TEAM"
    Kind.REFLECT -> "still yours"
}

@Composable
private fun ActivityState(sport: Sport, activity: Activity) {
    Spacer(Modifier.height(8.dp))
    Text(
        "TODAY'S CONNECTION · ${activity.minutes} MIN",
        style = MaterialTheme.typography.labelSmall,
        color = Midnight
    )
    Spacer(Modifier.height(12.dp))
    Text(activity.titleFor(sport), style = MaterialTheme.typography.headlineMedium, color = Midnight)
    Spacer(Modifier.height(8.dp))

    // Illustration with a handwritten note, no card around it.
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(1000f / 640f)
    ) {
        Image(
            painterResource(R.drawable.today_art),
            contentDescription = null,
            modifier = Modifier.fillMaxSize()
        )
        Text(
            noteFor(activity.kind),
            fontFamily = Caveat,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = CourtOrange,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(start = 8.dp, top = 40.dp)
                .rotate(-6f)
        )
    }
    Spacer(Modifier.height(8.dp))
    Text(activity.kind.label.uppercase(), style = MaterialTheme.typography.labelSmall, color = MutedOnLight)
    Spacer(Modifier.height(8.dp))
    Text(activity.whyFor(sport), style = MaterialTheme.typography.bodyLarge, color = Midnight)
    // Hand-drawn underline in orange.
    Canvas(
        Modifier
            .padding(top = 10.dp)
            .fillMaxWidth(0.4f)
            .height(10.dp)
    ) {
        val p = Path().apply {
            moveTo(0f, size.height * 0.7f)
            cubicTo(size.width * 0.3f, size.height * 0.2f, size.width * 0.6f, size.height * 0.9f, size.width, size.height * 0.3f)
        }
        drawPath(p, CourtOrange, style = Stroke(width = 4.dp.toPx(), cap = StrokeCap.Round))
    }
    Spacer(Modifier.height(16.dp))
}

@Composable
private fun DoneState(sport: Sport) {
    Spacer(Modifier.height(8.dp))
    Box(
        Modifier
            .fillMaxWidth()
            .aspectRatio(800f / 560f)
    ) {
        Image(painterResource(R.drawable.done_art), contentDescription = null, modifier = Modifier.fillMaxSize())
        Text(
            "you showed up!",
            fontFamily = Caveat,
            fontWeight = FontWeight.Bold,
            fontSize = 30.sp,
            color = Midnight,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 4.dp, bottom = 2.dp)
                .rotate(-4f)
        )
    }
    Spacer(Modifier.height(16.dp))
    Text("CONNECTION COMPLETE", style = MaterialTheme.typography.labelSmall, color = MutedOnLight)
    Spacer(Modifier.height(12.dp))
    Text("You showed up today.", style = MaterialTheme.typography.headlineLarge, color = Midnight)
    Spacer(Modifier.height(16.dp))
    Text(
        "That counts. Your ${sport.field} has a new piece. Come back tomorrow for the next one.",
        style = MaterialTheme.typography.bodyLarge,
        color = MutedOnLight
    )
}
