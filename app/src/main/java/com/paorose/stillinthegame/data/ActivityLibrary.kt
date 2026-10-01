package com.paorose.stillinthegame.data

import com.paorose.stillinthegame.data.Miss.BELONGING
import com.paorose.stillinthegame.data.Miss.COMPETITION
import com.paorose.stillinthegame.data.Miss.IMPROVING
import com.paorose.stillinthegame.data.Miss.PLAYING
import com.paorose.stillinthegame.data.Miss.ROUTINE
import com.paorose.stillinthegame.data.Miss.TEAMMATES
import com.paorose.stillinthegame.data.Situation.CANT
import com.paorose.stillinthegame.data.Situation.DIFFERENT
import com.paorose.stillinthegame.data.Situation.RETURNING
import com.paorose.stillinthegame.data.Sport.BASKETBALL
import com.paorose.stillinthegame.data.Sport.FOOTBALL
import com.paorose.stillinthegame.data.Sport.RUNNING
import com.paorose.stillinthegame.data.Sport.VOLLEYBALL
import java.time.LocalDate

/** Written for this app. No physical exercises, no medical claims. */
object ActivityLibrary {

    val all: List<Activity> = listOf(
        // Connect
        Activity("msg-appreciate", Kind.CONNECT, 5, "Send one teammate a message telling them something you appreciate about their game.",
            "You're still part of the team. Teams are built on moments like this.", setOf(TEAMMATES, BELONGING)),
        Activity("ask-practice", Kind.CONNECT, 5, "Ask a teammate what they worked on at practice this week.",
            "Staying in the loop keeps your place in the conversation.", setOf(TEAMMATES, IMPROVING, BELONGING)),
        Activity("cheer-next", Kind.CONNECT, 5, "Find out when your team plays next and tell them you'll be cheering.",
            "Support goes both ways. They'll feel it.", setOf(TEAMMATES, COMPETITION, BELONGING)),
        Activity("watch-practice", Kind.CONNECT, 30, "Go watch your team practice, even for half an hour.",
            "Being in the gym counts. You're still part of the routine.", setOf(TEAMMATES, ROUTINE, BELONGING), setOf(DIFFERENT, RETURNING)),
        Activity("share-clip", Kind.CONNECT, 5, "Send your team chat a great {sport} clip you found and say why you liked it.",
            "You bring something to the team even from outside the court.", setOf(TEAMMATES, BELONGING)),
        Activity("thank-coach", Kind.CONNECT, 5, "Message your coach and ask one thing you could study while you're out.",
            "Coaches notice the players who stay curious.", setOf(IMPROVING, BELONGING)),
        Activity("help-role", Kind.CONNECT, 10, "Ask your team if you can help with something: stats, warm-up music, the water bottles.",
            "There's more than one way to be on the team.", setOf(TEAMMATES, BELONGING, ROUTINE), setOf(DIFFERENT, RETURNING)),

        // Watch
        Activity("watch-defense", Kind.WATCH, 10, "Watch 10 minutes of a pro volleyball match and spot three great defensive digs.",
            "Your eyes are still training, even if your body is resting.", setOf(PLAYING, IMPROVING, COMPETITION), sports = setOf(VOLLEYBALL)),
        Activity("watch-setter", Kind.WATCH, 10, "Watch one set of a pro match following only the setter. Where do they look before each set?",
            "Seeing the game through one player changes how you read it.", setOf(IMPROVING, PLAYING), sports = setOf(VOLLEYBALL)),
        Activity("watch-pnr", Kind.WATCH, 10, "Watch 10 minutes of a pro game and count how many plays start with a pick and roll.",
            "Patterns are easier to spot from the bench. Use that.", setOf(IMPROVING, PLAYING), sports = setOf(BASKETBALL)),
        Activity("watch-press", Kind.WATCH, 10, "Watch 10 minutes of a pro match and notice when the team presses high and when they drop back.",
            "Understanding the why behind movement makes you smarter on the pitch.", setOf(IMPROVING, PLAYING), sports = setOf(FOOTBALL)),
        Activity("watch-race", Kind.WATCH, 10, "Watch the last 400 meters of a pro race and notice when the winner makes their move.",
            "Race sense is something you can build without running a step.", setOf(IMPROVING, COMPETITION), sports = setOf(RUNNING)),
        Activity("watch-favorite", Kind.WATCH, 10, "Watch highlights of your favorite {sport} player and pick one thing you'd copy.",
            "Every athlete learns by watching. Today, that's your training.", setOf(PLAYING, IMPROVING)),
        Activity("watch-team-game", Kind.WATCH, 15, "Watch your team's last game, live or on video, and note one moment you'd want to talk about.",
            "You see things from outside that players on the court can't.", setOf(TEAMMATES, COMPETITION, BELONGING)),
        Activity("watch-classic", Kind.WATCH, 15, "Watch a famous {sport} final you've never seen.",
            "Remember why you fell in love with this game.", setOf(PLAYING, COMPETITION)),

        // Learn
        Activity("learn-rotation", Kind.LEARN, 10, "Learn one rotation your team uses and draw it on paper.",
            "When you're back, you'll already know where to stand.", setOf(IMPROVING, TEAMMATES), sports = setOf(VOLLEYBALL)),
        Activity("learn-rule", Kind.LEARN, 10, "Look up one {sport} rule you're not 100% sure about and explain it in your own words.",
            "Knowing the game deeply makes you calmer when you play it.", setOf(IMPROVING, COMPETITION)),
        Activity("learn-play", Kind.LEARN, 10, "Pick one set play your team runs and write down each player's job.",
            "Game IQ is a skill. You're building it right now.", setOf(IMPROVING, TEAMMATES), sports = setOf(BASKETBALL, FOOTBALL)),
        Activity("learn-pacing", Kind.LEARN, 10, "Read about negative splits and plan how you'd pace your next race.",
            "Smart racing starts long before the start line.", setOf(IMPROVING, COMPETITION), sports = setOf(RUNNING)),
        Activity("learn-history", Kind.LEARN, 10, "Read the story of an athlete in {sport} who came back after time away.",
            "Every comeback starts with someone deciding they're not done.", setOf(BELONGING, PLAYING)),
        Activity("learn-scout", Kind.LEARN, 15, "Scout your team's next opponent: one strength, one weakness.",
            "Share it with your team. That's real help.", setOf(COMPETITION, TEAMMATES, IMPROVING), setOf(DIFFERENT, RETURNING)),
        Activity("learn-mental", Kind.LEARN, 10, "Find one pre-game routine a pro athlete uses and write down what you'd borrow.",
            "The mental side of {sport} is something you can train every day.", setOf(IMPROVING, ROUTINE)),

        // Reflect
        Activity("reflect-why", Kind.REFLECT, 5, "Write down three reasons you love {sport}.",
            "This is still who you are. Different route, same you.", setOf(BELONGING, PLAYING)),
        Activity("reflect-goal", Kind.REFLECT, 5, "Write one goal for when you're back. Not a number, a feeling.",
            "Picture the place you're going back to.", setOf(PLAYING, COMPETITION, IMPROVING), setOf(CANT, DIFFERENT)),
        Activity("reflect-best", Kind.REFLECT, 5, "Remember your best moment in {sport}. Write it down in as much detail as you can.",
            "That moment is still yours. So is the next one.", setOf(PLAYING, BELONGING)),
        Activity("reflect-routine", Kind.REFLECT, 5, "Keep your practice time today: use it for 10 minutes of {sport}, any way you can.",
            "Your routine is still yours. It just looks different for now.", setOf(ROUTINE)),
        Activity("reflect-letter", Kind.REFLECT, 10, "Write a short note to yourself for your first day back.",
            "Future you will want to read this.", setOf(BELONGING, PLAYING), setOf(CANT, DIFFERENT)),
        Activity("reflect-progress", Kind.REFLECT, 5, "Write down one thing you understand about {sport} now that you didn't a month ago.",
            "You've been improving all along.", setOf(IMPROVING), setOf(DIFFERENT, RETURNING)),
        Activity("reflect-team", Kind.REFLECT, 5, "Write down what you bring to your team that isn't about skill.",
            "Teams need more than talent. They need you.", setOf(TEAMMATES, BELONGING))
    )

    /** Activities that fit this person, best matches first. Never empty. */
    fun candidates(sport: Sport, situation: Situation, misses: Set<Miss>): List<Activity> {
        val fit = all.filter { sport in it.sports && situation in it.situations }
        val matched = fit.filter { a -> a.misses.any { it in misses } }
        return (matched + fit).distinct()
    }

    /** Today's pick, stable through the day, skipping ones already done. */
    fun pick(
        sport: Sport,
        situation: Situation,
        misses: Set<Miss>,
        done: Set<String>,
        skip: Int,
        today: LocalDate = LocalDate.now()
    ): Activity {
        val pool = candidates(sport, situation, misses)
        val fresh = pool.filterNot { it.id in done }.ifEmpty { pool }
        val start = (today.toEpochDay() % fresh.size).toInt()
        return fresh[(start + skip).mod(fresh.size)]
    }
}
