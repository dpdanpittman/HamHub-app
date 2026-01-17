# HamHub

A native Android app for amateur radio operators to log contacts, track awards, and access ham radio tools.

## Features

- **QSO Logbook** - Log and manage your contacts with full ADIF field support
- **Dashboard** - View statistics and charts of your activity
- **Awards Tracking** - Track progress toward DXCC, WAS, and grid square awards
- **QSO Map** - Visualize your contacts on a world map
- **Propagation Data** - Solar flux, A/K indices, and band conditions
- **ISS Tracker** - Real-time ISS position and pass predictions
- **Repeater Search** - Find nearby repeaters via RepeaterBook
- **Callsign Lookup** - Look up license info via Callook.info
- **Band Plan Reference** - Quick reference for frequency allocations
- **ADIF Import/Export** - Backup and transfer your log data

## Requirements

- Android 8.0 (API 26) or higher
- Internet connection for live data features (propagation, ISS, repeaters, callsign lookup)
- Core logging features work fully offline

## Tech Stack

| Component | Technology |
|-----------|------------|
| Language | Kotlin |
| UI | Jetpack Compose |
| Database | Room (SQLite) |
| Architecture | MVVM + Repository |
| DI | Hilt |
| Navigation | Navigation Compose |
| Networking | Retrofit + OkHttp |
| Maps | osmdroid (OpenStreetMap) |
| Charts | Vico |

## Building

1. Clone the repository
2. Open in Android Studio (Hedgehog or newer recommended)
3. Sync Gradle
4. Run on device or emulator

```bash
./gradlew assembleDebug
```

## Project Structure

```
app/src/main/java/com/hamhub/app/
├── data/
│   ├── local/database/     # Room entities, DAOs, database
│   ├── remote/api/         # Retrofit API interfaces
│   └── repository/         # Data repositories
├── domain/
│   ├── model/              # Domain models
│   ├── usecase/            # Business logic
│   └── util/               # Utilities (ADIF parser, grid calc, etc.)
├── ui/
│   ├── components/         # Reusable Compose components
│   ├── navigation/         # Navigation setup
│   ├── screens/            # Screen composables
│   └── theme/              # Material 3 theming
└── di/                     # Hilt modules
```

## License

This project is licensed under the [Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License](LICENSE) (CC BY-NC-SA 4.0).

You are free to:
- **Share** - copy and redistribute the material
- **Adapt** - remix, transform, and build upon the material

Under the following terms:
- **Attribution** - Give appropriate credit
- **NonCommercial** - Not for commercial purposes
- **ShareAlike** - Distribute contributions under the same license

## Privacy

HamHub respects your privacy. All data is stored locally on your device. See our [Privacy Policy](PRIVACY_POLICY.md) for details.

## [Contributing](CONTRIBUTING.md)

Contributions are welcome! Please ensure any contributions are compatible with the CC BY-NC-SA 4.0 license.

## Acknowledgments

- [Callook.info](https://callook.info) for callsign lookup API
- [RepeaterBook](https://www.repeaterbook.com) for repeater data
- [HamQSL](https://www.hamqsl.com) for solar/propagation data
- [Open Notify](http://open-notify.org) for ISS tracking data
