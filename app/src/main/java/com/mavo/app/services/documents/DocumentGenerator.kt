package com.mavo.app.services.documents

import com.mavo.app.data.BusinessProfile
import com.mavo.app.data.Party
import com.mavo.app.data.Voucher
import com.mavo.app.data.VoucherItem
import com.mavo.app.services.InvoiceGenerator

/**
 * Interface for document-specific generators.
 * Each document type (Tax Invoice, Quotation, etc.) implements this interface
 * to provide its own HTML template and file naming convention.
 */
interface DocumentGenerator {

    /**
     * The document type this generator handles.
     */
    val documentType: DocumentType

    /**
     * Generate HTML for the document.
     * 
     * @param voucher The voucher data
     * @param items List of voucher items
     * @param business Business profile information
     * @param party Customer/supplier party information
     * @param extras Additional voucher render extras
     * @return Complete HTML string for the document
     */
    fun generateHtml(
        voucher: Voucher,
        items: List<VoucherItem>,
        business: BusinessProfile,
        party: Party?,
        extras: InvoiceGenerator.VoucherRenderExtras = InvoiceGenerator.VoucherRenderExtras()
    ): String
    
    /**
     * Generate the export file name for the document.
     * 
     * @param voucher The voucher data
     * @param financialYearCode The financial year code
     * @return File name without .pdf extension
     */
    fun getExportFileName(voucher: Voucher, financialYearCode: String): String {
        val sequence = voucher.voucherNo.substringAfterLast('/').padStart(5, '0')
        val yearCode = financialYearCode.replace("/", "-")
        return "${documentType.prefix}-$yearCode-$sequence"
    }
    
    /**
     * Get the display title for the document.
     * 
     * @param voucher The voucher data
     * @param business Business profile information
     * @return The document title to display
     */
    fun getDocumentTitle(voucher: Voucher, business: BusinessProfile): String {
        return documentType.displayName
    }
}
