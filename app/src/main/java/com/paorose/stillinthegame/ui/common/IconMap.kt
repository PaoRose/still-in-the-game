package com.paorose.stillinthegame.ui.common

import com.paorose.stillinthegame.data.Miss
import com.paorose.stillinthegame.data.Situation
import com.paorose.stillinthegame.data.Sport

fun Sport.icon(): List<String> = when (this) {
    Sport.VOLLEYBALL -> LineIcons.VOLLEYBALL
    Sport.BASKETBALL -> LineIcons.BASKETBALL
    Sport.FOOTBALL -> LineIcons.FOOTBALL
    Sport.RUNNING -> LineIcons.RUNNING
}

fun Situation.icon(): List<String> = when (this) {
    Situation.CANT -> LineIcons.PAUSE
    Situation.DIFFERENT -> LineIcons.EYE
    Situation.RETURNING -> LineIcons.SUNRISE
}

fun Miss.icon(): List<String> = when (this) {
    Miss.TEAMMATES -> LineIcons.TEAMMATES
    Miss.PLAYING -> LineIcons.PLAYING
    Miss.IMPROVING -> LineIcons.IMPROVING
    Miss.COMPETITION -> LineIcons.COMPETITION
    Miss.ROUTINE -> LineIcons.ROUTINE
    Miss.BELONGING -> LineIcons.BELONGING
}
