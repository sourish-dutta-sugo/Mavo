# Business Rules

1. All transactional observations use selected financial-year code.
2. A missing financial year is created from its code and date boundaries.
3. Party/product balances may be overlaid by financial-year balance records.
4. Voucher items retain product snapshot fields (name, HSN, rate, tax values) for document/history stability.
5. `DocumentType` defines display name, prefix, draft flag, ledger-posting flag, and mapped voucher type.
6. Only registered document generators can render outbound document output.
7. Receipt allocation calls outstanding recalculation.
8. Financial-year closure/locking is a state transition with audit records and carry-forward balances.
9. Setup completion is inferred from existence of a stored business profile.
10. PIN and theme preferences are local device settings, separate from Room business data.

Tax, round-off, inventory mutation, and posting error semantics remain **UNKNOWN** unless confirmed by targeted tests.
