# ROADMAP.md — ZeroBook Product Roadmap & Milestones

> Last updated: 2026-09-30  
> Single source of truth for active development phases, prioritized tasks, and platform evolution.

---

## 🧭 Strategic Vision & Phases

ZeroBook is evolving from a single-device native Android billing app into an omnichannel MSME ERP engine. Development is organized into clear progressive phases:

```
[ Phase 0: Android Baseline ] ──▶ [ Phase 1: Rapid Invoicing & Quote Flow ]
                                                  │
                                                  ▼
[ Phase 3: KMP Cross-Platform ] ◀── [ Phase 2: Dynamic Business Modes ]
                │
                ▼
[ Phase 4: Multi-Device Cloud & Departments ]
```

---

## 📍 Phase 0: Core Foundation (Current State — v2.2.x)

- [x] Complete 16+ voucher accounting engine (Tax Invoices, Purchases, Orders, Challans, Returns, Debit/Credit Notes, GRN, Day Book)
- [x] Automated GST tax engine (CGST/SGST/IGST detection, HSN lookup, round-off)
- [x] Double-entry ledger posting, party running balances, and FY carry-forward
- [x] Local Room SQLite database with multi-version schema migrations
- [x] In-app PDF generation, JavaMail export, and CameraX barcode scanning

---

## ⚡ Phase 1: Rapid Invoicing & Workflow Accelerators (Next Up)

| Feature / Milestone | Priority | Scope & Notes |
|---|---|---|
| **1-Tap Quotation-to-Sale Conversion** | **P0 (Immediate)** | Convert Quotation vouchers directly into Tax Invoices. Includes interactive item selector to drop/edit unpurchased items when a customer buys only part of the quote. |
| **Quick Sale Counter UI** | **P0 (Immediate)** | Complete high-speed counter sale UI for rush-hour retail checkouts with minimum keystrokes. |
| **Vernacular Voice-to-Bill (Mobile)** | **P1 (High)** | Voice input on Android supporting Indian languages (Hindi, Bengali, English). Transcribe spoken item lists (e.g. "2 kg sugar, 5 packets biscuits") directly into invoice line items. |
| **OCR Purchase Bill Ingestion** | **P1 (High)** | Parse printed vendor invoices via ML Kit text recognition to auto-fill purchase voucher line items. |
| **Batch & Expiry Inventory Logic** | **P2 (Medium)** | Utilize existing batch/expiry database fields for FIFO inventory valuation and expiring item alerts. |

---

## 🏢 Phase 2: Dynamic Business Mode Engine

| Feature / Milestone | Priority | Scope & Notes |
|---|---|---|
| **Adaptive Setup Wizard** | **P1 (High)** | Business type selector on first launch (Retailer, Wholesaler, Manufacturer, Service Provider). |
| **Adaptive Voucher UI Filtering** | **P1 (High)** | Tailor visible voucher types based on business mode (e.g., conceal raw-material GRN for Kirana; emphasize SAC codes and transit challans for Transporters/Services). |
| **Manufacturing Raw Material Flow** | **P2 (Medium)** | Dedicated purchase-to-raw-material tracking and bill-of-materials (BOM) inventory consumption. |
| **Services & SAC Code Catalog** | **P2 (Medium)** | Native support for Service Accounting Codes (SAC), time/service billing, and non-inventory challans. |

---

## 💻 Phase 3: Cross-Platform KMP Rebuild (Desktop & Web)

| Feature / Milestone | Priority | Scope & Notes |
|---|---|---|
| **SQLDelight Multiplatform Database** | **P1 (High)** | Migrate database schema and business logic to Kotlin Multiplatform (KMP) shared repository. |
| **Desktop Keyboard-First Navigation** | **P1 (High)** | Ultra-fast desktop client (Windows, macOS, Linux) with Tally-style keyboard shortcuts (`Alt+F1`, `F8` for Sales, `F9` for Purchase, Enter-to-advance). |
| **Compose Multiplatform UI** | **P2 (Medium)** | Shared UI layer across Android, iOS, and Desktop. |

---

## ☁️ Phase 4: Multi-Device Cloud & Department Workspace

| Feature / Milestone | Priority | Scope & Notes |
|---|---|---|
| **Unified Business Account** | **P1 (High)** | Connect multiple staff devices to a single business ID / email login. |
| **Department Role Workflows** | **P1 (High)** | Real-time separation of roles: receiving dock logs inward Purchases/GRN while sales counters ring up customer invoices. |
| **Conflict-Free Real-Time Sync** | **P2 (Medium)** | Local-first offline capability with CRDT or server-assisted synchronization for multi-counter shops. |

---

## 🐛 Known Issues & Backlog

- **Known Bugs:** Zero active bugs blocking v2.2.3.
- **Verification Checklist:** Unit tests (`./gradlew testDebugUnitTest`) pass with 0 warnings.
