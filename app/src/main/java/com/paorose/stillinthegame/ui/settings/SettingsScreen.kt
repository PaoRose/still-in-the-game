package com.paorose.stillinthegame.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.paorose.stillinthegame.BuildConfig
import com.paorose.stillinthegame.billing.Plus
import com.paorose.stillinthegame.data.Profile
import com.paorose.stillinthegame.data.Sport
import com.paorose.stillinthegame.ui.common.LineIcon
import com.paorose.stillinthegame.ui.common.icon
import com.paorose.stillinthegame.ui.theme.Chalk
import com.paorose.stillinthegame.ui.theme.CourtOrange
import com.paorose.stillinthegame.ui.theme.Midnight
import com.paorose.stillinthegame.ui.theme.MutedOnDark
import com.paorose.stillinthegame.ui.theme.RallyYellow
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.restorePurchasesWith

private const val REPO = "https://github.com/PaoRose/still-in-the-game"
private const val PLAY_SUBSCRIPTIONS = "https://play.google.com/store/account/subscriptions"

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
    val code by Plus.redeemedCode.collectAsState()
    val uri = LocalUriHandler.current
    var restoreNote by remember { mutableStateOf<String?>(null) }
    val sport = profile.sport ?: Sport.VOLLEYBALL

    Column(
        Modifier
            .fillMaxSize()
            .background(Midnight)
            .systemBarsPadding()
    ) {
        // The bar stays put; the list scrolls under it.
        SettingsTopBar("Settings", onClose)

        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp)
        ) {
            // Account first, the way Google apps put your profile at the top.
            Spacer(Modifier.height(8.dp))
            Group {
                SettingsRow(
                    title = account ?: "Sign in",
                    supporting = when {
                        account != null && isPlus -> "Plus member"
                        account != null -> "Free plan"
                        else -> "Optional. Keeps Plus with you on a new phone"
                    },
                    icon = {
                        Box(
                            Modifier.size(24.dp).clip(CircleShape).background(if (account != null) CourtOrange else MutedOnDark),
                            contentAlignment = Alignment.Center
                        ) {
                            val initial = account?.firstOrNull()?.uppercase()
                            if (initial != null) Text(initial, style = MaterialTheme.typography.labelSmall, color = Midnight)
                            else RowIcon(Icons.Filled.Person, Midnight)
                        }
                    },
                    onClick = onAccount
                )
            }

            GroupHeader("Subscription")
            Group {
                SettingsRow(
                    title = "Still In The Game+",
                    supporting = when (source) {
                        Plus.Source.PURCHASE -> "Active. Thanks for supporting the app"
                        Plus.Source.ACCOUNT -> "Active on your account"
                        Plus.Source.CODE -> "Active with a promo code"
                        Plus.Source.NONE -> "See what Plus adds"
                    },
                    icon = { RowIcon(Icons.Filled.Star, if (isPlus) RallyYellow else Chalk) },
                    value = if (isPlus) "Active" else "Free",
                    onClick = onPlus
                )
                RowDivider()
                SettingsRow(
                    title = "Redeem code",
                    supporting = code?.let { "$it is active on this phone" } ?: "Have a promo code? Enter it here",
                    icon = { RowIcon(Icons.Filled.AddCircle) },
                    onClick = onAccount
                )
                RowDivider()
                SettingsRow(
                    title = "Restore purchases",
                    supporting = restoreNote ?: "Bought Plus before? Bring it back",
                    icon = { RowIcon(Icons.Filled.Refresh) },
                    onClick = {
                        if (!Plus.configured) {
                            restoreNote = "Purchases aren't set up in this build. Try a promo code"
                            return@SettingsRow
                        }
                        restoreNote = "Checking..."
                        Purchases.sharedInstance.restorePurchasesWith(
                            onError = { restoreNote = "Couldn't restore right now. Try again later" },
                            onSuccess = {
                                Plus.update(it)
                                restoreNote = if (Plus.active.value) "Plus restored" else "No purchase found on this account"
                            }
                        )
                    }
                )
                if (source == Plus.Source.PURCHASE) {
                    RowDivider()
                    SettingsRow(
                        title = "Manage subscription",
                        supporting = "Cancel or change your plan in Google Play",
                        icon = { RowIcon(Icons.Filled.ShoppingCart) },
                        onClick = { runCatching { uri.openUri(PLAY_SUBSCRIPTIONS) } }
                    )
                }
            }

            GroupHeader("Your journey")
            Group {
                SettingsRow(
                    title = "Sport",
                    supporting = "More sports are coming soon",
                    icon = { LineIcon(sport.icon(), Chalk, 24.dp) },
                    value = sport.label
                )
                RowDivider()
                SettingsRow(
                    title = "Your answers",
                    supporting = "Where you are now and what you miss. Your court stays",
                    icon = { RowIcon(Icons.Filled.Edit) },
                    onClick = onEditAnswers
                )
            }

            GroupHeader("About")
            Group {
                SettingsRow(
                    title = "Version",
                    icon = { RowIcon(Icons.Filled.Info) },
                    value = BuildConfig.VERSION_NAME
                )
                RowDivider()
                SettingsRow(
                    title = "Privacy",
                    supporting = "Your answers and your court stay on this phone",
                    icon = { RowIcon(Icons.Filled.Lock) },
                    onClick = { runCatching { uri.openUri("$REPO#privacy") } }
                )
                RowDivider()
                SettingsRow(
                    title = "Source code",
                    supporting = "See how the app is made on GitHub",
                    icon = { RowIcon(Icons.Filled.Build) },
                    onClick = { runCatching { uri.openUri(REPO) } }
                )
            }

            // Destructive action last and in red, with a confirmation dialog.
            Spacer(Modifier.height(24.dp))
            Group {
                SettingsRow(
                    title = "Start over",
                    supporting = "Clears your answers, your days and your court on this phone",
                    icon = { RowIcon(Icons.Filled.Delete, CourtOrange) },
                    titleColor = CourtOrange,
                    onClick = onStartOver
                )
            }

            // Footer note, like the fine print at the end of most settings screens.
            Text(
                "Still In The Game helps you stay connected to your sport. It isn't medical advice. Follow your doctor or physio for your recovery.\n\nMade by Paola Quinteros for the RevenueCat Shipaton 2026.",
                style = MaterialTheme.typography.bodySmall,
                color = MutedOnDark,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 20.dp, bottom = 32.dp)
            )
        }
    }
}
