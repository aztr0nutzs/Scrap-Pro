#!/usr/bin/env python3
"""Deterministic structural acceptance checks for ScrapPro's Android and web layers."""

from pathlib import Path
import re
import struct
import sys

ROOT = Path(__file__).resolve().parents[1]
ANDROID = ROOT / "app/src/main"
NAV = (ANDROID / "java/com/example/ui/navigation/AppNavigation.kt").read_text()
SCREEN = (ANDROID / "java/com/example/ui/navigation/Screen.kt").read_text()
KOTLIN = "\n".join(p.read_text(errors="replace") for p in (ANDROID / "java").rglob("*.kt"))
BUILD_TEXT = "\n".join(p.read_text(errors="replace") for p in [ROOT / "build.gradle.kts", ROOT / "app/build.gradle.kts", ROOT / "gradle/libs.versions.toml", ANDROID / "AndroidManifest.xml", ROOT / ".env.example"])
HTML = (ROOT / "preview/index.html").read_text()
CSS = (ROOT / "preview/styles.css").read_text()

errors: list[str] = []

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

assets = ("scrap_icon", "scrap_main", "id_wizard", "load_calc", "load_bal", "price_track", "scrap_calc")
for name in assets:
    path = ANDROID / f"res/drawable/{name}.png"
    if not path.is_file():
        errors.append(f"Missing drawable: {name}.png")
        continue
    with path.open("rb") as handle:
        signature = handle.read(24)
    if signature[:8] != b"\x89PNG\r\n\x1a\n":
        errors.append(f"Drawable is not PNG: {name}.png")
    elif name != "scrap_icon":
        width, height = struct.unpack(">II", signature[16:24])
        if width <= height:
            errors.append(f"Banner is not landscape: {name}.png ({width}x{height})")

if "setPackage(\"com.google.android.apps.maps\")" not in KOTLIN or "geo:0,0?q=" not in KOTLIN:
    errors.append("Native Google Maps intent contract is incomplete")
if "ACTION_DIAL" not in KOTLIN or "NAVIGATE NOW" not in KOTLIN:
    errors.append("Yard call/navigation actions are incomplete")

if errors:
    print("ScrapPro verification FAILED")
    for error in errors:
        print(f"- {error}")
    sys.exit(1)

print("ScrapPro verification PASSED")
print("- 6 Android routes and 6 web tabs")
print("- state-saving navigation contract")
print("- 7 exact PNG resources")
print("- contain-only banner rendering")
print("- no Maps SDK, secrets plugin, map key, Coil, crop, or title overlay")
print("- native dialer and Google Maps intent actions")
