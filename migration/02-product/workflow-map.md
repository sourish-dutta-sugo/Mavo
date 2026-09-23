# Workflow Map

```mermaid
sequenceDiagram
    actor Operator
    participant UI as Compose screen
    participant VM as AppViewModel
    participant Repo as AppRepository
    participant DB as Room/SQLite
    Operator->>UI: opens app
    UI->>VM: observe startup state
    VM->>Repo: load profile and FY
    Repo->>DB: query local entities
    DB-->>UI: StateFlow data
    Operator->>UI: saves voucher
    UI->>VM: submit voucher and items
    VM->>Repo: transactional save/post
    Repo->>DB: persist voucher, ledger, stock effects
```

## Entry and exit

- Startup enters splash, setup, optional PIN, then dashboard.
- Main destinations are dashboard, vouchers, parties, settings.
- Secondary routes open reports, products, bank/cash, expenses, counter sale, new voucher, invoice, ledger books, and party detail.
- File/PDF/email workflows exit through Android shares, FileProvider URIs, WebView PDF output, SMTP, or WorkManager.
