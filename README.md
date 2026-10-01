# Still In The Game

**Different route, same you.**

When you're injured, you lose more than the game. You lose your routine, your team and part of who you are. Still In The Game is for athletes who can't play right now. Pick your sport, tell the app what you miss, and get one small way to stay connected each day: watch, learn, reflect, or reach out to your team.

Every activity you complete brings back a piece of your court. First the court itself, then your team, one player at a time, until it's six against six under the lights. Progress only goes up. Missing a day costs nothing.

Built for the RevenueCat Shipaton 2026, Next Gen category.

## Judges: a 3 minute tour

1. Install the APK from the [Releases page](../../releases) (the debug build, which has the RevenueCat Test Store paywall), or build from source (see "How to run it").
2. On the welcome screen tap **Get started**. Volleyball is selected. Pick where you are now and what you miss, then **Next**.
3. **Today's connection** shows one small activity. Tap **I did it**. The court screen opens and the first piece of your court fades in.
4. Tap **Today's connection** again. On the free plan you've done today's activity, so tap **Demo: jump to tomorrow** to move the app one day ahead and get a new one. Repeat to watch the court grow.
5. To try Plus, use any of these:
   - **Promo code:** on the welcome screen tap **I have an account or a code**, or go to Settings > **Redeem code**, and type `SHIPATON2026`.
   - **Test account:** Settings > **Sign in**, username `judge`. This user has Plus granted in the RevenueCat dashboard.
   - **Test purchase:** court screen > **Plus**, pick Yearly or Monthly and tap **Get Plus**. It's the RevenueCat Test Store, so no real money is charged.
6. With Plus, "I did it" can be tapped as often as you like, so you can see Season 2 (players arriving) and the rally after day 24 in a few minutes.

## What's in the app

- **Welcome:** a painted gym floor at night, with "Get started" and "I have an account or a code".
- **Your world:** volleyball is open now. Basketball, football and running are marked coming soon.
- **Where are you now:** can't participate, participating differently, or getting back into it. Plus what you miss most: teammates, playing, improving, competition, routine, belonging.
- **Today's connection:** one activity a day from a library of 29, matched to your answers. Never exercise or medical advice. Free users get 2 "give me another one" per day.
- **Your comeback journey:** an illustrated night court that grows one piece per activity.
  - Season 1 (12 pieces): ground, color, lines, posts, net, ball, bench, trees, street lamp, your bag, string lights, scoreboard.
  - Season 2 (12 pieces): the players arrive one by one, taking turns between your team and theirs, until it's six against six.
  - After that the ball moves around the court like a rally, and the bar starts a new season every 12 activities.
- **Still In The Game+:** more than one activity a day and unlimited suggestions. Yearly $19.99 (about 44% less than monthly) or Monthly $2.99, test prices in the RevenueCat Test Store. Everything you need to stay connected stays free.
- **Settings:** account, Plus status, redeem code, restore purchases, manage subscription, your answers, privacy, source code, start over.

## How RevenueCat is used

- Android SDK 10.24.0, configured in `StillApp.kt` with the key from `local.properties`.
- Entitlement `plus`, checked in `billing/Plus.kt` with a customer info listener, so Plus turns on the moment a purchase, restore or grant lands.
- The current offering's packages (Annual and Monthly) are loaded on the Plus screen and shown as a plan picker. The yearly savings badge is calculated from the real prices.
- Purchase, restore, and log in / log out with an app user ID (the optional username).
- A redeemed promo code is saved as the `promo_code` attribute on the customer, so it shows in the dashboard.
- Promotional entitlements granted in the dashboard (like the `judge` account) are recognized too.

## Privacy

Your answers, your days and your court are saved only on your phone. There's no backend. RevenueCat handles purchases and Plus: it gets an anonymous ID, or the username you choose if you sign in, plus a redeemed promo code. Nothing about your injury or health is collected. Start over in Settings deletes everything the app saved.

Sign in only asks for a username, with no email, phone or password, because there's no backend: the username becomes the RevenueCat app user ID. It's for keeping Plus on a new phone, not for protecting private data. Proper sign in is on the list for later.

## Shipaton 2026 Next Gen checklist

- Open source: MIT license in `LICENSE`, all code and art in this repo, art generator in `tools/art_gen.py`.
- RevenueCat SDK powers the Plus subscription (see "How RevenueCat is used").
- Judges can unlock every premium feature with the promo code `SHIPATON2026` or the `judge` account.
- Runs on Android 8.0+ (minSdk 26), phone and tablet, portrait.
- Demo video and screenshots are on the Devpost page.

## What's next

Recovery doesn't have a fixed end date, so the journey shouldn't either. Next up: courts for basketball, football and running, more places to unlock for long recoveries, Spanish, reminders, and a way for teams and coaches to keep injured players close.

## How to run it

1. Install [Android Studio](https://developer.android.com/studio) (it includes the Android SDK and JDK 21).
2. File > Open and choose this folder. If asked for a Gradle JVM, pick JVM 21. If Android Studio asks to install Android 15 (API 35), accept.
3. Create an emulator in Device Manager (Pixel 7, API 34) or plug in an Android phone with USB debugging.
4. Press Run.

The app runs without any keys, and the promo code works offline. To try the purchase and the `judge` account, add a RevenueCat Test Store key:

1. In the [RevenueCat dashboard](https://app.revenuecat.com), open the **Test Store** app under Apps and copy its API key (starts with `test_`).
2. Create the entitlement `plus` and two Test Store subscriptions (monthly and yearly), and attach both to `plus`.
3. In the `default` offering, add a Monthly package and an Annual package with those products.
4. Add this line to `local.properties` in the project root:
   ```
   revenuecat.apiKey=test_YOUR_KEY
   ```
5. Run again and open Plus from the court screen. The Test Store shows its own purchase dialog, so no Google Play account is needed.

Use a debug build with a Test Store key. RevenueCat stops release builds that use a Test Store key on purpose, so the release build leaves the key out and runs on promo codes only.

## Tech

Kotlin 2.0, Jetpack Compose, DataStore, RevenueCat Android SDK 10.24.0. Min SDK 26, compile SDK 35.

## Credits

Fonts: [Sora](https://fonts.google.com/specimen/Sora), [Inter](https://fonts.google.com/specimen/Inter) and [Caveat](https://fonts.google.com/specimen/Caveat), all under the SIL Open Font License.

Illustrations: painted in code with Python and Pillow, see `tools/art_gen.py`.

Code under the MIT License, see `LICENSE`.
