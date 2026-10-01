# Still In The Game

**Different route, same you.**

When you're injured, you lose more than the game. You lose your routine, your team and part of who you are. Still In The Game is for athletes who can't play right now. Pick your sport, tell the app what you miss, and get one small way to stay connected each day: watch, learn, reflect, or reach out to your team.

Every activity you complete rebuilds a piece of your court, from an empty outline to a full match with the net, your teammates, the lights and the crowd. Progress only goes up. Missing a day costs nothing.

Built for the RevenueCat Shipaton 2026, Next Gen category.

## What's in the app

- Pick your world: volleyball is open now. Basketball, football and running are coming soon
- Where are you now: can't participate, participating differently, or getting back into it
- What you miss most: teammates, playing, improving, competition, routine, belonging
- Today's connection: one activity a day from a library of 29, matched to your answers
- Your comeback journey: an illustrated night court that comes back in 12 stages, one for each day you show up (court, net, ball, bench, trees, street lamp, teammates, lights)
- Still In The Game+ through RevenueCat: more than one activity a day and unlimited new suggestions
- Settings: account, Plus status, restore purchases, promo codes, update your answers, start over
- Account: optional sign in with a username (RevenueCat app user ID), so Plus can follow you to a new phone

Everything is stored on the phone. No account, no backend. The app gives no medical or physical advice: activities are about watching, learning, reflecting and connecting.

## For judges and testers

You can reach Plus three ways:

1. Promo code. Open the court, tap Settings, then "Redeem a promo code" (or "Have a promo code?" on the Plus screen). Type `SHIPATON2026`. Works offline and even in a build without a RevenueCat key.
2. Test purchase. Builds with a RevenueCat Test Store key show the real paywall. Tap "Get Plus" and confirm the test purchase. No money moves.
3. Test account. In Settings, tap "Sign in with a username" and type `judge`. That user has Plus granted in the RevenueCat dashboard (a promotional entitlement), so Plus turns on as soon as you sign in.

Sign in only asks for a username. There's no email, phone or password, because the app has no backend: the username becomes the RevenueCat app user ID. It's meant for keeping Plus on a new phone, not for protecting private data, and your court never leaves the phone. Proper sign in with Google is on the list for later.

To see the days go by without waiting, use Settings > "Jump to tomorrow" (or "Demo: jump to tomorrow" after finishing an activity). Each day you can do a new activity and a new piece of the court comes back.

A redeemed code is also saved as the `promo_code` attribute on the RevenueCat customer, so it shows up in the dashboard.

## What's next

Recovery doesn't have a fixed end date, so the journey shouldn't either. Next up: more stages after the 12th (a crowd, a scoreboard, a season banner), courts for the other sports, and new places to unlock for long recoveries.

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

Fonts: [Sora](https://fonts.google.com/specimen/Sora), [Inter](https://fonts.google.com/specimen/Inter) and [Caveat](https://fonts.google.com/specimen/Caveat), all under the SIL Open Font License.

Illustrations: painted in code with Python and Pillow, see `tools/art_gen.py`.

Code under the MIT License, see `LICENSE`.
