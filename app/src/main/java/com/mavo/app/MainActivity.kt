package com.mavo.app

import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.filled.AccountBalance
import com.mavo.app.ui.animation.premiumCombinedClickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
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
import com.mavo.app.ui.AppNavigationDestination
import com.mavo.app.ui.LocalAppWindowSizeClass
import com.mavo.app.ui.components.AdaptiveNavigationBar
import com.mavo.app.ui.components.AdaptiveNavigationRail
import com.mavo.app.ui.usesTwoPaneLayout
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
import com.mavo.app.ui.theme.BrandColors
import com.mavo.app.ui.theme.LocalAppTheme
import com.mavo.app.ui.theme.MavoTheme
import com.mavo.app.ui.theme.ThemeViewModel
import com.mavo.app.ui.usesNavigationRail
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

private val topLevelDestinations = listOf(
    AppNavigationDestination(Routes.Dashboard, "Home", Icons.Outlined.Home),
    AppNavigationDestination(Routes.Vouchers, "Vouchers", Icons.Outlined.Description),
    AppNavigationDestination(Routes.Parties, "Parties", Icons.Outlined.People),
    AppNavigationDestination(Routes.Settings, "More", Icons.Outlined.Settings)
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

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            val currentTheme by themeViewModel.currentTheme.collectAsState()
            val windowSizeClass = calculateWindowSizeClass(this@MainActivity)
            val windowInsetsController = remember(window) {
                WindowInsetsControllerCompat(window, window.decorView)
            }

            @Suppress("DEPRECATION")
            SideEffect {
                window.statusBarColor = Color.Transparent.toArgb()
                windowInsetsController.isAppearanceLightStatusBars = currentTheme.statusBarDarkIcons
                window.navigationBarColor = Color.Transparent.toArgb()
                windowInsetsController.isAppearanceLightNavigationBars = currentTheme.statusBarDarkIcons
            }

            CompositionLocalProvider(
                LocalAppTheme provides currentTheme,
                LocalAppWindowSizeClass provides windowSizeClass.widthSizeClass
            ) {
                MavoTheme(appTheme = currentTheme) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        MainAppEntry(viewModel, themeViewModel, dashboardViewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun MainAppEntry(
    viewModel: AppViewModel,
    themeViewModel: ThemeViewModel,
    dashboardViewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val dbState by viewModel.dbInitState.collectAsState()

    when (val state = dbState) {
        is AppViewModel.DbInitState.Loading -> {
            Box(
                modifier = modifier
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
                modifier = modifier
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
                            shape = RoundedCornerShape(16.dp)
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
                dashboardViewModel = dashboardViewModel,
                modifier = modifier
            )
        }
    }
}

@Composable
private fun AppContent(
    viewModel: AppViewModel,
    themeViewModel: ThemeViewModel,
    dashboardViewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val windowWidthSizeClass = LocalAppWindowSizeClass.current
    val isSetupCompleted by viewModel.isSetupCompleted.collectAsState()
    val setupStatusResolved by viewModel.setupStatusResolved.collectAsState()
    var showSplash by remember { mutableStateOf(true) }

    Crossfade(
        targetState = showSplash,
        modifier = modifier,
        animationSpec = tween(durationMillis = 240, easing = FastOutSlowInEasing)
    ) { isSplash ->
        if (isSplash) {
            SplashScreen(onTimeout = { showSplash = false })
        } else {
            var changelogData by remember { mutableStateOf<ChangelogData?>(null) }
            var showChangelog by remember { mutableStateOf(false) }
            val uiScope = rememberCoroutineScope()

            LaunchedEffect(isSetupCompleted) {
                if (isSetupCompleted) {
                    viewModel.autoAdvanceFinancialYearIfNeeded(context)?.let { updatedFy ->
                        Toast.makeText(context, "Financial year updated to FY $updatedFy", Toast.LENGTH_LONG).show()
                    }
                    val loadedChangelog = ChangelogLoader.load(context)
                    val lastSeenVersion = AppPreferences.getLastSeenChangelogVersion(context)
                    if (loadedChangelog != null && (lastSeenVersion == null || lastSeenVersion != loadedChangelog.version)) {
                        changelogData = loadedChangelog
                        showChangelog = true
                    }
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
                        Image(
                            painter = painterResource(R.drawable.ic_mavo_logo),
                            contentDescription = null,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Loading your books...",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 0.sp,
                            color = AppColors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
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
                            .statusBarsPadding(),
                        containerColor = AppColors.screenBg,
                        bottomBar = {
                            if (isTopLevel && windowWidthSizeClass == WindowWidthSizeClass.Compact) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .navigationBarsPadding(),
                                    horizontalAlignment = Alignment.End
                                ) {
                                    if (currentDestination?.route == Routes.Dashboard) {
                                        var showQuickMenu by remember { mutableStateOf(false) }
                                        Box(
                                            modifier = Modifier.padding(end = 16.dp, bottom = 12.dp)
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(52.dp)
                                                    .clip(CircleShape)
                                                    .background(BrandColors.mangoPunch)
                                                    .border(1.dp, BrandColors.aubergineInk.copy(alpha = 0.2f), CircleShape)
                                                    .premiumCombinedClickable(
                                                        onClick = { navController.navigate(newVoucherRoute()) },
                                                        onLongClick = { showQuickMenu = true }
                                                    ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Add,
                                                    contentDescription = "New Voucher",
                                                    tint = BrandColors.aubergineInk,
                                                    modifier = Modifier.size(26.dp)
                                                )
                                            }

                                            if (showQuickMenu) {
                                                Column(
                                                    modifier = Modifier
                                                        .align(Alignment.BottomEnd)
                                                        .padding(end = 0.dp, bottom = 60.dp),
                                                    horizontalAlignment = Alignment.End,
                                                    verticalArrangement = Arrangement.spacedBy(10.dp)
                                                ) {
                                                    DashboardQuickCircularOption(
                                                        icon = Icons.Outlined.Description,
                                                        label = "Create Vouchers",
                                                        onClick = {
                                                            showQuickMenu = false
                                                            navController.navigate(newVoucherRoute())
                                                        }
                                                    )
                                                    DashboardQuickCircularOption(
                                                        icon = Icons.Outlined.People,
                                                        label = "Create Parties",
                                                        onClick = {
                                                            showQuickMenu = false
                                                            viewModel.setOpenCreateParty(true)
                                                            navController.navigateToTopLevel(Routes.Parties)
                                                        }
                                                    )
                                                    DashboardQuickCircularOption(
                                                        icon = Icons.Outlined.Inventory2,
                                                        label = "Create Product",
                                                        onClick = {
                                                            showQuickMenu = false
                                                            viewModel.setOpenCreateProduct(true)
                                                            navController.navigate(Routes.Products)
                                                        }
                                                    )
                                                    DashboardQuickCircularOption(
                                                        icon = Icons.Default.AccountBalance,
                                                        label = "Create Chart of Accounts",
                                                        onClick = {
                                                            showQuickMenu = false
                                                            viewModel.setOpenCreateLedger(true)
                                                            navController.navigate(Routes.LedgerBooks)
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    AdaptiveNavigationBar(
                                        destinations = topLevelDestinations,
                                        currentRoute = currentDestination?.route,
                                        onNavigate = { navController.navigateToTopLevel(it) }
                                    )
                                }
                            }
                        }
                    ) { innerPadding ->
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                        ) {
                            if (isTopLevel && windowWidthSizeClass.usesNavigationRail()) {
                                AdaptiveNavigationRail(
                                    destinations = topLevelDestinations,
                                    currentRoute = currentDestination?.route,
                                    onNavigate = { navController.navigateToTopLevel(it) },
                                    onNewSale = { navController.navigate(newVoucherRoute()) }
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .fillMaxHeight()
                            ) {
                                MavoNavHost(
                                    navController = navController,
                                    viewModel = viewModel,
                                    themeViewModel = themeViewModel,
                                    dashboardViewModel = dashboardViewModel
                                )
                            }
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
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
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
    dashboardViewModel: DashboardViewModel,
    modifier: Modifier = Modifier
) {
    val useTwoPaneContent = LocalAppWindowSizeClass.current.usesTwoPaneLayout()
    NavHost(
        navController = navController,
        startDestination = Routes.Dashboard,
        modifier = modifier,
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
                        "PRODUCTS" -> navController.navigate(Routes.Products)
                        "LEDGER_BOOKS" -> navController.navigate(Routes.LedgerBooks)
                        "PARTIES" -> navController.navigateToTopLevel(Routes.Parties)
                    }
                }
            )
        }

        composable(Routes.Vouchers) {
            VouchersScreen(
                viewModel = viewModel,
                isDesktop = useTwoPaneContent,
                navigateToNewVoucher = { id -> navController.navigate(newVoucherRoute(id)) },
                navigateToVoucherDetail = { id -> navController.navigate(voucherDetailRoute(id)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Parties) {
            PartiesScreen(
                viewModel = viewModel,
                isDesktop = useTwoPaneContent,
                onPartySelected = { id -> navController.navigate(partyDetailRoute(id)) },
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Settings) {
            SettingsScreen(
                viewModel = viewModel,
                themeViewModel = themeViewModel,
                isDesktop = useTwoPaneContent,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Routes.Reports) {
            ReportsScreen(
                viewModel = viewModel,
                isDesktop = useTwoPaneContent,
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
                onNavigateBack = { navController.navigate(Routes.Dashboard) { popUpTo(0) } }
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
                onNavigateBack = { navController.navigate(Routes.Dashboard) { popUpTo(0) } },
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
                onNavigateBack = { navController.navigate(Routes.Dashboard) { popUpTo(0) } },
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
                onBack = { navController.navigate(Routes.Dashboard) { popUpTo(0) } },
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
                onNavigateBack = { navController.navigate(Routes.Dashboard) { popUpTo(0) } }
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
private fun DashboardQuickCircularOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(com.mavo.app.ui.theme.Surface.card)
            .border(1.dp, com.mavo.app.ui.theme.Surface.cardBorder, CircleShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = AppColors.primary,
            modifier = Modifier.size(22.dp)
        )
    }
}
