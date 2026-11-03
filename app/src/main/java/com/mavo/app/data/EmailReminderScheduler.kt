package com.mavo.app.data

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.mavo.app.services.EmailComposer
import com.mavo.app.services.InvoiceGenerator
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit
import javax.activation.DataHandler
import javax.activation.FileDataSource
import javax.mail.Authenticator
import javax.mail.Message
import javax.mail.PasswordAuthentication
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeBodyPart
import javax.mail.internet.MimeMessage
import javax.mail.internet.MimeMultipart
import java.util.Properties

private const val REMINDER_PREFS = "mavo_pref"
private const val KEY_LOGS = "email_logs_list"
private const val KEY_USE_CUSTOM_TEMPLATE = "email_use_custom_template"
private const val KEY_CUSTOM_TEMPLATE = "email_custom_template"
private const val DUE_REMINDER_CHANNEL_ID = "payment_due_reminders"

private const val DEFAULT_TEMPLATE = """
Dear {party_name},

This is a reminder for invoice {invoice_no}.
Amount due: {amount}
Due date: {due_date}
Outstanding total: {outstanding}

Regards,
{business_name}
{business_phone}
"""

data class ReminderRecipientUi(
    val partyId: String,
    val partyName: String,
    val email: String,
    val dueAmount: Double
)

object EmailReminderScheduler {

    fun loadTemplate(context: Context): String =
        context.getSharedPreferences(REMINDER_PREFS, Context.MODE_PRIVATE)
            .getString(KEY_CUSTOM_TEMPLATE, DEFAULT_TEMPLATE)
            .orEmpty()
            .ifBlank { DEFAULT_TEMPLATE }


    fun previewTemplate(
        template: String = DEFAULT_TEMPLATE,
        partyName: String = "Agarwal Traders",
        invoiceNo: String = "SAL/2026-27/0001",
        amount: String = "Rs 5,000.00",
        dueDate: String = "15-Jun-2026",
        outstanding: String = "Rs 8,500.00",
        businessName: String = "Mavo",
        businessPhone: String = "9876543210"
    ): String = template
        .replace("{party_name}", partyName)
        .replace("{invoice_no}", invoiceNo)
        .replace("{amount}", amount)
        .replace("{due_date}", dueDate)
        .replace("{outstanding}", outstanding)
        .replace("{business_name}", businessName)
        .replace("{business_phone}", businessPhone)

