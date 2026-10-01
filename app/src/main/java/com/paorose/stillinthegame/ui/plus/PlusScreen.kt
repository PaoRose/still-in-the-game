package com.paorose.stillinthegame.ui.plus

import android.app.Activity
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Close
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
import com.paorose.stillinthegame.ui.theme.MidnightLine
import com.paorose.stillinthegame.ui.theme.MidnightRaised
import com.paorose.stillinthegame.ui.theme.MutedOnDark
import com.paorose.stillinthegame.ui.theme.RallyYellow
import com.revenuecat.purchases.Package
import androidx.compose.foundation.clickable
import com.revenuecat.purchases.PackageType
import com.revenuecat.purchases.PurchaseParams
import com.revenuecat.purchases.Purchases
import com.revenuecat.purchases.getOfferingsWith
import com.revenuecat.purchases.purchaseWith
import com.revenuecat.purchases.restorePurchasesWith

@Composable
fun PlusScreen(onClose: () -> Unit, onPromo: () -> Unit) {
    val activity = LocalContext.current as? Activity
    val isPlus by Plus.active.collectAsState()
    var pkg by remember { mutableStateOf<Package?>(null) }
    var packages by remember { mutableStateOf<List<Package>>(emptyList()) }
    var status by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        if (!Plus.configured) {
            status = "Purchases aren't set up in this build. You can still unlock Plus with a promo code."
            return@LaunchedEffect
        }
        Purchases.sharedInstance.getOfferingsWith(
            onError = { status = "Couldn't load the plan. Check your connection." },
            onSuccess = { offerings ->
                // Yearly first and selected by default, then monthly.
                val order = listOf(PackageType.ANNUAL, PackageType.MONTHLY, PackageType.WEEKLY)
                packages = offerings.current?.availablePackages.orEmpty()
                    .sortedBy { order.indexOf(it.packageType).let { i -> if (i < 0) 99 else i } }
                pkg = packages.firstOrNull()
                if (pkg == null) status = "Plus isn't on sale right now. You can still unlock it with a promo code."
            }
        )
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Midnight)
            .systemBarsPadding()
            .padding(horizontal = 24.dp)
    ) {
        Row(Modifier.fillMaxWidth().height(72.dp), horizontalArrangement = Arrangement.End, verticalAlignment = Alignment.CenterVertically) {
            // Standard close icon for a full screen paywall.
            androidx.compose.material3.IconButton(onClick = onClose) {
                androidx.compose.material3.Icon(
                    androidx.compose.material.icons.Icons.Filled.Close,
                    contentDescription = "Close",
                    tint = Chalk
                )
            }
        }
        Column(
            Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(Modifier.height(24.dp))
            Text("STILL IN THE GAME+", style = MaterialTheme.typography.labelSmall, color = RallyYellow)
            Spacer(Modifier.height(12.dp))
            Text("Stay closer to the game.", style = MaterialTheme.typography.headlineLarge, color = Chalk)
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
                    "Build your court faster on the days you need it most",
                    "Plus follows you to a new phone when you sign in"
                )
            )
            if (!isPlus && packages.size > 1) {
                Spacer(Modifier.height(16.dp))
                Text("CHOOSE YOUR PLAN", style = MaterialTheme.typography.labelSmall, color = MutedOnDark)
                Spacer(Modifier.height(10.dp))
                val monthly = packages.firstOrNull { it.packageType == PackageType.MONTHLY }
                packages.forEach { option ->
                    val save = if (option.packageType == PackageType.ANNUAL && monthly != null) {
                        val year = option.product.price.amountMicros.toDouble()
                        val twelve = monthly.product.price.amountMicros.toDouble() * 12
                        if (twelve > 0 && year < twelve) ((1 - year / twelve) * 100).toInt() else 0
                    } else 0
                    PlanOption(
                        title = planTitle(option.packageType),
                        price = option.product.price.formatted + periodOf(option.packageType),
                        badge = if (save > 0) "Save $save%" else null,
                        selected = option == pkg,
                        onClick = { pkg = option }
                    )
                    Spacer(Modifier.height(8.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            status?.let { Text(it, style = MaterialTheme.typography.bodyMedium, color = MutedOnDark) }
        }

        if (isPlus) {
            PrimaryButton(text = "You're on Plus", onClick = onClose, container = RallyYellow, arrow = false)
            Spacer(Modifier.height(24.dp))
        } else {
            // "$9.99/month", the way stores show subscription prices.
            val price = pkg?.let { it.product.price.formatted + periodOf(it.packageType) }
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
            // Builds on RevenueCat's Test Store simulate purchases; say so, so nobody worries.
            if (com.paorose.stillinthegame.BuildConfig.REVENUECAT_API_KEY.startsWith("test_")) {
                Text(
                    "Test mode: no real money is charged.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MutedOnDark,
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
            TextButton(
                onClick = {
                    if (!Plus.configured) {
                        status = "Purchases aren't set up in this build. Try a promo code."
                        return@TextButton
                    }
                    Purchases.sharedInstance.restorePurchasesWith(
                        onError = { status = "Couldn't restore right now." },
                        onSuccess = {
                            Plus.update(it)
                            if (!Plus.active.value) status = "We didn't find a purchase to restore."
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Restore purchases", style = MaterialTheme.typography.labelLarge, color = Chalk) }
            TextButton(onClick = onPromo, modifier = Modifier.fillMaxWidth()) {
                Text("Have a promo code?", style = MaterialTheme.typography.labelLarge, color = RallyYellow)
            }
            Spacer(Modifier.height(8.dp))
        }
    }
}

private fun periodOf(type: PackageType): String = when (type) {
    PackageType.MONTHLY -> "/month"
    PackageType.ANNUAL -> "/year"
    PackageType.WEEKLY -> "/week"
    else -> ""
}

private fun planTitle(type: PackageType): String = when (type) {
    PackageType.MONTHLY -> "Monthly"
    PackageType.ANNUAL -> "Yearly"
    PackageType.WEEKLY -> "Weekly"
    PackageType.LIFETIME -> "Lifetime"
    else -> "Plus"
}

/** One selectable plan, like the plan pickers in most subscription apps. */
@Composable
private fun PlanOption(title: String, price: String, badge: String?, selected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Row(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MidnightRaised)
            .border(if (selected) 2.dp else 1.dp, if (selected) CourtOrange else MidnightLine, shape)
            .clickable(role = androidx.compose.ui.semantics.Role.RadioButton, onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        androidx.compose.material3.RadioButton(
            selected = selected,
            onClick = onClick,
            colors = androidx.compose.material3.RadioButtonDefaults.colors(
                selectedColor = CourtOrange,
                unselectedColor = MutedOnDark
            )
        )
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = Chalk)
            Text(price, style = MaterialTheme.typography.bodyMedium, color = MutedOnDark)
        }
        if (badge != null) {
            Text(
                badge,
                style = MaterialTheme.typography.labelSmall,
                color = Midnight,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(RallyYellow)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }
    }
}

@Composable
private fun PlanCard(title: String, accent: Color, items: List<String>) {
    val shape = RoundedCornerShape(20.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MidnightRaised)
            .border(1.dp, MidnightLine, shape)
            .padding(18.dp)
    ) {
        Column {
            Text(title, style = MaterialTheme.typography.titleLarge, color = Chalk)
            Spacer(Modifier.height(10.dp))
            items.forEach { line ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 6.5.dp)) {
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
