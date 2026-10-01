package com.paorose.stillinthegame.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import androidx.datastore.preferences.core.Preferences
import java.time.LocalDate
import java.time.temporal.ChronoUnit

private val Context.dataStore by preferencesDataStore(name = "still_in_the_game")

/** Everything the app remembers. Stored only on the phone. */
data class Profile(
    val sport: Sport? = null,
    val situation: Situation? = null,
    val misses: Set<Miss> = emptySet(),
    /** Total activities completed. Only ever goes up. */
    val connected: Int = 0,
    val doneIds: Set<String> = emptySet(),
    /** Day of the last completed activity, ISO date. */
    val lastDoneDay: String? = null,
    /** How many times "give me another one" was tapped today. */
    val skipsToday: Int = 0,
    val skipsDay: String? = null,
    /** Days added by "Jump to tomorrow", so a demo can show several days in a few minutes. */
    val dayOffset: Int = 0,
    /** The day the journey started, ISO date. */
    val firstDay: String? = null
) {
    val today: LocalDate get() = LocalDate.now().plusDays(dayOffset.toLong())
    val onboarded get() = sport != null && situation != null
    val doneToday get() = lastDoneDay == today.toString()
    val skips get() = if (skipsDay == today.toString()) skipsToday else 0
    /** Day 1 is the day you started. */
    val journeyDay: Int
        get() = firstDay
            ?.let { runCatching { ChronoUnit.DAYS.between(LocalDate.parse(it), today).toInt() + 1 }.getOrNull() }
            ?.coerceAtLeast(1) ?: 1
}

private fun todayFor(p: Preferences): String =
    LocalDate.now().plusDays((p[Store.OFFSET] ?: 0).toLong()).toString()

class Store(private val context: Context) {

    val profile: Flow<Profile> = context.dataStore.data.map { p ->
        Profile(
            sport = p[SPORT]?.let { runCatching { Sport.valueOf(it) }.getOrNull() },
            situation = p[SITUATION]?.let { runCatching { Situation.valueOf(it) }.getOrNull() },
            misses = p[MISSES].orEmpty().mapNotNull { runCatching { Miss.valueOf(it) }.getOrNull() }.toSet(),
            connected = p[CONNECTED] ?: 0,
            doneIds = p[DONE].orEmpty(),
            lastDoneDay = p[LAST_DAY],
            skipsToday = p[SKIPS] ?: 0,
            skipsDay = p[SKIPS_DAY],
            dayOffset = p[OFFSET] ?: 0,
            firstDay = p[FIRST_DAY]
        )
    }

    suspend fun saveSetup(sport: Sport, situation: Situation, misses: Set<Miss>) {
        context.dataStore.edit {
            it[SPORT] = sport.name
            it[SITUATION] = situation.name
            it[MISSES] = misses.map { m -> m.name }.toSet()
            if (it[FIRST_DAY] == null) it[FIRST_DAY] = todayFor(it)
        }
    }

    suspend fun complete(activityId: String) {
        context.dataStore.edit {
            it[CONNECTED] = (it[CONNECTED] ?: 0) + 1
            it[DONE] = it[DONE].orEmpty() + activityId
            it[LAST_DAY] = todayFor(it)
        }
    }

    suspend fun skip() {
        context.dataStore.edit {
            val today = todayFor(it)
            val current = if (it[SKIPS_DAY] == today) it[SKIPS] ?: 0 else 0
            it[SKIPS] = current + 1
            it[SKIPS_DAY] = today
        }
    }

    /** Demo helper: moves the app to the next day. */
    suspend fun nextDay() {
        context.dataStore.edit { it[OFFSET] = (it[OFFSET] ?: 0) + 1 }
    }

    /** For the demo and for "change my sport": wipes everything. */
    suspend fun reset() {
        context.dataStore.edit { it.clear() }
    }

    companion object {
        val SPORT = stringPreferencesKey("sport")
        val SITUATION = stringPreferencesKey("situation")
        val MISSES = stringSetPreferencesKey("misses")
        val CONNECTED = intPreferencesKey("connected")
        val DONE = stringSetPreferencesKey("done_ids")
        val LAST_DAY = stringPreferencesKey("last_done_day")
        val SKIPS = intPreferencesKey("skips_today")
        val SKIPS_DAY = stringPreferencesKey("skips_day")
        val OFFSET = intPreferencesKey("day_offset")
        val FIRST_DAY = stringPreferencesKey("first_day")
    }
}
