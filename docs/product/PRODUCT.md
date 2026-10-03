# PRODUCT.md — Mavo Product Definition

> Last updated: 2026-10-03  
> Single source of truth for Mavo product identity, vision, user personas, and design philosophy.

---

## 1. What Mavo Is

**Mavo** is a zero-friction, 100% free enterprise resource planning (ERP) and GST invoicing engine built specifically for Indian Micro, Small, and Medium Enterprises (MSMEs).

It replaces legacy, expensive accounting software (Tally Prime, SAP, Oracle) and restrictive paywalled mobile apps (Vyapar, MyBillBook) with an intuitive, jargon-free platform that anyone can operate without an accounting or commerce background.

While this repository contains the **stable Android native reference implementation**, the product vision spans mobile (Android/iOS) and desktop (Windows/macOS/Linux/Web) powered by shared business logic.

---

## 2. Who Mavo Serves (Target Personas)

Mavo is built to adapt dynamically to four core MSME business archetypes:

### A. Retailers & Shop Owners
- **Who:** Kirana stores, electronics shops, hardware stores, stationery, apparel merchants.
- **Workflow:** High-speed counter billing, barcode scanning, dual units of measurement, fast cash/UPI reconciliation.
- **Killer Feature:** **Vernacular Voice-to-Bill** — Counter operators speak item names and quantities in Indian languages (Hindi, Bengali, English) on their phone during busy hours; Mavo auto-populates the invoice lines with one-tap confirmation.

### B. Wholesalers & Distributors
- **Who:** Bulk FMCG distributors, grain merchants, building material suppliers, hardware stockists.
- **Workflow:** Sending bulk price quotations, managing credit cycles, delivery challans, and goods dispatch.
- **Killer Feature:** **1-Tap Partial Quote-to-Sale Conversion** — When a buyer requests a quote for 100 items but decides to purchase only 35, the seller converts the quotation into an official Tax Invoice instantly, dropping unpurchased items with one tap.

### C. Manufacturers & Fabricators
- **Who:** Small workshops, food processing units, garment makers, component assemblers.
- **Workflow:** Inward raw material procurement, vendor ledger tracking, Goods Receipt Notes (GRN), and finished goods stock inventory.

### D. Service & Logistics Contractors
- **Who:** Transporters, freelance contractors, repair agencies, maintenance providers.
- **Workflow:** Services billing with SAC codes, transit Delivery Challans for non-sales consignments, expense tracking.

---

## 3. The Core Problems It Solves

1. **The Cost & Complexity Barrier:** Tally and SAP cost tens of thousands of rupees annually and require specialized accountants. Mavo is 100% free and requires zero accounting education.
2. **The "Kirana-Only" & Paywall Trap:** Existing mobile apps either gate essential features behind costly subscriptions or assume every small business is a simple grocery store.
3. **The Counter Speed Bottleneck:** Typing on small smartphone touchscreens while 5 impatient customers wait at the counter is slow. Voice billing and fast quick-sale flows solve this.
4. **Disjointed Department Workflows:** In multi-person shops, the stock receiver at the back dock logs purchases while the front counter rings up sales. Mavo synchronizes departments under a single unified business account.

---

## 4. Product Design Principles

- **Zero Commerce College Needed:** Use plain, unmistakable language. "Sales" (money/stock out), "Purchase" (money/stock in), "Stock" (items on hand), "Parties" (who you deal with).
- **Zero Friction Onboarding:** First launch asks: *Are you a Retailer, Wholesaler, Manufacturer, or Service provider?* The UI automatically exposes relevant voucher types and conceals unnecessary ones.
- **Speed First:** High-frequency actions must execute in under 3 taps.
- **Offline-First Resilience:** Zero network latency during checkout. Complete Room SQLite data persistence with background cloud synchronization.
- **100% Indian GST Compliance:** Automated intrastate (CGST + SGST) and interstate (IGST) split, HSN/SAC directory, e-way bill ready fields, and audit-safe financial year locking.

---

## 5. Scope & Deliberate Boundaries

### In Scope
- Complete 16+ voucher suite (Tax Invoices, Purchases, Orders, Challans, Returns, Debit/Credit Notes, GRN, Day Book).
- Multi-business sector workflows (Retail, Wholesale, Manufacturing, Services).
- Indian vernacular voice billing for mobile.
- Quotation-to-Sale flexible conversion.
- Cross-platform parity (Android reference -> KMP Desktop & Mobile).
- Unified multi-counter and multi-department cloud sync.

### Explicit Non-Goals
- **No Complex Enterprise Bloat:** No rigid multi-tier corporate hierarchies, complex enterprise payroll/HR systems, or foreign currency hedging.
- **No Monetization Paywalls on Core Workflows:** Core accounting, voucher creation, and data export will never be locked behind paywalls.
