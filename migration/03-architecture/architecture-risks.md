# Architecture Risks

- Broad `AppViewModel` and `AppRepository` increase feature coupling.
- Legacy schema compatibility uses defensive column checks in addition to Room migrations.
- Sensitive SMTP password is stored in profile/entity pathways; encrypted storage is also used elsewhere. Exact protection boundary needs audit.
- Document generator enum surface exceeds registry implementation.
- Local-only storage has no verified sync/conflict model.
- Android-specific PDF, file, camera, email, and scheduler APIs require replacement adapters for non-Android targets.
