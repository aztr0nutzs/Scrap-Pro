# Build Verification

> **Current branch status:** This is a deliberately binary-free reconstruction PR. The successful baseline receipts below were captured before the binary payloads were removed. A fresh Android build is expected to remain blocked until the wrapper JAR and Android artwork are restored in a separate asset-only change.

## Verified environment

- Verification date: 2026-07-31 (UTC)
- Host: Linux x86_64
- JDK: Oracle OpenJDK 21.0.2
- Gradle wrapper: 9.3.1
- Android Gradle Plugin: 9.1.1
- Kotlin Compose plugin: 2.2.10
- Android compile SDK: 36.1; target SDK: 36; minimum SDK: 24
- Node/Vite: repository lockfile with Vite 7.3.6

The container initially had no Android SDK. Platform 36/36.1 and Build Tools 36.0.0 were installed under `/opt/android-sdk`; the machine-local `local.properties` is deliberately ignored. Robolectric's runtime Android artifact was also pre-fetched into the user Maven cache because the test JVM did not inherit the container's HTTP proxy. Neither environment workaround changes or vendors project source.

## Command results

| Command | Result | Evidence / notes |
|---|---|---|
| `chmod +x gradlew` | Pass | Executable mode restored and tracked. |
| `./gradlew --version` | Pass | Gradle 9.3.1 on JVM 21.0.2. |
| `./gradlew clean` | Pass | Standalone `:app` project discovered and cleaned. |
| `./gradlew test` | Pass | Six JVM/Robolectric/calculation tests passed after environment-only Robolectric artifact prefetch. |
| `./gradlew assembleDebug` | Pass | Debug APK packaged and signed with the standard debug key. |
| `npm install` | Pass | Lockfile resolved; audit reported zero vulnerabilities. |
| `npm run build` | Pass | Vite built the six-tab preview into ignored `dist-preview/`. |
| `python3 scripts/verify_project.py` | Pass | Routes, assets, references, packages, names, forbidden dependencies, and structure passed. |
| Full tree/duplicate/package/resource checks | Pass | Incorporated into the verifier and supplemented with `find`, `sha256sum`, and filename grouping commands. |
| ZIP creation and `unzip -t` | Pass | `ScrapPro-reconstructed.zip` contains the reconstructed tree and excludes generated/local content. |

## Current binary-free verification

- `python3 scripts/verify_project.py` passes and rejects any binary payload accidentally added to this branch.
- `npm run build` remains applicable; Vite preserves unresolved local asset URLs until the asset-only follow-up restores them.
- `./gradlew --version`, `./gradlew test`, and `./gradlew assembleDebug` are intentionally blocked because `gradle/wrapper/gradle-wrapper.jar` is excluded.
- After restoring the wrapper JAR, Android resource processing will remain intentionally blocked until the excluded PNG/WebP files are also restored.

## Build repairs made

1. Moved the flattened Android application build DSL to `app/build.gradle.kts` and restored a minimal root plugin declaration.
2. Restored Gradle version catalog and wrapper files to `gradle/` and `gradle/wrapper/`.
3. Added the missing `kotlinx-coroutines-play-services` catalog/module dependency required by `Task.await()` in `LocationRepository`.
4. Enabled headless AWT for stable command-line KSP execution in this non-interactive environment.
5. Restored conventional Android source sets, resources, manifest, tests, and preview paths.

## Artifact receipts

- Debug APK: `app/build/outputs/apk/debug/app-debug.apk`
- Clean source archive: `ScrapPro-reconstructed.zip` (repository root, ignored by Git)
- Preview build: `dist-preview/` (generated and ignored)

No emulator or physical Android device was available, so installation, launch, and physical traversal of the six destinations were not performed. The Android navigation graph, web tabs, JVM tests, resource packaging, and APK assembly were verified programmatically.

## Phase 2 persistent Home verification

- Temporarily restored the previously verified binary wrapper/artwork from commit `fe4e3f8` as uncommitted build inputs; all were removed again before the Phase 2 commit.
- `./gradlew test` passed 13 tests, including six active-haul repository cases and the schema 1→2 migration test.
- `./gradlew assembleDebug` passed and produced `app/build/outputs/apk/debug/app-debug.apk`.
- `python3 scripts/verify_project.py` passed after binary cleanup and verifies repository-backed Home states, explicit non-destructive migration, Profit persistence wiring, and absence of the former fake dashboard values.
- No emulator or physical device was available. Room restoration is covered by reopening the repository over the same database; tab restoration remains covered structurally by Navigation Compose save/restore checks.

## Phase 3 yards directory verification

- Temporarily restored the authoritative wrapper and artwork from commit `6141566` only as uncommitted build inputs; removed them before commit and archive creation.
- `./gradlew test` passed 22 tests, including directory filtering/hours/distance/staleness, permission preconditions, dial/map intent construction, Room CRUD/cascade behavior, and migrations 1→2→3.
- `./gradlew assembleDebug` passed and produced `app/build/outputs/apk/debug/app-debug.apk`.
- `npm ci && npm run build` passed with zero audit vulnerabilities.
- `python3 scripts/verify_project.py` passed after binary cleanup and rejects fictional 555 records, silent New York fallback, missing location gating, and missing Phase 3 contracts.
