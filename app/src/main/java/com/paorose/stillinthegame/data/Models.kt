package com.paorose.stillinthegame.data

/** Where the athlete is right now. Changes the tone and effort of activities. */
enum class Situation(val title: String, val subtitle: String) {
    CANT("I can't participate right now", "Rest, recovery, or life got in the way"),
    DIFFERENT("I'm participating in a different way", "Watching, helping out, staying close"),
    RETURNING("I'm getting back into it", "Slowly finding my way back")
}

/** What they miss most. Activities are matched to these. */
enum class Miss(val label: String) {
    TEAMMATES("My teammates"),
    PLAYING("Playing"),
    IMPROVING("Improving"),
    COMPETITION("Competition"),
    ROUTINE("My routine"),
    BELONGING("Feeling part of something")
}

enum class Kind(val label: String) { WATCH("Watch"), LEARN("Learn"), CONNECT("Connect"), REFLECT("Reflect") }

/**
 * One connection activity. Never physical exercise, never medical advice.
 * {sport} and {team} in the text are filled in for the user's sport.
 */
data class Activity(
    val id: String,
    val kind: Kind,
    val minutes: Int,
    val title: String,
    val why: String,
    val misses: Set<Miss>,
    val situations: Set<Situation> = Situation.values().toSet(),
    val sports: Set<Sport> = Sport.values().toSet()
) {
    fun titleFor(sport: Sport) = title.fill(sport)
    fun whyFor(sport: Sport) = why.fill(sport)
}

private fun String.fill(sport: Sport) = replace("{sport}", sport.label.lowercase())