    fun scheduleDueReminder(
        context: Context,
        voucherId: String,
        partyEmail: String,
        creditDueDate: String
    ) {
        if (voucherId.isBlank() || creditDueDate.isBlank()) return
        val dueDateMillis = parseReminderDateToMillis(creditDueDate) ?: return
        val dueDelayMs = (dueDateMillis - System.currentTimeMillis()).coerceAtLeast(0L)
        val warningDelayMs = dueDelayMs - TimeUnit.DAYS.toMillis(3)
        val dueData = workDataOf(
            "voucher_id" to voucherId,
            "party_email" to partyEmail,
            "is_warning" to false
        )
        if (dueDelayMs > 0L) {
            val request = OneTimeWorkRequestBuilder<DueReminderWorker>()
                .setInputData(dueData)
                .setInitialDelay(dueDelayMs, TimeUnit.MILLISECONDS)
                .addTag("due_reminder")
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "due_$voucherId",
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
        if (warningDelayMs > 0L) {
            val warningRequest = OneTimeWorkRequestBuilder<DueReminderWorker>()
                .setInputData(
                    workDataOf(
                        "voucher_id" to voucherId,
                        "party_email" to partyEmail,
                        "is_warning" to true
                    )
                )
                .setInitialDelay(warningDelayMs, TimeUnit.MILLISECONDS)
                .addTag("due_warning")
                .build()
            WorkManager.getInstance(context).enqueueUniqueWork(
                "warn_$voucherId",
                ExistingWorkPolicy.REPLACE,
                warningRequest
            )
        }
    }

    suspend fun processReminders(
        context: Context,
        partyId: String? = null,
        scheduleId: String? = null,
        reminderType: String = "MANUAL"
    ): Int {
        val db = AppDatabase.getDatabase(context)
        val profile = db.businessProfileDao().getProfileSync()
        val businessName = profile?.businessName?.ifBlank { "Mavo" } ?: "Mavo"
        val activeFinancialYearCode = profile?.fyLabel?.takeIf { it.isNotBlank() }
            ?: FinancialYearUtils.currentFinancialYearCode()
        val allBills = db.billReceivableDao().getAllBillsSync(activeFinancialYearCode)
            .filter { it.outstandingAmount > 0.0 }
        val groupedBills = if (partyId.isNullOrBlank()) {
            allBills.groupBy { it.partyId }
        } else {
            allBills.filter { it.partyId == partyId }.groupBy { it.partyId }
        }

        if (groupedBills.isEmpty()) {
            appendLogs(context, listOf("${formatNow()} | $reminderType | No outstanding dues found"))
            updateScheduleLastSent(context, scheduleId, active = reminderType == "RECURRING")
            return 0
        }

        val processedRows = mutableListOf<String>()
        groupedBills.forEach { (duePartyId, bills) ->
            val party = db.partyDao().getPartyById(duePartyId) ?: return@forEach
            val recipientEmail = party.email.trim()
            val body = buildReminderBody(
                context = context,
                partyName = party.name,
                bills = bills,
                businessName = businessName,
                businessPhone = profile?.phone.orEmpty(),
                businessEmail = profile?.email.orEmpty()
            )
            val subject = "Payment reminder from $businessName"
            launchEmailDraft(context, recipientEmail, subject, body)
            processedRows += "${formatNow()} | $reminderType | ${party.name} | Total ${Utils.formatIndianCurrency(bills.sumOf { it.outstandingAmount })}"
        }

        appendLogs(context, processedRows)
        updateScheduleLastSent(context, scheduleId, active = reminderType == "RECURRING")
        return processedRows.size
    }

    private fun launchEmailDraft(context: Context, email: String, subject: String, body: String) {
        if (email.isBlank()) return
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        runCatching { context.startActivity(Intent.createChooser(intent, "Send reminder").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)) }
    }

