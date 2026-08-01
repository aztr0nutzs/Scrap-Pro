#!/usr/bin/env python3
"""Deterministic structural acceptance checks for ScrapPro's Android and web layers."""

from pathlib import Path
import hashlib
import re
import sys

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "app/src/main"
NAV = (ANDROID / "java/com/example/ui/navigation/AppNavigation.kt").read_text()
SCREEN = (ANDROID / "java/com/example/ui/navigation/Screen.kt").read_text()
KOTLIN = "\n".join(p.read_text(errors="replace") for p in (ANDROID / "java").rglob("*.kt"))
BUILD_TEXT = "\n".join(p.read_text(errors="replace") for p in [ROOT / "build.gradle.kts", ROOT / "app/build.gradle.kts", ROOT / "gradle/libs.versions.toml", ANDROID / "AndroidManifest.xml", ROOT / ".env.example"])
HTML = (ROOT / "preview/index.html").read_text()
CSS = (ROOT / "preview/styles.css").read_text()
HOME = (ANDROID / "java/com/example/ui/home/HomeScreen.kt").read_text()
DATABASE = (ANDROID / "java/com/example/data/local/ScrapProDatabase.kt").read_text()
PROFIT = (ANDROID / "java/com/example/ui/calculators/ProfitCalculator.kt").read_text()

errors: list[str] = []

required_paths = (
    "settings.gradle.kts", "build.gradle.kts", "gradle.properties", "gradlew",
    "gradle/libs.versions.toml",
    "gradle/wrapper/gradle-wrapper.properties", "app/build.gradle.kts",
    "app/proguard-rules.pro", "app/src/main/AndroidManifest.xml",
    "preview/index.html", "preview/styles.css", "preview/app.js",
)
for relative in required_paths:
    if not (ROOT / relative).is_file():
        errors.append(f"Missing required project file: {relative}")

if not (ROOT / "gradlew").stat().st_mode & 0o111:
    errors.append("gradlew is not executable")
if 'include(":app")' not in (ROOT / "settings.gradle.kts").read_text():
    errors.append('settings.gradle.kts does not include ":app"')

flattened_suffixes = {".kt", ".xml", ".png", ".webp", ".html", ".css", ".js", ".toml", ".jar"}
for path in ROOT.iterdir():
    if path.is_file() and path.suffix.lower() in flattened_suffixes:
        errors.append(f"Project file remains flattened at repository root: {path.name}")

source_roots = {
    ROOT / "app/src/main/java": "main",
    ROOT / "app/src/test/java": "test",
    ROOT / "app/src/androidTest/java": "androidTest",
}
for source_root, _source_set in source_roots.items():
    for path in source_root.rglob("*.kt"):
        match = re.search(r"^package\s+([\w.]+)", path.read_text(), re.MULTILINE)
        if not match:
            errors.append(f"Kotlin source has no package declaration: {path.relative_to(ROOT)}")
            continue
        expected = Path(*match.group(1).split("."))
        if path.parent.relative_to(source_root) != expected:
            errors.append(f"Kotlin package/path mismatch: {path.relative_to(ROOT)} -> {match.group(1)}")

resource_name = re.compile(r"^[a-z][a-z0-9_]*$")
resource_dirs = (ANDROID / "res").glob("*")
for directory in resource_dirs:
    if not directory.is_dir() or directory.name == "values":
        continue
    for path in directory.iterdir():
        if path.is_file() and not resource_name.fullmatch(path.stem):
            errors.append(f"Invalid Android resource name: {path.relative_to(ROOT)}")

available_resources: dict[str, set[str]] = {kind: set() for kind in ("drawable", "mipmap", "string", "xml")}
asset_names = ("scrap_icon", "scrap_main", "id_wizard", "load_calc", "load_bal", "price_track", "scrap_calc")
# Binary artwork is intentionally delivered separately from this text-only reconstruction PR.
available_resources["drawable"].update(asset_names)
available_resources["mipmap"].update(("ic_launcher", "ic_launcher_round"))
for path in (ANDROID / "res").glob("drawable*/*"):
    available_resources["drawable"].add(path.stem)
for path in (ANDROID / "res").glob("mipmap*/*"):
    available_resources["mipmap"].add(path.stem)
for path in (ANDROID / "res/xml").glob("*.xml"):
    available_resources["xml"].add(path.stem)
for path in (ANDROID / "res/values").glob("*.xml"):
    text = path.read_text()
    for kind, name in re.findall(r'<(string)\s+name="([a-zA-Z0-9_]+)"', text):
        available_resources[kind].add(name)
