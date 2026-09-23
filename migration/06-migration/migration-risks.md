# Migration Risks

1. Accounting parity can fail if tax, rounding, stock, or ledger posting order differs.
2. Room legacy migrations and defensive column creation encode real user data compatibility.
3. Document output may differ across renderers and page sizes.
4. Local-only data has no verified cross-device conflict strategy.
5. Sensitive SMTP credentials require stronger, explicit secret-storage design.
6. Registry-supported document types are narrower than enum-declared types.
7. Mobile-first layouts may not define tablet/desktop behavior; responsive rules are **UNKNOWN**.
