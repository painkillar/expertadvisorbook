# How to Build the Forex Trading Android APK

Since building Android APKs requires the Android SDK and development tools, here are your options to build the app:

## Option 1: Build with Android Studio (Recommended)

### Prerequisites
1. Download and install [Android Studio](https://developer.android.com/studio)
2. Install Java JDK 17 or later

### Steps
1. **Open the project:**
   ```bash
   # Clone or navigate to the project
   cd forex-trading-app
   ```

2. **Open in Android Studio:**
   - Launch Android Studio
   - Click "Open" or "Open an existing project"
   - Navigate to `forex-trading-app` folder and select it
   - Wait for Gradle sync to complete

3. **Build Debug APK:**
   - In Android Studio, go to: `Build` → `Build Bundle(s) / APK(s)` → `Build APK(s)`
   - Or use terminal in Android Studio:
     ```bash
     ./gradlew assembleDebug
     ```
   - APK location: `app/build/outputs/apk/debug/app-debug.apk`

4. **Build Release APK (for distribution):**
   ```bash
   ./gradlew assembleRelease
   ```
   - APK location: `app/build/outputs/apk/release/app-release-unsigned.apk`

## Option 2: Build from Command Line

### Prerequisites
1. Install Android SDK Command Line Tools
2. Set up environment variables:
   ```bash
   export ANDROID_HOME=/path/to/android/sdk
   export PATH=$PATH:$ANDROID_HOME/tools:$ANDROID_HOME/platform-tools
   ```

### Steps
1. **Accept SDK licenses:**
   ```bash
   sdkmanager --licenses
   ```

2. **Install required SDK components:**
   ```bash
   sdkmanager "platform-tools" "platforms;android-34" "build-tools;34.0.0"
   ```

3. **Build the APK:**
   ```bash
   cd forex-trading-app
   chmod +x gradlew
   ./gradlew assembleDebug
   ```

4. **Find your APK:**
   ```bash
   ls app/build/outputs/apk/debug/
   # app-debug.apk will be there
   ```

## Option 3: Use GitHub Actions (Automated Build)

Create `.github/workflows/build.yml` in your repository:

```yaml
name: Build APK

on:
  push:
    branches: [ main, master ]
  pull_request:
    branches: [ main, master ]

jobs:
  build:
    runs-on: ubuntu-latest

    steps:
    - uses: actions/checkout@v3

    - name: Set up JDK 17
      uses: actions/setup-java@v3
      with:
        java-version: '17'
        distribution: 'temurin'

    - name: Grant execute permission for gradlew
      run: chmod +x gradlew
      working-directory: ./forex-trading-app

    - name: Build with Gradle
      run: ./gradlew assembleDebug
      working-directory: ./forex-trading-app

    - name: Upload APK
      uses: actions/upload-artifact@v3
      with:
        name: app-debug
        path: forex-trading-app/app/build/outputs/apk/debug/app-debug.apk
```

Push this file to GitHub, and the APK will be automatically built and available in the Actions tab.

## Option 4: Quick Build Script (Linux/Mac)

Create a file named `build.sh`:

```bash
#!/bin/bash

# Navigate to project directory
cd forex-trading-app

# Make gradlew executable
chmod +x gradlew

# Build debug APK
./gradlew assembleDebug

# Copy APK to current directory
cp app/build/outputs/apk/debug/app-debug.apk ../forex-trading-app-debug.apk

echo "APK built successfully: forex-trading-app-debug.apk"
```

Make it executable and run:
```bash
chmod +x build.sh
./build.sh
```

## Signing the Release APK (for Production)

### 1. Generate a keystore:
```bash
keytool -genkey -v -keystore my-release-key.jks -keyalg RSA -keysize 2048 -validity 10000 -alias my-key-alias
```

### 2. Create `keystore.properties` in the project root:
```properties
storePassword=YourStorePassword
keyPassword=YourKeyPassword
keyAlias=my-key-alias
storeFile=/path/to/my-release-key.jks
```

### 3. Update `app/build.gradle` to add signing config:
```gradle
android {
    ...
    signingConfigs {
        release {
            def keystorePropertiesFile = rootProject.file("keystore.properties")
            def keystoreProperties = new Properties()
            keystoreProperties.load(new FileInputStream(keystorePropertiesFile))

            keyAlias keystoreProperties['keyAlias']
            keyPassword keystoreProperties['keyPassword']
            storeFile file(keystoreProperties['storeFile'])
            storePassword keystoreProperties['storePassword']
        }
    }
    buildTypes {
        release {
            signingConfig signingConfigs.release
            minifyEnabled true
            ...
        }
    }
}
```

### 4. Build signed release APK:
```bash
./gradlew assembleRelease
```

## Troubleshooting

### Build fails with "SDK location not found"
Create `local.properties` file with:
```properties
sdk.dir=/path/to/your/android/sdk
```

### Out of memory errors
Add to `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096m -XX:MaxPermSize=512m
```

### Dependency resolution issues
1. Check your internet connection
2. Clear Gradle cache:
   ```bash
   rm -rf ~/.gradle/caches/
   ./gradlew --refresh-dependencies
   ```

### Build tools not found
Install required build tools:
```bash
sdkmanager "build-tools;34.0.0"
```

## APK Output Locations

- **Debug APK**: `app/build/outputs/apk/debug/app-debug.apk`
- **Release APK**: `app/build/outputs/apk/release/app-release.apk`

## Installing the APK

### On Physical Device:
1. Enable "Unknown Sources" in Settings → Security
2. Transfer APK to device
3. Tap the APK file to install

### Using ADB:
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

## File Size

Expected APK size:
- Debug: ~15-20 MB
- Release (with ProGuard): ~8-12 MB

---

**Note**: The app currently uses mock data. Before building for production, integrate with real forex data APIs as described in the main README.md file.
