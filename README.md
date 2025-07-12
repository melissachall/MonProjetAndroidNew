#  Travel APP (Compose Multiplatform)

Travel App demonstrate the use of
_**Compose Multiplatform**_ for developing IOS, Android, MacOS, Window & Web applications
using **Jetpack Compose** 🚀.

## ![Android](https://img.shields.io/badge/Android-black.svg?style=for-the-badge&logo=android) 


### 📱 Preview

Currently, the app looks like this on platforms:


---

## Built with 

- [Kotlin](kotlinlang.org): Programming language
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html): For building multi-platform applications in the single codebase.
- [Compose Resource](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-resources.html): 
Compose Resource is a structured approach for managing and accessing UI resources (such as strings, colors, and images) in Jetpack Compose across multiple platforms. When used in conjunction with libraries like Moko Resources, it allows for a unified and type-safe way to work with localized and platform-specific assets in Kotlin Multiplatform projects. This setup ensures that resources are accessible in a Compose-friendly way across Android, iOS, macOS, the JVM, and JS (Browser), with full support for system localization and consistent design.
-  [Compose Image Loader](https://github.com/qdsfdhvh/compose-imageloader): Compose Image library for Kotlin Multiplatform.


## TODOs

### ✅ Completed
- [x] Home screen UI.
- [x] Destination Detail screen UI.
- [x] Favorite Detail screen UI.
- [x] Coil(Image loading from Url).
- [x] Bottom Navigation Menu.
- [x] Compose resources e-g images, strings and fonts.
- [x] Voyager (Navigation).
- [x] Cart Screen Design.
- [x] Gemini Chat Screen Design.
- [x] Support for MacOS, Window, Web.
- [x] CI/CD Configure for IOS, Android, MacOS, Window, Web.
- [x] CI/CD generate artifacts to download
- [x] CI/CD to publish web app on github pages


## Project structure 

This Compose Multiplatform project includes:

### [`composeApp`](/composeApp)
This is a Kotlin module that contains the common UI/logic for both Android and iOS applications, the code you share between platforms.
This shared module is also where you write your Compose Multiplatform code. In `composeApp/src/commonMain/kotlin/App.kt`, you can find the shared root `@Composable` function for your app.
It uses Gradle as the build system. You can add dependencies and change settings in `composeApp/build.gradle.kts`. The shared module builds into an Android library and an iOS framework.

## Setting up project 👨🏻‍💻

- Refer to the ***"Setting up environment"*** section of [this repository](https://github.com/JetBrains/compose-multiplatform-ios-android-template/main/README.md) 
for knowing the setup guidelines
- After validating requirements as per the above guide, clone this repository.
- Open this project in Android Studio Electric Eel or newer version.
- Build project 🔨 and see if everything is working fine.
- Run App
  - Select "androidApp" as run configuration and you'll be able to run the Android app.


---

## Contribute

If you want to contribute to this library, you're always welcome!


