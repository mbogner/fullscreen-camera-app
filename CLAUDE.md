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

**Pattern:** MVVM with Jetpack Compose + Compose Navigation

**Navigation flow:** `StartScreen` (camera lens picker) → `CameraScreen/{lensFacing}` (fullscreen preview)

**Key source paths** (under `app/src/main/java/dev/mbo/androidcamera/`):
- `MainActivity.kt` — entry point, handles camera permission requests
- `ui/NavigationHost.kt` — Compose navigation graph setup
- `ui/NavigationTargets.kt` — route constants
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

- **SDK:** compileSdk 36, minSdk 30, targetSdk 36
- **Java:** 17 (Kotlin 2.3)
- **Compose BOM:** 2026.05.00
- **CameraX:** 1.6.1
- **Release build:** R8 minification + resource shrinking enabled
- **Signing:** uses `android_key.jks` with credentials in separate `.alias`, `.key_password`, `.store_password` files (not committed)
