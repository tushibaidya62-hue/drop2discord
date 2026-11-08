# Drop2Discord — Agent Rules

Keep messages to the user short and plain. The user is not a programmer and has only an Android phone. The APK is built by GitHub Actions. Code must compile on the first try.

## Rules

1. App has NO upload code: no webhook, no bot, no token, no login. Never press Send inside Discord. The app only opens Discord with files ready.
2. Use exact versions: Kotlin 2.0.21, Compose plugin 2.0.21, AGP 8.7.3, Gradle 8.9, JDK 17, minSdk 26, compileSdk 35, targetSdk 34, Compose BOM 2024.10.01, core-ktx 1.15.0, activity-compose 1.9.3, lifecycle 2.8.7. Do not upgrade or add libraries.
3. One module called app. Package com.drop2discord.app. No permissions.
4. Do not create Gradle wrapper files. Root build.gradle.kts uses apply false. settings.gradle.kts uses google() and mavenCentral().
5. Resources: theme parent android:Theme.Material.Light.NoActionBar. Adaptive icon in mipmap-anydpi-v26, no PNG. allowBackup=false. Texts in strings.xml.
6. Share with FileProvider authority = applicationId + ".fileprovider", files-path queue/. ACTION_SEND for 1 file, ACTION_SEND_MULTIPLE for more. Put URIs in EXTRA_STREAM and ClipData. Add FLAG_GRANT_READ_URI_PERMISSION. Set package com.discord, fall back to createChooser.
7. Check free space (batch size + 300 MB) before copying. Copy with small buffer. Never load a whole file in memory. Never delete the user's original files.
8. Build with gradle assembleDebug. Fix every error. If no local Android SDK exists, do a careful self-review instead of installing tools.

## Stage 1

One screen: Pick files (OpenMultipleDocuments, any type), list with name and size, max 10. Share all to Discord: check space, copy to filesDir/queue/batch_0, open Discord. Delete copies removes filesDir/queue.

## Stage 2 (only after user says "continue")

Add queue, batches, history, settings, Room, Coil. Keep package and file names. Do not leave unused files.
