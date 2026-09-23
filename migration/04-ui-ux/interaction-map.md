# Interaction Map

- Bottom navigation preserves top-level destinations with `popUpTo`, `launchSingleTop`, and state restoration.
- Voucher/party/product selection has dedicated controller components.
- Voucher prefill crosses routes through `AppViewModel.VoucherPrefillRequest`.
- Forms call ViewModel/repository operations through coroutine-backed functions.
- Document actions invoke Android share/email/file boundaries.
- Theme and KPI animation settings persist locally.
