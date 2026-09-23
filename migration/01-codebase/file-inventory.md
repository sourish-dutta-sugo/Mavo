# File Inventory

## Runtime source

| Package | Main responsibility |
|---|---|
| `com.zerobook.app` | Activity/bootstrap |
| `data` | Persistence, repository, preference and scheduling code |
| `domain.model` | Ledger, party, product, voucher types |
| `ui.screens` | Product screens and dialogs |
| `ui.theme` | Theme variants and design tokens |
| `ui.selection` | Multi-select state and selection UI |
| `ui.transitions` | Screen and press transitions |
| `services.documents` | Document type abstraction and generator registry |
| `services.documents.outbound` | Six outbound document renderers |
| `services` | Invoice/PDF, email, CSV and export |
| `feature.billing` | Quick counter sale |

## Resources

Manifest permissions and FileProvider: `app/src/main/AndroidManifest.xml`.  
XML resources: `app/src/main/res/xml/`.  
Theme/colors/strings: `app/src/main/res/values/`.  
Brand assets: `app/src/main/res/drawable/` and `mipmap-*`.

## Generated or local artifacts

Build logs, `.manus_*`, IDE state, and current untracked audit/prototype files are not treated as application requirements.
