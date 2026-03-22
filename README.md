# ChargeScope Pixel

ChargeScope Pixel is a Kotlin Android charging monitor built with Jetpack Compose and Material 3.
It detects charger plug/unplug events, records charging sessions, samples battery telemetry every 10 seconds, and provides health/insight dashboards with CSV export.

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Room
- DataStore
- MVVM
- Foreground Service + BroadcastReceiver

## Build Debug APK

```bash
./gradlew assembleDebug
```

Debug APK output:

`app/build/outputs/apk/debug/app-debug.apk`

## Release APK Signing Instructions

1. Create a keystore:

```bash
keytool -genkeypair -v \
  -keystore chargescopixel-release.jks \
  -alias chargescopixel \
  -keyalg RSA -keysize 2048 -validity 10000
```

2. Add signing config in `app/build.gradle.kts` (inside `android { signingConfigs { ... } buildTypes { release { ... } } }`):

```kotlin
signingConfigs {
    create("release") {
        storeFile = file("../chargescopixel-release.jks")
        storePassword = System.getenv("CHARGESCOPE_STORE_PASSWORD")
        keyAlias = "chargescopixel"
        keyPassword = System.getenv("CHARGESCOPE_KEY_PASSWORD")
    }
}

buildTypes {
    release {
        isMinifyEnabled = true
        signingConfig = signingConfigs.getByName("release")
        proguardFiles(
            getDefaultProguardFile("proguard-android-optimize.txt"),
            "proguard-rules.pro"
        )
    }
}
```

3. Export passwords in your shell:

```bash
export CHARGESCOPE_STORE_PASSWORD="your_store_password"
export CHARGESCOPE_KEY_PASSWORD="your_key_password"
```

4. Build signed release APK:

```bash
./gradlew assembleRelease
```

Release APK output:

`app/build/outputs/apk/release/app-release.apk`

## Notes

- Target: Pixel phones first (especially Pixel 10)
- Minimum Android version: Android 11 (API 30)
- If current-now or charge-counter data is unavailable, the app displays: "Not supported on this device".
