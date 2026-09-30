# API.md — ZeroBook (Services & Internal APIs)

> Last updated: 2026-09-29

ZeroBook has **no backend server**. All data is local (Room/SQLite). This doc covers internal services and integrations.

## Invoice generation

- **File:** `services/InvoiceGenerator.kt`
- **Method:** Builds HTML string → renders in Android WebView → prints to PDF
- **Input:** Voucher + VoucherItems + BusinessProfile
- **Output:** PDF file on device storage
- **Configurable sections:** Logo, signature, bank details, UPI QR code, transport details, GST breakup, HSN column, amounts in words, terms & conditions

## PDF writing

- **File:** `services/WebViewPdfWriter.java`
- **Method:** Java class that takes a WebView with loaded HTML and creates a PDF via `PrintDocumentAdapter`

## CSV export / import

- **File:** `services/CsvTransferManager.kt`
- **Export:** Vouchers, parties, products, ledger entries → CSV files
- **Import:** CSV → parsed → inserted into Room DB
- **Used for:** Data backup, migration to other systems

## File export

- **File:** `services/ExportStorageManager.kt`
- **Method:** Saves generated files (PDF, CSV) to device external storage
- **Permissions:** Storage access via SAF (Storage Access Framework)

## Email

### Composer
- **File:** `services/EmailComposer.kt`
- **Method:** Builds MIME message with subject, body, PDF attachment

### Automation
- **File:** `services/EmailAutomationService.kt`
- **Method:** WorkManager-based scheduled email sending
- **Config:** Per-customer rules with schedule, frequency, template
- **Auth:** Google OAuth (`play-services-auth`) or SMTP credentials

### Transport
- **Protocol:** SMTP via JavaMail (`com.sun.mail:android-mail`)
- **Default host:** `smtp.gmail.com:587`
- **Alt auth:** Google OAuth token via `EmailAccount` entity

## Camera / ML Kit

### Barcode scanning
- **Library:** `play-services-mlkit-barcode-scanning`
- **Camera:** CameraX (`androidx.camera.*`)
- **Output:** Barcode string value → `product.barcodeValue`

### Text recognition (OCR)
- **Library:** `play-services-mlkit-text-recognition`
- **Camera:** CameraX
- **Output:** Recognized text for manual extraction

## Location

- **Library:** `play-services-location`
- **Usage:** Auto-detect GST state code from device GPS during setup
- **Optional:** Falls back to manual entry

## DataStore preferences

- **File:** `data/AppPreferences.kt`
- **Store name:** `zerobook_prefs`
- **Keys:** FY last checked date, last seen changelog version, progress tracker settings, KPI animation mode

## Changelog (in-app)

- **Source file:** `app/src/main/assets/changelog.json`
- **Loader:** `data/ChangelogLoader.kt`
- **Format:** JSON with `history` array of `{version, changes}` entries
- **Display:** Settings screen → "What's New" section
- **Show-once logic:** Compares `lastSeenChangelogVersion` in DataStore with latest entry
