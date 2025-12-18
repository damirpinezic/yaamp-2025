# YAAMP - Yet Another Android MP3 Player

A modern, fully-featured Android music player built with the latest Android development best practices.

## Overview

YAAMP has been completely modernized from its 2012 legacy codebase to a contemporary 2025 Android application. The app follows current best practices and implements modern Android architecture patterns.

## Features

### Core Features
- 🎵 Play audio files from device storage
- ⏯️ Full playback controls (play, pause, stop, next, previous)
- 🔀 Shuffle mode
- 🔁 Repeat modes (off, all, one)
- 📱 Background playback with foreground service
- 🔔 Media notification with controls
- 🔒 Lock screen media controls
- 🎨 Album art display
- 🔍 Search functionality
- ❤️ Favorite/liked songs
- 📋 Playlist management

### Modern Android Features
- 🎨 Material Design 3 (Material You)
- 🌓 Light and dark theme support
- 📱 Android 14 (API 34) target support
- 🔐 Modern runtime permissions handling
- 💾 Scoped storage compliance
- 🚀 Splash Screen API

## Technical Stack

### Architecture
- **MVVM** (Model-View-ViewModel) architecture
- **Clean Architecture** with clear separation of layers:
  - Data Layer (models, repositories, DAOs)
  - Domain Layer (business logic)
  - Presentation Layer (UI, ViewModels)

### Key Technologies

#### Language & Build System
- **Kotlin** - 100% Kotlin codebase
- **Gradle** with Kotlin DSL
- **Android Gradle Plugin 8.3.2**
- Target SDK: API 34 (Android 14)
- Minimum SDK: API 26 (Android 8.0)

#### Android Jetpack Components
- **ViewModel** - Lifecycle-aware business logic
- **LiveData** & **StateFlow** - Observable data holders
- **Room Database** - Local data persistence
- **Navigation Component** - Screen navigation
- **WorkManager** - Background tasks
- **ViewBinding** - Type-safe view access
- **Splash Screen API** - Modern app startup

#### Media Framework
- **AndroidX Media3 (ExoPlayer)** - Modern audio playback
- **MediaSession** - Media controls and integration
- **Foreground Service** - Background playback

#### Dependency Injection
- **Hilt** - Dependency injection framework

#### Asynchronous Programming
- **Kotlin Coroutines** - Async operations
- **Kotlin Flow** - Reactive data streams

#### UI & Design
- **Material Design 3** - Modern UI components
- **Coil** - Image loading for album art
- **RecyclerView** - Efficient list display

#### Logging & Debugging
- **Timber** - Logging framework

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/g3ck0/yaamp/
│   │   │   ├── YaampApplication.kt
│   │   │   ├── data/
│   │   │   │   ├── local/
│   │   │   │   │   ├── SongDao.kt
│   │   │   │   │   ├── PlaylistDao.kt
│   │   │   │   │   └── YaampDatabase.kt
│   │   │   │   ├── model/
│   │   │   │   │   ├── Song.kt
│   │   │   │   │   ├── Playlist.kt
│   │   │   │   │   └── PlaylistSongCrossRef.kt
│   │   │   │   └── repository/
│   │   │   │       ├── SongRepository.kt
│   │   │   │       └── PlaylistRepository.kt
│   │   │   ├── di/
│   │   │   │   └── DatabaseModule.kt
│   │   │   ├── media/
│   │   │   │   ├── PlaybackService.kt
│   │   │   │   └── PlaybackManager.kt
│   │   │   ├── presentation/
│   │   │   │   ├── main/
│   │   │   │   │   └── MainActivity.kt
│   │   │   │   ├── library/
│   │   │   │   │   ├── LibraryViewModel.kt
│   │   │   │   │   └── SongAdapter.kt
│   │   │   │   └── nowplaying/
│   │   │   │       └── NowPlayingViewModel.kt
│   │   │   └── util/
│   │   │       ├── PermissionUtils.kt
│   │   │       └── PlaybackState.kt
│   │   ├── res/
│   │   │   ├── drawable/      # Vector icons
│   │   │   ├── layout/        # UI layouts
│   │   │   ├── mipmap-*/      # App icons
│   │   │   ├── values/        # Themes, colors, strings
│   │   │   └── xml/           # Backup rules
│   │   └── AndroidManifest.xml
│   └── test/                  # Unit tests
└── build.gradle.kts
```

## Permissions

The app requests the following permissions:

### Android 13+ (API 33+)
- `READ_MEDIA_AUDIO` - Access audio files
- `POST_NOTIFICATIONS` - Display playback notifications

### Android 12 and below (API 32-)
- `READ_EXTERNAL_STORAGE` - Access audio files

### All Versions
- `FOREGROUND_SERVICE` - Background playback
- `FOREGROUND_SERVICE_MEDIA_PLAYBACK` - Media playback service
- `WAKE_LOCK` - Keep playback active

## Building the Project

### Prerequisites
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK with API 34
- Gradle 8.2+

### Build Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/damirpinezic/yaamp-2025.git
   cd yaamp-2025
   ```

