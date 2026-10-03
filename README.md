# Mavo 📒

**The Free, Zero-Friction MSME ERP & GST Invoicing Engine**

Mavo is a modern, free, cross-platform enterprise resource planning (ERP) and GST billing engine designed specifically for Indian Micro, Small, and Medium Enterprises (MSMEs). From high-traffic retail counters and bustling wholesale yards to manufacturing workshops and service contractors, Mavo eliminates accounting complexity so business owners can record, transact, and scale effortlessly.

This repository hosts the **native Android reference implementation**, built with Kotlin and Jetpack Compose.

---

## 🛑 The Problem: The Indian MSME Digital Dilemma

Over 63 million MSMEs power the Indian economy, yet their digital toolset remains broken:

| Current Solution | The Reality & Pain Points |
|---|---|
| **Enterprise Desktop ERPs** *(Tally Prime, SAP, Oracle)* | **Expensive & Intimidating:** Prohibitively expensive licenses, steep learning curves, cluttered keyboard shortcuts, and confusing debit/credit interfaces that demand a dedicated commerce graduate or accountant. |
| **Mobile Billing Apps** *(Vyapar, MyBillBook)* | **Restrictive Paywalls:** Gate critical features behind paid subscriptions, limit multi-device workflows, or treat businesses purely as basic kirana shops without wholesale, service, or manufacturing workflows. |
| **Traditional Paper Khatas** *(Red Books / Diaries)* | **Fragile & Unscalable:** "Where did I write that 5 minutes ago?" Lost paper receipts, untracked party credit, manual math mistakes, and zero automated GST compliance. |

---

## 💡 What is ZeroBook?

ZeroBook bridges this gap with a radical philosophy: **Zero Learning Curve, Zero Subscription Barriers, Zero Jargon.**

If you know how to tap a phone screen or click a mouse, you can run your entire enterprise. You don't need a commerce degree. "Sales" means goods going out; "Purchase" means stock coming in; "Stock" is what sits on your shelves.

### Built for Every MSME Sector

1. **Retail Stores (Kirana, Electronics, Apparel, General Stores):**
   - Rapid counter checkout, barcode scanning, and instant thermal printing.
   - **Voice-to-Bill (Mobile):** Shopkeepers can speak line items in Indian vernacular languages (Hindi, Bengali, English, etc.) during rush hours to generate itemized invoices in one tap.
2. **Wholesalers & Distributors:**
   - Multi-item Quotations with instant **1-tap Quote-to-Invoice conversion** (drop unselected items dynamically when the customer buys only part of the quote).
   - Sales Orders, Delivery Challans, and party credit balance monitoring.
3. **Manufacturers & Assemblers:**
   - Inward raw material procurement, Goods Receipt Notes (GRN), and production stock tracking.
4. **Service & Logistics Providers (Transport, Agencies, Repairs):**
   - Dedicated Services mode with SAC code support and Delivery Challans for transit goods without immediate tax invoices.

---

## ⚡ Core Value Pillars

- **Zero-Commerce Jargon:** Intuitive everyday language replaces intimidating accounting terminology.
- **Dynamic Business Adapters:** Dynamic setup onboarding adapts vouchers, fields, and views to your business type (Retail vs. Wholesale vs. Manufacturing vs. Services).
- **Omnichannel & Multi-Counter Sync:** One unified business account connects warehouse docks (purchase/inward stock) with counter terminals (sales/receipts) in real time.
- **Vernacular Voice Billing:** Speak items and quantities on mobile to eliminate slow touchscreen typing during peak shop hours.
- **Offline-First Resilience:** Instant local SQLite database response times with zero lag, syncing seamlessly when connectivity is present.
- **Full GST & Voucher Engine:** 16+ voucher types (Tax Invoices, Purchases, Returns, Debit/Credit Notes, Delivery Challans, Orders, Receipts, Payments, and Day Book).

---

## 📊 ZeroBook vs. The Market

