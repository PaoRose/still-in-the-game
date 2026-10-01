package com.paorose.stillinthegame.ui.plus

import android.app.Activity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.paorose.stillinthegame.billing.Plus
import com.paorose.stillinthegame.ui.common.PrimaryButton
import com.paorose.stillinthegame.ui.theme.Chalk
import com.paorose.stillinthegame.ui.theme.CourtOrange
import com.paorose.stillinthegame.ui.theme.ElectricBlue
import com.paorose.stillinthegame.ui.theme.Midnight
import com.paorose.stillinthegame.ui.theme.MidnightRaised
import com.paorose.stillinthegame.ui.theme.MutedOnDark
import com.paorose.stillinthegame.ui.theme.RallyYellow
import com.revenuecat.purchases.Package
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.purchaseWith
import com.revenuecat.purchases.restorePurchasesWith

@Composable
fun PlusScreen(onClose: () -> Unit) {
    val activity = LocalContext.current as? Activity
    val isPlus by Plus.active.collectAsState()
    var pkg by remember { mutableStateOf<Package?>(null) }
    var status by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!Plus.configured) {
            status = "Purchases aren't set up in this build yet."
            return@LaunchedEffect
        }
        Purchases.sharedInstance.getOfferingsWith(
            onError = { status = "Couldn't load the plan. Check your connection." },
            onSuccess = { offerings -> pkg = offerings.current?.availablePackages?.firstOrNull() }
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Midnight)
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            TextButton(onClick = onClose) { Text("Close", style = MaterialTheme.typography.labelLarge, color = Chalk) }
        }
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Text("STILL IN THE GAME+", style = MaterialTheme.typography.labelSmall, color = RallyYellow)
            Spacer(Modifier.height(10.dp))
            Text("Stay closer\nto the game.", style = MaterialTheme.typography.headlineLarge, color = Chalk)
            Spacer(Modifier.height(24.dp))

            // Free is always listed first: nobody pays to stay connected.
            PlanCard(
                title = "Free, always",
                accent = ElectricBlue,
                items = listOf(
                    "One connection activity every day",
                    "Your comeback journey and court",
                    "Activities matched to what you miss"
                )
            )
            Spacer(Modifier.height(12.dp))
            PlanCard(
                title = "Plus",
                accent = CourtOrange,
                items = listOf(
                    "More than one activity a day, whenever you want",
                    "Unlimited \"give me another one\"",
                    "Build your court faster on the days you need it most"
                )
            )
            Spacer(Modifier.height(16.dp))
            status?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MutedOnDark) }
        }

        if (isPlus) {
            PrimaryButton(text = "You're on Plus", onClick = onClose, container = RallyYellow)
        } else {
            val price = pkg?.product?.price?.formatted
            PrimaryButton(
                text = if (price != null) "Get Plus · $price" else "Get Plus",
                enabled = pkg != null && activity != null && !busy,
                onClick = {
                    val p = pkg ?: return@PrimaryButton
                    val a = activity ?: return@PrimaryButton
                    busy = true
                    status = null
                    Purchases.sharedInstance.purchaseWith(
                        PurchaseParams.Builder(a, p).build(),
                        onError = { _, cancelled ->
                            busy = false
                            status = if (cancelled) null else "The purchase didn't go through. Nothing was charged."
                        },
                        onSuccess = { _, info ->
                            busy = false
                            Plus.update(info)
                        }
                    )
                }
            )
            TextButton(
                onClick = {
                    if (!Plus.configured) return@TextButton
                    Purchases.sharedInstance.restorePurchasesWith(
                        onError = { status = "Couldn't restore right now." },
                        onSuccess = { Plus.update(it) }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Restore purchases", style = MaterialTheme.typography.labelLarge, color = Chalk) }
        }
    }
}

@Composable
private fun PlanCard(title: String, accent: Color, items: List<String>) {
    Box(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(MidnightRaised)
            .padding(18.dp)
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.titleLarge, color = Chalk)
            Spacer(Modifier.height(10.dp))
            items.forEach { line ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(accent)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text(line, style = MaterialTheme.typography.bodyMedium, color = Chalk)
                }
            }
        }
    }
}
