package com.paorose.stillinthegame.ui.settings

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.paorose.stillinthegame.BuildConfig
import com.paorose.stillinthegame.billing.Plus
import com.paorose.stillinthegame.data.Profile
import com.paorose.stillinthegame.ui.theme.Chalk
import com.paorose.stillinthegame.ui.theme.CourtOrange
import com.paorose.stillinthegame.ui.theme.Midnight
import com.paorose.stillinthegame.ui.theme.MidnightLine
import com.paorose.stillinthegame.ui.theme.MidnightRaised
import com.paorose.stillinthegame.ui.theme.MutedOnDark
import com.paorose.stillinthegame.ui.theme.RallyYellow
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.restorePurchasesWith

@Composable
fun SettingsScreen(
    profile: Profile,
    onClose: () -> Unit,
    onAccount: () -> Unit,
    onPlus: () -> Unit,
    onEditAnswers: () -> Unit,
    onNextDay: () -> Unit,
    onStartOver: () -> Unit
) {
    val isPlus by Plus.active.collectAsState()
    val source by Plus.source.collectAsState()
    val account by Plus.account.collectAsState()
    var note by remember { mutableStateOf<String?>(null) }

    Column(
        Modifier
            .fillMaxSize()
            .background(Midnight)
            .systemBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp)
    ) {
        Row(
            Modifier.fillMaxWidth().height(72.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Settings", style = MaterialTheme.typography.headlineMedium, color = Chalk)
            TextButton(onClick = onClose) { Text("Done", style = MaterialTheme.typography.labelLarge, color = Chalk) }
        }

        Column {
            Section("ACCOUNT") {
                Item(
                    title = if (account != null) "Signed in as $account" else "Sign in with a username",
                    detail = if (account != null) "Your Plus follows you to a new phone." else "No email or phone number needed. Keeps Plus with you on a new phone.",
                    onClick = onAccount
                )
                Divider()
                Item("Redeem a promo code", "Got a code from us? Unlock Plus here.", onClick = onAccount)
            }

            Section("STILL IN THE GAME+") {
                val status = when (source) {
                    Plus.Source.PURCHASE -> "Plus is on. Thanks for supporting the app."
                    Plus.Source.ACCOUNT -> "Plus is on, granted to your account."
                    Plus.Source.CODE -> "Plus is on, unlocked with a promo code."
                    Plus.Source.NONE -> "You're on Free. Everything you need to stay connected is here."
                }
                Item(
                    title = if (isPlus) "Plus" else "Free",
                    detail = status,
                    titleColor = if (isPlus) RallyYellow else Chalk,
                    onClick = onPlus
                )
                Divider()
                Item("Restore purchases", note ?: "Bought Plus before? Bring it back.") {
                    if (!Plus.configured) {
                        note = "Purchases aren't set up in this build. You can use a promo code."
                        return@Item
                    }
                    note = "Checking..."
                    Purchases.sharedInstance.restorePurchasesWith(
                        onError = { note = "Couldn't restore right now. Try again later." },
                        onSuccess = {
                            Plus.update(it)
                            note = if (Plus.active.value) "Plus restored." else "We didn't find a purchase on this account."
                        }
                    )
                }
            }

            Section("YOUR JOURNEY") {
                Item(
                    title = "Sport: ${profile.sport?.label ?: "Volleyball"}",
                    detail = "Basketball, football and running are coming soon.",
                    onClick = null
                )
                Divider()
                Item("Update your answers", "Where you are now and what you miss. Your court stays.", onClick = onEditAnswers)
                Divider()
                Item(
                    title = "Start over",
                    detail = "Clears your answers, your days and your court on this phone.",
                    titleColor = CourtOrange,
                    onClick = onStartOver
                )
            }

            Section("FOR DEMOS AND JUDGES") {
                Item(
                    title = "Jump to tomorrow",
                    detail = "You're on day ${profile.journeyDay}. Moves the app one day ahead so you can do the next activity and watch your court grow without waiting.",
                    onClick = onNextDay
                )
                Divider()
                Item("Promo code for Plus", "SHIPATON2026", onClick = onAccount)
            }

            Section("ABOUT") {
                Item(
                    title = "Not medical advice",
                    detail = "Still In The Game helps you stay connected to your sport. It doesn't give medical or training advice. Follow your doctor or physio for your recovery.",
                    onClick = null
                )
                Divider()
                Item(
                    title = "Your data",
                    detail = "Your answers and your court live only on this phone. RevenueCat handles purchases and Plus.",
                    onClick = null
                )
                Divider()
                Item(
                    title = "Version ${BuildConfig.VERSION_NAME}",
                    detail = "Made by Paola Quinteros for the RevenueCat Shipaton 2026.",
                    onClick = null
                )
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun Section(label: String, content: @Composable () -> Unit) {
    Spacer(Modifier.height(20.dp))
    Text(label, style = MaterialTheme.typography.labelSmall, color = MutedOnDark)
    Spacer(Modifier.height(10.dp))
    val shape = RoundedCornerShape(18.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MidnightRaised)
            .border(1.dp, MidnightLine, shape)
    ) { content() }
}

@Composable
private fun Divider() = HorizontalDivider(color = MidnightLine)

@Composable
private fun Item(
    title: String,
    detail: String?,
    titleColor: Color = Chalk,
    onClick: (() -> Unit)?
) {
    val base = Modifier.fillMaxWidth().heightIn(min = 56.dp)
    Column(
        (if (onClick != null) base.clickable(role = Role.Button, onClick = onClick) else base)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Text(title, style = MaterialTheme.typography.titleMedium, color = titleColor)
        if (detail != null) {
            Spacer(Modifier.height(4.dp))
            Text(detail, style = MaterialTheme.typography.bodyMedium, color = MutedOnDark)
        }
    }
}