| Feature / Metric | Traditional ERP *(Tally/SAP)* | Paid Billing Apps *(Vyapar/MyBillBook)* | Paper Khata | ZeroBook |
|---|---|---|---|---|
| **Price** | Very High (₹18k – Lakhs) | Subscription paywalls | Low (notebooks) | **100% Free / MSME First** |
| **Learning Curve** | Months (needs accountant) | Moderate (mobile-first) | Low | **Zero (Intuitive Plain Language)** |
| **Business Scope** | Enterprise / Generic | Mostly Kirana / Retail | Any (unstructured) | **Retail, Wholesale, Mfg & Services** |
| **Multi-Device / Sync** | Complex server setups | Paid multi-user tier | Impossible | **Unified Multi-Department Sync** |
| **Voice-to-Bill** | ❌ No | ❌ No | ❌ No | **✅ Vernacular Indian Voice AI** |
| **Quote-to-Sale Conversion**| Rigid | Limited | Manual | **✅ 1-Tap Partial & Full Convert** |

---

## ✨ Feature Inventory (Android Reference)

- **GST Billing & Tax Engine:** Automated CGST, SGST, and IGST calculation based on intrastate/interstate rules.
- **16+ Voucher Types:** Tax Invoices, Purchases, Sale/Purchase Returns, Receipts, Payments, Debit/Credit Notes, Quotations, Proforma, Delivery Challans, Sales/Purchase Orders, GRN, Material Notes, and Incomes/Expenses.
- **Party & Ledger Accounting:** Customers & suppliers with running balances, financial-year partitioning, credit limits, and aging statements.
- **Inventory & Stock Management:** Dual units of measure (e.g. Box + Pcs), batch/expiry support, low-stock thresholds, and built-in HSN/SAC directory.
- **Camera Scanning & OCR:** CameraX + ML Kit for instant barcode entry and bill data capture.
- **Financial Reports:** Real-time Profit & Loss, Balance Sheet, Trial Balance, Stock Summary, and Day Book.
- **Export & Reminders:** In-app PDF invoice generation, CSV exports, and automated email reminders via WorkManager.

---

## 🛠 Tech Stack (Android Reference)

| Layer | Technology |
|---|---|
| **Language & UI** | Kotlin 2.x, Jetpack Compose, Material 3 with 5 switchable themes |
| **Architecture** | MVVM + Repository Pattern with Coroutines & StateFlow |
| **Persistence** | Room (SQLite) with multi-version automated schema migrations |
| **Hardware & ML** | CameraX, Google ML Kit (Barcode Scanning & Text Recognition) |
| **Background & I/O** | WorkManager, JavaMail, Android Printing Framework |

> **Cross-Platform Evolution:** This repository represents the rock-solid Android native reference app. ZeroBook is actively evolving into a **Kotlin Multiplatform (KMP) + Compose Multiplatform** architecture to natively power Android, iOS, Desktop (Windows, macOS, Linux with keyboard-first navigation), and Web.

---

## 🚀 Getting Started

### Prerequisites
- Android Studio Ladybug / Meerkat (or newer)
- JDK 17+
- Android Device or Emulator running API 24+ (Android 7.0+)

### Build & Run
```bash
git clone https://github.com/sourish-dutta-sugo/ZeroBook.git
cd ZeroBook
./gradlew assembleDebug
```
Launch on your connected device. On initial run, the setup wizard will guide you through your business profile and industry mode selection.

---

## 🗺 Strategic Roadmap

- [x] **Core Accounting & 16+ Voucher Engine:** Complete GST calculation, ledger posting, and reporting.
- [ ] **Vernacular Voice Billing (Mobile):** Multi-language speech transcription (Hindi, Bengali, English) for rapid counter checkout.
- [ ] **1-Tap Quotation-to-Sale Flow:** Dynamic line-item selection and conversion.
- [ ] **Multi-Mode Business Onboarding:** Dynamic UI customization for Retail, Wholesale, Manufacturing, and Services.
- [ ] **Cross-Platform KMP Rebuild:** Shared SQLDelight and Compose Multiplatform for Desktop and Web.
- [ ] **Cloud Workspace & Department Sync:** Real-time synchronization between dock purchasing and counter billing.

See [docs/product/ROADMAP.md](docs/product/ROADMAP.md) for detailed release milestones.

---

## 📄 License & Rights

Copyright (c) 2026 Sugo. All Rights Reserved.  
ZeroBook is free for personal, educational, and internal business use. Redistribution, rebranding, or publishing this application under another name on public app stores without authorization is strictly prohibited. See the full license in [docs/README.md](docs/README.md).
