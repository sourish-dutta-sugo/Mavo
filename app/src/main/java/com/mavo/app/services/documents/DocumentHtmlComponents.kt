package com.mavo.app.services.documents

import com.mavo.app.data.BusinessProfile
import com.mavo.app.data.Party
import java.text.DecimalFormat

/**
 * Shared HTML components for document templates.
 * These components are reused across different document types.
 */
object DocumentHtmlComponents {
    
    private val decimalFormat = DecimalFormat("#,##0.00")
    
    /**
     * Escape HTML special characters.
     */
    fun escapeHtml(value: String): String {
        return value.replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&#39;")
    }
    
    /**
     * Format a number as currency.
     */
    fun formatMoney(value: Double): String {
        return decimalFormat.format(value)
    }
    
    /**
     * Format quantity with 3 decimal places.
     */
    fun formatQty(value: Double): String {
        return String.format(java.util.Locale.ENGLISH, "%.3f", value)
    }
    
    /**
     * Convert file path to file URL for WebView.
     */
    fun toFileUrl(path: String): String {
        return if (path.startsWith("/")) "file://$path" else "file:///$path"
    }
    
    /**
     * Build the business header box with optional logo.
     */
    fun buildBusinessHeader(business: BusinessProfile, showLogo: Boolean, inlineName: Boolean = false): String {
        val detailsHtml = buildString {
            if (inlineName) {
                append("<div style='font-size:13px;font-weight:bold;margin-bottom:4px;'>${escapeHtml(business.businessName)}</div>")
            } else {
                append("<div class='company-name'>${escapeHtml(business.businessName)}</div>")
            }
            if (business.address.isNotBlank()) {
                append("<div>${escapeHtml(business.address).replace("\n", "<br/>")}</div>")
            }
            if (business.city.isNotBlank() || business.pin.isNotBlank()) {
                append("<div>${escapeHtml(business.city)}${if (business.pin.isNotBlank()) " - ${escapeHtml(business.pin)}" else ""}</div>")
            }
            if (business.gstin.isNotBlank()) {
                append("<div>GSTIN: ${escapeHtml(business.gstin)}</div>")
            }
        }

        return if (showLogo) {
            """
            <table class='seller-grid' style='border:1px solid #000;'>
              <tr>
                <td class='seller-logo' style='border-right:1px solid #000;padding:8px;'><img src='${toFileUrl(business.logoPath!!)}'/></td>
                <td style='padding:8px;vertical-align:top;'>$detailsHtml</td>
              </tr>
            </table>
            """.trimIndent()
        } else {
            """
            <div style='border:1px solid #000;padding:8px;'>
              $detailsHtml
            </div>
            """.trimIndent()
        }
    }

    /**
     * Build the party block (To: / Consignee:).
     */
    fun buildPartySection(party: Party?, label: String = "To:", emptyText: String = "Cash / Walk-in Customer"): String = buildString {
        append("<div style='margin-bottom:4px;'><strong>${escapeHtml(label)}</strong></div>")
        if (party != null) {
            append("<div><strong>${escapeHtml(party.name)}</strong></div>")
            if (party.address.isNotBlank()) {
                append("<div>${escapeHtml(party.address).replace("\n", "<br/>")}</div>")
            }
            if (party.city.isNotBlank() || party.pin.isNotBlank()) {
                append("<div>${escapeHtml(party.city)}${if (party.pin.isNotBlank()) " - ${escapeHtml(party.pin)}" else ""}</div>")
            }
        } else {
            append("<div>$emptyText</div>")
        }
    }

    /**
     * Build the terms and conditions block.
     */
    fun buildTermsLines(rawTerms: String): String =
        rawTerms.lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .mapIndexed { index, line ->
                val prefix = if (line.first().isDigit()) "" else "${index + 1}. "
                "<span class='terms-line'>$prefix${escapeHtml(line)}</span>"
            }
            .joinToString("")

    fun buildTermsSection(rawTerms: String): String {
        if (rawTerms.isBlank()) return ""

        val termsLines = buildTermsLines(rawTerms)

        return """
        <div style='margin:16px 0;'>
          <strong>Terms & Conditions:</strong>
          <div style='margin-top:8px;'>
            $termsLines
          </div>
        </div>
        """.trimIndent()
    }
    
    /**
     * Base CSS shared by every generated A4 document. Document-specific rules
     * (title, tables, signatures) stay in each generator.
     */
    fun buildBaseStyles(): String = """
        * { margin:0; padding:0; box-sizing:border-box; }
        body { font-family:Arial,Helvetica,sans-serif; font-size:11px; color:#111; background:#fff; }
        @page { size: A4 portrait; margin: 10mm 12mm; }
        .page { width:100%; max-width:794px; margin:0 auto; padding:14px 18px; background:#fff; }
        .center { text-align:center; }
        .right { text-align:right; }
        .bold { font-weight:700; }
        .small-label { font-size:9px; color:#666; margin-bottom:4px; }
        .meta-value { font-size:11px; font-weight:700; margin-top:2px; }
        .section-body { line-height:1.45; font-size:11px; }
        .company-name { font-size:14px; font-weight:700; color:#000; display:block; margin-bottom:4px; }
        .buyer-name { font-size:12px; font-weight:700; margin-bottom:3px; }
        .seller-head { font-size:9px; color:#666; margin-bottom:6px; }
        .seller-grid { width:100%; border-collapse:collapse; table-layout:fixed; }
        .seller-grid td { border:none; padding:0; vertical-align:top; }
        .seller-logo { width:86px; padding-right:8px; }
        .seller-logo img { max-width:80px; max-height:80px; object-fit:contain; display:block; }
        .page-break { page-break-before:always; }
        .no-break-inside { page-break-inside:avoid; }
        .continuation-header { border-bottom:1px solid #000; padding:4px 0; margin-bottom:8px; font-size:10px; }
        .page-number { text-align:center; font-size:9px; color:#666; margin-top:8px; }
        @media print {
            body { -webkit-print-color-adjust:exact; }
            .page-break { page-break-before:always; }
            .no-break-inside { page-break-inside:avoid; }
        }
    """.trimIndent()

    /**
     * Build a continuation page header for multi-page documents.
     * Shows business name, document title, and document number.
     */
    fun buildContinuationHeader(
        businessName: String,
        documentTitle: String,
        documentNumber: String
    ): String {
        return """
        <div class='continuation-header'>
            <table style='width:100%;border-collapse:collapse;'>
                <tr>
                    <td style='border:none;padding:2px 0;width:40%;'><strong>${escapeHtml(businessName)}</strong></td>
                    <td style='border:none;padding:2px 0;width:30%;text-align:center;'>${escapeHtml(documentTitle)}</td>
                    <td style='border:none;padding:2px 0;width:30%;text-align:right;'>${escapeHtml(documentNumber)}</td>
                </tr>
            </table>
        </div>
        """.trimIndent()
    }
    
    /**
     * Build page number display.
     */
    fun buildPageNumber(currentPage: Int, totalPages: Int): String {
        return if (totalPages > 1) {
            "<div class='page-number'>Page $currentPage of $totalPages</div>"
        } else {
            ""
        }
    }
}
