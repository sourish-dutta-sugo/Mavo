package com.mavo.app

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.People
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.filled.Add
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.RowScope
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mavo.app.data.AppPreferences
import com.mavo.app.data.ChangelogData
import com.mavo.app.data.ChangelogLoader
import com.mavo.app.ui.AppViewModel
import com.mavo.app.ui.components.ScreenBorder
import com.mavo.app.ui.DashboardViewModel
import com.mavo.app.ui.animation.NavBarContent
import com.mavo.app.ui.animation.clickableScale
import com.mavo.app.ui.animation.dialogEnter
import com.mavo.app.ui.animation.pressScale
import com.mavo.app.ui.animation.dialogExit
import com.mavo.app.ui.animation.enterTransition
import com.mavo.app.ui.animation.exitTransition
import com.mavo.app.ui.screens.BankCashScreen
import com.mavo.app.ui.screens.DashboardScreen
import com.mavo.app.ui.screens.ExpensesScreen
import com.mavo.app.ui.screens.InvoiceScreen
import com.mavo.app.ui.screens.LedgerListScreen
import com.mavo.app.ui.screens.LowStockScreen
import com.mavo.app.ui.screens.NewVoucherScreen
import com.mavo.app.ui.screens.PartiesScreen
import com.mavo.app.ui.screens.PartyDetailScreen
import com.mavo.app.ui.screens.ProductsScreen
import com.mavo.app.feature.billing.BillingScreen
import com.mavo.app.ui.screens.ReportsScreen
import com.mavo.app.ui.screens.SettingsScreen
import com.mavo.app.ui.screens.FirstRunFlow
import com.mavo.app.ui.screens.SplashScreen
import com.mavo.app.ui.screens.VoucherDetailScreen
import com.mavo.app.ui.screens.VouchersScreen
import com.mavo.app.ui.theme.AppColors
import com.mavo.app.ui.theme.LocalAppTheme
import com.mavo.app.ui.theme.MavoTheme
import com.mavo.app.ui.theme.ThemeViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private object Routes {
    const val Dashboard = "dashboard"
    const val Vouchers = "vouchers"
    const val Parties = "parties"
    const val Settings = "settings"
    const val Reports = "reports"
    const val LedgerBooks = "ledger_books"
    const val Expenses = "expenses"
    const val CounterSale = "counter_sale"
    const val Products = "products"
    const val LowStock = "lowstock"
    const val BankCash = "bank_cash"
    const val NewVoucher = "new_voucher?voucherId={voucherId}"
    const val NewVoucherBase = "new_voucher"
    const val Invoice = "invoice/{voucherId}"
    const val VoucherDetail = "voucher_detail/{voucherId}"
    const val PartyDetail = "party_detail/{partyId}"
}

@androidx.compose.runtime.Immutable
private data class TopLevelDestination(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

private val topLevelDestinations = listOf(
    TopLevelDestination(Routes.Dashboard, "Home", Icons.Outlined.Home),
    TopLevelDestination(Routes.Vouchers, "Vouchers", Icons.Outlined.Description),
    TopLevelDestination(Routes.Parties, "Parties", Icons.Outlined.People),
    TopLevelDestination(Routes.Settings, "Settings", Icons.Outlined.Settings)
)

private fun newVoucherRoute(voucherId: String? = null): String =
    voucherId?.let { "${Routes.NewVoucherBase}?voucherId=$it" } ?: Routes.NewVoucherBase

    private fun invoiceRoute(voucherId: String): String = "invoice/$voucherId"

    private fun voucherDetailRoute(voucherId: String): String = "voucher_detail/$voucherId"

private fun partyDetailRoute(partyId: String): String = "party_detail/$partyId"

class MainActivity : ComponentActivity() {
    private val viewModel: AppViewModel by viewModels()
    private val themeViewModel: ThemeViewModel by viewModels()
    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            val currentTheme by themeViewModel.currentTheme.collectAsState()
            val windowInsetsController = remember(window) {
                WindowInsetsControllerCompat(window, window.decorView)
            }

            @Suppress("DEPRECATION")
            SideEffect {
                window.statusBarColor = currentTheme.statusBarColor.toArgb()
                windowInsetsController.isAppearanceLightStatusBars = currentTheme.statusBarDarkIcons
                window.navigationBarColor = currentTheme.backgroundPrimary.toArgb()
                windowInsetsController.isAppearanceLightNavigationBars = currentTheme.statusBarDarkIcons
            }

            CompositionLocalProvider(LocalAppTheme provides currentTheme) {
                MavoTheme(appTheme = currentTheme) {
                    MainAppEntry(viewModel, themeViewModel, dashboardViewModel)
                }
            }
        }
    }
}

