# Migration Research

## Preserve

- Accounting vocabulary, voucher/document types, GST fields, FY partitioning, ledger/stock semantics, and business-profile invoice settings.
- Screen-level workflows and route parameters.
- Local-first behavior unless product scope explicitly adds sync.
- Theme token roles and five theme choices.

## Translate

- Compose screens to target UI components.
- ViewModel/StateFlow to target state/store primitives.
- Room entities/DAOs/repository to shared database or API-backed repositories.
- Android FileProvider/WebView PDF to platform-neutral document/share adapters.

## Replace

- Android navigation, permissions, camera, WorkManager, SharedPreferences/DataStore, JavaMail, Google Play Services auth, and WebView.
- Room migration strategy if schema is shared across platforms.

## Cannot directly migrate

- Android URI grants, Activity lifecycle behavior, device permission APIs, WorkManager guarantees, and Android-specific PDF rendering.

## Target architecture notes

Use a shared domain contract for entities, voucher calculations, FY rules, document metadata, and validation. Keep platform adapters for persistence, camera/scanning, file access, notifications/scheduling, auth, email, and PDF/share. Do not assume current README networking claims are real.
