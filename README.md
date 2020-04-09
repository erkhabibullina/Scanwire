# Scanwire
A simple application, that can scan every QR-code, decrypt it and store them in local database.

## Features
- Search QR-codes, dynamically updated in background view as user input query.
- Sets date and time formatting depending on user country, and they can also override this in settings activity.
- Switch between light / dark theme, icons / clickable backgrounds change accordingly.

## Built With
- [MVVM](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93viewmodel) - An architectural pattern, that I used to design this app
- [Preferences](https://developer.android.com/jetpack/androidx/releases/preference) - A Library to build interactive settings screens
- [Material](https://material.io) - Design Library
- [Room](https://developer.android.com/topic/libraries/architecture/room) - Database, used to store QR-codes
- [Paging Library](https://developer.android.com/topic/libraries/architecture/paging) - Library, that 
used to loading partial data on demand
- [Dagger](https://dagger.dev/) - Dependency Injection Library
- [CameraX](https://developer.android.com/jetpack/androidx/releases/camera) - Library, that used to manage camera
- [ML-Kit](https://developers.google.com/ml-kit) - Image Analyzing Library
- [GSON](https://github.com/google/gson) - Serialization/deserialization library
- [ZXing](https://github.com/zxing/zxing) - Barcode scanning library

## License
This project is licensed under the MIT License - see the [LICENSE.md](https://github.com/erkhabibullina/Scanwire/blob/master/LICENSE) file for details
