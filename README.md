# ScrapPro

ScrapPro is a keyless Android scrap-hauling toolkit with a synchronized interactive web preview. The repository root is the only Gradle/Vite project root; Android code lives in `app/`, the browser implementation in `preview/`, and deterministic structural checks in `scripts/`.

> **Text-only reconstruction PR:** Binary files are intentionally excluded from this branch so the directory/build reconstruction can be reviewed in a binary-free pull request. Before running Android builds or reviewing preview artwork, restore the authoritative files listed in [Binary assets to restore](#binary-assets-to-restore) at their documented paths. Do not substitute recompressed or placeholder assets.

## Requirements

- JDK 17 or newer (the verified build used JDK 21)
- Android SDK Platform 36.1 and Build Tools 36.0.0
- Node.js 20.19+ or 22.12+ (required by Vite 7)
- Python 3.10+

## Android app

1. Open this directory in Android Studio.
2. Use JDK 17+ and install Android SDK 36.1 when prompted.
3. Sync Gradle and run the `app` configuration.
4. For command-line builds, run `chmod +x gradlew && ./gradlew clean test assembleDebug`.

The debug APK is produced at `app/build/outputs/apk/debug/app-debug.apk`.

The six bottom destinations are `home`, `profit`, `wire`, `payload`, `id`, and `yards`. Tab navigation saves and restores each destination state. Yard directions use Android system intents: Google Maps is targeted first, with a browser maps URL as fallback. No Maps SDK or API key is used.

## AI Studio web preview

1. Run `npm install`.
2. Run `npm run dev` (Vite serves `preview/` as its web root).
3. Open the local URL printed by Vite.

The preview implements the same six destinations, calculators, diagnostic wizard, yard filters, banner mappings, and bottom-tab behavior as the Compose app.

## Verification

Run:

```bash
python3 scripts/verify_project.py
npm run build
./gradlew test assembleDebug
```

The structural verifier checks root/module files, flattened-file leakage, Kotlin package paths, Android names and references, route completeness, saved navigation state, the seven reserved PNG asset names, binary exclusion, banner fit rules, forbidden Maps/key dependencies, duplicate-content groups, and yard intents. See `PROJECT_RECONSTRUCTION_REPORT.md` for the complete original-to-final inventory and `BUILD_VERIFICATION.md` for verified command receipts and environment notes.

## Binary assets to restore

- `gradle/wrapper/gradle-wrapper.jar`
- `app/src/main/res/drawable/{scrap_icon,scrap_main,id_wizard,load_calc,load_bal,price_track,scrap_calc}.png`
- `preview/assets/{scrap_icon,scrap_main,id_wizard,load_calc,load_bal,price_track,scrap_calc}.png`
- `app/src/main/res/mipmap-mdpi/{ic_launcher,ic_launcher_round}.webp`
- `app/src/test/screenshots/greeting.png`

The structural verifier reserves and checks these names while rejecting binary payloads in this branch. After the asset-only follow-up restores them, run the full Android, preview, hash, and ZIP verification sequence below.

## Clean distribution ZIP

From the parent of this repository, create a source ZIP without generated content:

```bash
zip -r ScrapPro-reconstructed.zip Scrap-Pro \
  -x 'Scrap-Pro/.git/*' 'Scrap-Pro/.gradle/*' 'Scrap-Pro/**/build/*' \
     'Scrap-Pro/node_modules/*' 'Scrap-Pro/dist-preview/*' \
     'Scrap-Pro/local.properties' 'Scrap-Pro/*.zip'
unzip -t ScrapPro-reconstructed.zip
```