2. **Open in Android Studio**
   - Launch Android Studio
   - Select "Open an Existing Project"
   - Navigate to the cloned directory
   - Wait for Gradle sync to complete

3. **Build the app**
   ```bash
   ./gradlew build
   ```

4. **Run on device/emulator**
   ```bash
   ./gradlew installDebug
   ```
   Or use the "Run" button in Android Studio

### Build Variants
- **debug** - Debug build with debug symbols
- **release** - Optimized release build with ProGuard/R8

## Installation

### From Source
1. Build the project using instructions above
2. The APK will be generated in `app/build/outputs/apk/`
3. Install on device: `adb install app/build/outputs/apk/debug/app-debug.apk`

### Requirements
- Android 8.0 (API 26) or higher
- At least 20 MB of free storage space
- Permissions for accessing media files and posting notifications

## Usage

1. **Grant Permissions**
   - On first launch, grant audio file access permission
   - Grant notification permission (Android 13+) for playback controls

2. **Browse Library**
   - The app automatically scans your device for audio files
   - Browse your music library in the main screen
   - Use the search feature to find specific songs

3. **Playback**
   - Tap any song to play it
   - Use the playback notification for controls
   - Control playback from lock screen
   - Toggle shuffle and repeat modes

4. **Favorites**
   - Tap the heart icon to add songs to favorites
   - Access favorites from the favorites section

## Architecture Details

### Data Flow
```
UI (Activity/Fragment)
  ↓
ViewModel
  ↓
Repository
  ↓
Data Sources (Room Database, MediaStore API)
```

### Media Playback Flow
```
PlaybackManager → MediaController → PlaybackService → Media3 ExoPlayer
```

## Configuration

### ProGuard/R8 Rules
The app includes comprehensive ProGuard rules for:
- Room Database entities
- Hilt dependency injection
- Media3 components
- Kotlin coroutines
- Timber logging

### Gradle Properties
Build optimizations include:
- Parallel execution
- Configuration cache
- AndroidX namespace
- Build cache

## Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumentation Tests
```bash
./gradlew connectedAndroidTest
```

## Known Limitations

- No playlist editing UI (data model and repository implemented)
- No equalizer functionality
- No sleep timer
- No recently played tracking
- Basic UI without now playing full screen

## Future Enhancements

- [ ] Full now playing screen with seek bar
- [ ] Playlist management UI
- [ ] Recently played tracks
- [ ] Sleep timer
- [ ] Audio equalizer
- [ ] Album and artist views
- [ ] Material You dynamic colors
- [ ] Widgets for home screen
- [ ] Android Auto support
- [ ] Wear OS companion app

## Contributing

Contributions are welcome! Please follow these guidelines:

1. Fork the repository
2. Create a feature branch
3. Follow Kotlin coding conventions
4. Write tests for new features
5. Submit a pull request

## License

This project maintains the original YAAMP spirit of being open and accessible.

## Credits

- **Original YAAMP** - Legacy codebase from 2012
- **Modernization** - Complete rewrite with modern Android practices (2025)

## Support

For issues, questions, or suggestions:
- Open an issue on GitHub
- Check existing issues for solutions

## Changelog

### Version 2.0 (2025)
- Complete modernization to current Android standards
- Migrated from Java to Kotlin
- Implemented MVVM architecture
- Added Material Design 3
- Integrated Media3 (ExoPlayer)
- Added Hilt dependency injection
- Implemented Room database
- Added modern permission handling
- Updated to API 34 target
- Added background playback support
- Implemented notification controls

### Version 1.0 (2012)
- Original YAAMP release
- Basic MP3 playback functionality

---

**Built with ❤️ using modern Android development practices**
