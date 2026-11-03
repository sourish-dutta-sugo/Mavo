package com.mavo.app.services.documents.outbound

import com.mavo.app.data.BusinessProfile
import com.mavo.app.data.Party
import com.mavo.app.data.Voucher
import com.mavo.app.data.VoucherItem
import com.mavo.app.services.InvoiceGenerator
import com.mavo.app.services.documents.DocumentGenerator
import com.mavo.app.services.documents.DocumentHtmlComponents
import com.mavo.app.services.documents.DocumentHtmlComponents.buildBusinessHeader
import com.mavo.app.services.documents.DocumentHtmlComponents.buildPartySection
import com.mavo.app.services.documents.DocumentHtmlComponents.buildTermsSection
import com.mavo.app.services.documents.DocumentHtmlComponents.escapeHtml
import com.mavo.app.services.documents.DocumentHtmlComponents.formatMoney
import com.mavo.app.services.documents.DocumentHtmlComponents.formatQty
import com.mavo.app.services.documents.DocumentHtmlComponents.toFileUrl
import com.mavo.app.services.documents.DocumentType
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Generator for Quotation documents.
 *
 * Reference design characteristics:
 * - Large "QUOTATION" title at top in bordered box
 * - Business header with logo and address
 * - To: (customer details) on left
 * - Date, Quotation Number, Valid Until on right
 * - Item table with: No., Item Description, Qty, Unit Price, Total
 * - Sub-Total, CGST, SGST, Grand Total
 * - Terms & Conditions section
 * - Authorized Signature at bottom right
 */
class QuotationGenerator : DocumentGenerator {

    override val documentType = DocumentType.QUOTATION

    override fun generateHtml(
        voucher: Voucher,
        items: List<VoucherItem>,
        business: BusinessProfile,
        party: Party?,
        extras: InvoiceGenerator.VoucherRenderExtras
    ): String = quotationFamilyHtml(
        voucher = voucher,
        items = items,
        business = business,
        party = party,
        title = documentType.displayName,
        taxLabelPrefix = "",
        noticeHtml = "",
        validUntil = extras.creditDueDate
    )
}

/**
 * Shared body for the Quotation / Pro Forma Invoice family: same layout, with
 * per-document title, tax labels, optional notice box and Valid-Until row.
 *
 * `taxLabelPrefix` ("Estimated ") is prepended to CGST/IGST labels;
 * `validUntil` blank renders "-" but null removes the row.
 */