    private fun buildReminderBody(
        context: Context,
        partyName: String,
        bills: List<BillReceivable>,
        businessName: String,
        businessPhone: String,
        businessEmail: String
    ): String {
        val dateFormat = SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH)
        val invoiceLines = bills.joinToString("\n") { bill ->
            val invoiceDate = dateFormat.format(Date(bill.billDate))
            "Invoice ${bill.voucherNo.orEmpty()} dated $invoiceDate - Due ${Utils.formatIndianCurrency(bill.outstandingAmount)}"
        }
        val totalDue = bills.sumOf { it.outstandingAmount }
        return previewTemplate(
            template = loadTemplate(context),
            partyName = partyName,
            invoiceNo = bills.firstOrNull()?.voucherNo.orEmpty(),
            amount = bills.firstOrNull()?.outstandingAmount?.let(Utils::formatIndianCurrency).orEmpty(),
            dueDate = bills.firstOrNull()?.dueDate?.let { dateFormat.format(Date(it)) }.orEmpty(),
            outstanding = Utils.formatIndianCurrency(totalDue),
            businessName = businessName,
            businessPhone = businessPhone
        ) + "\n\nOutstanding invoices:\n$invoiceLines" + if (businessEmail.isNotBlank()) "\n\nEmail: $businessEmail" else ""
    }

    private fun updateScheduleLastSent(context: Context, scheduleId: String?, active: Boolean) {
        if (scheduleId.isNullOrBlank()) return
        val db = AppDatabase.getDatabase(context)
        db.openHelper.writableDatabase.execSQL(
            """
            UPDATE reminder_schedules
            SET last_sent = ?, is_active = ?
            WHERE id = ?
            """.trimIndent(),
            arrayOf<Any?>(
                LocalDateTime.now().toString(),
                if (active) 1 else 0,
                scheduleId
            )
        )
    }

    private fun appendLogs(context: Context, rows: List<String>) {
        val prefs = context.getSharedPreferences(REMINDER_PREFS, Context.MODE_PRIVATE)
        val existing = prefs.getString(KEY_LOGS, "").orEmpty()
        val joined = rows.joinToString("\n")
        val finalLogs = if (existing.isBlank()) joined else "$joined\n$existing"
        prefs.edit().putString(KEY_LOGS, finalLogs).apply()
    }

    private fun formatNow(): String =
        SimpleDateFormat("dd-MMM-yyyy hh:mm a", Locale.ENGLISH).format(Date())

    private fun parseReminderDateToMillis(raw: String): Long? {
        raw.toLongOrNull()?.let {
            return java.util.Calendar.getInstance().apply {
                timeInMillis = it
                set(java.util.Calendar.HOUR_OF_DAY, 9)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
        val patterns = listOf("dd-MMM-yyyy", "dd-MMM-yy", "dd-MM-yyyy")
        return patterns.firstNotNullOfOrNull { pattern ->
            runCatching {
                SimpleDateFormat(pattern, Locale.ENGLISH).parse(raw)?.time
            }.getOrNull()
        }?.let { parsed ->
            java.util.Calendar.getInstance().apply {
                timeInMillis = parsed
                set(java.util.Calendar.HOUR_OF_DAY, 9)
                set(java.util.Calendar.MINUTE, 0)
                set(java.util.Calendar.SECOND, 0)
                set(java.util.Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    }
}

class ReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    override suspend fun doWork(): Result {
        val partyId = inputData.getString("party_id")
        val scheduleId = inputData.getString("schedule_id")
        val reminderType = inputData.getString("reminder_type").orEmpty().ifBlank { "WORKER" }
        return runCatching {
            EmailReminderScheduler.processReminders(
                context = applicationContext,
                partyId = partyId,
                scheduleId = scheduleId,
                reminderType = reminderType
            )
        }.fold(
            onSuccess = { Result.success() },
            onFailure = { Result.retry() }
        )
    }
}

class DueReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    @android.annotation.SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        val voucherId = inputData.getString("voucher_id").orEmpty()
        val fallbackEmail = inputData.getString("party_email").orEmpty()
        val isWarning = inputData.getBoolean("is_warning", false)
        if (voucherId.isBlank()) return Result.success()

        return runCatching {
            val bundle = InvoiceGenerator.buildRenderBundleWithTypeAware(applicationContext, voucherId) ?: return Result.success()
            val balanceDue = bundle.document.paymentSnapshot.balanceDue.coerceAtLeast(0.0)
            if (balanceDue <= 0.0) return Result.success()

            val pdfFile = InvoiceGenerator.renderBundleToPdf(applicationContext, bundle)
            val pdfUri = FileProvider.getUriForFile(
                applicationContext,
                applicationContext.packageName + ".provider",
                pdfFile
            )
            val business = bundle.document.business
            val partyName = bundle.document.buyer?.name ?: "Customer"
            val partyEmail = bundle.document.buyer?.email.orEmpty().ifBlank { fallbackEmail }
            if (partyEmail.isBlank()) {
                ensureDueReminderChannel(applicationContext)
                NotificationManagerCompat.from(applicationContext).notify(
                    voucherId.hashCode(),
                    NotificationCompat.Builder(applicationContext, DUE_REMINDER_CHANNEL_ID)
                        .setSmallIcon(android.R.drawable.ic_dialog_email)
                        .setContentTitle("No email for $partyName")
                        .setContentText("Party has no email address saved")
                        .setAutoCancel(true)
                        .build()
                )
                return Result.success()
            }
            val currency = DecimalFormat("#,##0.00")
            val subjectPrefix = if (isWarning) "Upcoming Payment Due" else "Payment Due"
            val subject = "$subjectPrefix - Invoice ${bundle.document.invoiceNumber} - \u20B9${currency.format(balanceDue)} - From ${business.businessName}"
            val body = """
                Dear $partyName,

                ${if (isWarning) "This is a friendly reminder that the following invoice will become due soon." else "This is a payment reminder for the following unpaid invoice."}

                Invoice No: ${bundle.document.invoiceNumber}
                Invoice Date: ${bundle.document.issuedAtLabel}
                Invoice Total: \u20B9${currency.format(bundle.document.totals.netAmount)}
                Amount Paid: \u20B9${currency.format(bundle.document.paymentSnapshot.partPaymentReceived)}
                Balance Due: \u20B9${currency.format(balanceDue)}
                Due Date: ${bundle.document.dueDateLabel.ifBlank { "Not specified" }}

                A PDF copy of the invoice is attached for your reference.

                ${if (isWarning) "Please keep the payment ready before the due date." else "Please arrange payment at the earliest."}

                For any queries, please contact us at ${business.phone}${business.email.takeIf { it.isNotBlank() }?.let { " or $it" } ?: ""}.

                Regards,
                ${business.businessName}
                GSTIN: ${business.gstin}
            """.trimIndent()

            val smtpConfigured = business.smtpEmail.isNotBlank() && business.smtpPassword.isNotBlank()
            if (smtpConfigured) {
                runCatching {
                    sendViaSmtp(
                        smtpEmail = business.smtpEmail,
                        smtpPassword = business.smtpPassword,
                        smtpHost = business.smtpHost.ifBlank { "smtp.gmail.com" },
                        smtpPort = business.smtpPort.ifBlank { "587" },
                        to = partyEmail,
                        subject = subject,
                        body = body,
                        attachmentFile = pdfFile
                    )
                }.onSuccess {
                    ensureDueReminderChannel(applicationContext)
                    NotificationManagerCompat.from(applicationContext).notify(
                        voucherId.hashCode(),
                        NotificationCompat.Builder(applicationContext, DUE_REMINDER_CHANNEL_ID)
                            .setSmallIcon(android.R.drawable.ic_dialog_email)
                            .setContentTitle("Reminder sent")
                            .setContentText("Email sent to $partyName")
                            .setAutoCancel(true)
                            .build()
                    )
                    return Result.success()
                }
            }

            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                data = Uri.parse("mailto:")
                type = "application/pdf"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(partyEmail))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, body)
                putExtra(Intent.EXTRA_STREAM, pdfUri)
                clipData = ClipData.newRawUri("invoice", pdfUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val chooserIntent = Intent.createChooser(emailIntent, "Send payment reminder")
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val pendingIntent = PendingIntent.getActivity(
                applicationContext,
                voucherId.hashCode(),
                chooserIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            ensureDueReminderChannel(applicationContext)
            val notification = NotificationCompat.Builder(applicationContext, DUE_REMINDER_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_email)
                .setContentTitle(if (isWarning) "Payment due soon: $partyName" else "Payment due: $partyName")
                .setContentText("Tap to send reminder email. Due: \u20B9${currency.format(balanceDue)}")
                .setStyle(NotificationCompat.BigTextStyle().bigText(subject))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .addAction(android.R.drawable.ic_dialog_email, "Send Email", pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build()

            NotificationManagerCompat.from(applicationContext).notify(voucherId.hashCode(), notification)
            Result.success()
        }.getOrElse {
            Result.retry()
        }
    }

    private fun ensureDueReminderChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            DUE_REMINDER_CHANNEL_ID,
            "Payment Due Reminders",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "Notifications for invoice payment reminder emails"
        }
        manager.createNotificationChannel(channel)
    }
}