@Composable
fun MainAppEntry(
    viewModel: AppViewModel,
    themeViewModel: ThemeViewModel,
    dashboardViewModel: DashboardViewModel
) {
    val context = LocalContext.current
    val dbState by viewModel.dbInitState.collectAsState()

    when (val state = dbState) {
        is AppViewModel.DbInitState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.screenBg),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator(color = AppColors.primary)
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Initializing Secure Database...",
                        fontSize = 14.sp,
                        color = AppColors.textPrimary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        is AppViewModel.DbInitState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(AppColors.screenBg)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AppColors.cardBg),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Database Connection Failed",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = AppColors.error
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = state.message,
                            fontSize = 14.sp,
                            color = AppColors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            modifier = Modifier.pressScale(),
                            onClick = {
                                if (context is android.app.Activity) {
                                    context.recreate()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.primary),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Text("Retry Connection", color = AppColors.textOnPrimary)
                        }
                    }
                }
            }
        }

        is AppViewModel.DbInitState.Success -> {
            AppContent(
                viewModel = viewModel,
                themeViewModel = themeViewModel,
                dashboardViewModel = dashboardViewModel
            )
        }
    }
}

@Composable
private fun AppContent(
    viewModel: AppViewModel,
    themeViewModel: ThemeViewModel,
    dashboardViewModel: DashboardViewModel
) {
    val context = LocalContext.current
    val isSetupCompleted by viewModel.isSetupCompleted.collectAsState()
    val setupStatusResolved by viewModel.setupStatusResolved.collectAsState()
    var showSplash by remember { mutableStateOf(true) }

    Crossfade(targetState = showSplash, animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)) { isSplash ->
        if (isSplash) {
            SplashScreen(onTimeout = { showSplash = false })
        } else {
            val sharedPreferences = remember(context) {
                context.getSharedPreferences("mavo_pref", Context.MODE_PRIVATE)
            }
            var pinRequired by remember {
                mutableStateOf(sharedPreferences.getBoolean("pin_enabled", false))
            }
            val correctPin = remember(sharedPreferences) {
                sharedPreferences.getString("lock_pin", "1234") ?: "1234"
            }
            var pinAuthed by remember { mutableStateOf(false) }
            var changelogData by remember { mutableStateOf<ChangelogData?>(null) }
            var showChangelog by remember { mutableStateOf(false) }
            val uiScope = rememberCoroutineScope()

            LaunchedEffect(isSetupCompleted, pinRequired, pinAuthed) {
                if (isSetupCompleted && (!pinRequired || pinAuthed)) {
                    viewModel.autoAdvanceFinancialYearIfNeeded(context)?.let { updatedFy ->
                        Toast.makeText(context, "Financial year updated to FY $updatedFy", Toast.LENGTH_LONG).show()
                    }
                    val loadedChangelog = ChangelogLoader.load(context)
                    val lastSeenVersion = AppPreferences.getLastSeenChangelogVersion(context)
                    if (loadedChangelog != null && (lastSeenVersion == null || lastSeenVersion != loadedChangelog.version)) {
                        changelogData = loadedChangelog
                        showChangelog = true
                    }
                    pinRequired = sharedPreferences.getBoolean("pin_enabled", false)
                }
            }

            when {
                !setupStatusResolved -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppColors.screenBg),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(RoundedCornerShape(24.dp))
                                .background(AppColors.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(R.drawable.logo_transparent),
                                contentDescription = null,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "INITIALIZING",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 3.sp,
                            color = AppColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        // ponytail: no real init progress % (DB open + status checks) — indeterminate bar.
                        // upgrade path: surface DAO migration progress via Flow.
                        LinearProgressIndicator(
                            modifier = Modifier
                                .width(200.dp)
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = AppColors.primary,
                            trackColor = AppColors.border
                        )
                    }
                }

                !isSetupCompleted -> {
                    FirstRunFlow(viewModel = viewModel)
                }

                pinRequired && !pinAuthed -> {
                    PinLockScreen(
                        correctPin = correctPin,
                        onAuthentic = { pinAuthed = true }
                    )
                }

                else -> {
                    val navController = rememberNavController()
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentDestination = navBackStackEntry?.destination
                    val isTopLevel = remember(currentDestination) {
                        topLevelDestinations.any { destination ->
                            currentDestination?.hierarchy?.any { it.route == destination.route } == true
                        }
                    }

                    Scaffold(
                        modifier = Modifier
                            .fillMaxSize()
                            .statusBarsPadding()
                            .navigationBarsPadding(),
                        containerColor = AppColors.screenBg,
                        bottomBar = {
                            if (isTopLevel) {
                                ZeroBottomBar(
                                    currentRoute = currentDestination?.route,
                                    onNavigate = { navController.navigateToTopLevel(it) },
                                    onCreate = { navController.navigate(newVoucherRoute()) }
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            MavoNavHost(
                                navController = navController,
                                viewModel = viewModel,
                                themeViewModel = themeViewModel,
                                dashboardViewModel = dashboardViewModel
                            )
                        }
                    }

                    if (showChangelog && changelogData != null) {
                        val configuration = LocalConfiguration.current
                        androidx.compose.animation.AnimatedVisibility(
                            visible = showChangelog,
                            enter = dialogEnter(),
                            exit = dialogExit()
                        ) {
                            androidx.compose.material3.AlertDialog(
                                onDismissRequest = {},
                                confirmButton = {
                                    Button(
                                        modifier = Modifier.pressScale(),
                                        onClick = {
                                            val version = changelogData?.version.orEmpty()
                                            if (version.isNotBlank()) {
                                                uiScope.launch {
                                                    AppPreferences.setLastSeenChangelogVersion(context, version)
                                                    showChangelog = false
                                                }
                                            } else {
                                                showChangelog = false
                                            }
                                        }
                                    ) {
                                        Text("Got it")
                                    }
                                },
                                title = {
                                    Text("What's New in Mavo ${changelogData?.version.orEmpty()}")
                                },
                                text = {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .heightIn(max = configuration.screenHeightDp.dp * 0.6f)
                                            .verticalScroll(rememberScrollState()),
                                        verticalArrangement = Arrangement.spacedBy(10.dp)
                                    ) {
                                        changelogData?.changes?.forEach { change ->
                                            Text("• $change", color = AppColors.textPrimary)
                                        }
                                    }
                                },
                                containerColor = Color.White,
                                textContentColor = AppColors.textPrimary,
                                titleContentColor = AppColors.textPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MavoNavHost(
    navController: NavHostController,
    viewModel: AppViewModel,
    themeViewModel: ThemeViewModel,
    dashboardViewModel: DashboardViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Dashboard,
        enterTransition = enterTransition(navigatingBack = false),
        exitTransition = exitTransition(navigatingBack = false),
        popEnterTransition = enterTransition(navigatingBack = true),
        popExitTransition = exitTransition(navigatingBack = true)
    ) {
        composable(Routes.Dashboard) {
            DashboardScreen(
                viewModel = viewModel,
                dashboardViewModel = dashboardViewModel,
                isDesktop = false,
                onQuickAction = { action ->
                    when (action) {
                        "SALE", "PURCHASE" -> navController.navigate(newVoucherRoute())
                        "RECEIPT", "PAYMENT" -> navController.navigate(Routes.BankCash)
                        "REPORTS" -> navController.navigate(Routes.Reports)
                        "LOW_STOCK" -> navController.navigate(Routes.LowStock)
                        "QUICK_SALE" -> navController.navigate(Routes.CounterSale)
                        "EXPENSES" -> navController.navigate(Routes.Expenses)
                        "PARTY" -> navController.navigateToTopLevel(Routes.Parties)
                        "VOUCHERS" -> navController.navigateToTopLevel(Routes.Vouchers)
                    }
                }
            )
        }

        composable(Routes.Vouchers) {
            VouchersScreen(
                viewModel = viewModel,
                isDesktop = false,
                navigateToNewVoucher = { id -> navController.navigate(newVoucherRoute(id)) },
                navigateToVoucherDetail = { id -> navController.navigate(voucherDetailRoute(id)) }
            )
        }

        composable(Routes.Parties) {
            PartiesScreen(
                viewModel = viewModel,
                isDesktop = false,
                onPartySelected = { id -> navController.navigate(partyDetailRoute(id)) }
            )
        }

        composable(Routes.Settings) {
            SettingsScreen(
                viewModel = viewModel,
                themeViewModel = themeViewModel,
                isDesktop = false,
                navigateToProducts = { navController.navigate(Routes.Products) },
                navigateToLedgerBooks = { navController.navigate(Routes.LedgerBooks) },
                navigateToParties = { navController.navigate(Routes.Parties) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Reports) {
            ReportsScreen(
                viewModel = viewModel,
                isDesktop = false,
                navigateToLedgerBooks = { navController.navigate(Routes.LedgerBooks) },
                navigateToExpenses = { navController.navigate(Routes.Expenses) },
                navigateToNewVoucher = { navController.navigate(newVoucherRoute(it)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Products) {
            ProductsScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToLowStock = { navController.navigate(Routes.LowStock) }
            )
        }

        composable(Routes.LowStock) {
            val products by viewModel.products.collectAsState()
            LowStockScreen(
                products = products,
                onBack = { navController.popBackStack() },
                onReorder = {
                    viewModel.setVoucherPrefillRequest(
                        AppViewModel.VoucherPrefillRequest(
                            voucherType = "PURCHASE",
                            partyId = null,
                            invoiceId = null,
                            amount = null
                        )
                    )
                    navController.navigate(newVoucherRoute())
                }
            )
        }

        composable(Routes.BankCash) {
            BankCashScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Expenses) {
            ExpensesScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.CounterSale) {
            BillingScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.NewVoucher,
            arguments = listOf(
                navArgument("voucherId") {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { entry ->
            NewVoucherScreen(
                viewModel = viewModel,
                voucherId = entry.arguments?.getString("voucherId"),
                isDesktop = false,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToInvoice = { voucherId ->
                    navController.popBackStack()
                    navController.navigate(invoiceRoute(voucherId))
                },
                onNavigateToPartyDetail = { id -> navController.navigate(partyDetailRoute(id)) }
            )
        }

        composable(
            route = Routes.Invoice,
            arguments = listOf(navArgument("voucherId") { type = NavType.StringType })
        ) { entry ->
            val voucherId = entry.arguments?.getString("voucherId").orEmpty()
            InvoiceScreen(
                viewModel = viewModel,
                voucherId = voucherId,
                onNavigateBack = { navController.popBackStack() },
                onEditVoucher = { id -> navController.navigate(newVoucherRoute(id)) },
                onCreateSaleFromVoucher = { sourceVoucherId ->
                    val sourceVoucher = viewModel.getVoucherById(sourceVoucherId)
                    val targetType = if (sourceVoucher?.type in setOf("PURCHASE_ORDER", "GOODS_RECEIPT_NOTE")) "PURCHASE" else "SALE"
                    viewModel.setVoucherPrefillRequest(
                        AppViewModel.VoucherPrefillRequest(
                            voucherType = targetType,
                            partyId = sourceVoucher?.partyId,
                            invoiceId = null,
                            amount = null,
                            sourceVoucherId = sourceVoucherId
                        )
                    )
                    navController.navigate(newVoucherRoute())
                }
            )
        }

        composable(
            route = Routes.VoucherDetail,
            arguments = listOf(navArgument("voucherId") { type = NavType.StringType })
        ) { entry ->
            val voucherId = entry.arguments?.getString("voucherId").orEmpty()
            VoucherDetailScreen(
                viewModel = viewModel,
                voucherId = voucherId,
                onBack = { navController.popBackStack() },
                onEdit = { navController.navigate(newVoucherRoute(voucherId)) },
                onOpenInvoice = { navController.navigate(invoiceRoute(voucherId)) }
            )
        }

        composable(Routes.LedgerBooks) {
            LedgerListScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.PartyDetail,
            arguments = listOf(navArgument("partyId") { type = NavType.StringType })
        ) { entry ->
            PartyDetailScreen(
                viewModel = viewModel,
                partyId = entry.arguments?.getString("partyId").orEmpty(),
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

private fun NavHostController.navigateToTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

@Composable
private fun ZeroBottomBar(
    currentRoute: String?,
    onNavigate: (String) -> Unit,
    onCreate: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.bottomBarBg)
    ) {
        HorizontalDivider(color = ScreenBorder)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(78.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomTab(topLevelDestinations[0], currentRoute, onNavigate)
            BottomTab(topLevelDestinations[1], currentRoute, onNavigate)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(AppColors.primary)
                        .shadow(6.dp, CircleShape)
                        .clickable(onClick = onCreate),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Voucher",
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
            BottomTab(topLevelDestinations[2], currentRoute, onNavigate)
            BottomTab(topLevelDestinations[3], currentRoute, onNavigate)
        }
    }
}

@Composable
private fun RowScope.BottomTab(
    destination: TopLevelDestination,
    currentRoute: String?,
    onNavigate: (String) -> Unit
) {
    val selected = currentRoute == destination.route
    Column(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clickable { if (!selected) onNavigate(destination.route) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(width = 40.dp, height = 3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(if (selected) AppColors.primary else Color.Transparent)
        )
        Spacer(modifier = Modifier.weight(1f))
        Box(
            modifier = Modifier
                .size(width = 46.dp, height = 36.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (selected) AppColors.primary else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = destination.icon,
                contentDescription = destination.label,
                modifier = Modifier.size(22.dp),
                tint = if (selected) Color.White else AppColors.textTertiary
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = destination.label,
            fontSize = 11.sp,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
            color = if (selected) AppColors.textPrimary else AppColors.textTertiary
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun PinLockScreen(
    correctPin: String,
    onAuthentic: () -> Unit
) {
    var enteredText by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A5C4B))
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Set your access PIN",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = "Keep your books secure",
            fontSize = 13.sp,
            color = Color(0xFF9CA3AF),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            repeat(4) { index ->
                val active = index < enteredText.length
                Box(
                    modifier = Modifier
                        .size(14.dp)
                        .background(
                            color = if (active) Color.White else Color(0xFF3AAA87),
                            shape = RoundedCornerShape(50)
                        )
                )
            }
        }

        if (hasError) {
            Spacer(modifier = Modifier.height(12.dp))
            Text("Incorrect PIN! Try again.", color = Color(0xFFE24B4A), fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth(0.65f)
        ) {
            val keys = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("CLR", "0", "OK")
            )
            keys.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    row.forEach { key ->
                        val isAction = key == "CLR" || key == "OK"
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .background(
                                    if (isAction) Color(0x0AFFFFFF) else Color(0x1AFFFFFF),
                                    RoundedCornerShape(50)
                                )
                                .clickableScale {
                                    hasError = false
                                    when (key) {
                                        "CLR" -> enteredText = ""
                                        "OK" -> {
                                            if (enteredText == correctPin) {
                                                onAuthentic()
                                            } else {
                                                hasError = true
                                                enteredText = ""
                                            }
                                        }
                                        else -> {
                                            if (enteredText.length < 4) {
                                                enteredText += key
                                                if (enteredText.length == 4 && enteredText == correctPin) {
                                                    onAuthentic()
                                                }
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = key,
                                fontSize = if (isAction) 14.sp else 22.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}
