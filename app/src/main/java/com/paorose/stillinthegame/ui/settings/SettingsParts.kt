package com.paorose.stillinthegame.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.paorose.stillinthegame.ui.theme.Chalk
import com.paorose.stillinthegame.ui.theme.CourtOrange
import com.paorose.stillinthegame.ui.theme.Inter
import com.paorose.stillinthegame.ui.theme.MidnightLine
import com.paorose.stillinthegame.ui.theme.MidnightRaised
import com.paorose.stillinthegame.ui.theme.MutedOnDark

/*
 * Settings follow the Android pattern people already know (Jakob's law):
 * back arrow and title at the top left, grouped lists with sentence case headers,
 * icon on the left, value or chevron on the right, destructive actions in red at the end.
 */

/** Android top app bar: back arrow, then the title. */
@Composable
internal fun SettingsTopBar(title: String, onBack: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().height(64.dp).padding(start = 4.dp, end = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Chalk)
        }
        Spacer(Modifier.width(4.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, color = Chalk)
    }
}

/** Group header in sentence case, accent colored, like Android Settings. */
@Composable
internal fun GroupHeader(text: String) {
    Text(
        text,
        fontFamily = Inter,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        color = CourtOrange,
        modifier = Modifier.padding(start = 16.dp, top = 24.dp, bottom = 8.dp)
    )
}

/** A rounded group of rows. */
@Composable
internal fun Group(content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(20.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MidnightRaised)
            .border(1.dp, MidnightLine, shape)
    ) { content() }
}

@Composable
internal fun RowDivider() = HorizontalDivider(color = MidnightLine, modifier = Modifier.padding(start = 56.dp))

/**
 * One settings row. A chevron shows only when the row opens something,
 * so rows that only show information don't look tappable.
 */
@Composable
internal fun SettingsRow(
    title: String,
    supporting: String? = null,
    icon: (@Composable () -> Unit)? = null,
    value: String? = null,
    titleColor: Color = Chalk,
    onClick: (() -> Unit)? = null
) {
    val base = Modifier.fillMaxWidth().heightIn(min = 56.dp)
    Row(
        (if (onClick != null) base.clickable(role = Role.Button, onClick = onClick) else base)
            .padding(start = 16.dp, end = 12.dp, top = 12.dp, bottom = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Box(Modifier.size(24.dp), contentAlignment = Alignment.Center) { icon() }
            Spacer(Modifier.width(16.dp))
        }
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = titleColor)
            if (supporting != null) {
                Spacer(Modifier.height(2.dp))
                Text(supporting, style = MaterialTheme.typography.bodyMedium, color = MutedOnDark)
            }
        }
        if (value != null) {
            Spacer(Modifier.width(12.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium, color = MutedOnDark)
        }
        if (onClick != null) {
            Spacer(Modifier.width(4.dp))
            Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null, tint = MutedOnDark)
        }
    }
}

@Composable
internal fun RowIcon(vector: androidx.compose.ui.graphics.vector.ImageVector, tint: Color = Chalk) =
    Icon(vector, contentDescription = null, tint = tint, modifier = Modifier.size(24.dp))
