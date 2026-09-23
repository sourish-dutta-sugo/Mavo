# Dependency Map

```mermaid
flowchart TD
    Activity[MainActivity] --> VM[AppViewModel / DashboardViewModel / ThemeViewModel]
    VM --> Repo[AppRepository]
    Repo --> Room[AppDatabase + DAOs]
    Room --> SQLite[(ZeroBook.db)]
    VM --> Preferences[DataStore / SharedPreferences]
    Screens[Compose screens] --> VM
    Screens --> Services[PDF / CSV / email / scanner services]
    Services --> Android[Android WebView, FileProvider, WorkManager, JavaMail]
```

**VERIFIED:** dependency edges are based on imports and construction in current source.  
**INFERRED:** diagram represents runtime call direction, not a strict compile-time module boundary.
