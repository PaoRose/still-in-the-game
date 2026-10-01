package com.paorose.stillinthegame.data

/** The worlds a user can return to. Each one has its own "field of return". */
enum class Sport(val label: String, val field: String) {
    VOLLEYBALL("Volleyball", "court"),
    BASKETBALL("Basketball", "court"),
    FOOTBALL("Football", "pitch"),
    RUNNING("Running", "track")
}
