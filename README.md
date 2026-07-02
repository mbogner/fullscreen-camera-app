# Fullscreen Camera

Very simple Android app that start with max resolution of the main camera in fullscreen mode.

This can then be used to stream with [srccpy](https://github.com/Genymobile/scrcpy).

See the [stream.sh](stream.sh) file for a sample to start srccpy. The cool thing is that you can not
just stream the image with very small delay, you can also remotely control your phone.

In my `stream.sh` file I use `-s <deviceId>` because I have multiple devices connected. You get the
id by running

```shell
adb devices
```

If you only have one device connected you can skip that `-s` parameter completely.

Make sure to have debugging enabled on your phone. For a tutorial how to do so, please lookup a
tutorial in the internet for your specific phone.

## Build & Test

All commands run from the project root via the Gradle wrapper (no local Gradle install needed).

Build:

```shell
# Compile + assemble the debug APK
./gradlew assembleDebug

# Assemble the minified (R8) release APK
./gradlew assembleRelease

# Full build (compile, assemble, checks)
./gradlew build
```

Unit tests (JVM, no device required):

```shell
# Run all unit tests
./gradlew testDebugUnitTest

# Run a single test class
./gradlew testDebugUnitTest --tests "dev.mbo.androidcamera.ui.viewmodels.CameraFilterTest"
```

Note: use the `testDebugUnitTest` task, not `test`. The aggregate `test` task does not accept the
`--tests` filter.

Instrumented tests (require a connected device or running emulator):

```shell
# Compile the instrumented test APK without a device
./gradlew assembleDebugAndroidTest

# Run instrumented tests on a connected device/emulator
./gradlew connectedDebugAndroidTest
```

Lint:

```shell
./gradlew lintDebug   # report at app/build/reports/lint-results-debug.html
```

## Play Store

https://play.google.com/store/apps/details?id=dev.mbo.androidcamera