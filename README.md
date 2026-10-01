# Still In The Game

**Different route, same you.**

When you're injured, you lose more than the game. You lose your routine, your team and part of who you are. Still In The Game is for athletes who can't play right now. Pick your sport, tell the app what you miss, and get one small way to stay connected each day: watch, learn, reflect, or reach out to your team.

Every activity you complete rebuilds a piece of your court, from an empty outline to a full match with the net, your teammates, the lights and the crowd. Progress only goes up. Missing a day costs nothing.

Built for the RevenueCat Shipaton 2026, Next Gen category.

## What's in the app

- Pick your world: volleyball, basketball, football or running
- Where are you now: can't participate, participating differently, or getting back into it
- What you miss most: teammates, playing, improving, competition, routine, belonging
- Today's connection: one activity a day from a library of 29, matched to your answers
- Your comeback journey: a court drawn in code that rebuilds piece by piece
- Still In The Game+ through RevenueCat: more than one activity a day and unlimited new suggestions

Everything is stored on the phone. No account, no backend. The app gives no medical or physical advice: activities are about watching, learning, reflecting and connecting.

## How to run it

1. Install [Android Studio](https://developer.android.com/studio) (it includes the Android SDK and JDK 21).
2. File → Open and choose this folder. If asked for a Gradle JVM, pick JVM 21.
3. Create an emulator in Device Manager (Pixel 7, API 34) or plug in an Android phone with USB debugging.
4. Press Run.

The app runs without any keys. To try the Plus purchase:

1. In the [RevenueCat dashboard](https://app.revenuecat.com), enable the **Test Store** under Apps & providers and copy its API key (starts with `test_`).
2. Create an entitlement called `plus`, a product in the Test Store, and attach it to the `current` offering.
3. Add this line to `local.properties` in the project root:
   ```
   revenuecat.apiKey=test_YOUR_KEY
   ```
4. Run again and open Plus from the court screen. The Test Store shows its own purchase dialog, so no Google Play account is needed.

## Tech

Kotlin, Jetpack Compose, DataStore, RevenueCat SDK 9.9. Min SDK 26.

## Credits

Fonts: [Sora](https://fonts.google.com/specimen/Sora) and [Inter](https://fonts.google.com/specimen/Inter), both under the SIL Open Font License.

Code under the MIT License, see `LICENSE`.