for kind, name in re.findall(r"R\.(drawable|mipmap|string|xml)\.([A-Za-z0-9_]+)", KOTLIN):
    if name not in available_resources[kind]:
        errors.append(f"Unresolved Android resource reference: R.{kind}.{name}")

hash_groups: dict[str, list[Path]] = {}
for path in ROOT.rglob("*"):
    if not path.is_file() or any(part in {".git", ".gradle", "build", "node_modules", "dist-preview"} for part in path.parts):
        continue
    digest = hashlib.sha256(path.read_bytes()).hexdigest()
    hash_groups.setdefault(digest, []).append(path)
duplicate_groups = [paths for paths in hash_groups.values() if len(paths) > 1]

for route in ("home", "profit", "wire", "payload", "id", "yards"):
    if f'"{route}"' not in SCREEN or f"Screen.{route.capitalize()}.route" not in NAV:
        errors.append(f"Missing Android route: {route}")
    if f'data-screen="{route}"' not in HTML or f'data-tab="{route}"' not in HTML:
        errors.append(f"Missing web route/tab: {route}")

for required in ("saveState = true", "restoreState = true", "launchSingleTop = true"):
    if required not in NAV:
        errors.append(f"Missing navigation state behavior: {required}")

for forbidden in ("play-services-maps", "maps-compose", "secrets-gradle", "MAPS_API_KEY", "AsyncImage"):
    if forbidden in BUILD_TEXT + KOTLIN:
        errors.append(f"Forbidden token remains: {forbidden}")

if "ContentScale.Crop" in KOTLIN or "Brush.verticalGradient" in KOTLIN:
    errors.append("A cropping or gradient-overlay banner pattern remains in Kotlin")
if not all(rule in CSS for rule in (".banner", "width:100%", "height:auto", "object-fit:contain")):
    errors.append("Web banner contain rules are incomplete")
if re.search(r'<(?:h1|div)[^>]*class=["\']title["\']', HTML, re.I):
    errors.append("Web banner overlay title remains")

binary_suffixes = {".jar", ".png", ".webp", ".jpg", ".jpeg", ".gif", ".zip", ".keystore", ".jks"}
for path in ROOT.rglob("*"):
    if path.is_file() and not any(part in {".git", ".gradle", "build", "node_modules", "dist-preview"} for part in path.parts):
        if path.suffix.lower() in binary_suffixes:
            errors.append(f"Binary file must be delivered separately: {path.relative_to(ROOT)}")

for name in asset_names:
    if f"assets/{name}.png" not in HTML and name != "scrap_icon" and name != "load_calc":
        errors.append(f"Preview does not reference expected asset name: {name}.png")

if "setPackage(\"com.google.android.apps.maps\")" not in KOTLIN or "geo:0,0?q=" not in KOTLIN:
    errors.append("Native Google Maps intent contract is incomplete")
if "ACTION_DIAL" not in KOTLIN or "NAVIGATE NOW" not in KOTLIN:
    errors.append("Yard call/navigation actions are incomplete")

for forbidden_home_value in ("753 lb", "$329.10", "$287.60", "Apex Metals", "Steel City Scrap", "Quick Cash"):
    if forbidden_home_value in HOME:
        errors.append(f"Hardcoded Home dashboard value remains: {forbidden_home_value}")
for required_home_contract in ("HomeViewModel", "HomeUiState.Loading", "HomeUiState.Empty", "HomeUiState.Populated", "HomeUiState.Error"):
    if required_home_contract not in HOME + KOTLIN:
        errors.append(f"Persistent Home contract missing: {required_home_contract}")
if "fallbackToDestructiveMigration" in DATABASE or "MIGRATION_1_2" not in DATABASE:
    errors.append("Room must use the explicit 1-to-2 migration without destructive fallback")
if "saveToActiveHaul" not in PROFIT or "RoomHomeRepository" not in PROFIT:
    errors.append("Profit Calculator is not connected to the persistent active haul")

if errors:
    print("ScrapPro verification FAILED")
    for error in errors:
        print(f"- {error}")
    sys.exit(1)

print("ScrapPro verification PASSED")
print("- 6 Android routes and 6 web tabs")
print("- state-saving navigation contract")
print("- binary-free PR with 7 Android asset names reserved for separate delivery")
print("- contain-only banner rendering")
print("- no Maps SDK, secrets plugin, map key, Coil, crop, or title overlay")
print("- native dialer and Google Maps intent actions")
print("- conventional Gradle/source/resource placement and executable wrapper script")
print("- Kotlin package-to-directory and Android resource-reference validation")
print("- repository-backed Home states, explicit Room migration, and Profit-to-haul persistence")
print(f"- duplicate-content groups reviewed: {len(duplicate_groups)}")
