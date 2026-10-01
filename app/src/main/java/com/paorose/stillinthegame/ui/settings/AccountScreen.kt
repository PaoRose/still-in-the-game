package com.paorose.stillinthegame.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import com.paorose.stillinthegame.billing.Plus
import com.paorose.stillinthegame.ui.common.PrimaryButton
import com.paorose.stillinthegame.ui.theme.Chalk
import com.paorose.stillinthegame.ui.theme.CourtOrange
import com.paorose.stillinthegame.ui.theme.Midnight
import com.paorose.stillinthegame.ui.theme.MidnightLine
import com.paorose.stillinthegame.ui.theme.MidnightRaised
import com.paorose.stillinthegame.ui.theme.MutedOnDark
import com.paorose.stillinthegame.ui.theme.RallyYellow

/** Sign in (optional) and redeem a promo code. Judges use the code to reach Plus. */
@Composable
fun AccountScreen(onClose: () -> Unit) {
    val focus = LocalFocusManager.current
    val account by Plus.account.collectAsState()
    val source by Plus.source.collectAsState()
    val redeemed by Plus.redeemedCode.collectAsState()

    var code by remember { mutableStateOf("") }
    var codeMsg by remember { mutableStateOf<String?>(null) }
    var codeOk by remember { mutableStateOf(false) }

    var name by remember { mutableStateOf("") }
    var nameMsg by remember { mutableStateOf<String?>(null) }
    var busy by remember { mutableStateOf(false) }

    fun redeem() {
        focus.clearFocus()
        Plus.redeem(code) { r ->
            codeOk = r is Plus.Result.Ok
            codeMsg = when (r) {
                is Plus.Result.Ok -> "Plus is on. Enjoy!"
                is Plus.Result.Error -> r.message
            }
        }
    }

    fun signIn() {
        focus.clearFocus()
        busy = true
        nameMsg = null
        Plus.signIn(name) { r ->
            busy = false
            nameMsg = (r as? Plus.Result.Error)?.message
            if (r is Plus.Result.Ok) name = ""
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(Midnight)
            .systemBarsPadding()
            .imePadding()
    ) {
        SettingsTopBar("Account", onClose)

        Column(Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp)) {
            // Promo code first: it's the quickest way into Plus.
            Spacer(Modifier.height(16.dp))
            Card {
            Text("Redeem code", style = MaterialTheme.typography.titleMedium, color = Chalk)
            Spacer(Modifier.height(8.dp))
            if (redeemed != null) {
                Text("Code $redeemed is active on this phone.", style = MaterialTheme.typography.bodyLarge, color = Chalk)
                TextButton(onClick = { Plus.removeCode(); codeMsg = null; codeOk = false }) {
                    Text("Remove code", style = MaterialTheme.typography.labelLarge, color = MutedOnDark)
                }
            } else {
                Text(
                    "Enter a promo code to unlock Plus. Judges: use the code in the testing notes.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedOnDark
                )
                Spacer(Modifier.height(12.dp))
                Field(
                    value = code,
                    onChange = { code = it; codeMsg = null },
                    label = "Code",
                    caps = KeyboardCapitalization.Characters,
                    onGo = { redeem() }
                )
                codeMsg?.let {
                    Spacer(Modifier.height(8.dp))
                    Text(it, style = MaterialTheme.typography.bodyMedium, color = if (codeOk) RallyYellow else CourtOrange)
                }
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "Redeem", arrow = false, onClick = { redeem() }, enabled = code.isNotBlank())
            }

            }
            Spacer(Modifier.height(20.dp))
            Card {
            Text("Sign in", style = MaterialTheme.typography.titleMedium, color = Chalk)
            Spacer(Modifier.height(8.dp))
            if (account != null) {
                Text("Signed in as $account", style = MaterialTheme.typography.bodyLarge, color = Chalk)
                if (source == Plus.Source.ACCOUNT) {
                    Text("Plus is granted to this account.", style = MaterialTheme.typography.bodyMedium, color = RallyYellow)
                }
                TextButton(onClick = {
                    busy = true
                    Plus.signOut { r -> busy = false; nameMsg = (r as? Plus.Result.Error)?.message }
                }, enabled = !busy) {
                    Text("Sign out", style = MaterialTheme.typography.labelLarge, color = MutedOnDark)
                }
            } else {
                Text(
                    "Optional. No email, phone or password. Pick a username and Plus comes with you to a new phone. Your court stays on this phone.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MutedOnDark
                )
                Spacer(Modifier.height(12.dp))
                Field(
                    value = name,
                    onChange = { name = it; nameMsg = null },
                    label = "Username",
                    caps = KeyboardCapitalization.None,
                    onGo = { signIn() }
                )
                Spacer(Modifier.height(12.dp))
                PrimaryButton(
                    text = if (busy) "Signing in..." else "Sign in",
                    arrow = false,
                    onClick = { signIn() },
                    enabled = name.isNotBlank() && !busy,
                    container = Chalk
                )
            }
            nameMsg?.let {
                Spacer(Modifier.height(8.dp))
                Text(it, style = MaterialTheme.typography.bodyMedium, color = CourtOrange)
            }
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
private fun Card(content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(MidnightRaised)
            .border(1.dp, MidnightLine, shape)
            .padding(16.dp)
    ) { content() }
}

@Composable
private fun Field(
    value: String,
    onChange: (String) -> Unit,
    label: String,
    caps: KeyboardCapitalization,
    onGo: () -> Unit
) {
    OutlinedTextField(
        value = value,
        onValueChange = { onChange(it.take(32)) },
        label = { Text(label) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        keyboardOptions = KeyboardOptions(capitalization = caps, autoCorrect = false, imeAction = ImeAction.Go),
        keyboardActions = KeyboardActions(onGo = { onGo() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Chalk,
            unfocusedTextColor = Chalk,
            focusedBorderColor = CourtOrange,
            unfocusedBorderColor = MidnightLine,
            focusedLabelColor = CourtOrange,
            unfocusedLabelColor = MutedOnDark,
            cursorColor = CourtOrange,
            focusedContainerColor = MidnightRaised,
            unfocusedContainerColor = MidnightRaised
        ),
        modifier = Modifier.fillMaxWidth()
    )
}
