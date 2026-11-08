# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Fullscreen USB StreamCam — a minimal Android app (`dev.mbo.androidcamera`) that displays the device camera in fullscreen at maximum resolution, designed for use with scrcpy to stream the camera feed over USB. Supports back/front camera selection.

## Build & Development Commands

```bash
# Build (from project root, not fastlane/)
cd /path/to/AndroidCamera
./gradlew build

# Run unit tests
./gradlew test
# Or via fastlane:
fastlane android test

# Assemble debug APK
./gradlew assembleDebug

# Release workflow (fastlane lanes)
fastlane android prepare_minor_release   # bump minor version + changelog
fastlane android prepare_major_release   # bump major version + changelog
fastlane android prepare_hotfix_release  # bump hotfix version + changelog
fastlane android tag                     # git tag + push
fastlane android deploy                  # build signed AAB + upload to Play Store
```

## Architecture

**Pattern:** MVVM with Jetpack Compose + Navigation 3

**Navigation flow:** `Start` (camera lens picker) → `Camera(lensFacing, …)` (fullscreen `CameraXViewfinder` preview); keys are `@Serializable` `NavKey`s

**Orientation:** `NavigationHost` locks the menu to portrait; the camera screen follows the device rotation. `CameraScreen` pushes display rotation into `Preview.targetRotation`, which `CameraXViewfinder` needs because the activity handles orientation changes itself.

**Key source paths** (under `app/src/main/java/dev/mbo/androidcamera/`):
- `MainActivity.kt` — entry point, handles camera permission requests
- `ui/NavigationHost.kt` — Navigation 3 `NavDisplay` setup
- `ui/NavigationTargets.kt` — `NavKey` route types
- `ui/screens/StartScreen.kt`, `CameraScreen.kt` — the two screens
- `ui/viewmodels/StartViewModel.kt`, `CameraViewModel.kt` — state management
- `utils/CameraSizeUtil.kt` — max resolution detection

## Fastlane

The `fastlane/` directory contains deployment automation. Key files:
- `Fastfile` — lane definitions with helpers for version bumping, changelog generation, and Play Store upload
- `Appfile` — package name and Play Store API key path
- `metadata/android/en-US/` — Play Store listing metadata (title, descriptions, changelogs, screenshots)

Version management: `versionCode` is fetched from Play Store API and incremented; `versionName` follows semver (MAJOR.MINOR.HOTFIX) and is parsed/incremented in `Fastfile` helpers.

## Build Configuration

- **SDK:** compileSdk 37, minSdk 30, targetSdk 37
- **Java:** 17 (Kotlin 2.4)
- **Compose BOM:** 2026.09.00
- **CameraX:** 1.6.2 (`camera-compose` viewfinder)
- **Navigation 3:** 1.2.1
- **Release build:** R8 minification + resource shrinking enabled
- **Signing:** uses `android_key.jks` with credentials in separate `.alias`, `.key_password`, `.store_password` files (not committed)
