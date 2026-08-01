# ScrapPro Forensic Tab & System Audit

> **Binary-free PR note:** The reconstruction identified and assigned the exact binary destinations described below, but binary payloads are intentionally excluded from this branch for pull-request creation. Restore the authoritative PNG, WebP, screenshot baseline, and Gradle wrapper JAR files at the paths recorded in `PROJECT_RECONSTRUCTION_REPORT.md`; no placeholder or recompressed substitute is acceptable.

## SECTION 1: FORENSIC TAB & SYSTEM AUDIT TABLE

| Severity | Tab / Screen | Defect Type | Root Cause | Exact Impact | Remediation |
| :--- | :--- | :--- | :--- | :--- | :--- |
| Critical | all | Missing Logic | Navigation exposed five legacy aggregate routes rather than the six mandatory routes. | `home`, `profit`, `wire`, `payload`, `id`, and `yards` could not all be reached as first-class bottom tabs. | Replaced the graph and sealed route model with six destinations and saved/restored tab back stacks. |
| Critical | all web screens | Missing Logic | No HTML, CSS, JavaScript, package manifest, or preview entry point existed. | AI Studio had no project-owned interactive mockup implementation to render. | Added a Vite-served preview with six functional tabs and parallel calculator/wizard/directory logic. |
| High | all banners | UI Crop / Duplicate Text | `HeaderBanner` and `MetalHeaderBanner` used fixed 200 dp heights, `ContentScale.Crop`, gradients, and overlaid titles. | Artwork titles could be clipped and duplicated. | Replaced banner rendering with `fillMaxWidth`, intrinsic height, and `ContentScale.FillWidth`; removed overlays. |
| High | all Android screens | Asset Mapping | Required PNGs were outside Android resources while same-name JPEGs occupied `res/drawable`. | Android used recompressed JPEG copies rather than the exact supplied assets. | Replaced the seven JPEG resources with the seven exact PNG assets and assigned `scrap_icon` as the app icon. |
| High | home | Missing Logic | The legacy calculator container doubled as the start destination. | No dashboard, haul summary, earnings snapshot, calculator shortcuts, or recent activity existed. | Added a complete dashboard and wired shortcuts to all five other tabs. |
| High | profit | Missing Logic | The screen lacked editable expense/labor deductions and explicit yield-loss control. | Net profit and clean/dirty comparisons were incomplete. | Added multi-metal rows, direct weight entry, clean/dirty adjustment, recoverable-yield percentage, fuel/other/labor fields, and guarded totals. |
| High | wire | Missing Logic | The calculator stopped at hourly wage. | It did not expose recovery-rate control, labor duration, machine cost, or machine payback. | Added all fields and finite machine-payback calculation. |
| Critical | payload | Missing Logic | No tongue-weight model existed. | The mandatory 10%–15% safe zone and axle-balance warning could not be evaluated. | Added measured tongue weight, loaded-trailer basis, safe range, visual status, and green/yellow/red warnings. |
| High | id | Missing Logic / Duplicate Text | The wizard skipped acid-test branching and rendered a second image/title overlay in results. | Diagnostic coverage was incomplete and violated banner rules. | Added guarded acid-kit branching and removed result image overlays. |
| High | yards | Intent / Missing Logic | Yard results were hidden until location permission was granted; filters and primary navigation were missing from list cards. | The directory was unusable when permission was denied and navigation was buried. | Preserved fallback yards, added accepted-metal/operations filters, dialer actions, and `NAVIGATE NOW` on every card. |
| High | build | Missing Logic | Secrets and Google-services scaffolding remained, `.env.example` contained `MAPS_API_KEY`, and the Gradle wrapper was absent. | The archive violated the keyless requirement and was not a standalone Gradle project. | Removed key scaffolding and added a Gradle 9.3.1 wrapper for AGP 9.1.1/JDK 17. |
| Medium | profit / wire / payload | Missing Logic | Several equations accepted negative values or could divide by zero. | Invalid input could produce negative/undefined ratios or misleading ROI. | Clamped negative values and explicitly returns finite zero results when denominators are zero. |
| Medium | tests | Missing Logic | Screenshot and resource tests referenced deleted `Greeting` UI and the wrong app label. | Test source compilation would fail or assert stale behavior. | Replaced the screenshot target, corrected the label assertion, and added calculator edge-case tests. |

## SECTION 2: COMPLETE CODE REMEDIATION BLOCKS

The production remediation is applied directly as complete files, not excerpts. Principal files:

- `app/src/main/java/com/example/ui/navigation/Screen.kt`
- `app/src/main/java/com/example/ui/navigation/AppNavigation.kt`
- `app/src/main/java/com/example/ui/home/HomeScreen.kt`
- `app/src/main/java/com/example/ui/calculators/ProfitCalculator.kt`
- `app/src/main/java/com/example/ui/calculators/WireStripping.kt`
- `app/src/main/java/com/example/ui/calculators/PayloadSafety.kt`
- `app/src/main/java/com/example/ui/wizard/MetalIdWizard.kt`
- `app/src/main/java/com/example/ui/yardfinder/YardFinderScreen.kt`
- `app/src/main/java/com/example/domain/engine/ProfitCalculationEngine.kt`
- `app/src/main/java/com/example/domain/engine/WireStrippingRoiEngine.kt`
- `app/src/main/java/com/example/domain/engine/PayloadSafetyEngine.kt`
- `preview/index.html`, `preview/styles.css`, and `preview/app.js`
- `scripts/verify_project.py`

The exact seven Android drawable mappings are `scrap_icon.png`, `scrap_main.png`, `id_wizard.png`, `load_calc.png`, `load_bal.png`, `price_track.png`, and `scrap_calc.png`.

## SECTION 3: PRIORITIZED ACTIONABLE NEXT STEPS

1. Run `python3 scripts/verify_project.py`; it must pass.
2. Run `npm install && npm run build`; confirm Vite completes without warnings.
3. With Android SDK access, run `./gradlew test assembleDebug` under JDK 17.
4. Install the debug APK and manually traverse all six tabs twice, confirming scroll/input state restoration.
5. Test yard calls and directions on one device with Google Maps installed and one without it.
6. Enter empty, zero, negative, and large values in all calculators and confirm every result remains finite.
7. Capture final device screenshots at compact and large font scales before release signing.
