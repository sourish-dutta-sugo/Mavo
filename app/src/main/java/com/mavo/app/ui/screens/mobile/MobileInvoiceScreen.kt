package com.mavo.app.ui.screens.mobile

import android.content.Intent
import android.net.Uri
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.Send
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.mavo.app.services.ExportStorageManager
import com.mavo.app.services.InvoiceGenerator
import com.mavo.app.services.configureInvoiceWebView
import com.mavo.app.services.printInvoice
import com.mavo.app.services.shareInvoicePdf
import com.mavo.app.services.shareInvoicePdfToWhatsApp
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.screens.graphPaperCanvas
import com.mavo.app.ui.theme.AppColors
import kotlinx.coroutines.launch

/**
 * Full Mobile screen view for Invoice preview and generator.
 * Compact mobile layout with vertical action bar and touch-friendly controls.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MobileInvoiceScreen(
    viewModel: AppViewModel,
    voucherId: String,
    onNavigateBack: () -> Unit,
    onEditVoucher: (String) -> Unit,
    onCreateSaleFromVoucher: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var renderBundle by remember { mutableStateOf<InvoiceGenerator.InvoiceRenderBundle?>(null) }
    var isWorking by remember { mutableStateOf(false) }
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var lastGeneratedPdf by remember { mutableStateOf<java.io.File?>(null) }
    var pendingSaveAsFile by remember { mutableStateOf<java.io.File?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val savePdfLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/pdf")
    ) { uri ->
        val sourceFile = pendingSaveAsFile
        pendingSaveAsFile = null
        if (uri == null || sourceFile == null) return@rememberLauncherForActivityResult
        scope.launch {
            runCatching {
                ExportStorageManager.writeFileToUri(context, sourceFile, uri)
                val action = snackbarHostState.showSnackbar(
                    message = "Invoice saved successfully",
                    actionLabel = "Open"
                )
                if (action == SnackbarResult.ActionPerformed) {
                    context.startActivity(
                        Intent(Intent.ACTION_VIEW).apply {
                            setDataAndType(uri, "application/pdf")
                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                        }
                    )
                }
            }.onFailure {
                Toast.makeText(context, it.message ?: "Failed to save invoice", Toast.LENGTH_LONG).show()
            }
        }
    }

    fun refreshBundle() {
        scope.launch {
            isWorking = true
            renderBundle = viewModel.getInvoiceRenderBundle(voucherId)
            isWorking = false
        }
    }

    LaunchedEffect(voucherId) {
        refreshBundle()
    }

    DisposableEffect(Unit) {
        onDispose {
            webViewRef?.apply {
                stopLoading()
                destroy()
            }
            webViewRef = null
        }
    }

    fun runPdfAction(action: suspend (InvoiceGenerator.InvoiceRenderBundle, java.io.File) -> Unit) {
        scope.launch {
            isWorking = true
            val freshBundle = viewModel.getInvoiceRenderBundle(voucherId)
            if (freshBundle == null) {
                isWorking = false
                Toast.makeText(context, "Failed to load latest invoice data", Toast.LENGTH_SHORT).show()
                return@launch
            }
            renderBundle = freshBundle
            runCatching {
                val pdfFile = InvoiceGenerator.renderBundleToPdf(context, freshBundle)
                lastGeneratedPdf = pdfFile
                action(freshBundle, pdfFile)
            }.onFailure {
                Toast.makeText(context, it.message ?: "Invoice action failed", Toast.LENGTH_LONG).show()
            }
            isWorking = false
        }
    }

    Scaffold(
        modifier = modifier.graphPaperCanvas(),
        containerColor = Color.Transparent,
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            renderBundle?.document?.displayTitle ?: "Document Viewer",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            renderBundle?.document?.invoiceNumber ?: "",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                actions = {
                    IconButton(onClick = { webViewRef?.zoomOut() }) {
                        Icon(Icons.Outlined.Remove, contentDescription = "Zoom Out", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { webViewRef?.zoomIn() }) {
                        Icon(Icons.Outlined.Add, contentDescription = "Zoom In", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { refreshBundle() }) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "Regenerate", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { onEditVoucher(voucherId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        val bundle = renderBundle
        if (bundle == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AppColors.primary)
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    AndroidView(
                        modifier = Modifier.fillMaxSize(),
                        factory = { ctx ->
                            WebView(ctx).apply {
                                configureInvoiceWebView(this)
                                webViewClient = WebViewClient()
                                webViewRef = this
                            }
                        },
                        update = { webView ->
                            webViewRef = webView
                            webView.loadDataWithBaseURL("file:///", bundle.html, "text/html", "UTF-8", null)
                        }
                    )
                }

                if (isWorking) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = AppColors.primary)
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surface,
                    tonalElevation = 2.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = 12.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        MobileInvoiceActionButton(
                            icon = Icons.Outlined.Share,
                            label = "Share",
                            enabled = !isWorking,
                            onClick = {
                                runPdfAction { _, pdfFile -> shareInvoicePdf(context, pdfFile) }
                            }
                        )
                        MobileInvoiceActionButton(
                            icon = Icons.Outlined.Download,
                            label = "Save As",
                            tint = AppColors.primary,
                            enabled = !isWorking,
                            onClick = {
                                runPdfAction { _, pdfFile ->
                                    pendingSaveAsFile = pdfFile
                                    savePdfLauncher.launch(bundle.exportFileName)
                                }
                            }
                        )
                        MobileInvoiceActionButton(
                            icon = Icons.Outlined.Print,
                            label = "Print",
                            enabled = !isWorking,
                            onClick = {
                                scope.launch {
                                    isWorking = true
                                    val freshBundle = viewModel.getInvoiceRenderBundle(voucherId)
                                    if (freshBundle != null) {
                                        renderBundle = freshBundle
                                        runCatching {
                                            printInvoice(context, freshBundle.html, freshBundle.document.invoiceNumber)
                                        }.onFailure {
                                            Toast.makeText(context, it.message ?: "Print failed", Toast.LENGTH_LONG).show()
                                        }
                                    }
                                    isWorking = false
                                }
                            }
                        )
                        MobileInvoiceActionButton(
                            icon = Icons.AutoMirrored.Outlined.Send,
                            label = "WhatsApp",
                            enabled = !isWorking,
                            onClick = {
                                runPdfAction { _, pdfFile ->
                                    shareInvoicePdfToWhatsApp(context, pdfFile)
                                }
                            }
                        )
                        MobileInvoiceActionButton(
                            icon = Icons.Outlined.Close,
                            label = "Close",
                            enabled = !isWorking,
                            onClick = onNavigateBack
                        )
                    }
                }
                if (bundle.document.voucher.type in setOf("QUOTATION", "DELIVERY_CHALLAN", "SALES_ORDER", "PROFORMA", "PURCHASE_ORDER", "GOODS_RECEIPT_NOTE")) {
                    Button(
                        onClick = { onCreateSaleFromVoucher(voucherId) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary)
                    ) {
                        val convertLabel = when (bundle.document.voucher.type) {
                            "QUOTATION" -> "Convert to Invoice"
                            "DELIVERY_CHALLAN" -> "Convert Challan to Invoice"
                            "SALES_ORDER" -> "Convert to Invoice"
                            "PROFORMA" -> "Convert to Tax Invoice"
                            "PURCHASE_ORDER" -> "Convert to Purchase"
                            "GOODS_RECEIPT_NOTE" -> "Convert to Purchase"
                            else -> "Convert"
                        }
                        Text(
                            convertLabel,
                            color = AppColors.textOnPrimary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MobileInvoiceActionButton(
    icon: ImageVector,
    label: String,
    enabled: Boolean,
    tint: Color = AppColors.textSecondary,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .width(64.dp)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (enabled) tint else AppColors.textTertiary,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            color = if (enabled) tint else AppColors.textTertiary,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}
