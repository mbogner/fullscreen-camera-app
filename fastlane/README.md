# Fastlane Release Guide

## Prerequisites

- Ruby (managed via system or rbenv)
- Bundler: `gem install bundler`
- Dependencies: `bundle install` (run from project root)
- Signing key files in project root: `android_key.jks`, `.alias`, `.key_password`, `.store_password`
- Google Play service account key: `fastlane/keyfile.json`
- For screenshots: a connected Android device or running emulator

All fastlane commands **must** be run from the `fastlane/` directory using `bundle exec`.

## Release Workflow

### 1. Run Tests

```sh
bundle exec fastlane android test
```

### 2. Capture Screenshots (requires connected device)

Builds the debug APK and test APK, then runs the screenshot tests on the connected device:

```sh
bundle exec fastlane android screenshots
```

Screenshots are saved to `fastlane/metadata/android/en-US/images/` and will be uploaded with the next deploy.

### 3. Prepare Release

Fetches the current version from Google Play, increments it, and generates the changelog from git commits.

```sh
# Bump hotfix (1.3.0 → 1.3.1)
bundle exec fastlane android prepare_hotfix_release

# Bump minor (1.3.0 → 1.4.0)
bundle exec fastlane android prepare_minor_release

# Bump major (1.3.0 → 2.0.0)
bundle exec fastlane android prepare_major_release
```

This updates `versionCode` and `versionName` in `app/build.gradle.kts` and writes changelogs to both `changelog.md` and `fastlane/metadata/android/en-US/changelogs/`.

### 4. Commit, Tag, and Push

Commit the version bump and changelog, then tag:

```sh
git add -A && git commit -m "prepare release X.Y.Z"
bundle exec fastlane android tag
```

### 5. Deploy to Google Play

Builds the signed release AAB and uploads it (with metadata and screenshots) to Google Play:

```sh
bundle exec fastlane android deploy
```

## Updating Fastlane

Fastlane version is pinned in `Gemfile`. To update:

```sh
bundle update fastlane
```
