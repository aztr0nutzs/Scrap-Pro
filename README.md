# ScrapPro

ScrapPro is a keyless Android scrap-hauling toolkit with a synchronized interactive web preview.

## Android app

1. Open this directory in Android Studio.
2. Use JDK 17 and install Android SDK 36.1 when prompted.
3. Sync Gradle and run the `app` configuration.
4. For command-line builds, run `./gradlew test assembleDebug`.

The six bottom destinations are `home`, `profit`, `wire`, `payload`, `id`, and `yards`. Tab navigation saves and restores each destination state. Yard directions use Android system intents: Google Maps is targeted first, with a browser maps URL as fallback. No Maps SDK or API key is used.

## AI Studio web preview

1. Run `npm install`.
2. Run `npm run dev`.
3. Open the local URL printed by Vite.

The preview implements the same six destinations, calculators, diagnostic wizard, yard filters, banner mappings, and bottom-tab behavior as the Compose app.

## Verification

Run:

```bash
python3 scripts/verify_project.py
npm run build
./gradlew test assembleDebug
```

The structural verifier checks route completeness, saved navigation state, all seven PNG resources, banner fit rules, forbidden Maps/key dependencies, and yard intents.