internal fun quotationFamilyHtml(
    voucher: Voucher,
    items: List<VoucherItem>,
    business: BusinessProfile,
    party: Party?,
    title: String,
    taxLabelPrefix: String,
    noticeHtml: String,
    validUntil: String?
): String {
    val dateStr = SimpleDateFormat("dd-MMM-yy", Locale.ENGLISH).format(Date(voucher.date))
    val showLogo = business.showLogo && !business.logoPath.isNullOrBlank()
        && java.io.File(business.logoPath!!).exists()
    val showSignature = business.showSignature && !business.signaturePath.isNullOrBlank()
        && java.io.File(business.signaturePath!!).exists()

    // Calculate totals
    val totalTaxableAmount = items.sumOf { it.taxableAmount }
    val totalCgst = items.sumOf { it.cgstAmount }
    val totalSgst = items.sumOf { it.sgstAmount }
    val totalIgst = items.sumOf { it.igstAmount }
    val grandTotal = totalTaxableAmount + totalCgst + totalSgst + totalIgst

    // Build item rows
    val itemRows = items.mapIndexed { index, item ->
        val total = item.taxableAmount + item.cgstAmount + item.sgstAmount + item.igstAmount
        """
        <tbody class='no-break-inside'>
        <tr>
          <td class='center'>${index + 1}</td>
          <td>${escapeHtml(item.productName)}</td>
          <td class='center'>${formatQty(item.qty)}</td>
          <td class='right'>${formatMoney(item.rate)}</td>
          <td class='right'>${formatMoney(total)}</td>
        </tr>
        </tbody>
        """.trimIndent()
    }.joinToString("\n")

    // Build business header
    val businessHeaderHtml = buildBusinessHeader(business, showLogo)

    // Build party section
    val partyHtml = buildPartySection(party)

    // Build terms section
    val termsHtml = buildTermsSection(business.termsAndConditions)

    // Signature html
    val signatureHtml = if (showSignature) {
        "<img src='${toFileUrl(business.signaturePath!!)}' style='max-height:62px;max-width:180px;object-fit:contain;display:block;margin-left:auto;'/>"
    } else {
        "<div style='height:62px;'></div>"
    }

    val validUntilRow = validUntil?.let {
        """
            <tr>
              <td style='border:none;padding:2px 0;'><strong>Valid Until</strong></td>
              <td style='border:none;padding:2px 0;'>: ${escapeHtml(it.ifBlank { "-" })}</td>
            </tr>
        """.trimIndent()
    } ?: ""

    return """
    <!DOCTYPE html>
    <html>
    <head>
      <meta charset='UTF-8'/>
      <meta name='viewport' content='width=device-width, initial-scale=1.0'/>
      <style>
        ${DocumentHtmlComponents.buildBaseStyles()}
        table { width:100%; border-collapse:collapse; }
        td, th { border:1px solid #000; padding:6px 8px; vertical-align:top; word-wrap:break-word; overflow-wrap:break-word; }
        .title { text-align:center; font-size:18px; font-weight:700; letter-spacing:0.5px; padding:8px 0; border:2px solid #000; margin-bottom:12px; }
        .notice { text-align:center; font-size:11px; font-weight:bold; color:#000; margin:8px 0; padding:4px; border:1px solid #000; }
        .items-header th { background:#eef2fa; font-size:10px; font-weight:700; text-align:center; border:1px solid #000; }
        .terms-line { display:block; line-height:1.5; font-size:10px; }
        .signature { margin-top:40px; text-align:right; }
        .signature-line { border-top:1px solid #000; width:200px; display:inline-block; margin-top:60px; }
      </style>
    </head>
    <body>
      <div class='page'>
        <div class='title'>$title</div>
        $businessHeaderHtml

        <table style='width:100%;border-collapse:collapse;margin:12px 0;'>
          <tr>
            <td style='width:50%;border:none;vertical-align:top;'>
              $partyHtml
            </td>
            <td style='width:50%;border:none;vertical-align:top;'>
              <table style='width:100%;border-collapse:collapse;'>
                <tr>
                  <td style='border:none;padding:2px 0;'><strong>Date</strong></td>
                  <td style='border:none;padding:2px 0;'>: ${escapeHtml(dateStr)}</td>
                </tr>
                <tr>
                  <td style='border:none;padding:2px 0;'><strong>Quotation Number</strong></td>
                  <td style='border:none;padding:2px 0;'>: ${escapeHtml(voucher.voucherNo)}</td>
                </tr>
                $validUntilRow
              </table>
            </td>
          </tr>
        </table>

        $noticeHtml
        <table style='width:100%;border-collapse:collapse;margin:12px 0;'>
          <tr class='items-header'>
            <th style='width:6%;'>No.</th>
            <th style='width:40%; text-align:left;'>Item Description</th>
            <th style='width:14%;'>Qty</th>
            <th style='width:18%;'>Unit Price</th>
            <th style='width:22%;'>Total</th>
          </tr>
          $itemRows
        </table>

        <table style='width:100%;border-collapse:collapse;margin:12px 0;'>
          <tr>
            <td style='width:60%;border:none;'></td>
            <td style='width:20%;border:1px solid #000;padding:4px 8px;text-align:right;'><strong>Sub-Total</strong></td>
            <td style='width:20%;border:1px solid #000;padding:4px 8px;text-align:right;'>${formatMoney(totalTaxableAmount)}</td>
          </tr>
          ${if (totalCgst > 0) """
          <tr>
            <td style='border:none;'></td>
            <td style='width:20%;border:1px solid #000;padding:4px 8px;text-align:right;'>${taxLabelPrefix}CGST</td>
            <td style='width:20%;border:1px solid #000;padding:4px 8px;text-align:right;'>${formatMoney(totalCgst)}</td>
          </tr>
          """ else ""}
          ${if (totalSgst > 0) """
          <tr>
            <td style='border:none;'></td>
            <td style='width:20%;border:1px solid #000;padding:4px 8px;text-align:right;'>SGST</td>
            <td style='width:20%;border:1px solid #000;padding:4px 8px;text-align:right;'>${formatMoney(totalSgst)}</td>
          </tr>
          """ else ""}
          ${if (totalIgst > 0) """
          <tr>
            <td style='border:none;'></td>
            <td style='width:20%;border:1px solid #000;padding:4px 8px;text-align:right;'>${taxLabelPrefix}IGST</td>
            <td style='width:20%;border:1px solid #000;padding:4px 8px;text-align:right;'>${formatMoney(totalIgst)}</td>
          </tr>
          """ else ""}
          <tr>
            <td style='border:none;'></td>
            <td style='width:20%;border:1px solid #000;padding:4px 8px;text-align:right;font-weight:bold;'>Grand Total</td>
            <td style='width:20%;border:1px solid #000;padding:4px 8px;text-align:right;font-weight:bold;'>${formatMoney(grandTotal)}</td>
          </tr>
        </table>

        $termsHtml

        <div class='signature'>
          <div>Authorized Signature</div>
          $signatureHtml
        </div>
      </div>
    </body>
    </html>
    """.trimIndent()
}
