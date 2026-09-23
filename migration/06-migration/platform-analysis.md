# Platform Analysis

| Concern | Android implementation | Cross-platform replacement |
|---|---|---|
| Database | Room/SQLite | SQLDelight/SQLite or platform repository |
| Navigation | Navigation Compose | React Router/navigation, Flutter Navigator, or shared route model |
| PDF | WebView + PDF writer | HTML/PDF service or native renderer |
| Files/share | FileProvider | browser download/share sheet/native file APIs |
| Camera | CameraX + ML Kit | platform camera/scanner adapters |
| Scheduling | WorkManager | OS scheduler, server job, or opt-in client scheduler |
| Auth | Google Play Services | OAuth provider abstraction |
| Email | JavaMail/SMTP | backend email service or platform-compatible SMTP |
| Preferences | DataStore/SharedPreferences | secure/local settings store |