private fun sendViaSmtp(
    smtpEmail: String,
    smtpPassword: String,
    smtpHost: String,
    smtpPort: String,
    to: String,
    subject: String,
    body: String,
    attachmentFile: java.io.File?
) {
    val props = Properties().apply {
        put("mail.smtp.auth", "true")
        put("mail.smtp.starttls.enable", "true")
        put("mail.smtp.host", smtpHost)
        put("mail.smtp.port", smtpPort)
        put("mail.smtp.ssl.trust", smtpHost)
    }
    val session = Session.getInstance(props, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication =
            PasswordAuthentication(smtpEmail, smtpPassword)
    })
    val message = MimeMessage(session).apply {
        setFrom(InternetAddress(smtpEmail))
        setRecipients(Message.RecipientType.TO, InternetAddress.parse(to))
        setSubject(subject)
        if (attachmentFile != null) {
            val textPart = MimeBodyPart().apply { setText(body) }
            val attachmentPart = MimeBodyPart().apply {
                dataHandler = DataHandler(FileDataSource(attachmentFile))
                fileName = attachmentFile.name
            }
            setContent(MimeMultipart().apply {
                addBodyPart(textPart)
                addBodyPart(attachmentPart)
            })
        } else {
            setText(body)
        }
    }
    Transport.send(message)
}
