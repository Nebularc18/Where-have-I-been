# Where Have I Been

Where Have I Been is an offline-first Android app for tracking the countries you have visited and the places you still want to go. It ships with a local catalog of 195 countries, stores progress on-device with Room, and presents the data through a world map, searchable lists, and progress statistics.

## Features

- Interactive world map with visited countries highlighted
- Searchable country list by name, ISO code, capital, or continent
- Wishlist screen for countries you want to visit later
- Travel stats screen with total progress and continent-by-continent breakdowns
- Offline local data seeding from `app/src/main/assets/countries.json`
- Persistent storage with Room so visited and wishlist state survives app restarts

## Tech Stack

- Kotlin
- Jetpack Compose
- Material 3
- Navigation Compose
- Room
- Kotlin Coroutines and Flow
- Kotlinx Serialization
- `bota` world map library

## Requirements

- Android Studio with Android SDK 35 installed
- JDK 17
- Android device or emulator running Android 7.0 (API 24) or newer

## Getting Started

1. Clone the repository.
2. Open the project in Android Studio.
3. Let Gradle sync and install any missing SDK components.
4. Run the `app` configuration on an emulator or physical device.

## Build and Run from the Command Line

On Windows:

```powershell
.\gradlew.bat assembleDebug
```

On macOS/Linux:

```bash
./gradlew assembleDebug
```

To install on a connected device or emulator:

```powershell
.\gradlew.bat installDebug
```

## Testing

Run unit tests:

```powershell
.\gradlew.bat testDebugUnitTest
```

Run instrumented tests on a connected emulator/device:

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

The project already includes tests for:

- JSON country seed parsing
- Domain model search and travel stat calculations
- Room database behavior
- Repository behavior
- Navigation/UI instrumentation

## Project Structure

```text
app/src/main/java/com/hampu/wherehaveibeen
|- data/local        # Room entities, DAO, database, asset loading
|- data/repository   # Offline-first repository and dependency container
|- domain/model      # Country and travel statistics models
|- navigation        # Top-level destinations
|- ui                # Compose screens, components, theme, and view models
```

## Data Model

Each country record includes:

- ISO code
- Country name
- Continent
- Flag emoji
- Capital city
- Visited state
- Wishlist state

The app seeds the database from the bundled JSON asset on first launch and then reads and writes only from the local database.

## Notes

- There are no external API dependencies for country data.
- The app database file is stored locally as `where_have_i_been.db`.
- Room schema exports are checked into `app/schemas` for migration tracking.

## License

This project is licensed under the MIT License. See [LICENSE](LICENSE).
