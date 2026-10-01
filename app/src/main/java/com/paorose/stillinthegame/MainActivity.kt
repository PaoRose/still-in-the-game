package com.paorose.stillinthegame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.paorose.stillinthegame.billing.Plus
import com.paorose.stillinthegame.data.ActivityLibrary
import com.paorose.stillinthegame.data.Miss
import com.paorose.stillinthegame.data.Profile
import com.paorose.stillinthegame.data.Situation
import com.paorose.stillinthegame.data.Sport
import com.paorose.stillinthegame.data.Store
import com.paorose.stillinthegame.ui.court.CourtScreen
import com.paorose.stillinthegame.ui.onboarding.SituationScreen
import com.paorose.stillinthegame.ui.onboarding.WelcomeScreen
import com.paorose.stillinthegame.ui.onboarding.WorldScreen
import com.paorose.stillinthegame.ui.plus.PlusScreen
import com.paorose.stillinthegame.ui.settings.AccountScreen
import com.paorose.stillinthegame.ui.settings.SettingsScreen
import com.paorose.stillinthegame.ui.theme.Midnight
import com.paorose.stillinthegame.ui.theme.StillTheme
import com.paorose.stillinthegame.ui.today.TodayScreen
import kotlinx.coroutines.launch

enum class Screen { WELCOME, WORLD, SITUATION, TODAY, COURT, PLUS, SETTINGS, ACCOUNT }

/** Free users get this many "give me another one" per day. */
private const val FREE_SKIPS = 2

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val store = Store(applicationContext)
        setContent {
            StillTheme {
                val profile by store.profile.collectAsState(initial = null)
                Box(Modifier.fillMaxSize().background(Midnight)) {
                    profile?.let { AppRoot(store, it) }
                }
            }
        }
    }
}

@Composable
private fun AppRoot(store: Store, profile: Profile) {
    val scope = rememberCoroutineScope()

    var screen by remember { mutableStateOf(if (profile.onboarded) Screen.TODAY else Screen.WELCOME) }
    var animateNewest by remember { mutableStateOf(false) }
    var askReset by remember { mutableStateOf(false) }
    val isPlus by Plus.active.collectAsState()
    var backFromPlus by remember { mutableStateOf(Screen.TODAY) }
    var backFromSettings by remember { mutableStateOf(Screen.COURT) }
    var backFromAccount by remember { mutableStateOf(Screen.SETTINGS) }

    // Onboarding draft, saved only when the user finishes "Where are you now?".
    var sport by remember { mutableStateOf<Sport?>(profile.sport ?: Sport.VOLLEYBALL) }
    var situation by remember { mutableStateOf(profile.situation) }
    var misses by remember { mutableStateOf(profile.misses) }

    BackHandler(enabled = screen != Screen.WELCOME && screen != Screen.TODAY) {
        screen = when (screen) {
            Screen.WORLD -> Screen.WELCOME
            Screen.SITUATION -> if (profile.onboarded) Screen.SETTINGS else Screen.WORLD
            Screen.PLUS -> backFromPlus
            Screen.SETTINGS -> backFromSettings
            Screen.ACCOUNT -> backFromAccount
            else -> Screen.TODAY
        }
    }

    // Dark status bar icons on the light screens, light icons on the dark ones.
    val view = LocalView.current
    val lightScreen = screen == Screen.WORLD || screen == Screen.TODAY
    SideEffect {
        val window = (view.context as? android.app.Activity)?.window ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = lightScreen
        WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = lightScreen
    }

    AnimatedContent(
        targetState = screen,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "screens"
    ) { current ->
        when (current) {
            Screen.WELCOME -> WelcomeScreen(onStart = { screen = Screen.WORLD })

            Screen.WORLD -> WorldScreen(
                selected = sport,
                onSelect = { sport = it },
                onNext = { screen = Screen.SITUATION }
            )

            Screen.SITUATION -> SituationScreen(
                editing = profile.onboarded,
                situation = situation,
                misses = misses,
                onSituation = { situation = it },
                onToggleMiss = { m: Miss -> misses = if (m in misses) misses - m else misses + m },
                onNext = {
                    val sp = sport
                    val si = situation
                    if (sp != null && si != null) {
                        scope.launch { store.saveSetup(sp, si, misses) }
                        screen = Screen.TODAY
                    }
                }
            )

            Screen.TODAY -> {
                val sp = profile.sport ?: sport ?: Sport.VOLLEYBALL
                val si = profile.situation ?: situation ?: Situation.CANT
                val ms = if (profile.onboarded) profile.misses else misses
                val activity = ActivityLibrary.pick(sp, si, ms, profile.doneIds, profile.skips, profile.today)
                TodayScreen(
                    sport = sp,
                    day = profile.journeyDay,
                    onNextDay = { scope.launch { store.nextDay() } },
                    onPlus = {
                        backFromPlus = Screen.TODAY
                        screen = Screen.PLUS
                    },
                    activity = activity,
                    // Plus can do more than one activity a day.
                    doneToday = profile.doneToday && !isPlus,
                    onDone = {
                        scope.launch {
                            store.complete(activity.id)
                            animateNewest = true
                            screen = Screen.COURT
                        }
                    },
                    onAnother = {
                        if (isPlus || profile.skips < FREE_SKIPS) {
                            scope.launch { store.skip() }
                        } else {
                            backFromPlus = Screen.TODAY
                            screen = Screen.PLUS
                        }
                    },
                    onCourt = {
                        animateNewest = false
                        screen = Screen.COURT
                    }
                )
            }

            Screen.COURT -> CourtScreen(
                sport = profile.sport ?: sport ?: Sport.VOLLEYBALL,
                connected = profile.connected,
                day = profile.journeyDay,
                animateNewest = animateNewest,
                isPlus = isPlus,
                onToday = {
                    animateNewest = false
                    screen = Screen.TODAY
                },
                onPlus = {
                    animateNewest = false
                    backFromPlus = Screen.COURT
                    screen = Screen.PLUS
                },
                onSettings = {
                    animateNewest = false
                    backFromSettings = Screen.COURT
                    screen = Screen.SETTINGS
                }
            )

            Screen.PLUS -> PlusScreen(
                onClose = { screen = backFromPlus },
                onPromo = {
                    backFromAccount = Screen.PLUS
                    screen = Screen.ACCOUNT
                }
            )

            Screen.SETTINGS -> SettingsScreen(
                profile = profile,
                onClose = { screen = backFromSettings },
                onAccount = {
                    backFromAccount = Screen.SETTINGS
                    screen = Screen.ACCOUNT
                },
                onPlus = {
                    backFromPlus = Screen.SETTINGS
                    screen = Screen.PLUS
                },
                onEditAnswers = { screen = Screen.SITUATION },
                onNextDay = { scope.launch { store.nextDay() } },
                onStartOver = { askReset = true }
            )

            Screen.ACCOUNT -> AccountScreen(onClose = { screen = backFromAccount })
        }
    }

    if (askReset) {
        AlertDialog(
            onDismissRequest = { askReset = false },
            title = { Text("Start over?") },
            text = { Text("This clears your answers, your days and your court on this phone.") },
            confirmButton = {
                TextButton(onClick = {
                    askReset = false
                    sport = Sport.VOLLEYBALL
                    situation = null
                    misses = emptySet()
                    scope.launch { store.reset() }
                    screen = Screen.WELCOME
                }) { Text("Start over") }
            },
            dismissButton = { TextButton(onClick = { askReset = false }) { Text("Cancel") } }
        )
    }
}
