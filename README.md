# EventRSVP

Event & RSVP Manager for Android.

## Features
- ✨ Create events (name, date, time, location, description)
- 👥 Add guests with RSVP status (Attending / Maybe / Declined / No Response)
- 🍽 Track dietary requirements & plus-ones
- 📤 Share event invite text
- 📊 Dashboard with response breakdown
- ⏰ Reminder notifications for upcoming events
- 🔍 Past / upcoming event filter
- 📋 Export guest list as JSON
- 🌙 Dark theme, Material Design 3
- ℹ️ About section with version, update check, share

## Tech Stack
- Kotlin + AndroidX + Room
- Material Design 3 (dark theme)
- AlarmManager for reminders
- Coroutines (Dispatchers.IO for all DB ops)

## Build
```bash
./gradlew assembleRelease
```

## Download
Get the latest APK from [Releases](https://github.com/jnetai-clawbot/EventRSVP/releases).

## License
MIT