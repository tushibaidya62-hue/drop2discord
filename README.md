# Drop2Discord

Simple app for sending files to Discord. It does not upload files. It only opens Discord with your files ready. You pick your channel and tap Send inside Discord. Your real account sends the files, so your Nitro limit applies.

## How to use

1. Tap Pick files. Choose up to 10 files.
2. Tap Share all to Discord.
3. Discord opens with all files ready. Pick your channel. Tap Send.
4. Come back here. Tap Delete copies to clean up.

## Build

This app is built online by GitHub Actions. You do not need a PC or Android Studio.
Push to the main branch, or run the workflow by hand. The APK is saved as a release.

## Files

- app: the Android app.
- .github/workflows/build.yml: the cloud build.

No secrets are in this repo. The app has no upload code, no webhook, no bot, no token, no login.
