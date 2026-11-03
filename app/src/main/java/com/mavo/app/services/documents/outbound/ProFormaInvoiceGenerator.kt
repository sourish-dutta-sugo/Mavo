package com.mavo.app.services.documents.outbound

import com.mavo.app.data.BusinessProfile
import com.mavo.app.data.Party
import com.mavo.app.data.Voucher
import com.mavo.app.data.VoucherItem
import com.mavo.app.services.InvoiceGenerator
import com.mavo.app.services.documents.DocumentGenerator
import com.mavo.app.services.documents.DocumentType

/**
 * Generator for Pro Forma Invoice documents.
 *
 * Reference design characteristics (bordered title, business header, items table,
 * terms, signature) are shared with [QuotationGenerator] via [quotationFamilyHtml];
 * "THIS IS NOT A TAX INVOICE" notice box (no Valid Until row).
 */
class ProFormaInvoiceGenerator : DocumentGenerator {

    override val documentType = DocumentType.PRO_FORMA_INVOICE

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
        taxLabelPrefix = "Estimated ",
        noticeHtml = "<div class='notice'>THIS IS NOT A TAX INVOICE. For advance payment only.</div>",
        validUntil = null
    )
}
