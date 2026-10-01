package com.paorose.stillinthegame.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate

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
    val skipsDay: String? = null
) {
    val onboarded get() = sport != null && situation != null
    val doneToday get() = lastDoneDay == LocalDate.now().toString()
    val skips get() = if (skipsDay == LocalDate.now().toString()) skipsToday else 0
}

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
            skipsDay = p[SKIPS_DAY]
        )
    }

    suspend fun saveSetup(sport: Sport, situation: Situation, misses: Set<Miss>) {
        context.dataStore.edit {
            it[SPORT] = sport.name
            it[SITUATION] = situation.name
            it[MISSES] = misses.map { m -> m.name }.toSet()
        }
    }

    suspend fun complete(activityId: String) {
        context.dataStore.edit {
            it[CONNECTED] = (it[CONNECTED] ?: 0) + 1
            it[DONE] = it[DONE].orEmpty() + activityId
            it[LAST_DAY] = LocalDate.now().toString()
        }
    }

    suspend fun skip() {
        val today = LocalDate.now().toString()
        context.dataStore.edit {
            val current = if (it[SKIPS_DAY] == today) it[SKIPS] ?: 0 else 0
            it[SKIPS] = current + 1
            it[SKIPS_DAY] = today
        }
    }

    /** For the demo and for "change my sport": wipes everything. */
    suspend fun reset() {
        context.dataStore.edit { it.clear() }
    }

    private companion object {
        val SPORT = stringPreferencesKey("sport")
        val SITUATION = stringPreferencesKey("situation")
        val MISSES = stringSetPreferencesKey("misses")
        val CONNECTED = intPreferencesKey("connected")
        val DONE = stringSetPreferencesKey("done_ids")
        val LAST_DAY = stringPreferencesKey("last_done_day")
        val SKIPS = intPreferencesKey("skips_today")
        val SKIPS_DAY = stringPreferencesKey("skips_day")
    }
}
