package com.mavo.app.services.documents

/**
 * Enum defining all document types supported by Mavo.
 * Each document type maps to a specific voucher type and has its own template.
 */
enum class DocumentType(
    val displayName: String,
    val prefix: String,
    val voucherType: String
) {
    // Outbound Documents (issued by my business)
    TAX_INVOICE("TAX INVOICE", "SAL", "SALE"),
    QUOTATION("QUOTATION", "QUO", "QUOTATION"),
    PRO_FORMA_INVOICE("PRO FORMA INVOICE", "PFI", "PROFORMA"),
    SALES_ORDER("SALES ORDER", "SOR", "SALES_ORDER"),
    DELIVERY_CHALLAN("DELIVERY CHALLAN", "DC", "DELIVERY_CHALLAN"),
    CREDIT_NOTE("CREDIT NOTE", "CRN", "CREDIT_NOTE"),

    // Inbound Documents (received from supplier) - Not implemented yet
    PURCHASE_INVOICE("PURCHASE INVOICE", "PUR", "PURCHASE"),
    PURCHASE_ORDER("PURCHASE ORDER", "POR", "PURCHASE_ORDER"),
    GOODS_RECEIPT_NOTE("GOODS RECEIPT NOTE", "GRN", "GOODS_RECEIPT_NOTE"),
    DEBIT_NOTE("DEBIT NOTE", "DBN", "DEBIT_NOTE"),
    PURCHASE_RETURN("PURCHASE RETURN", "PRN", "PURCHASE_RETURN"),
    SALES_RETURN("SALES RETURN", "SRN", "SALE_RETURN"),

    // Internal Documents - Not implemented yet
    RECEIPT("RECEIPT", "RCP", "RECEIPT"),
    PAYMENT("PAYMENT", "PMT", "PAYMENT"),
    JOURNAL("JOURNAL", "JNL", "JOURNAL"),
    MATERIAL_TRANSFER_NOTE("MATERIAL TRANSFER NOTE", "STK", "MATERIAL_NOTE"),
    REJECTION_NOTE("REJECTION NOTE", "REJ", "REJECTION_NOTE"),
    PETTY_CASH("PETTY CASH VOUCHER", "PCV", "PETTY_CASH"),
    INCOME("INCOME", "INC", "INCOME"),
    EXPENSE("EXPENSE", "EXP", "EXPENSE"),
    BILLS_RECEIVABLE("BILLS RECEIVABLE", "VCH", "BILLS_RECEIVABLE"),
    BILLS_PAYABLE("BILLS PAYABLE", "VCH", "BILLS_PAYABLE"),
    REQUEST_FOR_QUOTATION("REQUEST FOR QUOTATION", "RFQ", "INQUIRY");

    companion object {
        private val voucherTypeToDocumentType: Map<String, DocumentType> = entries.associateBy { it.voucherType }

        /**
         * Resolve a DocumentType from a voucher type string.
         * Falls back to TAX_INVOICE if the voucher type is not recognized.
         */
        fun fromVoucherType(voucherType: String): DocumentType {
            return voucherTypeToDocumentType[voucherType] ?: TAX_INVOICE
        }
    }
}
