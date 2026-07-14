package com.dg479.longrunportfolio

import android.app.Activity
import android.content.Context
import android.graphics.Color as AndroidColor
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.dg479.longrunportfolio.formatting.CurrencyDisplayFormatter
import com.dg479.longrunportfolio.protection.ProtectionDailyUsage
import com.dg479.longrunportfolio.protection.ProtectionMode
import com.dg479.longrunportfolio.protection.ProtectionSettings
import com.dg479.longrunportfolio.protection.ProtectionStore
import com.dg479.longrunportfolio.protection.hashQrContent
import com.dg479.longrunportfolio.protection.matchesQr
import com.dg479.longrunportfolio.simulation.DividendProjectionEngine
import com.dg479.longrunportfolio.simulation.DividendProjectionInput
import com.dg479.longrunportfolio.simulation.DividendProjectionRow as DividendGrowthProjectionRow
import com.dg479.longrunportfolio.simulation.DividendTaxEngine
import com.dg479.longrunportfolio.simulation.DividendTaxPerson
import com.dg479.longrunportfolio.simulation.HistoricalAssetAllocation
import com.dg479.longrunportfolio.simulation.HistoricalPricePoint
import com.dg479.longrunportfolio.simulation.HistoricalVolatilityEngine
import com.dg479.longrunportfolio.simulation.HistoricalVolatilityEstimate
import com.dg479.longrunportfolio.simulation.SelfDividendAssetInput
import com.dg479.longrunportfolio.simulation.SelfDividendEngine
import com.dg479.longrunportfolio.simulation.SelfDividendProjectionRow
import com.dg479.longrunportfolio.simulation.ScenarioComparisonEngine
import com.dg479.longrunportfolio.simulation.ScenarioComparisonPoint
import com.dg479.longrunportfolio.simulation.ScenarioComparisonSeries
import com.dg479.longrunportfolio.simulation.ScenarioComparisonType
import com.dg479.longrunportfolio.simulation.RetirementSuccessEngine
import com.dg479.longrunportfolio.simulation.RetirementSuccessInput
import com.dg479.longrunportfolio.simulation.RetirementSuccessResult
import com.dg479.longrunportfolio.simulation.RetirementSuccessYear
import com.dg479.longrunportfolio.simulation.RetirementScenarioSuccessAnalysis
import com.dg479.longrunportfolio.simulation.RetirementScenarioSuccessEngine
import com.dg479.longrunportfolio.simulation.ThreeAssetAllocation as FourAssetAllocation
import com.dg479.longrunportfolio.simulation.ThreeAssetAnnualRow as FourAssetAnnualRow
import com.dg479.longrunportfolio.simulation.ThreeAssetRetirementEngine
import com.dg479.longrunportfolio.simulation.ThreeAssetRetirementInput as FourAssetRetirementInput
import com.dg479.longrunportfolio.simulation.ThreeAssetRetirementResult as FourAssetRetirementResult
import com.dg479.longrunportfolio.ui.theme.LongRunPortfolioTheme
import com.google.zxing.BarcodeFormat
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.roundToLong

private var AppBackground = Color.White
private var SoftSurface = Color(0xFFF5F6F8)
private var LineColor = Color(0xFFE8EAEE)
private var TextPrimary = Color(0xFF111315)
private var TextSecondary = Color(0xFF737A84)
private var MutedText = Color(0xFFB9C0C8)
private var PanelColor = Color.White
private val PositiveRed = Color(0xFFF53B66)
private val NegativeBlue = Color(0xFF1483E6)
private val BrandGreen = Color(0xFF007AF5)
private val BrandSoftBlue = Color(0xFF99BDEF)
private val NavigationChrome = Color(0xFF101115)
private val BrokerBlue = Color(0xFF0B49DF)
private val NasdaqBlue = Color(0xFF0DA9D7)
private val CashOrange = Color(0xFFFFB02E)
private val PsuOrange = Color(0xFFE9914A)
private val FourAssetSchdColor = Color(0xFF10B981)
private val FourAssetJepqColor = Color(0xFF3B82F6)
private val FourAssetQldColor = Color(0xFF8B5CF6)
private val FourAssetCashColor = Color(0xFFF59E0B)
private val SimulatorBacktestColor = Color(0xFF2563EB)
private val SimulatorCashFlowColor = Color(0xFF07845F)
private val SimulatorAnalysisColor = Color(0xFF7C3AED)
private const val DefaultUsdKrw = 1535.29
private var DisplayCurrency = "KRW"
private var DisplayUsdKrw = DefaultUsdKrw
private const val MarketRefreshIntervalMs = 5L * 60L * 1000L
private const val ManualRefreshThrottleMs = 60L * 1000L
private const val KisRequestDelayMs = 550L
private const val KisBaseUrl = "https://openapi.koreainvestment.com:9443"
private const val KiwoomRequestDelayMs = 150L
private const val KiwoomBaseUrl = "https://mockapi.kiwoom.com"
private val KisOverseasExchangeOrder = listOf("NAS", "AMS", "NYS")
private val KisOverseasExchangeHints = mapOf(
    "AAPL" to "NAS",
    "GOOGL" to "NAS",
    "MSFT" to "NAS",
    "NVDA" to "NAS",
    "QQQ" to "NAS",
    "TQQQ" to "NAS",
    "TSLA" to "NAS",
    "QLD" to "AMS",
    "SCHD" to "AMS",
    "QQQI" to "AMS",
    "SPYI" to "AMS",
    "SOXL" to "AMS",
    "SPY" to "AMS",
    "VOO" to "AMS"
)
private const val MainTabTopPadding = 18

private data class AssetOption(
    val ticker: String,
    val name: String,
    val price: Double,
    val color: Color
)

private data class TradeUi(
    val id: Long,
    val date: String,
    val side: String,
    val quantity: Double,
    val price: Double,
    val exchangeRate: Double = DefaultUsdKrw
)

private data class HoldingUi(
    val ticker: String,
    val name: String,
    val quantity: Double,
    val averagePrice: Double,
    val currentPrice: Double,
    val color: Color,
    val exchangeRate: Double = DefaultUsdKrw,
    val averageExchangeRate: Double = DefaultUsdKrw,
    val previousClosePrice: Double = 0.0,
    val trades: List<TradeUi> = emptyList()
) {
    val amount: Long = (quantity * currentPrice * holdingExchangeRate(ticker, exchangeRate)).roundToLong()
    val principal: Long = (quantity * averagePrice * holdingExchangeRate(ticker, averageExchangeRate)).roundToLong()
}

private data class AccountUi(
    val id: Int,
    val name: String,
    val broker: String,
    val manual: Boolean,
    val color: Color,
    val iconText: String = "",
    val fixedAmount: Long? = null,
    val holdings: List<HoldingUi> = emptyList(),
    val sortField: String = SortField.DIRECT,
    val sortDescending: Boolean = true
) {
    val totalAmount: Long = fixedAmount ?: holdings.sumOf { it.amount }
    val dayProfit: Long = holdings.sumOf { it.dayProfit }
}

private data class MarketQuote(
    val price: Double,
    val previousClose: Double? = null,
    val exchangeRate: Double? = null
)

private data class AccountIconOption(
    val label: String,
    val text: String,
    val color: Color
)

private data class GoalPlan(
    val startDate: String = "2026. 6. 28.",
    val years: Int = 20,
    val annualTargetReturn: Double = 12.0,
    val targetAmount: Long = 1_000_000_000
)

private data class SavedAppState(
    val accounts: List<AccountUi> = emptyList(),
    val goalPlan: GoalPlan = GoalPlan(),
    val usdKrw: Double = DefaultUsdKrw
)

private data class AppSettings(
    val displayMode: String = DisplayMode.SYSTEM,
    val currency: String = CurrencyMode.KRW,
    val apiProvider: String = ApiProvider.KIS,
    val kisAppKey: String = "",
    val kisAppSecret: String = "",
    val kiwoomAppKey: String = "",
    val kiwoomAppSecret: String = "",
    val homeProfitMode: String = ProfitMode.DAY
)

private data class BacktestAssetUi(
    val ticker: String,
    val name: String,
    val weight: Double,
    val color: Color
)

private data class SelfDividendAssetUi(
    val ticker: String,
    val name: String,
    val color: Color,
    val investmentAmount: String = "",
    val annualWithdrawal: String = "",
    val withdrawalGrowthRate: String = "3",
    val taxMode: String = "해외직투 양도세"
)

private data class SelfDividendPreset(
    val name: String,
    val assets: List<SelfDividendAssetUi>,
    val result: List<SelfDividendProjectionRow> = emptyList()
)

private data class FourAssetDistributionPreset(
    val name: String,
    val totalCapitalEok: String,
    val monthlyExpenseMan: String,
    val schdRatio: String,
    val jepqRatio: String,
    val qldRatio: String,
    val cashRatio: String,
    val appliedSchdRatio: Double,
    val appliedJepqRatio: Double,
    val appliedQldRatio: Double,
    val appliedCashRatio: Double,
    val exchangeRate: String,
    val schdPrice: String,
    val jepqPrice: String,
    val qldPrice: String,
    val schdYield: String,
    val schdDividendGrowth: String,
    val schdPriceGrowth: String,
    val jepqYield: String,
    val jepqDividendGrowth: String,
    val jepqPriceGrowth: String,
    val qldPriceGrowth: String,
    val cashYield: String,
    val inflationRate: String,
    val taxAndInsuranceRate: String,
    val stressTestEnabled: Boolean
)

private data class DividendEtfUi(
    val ticker: String,
    val name: String,
    val yieldRate: Double,
    val dividendGrowth5y: Double,
    val priceGrowth10y: Double,
    val frequency: String,
    val currency: String,
    val fallbackPrice: Double,
    val color: Color
)

private data class DividendHoldingProjection(
    val holding: HoldingUi,
    val candidate: DividendEtfUi,
    val annualDividend: Long,
    val fiveYearAnnualDividend: Long
)

private data class PortfolioDividendProfile(
    val perShareAmount: Double,
    val currency: String,
    val months: List<Int>,
    val day: Int
)

private data class PortfolioDividendItem(
    val month: Int,
    val day: Int,
    val holding: HoldingUi,
    val perShareAmount: Double,
    val currency: String,
    val amount: Long,
    val months: List<Int>,
    val payDateLabel: String
)

private data class DividendPersonUi(
    val id: Long,
    val name: String,
    val incomeTaxRate: Double = 0.0,
    val healthInsuranceRate: Double = 0.0,
    val annualPensionIncome: Long = 0L
)

private data class DividendSimulationPreset(
    val name: String,
    val ticker: String,
    val modeIndex: Int,
    val targetInput: String,
    val showAfterTax: Boolean,
    val selectedPersonId: Long,
    val people: List<DividendPersonUi>,
    val projectionRows: List<DividendGrowthProjectionRow> = emptyList()
)

private data class DividendMetricUi(
    val label: String,
    val value: String,
    val numericValue: Double? = null
)

private data class DividendChartUpdateSnapshot(
    val inputKey: String,
    val ticker: String,
    val targetMonthlyDividend: Long,
    val dividendGrowthMetric: DividendMetricUi,
    val priceGrowthMetric: DividendMetricUi,
    val pricePoints: List<Pair<LocalDate, Double>>,
    val growthPoints: List<Pair<LocalDate, Double>>,
    val projectionRows: List<DividendGrowthProjectionRow>
)

private data class ScenarioComparisonCandidate(
    val id: String,
    val name: String,
    val type: ScenarioComparisonType,
    val series: ScenarioComparisonSeries?,
    val historicalAllocations: List<HistoricalAssetAllocation>
)

private data class RetirementVolatilityFailure(
    val scenarioId: String,
    val scenarioName: String,
    val tickers: List<String>
)

private class QrScanCoordinator {
    var externalFlowActive: Boolean = false
    var resultHandler: ((String?) -> Unit)? = null
}

private data class RetirementSuccessSnapshot(
    val initialAssetEok: String = "10",
    val monthlySpendingMan: String = "400",
    val expectedReturn: String = "7",
    val volatility: String = "15",
    val inflation: String = "2.5",
    val years: String = "30",
    val simulations: Int = 5_000,
    val result: RetirementSuccessResult? = null
)

private data class BacktestAllocationPoint(
    val ticker: String,
    val name: String,
    val weight: Double,
    val amount: Long,
    val color: Color
)

private data class BacktestResultUi(
    val monthlyAssets: List<Long>,
    val monthlyDrawdowns: List<Double>,
    val annualReturns: List<Pair<Int, Double>>,
    val rows: List<BacktestReportRow>,
    val initialAmount: Long = 0L,
    val investedAmount: Long = 0L,
    val monthLabels: List<String> = emptyList(),
    val monthlyReturns: List<Double> = emptyList(),
    val monthlyAllocations: List<List<BacktestAllocationPoint>> = emptyList(),
    val usedHistoricalData: Boolean = false
)

private data class BacktestReportRow(
    val year: Int,
    val finalReturn: Double,
    val profit: Long,
    val maxDrawdown: Double,
    val finalAsset: Long
)

private data class BacktestPreset(
    val name: String,
    val startYear: String,
    val endYear: String,
    val startingMoney: String,
    val rebalanceEnabled: Boolean,
    val rebalanceFrequency: String,
    val contributionEnabled: Boolean,
    val contributionPeriod: String,
    val contributionAmount: String,
    val dividendReinvest: Boolean,
    val exchangeRateEnabled: Boolean = true,
    val assets: List<BacktestAssetUi>,
    val result: BacktestResultUi? = null
)

private data class HistoricalPoint(
    val date: String,
    val close: Double
)

private data class DividendPaymentPoint(
    val date: String,
    val amount: Double
)

private data class DownloadCandidate(
    val symbol: String,
    val label: String,
    val color: Color
)

private data class HistoricalDownloadResult(
    val status: DownloadStatus,
    val points: List<HistoricalPoint> = emptyList(),
    val message: String
)

private data class NasdaqCrisisEvent(
    val year: Int,
    val label: String,
    val nasdaqDrop: Double
)

private object HistoryInterval {
    const val DAILY = "D"
    const val MONTHLY = "M"
}

private const val VolatilityHistoryPreferences = "long_run_volatility_history"
private const val VolatilityHistoryMetaPreferences = "long_run_volatility_history_meta"
private const val VolatilityHistoryCacheMs = 7L * 24L * 60L * 60L * 1_000L

private enum class DownloadStatus {
    SUCCESS,
    API_KEY_MISSING,
    NO_RESPONSE,
    NO_DATA,
    UNSUPPORTED_SYMBOL,
    INSUFFICIENT_SOURCE
}

private data class GoalChartPoint(
    val label: String,
    val actual: Double?,
    val target: Double
)

private enum class BacktestChartType {
    ASSET,
    DRAWDOWN,
    RETURN,
    PRICE
}

private var lastBacktestPresetCache: BacktestPreset? = null
private var lastBacktestResultCache: BacktestResultUi? = null
private val savedBacktestPresetsCache = mutableListOf<BacktestPreset>()
private var lastDividendSimulationPresetCache: DividendSimulationPreset? = null
private var lastDividendChartUpdateSnapshotCache: DividendChartUpdateSnapshot? = null
private val savedDividendSimulationPresetsCache = mutableListOf<DividendSimulationPreset>()
private var lastSelfDividendPresetCache: SelfDividendPreset? = null
private val savedSelfDividendPresetsCache = mutableListOf<SelfDividendPreset>()
private var lastFourAssetDistributionPresetCache: FourAssetDistributionPreset? = null
private val savedFourAssetDistributionPresetsCache = mutableListOf<FourAssetDistributionPreset>()

private data class MarketRefreshResult(
    val accounts: List<AccountUi>,
    val usdKrw: Double,
    val updated: Boolean,
    val message: String = ""
)

private object InvestmentMode {
    const val QUOTE = "quote"
    const val VALUATION = "valuation"
}

private object ProfitMode {
    const val TOTAL = "total"
    const val DAY = "day"
}

private object TradeSide {
    const val BUY = "buy"
    const val SELL = "sell"
    const val HOLD = "hold"
}

private object DisplayMode {
    const val SYSTEM = "system"
    const val LIGHT = "light"
    const val DARK = "dark"
}

private object CurrencyMode {
    const val KRW = "KRW"
    const val USD = "USD"
}

private object ApiProvider {
    const val KIS = "kis"
    const val KIWOOM = "kiwoom"
}

private object AnalysisSection {
    const val PROFIT = "profit"
    const val DIVIDEND = "dividend"
    const val TAX = "tax"
    const val TREND = "trend"
    const val ALLOCATION = "allocation"
}

private object SortField {
    const val AMOUNT = "amount"
    const val PRINCIPAL = "principal"
    const val TOTAL_PROFIT = "totalProfit"
    const val TOTAL_RETURN = "totalReturn"
    const val DAY_PROFIT = "dayProfit"
    const val DAY_RETURN = "dayReturn"
    const val DIRECT = "direct"
}

private data class MarketPosition(
    val title: String,
    val drawdownPercent: Int,
    val comparison: String,
    val caption: String,
    val color: Color
)

private data class TabItem(val label: String, val glyph: String)

private enum class AppRoute {
    Main,
    AccountDetail,
    HoldingDetail,
    AssetSearch,
    AddAsset,
    TradeAsset,
    AccountManage,
    DeleteManualAccounts,
    AddAccount,
    Settings,
    DisplayModeSettings
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (BuildConfig.DEBUG && intent.getBooleanExtra(ResetProtectionForDevelopmentRunExtra, false)) {
            ProtectionStore.resetToNormalForDevelopmentRun(this)
            intent.removeExtra(ResetProtectionForDevelopmentRunExtra)
        }
        enableEdgeToEdge()
        setContent {
            LongRunApp()
        }
    }

    private companion object {
        const val ResetProtectionForDevelopmentRunExtra =
            "com.dg479.longrunportfolio.RESET_PROTECTION_FOR_DEVELOPMENT_RUN"
    }
}

@Composable
private fun ScaledApp(scale: Float, content: @Composable () -> Unit) {
    val currentDensity = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = currentDensity.density * 0.9f,
            fontScale = currentDensity.fontScale
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LongRunApp() {
    val context = LocalContext.current
    val savedState = remember { loadAppState(context) }
    var appSettings by remember { mutableStateOf(loadAppSettings(context)) }
    var protectionSettings by remember {
        mutableStateOf(ProtectionStore.loadSettingsForAppLaunch(context, resetOnAppUpdate = BuildConfig.DEBUG))
    }
    var dailyProtectionUsage by remember { mutableStateOf(ProtectionStore.loadTodayUsage(context)) }
    var isAppUnlocked by remember { mutableStateOf(protectionSettings.mode == ProtectionMode.NORMAL) }
    var showLockedSimulator by remember { mutableStateOf(false) }
    var lockStartedAt by remember { mutableLongStateOf(ProtectionStore.loadLockStartedAt(context)) }
    var bypassWaitForCurrentLock by remember {
        mutableStateOf(
            protectionSettings.mode != ProtectionMode.NORMAL &&
                ProtectionStore.consumeOneTimeWaitBypass(context)
        )
    }
    val qrScanCoordinator = remember { QrScanCoordinator() }
    val lifecycleOwner = LocalLifecycleOwner.current
    val qrScannerLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        qrScanCoordinator.externalFlowActive = false
        val handler = qrScanCoordinator.resultHandler
        qrScanCoordinator.resultHandler = null
        handler?.invoke(result.contents)
    }
    val requestQrScan: (((String?) -> Unit) -> Unit) = { handler ->
        qrScanCoordinator.externalFlowActive = true
        qrScanCoordinator.resultHandler = handler
        qrScannerLauncher.launch(
            ScanOptions().apply {
                setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                setPrompt("등록한 QR 코드를 화면 안에 맞춰 주세요")
                setBeepEnabled(false)
                setOrientationLocked(true)
                setCaptureActivity(PortraitCaptureActivity::class.java)
            }
        )
    }
    DisposableEffect(lifecycleOwner, protectionSettings.mode) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> if (
                    protectionSettings.mode != ProtectionMode.NORMAL &&
                    !isAppUnlocked &&
                    !qrScanCoordinator.externalFlowActive &&
                    ProtectionStore.consumeOneTimeWaitBypass(context)
                ) {
                    bypassWaitForCurrentLock = true
                }
                Lifecycle.Event.ON_STOP -> if (
                    protectionSettings.mode != ProtectionMode.NORMAL &&
                    !qrScanCoordinator.externalFlowActive
                ) {
                    if (isAppUnlocked) {
                        lockStartedAt = ProtectionStore.startNewLock(context)
                    }
                    isAppUnlocked = false
                    showLockedSimulator = false
                    bypassWaitForCurrentLock = false
                }
                else -> Unit
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }
    val systemDark = isSystemInDarkTheme()
    val darkMode = when (appSettings.displayMode) {
        DisplayMode.DARK -> true
        DisplayMode.LIGHT -> false
        else -> systemDark
    }
    applyAppPalette(darkMode)
    val view = LocalView.current
    SideEffect {
        val window = (view.context as? Activity)?.window ?: return@SideEffect
        val barColor = if (darkMode) AndroidColor.BLACK else AndroidColor.WHITE
        window.statusBarColor = barColor
        window.navigationBarColor = if (darkMode) NavigationChrome.toArgb() else AndroidColor.WHITE
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = !darkMode
            isAppearanceLightNavigationBars = !darkMode
        }
    }
    val accounts = remember { mutableStateListOf(*savedState.accounts.toTypedArray()) }
    val editingAccounts = remember { mutableStateListOf<AccountUi>() }
    val selectedDeleteIds = remember { mutableStateListOf<Int>() }
    val investmentModes = remember { mutableStateMapOf<Int, String>() }
    val profitModes = remember { mutableStateMapOf<Int, String>() }
    var selectedTab by remember { mutableIntStateOf(0) }
    var route by remember { mutableStateOf(AppRoute.Main) }
    var selectedAccountId by remember { mutableIntStateOf(accounts.firstOrNull()?.id ?: 0) }
    var selectedAsset by remember { mutableStateOf(assetOptions.first()) }
    var selectedHoldingTicker by remember { mutableStateOf("") }
    var tradeSide by remember { mutableStateOf("buy") }
    var editingTrade by remember { mutableStateOf<TradeUi?>(null) }
    var showManualSheet by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var showHoldingDeleteConfirm by remember { mutableStateOf(false) }
    var showAccountHoldingSheet by remember { mutableStateOf(false) }
    var pendingTradeDelete by remember { mutableStateOf<TradeUi?>(null) }
    var showGoalPlanDialog by remember { mutableStateOf(false) }
    var showSortSheet by remember { mutableStateOf(false) }
    var sortTargetAccountId by remember { mutableIntStateOf(0) }
    var goalPlan by remember { mutableStateOf(savedState.goalPlan) }
    var usdKrw by remember { mutableStateOf(savedState.usdKrw) }
    var isRefreshing by remember { mutableStateOf(false) }
    applyCurrencyDisplay(appSettings.currency, usdKrw)

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val selectedAccount = accounts.firstOrNull { it.id == selectedAccountId }
    val backupExportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("*/*")
    ) { uri ->
        if (uri != null) {
            val result = exportAppBackup(
                context = context,
                uri = uri,
                accounts = accounts.toList(),
                goalPlan = goalPlan,
                usdKrw = usdKrw,
                settings = appSettings
            )
            Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
        }
    }
    val backupImportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val result = importAppBackup(context, uri)
            if (result.success) {
                val restoredState = loadAppState(context)
                appSettings = loadAppSettings(context)
                val restoredProtectionSettings = ProtectionStore.loadSettings(context)
                protectionSettings = restoredProtectionSettings
                dailyProtectionUsage = ProtectionStore.loadTodayUsage(context)
                isAppUnlocked = restoredProtectionSettings.mode == ProtectionMode.NORMAL
                lockStartedAt = ProtectionStore.startNewLock(context)
                bypassWaitForCurrentLock = false
                accounts.clear()
                accounts.addAll(restoredState.accounts)
                selectedAccountId = restoredState.accounts.firstOrNull()?.id ?: 0
                goalPlan = restoredState.goalPlan
                usdKrw = restoredState.usdKrw
                route = AppRoute.Main
                selectedTab = 0
            }
            Toast.makeText(context, result.message, Toast.LENGTH_LONG).show()
        }
    }
    fun refreshNow() {
        if (isRefreshing) return
        scope.launch {
            val prefs = context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
            val now = System.currentTimeMillis()
            val lastRefreshAt = prefs.getLong("market_last_refresh_at", 0L)
            if (now - lastRefreshAt in 0 until ManualRefreshThrottleMs) {
                Toast.makeText(context, "방금 전 업데이트 했어요", Toast.LENGTH_SHORT).show()
                return@launch
            }
            isRefreshing = true
            val result = refreshMarketDataIfNeeded(context, accounts.toList(), usdKrw, appSettings, force = true)
            if (result.updated) {
                accounts.clear()
                accounts.addAll(result.accounts)
                usdKrw = result.usdKrw
                saveAppState(context, accounts.toList(), goalPlan, usdKrw)
            }
            Toast.makeText(
                context,
                if (result.updated) "최신 정보로 업데이트 했어요" else result.message.ifBlank { "업데이트할 새 데이터가 없어요" },
                Toast.LENGTH_SHORT
            ).show()
            delay(1_000L)
            isRefreshing = false
        }
    }

    BackHandler {
        when {
            drawerState.isOpen -> scope.launch { drawerState.close() }
            showManualSheet -> showManualSheet = false
            showDeleteConfirm -> showDeleteConfirm = false
            showHoldingDeleteConfirm -> showHoldingDeleteConfirm = false
            showAccountHoldingSheet -> showAccountHoldingSheet = false
            pendingTradeDelete != null -> pendingTradeDelete = null
            editingTrade != null -> {
                editingTrade = null
                route = AppRoute.AccountDetail
            }
            showGoalPlanDialog -> showGoalPlanDialog = false
            showSortSheet -> showSortSheet = false
            route == AppRoute.TradeAsset -> route = AppRoute.AccountDetail
            route == AppRoute.HoldingDetail -> route = AppRoute.AccountDetail
            route == AppRoute.AddAsset -> route = AppRoute.AssetSearch
            route == AppRoute.AssetSearch -> route = AppRoute.AccountDetail
            route == AppRoute.DeleteManualAccounts -> route = AppRoute.AccountManage
            route == AppRoute.AccountManage -> route = AppRoute.Main
            route == AppRoute.AddAccount -> route = AppRoute.Main
            route == AppRoute.DisplayModeSettings -> route = AppRoute.Settings
            route == AppRoute.Settings -> route = AppRoute.Main
            route == AppRoute.AccountDetail -> route = AppRoute.Main
            selectedTab != 0 -> selectedTab = 0
        }
    }

    val autoRefreshTickersKey = accounts
        .flatMap { account -> account.holdings }
        .map { holding -> holding.ticker }
        .distinct()
        .sorted()
        .joinToString("|")

    LaunchedEffect(autoRefreshTickersKey) {
        if (route == AppRoute.Main && selectedTab == 0) {
            val result = refreshMarketDataIfNeeded(context, accounts.toList(), usdKrw, appSettings)
            if (result.updated) {
                accounts.clear()
                accounts.addAll(result.accounts)
                usdKrw = result.usdKrw
                saveAppState(context, accounts.toList(), goalPlan, usdKrw)
            }
        }
    }

    fun unlockProtectedApp() {
        val currentUsage = ProtectionStore.loadTodayUsage(context)
        if (currentUsage.entryCount >= protectionSettings.mode.dailyEntryLimit) {
            dailyProtectionUsage = currentUsage
            Toast.makeText(context, "오늘 허용된 진입 횟수를 모두 사용했어요.", Toast.LENGTH_LONG).show()
            return
        }
        dailyProtectionUsage = ProtectionStore.recordEntry(context)
        ProtectionStore.clearLockStartedAt(context)
        showLockedSimulator = false
        bypassWaitForCurrentLock = false
        isAppUnlocked = true
    }

    LongRunPortfolioTheme(darkTheme = darkMode, dynamicColor = false) {
    if (protectionSettings.mode != ProtectionMode.NORMAL && !isAppUnlocked) {
        if (showLockedSimulator) {
            ScaledApp(scale = 0.7f) {
                BacktestScreen(
                    accounts = accounts,
                    settings = appSettings,
                    usdKrw = usdKrw,
                    onLockedExit = { showLockedSimulator = false }
                )
            }
        } else {
            AppProtectionLockScreen(
                settings = protectionSettings,
                dailyUsage = dailyProtectionUsage,
                lockStartedAt = lockStartedAt,
                bypassWait = bypassWaitForCurrentLock,
                onUnlock = ::unlockProtectedApp,
                onOpenSimulator = { showLockedSimulator = true },
                onRequestQrUnlock = {
                    requestQrScan { content ->
                        if (protectionSettings.matchesQr(content)) {
                            unlockProtectedApp()
                        } else if (content != null) {
                            Toast.makeText(context, "등록된 QR 코드와 일치하지 않습니다.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            )
        }
    } else {
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ScaledApp(scale = 0.84f) {
                AccountDrawer(
                    accounts = accounts,
                    onTotalClick = {
                        selectedTab = 0
                        route = AppRoute.Main
                        scope.launch { drawerState.close() }
                    },
                    onAccountClick = { account ->
                        selectedAccountId = account.id
                        route = AppRoute.AccountDetail
                        scope.launch { drawerState.close() }
                    },
                    onManageClick = {
                        editingAccounts.clear()
                        editingAccounts.addAll(accounts)
                        selectedDeleteIds.clear()
                        route = AppRoute.AccountManage
                        scope.launch { drawerState.close() }
                    },
                    onAddAccountClick = {
                        route = AppRoute.AddAccount
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        when (route) {
            AppRoute.Main -> MainScaffold(
                selectedTab = selectedTab,
                onTabSelected = { selectedTab = it },
                accounts = accounts,
                goalPlan = goalPlan,
                usdKrw = usdKrw,
                appSettings = appSettings,
                investmentMode = investmentModes[0] ?: InvestmentMode.VALUATION,
                profitMode = appSettings.homeProfitMode,
                onInvestmentModeChange = { investmentModes[0] = it },
                onProfitModeChange = {
                    val updated = appSettings.copy(homeProfitMode = it)
                    appSettings = updated
                    saveAppSettings(context, updated)
                },
                onSortClick = {
                    sortTargetAccountId = 0
                    showSortSheet = true
                },
                onOpenSettings = { route = AppRoute.Settings },
                onRefresh = { refreshNow() },
                isRefreshing = isRefreshing,
                onGoalLongClick = { showGoalPlanDialog = true }
            )
            AppRoute.AccountDetail -> ScaledApp(scale = 0.84f) {
                AccountDetailScreen(
                    account = selectedAccount,
                    onBack = { route = AppRoute.Main },
                    onOpenMenu = { scope.launch { drawerState.open() } },
                    onManualAdd = { showManualSheet = true },
                    investmentMode = investmentModes[selectedAccountId] ?: InvestmentMode.VALUATION,
                    profitMode = profitModes[selectedAccountId] ?: ProfitMode.DAY,
                    onInvestmentModeChange = { investmentModes[selectedAccountId] = it },
                    onProfitModeChange = { profitModes[selectedAccountId] = it },
                    onSortClick = {
                        sortTargetAccountId = selectedAccountId
                        showSortSheet = true
                    },
                    onHoldingClick = { holding ->
                        selectedHoldingTicker = holding.ticker
                        showAccountHoldingSheet = true
                    },
                    onHoldingLongClick = { holding ->
                        selectedHoldingTicker = holding.ticker
                        showHoldingDeleteConfirm = true
                    }
                )
            }
            AppRoute.HoldingDetail -> ScaledApp(scale = 0.84f) {
                CompactDetailScale {
                    HoldingDetailScreen(
                    account = selectedAccount,
                    holding = selectedAccount?.holdings?.firstOrNull { it.ticker == selectedHoldingTicker },
                    onBack = { route = AppRoute.AccountDetail },
                    onBuy = {
                        tradeSide = "buy"
                        route = AppRoute.TradeAsset
                    },
                    onSell = {
                        tradeSide = "sell"
                        route = AppRoute.TradeAsset
                    },
                    onTradeLongClick = { pendingTradeDelete = it }
                    ,
                    onTradeEdit = { trade ->
                        editingTrade = trade
                        tradeSide = if (trade.side == TradeSide.SELL) "sell" else "buy"
                        route = AppRoute.TradeAsset
                    },
                    onDeleteTrades = { tradeIds ->
                        val index = accounts.indexOfFirst { it.id == selectedAccountId }
                        if (index >= 0) {
                            val old = accounts[index]
                            accounts[index] = old.copy(holdings = removeTrades(old.holdings, selectedHoldingTicker, tradeIds))
                            saveAppState(context, accounts.toList(), goalPlan, usdKrw)
                        }
                    }
                    )
                }
            }
            AppRoute.AssetSearch -> ScaledApp(scale = 0.84f) {
                AssetSearchScreen(
                    settings = appSettings,
                    onClose = { route = AppRoute.AccountDetail },
                    onSelect = {
                        selectedAsset = it
                        route = AppRoute.AddAsset
                    }
                )
            }
            AppRoute.AddAsset -> ScaledApp(scale = 0.84f) {
                AddAssetScreen(
                    account = selectedAccount,
                    asset = selectedAsset,
                    usdKrw = usdKrw,
                    settings = appSettings,
                    onClose = { route = AppRoute.AssetSearch },
                    onAdd = { quantity, averagePrice, tradeDate, purchaseExchangeRate ->
                        val newHolding = HoldingUi(
                            ticker = selectedAsset.ticker,
                            name = selectedAsset.name,
                            quantity = quantity,
                            averagePrice = averagePrice,
                            currentPrice = if (selectedAsset.price > 0.0) selectedAsset.price else averagePrice,
                            color = selectedAsset.color,
                            exchangeRate = usdKrw,
                            averageExchangeRate = purchaseExchangeRate,
                            trades = listOf(TradeUi(System.currentTimeMillis(), tradeDate, TradeSide.BUY, quantity, averagePrice, purchaseExchangeRate))
                        )
                        val index = accounts.indexOfFirst { it.id == selectedAccountId }
                        if (index >= 0) {
                            val old = accounts[index]
                            accounts[index] = old.copy(fixedAmount = null, holdings = mergeHolding(old.holdings, newHolding))
                            saveAppState(context, accounts.toList(), goalPlan, usdKrw)
                        }
                        route = AppRoute.AccountDetail
                    }
                )
            }
            AppRoute.TradeAsset -> ScaledApp(scale = 0.84f) {
                val holding = selectedAccount?.holdings?.firstOrNull { it.ticker == selectedHoldingTicker }
                TradeAssetScreen(
                    account = selectedAccount,
                    holding = holding,
                    side = tradeSide,
                    usdKrw = usdKrw,
                    settings = appSettings,
                    editingTrade = editingTrade,
                    onClose = { route = AppRoute.AccountDetail },
                    onAdd = { quantity, averageUsd, tradeDate, purchaseExchangeRate ->
                        val index = accounts.indexOfFirst { it.id == selectedAccountId }
                        if (index >= 0 && holding != null) {
                            val old = accounts[index]
                            val trade = TradeUi(
                                id = editingTrade?.id ?: System.currentTimeMillis(),
                                date = tradeDate,
                                side = if (tradeSide == "buy") TradeSide.BUY else TradeSide.SELL,
                                quantity = quantity,
                                price = averageUsd,
                                exchangeRate = purchaseExchangeRate
                            )
                            val updated = if (editingTrade != null) {
                                replaceTrade(old.holdings, holding.ticker, trade)
                            } else if (tradeSide == "buy") {
                                mergeHolding(old.holdings, holding.copy(quantity = quantity, averagePrice = averageUsd, exchangeRate = usdKrw, averageExchangeRate = purchaseExchangeRate, trades = listOf(trade)))
                            } else {
                                reduceHolding(old.holdings, holding.ticker, quantity, trade)
                            }
                            accounts[index] = old.copy(fixedAmount = null, holdings = updated)
                            saveAppState(context, accounts.toList(), goalPlan, usdKrw)
                        }
                        editingTrade = null
                        route = AppRoute.AccountDetail
                    }
                )
            }
            AppRoute.AccountManage -> ScaledApp(scale = 0.84f) {
                AccountManageScreen(
                    accounts = editingAccounts,
                    onBack = { route = AppRoute.Main },
                    onSave = {
                        accounts.clear()
                        accounts.addAll(editingAccounts)
                        if (accounts.none { it.id == selectedAccountId }) {
                            selectedAccountId = accounts.firstOrNull()?.id ?: 0
                        }
                        saveAppState(context, accounts.toList(), goalPlan, usdKrw)
                        route = AppRoute.Main
                    },
                    onDeleteManualClick = {
                        selectedDeleteIds.clear()
                        route = AppRoute.DeleteManualAccounts
                    },
                    onRename = { accountId, newName ->
                        val index = editingAccounts.indexOfFirst { it.id == accountId }
                        if (index >= 0) {
                            editingAccounts[index] = editingAccounts[index].copy(name = newName)
                        }
                    }
                )
            }
            AppRoute.DeleteManualAccounts -> ScaledApp(scale = 0.84f) {
                DeleteManualAccountsScreen(
                    accounts = editingAccounts,
                    selectedIds = selectedDeleteIds,
                    onBack = { route = AppRoute.AccountManage },
                    onDeleteClick = {
                        if (selectedDeleteIds.isNotEmpty()) showDeleteConfirm = true
                    }
                )
            }
            AppRoute.AddAccount -> ScaledApp(scale = 0.84f) {
                AddAccountScreen(
                    onBack = { route = AppRoute.Main },
                    onAdd = { name, broker, icon ->
                        val nextId = ((accounts + editingAccounts).maxOfOrNull { it.id } ?: 0) + 1
                        val newAccount = AccountUi(
                            id = nextId,
                            name = name,
                            broker = broker,
                            manual = true,
                            color = icon.color,
                            iconText = icon.text
                        )
                        accounts.add(newAccount)
                        selectedAccountId = nextId
                        saveAppState(context, accounts.toList(), goalPlan, usdKrw)
                        route = AppRoute.AccountDetail
                    }
                )
            }
            AppRoute.Settings -> ScaledApp(scale = 0.84f) {
                SettingsScreen(
                    settings = appSettings,
                    protectionSettings = protectionSettings,
                    accounts = accounts,
                    onBack = { route = AppRoute.Main },
                    onDisplayModeClick = { route = AppRoute.DisplayModeSettings },
                    onCurrencyChange = {
                        val updated = appSettings.copy(currency = it)
                        appSettings = updated
                        saveAppSettings(context, updated)
                    },
                    onApiSettingsSave = { updated ->
                        appSettings = updated
                        saveAppSettings(context, updated)
                    },
                    onProtectionSettingsChange = { updated ->
                        val normalized = updated.copy(investmentPrinciple = updated.investmentPrinciple.trim())
                        val modeChanged = normalized.mode != protectionSettings.mode
                        if (modeChanged) {
                            if (normalized.mode == ProtectionMode.NORMAL) {
                                ProtectionStore.clearOneTimeWaitBypass(context)
                            } else {
                                ProtectionStore.armOneTimeWaitBypass(context)
                            }
                        }
                        protectionSettings = normalized
                        ProtectionStore.saveSettings(context, normalized)
                        isAppUnlocked = true
                        dailyProtectionUsage = ProtectionStore.loadTodayUsage(context)
                    },
                    onRequestQrScan = requestQrScan,
                    onBackupClick = {
                        backupExportLauncher.launch(appBackupFileName())
                    },
                    onRestoreClick = {
                        backupImportLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
                    }
                )
            }
            AppRoute.DisplayModeSettings -> ScaledApp(scale = 0.84f) {
                DisplayModeSettingsScreen(
                    selectedMode = appSettings.displayMode,
                    onBack = { route = AppRoute.Settings },
                    onSelect = {
                        val updated = appSettings.copy(displayMode = it)
                        appSettings = updated
                        saveAppSettings(context, updated)
                    }
                )
            }
        }

        if (showAccountHoldingSheet) {
            val sheetAccount = selectedAccount
            val sheetHolding = sheetAccount?.holdings?.firstOrNull { it.ticker == selectedHoldingTicker }
            ModalBottomSheet(
                onDismissRequest = { showAccountHoldingSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = PanelColor,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                CompactDetailScale {
                    HoldingDetailScreen(
                        account = sheetAccount,
                        holding = sheetHolding,
                        onBack = { showAccountHoldingSheet = false },
                        onBuy = {
                            showAccountHoldingSheet = false
                            tradeSide = "buy"
                            route = AppRoute.TradeAsset
                        },
                        onSell = {
                            showAccountHoldingSheet = false
                            tradeSide = "sell"
                            route = AppRoute.TradeAsset
                        },
                        onTradeLongClick = { pendingTradeDelete = it },
                        onTradeEdit = { trade ->
                            showAccountHoldingSheet = false
                            editingTrade = trade
                            tradeSide = if (trade.side == TradeSide.SELL) "sell" else "buy"
                            route = AppRoute.TradeAsset
                        },
                        onDeleteTrades = { tradeIds ->
                            val index = accounts.indexOfFirst { it.id == selectedAccountId }
                            if (index >= 0) {
                                val old = accounts[index]
                                accounts[index] = old.copy(holdings = removeTrades(old.holdings, selectedHoldingTicker, tradeIds))
                                saveAppState(context, accounts.toList(), goalPlan, usdKrw)
                            }
                        }
                    )
                }
            }
        }

        if (showManualSheet) {
            ModalBottomSheet(
                onDismissRequest = { showManualSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = PanelColor,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                ScaledApp(scale = 0.84f) {
                    ManualAddSheet(
                        onAssetAdd = {
                            showManualSheet = false
                            route = AppRoute.AssetSearch
                        },
                        onCashAdd = { showManualSheet = false }
                    )
                }
            }
        }

        if (showDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showDeleteConfirm = false },
                containerColor = PanelColor,
                shape = RoundedCornerShape(28.dp),
                title = {
                    Text(
                        text = "계좌를 삭제하시겠습니까?",
                        color = TextPrimary,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = "선택한 계좌와 해당 계좌의 자산 내역이 함께 삭제됩니다.\n계좌 편집 화면에서 저장해야 최종 반영됩니다.",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        lineHeight = 23.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            editingAccounts.removeAll { it.id in selectedDeleteIds }
                            selectedDeleteIds.clear()
                            route = AppRoute.AccountManage
                            showDeleteConfirm = false
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TextPrimary),
                        modifier = Modifier.width(116.dp).height(54.dp)
                    ) {
                        Text("삭제", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showDeleteConfirm = false },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SoftSurface, contentColor = TextPrimary),
                        modifier = Modifier.width(116.dp).height(54.dp)
                    ) {
                        Text("취소", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        if (showHoldingDeleteConfirm) {
            AlertDialog(
                onDismissRequest = { showHoldingDeleteConfirm = false },
                containerColor = PanelColor,
                shape = RoundedCornerShape(28.dp),
                title = {
                    Text(
                        text = "종목을 삭제할까요?",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = "$selectedHoldingTicker 보유 내역을 이 계좌에서 삭제합니다.",
                        color = TextSecondary,
                        fontSize = 15.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val index = accounts.indexOfFirst { it.id == selectedAccountId }
                            if (index >= 0) {
                                val old = accounts[index]
                                accounts[index] = old.copy(holdings = old.holdings.filterNot { it.ticker == selectedHoldingTicker })
                                saveAppState(context, accounts.toList(), goalPlan, usdKrw)
                            }
                            showHoldingDeleteConfirm = false
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TextPrimary),
                        modifier = Modifier.width(112.dp).height(52.dp)
                    ) {
                        Text("삭제", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { showHoldingDeleteConfirm = false },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SoftSurface, contentColor = TextPrimary),
                        modifier = Modifier.width(112.dp).height(52.dp)
                    ) {
                        Text("취소", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        pendingTradeDelete?.let { trade ->
            AlertDialog(
                onDismissRequest = { pendingTradeDelete = null },
                containerColor = PanelColor,
                shape = RoundedCornerShape(28.dp),
                title = {
                    Text(
                        text = "거래 이력을 삭제할까요?",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                text = {
                    Text(
                        text = "${trade.date} ${if (trade.side == TradeSide.BUY) "매수" else "매도"} 기록을 삭제합니다.",
                        color = TextSecondary,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val index = accounts.indexOfFirst { it.id == selectedAccountId }
                            if (index >= 0) {
                                val old = accounts[index]
                                accounts[index] = old.copy(holdings = removeTrade(old.holdings, selectedHoldingTicker, trade.id))
                                saveAppState(context, accounts.toList(), goalPlan, usdKrw)
                            }
                            pendingTradeDelete = null
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TextPrimary),
                        modifier = Modifier.width(112.dp).height(52.dp)
                    ) {
                        Text("삭제", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { pendingTradeDelete = null },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SoftSurface, contentColor = TextPrimary),
                        modifier = Modifier.width(112.dp).height(52.dp)
                    ) {
                        Text("취소", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        if (showGoalPlanDialog) {
            GoalPlanDialog(
                initial = goalPlan,
                onDismiss = { showGoalPlanDialog = false },
                onSave = {
                    goalPlan = it
                    saveAppState(context, accounts.toList(), it, usdKrw)
                    showGoalPlanDialog = false
                }
            )
        }

        if (showSortSheet) {
            val targetAccount = accounts.firstOrNull { it.id == sortTargetAccountId }
            val currentField = targetAccount?.sortField ?: SortField.DIRECT
            val currentDescending = targetAccount?.sortDescending ?: true
            ModalBottomSheet(
                onDismissRequest = { showSortSheet = false },
                sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
                containerColor = PanelColor,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            ) {
                SortSheet(
                    selectedField = currentField,
                    descending = currentDescending,
                    onSelect = { field, descending ->
                        if (sortTargetAccountId == 0) {
                            accounts.indices.forEach { index ->
                                accounts[index] = accounts[index].copy(sortField = field, sortDescending = descending)
                            }
                        } else {
                            val index = accounts.indexOfFirst { it.id == sortTargetAccountId }
                            if (index >= 0) {
                                accounts[index] = accounts[index].copy(sortField = field, sortDescending = descending)
                            }
                        }
                        saveAppState(context, accounts.toList(), goalPlan, usdKrw)
                        showSortSheet = false
                    }
                )
            }
        }
    }
    }
    }
}

@Composable
private fun MainScaffold(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    accounts: List<AccountUi>,
    goalPlan: GoalPlan,
    usdKrw: Double,
    appSettings: AppSettings,
    investmentMode: String,
    profitMode: String,
    onInvestmentModeChange: (String) -> Unit,
    onProfitModeChange: (String) -> Unit,
    onSortClick: () -> Unit,
    onOpenSettings: () -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    onGoalLongClick: () -> Unit
) {
    val tabs = listOf(
        TabItem("홈", "H"),
        TabItem("시뮬레이터", "S"),
        TabItem("리포트", "R")
    )

    Scaffold(
        containerColor = AppBackground,
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(containerColor = if (PanelColor == Color.White) AppBackground else NavigationChrome) {
                tabs.forEachIndexed { index, tab ->
                    NavigationBarItem(
                        selected = selectedTab == index,
                        onClick = { onTabSelected(index) },
                        icon = { TabGlyph(tab.glyph, selectedTab == index) },
                        label = { Text(tab.label, fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = TextPrimary,
                            selectedTextColor = TextPrimary,
                            unselectedIconColor = TextSecondary,
                            unselectedTextColor = TextSecondary,
                            indicatorColor = SoftSurface
                        )
                    )
                }
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = AppBackground
        ) {
            ScaledApp(scale = 0.7f) {
                when (selectedTab) {
                    0 -> HomeScreen(
                        accounts = accounts,
                        goalPlan = goalPlan,
                        usdKrw = usdKrw,
                        investmentMode = investmentMode,
                        profitMode = profitMode,
                        onInvestmentModeChange = onInvestmentModeChange,
                        onProfitModeChange = onProfitModeChange,
                        onSortClick = onSortClick,
                        onOpenSettings = onOpenSettings,
                        onRefresh = onRefresh,
                        isRefreshing = isRefreshing,
                        onGoalLongClick = onGoalLongClick
                    )
                    1 -> BacktestScreen(accounts, appSettings, usdKrw)
                    else -> ReportScreen(accounts = accounts, goalPlan = goalPlan)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomeScreen(
    accounts: List<AccountUi>,
    goalPlan: GoalPlan,
    usdKrw: Double,
    investmentMode: String,
    profitMode: String,
    onInvestmentModeChange: (String) -> Unit,
    onProfitModeChange: (String) -> Unit,
    onSortClick: () -> Unit,
    onOpenSettings: () -> Unit,
    onRefresh: () -> Unit,
    isRefreshing: Boolean,
    onGoalLongClick: () -> Unit
) {
    val total = accounts.sumOf { it.totalAmount }
    val dayProfit = accounts.sumOf { it.dayProfit }
    val holdings = portfolioHoldings(accounts)
    val principal = holdings.sumOf { it.principal }
    val totalProfit = total - principal
    val totalRate = if (principal == 0L) 0.0 else totalProfit.toDouble() / principal
    val dayRate = if (total == 0L) 0.0 else dayProfit.toDouble() / total
    var selectedHomeHolding by remember { mutableStateOf<HoldingUi?>(null) }
    var showGoalDetail by remember { mutableStateOf(false) }
    var selectedAnalysisSection by remember { mutableStateOf<String?>(null) }
    val homeScrollState = rememberScrollState()
    var pullDistance by remember { mutableStateOf(0f) }
    var pullReadyAt by remember { mutableStateOf(0L) }
    val pullOffset = pullDistance.coerceIn(0f, 160f)
    val pullProgress = (pullOffset / 160f).coerceIn(0f, 1f)
    val animatedPullOffset by animateFloatAsState(
        targetValue = if (isRefreshing) 88f else pullOffset,
        animationSpec = tween(220),
        label = "pullOffset"
    )
    val refreshMarkFill by animateFloatAsState(
        targetValue = if (isRefreshing) 1f else pullProgress,
        animationSpec = tween(850),
        label = "refreshMarkFill"
    )
    LaunchedEffect(homeScrollState.value, isRefreshing) {
        if (homeScrollState.value == 0 && !isRefreshing) {
            if (pullReadyAt == 0L) pullReadyAt = System.currentTimeMillis() + 350L
        } else {
            pullReadyAt = 0L
            pullDistance = 0f
        }
    }
    val pullRefreshConnection = remember(homeScrollState, isRefreshing, pullReadyAt) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                if (isRefreshing) return Offset.Zero
                val atTop = homeScrollState.value == 0
                val ready = pullReadyAt > 0L && System.currentTimeMillis() >= pullReadyAt
                if (atTop && ready && available.y > 0f) {
                    pullDistance = (pullDistance + available.y).coerceIn(0f, 180f)
                    return Offset(0f, available.y)
                }
                if (pullDistance > 0f && available.y < 0f) {
                    val consumed = minOf(pullDistance, -available.y)
                    pullDistance -= consumed
                    return Offset(0f, -consumed)
                }
                return Offset.Zero
            }

            override suspend fun onPreFling(available: Velocity): Velocity {
                val shouldRefresh = homeScrollState.value == 0 && pullDistance > 90f && !isRefreshing
                pullDistance = 0f
                pullReadyAt = 0L
                if (shouldRefresh) onRefresh()
                return Velocity.Zero
            }
        }
    }
    BackHandler(enabled = selectedAnalysisSection != null) { selectedAnalysisSection = null }
    AnimatedContent(
        modifier = Modifier.fillMaxSize(),
        targetState = selectedAnalysisSection,
        contentAlignment = Alignment.TopStart,
        transitionSpec = {
            if (targetState != null) {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(260)) + fadeIn(tween(160)) togetherWith
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(260)) + fadeOut(tween(120))
            } else {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(260)) + fadeIn(tween(160)) togetherWith
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(260)) + fadeOut(tween(120))
            }
        },
        label = "home-analysis-section"
    ) { section ->
        if (section != null) {
            PortfolioAnalysisScreen(
                accounts = accounts,
                selectedSection = section,
                usdKrw = usdKrw,
                onBack = { selectedAnalysisSection = null }
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .nestedScroll(pullRefreshConnection)
            ) {
                MainTabScreenColumn(scrollState = homeScrollState) {
                    AppHeader(title = "총 자산", onMenuClick = onOpenSettings, onRefreshClick = onRefresh, isRefreshing = isRefreshing)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (pullProgress > 0.04f || isRefreshing) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopCenter)
                                    .padding(top = 22.dp)
                                    .size(34.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                LogoMark(
                                    markSize = 24,
                                    alpha = 1f,
                                    rotation = 0f,
                                    loadingProgress = refreshMarkFill
                                )
                            }
                        }
                        Column(modifier = Modifier.padding(top = animatedPullOffset.dp)) {
                            BigAssetAmount(
                                amount = total,
                                profit = if (profitMode == ProfitMode.TOTAL) totalProfit else dayProfit,
                                rate = if (profitMode == ProfitMode.TOTAL) totalRate else dayRate,
                                label = if (profitMode == ProfitMode.TOTAL) "총 수익" else "일간 수익",
                                onToggleProfitMode = {
                                    onProfitModeChange(if (profitMode == ProfitMode.TOTAL) ProfitMode.DAY else ProfitMode.TOTAL)
                                }
                            )
                            Spacer(modifier = Modifier.height(20.dp))
                            HomeAnalysisShortcutRow(onSelect = { selectedAnalysisSection = it })
                            Spacer(modifier = Modifier.height(24.dp))
                            GoalProgressCard(
                                accounts = accounts,
                                totalAmount = total,
                                principal = principal,
                                plan = goalPlan,
                                onClick = { showGoalDetail = true },
                                onLongClick = onGoalLongClick
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Bottom
                            ) {
                                Text("투자", color = TextPrimary, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                                Text(
                                    "원달러 ${formatDecimal(usdKrw)}원",
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(13.dp))
                            InvestmentToolbar(
                                investmentMode = investmentMode,
                                profitMode = profitMode,
                                sortLabel = sortLabel(accounts.firstOrNull()?.sortField ?: SortField.DIRECT),
                                onInvestmentModeChange = onInvestmentModeChange,
                                onProfitModeChange = onProfitModeChange,
                                onSortClick = onSortClick
                            )
                            Spacer(modifier = Modifier.height(18.dp))
                            if (holdings.isEmpty()) {
                                EmptyAccount()
                            } else {
                                sortHoldings(holdings, accounts.firstOrNull()?.sortField ?: SortField.DIRECT, accounts.firstOrNull()?.sortDescending ?: true)
                                    .forEach { holding ->
                                        InvestmentHoldingRow(
                                            holding = holding,
                                            investmentMode = investmentMode,
                                            profitMode = profitMode,
                                            onClick = { selectedHomeHolding = holding }
                                        )
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    selectedHomeHolding?.let { holding ->
        ModalBottomSheet(
            onDismissRequest = { selectedHomeHolding = null },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = PanelColor,
            modifier = Modifier.fillMaxHeight(0.92f),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            CompactDetailScale {
                HoldingOverviewSheet(
                    holding = holding,
                    accounts = accounts,
                    onClose = { selectedHomeHolding = null }
                )
            }
        }
    }

    if (showGoalDetail) {
        GoalProgressDetailDialog(
            accounts = accounts,
            plan = goalPlan,
            fallbackTotal = total,
            onDismiss = { showGoalDetail = false }
        )
    }
}

@Composable
private fun PortfolioScreen(accounts: List<AccountUi>) {
    MainTabScreenColumn {
        AppHeader(title = "포트폴리오", badge = "관리")
        SummaryCard(
            title = "목표 비중 상태",
            value = "${portfolioHoldings(accounts).size}개 자산 추적 중",
            caption = "종목과 계좌별 자산을 추적하고, 이후 목표 비중과 리밸런싱 상태를 연결합니다."
        )
        Spacer(modifier = Modifier.height(22.dp))
        SectionTitle("계좌")
        accounts.forEach { AccountRow(account = it, onClick = {}) }
        Spacer(modifier = Modifier.height(22.dp))
        SectionTitle("보유 자산")
        portfolioHoldings(accounts).forEach { HoldingRow(it) }
    }
}

@Composable
private fun HoldingOverviewSheet(holding: HoldingUi, onClose: () -> Unit) {
    val dayRate = holding.dayRate
    val dayProfit = holding.dayProfit
    val totalProfit = holding.amount - holding.principal
    val totalRate = if (holding.principal == 0L) 0.0 else totalProfit.toDouble() / holding.principal
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 18.dp, end = 24.dp, bottom = 42.dp)
    ) {
        Text("⌄", color = TextPrimary, fontSize = 34.sp, modifier = Modifier.clickable(onClick = onClose))
        Spacer(modifier = Modifier.height(18.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            AssetBadge(holding.ticker.take(1), holding.color)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(holding.name.ifBlank { holding.ticker }, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    "${holding.ticker} ${formatAssetPrice(holding.currentPrice, holding.ticker)} (${formatPercent(dayRate)})",
                    color = if (dayRate < 0) NegativeBlue else PositiveRed,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(36.dp))
        Text(formatWon(holding.amount), color = TextPrimary, fontSize = 38.sp, lineHeight = 44.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("원금 ${formatWon(holding.principal)}", color = TextSecondary, fontSize = 17.sp)
        Spacer(modifier = Modifier.height(32.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            DetailInfoBlock("보유량", "${formatQuantity(holding.quantity)}주", Modifier.weight(1f))
            DetailInfoBlock("평단가", formatAssetPrice(holding.averagePrice, holding.ticker), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(28.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            DetailInfoBlock("총 수익", "${formatSignedWon(totalProfit)}\n(${formatPercent(totalRate)})", Modifier.weight(1f), if (totalProfit < 0) NegativeBlue else PositiveRed)
            DetailInfoBlock(
                "일간 수익",
                "${formatSignedWon(dayProfit)}\n(${formatPercent(dayRate)})",
                Modifier.weight(1f),
                if (dayProfit < 0) NegativeBlue else PositiveRed
            )
        }
        Spacer(modifier = Modifier.height(28.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(SoftSurface)
                .padding(horizontal = 18.dp, vertical = 18.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("실현수익", color = TextSecondary, fontSize = 16.sp, modifier = Modifier.weight(1f))
                Text("0원", color = TextSecondary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun HoldingOverviewSheet(holding: HoldingUi, accounts: List<AccountUi>, onClose: () -> Unit) {
    val holdingAccounts = accounts.mapNotNull { account ->
        account.holdings.firstOrNull { it.ticker == holding.ticker }?.let { account to it }
    }
    Column(modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {
        HoldingOverviewSheet(holding = holding, onClose = onClose)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 24.dp, end = 24.dp, bottom = 42.dp)
        ) {
            DividerLine()
            Spacer(modifier = Modifier.height(24.dp))
            Text("자산", color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(18.dp))
            Text("${holdingAccounts.size}개", color = TextSecondary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))
            holdingAccounts.forEach { (account, accountHolding) ->
                HomeHoldingAccountRow(account = account, holding = accountHolding)
            }
        }
    }
}

@Composable
private fun HomeHoldingAccountRow(account: AccountUi, holding: HoldingUi) {
    val profit = holding.amount - holding.principal
    val rate = if (holding.principal == 0L) 0.0 else profit.toDouble() / holding.principal
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box {
            AssetBadge(holding.ticker.take(1), holding.color)
            Box(
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.BottomEnd)
                    .clip(CircleShape)
                    .background(account.color),
                contentAlignment = Alignment.Center
            ) {
                Text(accountBadgeText(account).take(1), color = Color.White, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(account.name, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("${formatQuantity(holding.quantity)}주 · ${formatAssetPrice(holding.averagePrice, holding.ticker)}", color = TextSecondary, fontSize = 15.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(formatWon(holding.amount), color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text("${formatSignedWon(profit)} (${formatPercent(rate)})", color = if (profit < 0) NegativeBlue else PositiveRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DetailInfoBlock(label: String, value: String, modifier: Modifier = Modifier, valueColor: Color = TextPrimary) {
    Column(modifier = modifier) {
        Text(label, color = TextSecondary, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(value, color = valueColor, fontSize = 19.sp, fontWeight = FontWeight.Bold, lineHeight = 25.sp)
    }
}

@Composable
private fun PortfolioScreenV2(accounts: List<AccountUi>) {
    var selectedSection by remember { mutableStateOf<String?>(null) }
    val holdings = portfolioHoldings(accounts)
    val totalAmount = holdings.sumOf { it.amount }
    val principal = holdings.sumOf { it.principal }
    val totalProfit = totalAmount - principal
    val totalRate = if (principal == 0L) 0.0 else totalProfit.toDouble() / principal

    BackHandler(enabled = selectedSection != null) { selectedSection = null }
    AnimatedContent(
        modifier = Modifier.fillMaxSize(),
        targetState = selectedSection,
        contentAlignment = Alignment.TopStart,
        transitionSpec = {
            if (targetState != null) {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(260)) + fadeIn(tween(160)) togetherWith
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left, animationSpec = tween(260)) + fadeOut(tween(120))
            } else {
                slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(260)) + fadeIn(tween(160)) togetherWith
                    slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right, animationSpec = tween(260)) + fadeOut(tween(120))
            }
        },
        label = "portfolio-section"
    ) { section ->
        if (section != null) {
            PortfolioAnalysisScreen(
                accounts = accounts,
                selectedSection = section,
                onBack = { selectedSection = null }
            )
        } else {
            MainTabScreenColumn {
                AppHeader(title = "포트폴리오")
                BigAssetAmount(
                    amount = totalAmount,
                    profit = totalProfit,
                    rate = totalRate,
                    label = "총 수익"
                )
                Spacer(modifier = Modifier.height(30.dp))
                PortfolioAnalysisGrid(onSelect = { selectedSection = it })
            }
        }
    }
}

@Composable
private fun PortfolioAnalysisGrid(onSelect: (String) -> Unit) {
    val items = listOf(
        AnalysisSection.PROFIT to "수익",
        AnalysisSection.DIVIDEND to "배당",
        AnalysisSection.ALLOCATION to "비중"
    )
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        items.chunked(2).forEach { rowItems ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                rowItems.forEach { (section, label) ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .height(142.dp)
                            .clip(RoundedCornerShape(22.dp))
                            .background(SoftSurface)
                            .clickable { onSelect(section) }
                            .padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        AnalysisShortcutIcon(section, modifier = Modifier.size(44.dp))
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(label, color = TextPrimary, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeAnalysisShortcutRow(onSelect: (String) -> Unit) {
    val items = listOf(
        AnalysisSection.PROFIT to "수익",
        AnalysisSection.TAX to "세금",
        AnalysisSection.DIVIDEND to "배당",
        AnalysisSection.ALLOCATION to "비중"
    )
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { (section, label) ->
            Column(
                modifier = Modifier
                    .width(58.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .clickable { onSelect(section) }
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnalysisShortcutIcon(section)
                Spacer(modifier = Modifier.height(6.dp))
                Text(label, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun AnalysisShortcutIcon(section: String, modifier: Modifier = Modifier) {
    val iconColor = TextSecondary
    Box(modifier = modifier.size(31.dp), contentAlignment = Alignment.Center) {
        when (section) {
            AnalysisSection.PROFIT,
            AnalysisSection.TAX,
            AnalysisSection.TREND,
            AnalysisSection.ALLOCATION -> {
                Image(
                    painter = painterResource(id = analysisIconRes(section)),
                    contentDescription = null,
                    modifier = Modifier.matchParentSize()
                )
            }
            AnalysisSection.DIVIDEND -> Canvas(modifier = Modifier.size(30.dp)) {
                val barWidth = size.width * 0.18f
                listOf(0.34f, 0.52f, 0.75f).forEachIndexed { index, heightRatio ->
                    val left = size.width * (0.22f + index * 0.23f)
                    val top = size.height * (1f - heightRatio)
                    drawRoundRect(
                        color = iconColor.copy(alpha = 0.38f + index * 0.16f),
                        topLeft = Offset(left, top),
                        size = androidx.compose.ui.geometry.Size(barWidth, size.height * heightRatio - size.height * 0.12f),
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                    )
                }
            }
        }
    }
}

private fun analysisIconRes(section: String): Int = when (section) {
    AnalysisSection.PROFIT -> R.drawable.ic_analysis_profit
    AnalysisSection.TAX -> R.drawable.ic_analysis_tax
    AnalysisSection.TREND -> R.drawable.ic_analysis_trend
    AnalysisSection.ALLOCATION -> R.drawable.ic_analysis_allocation
    else -> R.drawable.ic_analysis_trend
}

@Composable
private fun PortfolioAnalysisScreen(
    accounts: List<AccountUi>,
    selectedSection: String,
    usdKrw: Double = DefaultUsdKrw,
    onBack: () -> Unit
) {
    var activeSection by remember(selectedSection) { mutableStateOf(selectedSection) }
    val headerTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 12.dp
    ScreenColumn(topPadding = 0) {
        Spacer(modifier = Modifier.height(headerTopPadding))
        PlainTopBar(title = "분석", onBack = onBack, rightText = "", onRightClick = {})
        PortfolioAnalysisTabs(selectedSection = activeSection, onSelect = { activeSection = it })
        Spacer(modifier = Modifier.height(28.dp))
        when (activeSection) {
            AnalysisSection.PROFIT -> ProfitAnalysisContent(accounts)
            AnalysisSection.DIVIDEND -> PortfolioDividendContent(accounts, usdKrw)
            AnalysisSection.TAX -> TaxAnalysisContent(accounts)
            AnalysisSection.TREND -> TrendAnalysisContent(accounts)
            AnalysisSection.ALLOCATION -> AllocationAnalysisContent(accounts)
        }
    }
}

@Composable
private fun PortfolioAnalysisTabs(selectedSection: String, onSelect: (String) -> Unit) {
    val tabs = listOf(
        AnalysisSection.PROFIT to "수익",
        AnalysisSection.TAX to "세금",
        AnalysisSection.DIVIDEND to "배당",
        AnalysisSection.ALLOCATION to "비중"
    )
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        tabs.forEach { (section, label) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f).clickable { onSelect(section) }
            ) {
                Text(
                    label,
                    color = if (selectedSection == section) TextPrimary else TextSecondary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.58f)
                        .height(3.dp)
                        .background(if (selectedSection == section) TextPrimary else Color.Transparent)
                )
            }
        }
    }
    DividerLine()
}

@Composable
private fun AnalysisCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PanelColor),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp), content = content)
    }
}

@Composable
private fun ProfitAnalysisContent(accounts: List<AccountUi>) {
    val context = LocalContext.current
    var selectedPeriod by remember { mutableIntStateOf(0) }
    val holdings = portfolioHoldings(accounts)
    val totalAmount = holdings.sumOf { it.amount }
    val principal = holdings.sumOf { it.principal }
    val periodProfit = portfolioProfitForPeriod(context, holdings, selectedPeriod)
    val periodRate = when (selectedPeriod) {
        0 -> if (totalAmount == 0L) 0.0 else periodProfit.toDouble() / totalAmount
        1 -> if (principal == 0L) 0.0 else periodProfit.toDouble() / principal
        else -> {
            val periodBase = portfolioBaseForPeriod(context, holdings, selectedPeriod)
            if (periodBase == 0L) 0.0 else periodProfit.toDouble() / periodBase
        }
    }

    AnalysisCard {
        Text("수익 현황", color = TextPrimary, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(20.dp))
        SegmentedLabels(listOf("오늘", "총", "이번주", "이번달", "올해"), selectedIndex = selectedPeriod, onSelect = { selectedPeriod = it })
        Spacer(modifier = Modifier.height(22.dp))
        Row(verticalAlignment = Alignment.Top) {
            Text("평가수익", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
            Column(horizontalAlignment = Alignment.End) {
                Text(formatSignedWon(periodProfit), color = if (periodProfit < 0) NegativeBlue else PositiveRed, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
                Text("(${formatPercent(periodRate)})", color = if (periodProfit < 0) NegativeBlue else PositiveRed, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        holdings.take(4).forEach { holding ->
            val itemProfit = holdingProfitForPeriod(context, holding, selectedPeriod)
            val itemRate = when (selectedPeriod) {
                0 -> holding.dayRate
                1 -> if (holding.principal == 0L) 0.0 else itemProfit.toDouble() / holding.principal
                else -> {
                    val itemBase = holdingBaseForPeriod(context, holding, selectedPeriod)
                    if (itemBase == 0L) 0.0 else itemProfit.toDouble() / itemBase
                }
            }
            ProfitHoldingMiniRow(holding, itemProfit, itemRate)
        }
        Spacer(modifier = Modifier.height(18.dp))
        DividerLine()
        MetricRow("실현수익", formatWon(0L))
        MetricRow("배당금", formatWon(0L))
        DividerLine()
        Spacer(modifier = Modifier.height(12.dp))
        MetricRow("합계", "${formatSignedWon(periodProfit)} (${formatPercent(periodRate)})")
    }
}

@Composable
private fun ProfitHoldingMiniRow(holding: HoldingUi, profit: Long, rate: Double) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
        AssetBadge(holding.ticker.take(1), holding.color)
        Spacer(modifier = Modifier.width(12.dp))
        Text(assetDisplayName(holding), color = TextSecondary, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            Text(formatSignedWon(profit), color = if (profit < 0) NegativeBlue else PositiveRed, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text("(${formatPercent(rate)})", color = if (profit < 0) NegativeBlue else PositiveRed, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DividendPlaceholderContent() {
    DividendSimulationContent(emptyList(), DefaultUsdKrw)
}

@Composable
private fun TaxAnalysisContent(accounts: List<AccountUi>) {
    val holdings = portfolioHoldings(accounts)
    val overseasHoldings = holdings.filter { holding ->
        val symbol = holding.ticker.uppercase(Locale.US)
        !isKoreanTicker(symbol) && symbol != "KRW"
    }
    val beforeTaxTotal = holdings.sumOf { it.amount }
    val overseasTotal = overseasHoldings.sumOf { it.amount }
    val overseasPrincipal = overseasHoldings.sumOf { it.principal }
    val overseasGain = overseasHoldings.sumOf { (it.amount - it.principal).coerceAtLeast(0L) }
    val overseasLoss = overseasHoldings.sumOf { (it.amount - it.principal).coerceAtMost(0L) }
    val netOverseasProfit = overseasGain + overseasLoss
    val taxableProfit = netOverseasProfit.coerceAtLeast(0L)
    val deduction = 2_500_000L
    val taxBase = (taxableProfit - deduction).coerceAtLeast(0L)
    val transferTax = (taxBase * 0.22).roundToLong()
    val afterTaxTotal = beforeTaxTotal - transferTax
    val afterTaxOverseasProfit = netOverseasProfit - transferTax
    val afterTaxOverseasRate = if (overseasPrincipal > 0L) afterTaxOverseasProfit.toDouble() / overseasPrincipal else 0.0
    val nonOverseasTotal = beforeTaxTotal - overseasTotal
    val effectiveTaxRate = if (netOverseasProfit > 0L) transferTax.toDouble() / netOverseasProfit else 0.0

    AnalysisCard {
        Text("해외직투 세금", color = TextPrimary, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(18.dp))
        MetricRow("세전 총 자산", formatWon(beforeTaxTotal))
        MetricRow("세후 총 자산", formatWon(afterTaxTotal))
        MetricRow("비과세/국내 평가금", formatWon(nonOverseasTotal))
        MetricRow("해외직투 평가금", formatWon(overseasTotal))
        MetricRow("해외 수익", formatWon(overseasGain))
        MetricRow("해외 손실 상계", formatSignedWon(overseasLoss))
        MetricRow("손익통산 후 수익", formatSignedWon(netOverseasProfit))
        MetricRow("기본 공제", formatWon(deduction))
        MetricRow("과세표준", formatWon(taxBase))
        DividerLine()
        MetricRow("예상 양도세", formatWon(transferTax))
        MetricRow("실효 세율", formatPercent(effectiveTaxRate))
        MetricRow("세후 해외 수익률", formatPercent(afterTaxOverseasRate))
        Spacer(modifier = Modifier.height(10.dp))
        Text("현재 미실현 손익을 전부 매도한다고 가정한 추정치입니다. 실제 세금은 매도 시점의 확정 손익, 환율, 수수료, 다른 해외주식 손익에 따라 달라질 수 있어요.", color = TextSecondary, fontSize = 13.sp, lineHeight = 19.sp)
    }
    Spacer(modifier = Modifier.height(16.dp))
    AnalysisCard {
        Text("세금 체크", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(12.dp))
        val deductionRoom = (deduction - taxableProfit).coerceAtLeast(0L)
        val realizedSignal = when {
            overseasHoldings.isEmpty() -> "해외직투 종목 없음"
            netOverseasProfit <= 0L -> "손실 상계 우선"
            deductionRoom > 0L -> "기본공제 여유"
            else -> "양도세 발생 구간"
        }
        MetricRow("판단 구간", realizedSignal)
        MetricRow("남은 기본공제", formatWon(deductionRoom))
        MetricRow("손실 상계 효과", formatWon(kotlin.math.abs(overseasLoss)))
        MetricRow("해외 비중", formatPercent(if (beforeTaxTotal > 0L) overseasTotal.toDouble() / beforeTaxTotal else 0.0))
        Spacer(modifier = Modifier.height(8.dp))
        Text("올해 안에 일부 매도할 계획이 있다면 해외 수익 종목과 손실 종목을 함께 봐야 합니다. 이 카드는 현재 보유 손익 기준으로 기본공제와 손실 상계 여지를 먼저 보여줍니다.", color = TextSecondary, fontSize = 13.sp, lineHeight = 19.sp)
    }
    Spacer(modifier = Modifier.height(16.dp))
    AnalysisCard {
        val topHolding = holdings.maxByOrNull { it.amount }
        val topWeight = if (beforeTaxTotal > 0L && topHolding != null) topHolding.amount.toDouble() / beforeTaxTotal else 0.0
        val guardrailWeight = 0.50
        val trimAmount = if (topHolding != null && topWeight > guardrailWeight) {
            (topHolding.amount - beforeTaxTotal * guardrailWeight).roundToLong().coerceAtLeast(0L)
        } else {
            0L
        }
        val topSymbol = topHolding?.ticker?.uppercase(Locale.US).orEmpty()
        val topIsOverseas = topHolding != null && !isKoreanTicker(topSymbol) && topSymbol != "KRW"
        val topProfit = topHolding?.let { it.amount - it.principal } ?: 0L
        val profitRatioInSale = if (topHolding != null && topHolding.amount > 0L && topProfit > 0L) {
            topProfit.toDouble() / topHolding.amount
        } else {
            0.0
        }
        val trimProfit = if (topIsOverseas) (trimAmount * profitRatioInSale).roundToLong() else 0L
        val remainingDeduction = (deduction - taxableProfit).coerceAtLeast(0L)
        val trimTax = if (trimProfit > remainingDeduction) ((trimProfit - remainingDeduction) * 0.22).roundToLong() else 0L
        val afterTaxRebalanceCash = (trimAmount - trimTax).coerceAtLeast(0L)
        val rebalanceSignal = when {
            topHolding == null -> "보유 종목 없음"
            trimAmount <= 0L -> "세금 매도 압박 낮음"
            trimTax <= 0L -> "공제/손실상계 내 조정 가능"
            else -> "세후 재배치 금액 확인 필요"
        }

        Text("세금 반영 리밸런싱", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(12.dp))
        MetricRow("판단", rebalanceSignal)
        MetricRow("최대 종목 비중", topHolding?.let { "${assetDisplayName(it)} ${formatPercent(topWeight)}" } ?: "없음")
        MetricRow("가드레일", "단일 종목 50% 초과분 점검")
        MetricRow("조정 매도액", formatWon(trimAmount))
        MetricRow("예상 양도세", formatWon(trimTax))
        MetricRow("세후 재배치 가능액", formatWon(afterTaxRebalanceCash))
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "해외직투 수익 종목을 팔아 비중을 맞추면 양도세가 먼저 빠져 실제 재투입 가능 금액이 줄어듭니다. 손실 종목이 있거나 기본공제가 남아 있으면 같은 리밸런싱도 세금 부담이 낮아질 수 있어요.",
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )
    }
    Spacer(modifier = Modifier.height(24.dp))
    Text("해외직투 종목", color = TextPrimary, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
    Spacer(modifier = Modifier.height(12.dp))
    if (overseasHoldings.isEmpty()) {
        Text("해외직투 종목이 없습니다.", color = TextSecondary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
    } else {
        overseasHoldings.sortedByDescending { it.amount }.forEach { holding ->
            val profit = holding.amount - holding.principal
            val allocatedTax = if (profit > 0L && overseasGain > 0L) {
                (transferTax * profit.toDouble() / overseasGain).roundToLong()
            } else {
                0L
            }
            val afterTaxAmount = holding.amount - allocatedTax
            val afterTaxProfit = profit - allocatedTax
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssetBadge(holding.ticker.take(1), holding.color)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(assetDisplayName(holding), color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                    Text("${formatQuantity(holding.quantity)}주 · 원금 ${formatWon(holding.principal)}", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("세후 ${formatWon(afterTaxAmount)}", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Text("양도세 ${formatWon(allocatedTax)}", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("세후손익 ${formatSignedWon(afterTaxProfit)}", color = if (afterTaxProfit < 0) NegativeBlue else PositiveRed, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun PortfolioDividendContent(accounts: List<AccountUi>, usdKrw: Double) {
    val currentYear = LocalDate.now().year
    var selectedMonth by remember { mutableStateOf<Int?>(null) }
    var showNetAmount by remember { mutableStateOf(true) }
    val holdings = portfolioHoldings(accounts)
        .filter { it.ticker.uppercase(Locale.US) != "KRW" && it.quantity > 0.0 }
    val items = remember(holdings, usdKrw) { buildPortfolioDividendItems(holdings, usdKrw) }
    val monthlyTotals = (1..12).map { month ->
        items.filter { it.month == month }.sumOf { portfolioDividendDisplayAmount(it.amount, showNetAmount) }
    }
    val annualTotal = monthlyTotals.sum()
    val totalAmount = holdings.sumOf { it.amount }
    val dividendYield = if (totalAmount > 0L) annualTotal.toDouble() / totalAmount else 0.0
    val groupedMonths = items.groupBy { it.month }.toSortedMap()
    val selectedHasDividend = selectedMonth?.let { month -> monthlyTotals.getOrNull(month - 1)?.let { it > 0L } } == true
    val visibleGroupedMonths = if (selectedHasDividend) {
        groupedMonths.filterKeys { it == selectedMonth }
    } else {
        groupedMonths
    }

    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                "${currentYear}년",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text("▼", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.weight(1f))
        DividendCheckedLabel("실수령액", checked = showNetAmount) {
            showNetAmount = !showNetAmount
        }
    }
    Spacer(modifier = Modifier.height(18.dp))
    Text(
        formatWon(annualTotal),
        color = TextPrimary,
        fontSize = 39.sp,
        fontWeight = FontWeight.ExtraBold,
        lineHeight = 44.sp
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
        "투자배당률 ${formatDecimal(dividendYield * 100)}%",
        color = TextSecondary,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.height(18.dp))
    MonthlyDividendBarChart(monthlyTotals, selectedMonth) { month ->
        val hasDividend = monthlyTotals.getOrNull(month - 1)?.let { it > 0L } == true
        selectedMonth = if (!hasDividend || selectedMonth == month) null else month
    }
    Spacer(modifier = Modifier.height(20.dp))
    DividerLine()
    Spacer(modifier = Modifier.height(24.dp))

    if (items.isEmpty()) {
        AnalysisCard {
            Text(
                "예상 배당이 있는 보유 종목이 없습니다.",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "QLD, QQQ, SCHD, JEPI, JEPQ, QQQI, SPYI, ACE 미국나스닥100처럼 배당 프로필이 있는 종목을 보유하면 월별 배당 흐름을 보여줍니다.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
    } else {
        visibleGroupedMonths.forEach { (month, monthItems) ->
            DividendMonthSection(month, monthItems, showNetAmount)
        }
    }
}

@Composable
private fun DividendCheckedLabel(text: String, checked: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(23.dp)
                .clip(CircleShape)
                .background(if (checked) TextPrimary else LineColor),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Text("✓", color = AppBackground, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        Spacer(modifier = Modifier.width(6.dp))
        Text(text, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun MonthlyDividendBarChart(monthlyTotals: List<Long>, selectedMonth: Int?, onSelect: (Int) -> Unit) {
    val maxValue = (monthlyTotals.maxOrNull() ?: 0L).coerceAtLeast(1L)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
    ) {
        monthlyTotals.forEachIndexed { index, total ->
            val month = index + 1
            val active = month == selectedMonth
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSelect(month) },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (total > 0L) {
                    Text(
                        compactWon(total),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(28.dp)
                            .height((20f + total.toFloat() / maxValue.toFloat() * 74f).dp)
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (active) PositiveRed else PositiveRed.copy(alpha = 0.22f))
                    )
                } else {
                    Spacer(modifier = Modifier.height(94.dp))
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    month.toString(),
                    color = if (active) TextPrimary else TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun DividendMonthSection(month: Int, items: List<PortfolioDividendItem>, showNetAmount: Boolean) {
    val total = items.sumOf { portfolioDividendDisplayAmount(it.amount, showNetAmount) }
    Spacer(modifier = Modifier.height(18.dp))
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("${month}월", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.weight(1f))
        Text(formatWon(total), color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
    }
    Spacer(modifier = Modifier.height(14.dp))
    items.sortedWith(compareBy<PortfolioDividendItem> { it.day }.thenBy { it.holding.name }).forEach { item ->
        PortfolioDividendRow(item, showNetAmount)
    }
    DividerLine()
}

@Composable
private fun PortfolioDividendRow(item: PortfolioDividendItem, showNetAmount: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            "${item.day}일",
            color = TextSecondary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(42.dp)
        )
        AssetBadge(item.holding.ticker.take(1), item.holding.color)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                assetDisplayName(item.holding),
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "${formatQuantity(item.holding.quantity)}주 · 1주당 ${formatDividendPerShare(item)}",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(5.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item.months.forEach { DividendMonthChip("${it}월") }
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(horizontalAlignment = Alignment.End) {
            Text(
                formatWon(portfolioDividendDisplayAmount(item.amount, showNetAmount)),
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.End
            )
            Text(
                item.payDateLabel,
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.End
            )
        }
    }
}

private const val PortfolioDividendTaxRate = DividendTaxEngine.DOMESTIC_WITHHOLDING_TAX_RATE
private const val OverseasDividendTaxRate = DividendTaxEngine.OVERSEAS_WITHHOLDING_TAX_RATE
private const val DividendDefaultComprehensiveExtraTaxRate =
    DividendTaxEngine.DEFAULT_COMPREHENSIVE_EXTRA_TAX_RATE_PERCENT

private fun portfolioDividendDisplayAmount(grossAmount: Long, showNetAmount: Boolean): Long =
    if (showNetAmount) (grossAmount * (1.0 - PortfolioDividendTaxRate)).roundToLong() else grossAmount

private fun dividendWithholdingTaxRate(candidate: DividendEtfUi): Double =
    if (candidate.currency.equals("USD", ignoreCase = true)) OverseasDividendTaxRate else PortfolioDividendTaxRate

private fun DividendPersonUi.toDividendTaxPerson(): DividendTaxPerson =
    DividendTaxPerson(comprehensiveExtraTaxRatePercent = incomeTaxRate)

private fun estimatedDividendHealthInsuranceWon(
    grossAnnualDividendWon: Double,
    _person: DividendPersonUi
): Double = DividendTaxEngine.estimatedHealthInsuranceWon(grossAnnualDividendWon)

private fun estimatedDividendHealthInsuranceWon(
    grossAnnualDividendWon: Double,
    people: List<DividendPersonUi>
): Double = DividendTaxEngine.estimatedHealthInsuranceWon(
    grossAnnualDividendWon = grossAnnualDividendWon,
    people = people.map(DividendPersonUi::toDividendTaxPerson)
)

private fun dividendAfterTaxAnnualWon(
    grossAnnualDividendWon: Double,
    person: DividendPersonUi,
    withholdingTaxRate: Double = PortfolioDividendTaxRate
): Double {
    return DividendTaxEngine.afterTaxAnnualWon(
        grossAnnualDividendWon = grossAnnualDividendWon,
        person = person.toDividendTaxPerson(),
        withholdingTaxRate = withholdingTaxRate
    )
}

private fun dividendAfterTaxAnnualWon(
    grossAnnualDividendWon: Double,
    people: List<DividendPersonUi>,
    withholdingTaxRate: Double = PortfolioDividendTaxRate
): Double {
    return DividendTaxEngine.afterTaxAnnualWon(
        grossAnnualDividendWon = grossAnnualDividendWon,
        people = people.map(DividendPersonUi::toDividendTaxPerson),
        withholdingTaxRate = withholdingTaxRate
    )
}

private fun requiredDividendShares(
    grossAnnualDividendPerShare: Double,
    targetAnnualDividendWon: Long,
    people: List<DividendPersonUi>,
    withholdingTaxRate: Double = PortfolioDividendTaxRate
): Long {
    return DividendTaxEngine.requiredShares(
        grossAnnualDividendPerShare = grossAnnualDividendPerShare,
        targetAnnualDividendWon = targetAnnualDividendWon,
        people = people.map(DividendPersonUi::toDividendTaxPerson),
        withholdingTaxRate = withholdingTaxRate
    )
}

@Composable
private fun DividendMonthChip(text: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(SoftSurface)
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(text, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

private fun buildPortfolioDividendItems(holdings: List<HoldingUi>, usdKrw: Double): List<PortfolioDividendItem> =
    holdings.flatMap { holding ->
        val profile = dividendProfileForHolding(holding)
        if (profile == null) {
            emptyList()
        } else {
            profile.months.map { month ->
                val fx = if (profile.currency == "USD") usdKrw else 1.0
                val amount = (holding.quantity * profile.perShareAmount * fx).roundToLong().coerceAtLeast(0L)
                PortfolioDividendItem(
                    month = month,
                    day = profile.day,
                    holding = holding,
                    perShareAmount = profile.perShareAmount,
                    currency = profile.currency,
                    amount = amount,
                    months = profile.months,
                    payDateLabel = dividendPayDateLabel(month)
                )
            }
        }
    }

private fun dividendProfileForHolding(holding: HoldingUi): PortfolioDividendProfile? {
    val ticker = holding.ticker.uppercase(Locale.US)
    val name = holding.name
    return when {
        ticker == "QLD" -> PortfolioDividendProfile(0.04, "USD", listOf(3, 6, 9, 12), 30)
        ticker == "QQQ" -> PortfolioDividendProfile(0.85, "USD", listOf(3, 6, 9, 12), 30)
        ticker == "SPY" -> PortfolioDividendProfile(1.75, "USD", listOf(3, 6, 9, 12), 30)
        ticker == "VOO" -> PortfolioDividendProfile(1.80, "USD", listOf(3, 6, 9, 12), 30)
        ticker == "SCHD" -> PortfolioDividendProfile(0.75, "USD", listOf(3, 6, 9, 12), 30)
        ticker in setOf("JEPI", "JEPQ", "QYLD", "QQQI", "SPYI") -> {
            val candidate = dividendCandidates().firstOrNull { it.ticker.equals(ticker, ignoreCase = true) }
            val price = holding.currentPrice.takeIf { it > 0.0 } ?: candidate?.fallbackPrice ?: 0.0
            val annualDividend = price * ((candidate?.yieldRate ?: 0.0) / 100.0)
            PortfolioDividendProfile(annualDividend / 12.0, "USD", (1..12).toList(), 15)
        }
        ticker == "367380" || name.contains("ACE 미국나스닥100") -> PortfolioDividendProfile(35.0, "KRW", listOf(2, 5, 8, 11), 4)
        ticker == "433880" || name.contains("PLUS TDF2060") -> PortfolioDividendProfile(35.0, "KRW", listOf(2, 5, 8, 11), 4)
        else -> null
    }
}

private fun dividendPayDateLabel(month: Int): String {
    val exMonth = if (month == 1) 12 else month - 1
    return "배당락일 ${exMonth}월 30일 · 예상"
}

private fun formatDividendPerShare(item: PortfolioDividendItem): String =
    if (item.currency == "USD") "$${formatDecimal(item.perShareAmount)}" else formatWon(item.perShareAmount.roundToLong())

private fun compactWon(value: Long): String =
    if (value >= 10_000L) "${NumberFormat.getNumberInstance(Locale.KOREA).format(value / 10_000)}만" else value.toString()

private fun dividendProjectionInputKey(
    ticker: String,
    modeIndex: Int,
    targetInput: String,
    people: List<DividendPersonUi>,
    usdKrw: Double
): String = buildString {
    append(ticker.uppercase(Locale.US))
    append('|')
    append(modeIndex)
    append('|')
    append(targetInput.filter(Char::isDigit))
    append('|')
    append(usdKrw.toBits())
    people.forEach { person ->
        append('|')
        append(person.id)
        append(':')
        append(person.incomeTaxRate.toBits())
        append(':')
        append(person.annualPensionIncome)
    }
}

@Composable
private fun DividendScreen(accounts: List<AccountUi>, usdKrw: Double) {
    MainTabScreenColumn {
        AppHeader(title = "배당 시뮬레이션")
        DividendSimulationContent(accounts, usdKrw)
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DividendSimulationContent(accounts: List<AccountUi>, usdKrw: Double) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    remember(context) {
        if (savedDividendSimulationPresetsCache.isEmpty()) {
            savedDividendSimulationPresetsCache.addAll(loadSavedDividendPresets(context))
        }
        true
    }
    val candidates = remember { dividendCandidates() }
    val cachedPreset = remember(context) {
        (lastDividendSimulationPresetCache ?: loadLastDividendPreset(context)).also {
            lastDividendSimulationPresetCache = it
        }
    }
    val savedPresets = remember {
        mutableStateListOf<DividendSimulationPreset>().also { it.addAll(savedDividendSimulationPresetsCache) }
    }
    val defaultPeople = remember { listOf(DividendPersonUi(1L, "본인")) }
    val people = remember {
        mutableStateListOf<DividendPersonUi>().also { list ->
            list.addAll(cachedPreset?.people?.takeIf { it.isNotEmpty() } ?: defaultPeople)
        }
    }
    var selected by remember {
        mutableStateOf(candidates.firstOrNull { it.ticker.equals(cachedPreset?.ticker, ignoreCase = true) } ?: candidates.first())
    }
    var modeIndex by remember { mutableIntStateOf(cachedPreset?.modeIndex ?: 0) }
    var targetInput by remember { mutableStateOf(cachedPreset?.targetInput ?: "3000000") }
    var selectedPersonId by remember { mutableStateOf(cachedPreset?.selectedPersonId ?: people.first().id) }
    var showPersonDialog by remember { mutableStateOf(false) }
    var editingPerson by remember { mutableStateOf<DividendPersonUi?>(null) }
    var personName by remember { mutableStateOf("") }
    var personIncomeTax by remember { mutableStateOf("") }
    var personPensionIncome by remember { mutableStateOf("") }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showLoadDialog by remember { mutableStateOf(false) }
    var presetName by remember { mutableStateOf("") }
    var deletePerson by remember { mutableStateOf<DividendPersonUi?>(null) }
    var deletePreset by remember { mutableStateOf<DividendSimulationPreset?>(null) }
    var renamePreset by remember { mutableStateOf<DividendSimulationPreset?>(null) }
    var chartSnapshot by remember(context) {
        mutableStateOf(
            (lastDividendChartUpdateSnapshotCache ?: loadLastDividendChartUpdateSnapshot(context)).also {
                lastDividendChartUpdateSnapshotCache = it
            }
        )
    }
    var isUpdatingCharts by remember { mutableStateOf(false) }

    if (people.isEmpty()) people.addAll(defaultPeople)
    val activePeople = people.toList().ifEmpty { defaultPeople }
    val targetAmount = targetInput.filter { it.isDigit() }.toLongOrNull() ?: 0L
    val priceKrw = dividendPriceKrw(selected, usdKrw)
    val grossAnnualDividendPerShare = priceKrw * (selected.yieldRate / 100.0)
    val selectedWithholdingTaxRate = dividendWithholdingTaxRate(selected)
    val requiredShares = when (modeIndex) {
        0 -> requiredDividendShares(
            grossAnnualDividendPerShare,
            targetAmount.coerceAtMost(Long.MAX_VALUE / 12) * 12,
            activePeople,
            selectedWithholdingTaxRate
        )
        1 -> requiredDividendShares(grossAnnualDividendPerShare, targetAmount, activePeople, selectedWithholdingTaxRate)
        else -> if (priceKrw > 0.0) (targetAmount / priceKrw).roundToLong() else 0L
    }.coerceAtLeast(0L)
    val requiredCapital = (requiredShares * priceKrw).roundToLong()
    val grossExpectedAnnualDividend = requiredShares * grossAnnualDividendPerShare
    val estimatedHealthInsurance = estimatedDividendHealthInsuranceWon(grossExpectedAnnualDividend, activePeople).roundToLong()
    val expectedAnnualDividend =
        dividendAfterTaxAnnualWon(grossExpectedAnnualDividend, activePeople, selectedWithholdingTaxRate).roundToLong()
    val expectedMonthlyDividend = expectedAnnualDividend / 12
    val holdings = portfolioHoldings(accounts)
    val candidateByTicker = candidates.associateBy { it.ticker.uppercase(Locale.US) }
    val grossDividendProjections = holdings.mapNotNull { holding ->
        val candidate = candidateByTicker[holding.ticker.uppercase(Locale.US)] ?: return@mapNotNull null
        val grossAnnualDividend = holding.amount * (candidate.yieldRate / 100.0)
        Triple(holding, candidate, grossAnnualDividend)
    }
    val dividendProjections = grossDividendProjections.map { (holding, candidate, grossAnnualDividend) ->
        val annualDividend = dividendAfterTaxAnnualWon(
            grossAnnualDividend,
            activePeople,
            dividendWithholdingTaxRate(candidate)
        ).roundToLong()
        val growthRate = chartSnapshot
            ?.takeIf { it.ticker.equals(candidate.ticker, ignoreCase = true) }
            ?.dividendGrowthMetric
            ?.numericValue
            ?: candidate.dividendGrowth5y
        val fiveYearAnnualDividend = (annualDividend * (1.0 + growthRate / 100.0).pow(5.0)).roundToLong()
        DividendHoldingProjection(
            holding = holding,
            candidate = candidate,
            annualDividend = annualDividend,
            fiveYearAnnualDividend = fiveYearAnnualDividend
        )
    }
    val ownedMonthlyDividend = dividendProjections
        .filter { it.candidate.ticker.equals(selected.ticker, ignoreCase = true) }
        .sumOf { it.annualDividend } / 12
    val targetMonthlyDividend = when (modeIndex) {
        0 -> targetAmount
        1 -> targetAmount / 12
        else -> expectedMonthlyDividend
    }
    val selectedChartSnapshot = chartSnapshot?.takeIf { it.ticker.equals(selected.ticker, ignoreCase = true) }
    val displayedDividendGrowthMetric = selectedChartSnapshot?.dividendGrowthMetric ?: DividendMetricUi(
        label = "평균 배당성장율",
        value = "${formatDecimal(selected.dividendGrowth5y)}%",
        numericValue = selected.dividendGrowth5y
    )
    val displayedPriceGrowthMetric = selectedChartSnapshot?.priceGrowthMetric ?: DividendMetricUi(
        label = "평균 가격성장률",
        value = "${formatDecimal(selected.priceGrowth10y)}%",
        numericValue = selected.priceGrowth10y
    )
    val chartCandidate = chartSnapshot?.let { snapshot ->
        candidates.firstOrNull { it.ticker.equals(snapshot.ticker, ignoreCase = true) }
    }
    val currentProjectionInputKey = dividendProjectionInputKey(
        ticker = selected.ticker,
        modeIndex = modeIndex,
        targetInput = targetInput,
        people = activePeople,
        usdKrw = usdKrw
    )
    val currentProjectionRows = chartSnapshot
        ?.takeIf { it.inputKey == currentProjectionInputKey }
        ?.projectionRows
        .orEmpty()

    fun currentPreset(name: String = "마지막 배당 시뮬레이션") = DividendSimulationPreset(
        name = name,
        ticker = selected.ticker,
        modeIndex = modeIndex,
        targetInput = targetInput,
        showAfterTax = true,
        selectedPersonId = selectedPersonId,
        people = people.toList(),
        projectionRows = currentProjectionRows
    )

    fun saveNamedPreset(name: String) {
        val preset = currentPreset(name)
        savedPresets.removeAll { it.name == preset.name }
        savedDividendSimulationPresetsCache.removeAll { it.name == preset.name }
        savedPresets.add(preset)
        savedDividendSimulationPresetsCache.add(preset)
        saveSavedDividendPresets(context, savedDividendSimulationPresetsCache)
        lastDividendSimulationPresetCache = preset
        presetName = ""
        showSaveDialog = false
        Toast.makeText(context, "배당 시뮬레이션을 저장했어요.", Toast.LENGTH_SHORT).show()
    }

    fun renameSavedPreset(preset: DividendSimulationPreset, newName: String) {
        val renamed = preset.copy(name = newName)
        val listIndex = savedPresets.indexOfFirst { it.name == preset.name }
        val cacheIndex = savedDividendSimulationPresetsCache.indexOfFirst { it.name == preset.name }
        if (listIndex >= 0) savedPresets[listIndex] = renamed
        if (cacheIndex >= 0) savedDividendSimulationPresetsCache[cacheIndex] = renamed
        saveSavedDividendPresets(context, savedDividendSimulationPresetsCache)
        if (lastDividendSimulationPresetCache?.name == preset.name) {
            lastDividendSimulationPresetCache = renamed
            saveLastDividendPreset(context, renamed)
        }
        renamePreset = null
    }

    SideEffect {
        val latest = currentPreset()
        if (latest != lastDividendSimulationPresetCache) {
            lastDividendSimulationPresetCache = latest
            saveLastDividendPreset(context, latest)
        }
    }

    fun updateDividendCharts() {
        val candidate = selected
        val monthlyTarget = targetMonthlyDividend
        val peopleSnapshot = activePeople.toList()
        val grossDividendSnapshot = grossExpectedAnnualDividend
        val initialAssetSnapshot = requiredCapital
        val withholdingTaxSnapshot = selectedWithholdingTaxRate
        val inputKeySnapshot = currentProjectionInputKey
        scope.launch {
            isUpdatingCharts = true
            try {
                val updated = withContext(Dispatchers.Default) {
                    val updatedDividendGrowthMetric = dividendGrowthMetric(context, candidate)
                    val updatedPriceGrowthMetric = dividendPriceGrowthMetric(context, candidate)
                    DividendChartUpdateSnapshot(
                        inputKey = inputKeySnapshot,
                        ticker = candidate.ticker,
                        targetMonthlyDividend = monthlyTarget,
                        dividendGrowthMetric = updatedDividendGrowthMetric,
                        priceGrowthMetric = updatedPriceGrowthMetric,
                        pricePoints = dividendPriceChartPoints(context, candidate),
                        growthPoints = dividendGrowthChartPoints(
                            context = context,
                            candidate = candidate,
                            targetMonthlyDividend = monthlyTarget,
                            people = peopleSnapshot,
                            usdKrw = usdKrw
                        ),
                        projectionRows = dividendGrowthProjectionRows(
                            grossAnnualDividend = grossDividendSnapshot,
                            initialAsset = initialAssetSnapshot,
                            dividendGrowthRate = updatedDividendGrowthMetric.numericValue ?: candidate.dividendGrowth5y,
                            priceGrowthRate = updatedPriceGrowthMetric.numericValue ?: candidate.priceGrowth10y,
                            people = peopleSnapshot,
                            withholdingTaxRate = withholdingTaxSnapshot
                        )
                    )
                }
                chartSnapshot = updated
                lastDividendChartUpdateSnapshotCache = updated
                saveLastDividendChartUpdateSnapshot(context, updated)
            } catch (_: Exception) {
                Toast.makeText(context, "그래프 업데이트에 실패했어요.", Toast.LENGTH_SHORT).show()
            } finally {
                isUpdatingCharts = false
            }
        }
    }

    fun openPersonDialog(person: DividendPersonUi? = null) {
        editingPerson = person
        personName = person?.name ?: ""
        personIncomeTax = person?.incomeTaxRate?.takeIf { it > 0.0 }?.let { formatDecimal(it) } ?: ""
        personPensionIncome = person?.annualPensionIncome?.takeIf { it > 0L }?.let { formatNumberInput(it.toString()) } ?: ""
        showPersonDialog = true
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        SectionTitle("ETF 선택")
        Spacer(modifier = Modifier.weight(1f))
        Text(
            "불러오기",
            color = BrandGreen,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { showLoadDialog = true }
        )
    }
    Spacer(modifier = Modifier.height(12.dp))
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        candidates.chunked(2).forEach { row ->
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { candidate ->
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (candidate.ticker == selected.ticker) BrandSoftBlue.copy(alpha = 0.28f) else SoftSurface)
                            .clickable { selected = candidate }
                            .padding(14.dp)
                    ) {
                        Text(candidate.ticker, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("${formatDecimal(candidate.yieldRate)}% · ${candidate.currency}", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                if (row.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))
    AnalysisCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AssetBadge(selected.ticker.take(1), selected.color)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(selected.name, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text("${selected.frequency} · ${selected.currency}", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(BrandSoftBlue.copy(alpha = 0.32f))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text("배당성장", color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        Spacer(modifier = Modifier.height(18.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DividendInfoBlock("현재가", if (selected.currency == "USD") "$${formatDecimal(selected.fallbackPrice)}" else formatWon(priceKrw.roundToLong()))
            DividendInfoBlock("배당수익률", "${formatDecimal(selected.yieldRate)}%")
            DividendInfoBlock("배당주기", selected.frequency)
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            DividendInfoBlock(displayedDividendGrowthMetric.label, displayedDividendGrowthMetric.value)
            DividendInfoBlock(displayedPriceGrowthMetric.label, displayedPriceGrowthMetric.value)
            DividendInfoBlock("보유 월 배당", formatWon(ownedMonthlyDividend))
        }
    }

    Spacer(modifier = Modifier.height(18.dp))
    DividendTargetSettingsCard(
        modeIndex = modeIndex,
        targetInput = targetInput,
        activePeopleCount = activePeople.size,
        usdKrw = usdKrw,
        requiredShares = requiredShares,
        requiredCapital = requiredCapital,
        expectedMonthlyDividend = expectedMonthlyDividend,
        expectedAnnualDividend = expectedAnnualDividend,
        onModeChange = { modeIndex = it },
        onTargetInputChange = { targetInput = it }
    )

    Spacer(modifier = Modifier.height(18.dp))
    AnalysisCard {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("인원별 세금", color = TextPrimary, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                Text("사람마다 종소세와 건보료를 다르게 반영합니다.", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                "+ 인원",
                color = BrandGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.clickable { openPersonDialog() }
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        people.forEach { person ->
            val selectedPersonRow = person.id == selectedPersonId
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (selectedPersonRow) BrandSoftBlue.copy(alpha = 0.28f) else Color.Transparent)
                    .combinedClickable(
                        onClick = {
                            selectedPersonId = person.id
                            openPersonDialog(person)
                        },
                        onLongClick = { deletePerson = person }
                    )
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (selectedPersonRow) BrandGreen else MutedText),
                    contentAlignment = Alignment.Center
                ) {
                    Text(person.name.take(1), color = PanelColor, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(person.name, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        buildString {
                            if (person.incomeTaxRate > 0.0) {
                                append("종소세 추가 ${formatDecimal(person.incomeTaxRate)}%")
                            } else {
                                append("종소세 자동 ${formatDecimal(DividendDefaultComprehensiveExtraTaxRate)}%")
                            }
                            append(" · 건보료 자동")
                            if (person.annualPensionIncome > 0L) {
                                append(" · 연소득 ${formatWon(person.annualPensionIncome)}")
                            }
                        },
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                if (selectedPersonRow) Text("선택", color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "세후 배당은 인원별로 나눠 계산합니다. 1인당 배당소득이 연 2,000만원을 넘으면 건보료는 배당소득 전체에 반영하고, 2,000만원 초과분은 종소세 추가율을 반영해요. 예상 건보료 ${formatWon(estimatedHealthInsurance)}",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }

    Spacer(modifier = Modifier.height(18.dp))
    PrimaryActionButton(
        text = if (isUpdatingCharts) "그래프 업데이트 중..." else "그래프 및 20년 전망 업데이트",
        enabled = !isUpdatingCharts,
        onClick = ::updateDividendCharts
    )

    Spacer(modifier = Modifier.height(18.dp))
    AnalysisCard {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("가격 차트", color = TextPrimary, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                Text("${chartCandidate?.ticker ?: selected.ticker} · 다운로드 가격 데이터", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            Text(
                chartSnapshot?.priceGrowthMetric?.value ?: "업데이트 대기",
                color = if ((chartSnapshot?.priceGrowthMetric?.numericValue ?: 0.0) < 0) NegativeBlue else PositiveRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (chartSnapshot != null && chartCandidate != null) {
            DividendPriceChart(chartCandidate, chartSnapshot!!.pricePoints)
        } else {
            DividendEmptyChart("업데이트 버튼을 눌러 가격 차트를 계산해 주세요.")
        }
    }

    Spacer(modifier = Modifier.height(18.dp))
    AnalysisCard {
        Text("배당 성장 차트", color = TextPrimary, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
        Text(
            "목표 월 배당액을 첫 배당 데이터 시점에 받았다고 가정하고, 실제 분배금 성장 흐름을 적용해 보여줍니다.",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (chartSnapshot != null && chartCandidate != null) {
            DividendGrowthChart(
                candidate = chartCandidate,
                targetMonthlyDividend = chartSnapshot!!.targetMonthlyDividend,
                points = chartSnapshot!!.growthPoints
            )
        } else {
            DividendEmptyChart("업데이트 버튼을 눌러 배당 성장 흐름을 계산해 주세요.")
        }
        Spacer(modifier = Modifier.height(14.dp))
        DividendGrowthProjectionTable(
            rows = chartSnapshot?.projectionRows.orEmpty()
        )
    }

    Spacer(modifier = Modifier.height(18.dp))
    PrimaryActionButton("배당 시뮬레이션 저장", enabled = true) {
        if (currentProjectionRows.isEmpty()) {
            Toast.makeText(context, "그래프 및 20년 전망을 먼저 업데이트해 주세요.", Toast.LENGTH_SHORT).show()
        } else {
            showSaveDialog = true
        }
    }

    if (showPersonDialog) {
        AlertDialog(
            onDismissRequest = { showPersonDialog = false },
            containerColor = PanelColor,
            title = { Text(if (editingPerson == null) "인원 추가" else "인원 정보 수정", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = personName,
                        onValueChange = { personName = it },
                        singleLine = true,
                        label = { Text("이름") },
                        colors = appTextFieldColors()
                    )
                    OutlinedTextField(
                        value = personIncomeTax,
                        onValueChange = { personIncomeTax = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        singleLine = true,
                        label = { Text("종합소득세 추가율") },
                        suffix = { Text("%", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = appTextFieldColors()
                    )
                    OutlinedTextField(
                        value = personPensionIncome,
                        onValueChange = { personPensionIncome = formatNumberInput(it) },
                        singleLine = true,
                        label = { Text("연금/기타 연소득") },
                        suffix = { Text("원", color = TextSecondary) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = appTextFieldColors()
                    )
                    Text(
                        "종소세 추가율을 비워두면 ${formatDecimal(DividendDefaultComprehensiveExtraTaxRate)}%로 추정합니다. 건보료는 1인당 배당소득이 연 2,000만원을 넘으면 배당소득 전체에 보험료율을 적용해 자동 추정합니다.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val trimmed = personName.trim()
                    if (trimmed.isNotEmpty()) {
                        val saved = DividendPersonUi(
                            id = editingPerson?.id ?: System.currentTimeMillis(),
                            name = trimmed,
                            incomeTaxRate = personIncomeTax.toDoubleOrNull() ?: 0.0,
                            healthInsuranceRate = 0.0,
                            annualPensionIncome = personPensionIncome.filter { it.isDigit() }.toLongOrNull() ?: 0L
                        )
                        val index = people.indexOfFirst { it.id == saved.id }
                        if (index >= 0) people[index] = saved else people.add(saved)
                        selectedPersonId = saved.id
                        showPersonDialog = false
                    }
                }) { Text("저장", color = BrandGreen, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showPersonDialog = false }) { Text("취소", color = TextSecondary) }
            }
        )
    }

    deletePerson?.let { person ->
        AlertDialog(
            onDismissRequest = { deletePerson = null },
            containerColor = PanelColor,
            title = { Text("인원을 삭제할까요?", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = {
                Text(
                    if (people.size <= 1) "배당 계산에는 최소 1명의 인원이 필요합니다."
                    else "${person.name} 인원의 세금 설정을 삭제합니다.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                TextButton(
                    enabled = people.size > 1,
                    onClick = {
                        val removedSelectedPerson = selectedPersonId == person.id
                        people.removeAll { it.id == person.id }
                        if (removedSelectedPerson) {
                            selectedPersonId = people.firstOrNull()?.id ?: defaultPeople.first().id
                        }
                        if (people.isEmpty()) people.addAll(defaultPeople)
                        deletePerson = null
                    }
                ) {
                    Text(
                        "삭제",
                        color = if (people.size > 1) PositiveRed else TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { deletePerson = null }) { Text("취소", color = TextSecondary) }
            }
        )
    }

    if (showSaveDialog) {
        SimulationPresetSaveDialog(
            title = "배당 시뮬레이션 저장",
            name = presetName,
            existingNames = savedPresets.map { it.name },
            onNameChange = { presetName = it },
            onDismiss = { showSaveDialog = false },
            onSaveNew = ::saveNamedPreset,
            onOverwrite = ::saveNamedPreset
        )
    }

    if (showLoadDialog) {
        AlertDialog(
            onDismissRequest = { showLoadDialog = false },
            containerColor = PanelColor,
            title = { Text("배당 시뮬레이션 불러오기", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (savedPresets.isEmpty()) {
                        Text("저장된 배당 시뮬레이션이 없습니다.", color = TextSecondary)
                    } else {
                        savedPresets.forEach { preset ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .combinedClickable(
                                        onClick = {
                                            selected = candidates.firstOrNull { it.ticker.equals(preset.ticker, ignoreCase = true) } ?: candidates.first()
                                            modeIndex = preset.modeIndex
                                            targetInput = preset.targetInput
                                            people.clear()
                                            people.addAll(preset.people.ifEmpty { defaultPeople })
                                            selectedPersonId = preset.selectedPersonId
                                            lastDividendSimulationPresetCache = preset
                                            showLoadDialog = false
                                        },
                                        onLongClick = {
                                            showLoadDialog = false
                                            renamePreset = preset
                                        }
                                    )
                                    .background(SoftSurface)
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(preset.name, color = TextPrimary, fontWeight = FontWeight.ExtraBold)
                                    val projectionLabel = if (preset.projectionRows.isNotEmpty()) " · 20년 전망" else ""
                                    Text(
                                        "${preset.ticker} · ${preset.people.size}명$projectionLabel",
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text("불러오기", color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLoadDialog = false }) { Text("닫기", color = TextSecondary) }
            }
        )
    }

    renamePreset?.let { preset ->
        SimulationPresetRenameDialog(
            currentName = preset.name,
            existingNames = savedPresets.map { it.name },
            onDismiss = { renamePreset = null },
            onRename = { renameSavedPreset(preset, it) },
            onDelete = {
                renamePreset = null
                deletePreset = preset
            }
        )
    }

    deletePreset?.let { preset ->
        AlertDialog(
            onDismissRequest = { deletePreset = null },
            containerColor = PanelColor,
            title = { Text("저장 항목을 삭제할까요?", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = { Text("${preset.name} 배당 시뮬레이션을 삭제합니다.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    savedPresets.removeAll { it.name == preset.name }
                    savedDividendSimulationPresetsCache.removeAll { it.name == preset.name }
                    saveSavedDividendPresets(context, savedDividendSimulationPresetsCache)
                    deletePreset = null
                }) { Text("삭제", color = PositiveRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { deletePreset = null }) { Text("취소", color = TextSecondary) }
            }
        )
    }
}

@Composable
private fun DividendInfoBlock(label: String, value: String) {
    Column {
        Text(label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(5.dp))
        Text(value, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun DividendTargetSettingsCard(
    modeIndex: Int,
    targetInput: String,
    activePeopleCount: Int,
    usdKrw: Double,
    requiredShares: Long,
    requiredCapital: Long,
    expectedMonthlyDividend: Long,
    expectedAnnualDividend: Long,
    onModeChange: (Int) -> Unit,
    onTargetInputChange: (String) -> Unit
) {
    AnalysisCard {
        Text("목표 설정", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(14.dp))
        SegmentedLabels(listOf("월 배당금", "연 배당금", "투자금"), selectedIndex = modeIndex, onSelect = onModeChange)
        Spacer(modifier = Modifier.height(16.dp))
        BacktestInputRow(
            label = when (modeIndex) {
                0 -> "목표 월 배당금"
                1 -> "목표 연 배당금"
                else -> "투자금"
            },
            value = targetInput,
            suffix = "원",
            onValueChange = onTargetInputChange
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "세후 자동 계산 · ${activePeopleCount}명 기준 · 환율 ${formatDecimal(usdKrw)}원",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                DividendTargetSummaryCard("필요 수량", "${NumberFormat.getNumberInstance(Locale.KOREA).format(requiredShares)}주", "목표 기준")
            }
            Box(modifier = Modifier.weight(1f)) {
                DividendTargetSummaryCard("총 투자금", formatWon(requiredCapital), "현재가 기준")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Box(modifier = Modifier.weight(1f)) {
                DividendTargetSummaryCard("예상 월 배당금", formatWon(expectedMonthlyDividend), "세후 추정")
            }
            Box(modifier = Modifier.weight(1f)) {
                DividendTargetSummaryCard("예상 연 배당금", formatWon(expectedAnnualDividend), "세후 추정")
            }
        }
    }
}

@Composable
private fun DividendTargetSummaryCard(title: String, value: String, caption: String) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SoftSurface),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(14.dp)) {
            Text(title, color = TextSecondary, fontSize = 12.sp, maxLines = 1)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                value,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(caption, color = TextSecondary, fontSize = 12.sp, maxLines = 1)
        }
    }
}

@Composable
private fun DividendGrowthProjectionTable(rows: List<DividendGrowthProjectionRow>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("20년 월 평균 배당금", color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
        Text("목표 설정 기준으로 배당 성장률과 가격 성장률을 누적 적용합니다.", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        if (rows.isEmpty()) {
            Text("목표 금액을 입력하면 1년부터 20년까지 예상 경로를 보여줍니다.", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            return
        }
        DividendGrowthProjectionHeader()
        rows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("${row.year}년", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(52.dp))
                Text(formatWon(row.monthlyDividend), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                Spacer(modifier = Modifier.width(12.dp))
                Text(formatWon(row.totalAsset), color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
            }
        }
    }
}

@Composable
private fun DividendGrowthProjectionHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SoftSurface)
            .padding(horizontal = 0.dp, vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("연차", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(52.dp), textAlign = TextAlign.Center)
        Text("월 평균 배당금", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        Spacer(modifier = Modifier.width(12.dp))
        Text("총 자산", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}

@Composable
private fun DividendPriceChart(candidate: DividendEtfUi, points: List<Pair<LocalDate, Double>>) {
    var selectedIndex by remember(candidate.ticker, points.size) { mutableStateOf<Int?>(null) }

    if (points.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(226.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SoftSurface)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "다운로드된 가격 데이터가 없습니다.",
                color = TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        return
    }

    val values = points.map { it.second }
    val labels = points.map { dividendChartDateLabel(it.first) }
    val up = values.last() >= values.first()
    Column {
        Text(
            "${dividendChartFullDate(points.first().first)} ~ ${dividendChartFullDate(points.last().first)}",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(246.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SoftSurface)
                .padding(8.dp)
                .pointerInput(values) {
                    detectTapGestures { offset ->
                        selectedIndex = backtestChartIndexForOffset(
                            x = offset.x,
                            width = size.width.toFloat(),
                            count = values.size
                        )
                    }
                }
        ) {
            DetailedBacktestLineChart(
                values = values,
                labels = labels,
                color = if (up) PositiveRed else NegativeBlue,
                yAxisLabel = { dividendPriceLabel(candidate, it) },
                chartType = BacktestChartType.PRICE,
                selectedIndex = selectedIndex,
                valueLabel = { dividendPriceLabel(candidate, it) },
                detailedAxes = true,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun DividendGrowthChart(
    candidate: DividendEtfUi,
    targetMonthlyDividend: Long,
    points: List<Pair<LocalDate, Double>>
) {
    var selectedIndex by remember(candidate.ticker, targetMonthlyDividend, points.size) { mutableStateOf<Int?>(null) }

    if (targetMonthlyDividend <= 0L) {
        DividendEmptyChart("목표 월 배당액을 입력하면 배당 성장 흐름을 보여줍니다.")
        return
    }

    if (points.isEmpty()) {
        DividendEmptyChart("다운로드된 배당 데이터가 부족합니다.")
        return
    }

    val values = points.map { it.second }
    val labels = points.map { dividendChartDateLabel(it.first) }
    val up = values.last() >= values.first()
    Column {
        Text(
            "${dividendChartFullDate(points.first().first)} ~ ${dividendChartFullDate(points.last().first)}",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(246.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(SoftSurface)
                .padding(8.dp)
                .pointerInput(values) {
                    detectTapGestures { offset ->
                        selectedIndex = backtestChartIndexForOffset(
                            x = offset.x,
                            width = size.width.toFloat(),
                            count = values.size
                        )
                    }
                }
        ) {
            DetailedBacktestLineChart(
                values = values,
                labels = labels,
                color = if (up) PositiveRed else NegativeBlue,
                yAxisLabel = { formatWon(it.roundToLong()) },
                chartType = BacktestChartType.PRICE,
                selectedIndex = selectedIndex,
                valueLabel = { formatWon(it.roundToLong()) },
                detailedAxes = true,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

@Composable
private fun DividendEmptyChart(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(226.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(SoftSurface)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            message,
            color = TextSecondary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun sampleDividendPricePoints(points: List<Pair<LocalDate, Double>>, maxPoints: Int): List<Pair<LocalDate, Double>> {
    if (points.size <= maxPoints) return points
    val step = (points.lastIndex).toDouble() / (maxPoints - 1).coerceAtLeast(1)
    return (0 until maxPoints)
        .map { index -> points[(index * step).roundToInt().coerceIn(0, points.lastIndex)] }
        .distinctBy { it.first }
}

private fun dividendPriceChartPoints(
    context: Context,
    candidate: DividendEtfUi
): List<Pair<LocalDate, Double>> = sampleDividendPricePoints(
    loadHistoricalSeries(context, candidate.ticker)
        .mapNotNull { point ->
            parseAppDate(point.date)?.let { date -> date to point.close }
        }
        .sortedBy { it.first },
    maxPoints = 260
)

private fun dividendGrowthChartPoints(
    context: Context,
    candidate: DividendEtfUi,
    targetMonthlyDividend: Long,
    people: List<DividendPersonUi>,
    usdKrw: Double
): List<Pair<LocalDate, Double>> {
    if (targetMonthlyDividend <= 0L) return emptyList()
    val payments = loadDividendPaymentSeries(context, candidate.ticker)
        .mapNotNull { point ->
            parseAppDate(point.date)?.let { date -> date to point.amount }
        }
        .filter { (_, amount) -> amount > 0.0 }
        .sortedBy { it.first }
    if (payments.size < 2) return emptyList()

    val firstDate = payments.first().first
    val lastDate = payments.last().first
    val firstComparableMonth = if (firstDate.plusYears(1).isAfter(lastDate)) {
        firstDate.withDayOfMonth(1)
    } else {
        firstDate.plusYears(1).withDayOfMonth(1)
    }
    fun effectiveDateForMonth(month: LocalDate): LocalDate {
        val monthEnd = month.plusMonths(1).minusDays(1)
        return if (monthEnd.isAfter(lastDate)) lastDate else monthEnd
    }
    val currencyMultiplier = if (candidate.currency == "USD") usdKrw else 1.0
    val baselineEffectiveDate = effectiveDateForMonth(firstComparableMonth)
    val baselineDividend = trailingDividendAmount(payments, baselineEffectiveDate)
        .takeIf { it > 0.0 }
        ?: payments.first().second
    val baselineGrossAnnualPerShare = baselineDividend * currencyMultiplier
    if (baselineGrossAnnualPerShare <= 0.0) return emptyList()

    val targetAnnualDividend = targetMonthlyDividend.coerceAtMost(Long.MAX_VALUE / 12) * 12
    val chartShares = requiredDividendShares(
        grossAnnualDividendPerShare = baselineGrossAnnualPerShare,
        targetAnnualDividendWon = targetAnnualDividend,
        people = people,
        withholdingTaxRate = dividendWithholdingTaxRate(candidate)
    ).coerceAtLeast(0L)
    if (chartShares <= 0L) return emptyList()

    val rawPoints = mutableListOf<Pair<LocalDate, Double>>()
    var cursor = firstComparableMonth
    val endMonth = lastDate.withDayOfMonth(1)
    while (!cursor.isAfter(endMonth)) {
        val effectiveDate = effectiveDateForMonth(cursor)
            val trailingDividend = trailingDividendAmount(payments, effectiveDate)
        if (trailingDividend > 0.0) {
            val grossAnnualDividend = chartShares * trailingDividend * currencyMultiplier
            val netMonthlyDividend =
                dividendAfterTaxAnnualWon(grossAnnualDividend, people, dividendWithholdingTaxRate(candidate)) / 12.0
            rawPoints += cursor to netMonthlyDividend
        }
        cursor = cursor.plusMonths(1)
    }
    return sampleDividendPricePoints(rawPoints, maxPoints = 260)
}

private fun dividendGrowthProjectionRows(
    grossAnnualDividend: Double,
    initialAsset: Long,
    dividendGrowthRate: Double,
    priceGrowthRate: Double,
    people: List<DividendPersonUi>,
    withholdingTaxRate: Double
): List<DividendGrowthProjectionRow> {
    return DividendProjectionEngine.calculate(
        input = DividendProjectionInput(
            grossAnnualDividendWon = grossAnnualDividend,
            initialAssetWon = initialAsset,
            dividendGrowthRatePercent = dividendGrowthRate,
            priceGrowthRatePercent = priceGrowthRate,
            people = people.map(DividendPersonUi::toDividendTaxPerson),
            withholdingTaxRate = withholdingTaxRate
        )
    )
}

private fun dividendPriceLabel(candidate: DividendEtfUi, value: Double): String =
    if (candidate.currency == "USD") "$${formatDecimal(value)}" else formatWon(value.roundToLong())

private fun dividendChartDateLabel(date: LocalDate): String =
    "${date.year}.${date.monthValue.toString().padStart(2, '0')}"

private fun dividendChartFullDate(date: LocalDate): String =
    "${date.year}.${date.monthValue.toString().padStart(2, '0')}.${date.dayOfMonth.toString().padStart(2, '0')}"

private fun dividendGrowthMetric(context: Context, candidate: DividendEtfUi): DividendMetricUi {
    val availableYears = dividendPaymentAvailableYears(context, candidate.ticker, 5)
    val labelYears = when {
        availableYears >= 2 -> availableYears
        candidate.dividendGrowth5y != 0.0 -> 5
        else -> 0
    }
    val value = if (availableYears >= 2) {
        dividendAnnualizedDividendGrowth(context, candidate, labelYears)
    } else {
        null
    } ?: candidate.dividendGrowth5y.takeIf { it != 0.0 }
    return DividendMetricUi(
        label = if (labelYears >= 2) "${labelYears}년 평균 배당성장율" else "평균 배당성장율",
        value = value?.let { "${formatDecimal(it)}%" } ?: "분배금 데이터 부족",
        numericValue = value
    )
}

private fun dividendPriceGrowthMetric(context: Context, candidate: DividendEtfUi): DividendMetricUi {
    val availableYears = dividendAvailableYears(context, candidate.ticker, 10)
    val labelYears = when {
        availableYears >= 2 -> availableYears
        candidate.priceGrowth10y != 0.0 -> 10
        else -> 0
    }
    val value = if (availableYears >= 2) {
        dividendAnnualizedPriceGrowth(context, candidate.ticker, labelYears)
    } else {
        null
    } ?: candidate.priceGrowth10y.takeIf { it != 0.0 }
    return DividendMetricUi(
        label = if (labelYears >= 2) "${labelYears}년 평균 주가상승율" else "평균 주가상승율",
        value = value?.let { "${formatDecimal(it)}%" } ?: "2년 미만",
        numericValue = value
    )
}

private fun dividendAvailableYears(context: Context, ticker: String, maxYears: Int): Int {
    val dates = loadHistoricalSeries(context, ticker)
        .mapNotNull { parseAppDate(it.date) }
        .sorted()
    if (dates.size < 2) return 0
    return (ChronoUnit.DAYS.between(dates.first(), dates.last()) / 365.25)
        .toInt()
        .coerceIn(0, maxYears)
}

private fun dividendPaymentAvailableYears(context: Context, ticker: String, maxYears: Int): Int {
    val dates = loadDividendPaymentSeries(context, ticker)
        .mapNotNull { parseAppDate(it.date) }
        .sorted()
    if (dates.size < 2) return 0
    return (ChronoUnit.DAYS.between(dates.first(), dates.last()) / 365.25)
        .toInt()
        .coerceIn(0, maxYears)
}

private fun dividendAnnualizedPriceGrowth(context: Context, ticker: String, years: Int): Double? {
    val series = loadHistoricalSeries(context, ticker)
        .mapNotNull { point ->
            parseAppDate(point.date)?.let { date -> date to point.close }
        }
        .filter { it.second > 0.0 }
        .sortedBy { it.first }
    if (series.size < 2) return null
    val end = series.last()
    val desiredStart = end.first.minusYears(years.toLong())
    val start = series.firstOrNull { !it.first.isBefore(desiredStart) } ?: series.first()
    val actualYears = ChronoUnit.DAYS.between(start.first, end.first) / 365.25
    if (actualYears < 1.0 || start.second <= 0.0) return null
    return ((end.second / start.second).pow(1.0 / actualYears) - 1.0) * 100.0
}

private fun dividendAnnualizedDividendGrowth(context: Context, candidate: DividendEtfUi, years: Int): Double? {
    val payments = loadDividendPaymentSeries(context, candidate.ticker)
        .mapNotNull { point ->
            parseAppDate(point.date)?.let { date -> date to point.amount }
        }
        .filter { it.second > 0.0 }
        .sortedBy { it.first }
    if (payments.size < 2) return null

    val firstPaymentDate = payments.first().first
    val endDate = payments.last().first
    val desiredStartEndDate = endDate.minusYears(years.toLong())
    val firstComparableEndDate = firstPaymentDate.plusYears(1)
    val startEndDate = if (desiredStartEndDate.isBefore(firstComparableEndDate)) {
        firstComparableEndDate
    } else {
        desiredStartEndDate
    }
    val actualYears = ChronoUnit.DAYS.between(startEndDate, endDate) / 365.25
    if (actualYears < 1.0) return null

    val startDividend = trailingDividendAmount(payments, startEndDate)
    val endDividend = trailingDividendAmount(payments, endDate)
    if (startDividend <= 0.0 || endDividend <= 0.0) return null
    return ((endDividend / startDividend).pow(1.0 / actualYears) - 1.0) * 100.0
}

private fun trailingDividendAmount(payments: List<Pair<LocalDate, Double>>, endDate: LocalDate): Double {
    val startExclusive = endDate.minusYears(1)
    return payments
        .filter { (date, _) -> date.isAfter(startExclusive) && !date.isAfter(endDate) }
        .sumOf { it.second }
}

private fun dividendCandidates(): List<DividendEtfUi> = listOf(
    DividendEtfUi("SCHD", "Schwab U.S. Dividend Equity ETF", 3.5, 11.2, 9.4, "분기배당", "USD", 78.50, Color(0xFF2459D8)),
    DividendEtfUi("JEPI", "JPMorgan Equity Premium Income ETF", 7.5, 4.0, 5.8, "월배당", "USD", 56.20, Color(0xFF7A4DD8)),
    DividendEtfUi("JEPQ", "JPMorgan Nasdaq Equity Premium Income ETF", 9.0, 5.3, 8.6, "월배당", "USD", 54.80, Color(0xFF5467F5)),
    DividendEtfUi("QQQ", "Invesco QQQ Trust", 0.6, 5.0, 15.0, "분기배당", "USD", 520.0, Color(0xFF2F55FF)),
    DividendEtfUi("SPY", "SPDR S&P 500 ETF Trust", 1.2, 5.8, 10.2, "분기배당", "USD", 620.0, Color(0xFF4F7DAE)),
    DividendEtfUi("VOO", "Vanguard S&P 500 ETF", 1.2, 6.0, 10.1, "분기배당", "USD", 570.0, Color(0xFFB75C2D)),
    DividendEtfUi("QYLD", "Global X Nasdaq 100 Covered Call ETF", 11.5, 1.6, 2.5, "월배당", "USD", 17.80, Color(0xFF1AA8D0)),
    DividendEtfUi("QQQI", "NEOS Nasdaq-100 High Income ETF", 14.0, 0.0, 0.0, "월배당", "USD", 50.0, Color(0xFF4C6FFF)),
    DividendEtfUi("SPYI", "NEOS S&P 500 High Income ETF", 11.8, 0.0, 0.0, "월배당", "USD", 50.0, Color(0xFF2C7BE5))
)

private fun dividendPriceKrw(candidate: DividendEtfUi, usdKrw: Double): Double =
    if (candidate.currency == "USD") candidate.fallbackPrice * usdKrw else candidate.fallbackPrice

@Composable
private fun TrendAnalysisContent(accounts: List<AccountUi>) {
    var selectedRange by remember { mutableIntStateOf(0) }
    val holdings = portfolioHoldings(accounts)
    val totalAmount = holdings.sumOf { it.amount }
    val principal = holdings.sumOf { it.principal }
    val totalProfit = totalAmount - principal
    val totalRate = if (principal == 0L) 0.0 else totalProfit.toDouble() / principal
    val fxProfit = (totalProfit * 0.17).roundToLong()
    val assetProfit = totalProfit - fxProfit

    Text("투자 자산", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
    Spacer(modifier = Modifier.height(10.dp))
    Text(formatWon(totalAmount), color = TextPrimary, fontSize = 38.sp, fontWeight = FontWeight.ExtraBold)
    Spacer(modifier = Modifier.height(12.dp))
    Text("원금 ${formatWon(principal)}", color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(28.dp))
    TrendAssetChart(holdings, selectedRange)
    Spacer(modifier = Modifier.height(20.dp))
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        listOf("올해", "이달", "1달", "6달", "1년", "5년", "10년", "15년", "20년").forEachIndexed { index, label ->
            Text(
                label,
                color = if (selectedRange == index) Color.White else TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                modifier = (if (selectedRange == index) Modifier.clip(CircleShape).background(PositiveRed) else Modifier)
                    .clickable { selectedRange = index }
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(20.dp))
    Box(modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(SoftSurface).padding(18.dp)) {
        Text("그래프를 눌러 더 자세한 자산 변화 추이를 확인하세요.", color = TextSecondary, fontSize = 16.sp)
    }
    Spacer(modifier = Modifier.height(34.dp))
    DividerLine()
    Spacer(modifier = Modifier.height(28.dp))
    Text("수익", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
    Spacer(modifier = Modifier.height(14.dp))
    Text("자산 수익은 내 평단가와 현재가의 차이로 계산한 수익이에요.", color = TextSecondary, fontSize = 16.sp, lineHeight = 23.sp)
    Spacer(modifier = Modifier.height(22.dp))
    MetricRow("자산 수익", "${formatSignedWon(assetProfit)} (${formatPercent(if (principal == 0L) 0.0 else assetProfit.toDouble() / principal)})")
    MetricRow("+ 환차익", "${formatSignedWon(fxProfit)} (${formatPercent(if (principal == 0L) 0.0 else fxProfit.toDouble() / principal)})")
    DividerLine()
    MetricRow("총 수익", "${formatSignedWon(totalProfit)} (${formatPercent(totalRate)})")
}

@Composable
private fun AllocationAnalysisContent(accounts: List<AccountUi>) {
    var selectedMode by remember { mutableIntStateOf(0) }
    val holdings = portfolioHoldings(accounts).sortedByDescending { it.amount }
    val accountItems = accounts.sortedByDescending { it.totalAmount }
    val total = if (selectedMode == 0) holdings.sumOf { it.amount }.coerceAtLeast(1L) else accountItems.sumOf { it.totalAmount }.coerceAtLeast(1L)
    val colors = listOf(BrokerBlue, Color(0xFF58A9F4), Color(0xFFA6D4F8), Color(0xFFDDE5EE), CashOrange, NasdaqBlue)
    val topHolding = holdings.firstOrNull()
    val topLabel = if (selectedMode == 0) topHolding?.let { assetDisplayName(it) } else accountItems.firstOrNull()?.name
    val topAmount = if (selectedMode == 0) holdings.firstOrNull()?.amount ?: 0L else accountItems.firstOrNull()?.totalAmount ?: 0L
    val topColor = if (selectedMode == 0) holdings.firstOrNull()?.color ?: BrokerBlue else accountItems.firstOrNull()?.color ?: BrokerBlue
    val topPercent = topAmount.toDouble() / total

    SegmentedLabels(listOf("종목별", "계좌별"), selectedIndex = selectedMode, onSelect = { selectedMode = it })
    Spacer(modifier = Modifier.height(28.dp))
    if (topLabel != null) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AssetBadge(if (selectedMode == 0) topHolding?.ticker?.take(1).orEmpty() else topLabel.take(1), topColor)
            Spacer(modifier = Modifier.width(12.dp))
            Text(topLabel, color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.height(18.dp))
        Text("${formatDecimal(topPercent * 100)}%", color = TextPrimary, fontSize = 40.sp, fontWeight = FontWeight.ExtraBold)
        Text("평가액 ${formatWon(topAmount)}", color = TextSecondary, fontSize = 18.sp)
    }
    Spacer(modifier = Modifier.height(24.dp))
    DonutChart(
        if (selectedMode == 0) {
            holdings.mapIndexed { index, holding -> holding.amount.toFloat() to colors[index % colors.size] }
        } else {
            accountItems.mapIndexed { index, account -> account.totalAmount.toFloat() to colors[index % colors.size] }
        }
    )
    Spacer(modifier = Modifier.height(28.dp))
    if (selectedMode == 0) {
        holdings.forEachIndexed { index, holding ->
            AllocationRow(colors[index % colors.size], assetDisplayName(holding), holding.amount.toDouble() / total)
        }
    } else {
        accountItems.forEachIndexed { index, account ->
            AllocationRow(colors[index % colors.size], account.name, account.totalAmount.toDouble() / total)
        }
    }
}

@Composable
private fun SegmentedLabels(labels: List<String>, selectedIndex: Int, onSelect: (Int) -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(999.dp))
            .background(SoftSurface)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        labels.forEachIndexed { index, label ->
            Text(
                label,
                color = if (index == selectedIndex) TextPrimary else TextSecondary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (index == selectedIndex) PanelColor else Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(vertical = 9.dp)
            )
        }
    }
}

@Composable
private fun ProfitBarLineChart() {
    Canvas(modifier = Modifier.fillMaxWidth().height(190.dp).padding(top = 20.dp)) {
        val left = 28f
        val right = size.width - 18f
        val top = 18f
        val bottom = size.height - 30f
        val values = listOf(-2f, -1.5f, 3f, -4f, -12f)
        val maxAbs = 14f
        val zeroY = top + (bottom - top) * 0.46f

        listOf(top, zeroY, bottom).forEach { y ->
            drawLine(Color(0xFFE6EAF0), Offset(left, y), Offset(right, y), strokeWidth = 1.5f)
        }

        values.forEachIndexed { index, value ->
            val x = left + (right - left) * (index + 1) / 12f
            val barHeight = (kotlin.math.abs(value) / maxAbs) * (bottom - top) * 0.5f
            val y = if (value >= 0) zeroY - barHeight else zeroY
            drawRoundRect(
                color = Color(0xFFE8ECF2),
                topLeft = Offset(x - 10f, y),
                size = androidx.compose.ui.geometry.Size(20f, barHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
            )
        }

        var prev: Offset? = null
        values.forEachIndexed { index, value ->
            val x = left + (right - left) * (index + 1) / 12f
            val y = zeroY - (value / maxAbs) * (bottom - top) * 0.5f
            val point = Offset(x, y)
            prev?.let { drawLine(NegativeBlue, it, point, strokeWidth = 4f, cap = StrokeCap.Round) }
            prev = point
        }
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        (1..12).forEach { month ->
            Text("$month", color = TextSecondary, fontSize = 11.sp)
        }
    }
}

@Composable
private fun TrendAssetChart(holdings: List<HoldingUi>, selectedRange: Int) {
    val totalAmount = holdings.sumOf { it.amount }
    val trades = holdings.flatMap { holding ->
        holding.trades.filter { it.side == TradeSide.BUY }.mapNotNull { trade ->
            parseAppDate(trade.date)?.let { date ->
                date to (trade.quantity * trade.price * trade.exchangeRate).roundToLong()
            }
        }
    }.sortedBy { it.first }
    val rangeStart = trendRangeStart(selectedRange)
    val visibleTrades = trades.filter { it.first >= rangeStart }
    val chartTrades = if (visibleTrades.isEmpty() && trades.isNotEmpty()) listOf(trades.first()) else visibleTrades
    val startLabel = chartTrades.firstOrNull()?.first?.let { "${it.monthValue}/${it.dayOfMonth}" } ?: "-"
    val endDate = LocalDate.now()

    Canvas(modifier = Modifier.fillMaxWidth().height(260.dp)) {
        val left = 4f
        val right = size.width - 8f
        val top = 24f
        val bottom = size.height - 34f
        val principalPointsRaw = chartTrades.runningFold(0L) { acc, item -> acc + item.second }.drop(1).ifEmpty { listOf(0L) }
        val maxValue = maxOf(totalAmount, principalPointsRaw.maxOrNull() ?: 0L, 1L).toFloat()

        drawLine(Color(0xFFE3E7EC), Offset(left, bottom), Offset(right, bottom), strokeWidth = 1.5f)
        drawLine(Color(0xFFE3E7EC), Offset(left, top), Offset(right, top), strokeWidth = 1.5f)

        fun yFor(value: Long): Float = bottom - (bottom - top) * (value.toFloat() / maxValue).coerceIn(0f, 1f)

        fun drawSeries(points: List<Long>, color: Color, stroke: Float) {
            var prev: Offset? = null
            points.forEachIndexed { index, value ->
                val denominator = (points.size - 1).coerceAtLeast(1)
                val x = left + (right - left) * index / denominator
                val y = yFor(value)
                val point = Offset(x, y)
                prev?.let { drawLine(color, it, point, strokeWidth = stroke, cap = StrokeCap.Round) }
                prev = point
            }
        }

        drawSeries(principalPointsRaw, Color(0xFFC7CED8), 5f)
        val assetPoints = if (principalPointsRaw.size <= 1) listOf(principalPointsRaw.firstOrNull() ?: 0L, totalAmount) else principalPointsRaw.dropLast(1) + totalAmount
        drawSeries(assetPoints, PositiveRed, 5f)
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(startLabel, color = TextSecondary, fontSize = 11.sp)
        Text("${endDate.monthValue}/${endDate.dayOfMonth}", color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
private fun DonutChart(parts: List<Pair<Float, Color>>) {
    val total = parts.sumOf { it.first.toDouble() }.toFloat().coerceAtLeast(1f)
    Canvas(modifier = Modifier.fillMaxWidth().height(250.dp)) {
        val diameter = size.minDimension * 0.78f
        val left = (size.width - diameter) / 2f
        val top = (size.height - diameter) / 2f
        var startAngle = -90f
        parts.forEach { (value, color) ->
            val sweep = 360f * value / total
            drawArc(
                color = color,
                startAngle = startAngle,
                sweepAngle = sweep,
                useCenter = false,
                topLeft = Offset(left, top),
                size = androidx.compose.ui.geometry.Size(diameter, diameter),
                style = androidx.compose.ui.graphics.drawscope.Stroke(width = diameter * 0.27f)
            )
            startAngle += sweep
        }
    }
}

@Composable
private fun AllocationRow(color: Color, label: String, percent: Double) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(15.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(14.dp))
        Text(label, color = TextPrimary, fontSize = 19.sp, modifier = Modifier.weight(1f))
        Text("${formatDecimal(percent * 100)}%", color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun AccountDetailScreen(
    account: AccountUi?,
    onBack: () -> Unit,
    onOpenMenu: () -> Unit,
    onManualAdd: () -> Unit,
    investmentMode: String,
    profitMode: String,
    onInvestmentModeChange: (String) -> Unit,
    onProfitModeChange: (String) -> Unit,
    onSortClick: () -> Unit,
    onHoldingClick: (HoldingUi) -> Unit,
    onHoldingLongClick: (HoldingUi) -> Unit
) {
    if (account == null) {
        EmptyAccountsScreen(onBack = onBack)
        return
    }

    ScreenColumn(topPadding = 66) {
        AccountDetailHeader(account, onBack, onOpenMenu)
        BigAssetAmount(
            amount = account.totalAmount,
            profit = account.dayProfit,
            rate = if (account.totalAmount == 0L) 0.0 else account.dayProfit.toDouble() / account.totalAmount,
            label = "일간 수익"
        )
        Spacer(modifier = Modifier.height(34.dp))
        SectionTitle("투자")
        InvestmentToolbar(
            investmentMode = investmentMode,
            profitMode = profitMode,
            sortLabel = sortLabel(account.sortField),
            onInvestmentModeChange = onInvestmentModeChange,
            onProfitModeChange = onProfitModeChange,
            onSortClick = onSortClick
        )
        Spacer(modifier = Modifier.height(18.dp))

        if (account.holdings.isEmpty()) {
            EmptyAccount()
        } else {
            sortHoldings(account.holdings, account.sortField, account.sortDescending)
                .forEach {
                    InvestmentHoldingRow(
                        holding = it,
                        investmentMode = investmentMode,
                        profitMode = profitMode,
                        onClick = { onHoldingClick(it) },
                        onLongClick = { onHoldingLongClick(it) }
                    )
                }
        }

        Spacer(modifier = Modifier.height(34.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            SmallActionButton("+ 수동 추가", onClick = onManualAdd)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AccountManageScreen(
    accounts: List<AccountUi>,
    onBack: () -> Unit,
    onSave: () -> Unit,
    onDeleteManualClick: () -> Unit,
    onRename: (Int, String) -> Unit
) {
    var renameAccount by remember { mutableStateOf<AccountUi?>(null) }
    var renameText by remember { mutableStateOf("") }

    ScreenColumn(topPadding = 66) {
        PlainTopBar(title = "계좌 편집", onBack = onBack, rightText = "저장", onRightClick = onSave)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onDeleteManualClick)
                .padding(vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("계좌 삭제", color = TextPrimary, fontSize = 18.sp, modifier = Modifier.weight(1f))
            Text("›", color = TextSecondary, fontSize = 28.sp)
        }
        DividerLine()
        Spacer(modifier = Modifier.height(22.dp))
        Text("증권", color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(22.dp))
        accounts.forEach { account ->
            AccountManageRow(
                account = account,
                onLongClick = {
                    renameAccount = account
                    renameText = account.name
                }
            )
        }
    }

    renameAccount?.let { account ->
        AlertDialog(
            onDismissRequest = { renameAccount = null },
            containerColor = PanelColor,
            title = { Text("계좌명 변경", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = {
                OutlinedTextField(
                    value = renameText,
                    onValueChange = { renameText = it },
                    singleLine = true,
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(18.dp)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    val nextName = renameText.trim()
                    if (nextName.isNotBlank()) onRename(account.id, nextName)
                    renameAccount = null
                }) { Text("저장", color = BrandGreen, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { renameAccount = null }) { Text("취소", color = TextSecondary) }
            }
        )
    }
}

@Composable
private fun DeleteManualAccountsScreen(
    accounts: List<AccountUi>,
    selectedIds: MutableList<Int>,
    onBack: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(AppBackground)) {
        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(start = 24.dp, top = 76.dp, end = 24.dp, bottom = 54.dp)
            ) {
                PlainTopBar(title = "계좌 삭제", onBack = onBack, rightText = "저장", onRightClick = onDeleteClick)
                Spacer(modifier = Modifier.height(22.dp))
                accounts.forEach { account ->
                    SelectableAccountRow(
                        account = account,
                        selected = account.id in selectedIds,
                        onClick = {
                            if (account.id in selectedIds) selectedIds.remove(account.id) else selectedIds.add(account.id)
                        }
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(104.dp)
                    .background(PanelColor)
                    .padding(start = 36.dp, top = 12.dp, end = 36.dp, bottom = 26.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val allSelected = accounts.isNotEmpty() && selectedIds.size == accounts.size
                Text(
                    text = if (selectedIds.isEmpty()) "전체선택" else "${selectedIds.size} 선택해제",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        if (allSelected || selectedIds.isNotEmpty()) selectedIds.clear() else selectedIds.addAll(accounts.map { it.id })
                    }
                )
                Text(
                    text = "삭제",
                    color = if (selectedIds.isEmpty()) Color(0xFFF3A7B5) else PositiveRed,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(enabled = selectedIds.isNotEmpty(), onClick = onDeleteClick)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddAccountScreen(
    onBack: () -> Unit,
    onAdd: (String, String, AccountIconOption) -> Unit
) {
    var accountName by remember { mutableStateOf("") }
    var brokerName by remember { mutableStateOf("삼성증권") }
    var selectedIcon by remember { mutableStateOf(defaultAccountIcon) }
    var showIconPicker by remember { mutableStateOf(false) }
    val canAdd = accountName.isNotBlank()

    ScreenColumn(topPadding = 66) {
        PlainTopBar(title = "계좌 추가", onBack = onBack, rightText = "저장", onRightClick = {
            if (canAdd) onAdd(accountName.trim(), brokerName.trim().ifBlank { "증권" }, selectedIcon)
        })
        Text("계좌 이름", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = accountName,
            onValueChange = { accountName = it },
            singleLine = true,
            placeholder = { Text("예: 진영 직투") },
            shape = RoundedCornerShape(18.dp),
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(22.dp))
        Text("증권사", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        OutlinedTextField(
            value = brokerName,
            onValueChange = { brokerName = it },
            singleLine = true,
            placeholder = { Text("예: 삼성증권") },
            shape = RoundedCornerShape(18.dp),
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(22.dp))
        Text("계좌 이미지", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .background(SoftSurface)
                .clickable { showIconPicker = true }
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BrokerBadge(selectedIcon.text, selectedIcon.color, size = 52)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(selectedIcon.label, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text("목록에서 아이콘 선택", color = TextSecondary, fontSize = 14.sp)
            }
            Text("변경", color = NegativeBlue, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(34.dp))
        Button(
            onClick = { onAdd(accountName.trim(), brokerName.trim().ifBlank { "증권" }, selectedIcon) },
            enabled = canAdd,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TextPrimary,
                disabledContainerColor = SoftSurface,
                disabledContentColor = TextSecondary
            ),
            modifier = Modifier.fillMaxWidth().height(58.dp)
        ) {
            Text("추가하기", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
    }

    if (showIconPicker) {
        ModalBottomSheet(
            onDismissRequest = { showIconPicker = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = PanelColor,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            AccountIconPickerSheet(
                selected = selectedIcon,
                onSelect = {
                    selectedIcon = it
                    showIconPicker = false
                }
            )
        }
    }
}

@Composable
private fun AccountIconPickerSheet(
    selected: AccountIconOption,
    onSelect: (AccountIconOption) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 22.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(54.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFE8EAED))
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text("계좌 이미지 선택", color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(34.dp))
        Text("로고", color = TextSecondary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        AccountIconGrid(accountLogoOptions, selected, onSelect)
        Spacer(modifier = Modifier.height(24.dp))
        Text("기타", color = TextSecondary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        AccountIconGrid(accountEtcIconOptions, selected, onSelect)
        Spacer(modifier = Modifier.height(30.dp))
    }
}

@Composable
private fun AccountIconGrid(
    options: List<AccountIconOption>,
    selected: AccountIconOption,
    onSelect: (AccountIconOption) -> Unit
) {
    options.chunked(6).forEach { rowOptions ->
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            rowOptions.forEach { option ->
                AccountIconChoice(
                    option = option,
                    selected = option == selected,
                    onClick = { onSelect(option) }
                )
            }
            repeat(6 - rowOptions.size) {
                Spacer(modifier = Modifier.size(50.dp))
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
    }
}

@Composable
private fun AccountIconChoice(
    option: AccountIconOption,
    selected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clip(CircleShape)
            .background(if (selected) Color(0xFFE9F4FF) else Color.Transparent)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(if (selected) 48.dp else 46.dp)
                .clip(CircleShape)
                .background(option.color),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = option.text.take(3),
                color = Color.White,
                fontSize = if (option.text.length >= 3) 11.sp else 15.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsScreen(
    settings: AppSettings,
    protectionSettings: ProtectionSettings,
    accounts: List<AccountUi>,
    onBack: () -> Unit,
    onDisplayModeClick: () -> Unit,
    onCurrencyChange: (String) -> Unit,
    onApiSettingsSave: (AppSettings) -> Unit,
    onProtectionSettingsChange: (ProtectionSettings) -> Unit,
    onRequestQrScan: (((String?) -> Unit) -> Unit),
    onBackupClick: () -> Unit,
    onRestoreClick: () -> Unit
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showCurrencySheet by remember { mutableStateOf(false) }
    var showApiSheet by remember { mutableStateOf(false) }
    var showManualAssetSheet by remember { mutableStateOf(false) }
    var manualAssetVersion by remember { mutableIntStateOf(0) }
    var showDataDownloadSheet by remember { mutableStateOf(false) }
    var bulkDownloadRunning by remember { mutableStateOf(false) }
    var showProtectionSheet by remember { mutableStateOf(false) }
    var showPrincipleDialog by remember { mutableStateOf(false) }
    var qrReplacementAuthorized by remember { mutableStateOf(false) }
    var showRegisteredQr by remember { mutableStateOf(false) }
    val manualAssetCount = remember(manualAssetVersion) { loadCustomAssetOptions(context).size }
    val headerTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 12.dp

    ScreenColumn(topPadding = 0) {
        Spacer(modifier = Modifier.height(headerTopPadding))
        PlainTopBar(title = "설정", onBack = onBack, rightText = "", onRightClick = {})
        Spacer(modifier = Modifier.height(32.dp))

        SettingsSectionTitle("접근 보호")
        SettingsValueRow(
            title = "보호 모드",
            value = protectionModeLabel(protectionSettings.mode),
            onClick = { showProtectionSheet = true }
        )
        SettingsValueRow(
            title = "QR 코드 설정",
            value = if (protectionSettings.hasQr) "설정됨" else "미설정",
            onClick = {
                if (!protectionSettings.hasQr) {
                    onRequestQrScan { content ->
                        if (!content.isNullOrEmpty()) {
                            onProtectionSettingsChange(
                                protectionSettings.copy(
                                    qrHash = hashQrContent(content),
                                    qrContent = content
                                )
                            )
                            Toast.makeText(context, "QR 코드를 등록했어요.", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    showRegisteredQr = true
                }
            }
        )
        SettingsValueRow(
            title = "투자 원칙 문장",
            value = if (protectionSettings.investmentPrinciple.isBlank()) "미입력" else "입력됨",
            onClick = { showPrincipleDialog = true }
        )

        Spacer(modifier = Modifier.height(46.dp))
        SettingsSectionTitle("화면")
        SettingsValueRow(
            title = "화면 모드 설정",
            value = displayModeLabel(settings.displayMode),
            onClick = onDisplayModeClick
        )
        SettingsValueRow(
            title = "통화 설정",
            value = currencyLabel(settings.currency),
            onClick = { showCurrencySheet = true }
        )

        Spacer(modifier = Modifier.height(46.dp))
        SettingsSectionTitle("종목")
        SettingsValueRow(
            title = "수동 추가 종목",
            value = "${manualAssetCount}개 추가됨",
            onClick = { showManualAssetSheet = true }
        )

        Spacer(modifier = Modifier.height(46.dp))
        SettingsSectionTitle("API")
        SettingsValueRow(
            title = "API 키 설정",
            value = "${apiProviderLabel(settings.apiProvider)} · ${apiKeyStatusLabel(settings)}",
            onClick = { showApiSheet = true }
        )
        SettingsActionRow(
            title = "데이터 다운로드",
            actionText = if (bulkDownloadRunning) "다운로드 중" else "데이터 다운로드",
            enabled = !bulkDownloadRunning,
            onClick = {
                scope.launch {
                    bulkDownloadRunning = true
                    val candidates = downloadCandidates(accounts)
                    var storedCount = 0
                    var lastMessage = ""
                    candidates.forEach { candidate ->
                        val result = downloadHistoricalSeries(context, settings, candidate, HistoryInterval.DAILY)
                        if (result.status == DownloadStatus.SUCCESS || result.status == DownloadStatus.INSUFFICIENT_SOURCE) {
                            storedCount += 1
                        }
                        lastMessage = result.message
                    }
                    bulkDownloadRunning = false
                    Toast.makeText(
                        context,
                        if (candidates.isEmpty()) {
                            "다운로드할 종목이 없어요."
                        } else if (storedCount == candidates.size) {
                            "전체 데이터 다운로드를 완료했어요."
                        } else {
                            "일부 데이터만 저장했어요. $storedCount/${candidates.size}개 · $lastMessage"
                        },
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        )

        Spacer(modifier = Modifier.height(46.dp))
        SettingsSectionTitle("백업")
        SettingsActionRow(
            title = "백업 파일 저장",
            actionText = "저장",
            enabled = true,
            onClick = onBackupClick
        )
        SettingsActionRow(
            title = "백업 파일 불러오기",
            actionText = "불러오기",
            enabled = true,
            onClick = onRestoreClick
        )
        Text(
            text = "계좌, 거래내역, 목표설정, 수동 추가 종목, 저장한 시뮬레이션, 다운로드한 가격/배당 데이터와 API 키를 백업합니다. 저장 위치에서 Google Drive를 고르면 앱을 삭제해도 복원할 수 있어요.",
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )

        Spacer(modifier = Modifier.height(46.dp))
        SettingsSectionTitle("서비스")
        SettingsStaticRow(title = "버전정보", value = "2026.06.28")
    }

    if (showCurrencySheet) {
        ModalBottomSheet(
            onDismissRequest = { showCurrencySheet = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = PanelColor,
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            CurrencySettingsSheet(
                selectedCurrency = settings.currency,
                onSelect = {
                    onCurrencyChange(it)
                    showCurrencySheet = false
                }
            )
        }
    }

    if (showApiSheet) {
        ApiKeySettingsSheet(
            settings = settings,
            onDismiss = { showApiSheet = false },
            onSave = {
                onApiSettingsSave(it)
                showApiSheet = false
            }
        )
    }

    if (showProtectionSheet) {
        ProtectionModeSettingsSheet(
            selectedMode = protectionSettings.mode,
            onDismiss = { showProtectionSheet = false },
            onSelect = { targetMode ->
                when {
                    targetMode == protectionSettings.mode -> showProtectionSheet = false
                    !protectionSettings.hasQr -> Toast.makeText(
                        context,
                        "모드를 변경하려면 QR 코드를 먼저 등록해 주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                    targetMode.requiresPrinciple && protectionSettings.investmentPrinciple.isBlank() -> Toast.makeText(
                        context,
                        "중간 보호 모드에 사용할 투자 원칙 문장을 먼저 입력해 주세요.",
                        Toast.LENGTH_LONG
                    ).show()
                    else -> onRequestQrScan { content ->
                        if (protectionSettings.matchesQr(content)) {
                            onProtectionSettingsChange(protectionSettings.copy(mode = targetMode))
                            showProtectionSheet = false
                            Toast.makeText(context, "${protectionModeLabel(targetMode)}로 변경했어요.", Toast.LENGTH_SHORT).show()
                        } else if (content != null) {
                            Toast.makeText(context, "등록된 QR 코드와 일치하지 않습니다.", Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        )
    }

    if (showPrincipleDialog) {
        InvestmentPrincipleDialog(
            initialValue = protectionSettings.investmentPrinciple,
            onDismiss = { showPrincipleDialog = false },
            onSave = { principle ->
                onProtectionSettingsChange(protectionSettings.copy(investmentPrinciple = principle.trim()))
                showPrincipleDialog = false
            }
        )
    }

    if (showRegisteredQr) {
        RegisteredQrDialog(
            qrContent = protectionSettings.qrContent,
            onDismiss = { showRegisteredQr = false },
            onChange = {
                showRegisteredQr = false
                onRequestQrScan { content ->
                    if (protectionSettings.matchesQr(content)) {
                        qrReplacementAuthorized = true
                    } else if (content != null) {
                        Toast.makeText(context, "현재 등록된 QR 코드가 아닙니다.", Toast.LENGTH_LONG).show()
                    }
                }
            }
        )
    }

    if (qrReplacementAuthorized) {
        AlertDialog(
            onDismissRequest = { qrReplacementAuthorized = false },
            containerColor = PanelColor,
            shape = RoundedCornerShape(28.dp),
            title = { Text("새 QR 코드 등록", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = { Text("새롭게 사용할 QR 코드를 스캔해 주세요.", color = TextSecondary, lineHeight = 20.sp) },
            confirmButton = {
                TextButton(
                    onClick = {
                        qrReplacementAuthorized = false
                        onRequestQrScan { content ->
                            if (!content.isNullOrEmpty()) {
                                onProtectionSettingsChange(
                                    protectionSettings.copy(
                                        qrHash = hashQrContent(content),
                                        qrContent = content
                                    )
                                )
                                Toast.makeText(context, "QR 코드를 변경했어요.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                ) { Text("스캔", color = BrandGreen, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { qrReplacementAuthorized = false }) {
                    Text("취소", color = TextSecondary, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showManualAssetSheet) {
        ManualAssetSettingsSheet(
            onDismiss = { showManualAssetSheet = false },
            onChanged = { manualAssetVersion += 1 }
        )
    }

    if (showDataDownloadSheet) {
        DataDownloadSheet(
            accounts = accounts,
            onDismiss = { showDataDownloadSheet = false },
            onDownload = { candidate, interval, onDone ->
                scope.launch {
                    val result = downloadHistoricalSeries(context, settings, candidate, interval)
                    onDone(result)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProtectionModeSettingsSheet(
    selectedMode: ProtectionMode,
    onDismiss: () -> Unit,
    onSelect: (ProtectionMode) -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = PanelColor,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 22.dp, vertical = 8.dp)) {
            Text("보호 모드", color = TextPrimary, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(6.dp))
            Text("모드 변경은 등록된 QR 코드 확인 후 적용됩니다.", color = TextSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(18.dp))
            ProtectionMode.entries.forEach { mode ->
                val selected = mode == selectedMode
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (selected) BrandSoftBlue.copy(alpha = 0.22f) else Color.Transparent)
                        .clickable { onSelect(mode) }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(protectionModeLabel(mode), color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(protectionModeDescription(mode), color = TextSecondary, fontSize = 12.sp, lineHeight = 17.sp)
                    }
                    SmallRadio(selected = selected, selectedColor = BrandGreen)
                }
                Spacer(modifier = Modifier.height(6.dp))
            }
            Spacer(modifier = Modifier.height(22.dp))
        }
    }
}

@Composable
private fun InvestmentPrincipleDialog(
    initialValue: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var principle by remember(initialValue) { mutableStateOf(initialValue) }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelColor,
        shape = RoundedCornerShape(28.dp),
        title = { Text("투자 원칙 문장", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
        text = {
            Column {
                Text("중간 보호 모드 진입 시 동일한 문장을 직접 입력합니다.", color = TextSecondary, fontSize = 13.sp, lineHeight = 19.sp)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = principle,
                    onValueChange = { principle = it.take(200) },
                    placeholder = { Text("예: 가격보다 원칙을 먼저 확인한다") },
                    minLines = 3,
                    maxLines = 5,
                    colors = appTextFieldColors(),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = { onSave(principle) }, enabled = principle.trim().isNotEmpty()) {
                Text("저장", color = if (principle.trim().isNotEmpty()) BrandGreen else MutedText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소", color = TextSecondary, fontWeight = FontWeight.Bold) }
        }
    )
}

@Composable
private fun RegisteredQrDialog(
    qrContent: String,
    onDismiss: () -> Unit,
    onChange: () -> Unit
) {
    val qrImage = remember(qrContent) {
        qrContent.takeIf { it.isNotBlank() }?.let { content ->
            runCatching {
                BarcodeEncoder()
                    .encodeBitmap(content, BarcodeFormat.QR_CODE, 720, 720)
                    .asImageBitmap()
            }.getOrNull()
        }
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelColor,
        shape = RoundedCornerShape(28.dp),
        title = { Text("등록된 QR 코드", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                if (qrImage != null) {
                    Image(
                        bitmap = qrImage,
                        contentDescription = "등록된 보호 모드 QR 코드",
                        modifier = Modifier
                            .size(250.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White)
                            .padding(12.dp)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("이 QR 코드가 보호 모드 인증에 사용됩니다.", color = TextSecondary, fontSize = 12.sp)
                } else {
                    Text(
                        "기존 등록은 해시만 저장되어 QR 이미지를 복원할 수 없습니다. 기존 QR을 확인한 뒤 다시 등록해 주세요.",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 20.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("닫기", color = BrandGreen, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onChange) {
                Text(if (qrContent.isBlank()) "다시 등록" else "변경", color = TextSecondary, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun AppProtectionLockScreen(
    settings: ProtectionSettings,
    dailyUsage: ProtectionDailyUsage,
    lockStartedAt: Long,
    bypassWait: Boolean,
    onUnlock: () -> Unit,
    onOpenSimulator: () -> Unit,
    onRequestQrUnlock: () -> Unit
) {
    BackHandler(enabled = true) {}
    var nowMillis by remember(lockStartedAt) { mutableLongStateOf(System.currentTimeMillis()) }
    var enteredPrinciple by remember(settings.investmentPrinciple) { mutableStateOf("") }
    var principleMismatch by remember { mutableStateOf(false) }
    LaunchedEffect(lockStartedAt, bypassWait) {
        while (true) {
            nowMillis = System.currentTimeMillis()
            delay(1_000L)
        }
    }

    val remainingMillis = if (bypassWait) {
        0L
    } else {
        (lockStartedAt + settings.mode.waitMillis - nowMillis).coerceAtLeast(0L)
    }
    val waitFinished = remainingMillis <= 0L
    val limitReached = dailyUsage.entryCount >= settings.mode.dailyEntryLimit
    val remainingEntries = (settings.mode.dailyEntryLimit - dailyUsage.entryCount).coerceAtLeast(0)

    Box(modifier = Modifier.fillMaxSize().background(AppBackground), contentAlignment = Alignment.Center) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("잠시 멈춤", color = TextPrimary, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(protectionModeLabel(settings.mode), color = BrandGreen, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(34.dp))

            if (limitReached) {
                Text("오늘 진입 종료", color = PositiveRed, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(10.dp))
                Text("자정 이후 다시 진입할 수 있습니다.", color = TextSecondary, fontSize = 14.sp)
            } else {
                Text(
                    if (waitFinished) "대기 완료" else formatProtectionCountdown(remainingMillis),
                    color = if (waitFinished) FourAssetSchdColor else TextPrimary,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    when {
                        bypassWait -> "모드 변경 후 첫 재진입은 대기 시간이 면제됩니다."
                        waitFinished -> "진입 조건을 완료해 주세요."
                        else -> "앱을 닫아도 대기 시간은 계속 흐릅니다."
                    },
                    color = TextSecondary,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(28.dp))

                when (settings.mode) {
                    ProtectionMode.WEAK -> ProtectionUnlockButton(
                        text = "앱 열기",
                        enabled = waitFinished,
                        onClick = onUnlock
                    )
                    ProtectionMode.MEDIUM -> {
                        Text("입력할 투자 원칙", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(7.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(SoftSurface)
                                .padding(horizontal = 14.dp, vertical = 13.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                settings.investmentPrinciple,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                lineHeight = 22.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = enteredPrinciple,
                            onValueChange = {
                                enteredPrinciple = it.take(200)
                                principleMismatch = false
                            },
                            enabled = waitFinished,
                            placeholder = { Text("투자 원칙 문장 직접 입력") },
                            minLines = 2,
                            maxLines = 4,
                            colors = appTextFieldColors(),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        if (principleMismatch) {
                            Spacer(modifier = Modifier.height(7.dp))
                            Text("저장한 투자 원칙 문장과 일치하지 않습니다.", color = PositiveRed, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        ProtectionUnlockButton(
                            text = "문장 확인",
                            enabled = waitFinished && enteredPrinciple.trim().isNotEmpty(),
                            onClick = {
                                if (enteredPrinciple.trim() == settings.investmentPrinciple.trim()) onUnlock()
                                else principleMismatch = true
                            }
                        )
                    }
                    ProtectionMode.STRONG -> ProtectionUnlockButton(
                        text = "QR 코드 스캔",
                        enabled = waitFinished && settings.hasQr,
                        onClick = onRequestQrUnlock
                    )
                    ProtectionMode.NORMAL -> Unit
                }
            }

            Spacer(modifier = Modifier.height(22.dp))
            Button(
                onClick = onOpenSimulator,
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftSurface, contentColor = TextPrimary),
                modifier = Modifier.fillMaxWidth().height(52.dp)
            ) {
                Text("시뮬레이터 열기", fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("잠금 해제 없이 시뮬레이터만 사용할 수 있습니다.", color = TextSecondary, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(22.dp))
            Text(
                "오늘 ${dailyUsage.entryCount}/${settings.mode.dailyEntryLimit}회 사용 · ${remainingEntries}회 남음",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun ProtectionUnlockButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TextPrimary,
            contentColor = PanelColor,
            disabledContainerColor = SoftSurface,
            disabledContentColor = MutedText
        ),
        modifier = Modifier.fillMaxWidth().height(54.dp)
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ApiKeySettingsSheet(
    settings: AppSettings,
    onDismiss: () -> Unit,
    onSave: (AppSettings) -> Unit
) {
    var draft by remember(settings) { mutableStateOf(settings) }
    var editingProvider by remember { mutableStateOf<String?>(null) }
    val providers = listOf(ApiProvider.KIWOOM, ApiProvider.KIS)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = PanelColor,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(54.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LineColor)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("API 키 설정", color = TextPrimary, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("짧게 누르면 시세조회 제공사를 선택하고, 길게 누르면 API 키를 입력합니다.", color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
            Spacer(modifier = Modifier.height(22.dp))

            providers.forEach { provider ->
                ApiProviderRow(
                    provider = provider,
                    selected = draft.apiProvider == provider,
                    configured = providerHasKeys(draft, provider),
                    onClick = { draft = draft.copy(apiProvider = provider) },
                    onLongClick = { editingProvider = provider }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }

            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { onSave(draft) },
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TextPrimary, contentColor = PanelColor),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("저장", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(26.dp))
        }
    }

    editingProvider?.let { provider ->
        ApiKeyDialog(
            provider = provider,
            initialKey = providerAppKey(draft, provider),
            initialSecret = providerAppSecret(draft, provider),
            onDismiss = { editingProvider = null },
            onSave = { appKey, appSecret ->
                draft = when (provider) {
                    ApiProvider.KIWOOM -> draft.copy(kiwoomAppKey = appKey, kiwoomAppSecret = appSecret)
                    else -> draft.copy(kisAppKey = appKey, kisAppSecret = appSecret)
                }
                editingProvider = null
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun ManualAssetSettingsSheet(
    onDismiss: () -> Unit,
    onChanged: () -> Unit
) {
    val context = LocalContext.current
    val customAssets = remember {
        mutableStateListOf<AssetOption>().apply { addAll(loadCustomAssetOptions(context)) }
    }
    var tickerText by remember { mutableStateOf("") }
    var nameText by remember { mutableStateOf("") }
    var deleteTarget by remember { mutableStateOf<AssetOption?>(null) }
    val normalizedTicker = tickerText.trim().uppercase(Locale.US)
    val isEditing = customAssets.any { it.ticker.equals(normalizedTicker, ignoreCase = true) }

    fun persist() {
        saveCustomAssetOptions(context, customAssets)
        onChanged()
    }

    fun clearForm() {
        tickerText = ""
        nameText = ""
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = PanelColor,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(54.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LineColor)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("수동 추가 종목", color = TextPrimary, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "여기 추가한 종목은 계좌의 수동 자산 추가 검색 목록에 바로 나타납니다.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(22.dp))

            OutlinedTextField(
                value = tickerText,
                onValueChange = { tickerText = it.uppercase(Locale.US) },
                singleLine = true,
                label = { Text("종목 코드") },
                placeholder = { Text("예: VTI, 069500") },
                colors = appTextFieldColors(),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = nameText,
                onValueChange = { nameText = it },
                singleLine = true,
                label = { Text("종목명") },
                placeholder = { Text("예: Vanguard Total Stock Market ETF") },
                colors = appTextFieldColors(),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    val name = nameText.trim()
                    when {
                        normalizedTicker.isBlank() -> Toast.makeText(context, "종목 코드를 입력해 주세요.", Toast.LENGTH_SHORT).show()
                        name.isBlank() -> Toast.makeText(context, "종목명을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                        assetOptions.any { it.ticker.equals(normalizedTicker, ignoreCase = true) } -> {
                            Toast.makeText(context, "이미 기본 목록에 있는 종목이에요.", Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            val asset = AssetOption(
                                ticker = normalizedTicker,
                                name = name,
                                price = 0.0,
                                color = colorForCustomAsset(normalizedTicker)
                            )
                            val existingIndex = customAssets.indexOfFirst { it.ticker.equals(normalizedTicker, ignoreCase = true) }
                            if (existingIndex >= 0) {
                                customAssets[existingIndex] = asset
                            } else {
                                customAssets.add(asset)
                            }
                            persist()
                            clearForm()
                            Toast.makeText(context, if (isEditing) "종목을 수정했어요." else "종목을 추가했어요.", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TextPrimary, contentColor = PanelColor),
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text(if (isEditing) "수정하기" else "추가하기", fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("내가 추가한 종목", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(10.dp))
            if (customAssets.isEmpty()) {
                Text("아직 추가한 종목이 없어요.", color = TextSecondary, fontSize = 14.sp)
            } else {
                customAssets.forEach { asset ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .combinedClickable(
                                onClick = {
                                    tickerText = asset.ticker
                                    nameText = asset.name
                                },
                                onLongClick = { deleteTarget = asset }
                            )
                            .padding(vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AssetBadge(asset.ticker.take(1), asset.color, size = 42)
                        Spacer(modifier = Modifier.width(13.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(asset.ticker, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                            Text(asset.name, color = TextSecondary, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                        }
                        Text(
                            "삭제",
                            color = NegativeBlue,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { deleteTarget = asset }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(26.dp))
        }
    }

    deleteTarget?.let { asset ->
        AlertDialog(
            onDismissRequest = { deleteTarget = null },
            containerColor = PanelColor,
            shape = RoundedCornerShape(28.dp),
            title = { Text("종목을 삭제할까요?", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold) },
            text = { Text("${asset.ticker} ${asset.name}을 수동 추가 목록에서 삭제합니다.", color = TextSecondary, fontSize = 15.sp) },
            confirmButton = {
                Button(
                    onClick = {
                        customAssets.removeAll { it.ticker.equals(asset.ticker, ignoreCase = true) }
                        persist()
                        if (tickerText.equals(asset.ticker, ignoreCase = true)) clearForm()
                        deleteTarget = null
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NegativeBlue, contentColor = Color.White),
                    modifier = Modifier.width(112.dp).height(52.dp)
                ) {
                    Text("삭제", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                Button(
                    onClick = { deleteTarget = null },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SoftSurface, contentColor = TextPrimary),
                    modifier = Modifier.width(112.dp).height(52.dp)
                ) {
                    Text("취소", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ApiProviderRow(
    provider: String,
    selected: Boolean,
    configured: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(if (selected) SoftSurface else PanelColor)
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(horizontal = 16.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(if (provider == ApiProvider.KIWOOM) Color(0xFF0D4EA6) else Color(0xFF233C89)),
            contentAlignment = Alignment.Center
        ) {
            Text(if (provider == ApiProvider.KIWOOM) "키" else "한", color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.width(13.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(apiProviderLabel(provider), color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text(if (configured) "API 키 입력됨" else "API 키 미입력", color = TextSecondary, fontSize = 13.sp)
        }
        Box(
            modifier = Modifier
                .size(23.dp)
                .clip(CircleShape)
                .background(if (selected) TextPrimary else LineColor),
            contentAlignment = Alignment.Center
        ) {
            if (selected) Text("✓", color = PanelColor, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun ApiKeyDialog(
    provider: String,
    initialKey: String,
    initialSecret: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var appKey by remember { mutableStateOf(initialKey) }
    var appSecret by remember { mutableStateOf(initialSecret) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelColor,
        shape = RoundedCornerShape(28.dp),
        title = { Text("${apiProviderLabel(provider)} API 키", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold) },
        text = {
            Column {
                Text("APP Key", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = appKey,
                    onValueChange = { appKey = it.trim() },
                    singleLine = true,
                    placeholder = { Text("APP Key 입력") },
                    shape = RoundedCornerShape(18.dp),
                    colors = appTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text("APP Secret", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = appSecret,
                    onValueChange = { appSecret = it.trim() },
                    maxLines = 3,
                    placeholder = { Text("APP Secret 입력") },
                    shape = RoundedCornerShape(18.dp),
                    colors = appTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text("저장 후 API 키 설정 화면에서 제공사를 선택하고 저장하면 다음 시세 갱신부터 사용합니다.", color = TextSecondary, fontSize = 13.sp, lineHeight = 18.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(appKey, appSecret) },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandSoftBlue, contentColor = Color(0xFF003A76)),
                modifier = Modifier.width(112.dp).height(52.dp)
            ) {
                Text("저장", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftSurface, contentColor = TextPrimary),
                modifier = Modifier.width(112.dp).height(52.dp)
            ) {
                Text("취소", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun CompactDetailScale(content: @Composable () -> Unit) {
    val currentDensity = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(
            density = currentDensity.density * 0.9f,
            fontScale = currentDensity.fontScale * 0.9f
        ),
        content = content
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DataDownloadSheet(
    accounts: List<AccountUi>,
    onDismiss: () -> Unit,
    onDownload: (DownloadCandidate, String, (HistoricalDownloadResult) -> Unit) -> Unit
) {
    val context = LocalContext.current
    val candidates = remember(accounts) { downloadCandidates(accounts) }
    val refreshTokens = remember { mutableStateMapOf<String, Int>() }
    var downloading by remember { mutableStateOf<String?>(null) }
    var interval by remember { mutableStateOf(HistoryInterval.MONTHLY) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
        containerColor = PanelColor,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(54.dp)
                    .height(5.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(LineColor)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text("데이터 다운로드", color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(8.dp))
            Text("가격 데이터를 폰에 저장해 백테스트와 매수환율 계산에 사용합니다.", color = TextSecondary, fontSize = 14.sp, lineHeight = 20.sp)
            Spacer(modifier = Modifier.height(16.dp))
            SegmentedPill(
                left = "월별",
                right = "일별",
                selectedLeft = interval == HistoryInterval.MONTHLY,
                onLeftClick = { interval = HistoryInterval.MONTHLY },
                onRightClick = { interval = HistoryInterval.DAILY }
            )
            Spacer(modifier = Modifier.height(20.dp))
            candidates.forEach { candidate ->
                refreshTokens[candidate.symbol]
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .clickable(enabled = downloading == null) {
                            downloading = candidate.symbol
                            onDownload(candidate, interval) { result ->
                                Toast.makeText(context, result.message, Toast.LENGTH_SHORT).show()
                                refreshTokens[candidate.symbol] = (refreshTokens[candidate.symbol] ?: 0) + 1
                                downloading = null
                            }
                        }
                        .padding(vertical = 13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    BrokerBadge(candidate.symbol.take(1), candidate.color, size = 46)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(candidate.label, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                        Text(historicalSummary(context, candidate.symbol), color = TextSecondary, fontSize = 13.sp)
                    }
                    Text(
                        if (downloading == candidate.symbol) "다운로드 중" else "다운로드",
                        color = BrandGreen,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

private fun downloadCandidates(accounts: List<AccountUi>): List<DownloadCandidate> {
    val holdingCandidates = portfolioHoldings(accounts)
        .distinctBy { it.ticker }
        .map { DownloadCandidate(it.ticker, assetDisplayName(it), it.color) }
    val basicCandidates = assetOptions
        .filter { it.ticker in setOf("QLD", "QQQ", "005930", "367380", "433880") }
        .map { DownloadCandidate(it.ticker, it.name, it.color) }
    val dividendDownloadCandidates = dividendCandidates()
        .map { DownloadCandidate(it.ticker, it.name, it.color) }
    return (listOf(DownloadCandidate("USDKRW", "원달러", CashOrange)) + holdingCandidates + basicCandidates + dividendDownloadCandidates)
        .distinctBy { it.symbol.uppercase(Locale.US) }
}

@Composable
private fun DisplayModeSettingsScreen(
    selectedMode: String,
    onBack: () -> Unit,
    onSelect: (String) -> Unit
) {
    val headerTopPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding() + 12.dp
    ScreenColumn(topPadding = 0) {
        Spacer(modifier = Modifier.height(headerTopPadding))
        PlainTopBar(title = "화면 모드 설정", onBack = onBack, rightText = "", onRightClick = {})
        Spacer(modifier = Modifier.height(110.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            DisplayModeOption("시스템 설정", DisplayMode.SYSTEM, selectedMode, onSelect)
            DisplayModeOption("밝게", DisplayMode.LIGHT, selectedMode, onSelect)
            DisplayModeOption("어둡게", DisplayMode.DARK, selectedMode, onSelect)
        }
    }
}

@Composable
private fun DisplayModeOption(
    title: String,
    mode: String,
    selectedMode: String,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .width(96.dp)
            .clickable { onSelect(mode) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DisplayModePreview(mode)
        Spacer(modifier = Modifier.height(18.dp))
        Text(title, color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(18.dp))
        SmallRadio(selected = mode == selectedMode, selectedColor = NegativeBlue)
    }
}

@Composable
private fun DisplayModePreview(mode: String) {
    val dark = mode == DisplayMode.DARK
    val system = mode == DisplayMode.SYSTEM
    val previewDark = Color(0xFF111315)
    val previewLight = Color.White
    Box(
        modifier = Modifier
            .width(86.dp)
            .height(136.dp)
            .clip(RoundedCornerShape(14.dp))
            .background(if (dark) previewDark else previewLight),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            if (system) {
                drawRect(previewLight, size = androidx.compose.ui.geometry.Size(size.width, size.height / 2))
                drawRect(previewDark, topLeft = Offset(0f, size.height / 2), size = androidx.compose.ui.geometry.Size(size.width, size.height / 2))
                drawCircle(previewDark, radius = 20f, center = Offset(size.width / 2, size.height / 2))
                drawCircle(previewLight, radius = 11f, center = Offset(size.width / 2, size.height / 2))
            } else {
                val muted = if (dark) Color(0xFF2F333A) else Color(0xFFE8EAEE)
                repeat(5) { index ->
                    val y = 32f + index * 20f
                    drawCircle(muted, radius = 5f, center = Offset(18f, y))
                    drawLine(if (index % 2 == 0) PositiveRed else NegativeBlue, Offset(44f, y), Offset(60f, y - 5f), strokeWidth = 3f, cap = StrokeCap.Round)
                    drawRoundRect(if (index % 2 == 0) PositiveRed else NegativeBlue, topLeft = Offset(68f, y - 6f), size = androidx.compose.ui.geometry.Size(18f, 8f), cornerRadius = androidx.compose.ui.geometry.CornerRadius(5f, 5f))
                }
            }
        }
    }
}

@Composable
private fun CurrencySettingsSheet(
    selectedCurrency: String,
    onSelect: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 22.dp, vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(54.dp)
                .height(5.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(Color(0xFFE8EAED))
        )
        Spacer(modifier = Modifier.height(26.dp))
        Text("통화 설정", color = TextPrimary, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(12.dp))
        Text("자산 및 수익을 선택한 통화로 변경합니다. 현재 환율 기준입니다.", color = TextSecondary, fontSize = 16.sp, lineHeight = 23.sp)
        Spacer(modifier = Modifier.height(28.dp))
        CurrencyOptionRow("원", "₩", CashOrange, CurrencyMode.KRW, selectedCurrency, onSelect)
        CurrencyOptionRow("달러", "$", Color(0xFF44C996), CurrencyMode.USD, selectedCurrency, onSelect)
        Spacer(modifier = Modifier.height(28.dp))
    }
}

@Composable
private fun CurrencyOptionRow(
    title: String,
    symbol: String,
    color: Color,
    currency: String,
    selectedCurrency: String,
    onSelect: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect(currency) }
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SmallRadio(selected = currency == selectedCurrency, selectedColor = TextPrimary)
        Spacer(modifier = Modifier.width(18.dp))
        Box(modifier = Modifier.size(54.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
            Text(symbol, color = Color.White, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.width(18.dp))
        Text(title, color = TextPrimary, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(title, color = TextSecondary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
    Spacer(modifier = Modifier.height(20.dp))
}

@Composable
private fun SettingsValueRow(title: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 17.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
        Text(value, color = NegativeBlue, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.width(8.dp))
        Text("›", color = MutedText, fontSize = 30.sp)
    }
}

@Composable
private fun SettingsActionRow(title: String, actionText: String, enabled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 17.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
        Text(
            actionText,
            color = if (enabled) BrandGreen else MutedText,
            fontSize = 17.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(if (enabled) BrandSoftBlue.copy(alpha = 0.22f) else SoftSurface)
                .clickable(enabled = enabled, onClick = onClick)
                .padding(horizontal = 13.dp, vertical = 8.dp)
        )
    }
}

@Composable
private fun SettingsStaticRow(title: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 17.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(title, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
        Text(value, color = TextSecondary, fontSize = 19.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SmallRadio(selected: Boolean, selectedColor: Color) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(if (selected) selectedColor else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Text("✓", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
        } else {
            Canvas(modifier = Modifier.size(30.dp)) {
                drawCircle(Color(0xFFE6E9ED), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
            }
        }
    }
}

@Composable
private fun AccountDrawer(
    accounts: List<AccountUi>,
    onTotalClick: () -> Unit,
    onAccountClick: (AccountUi) -> Unit,
    onManageClick: () -> Unit,
    onAddAccountClick: () -> Unit
) {
    ModalDrawerSheet(
        drawerContainerColor = AppBackground,
        modifier = Modifier
            .fillMaxHeight()
            .fillMaxWidth(0.86f)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, top = 54.dp, end = 24.dp, bottom = 70.dp)
        ) {
            Text("계좌", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(28.dp))
            Card(
                colors = CardDefaults.cardColors(containerColor = SoftSurface),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(0.dp),
                modifier = Modifier.fillMaxWidth().clickable(onClick = onTotalClick)
            ) {
                Row(modifier = Modifier.padding(18.dp), verticalAlignment = Alignment.CenterVertically) {
                    LogoMark(markSize = 27)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text("총 자산", color = TextSecondary, fontSize = 14.sp)
                        Text(formatWon(accounts.sumOf { it.totalAmount }), color = TextPrimary, fontSize = 23.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 25.sp)
                    }
                }
            }
            Spacer(modifier = Modifier.height(22.dp))
            accounts.forEach { AccountRow(account = it, compactName = true, onClick = { onAccountClick(it) }) }
            Spacer(modifier = Modifier.height(26.dp))
            DrawerMenuRow("⚙", "계좌 관리", enabled = true, onClick = onManageClick)
            DrawerMenuRow("+", "계좌 추가", enabled = true, onClick = onAddAccountClick)
        }
    }
}

@Composable
private fun AccountRow(account: AccountUi, compactName: Boolean = false, onClick: () -> Unit) {
    val amountFontSize = if (compactName) 17.sp else 21.sp
    val amountLineHeight = if (compactName) 19.sp else 23.sp
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = if (compactName) 15.dp else 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BrokerBadge(accountBadgeText(account), account.color, size = 52)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                account.name,
                color = if (compactName) TextSecondary else TextPrimary,
                fontSize = if (compactName) 15.sp else 17.sp,
                fontWeight = if (compactName) FontWeight.Medium else FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(formatWon(account.totalAmount), color = TextPrimary, fontSize = amountFontSize, fontWeight = FontWeight.ExtraBold, lineHeight = amountLineHeight)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AccountManageRow(account: AccountUi, onLongClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = {}, onLongClick = onLongClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        BrokerBadge(accountBadgeText(account), account.color, size = 54)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(account.name, color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Text(formatWon(account.totalAmount), color = TextSecondary, fontSize = 16.sp)
        }
        Text("›", color = Color(0xFFC6CBD0), fontSize = 28.sp)
    }
}

@Composable
private fun SelectableAccountRow(account: AccountUi, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SelectionCircle(selected)
        Spacer(modifier = Modifier.width(18.dp))
        BrokerBadge(accountBadgeText(account), account.color, size = 54)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(account.name, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(formatWon(account.totalAmount), color = TextSecondary, fontSize = 16.sp)
        }
    }
}

@Composable
private fun AssetSearchScreen(settings: AppSettings, onClose: () -> Unit, onSelect: (AssetOption) -> Unit) {
    val context = LocalContext.current
    var query by remember { mutableStateOf("") }
    val normalizedQuery = query.trim()
    val compactQuery = compactSearchText(normalizedQuery)
    var remoteAsset by remember { mutableStateOf<AssetOption?>(null) }

    LaunchedEffect(normalizedQuery) {
        remoteAsset = null
        val code = normalizedQuery.filter { it.isDigit() }
        if (code.length == 6) {
            remoteAsset = lookupKisDomesticAsset(context, settings, code)
        }
    }

    val selectableAssets = remember(context) { manualAssetOptions(context) }
    val localAssets = if (normalizedQuery.isBlank()) {
        selectableAssets
    } else {
        selectableAssets.filter {
            compactSearchText(it.ticker).contains(compactQuery) ||
                compactSearchText(it.name).contains(compactQuery)
        }
    }
    val filteredAssets = (remoteAsset?.let { asset ->
        if (localAssets.none { it.ticker == asset.ticker }) listOf(asset) + localAssets else localAssets
    } ?: localAssets)

    ScreenColumn(topPadding = 92) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("주식, ETF 검색", color = TextSecondary) },
                singleLine = true,
                colors = appTextFieldColors(),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text("×", color = TextPrimary, fontSize = 38.sp, modifier = Modifier.clickable(onClick = onClose))
        }
        Spacer(modifier = Modifier.height(26.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            SearchTab("주식", true)
            SearchTab("코인", false)
            SearchTab("펀드", false)
            SearchTab("부동산", false)
        }
        Spacer(modifier = Modifier.height(24.dp))
        filteredAssets.forEachIndexed { index, asset ->
            AssetOptionRow(rank = index + 1, asset = asset, onClick = { onSelect(asset) })
        }
        if (filteredAssets.isEmpty()) {
            Text("검색 결과가 없습니다.", color = TextSecondary, fontSize = 16.sp)
        }
    }
}

@Composable
private fun AddAssetScreen(account: AccountUi?, asset: AssetOption, usdKrw: Double, settings: AppSettings, onClose: () -> Unit, onAdd: (Double, Double, String, Double) -> Unit) {
    if (account == null) {
        EmptyAccountsScreen(onBack = onClose)
        return
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var quantityText by remember { mutableStateOf("") }
    var averagePriceText by remember { mutableStateOf(if (asset.price > 0.0) asset.price.toString() else "") }
    var priceCurrency by remember { mutableStateOf(if (isKoreanTicker(asset.ticker)) "KRW" else "USD") }
    var tradeDate by remember { mutableStateOf(todayText()) }
    var purchaseExchangeRateText by remember { mutableStateOf(usdKrw.roundToLong().toString()) }
    val quantity = quantityText.toDoubleOrNull() ?: 0.0
    val enteredAveragePrice = averagePriceText.toDoubleOrNull() ?: 0.0
    val storedAveragePrice = when {
        isKoreanTicker(asset.ticker) -> enteredAveragePrice
        priceCurrency == "USD" -> enteredAveragePrice
        else -> enteredAveragePrice / usdKrw
    }
    val purchaseExchangeRate = if (isKoreanTicker(asset.ticker) || priceCurrency == "KRW") 1.0 else (purchaseExchangeRateText.toDoubleOrNull() ?: usdKrw)
    val canAdd = quantity > 0 && storedAveragePrice > 0

    ScreenColumn {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("자산 추가", color = TextPrimary, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.weight(1f))
            Text("×", color = TextPrimary, fontSize = 38.sp, modifier = Modifier.clickable(onClick = onClose))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("총 거래액", color = TextSecondary, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = PanelColor),
            shape = RoundedCornerShape(22.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AssetBadge(asset.ticker.take(1), asset.color)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(if (isKoreanTicker(asset.ticker)) asset.name else asset.ticker, color = TextPrimary, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                        Text(if (isKoreanTicker(asset.ticker)) asset.ticker else asset.name, color = TextSecondary, fontSize = 15.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                SegmentedPill(
                    left = "달러",
                    right = "원화",
                    selectedLeft = priceCurrency == "USD",
                    onLeftClick = { priceCurrency = "USD" },
                    onRightClick = { priceCurrency = "KRW" }
                )
                Spacer(modifier = Modifier.height(18.dp))
                InputRow(label = "수량", value = quantityText, suffix = "주", onValueChange = { quantityText = it })
                Spacer(modifier = Modifier.height(14.dp))
                InputRow(label = "평단가", value = averagePriceText, suffix = if (priceCurrency == "USD") "달러" else "원", onValueChange = { averagePriceText = it })
                Spacer(modifier = Modifier.height(14.dp))
                InputRow(label = "매수일", value = tradeDate, suffix = "", onValueChange = { tradeDate = it })
                Spacer(modifier = Modifier.height(14.dp))
                if (priceCurrency == "USD" && !isKoreanTicker(asset.ticker)) {
                    Spacer(modifier = Modifier.height(14.dp))
                    ExchangeRateInputRow(
                        label = "매수환율",
                        value = purchaseExchangeRateText,
                        onValueChange = { purchaseExchangeRateText = it },
                        onUpdateClick = {
                            scope.launch {
                                val updated = fetchUsdKrwForDate(context, settings, tradeDate, usdKrw)
                                if (updated != null) {
                                    purchaseExchangeRateText = updated.roundToLong().toString()
                                    Toast.makeText(context, "환율을 업데이트했어요.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "환율 업데이트에 실패했어요.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(26.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoChip(account.name)
            if (priceCurrency == "USD" && !isKoreanTicker(asset.ticker)) {
                InfoChip("${formatDecimal(usdKrw)}원")
            }
            InfoChip(if (priceCurrency == "USD" && !isKoreanTicker(asset.ticker)) "${formatDecimal(quantity * storedAveragePrice)}달러" else formatWon((quantity * enteredAveragePrice).roundToLong()))
            InfoChip(tradeDate)
        }
        Spacer(modifier = Modifier.height(34.dp))
        Button(
            onClick = { onAdd(quantity, storedAveragePrice, tradeDate.ifBlank { todayText() }, purchaseExchangeRate) },
            enabled = canAdd,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = TextPrimary,
                disabledContainerColor = SoftSurface,
                disabledContentColor = TextSecondary
            ),
            modifier = Modifier.fillMaxWidth().height(58.dp)
        ) {
            Text("추가하기", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun HoldingDetailScreen(
    account: AccountUi?,
    holding: HoldingUi?,
    onBack: () -> Unit,
    onBuy: () -> Unit,
    onSell: () -> Unit,
    onTradeLongClick: (TradeUi) -> Unit,
    onTradeEdit: (TradeUi) -> Unit,
    onDeleteTrades: (List<Long>) -> Unit
) {
    if (account == null || holding == null) {
        EmptyAccountsScreen(onBack = onBack)
        return
    }

    val dayRate = holding.dayRate
    val dayProfit = holding.dayProfit
    var editMode by remember(holding.ticker) { mutableStateOf(false) }
    val selectedTradeIds = remember(holding.ticker) { mutableStateListOf<Long>() }
    val deletedTradeIds = remember(holding.ticker) { mutableStateListOf<Long>() }
    val visibleTrades = (if (holding.trades.isEmpty()) {
        listOf(TradeUi(0L, "오늘", TradeSide.HOLD, holding.quantity, holding.averagePrice))
    } else {
        holding.trades.sortedByDescending { it.id }
    }).filterNot { it.id in deletedTradeIds }

    Box(modifier = Modifier.fillMaxSize().background(AppBackground)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(start = 24.dp, top = 68.dp, end = 24.dp, bottom = 180.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("⌄", color = TextPrimary, fontSize = 36.sp, modifier = Modifier.clickable(onClick = onBack))
                Spacer(modifier = Modifier.weight(1f))
                AssetBadge(holding.ticker.take(1), holding.color)
                Spacer(modifier = Modifier.width(8.dp))
                Text(assetDisplayName(holding), color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.weight(1f))
                Text("⋮", color = TextPrimary, fontSize = 32.sp)
            }
            Spacer(modifier = Modifier.height(64.dp))
            Text("보유 정보", color = TextPrimary, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(32.dp))
            DetailMetricRow("평가액", formatWon(holding.amount))
            DetailMetricRow("보유량", "${formatQuantity(holding.quantity)}주")
            DetailMetricRow("평단가", formatAssetPrice(holding.averagePrice, holding.ticker))
            if (!isKoreanTicker(holding.ticker)) {
                DetailMetricRow("매입환율", "${formatDecimal(holding.averageExchangeRate)}원")
            }
            DetailMetricRow("계좌", account.name)
            DetailMetricRow("메모", "추가하기")
            Spacer(modifier = Modifier.height(48.dp))
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Text("거래", color = TextPrimary, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    if (editMode) "저장" else "편집",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        if (editMode) {
                            if (deletedTradeIds.isNotEmpty()) onDeleteTrades(deletedTradeIds.toList())
                            selectedTradeIds.clear()
                            deletedTradeIds.clear()
                            editMode = false
                        } else {
                            editMode = true
                        }
                    }
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
            visibleTrades.forEach { trade ->
                TradeHistoryRow(
                    date = trade.date,
                    title = when (trade.side) {
                        TradeSide.BUY -> "매수"
                        TradeSide.SELL -> "매도"
                        else -> "보유"
                    },
                    caption = "${formatQuantity(trade.quantity)}주 · ${formatAssetPrice(trade.price, holding.ticker)}",
                    amount = formatWon((trade.quantity * holding.currentPrice * holdingExchangeRate(holding.ticker, holding.exchangeRate)).roundToLong()),
                    profit = if (trade.side == TradeSide.SELL) 0L else dayProfit,
                    rate = if (trade.side == TradeSide.SELL) 0.0 else dayRate,
                    editMode = editMode,
                    selected = trade.id in selectedTradeIds,
                    onClick = if (editMode && trade.id != 0L) ({
                        if (trade.id in selectedTradeIds) selectedTradeIds.remove(trade.id) else selectedTradeIds.add(trade.id)
                    }) else ({}),
                    onLongClick = if (trade.id == 0L || editMode) null else ({ onTradeEdit(trade) })
                )
            }
            Spacer(modifier = Modifier.height(120.dp))
            Text(
                "거래내역은 최대 5년까지 조회할 수 있어요",
                color = TextSecondary,
                fontSize = 15.sp,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(120.dp))
        }
        if (editMode) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(AppBackground)
                    .padding(start = 24.dp, top = 18.dp, end = 24.dp, bottom = 58.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    if (selectedTradeIds.isEmpty()) "전체선택" else "${selectedTradeIds.size} 선택해제",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable {
                        val editableIds = visibleTrades.filter { it.id != 0L }.map { it.id }
                        if (selectedTradeIds.isEmpty()) selectedTradeIds.addAll(editableIds) else selectedTradeIds.clear()
                    }
                )
                Text(
                    "삭제",
                    color = if (selectedTradeIds.isEmpty()) Color(0xFFF3A7B5) else PositiveRed,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable(enabled = selectedTradeIds.isNotEmpty()) {
                        deletedTradeIds.addAll(selectedTradeIds)
                        selectedTradeIds.clear()
                    }
                )
            }
        } else Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(AppBackground)
                .padding(start = 24.dp, top = 18.dp, end = 24.dp, bottom = 58.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onBuy,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PositiveRed),
                modifier = Modifier.weight(1f).height(58.dp)
            ) {
                Text("매수", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
            }
            Button(
                onClick = onSell,
                shape = RoundedCornerShape(28.dp),
                colors = ButtonDefaults.buttonColors(containerColor = NegativeBlue),
                modifier = Modifier.weight(1f).height(58.dp)
            ) {
                Text("매도", fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun TradeAssetScreen(
    account: AccountUi?,
    holding: HoldingUi?,
    side: String,
    usdKrw: Double,
    settings: AppSettings,
    editingTrade: TradeUi? = null,
    onClose: () -> Unit,
    onAdd: (Double, Double, String, Double) -> Unit
) {
    if (account == null || holding == null) {
        EmptyAccountsScreen(onBack = onClose)
        return
    }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var quantityText by remember(editingTrade?.id) { mutableStateOf(editingTrade?.quantity?.let { formatDecimal(it) } ?: "") }
    var averagePriceText by remember(editingTrade?.id) { mutableStateOf((editingTrade?.price ?: holding.currentPrice).toString()) }
    var priceCurrency by remember { mutableStateOf(if (isKoreanTicker(holding.ticker)) "KRW" else "USD") }
    var tradeDate by remember(editingTrade?.id) { mutableStateOf(editingTrade?.date ?: todayText()) }
    var purchaseExchangeRateText by remember(editingTrade?.id) { mutableStateOf((editingTrade?.exchangeRate ?: usdKrw).roundToLong().toString()) }
    val quantity = quantityText.toDoubleOrNull() ?: 0.0
    val enteredAveragePrice = averagePriceText.toDoubleOrNull() ?: 0.0
    val storedAveragePrice = when {
        isKoreanTicker(holding.ticker) -> enteredAveragePrice
        priceCurrency == "USD" -> enteredAveragePrice
        else -> enteredAveragePrice / usdKrw
    }
    val purchaseExchangeRate = if (isKoreanTicker(holding.ticker) || priceCurrency == "KRW") 1.0 else (purchaseExchangeRateText.toDoubleOrNull() ?: usdKrw)
    val hasTradeChanges = editingTrade == null ||
        quantity != editingTrade.quantity ||
        storedAveragePrice != editingTrade.price ||
        tradeDate != editingTrade.date ||
        purchaseExchangeRate != editingTrade.exchangeRate
    val quantityLimitOk = side == "buy" || editingTrade != null || quantity <= holding.quantity
    val canAdd = quantity > 0 && storedAveragePrice > 0 && tradeDate.isNotBlank() && quantityLimitOk && hasTradeChanges
    val sideLabel = if (side == "buy") "매수" else "매도"

    ScreenColumn {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Text("$sideLabel ${if (editingTrade == null) "추가" else "수정"}", color = TextPrimary, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.weight(1f))
            Text("×", color = TextPrimary, fontSize = 38.sp, modifier = Modifier.clickable(onClick = onClose))
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text("총 거래액", color = TextSecondary, fontSize = 15.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Card(
            colors = CardDefaults.cardColors(containerColor = PanelColor),
            shape = RoundedCornerShape(22.dp),
            elevation = CardDefaults.cardElevation(0.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AssetBadge(holding.ticker.take(1), holding.color)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(assetDisplayName(holding), color = TextPrimary, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
                        Text(if (isKoreanTicker(holding.ticker)) holding.ticker else holding.name, color = TextSecondary, fontSize = 15.sp)
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
                SegmentedPill(
                    left = "달러",
                    right = "원화",
                    selectedLeft = priceCurrency == "USD",
                    onLeftClick = { priceCurrency = "USD" },
                    onRightClick = { priceCurrency = "KRW" }
                )
                Spacer(modifier = Modifier.height(18.dp))
                InputRow(label = "수량", value = quantityText, suffix = "주", onValueChange = { quantityText = it })
                Spacer(modifier = Modifier.height(14.dp))
                InputRow(label = "평단가", value = averagePriceText, suffix = if (priceCurrency == "USD") "달러" else "원", onValueChange = { averagePriceText = it })
                Spacer(modifier = Modifier.height(14.dp))
                InputRow(label = if (side == "buy") "매수일" else "매도일", value = tradeDate, suffix = "", onValueChange = { tradeDate = it })
                if (priceCurrency == "USD" && !isKoreanTicker(holding.ticker)) {
                    Spacer(modifier = Modifier.height(14.dp))
                    ExchangeRateInputRow(
                        label = if (side == "buy") "매수환율" else "매도환율",
                        value = purchaseExchangeRateText,
                        onValueChange = { purchaseExchangeRateText = it },
                        onUpdateClick = {
                            scope.launch {
                                val updated = fetchUsdKrwForDate(context, settings, tradeDate, usdKrw)
                                if (updated != null) {
                                    purchaseExchangeRateText = updated.roundToLong().toString()
                                    Toast.makeText(context, "환율을 업데이트했어요.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "환율 업데이트에 실패했어요.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(26.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            InfoChip(account.name)
            if (priceCurrency == "USD" && !isKoreanTicker(holding.ticker)) {
                InfoChip("${formatDecimal(usdKrw)}원")
            }
            InfoChip(if (priceCurrency == "USD" && !isKoreanTicker(holding.ticker)) "${formatDecimal(quantity * storedAveragePrice)}달러" else formatWon((quantity * enteredAveragePrice).roundToLong()))
            InfoChip(tradeDate)
        }
        Spacer(modifier = Modifier.height(34.dp))
        Button(
            onClick = { onAdd(quantity, storedAveragePrice, tradeDate.ifBlank { todayText() }, purchaseExchangeRate) },
            enabled = canAdd,
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (side == "buy") PositiveRed else NegativeBlue,
                disabledContainerColor = SoftSurface,
                disabledContentColor = TextSecondary
            ),
            modifier = Modifier.fillMaxWidth().height(58.dp)
        ) {
            Text(if (editingTrade == null) "추가하기" else "저장하기", fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        }
    }
}

@Composable
private fun DetailMetricRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextSecondary, fontSize = 18.sp, modifier = Modifier.weight(1f))
        Text(value, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
    }
}

@Composable
private fun TradeHistoryRow(
    date: String,
    title: String,
    caption: String,
    amount: String,
    profit: Long,
    rate: Double,
    editMode: Boolean = false,
    selected: Boolean = false,
    onClick: () -> Unit = {},
    onLongClick: (() -> Unit)? = null
) {
    val color = if (profit < 0) NegativeBlue else PositiveRed
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (editMode) {
            SelectionCircle(selected)
            Spacer(modifier = Modifier.width(12.dp))
        }
        Text(date, color = TextSecondary, fontSize = 16.sp, modifier = Modifier.width(70.dp))
        Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(SoftSurface), contentAlignment = Alignment.Center) {
            Text("›", color = TextSecondary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(18.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 19.sp, fontWeight = FontWeight.Bold)
            Text(caption, color = TextSecondary, fontSize = 15.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(amount, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text(formatPercent(rate), color = color, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun ManualAddSheet(onAssetAdd: () -> Unit, onCashAdd: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 30.dp, vertical = 22.dp)) {
        Box(
            modifier = Modifier
                .width(64.dp)
                .height(6.dp)
                .clip(RoundedCornerShape(999.dp))
                .background(LineColor)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(28.dp))
        Text("수동 추가", color = TextPrimary, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(24.dp))
        SheetActionRow("✎", "수동 자산 추가", onAssetAdd)
        SheetActionRow("✎", "현금 추가", onCashAdd)
        Spacer(modifier = Modifier.height(34.dp))
    }
}

@Composable
private fun InputRow(label: String, value: String, suffix: String, onValueChange: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(76.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { next -> onValueChange(next.filter { it.isDigit() || it == '.' }) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(18.dp),
            colors = appTextFieldColors(),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(suffix, color = TextSecondary, fontSize = 16.sp)
    }

}

@Composable
private fun ExchangeRateInputRow(label: String, value: String, onValueChange: (String) -> Unit, onUpdateClick: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(76.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { next -> onValueChange(next.filter { it.isDigit() || it == '.' }) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            shape = RoundedCornerShape(18.dp),
            colors = appTextFieldColors(),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text("원", color = TextSecondary, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            "Update",
            color = BrandGreen,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(SoftSurface)
                .clickable(onClick = onUpdateClick)
                .padding(horizontal = 10.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun BacktestToolSwitcher(selected: String, onSelect: (String) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(
            listOf("백테스트", "배당", "자가배당"),
            listOf("3자산 분배", "시나리오 비교", "은퇴 성공확률")
        ).forEach { rowLabels ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowLabels.forEach { label ->
                    val active = selected == label
                    val toolColor = simulatorToolColor(label)
                    val inactiveAlpha = if (PanelColor == Color.White) 0.11f else 0.22f
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (active) toolColor else toolColor.copy(alpha = inactiveAlpha))
                            .clickable { onSelect(label) }
                            .padding(vertical = 11.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            label,
                            color = if (active) Color.White else toolColor,
                            fontSize = if (label == "은퇴 성공확률") 12.sp else 14.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

private fun simulatorToolColor(label: String): Color = when (label) {
    "백테스트" -> SimulatorBacktestColor
    "배당", "자가배당", "3자산 분배" -> SimulatorCashFlowColor
    else -> SimulatorAnalysisColor
}

@Composable
private fun SimulationPresetSaveDialog(
    title: String,
    name: String,
    existingNames: List<String>,
    onNameChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSaveNew: (String) -> Unit,
    onOverwrite: (String) -> Unit
) {
    var overwriteName by remember { mutableStateOf<String?>(null) }
    if (overwriteName == null) AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelColor,
        title = { Text(title, color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("저장 이름") },
                    singleLine = true,
                    colors = appTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                if (existingNames.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("기존 저장 목록", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                    Spacer(modifier = Modifier.height(7.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 220.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        existingNames.forEach { existingName ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SoftSurface)
                                    .clickable { overwriteName = existingName }
                                    .padding(horizontal = 12.dp, vertical = 11.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    existingName,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.weight(1f),
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text("업데이트", color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = name.isNotBlank(),
                onClick = {
                    val trimmed = name.trim()
                    if (existingNames.contains(trimmed)) overwriteName = trimmed else onSaveNew(trimmed)
                }
            ) {
                Text("저장", color = if (name.isNotBlank()) BrandGreen else MutedText, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소", color = TextSecondary) }
        }
    )

    overwriteName?.let { existingName ->
        AlertDialog(
            onDismissRequest = { overwriteName = null },
            containerColor = PanelColor,
            title = { Text("저장 항목 업데이트", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = { Text("$existingName 항목을 현재 조건과 결과로 업데이트할까요?", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    overwriteName = null
                    onOverwrite(existingName)
                }) { Text("업데이트", color = BrandGreen, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { overwriteName = null }) { Text("취소", color = TextSecondary) }
            }
        )
    }
}

@Composable
private fun SimulationPresetRenameDialog(
    currentName: String,
    existingNames: List<String>,
    onDismiss: () -> Unit,
    onRename: (String) -> Unit,
    onDelete: () -> Unit
) {
    var renamedText by remember(currentName) { mutableStateOf(currentName) }
    val trimmed = renamedText.trim()
    val duplicate = existingNames.any { it != currentName && it == trimmed }
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelColor,
        title = { Text("저장 이름 수정", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = renamedText,
                    onValueChange = { renamedText = it },
                    label = { Text("저장 이름") },
                    singleLine = true,
                    isError = duplicate,
                    supportingText = {
                        if (duplicate) Text("같은 이름의 저장 항목이 있습니다.", color = PositiveRed)
                    },
                    colors = appTextFieldColors(),
                    modifier = Modifier.fillMaxWidth()
                )
                TextButton(onClick = onDelete) {
                    Text("저장 항목 삭제", color = PositiveRed, fontWeight = FontWeight.Bold)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = trimmed.isNotEmpty() && !duplicate,
                onClick = { onRename(trimmed) }
            ) {
                Text(
                    "수정",
                    color = if (trimmed.isNotEmpty() && !duplicate) BrandGreen else MutedText,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("취소", color = TextSecondary) }
        }
    )
}

private fun scenarioComparisonTypeLabel(type: ScenarioComparisonType): String = when (type) {
    ScenarioComparisonType.DIVIDEND -> "배당"
    ScenarioComparisonType.SELF_DIVIDEND -> "자가배당"
    ScenarioComparisonType.THREE_ASSET -> "3자산 분배"
}

private fun scenarioComparisonTypeCashFlowLabel(type: ScenarioComparisonType): String = when (type) {
    ScenarioComparisonType.DIVIDEND -> "세후 월배당"
    ScenarioComparisonType.SELF_DIVIDEND -> "세후 월인출"
    ScenarioComparisonType.THREE_ASSET -> "월 생활비"
}

private fun scenarioComparisonColor(index: Int): Color = listOf(
    BrandGreen,
    PositiveRed,
    FourAssetSchdColor,
    PsuOrange,
    NasdaqBlue,
    FourAssetQldColor
)[index.mod(6)]

private fun scenarioComparisonCandidates(context: Context): List<ScenarioComparisonCandidate> {
    if (savedDividendSimulationPresetsCache.isEmpty()) {
        savedDividendSimulationPresetsCache.addAll(loadSavedDividendPresets(context))
    }
    if (savedSelfDividendPresetsCache.isEmpty()) {
        savedSelfDividendPresetsCache.addAll(loadSavedSelfDividendPresets(context))
    }
    if (savedFourAssetDistributionPresetsCache.isEmpty()) {
        savedFourAssetDistributionPresetsCache.addAll(loadSavedFourAssetDistributionPresets(context))
    }

    val candidates = mutableListOf<ScenarioComparisonCandidate>()
    savedDividendSimulationPresetsCache.forEach { preset ->
        val id = "dividend:${preset.name}"
        val series = preset.projectionRows.takeIf { it.isNotEmpty() }?.let { rows ->
            ScenarioComparisonSeries(
                id = id,
                name = preset.name,
                type = ScenarioComparisonType.DIVIDEND,
                points = rows.map { row ->
                    ScenarioComparisonPoint(row.year, row.monthlyDividend, row.totalAsset)
                }
            )
        }
        candidates += ScenarioComparisonCandidate(
            id = id,
            name = preset.name,
            type = ScenarioComparisonType.DIVIDEND,
            series = series,
            historicalAllocations = listOf(HistoricalAssetAllocation(preset.ticker, 1.0))
        )
    }
    savedSelfDividendPresetsCache.forEach { preset ->
        val id = "self_dividend:${preset.name}"
        val series = preset.result.takeIf { it.isNotEmpty() }?.let { rows ->
            ScenarioComparisonSeries(
                id = id,
                name = preset.name,
                type = ScenarioComparisonType.SELF_DIVIDEND,
                points = rows.map { row ->
                    ScenarioComparisonPoint(row.year, row.monthlyTakeHome, row.totalAsset)
                }
            )
        }
        candidates += ScenarioComparisonCandidate(
            id = id,
            name = preset.name,
            type = ScenarioComparisonType.SELF_DIVIDEND,
            series = series,
            historicalAllocations = preset.assets.mapNotNull { asset ->
                digitsToLong(asset.investmentAmount).takeIf { it > 0L }?.let { amount ->
                    HistoricalAssetAllocation(asset.ticker, amount.toDouble())
                }
            }
        )
    }
    savedFourAssetDistributionPresetsCache.forEach { preset ->
        val id = "three_asset:${preset.name}"
        val rows = calculateFourAssetRetirement(fourAssetRetirementInput(preset)).rows
        val series = rows.takeIf { it.isNotEmpty() }?.let {
            ScenarioComparisonSeries(
                id = id,
                name = preset.name,
                type = ScenarioComparisonType.THREE_ASSET,
                points = it.map { row ->
                    ScenarioComparisonPoint(
                        year = row.year,
                        monthlyCashFlowWon = row.actualAnnualCashFlowWon / 12L,
                        totalAssetWon = row.totalAssetWon
                    )
                }
            )
        }
        candidates += ScenarioComparisonCandidate(
            id = id,
            name = preset.name,
            type = ScenarioComparisonType.THREE_ASSET,
            series = series,
            historicalAllocations = listOf(
                HistoricalAssetAllocation("SCHD", preset.appliedSchdRatio),
                HistoricalAssetAllocation("JEPQ", preset.appliedJepqRatio),
                HistoricalAssetAllocation("QLD", preset.appliedQldRatio)
            )
        )
    }
    return candidates
}

@Composable
private fun ScenarioComparisonContent() {
    val context = LocalContext.current
    val candidates = remember(context) { scenarioComparisonCandidates(context) }
    val availableCandidates = candidates.filter { it.series != null }
    val selectedIds = remember(candidates) {
        val availableIds = availableCandidates.map { it.id }.toSet()
        val restored = loadScenarioComparisonSelection(context).filter { it in availableIds }
        mutableStateListOf<String>().also { selected ->
            selected.addAll((restored.ifEmpty { availableCandidates.take(2).map { it.id } }).take(4))
        }
    }
    var metricIndex by remember(context) { mutableIntStateOf(loadScenarioComparisonMetric(context)) }
    val selectedCandidates = candidates.filter { it.id in selectedIds && it.series != null }
    val selectedSeries = selectedCandidates.mapNotNull { it.series }

    fun toggleCandidate(candidate: ScenarioComparisonCandidate) {
        if (candidate.series == null) return
        if (candidate.id in selectedIds) {
            selectedIds.remove(candidate.id)
        } else if (selectedIds.size >= 4) {
            Toast.makeText(context, "비교 시나리오는 최대 4개까지 선택할 수 있어요.", Toast.LENGTH_SHORT).show()
            return
        } else {
            selectedIds.add(candidate.id)
        }
        saveScenarioComparisonSelection(context, selectedIds.toSet())
    }

    BacktestCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionTitle("비교 시나리오")
            Spacer(modifier = Modifier.weight(1f))
            Text("${selectedIds.size}/4 선택", color = BrandGreen, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "저장된 배당·자가배당·3자산 분배 결과 중 2개 이상을 선택하세요.",
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )
        Spacer(modifier = Modifier.height(14.dp))
        if (candidates.isEmpty()) {
            Text("비교할 저장 시나리오가 없습니다.", color = MutedText, fontSize = 14.sp)
        } else {
            ScenarioComparisonType.entries.forEach { type ->
                val typedCandidates = candidates.filter { it.type == type }
                if (typedCandidates.isNotEmpty()) {
                    Text(
                        scenarioComparisonTypeLabel(type),
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    typedCandidates.forEach { candidate ->
                        val selected = candidate.id in selectedIds
                        val candidateIndex = candidates.indexOf(candidate)
                        val selectedIndex = selectedCandidates.indexOfFirst { it.id == candidate.id }
                        val colorIndex = if (selectedIndex >= 0) selectedIndex else candidateIndex
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) BrandSoftBlue.copy(alpha = 0.22f) else Color.Transparent)
                                .clickable(enabled = candidate.series != null) { toggleCandidate(candidate) }
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (candidate.series == null) MutedText
                                        else scenarioComparisonColor(colorIndex)
                                    )
                            )
                            Spacer(modifier = Modifier.width(9.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    candidate.name,
                                    color = if (candidate.series == null) MutedText else TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    if (candidate.series == null) "계산 결과 없음" else scenarioComparisonTypeCashFlowLabel(type),
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (selected) {
                                Text("선택", color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                            }
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))
    if (selectedSeries.size < 2) {
        BacktestCard {
            Text("비교할 시나리오를 2개 이상 선택해 주세요.", color = TextSecondary, fontSize = 14.sp)
        }
        return
    }

    ScenarioComparisonSummaryCard(selectedSeries)
    Spacer(modifier = Modifier.height(14.dp))
    BacktestCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionTitle("연차별 비교")
            Spacer(modifier = Modifier.weight(1f))
            Text("20년", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        SegmentedLabels(
            labels = listOf("월 현금흐름", "총 자산"),
            selectedIndex = metricIndex,
            onSelect = {
                metricIndex = it
                saveScenarioComparisonMetric(context, it)
            }
        )
        Spacer(modifier = Modifier.height(14.dp))
        ScenarioComparisonChart(selectedSeries, metricIndex)
    }
    Spacer(modifier = Modifier.height(14.dp))
    ScenarioComparisonTable(selectedSeries, metricIndex)
}

@Composable
private fun ScenarioComparisonSummaryCard(
    series: List<ScenarioComparisonSeries>
) {
    val summaries = series.mapNotNull { scenario ->
        ScenarioComparisonEngine.summarize(scenario)?.let { scenario to it }
    }
    BacktestCard {
        SectionTitle("핵심 비교")
        Spacer(modifier = Modifier.height(4.dp))
        summaries.forEachIndexed { index, (scenario, summary) ->
            val candidateIndex = series.indexOfFirst { it.id == scenario.id }.coerceAtLeast(0)
            if (index > 0) {
                Spacer(modifier = Modifier.height(14.dp))
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LineColor))
                Spacer(modifier = Modifier.height(14.dp))
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(scenarioComparisonColor(candidateIndex)))
                Spacer(modifier = Modifier.width(8.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(scenario.name, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    Text(
                        scenarioComparisonTypeLabel(scenario.type),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(formatEokWon(summary.finalAssetWon), color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(11.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ScenarioComparisonMetric("1년 월", formatWon(summary.yearOneMonthlyCashFlowWon), Modifier.weight(1f))
                ScenarioComparisonMetric("10년 월", formatWon(summary.yearTenMonthlyCashFlowWon), Modifier.weight(1f))
                ScenarioComparisonMetric("20년 월", formatWon(summary.yearTwentyMonthlyCashFlowWon), Modifier.weight(1f))
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ScenarioComparisonMetric("20년 최종 자산", formatWon(summary.finalAssetWon), Modifier.weight(1f))
                ScenarioComparisonMetric("20년 누적 현금흐름", formatWon(summary.cumulativeCashFlowWon), Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun ScenarioComparisonMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(3.dp))
        Text(
            value,
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun ScenarioComparisonChart(
    series: List<ScenarioComparisonSeries>,
    metricIndex: Int
) {
    val years = series.flatMap { it.points }.map { it.year }.distinct().sorted()
    if (years.isEmpty()) return
    var selectedYear by remember(series.map { it.id }, metricIndex) { mutableIntStateOf(years.last()) }
    val maxValue = series.flatMap { scenario ->
        scenario.points.map { point ->
            if (metricIndex == 0) point.monthlyCashFlowWon else point.totalAssetWon
        }
    }.maxOrNull()?.coerceAtLeast(1L) ?: 1L

    Column(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clipToBounds()
                .pointerInput(years, metricIndex) {
                    detectTapGestures { offset ->
                        selectedYear = scenarioComparisonYearForOffset(offset.x, size.width.toFloat(), years)
                    }
                }
        ) {
            val left = 68f
            val right = size.width - 10f
            val top = 16f
            val bottom = size.height - 34f
            val chartWidth = (right - left).coerceAtLeast(1f)
            val chartHeight = (bottom - top).coerceAtLeast(1f)
            val labelPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = TextSecondary.toArgb()
                textSize = 18f
                textAlign = android.graphics.Paint.Align.RIGHT
            }
            val yearPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = TextSecondary.toArgb()
                textSize = 18f
                textAlign = android.graphics.Paint.Align.CENTER
            }

            repeat(4) { index ->
                val ratio = index / 3f
                val y = top + chartHeight * ratio
                val value = (maxValue * (1f - ratio)).roundToLong()
                drawLine(LineColor, Offset(left, y), Offset(right, y), strokeWidth = 1.2f)
                drawContext.canvas.nativeCanvas.drawText(compactWonAxis(value), left - 8f, y + 6f, labelPaint)
            }

            val yearRange = (years.last() - years.first()).coerceAtLeast(1)
            series.forEachIndexed { seriesIndex, scenario ->
                val path = Path()
                scenario.points.sortedBy { it.year }.forEachIndexed { index, point ->
                    val x = left + chartWidth * (point.year - years.first()).toFloat() / yearRange.toFloat()
                    val value = if (metricIndex == 0) point.monthlyCashFlowWon else point.totalAssetWon
                    val y = bottom - chartHeight * value.toFloat() / maxValue.toFloat()
                    if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(
                    path,
                    scenarioComparisonColor(seriesIndex),
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f, cap = StrokeCap.Round)
                )
            }

            listOf(years.first(), years.getOrElse(9) { years.last() }, years.last()).distinct().forEach { year ->
                val x = left + chartWidth * (year - years.first()).toFloat() / yearRange.toFloat()
                drawContext.canvas.nativeCanvas.drawText("${year}년", x, bottom + 25f, yearPaint)
            }

            val selectedX = left + chartWidth * (selectedYear - years.first()).toFloat() / yearRange.toFloat()
            drawLine(TextPrimary.copy(alpha = 0.45f), Offset(selectedX, top), Offset(selectedX, bottom), strokeWidth = 2f)
            series.forEachIndexed { seriesIndex, scenario ->
                ScenarioComparisonEngine.pointAt(scenario, selectedYear)?.let { point ->
                    val value = if (metricIndex == 0) point.monthlyCashFlowWon else point.totalAssetWon
                    val y = bottom - chartHeight * value.toFloat() / maxValue.toFloat()
                    drawCircle(PanelColor, radius = 7f, center = Offset(selectedX, y))
                    drawCircle(scenarioComparisonColor(seriesIndex), radius = 5f, center = Offset(selectedX, y))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("${selectedYear}년차", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(5.dp))
        series.forEach { scenario ->
            val point = ScenarioComparisonEngine.pointAt(scenario, selectedYear) ?: return@forEach
            val candidateIndex = series.indexOfFirst { it.id == scenario.id }.coerceAtLeast(0)
            val value = if (metricIndex == 0) point.monthlyCashFlowWon else point.totalAssetWon
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(scenarioComparisonColor(candidateIndex)))
                Spacer(modifier = Modifier.width(7.dp))
                Text(scenario.name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(formatWon(value), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

private fun scenarioComparisonYearForOffset(x: Float, width: Float, years: List<Int>): Int {
    if (years.size <= 1) return years.firstOrNull() ?: 1
    val left = 68f
    val right = (width - 10f).coerceAtLeast(left + 1f)
    val ratio = ((x - left) / (right - left)).coerceIn(0f, 1f)
    val index = (ratio * (years.size - 1)).roundToInt().coerceIn(years.indices)
    return years[index]
}

private fun compactWonAxis(value: Long): String =
    if (DisplayCurrency == CurrencyMode.USD) {
        CurrencyDisplayFormatter.formatCompact(value, DisplayUsdKrw, true)
    } else {
        when {
            value >= 1_000_000L -> "${formatDecimal(value / 100_000_000.0)}억"
            value >= 10_000L -> "${(value / 10_000.0).roundToInt()}만"
            else -> NumberFormat.getNumberInstance(Locale.KOREA).format(value)
        }
    }

@Composable
private fun ScenarioComparisonTable(series: List<ScenarioComparisonSeries>, metricIndex: Int) {
    val years = series.flatMap { it.points }.map { it.year }.distinct().sorted()
    BacktestCard {
        SectionTitle(if (metricIndex == 0) "연차별 월 현금흐름" else "연차별 총 자산")
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
            val tableWidth = (58 + series.size * 132).dp
            Row(
                modifier = Modifier
                    .width(tableWidth)
                    .clip(RoundedCornerShape(8.dp))
                    .background(SoftSurface)
                    .padding(vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("연차", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(58.dp), textAlign = TextAlign.Center)
                series.forEach { scenario ->
                    Text(
                        scenario.name,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.width(132.dp).padding(horizontal = 6.dp),
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            years.forEach { year ->
                Row(
                    modifier = Modifier.width(tableWidth).padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("${year}년", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(58.dp), textAlign = TextAlign.Center)
                    series.forEach { scenario ->
                        val point = ScenarioComparisonEngine.pointAt(scenario, year)
                        val value = point?.let { if (metricIndex == 0) it.monthlyCashFlowWon else it.totalAssetWon }
                        Text(
                            value?.let(::formatWon) ?: "-",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.width(132.dp).padding(horizontal = 6.dp),
                            textAlign = TextAlign.End,
                            maxLines = 1
                        )
                    }
                }
                Box(modifier = Modifier.width(tableWidth).height(1.dp).background(LineColor))
            }
        }
    }
}

@Composable
private fun RetirementSuccessContent() {
    val context = LocalContext.current
    val candidates = remember(context) { scenarioComparisonCandidates(context) }
    val availableCandidates = candidates.filter { it.series != null }
    val selectedIds = remember(candidates) {
        val availableIds = availableCandidates.map { it.id }.toSet()
        val restored = loadRetirementSuccessSelection(context).filter { it in availableIds }
        mutableStateListOf<String>().also { selected ->
            selected.addAll((restored.ifEmpty { availableCandidates.take(1).map { it.id } }).take(4))
        }
    }
    val selectedCandidates = candidates.filter { it.id in selectedIds && it.series != null }
    val selectedSeries = selectedCandidates.mapNotNull { it.series }
    var analyses by remember { mutableStateOf<List<RetirementScenarioSuccessAnalysis>>(emptyList()) }
    var volatilityFailures by remember { mutableStateOf<List<RetirementVolatilityFailure>>(emptyList()) }
    var isCalculating by remember { mutableStateOf(false) }

    LaunchedEffect(selectedSeries.map { it.id }) {
        if (selectedSeries.isEmpty()) {
            analyses = emptyList()
            volatilityFailures = emptyList()
            isCalculating = false
        } else {
            isCalculating = true
            val calculationResults = withContext(Dispatchers.IO) {
                selectedCandidates.map { candidate ->
                    val volatility = loadHistoricalVolatility(context, candidate)
                    candidate to volatility?.let { estimate ->
                        RetirementScenarioSuccessEngine.calculate(
                            series = candidate.series!!,
                            historicalVolatility = estimate
                        )
                    }
                }
            }
            analyses = calculationResults.mapNotNull { it.second }
            volatilityFailures = calculationResults.mapNotNull { (candidate, analysis) ->
                if (analysis != null) null else RetirementVolatilityFailure(
                    scenarioId = candidate.id,
                    scenarioName = candidate.name,
                    tickers = candidate.historicalAllocations.map { it.ticker }.filter { it.isNotBlank() }.distinct()
                )
            }
            isCalculating = false
        }
    }

    fun toggleCandidate(candidate: ScenarioComparisonCandidate) {
        if (candidate.series == null) return
        if (candidate.id in selectedIds) {
            selectedIds.remove(candidate.id)
        } else if (selectedIds.size >= 4) {
            Toast.makeText(context, "은퇴 시나리오는 최대 4개까지 선택할 수 있어요.", Toast.LENGTH_SHORT).show()
            return
        } else {
            selectedIds.add(candidate.id)
        }
        saveRetirementSuccessSelection(context, selectedIds.toSet())
    }

    BacktestCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionTitle("은퇴 시나리오")
            Spacer(modifier = Modifier.weight(1f))
            Text("${selectedIds.size}/4 선택", color = BrandGreen, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "저장된 배당·자가배당·3자산 분배 결과를 선택하면 5,000개 경로를 자동 계산합니다.",
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        if (candidates.isEmpty()) {
            Text("계산할 저장 시나리오가 없습니다.", color = MutedText, fontSize = 14.sp)
        } else {
            ScenarioComparisonType.entries.forEach { type ->
                val typedCandidates = candidates.filter { it.type == type }
                if (typedCandidates.isNotEmpty()) {
                    Text(
                        scenarioComparisonTypeLabel(type),
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                    )
                    typedCandidates.forEach { candidate ->
                        val selected = candidate.id in selectedIds
                        val selectedIndex = selectedCandidates.indexOfFirst { it.id == candidate.id }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (selected) BrandSoftBlue.copy(alpha = 0.22f) else Color.Transparent)
                                .clickable(enabled = candidate.series != null) { toggleCandidate(candidate) }
                                .padding(horizontal = 10.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (candidate.series == null) MutedText
                                        else scenarioComparisonColor(
                                            if (selectedIndex >= 0) selectedIndex else candidates.indexOf(candidate)
                                        )
                                    )
                            )
                            Spacer(modifier = Modifier.width(9.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    candidate.name,
                                    color = if (candidate.series == null) MutedText else TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    if (candidate.series == null) "계산 결과 없음" else scenarioComparisonTypeCashFlowLabel(type),
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            if (selected) Text("선택", color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))
    when {
        selectedSeries.isEmpty() -> BacktestCard {
            Text("성공확률을 계산할 시나리오를 선택해 주세요.", color = TextSecondary, fontSize = 14.sp)
        }
        isCalculating -> BacktestCard {
            Text("선택한 시나리오의 성공확률을 계산하고 있어요.", color = TextSecondary, fontSize = 14.sp)
        }
        else -> {
            volatilityFailures.forEachIndexed { index, failure ->
                if (index > 0) Spacer(modifier = Modifier.height(10.dp))
                RetirementVolatilityFailureCard(failure)
            }
            if (volatilityFailures.isNotEmpty() && analyses.isNotEmpty()) Spacer(modifier = Modifier.height(10.dp))
            analyses.forEachIndexed { index, analysis ->
                if (index > 0) Spacer(modifier = Modifier.height(10.dp))
                RetirementScenarioResultCard(analysis, index)
            }
            if (analyses.isNotEmpty()) {
                Spacer(modifier = Modifier.height(14.dp))
                BacktestCard {
                    SectionTitle("연차별 은퇴 생존확률")
                    Spacer(modifier = Modifier.height(12.dp))
                    RetirementScenarioSurvivalChart(analyses)
                }
                Spacer(modifier = Modifier.height(14.dp))
                RetirementScenarioSuccessTable(analyses)
            }
        }
    }
}

@Composable
private fun RetirementVolatilityFailureCard(failure: RetirementVolatilityFailure) {
    BacktestCard {
        Text(failure.scenarioName, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "과거 가격 데이터가 부족해 성공확률을 계산하지 않았습니다.",
            color = PositiveRed,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "필요 종목: ${failure.tickers.ifEmpty { listOf("종목 정보 없음") }.joinToString(" · ")} · 최소 월수익률 12개",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
private fun RetirementScenarioResultCard(analysis: RetirementScenarioSuccessAnalysis, colorIndex: Int) {
    val result = analysis.result
    val volatility = analysis.historicalVolatility
    val resultColor = when {
        result.successRatePercent >= 80.0 -> BrandGreen
        result.successRatePercent >= 60.0 -> CashOrange
        else -> PositiveRed
    }
    BacktestCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(scenarioComparisonColor(colorIndex)))
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(analysis.scenarioName, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(scenarioComparisonTypeLabel(analysis.type), color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Text("${formatDecimal(result.successRatePercent)}%", color = resultColor, fontSize = 27.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "${NumberFormat.getNumberInstance(Locale.KOREA).format(result.simulations)}개 경로 중 " +
                "${NumberFormat.getNumberInstance(Locale.KOREA).format(result.successfulPaths)}개 생존",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RetirementSuccessMetric("기준 초기자산", formatWon(analysis.inferredInitialAssetWon), Modifier.weight(1f))
            RetirementSuccessMetric("최종 중앙값", formatWon(result.medianFinalAssetWon), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RetirementSuccessMetric("역산 연수익률", "${formatDecimal(analysis.expectedAnnualReturnPercent)}%", Modifier.weight(1f))
            RetirementSuccessMetric("과거 연환산 변동성", "${formatDecimal(volatility.annualizedVolatilityPercent)}%", Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "${volatility.tickers.joinToString(" · ")} 조정종가 · " +
                "${volatility.startMonth.toString().replace('-', '.')}~${volatility.endMonth.toString().replace('-', '.')} · " +
                "월수익률 ${volatility.monthlyObservationCount}개",
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 17.sp
        )
    }
}

@Composable
private fun RetirementScenarioSurvivalChart(analyses: List<RetirementScenarioSuccessAnalysis>) {
    val years = analyses.flatMap { it.result.rows }.map { it.year }.distinct().sorted()
    if (years.isEmpty()) return
    var selectedYear by remember(analyses.map { it.scenarioId }) { mutableIntStateOf(years.last()) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .clipToBounds()
                .pointerInput(years) {
                    detectTapGestures { offset ->
                        selectedYear = scenarioComparisonYearForOffset(offset.x, size.width.toFloat(), years)
                    }
                }
        ) {
            val left = 58f
            val right = size.width - 8f
            val top = 14f
            val bottom = size.height - 34f
            val chartWidth = (right - left).coerceAtLeast(1f)
            val chartHeight = (bottom - top).coerceAtLeast(1f)
            val yearRange = (years.last() - years.first()).coerceAtLeast(1)
            val labelPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = TextSecondary.toArgb()
                textSize = 18f
                textAlign = android.graphics.Paint.Align.RIGHT
            }
            val yearPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = TextSecondary.toArgb()
                textSize = 18f
                textAlign = android.graphics.Paint.Align.CENTER
            }
            listOf(100, 75, 50, 25, 0).forEach { percent ->
                val y = bottom - chartHeight * percent / 100f
                drawLine(LineColor, Offset(left, y), Offset(right, y), strokeWidth = 1.2f)
                drawContext.canvas.nativeCanvas.drawText("$percent%", left - 7f, y + 6f, labelPaint)
            }
            analyses.forEachIndexed { index, analysis ->
                val path = Path()
                analysis.result.rows.forEachIndexed { pointIndex, row ->
                    val x = left + chartWidth * (row.year - years.first()).toFloat() / yearRange.toFloat()
                    val y = bottom - chartHeight * row.survivalRatePercent.toFloat() / 100f
                    if (pointIndex == 0) path.moveTo(x, y) else path.lineTo(x, y)
                }
                drawPath(path, scenarioComparisonColor(index), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 4f, cap = StrokeCap.Round))
            }
            listOf(years.first(), years.getOrElse(9) { years.last() }, years.last()).distinct().forEach { year ->
                val x = left + chartWidth * (year - years.first()).toFloat() / yearRange.toFloat()
                drawContext.canvas.nativeCanvas.drawText("${year}년", x, bottom + 25f, yearPaint)
            }
            val selectedX = left + chartWidth * (selectedYear - years.first()).toFloat() / yearRange.toFloat()
            drawLine(TextPrimary.copy(alpha = 0.45f), Offset(selectedX, top), Offset(selectedX, bottom), strokeWidth = 2f)
            analyses.forEachIndexed { index, analysis ->
                analysis.result.rows.firstOrNull { it.year == selectedYear }?.let { row ->
                    val y = bottom - chartHeight * row.survivalRatePercent.toFloat() / 100f
                    drawCircle(PanelColor, radius = 7f, center = Offset(selectedX, y))
                    drawCircle(scenarioComparisonColor(index), radius = 5f, center = Offset(selectedX, y))
                }
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("${selectedYear}년차", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        analyses.forEachIndexed { index, analysis ->
            val row = analysis.result.rows.firstOrNull { it.year == selectedYear } ?: return@forEachIndexed
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(scenarioComparisonColor(index)))
                Spacer(modifier = Modifier.width(7.dp))
                Text(analysis.scenarioName, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text("${formatDecimal(row.survivalRatePercent)}%", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun RetirementScenarioSuccessTable(analyses: List<RetirementScenarioSuccessAnalysis>) {
    val years = analyses.flatMap { it.result.rows }.map { it.year }.distinct().sorted()
    BacktestCard {
        SectionTitle("연차별 성공확률")
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
            val tableWidth = (58 + analyses.size * 132).dp
            Row(modifier = Modifier.width(tableWidth).clip(RoundedCornerShape(8.dp)).background(SoftSurface).padding(vertical = 9.dp)) {
                Text("연차", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(58.dp), textAlign = TextAlign.Center)
                analyses.forEach { analysis ->
                    Text(analysis.scenarioName, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(132.dp).padding(horizontal = 6.dp), textAlign = TextAlign.End, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            }
            years.forEach { year ->
                Row(modifier = Modifier.width(tableWidth).padding(vertical = 10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text("${year}년", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(58.dp), textAlign = TextAlign.Center)
                    analyses.forEach { analysis ->
                        val rate = analysis.result.rows.firstOrNull { it.year == year }?.survivalRatePercent
                        Text(rate?.let { "${formatDecimal(it)}%" } ?: "-", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(132.dp).padding(horizontal = 6.dp), textAlign = TextAlign.End, maxLines = 1)
                    }
                }
                Box(modifier = Modifier.width(tableWidth).height(1.dp).background(LineColor))
            }
        }
    }
}

@Suppress("unused")
@Composable
private fun LegacyRetirementSuccessContent() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val cached = remember(context) { loadRetirementSuccessSnapshot(context) ?: RetirementSuccessSnapshot() }
    var initialAssetEok by remember { mutableStateOf(cached.initialAssetEok) }
    var monthlySpendingMan by remember { mutableStateOf(cached.monthlySpendingMan) }
    var expectedReturn by remember { mutableStateOf(cached.expectedReturn) }
    var volatility by remember { mutableStateOf(cached.volatility) }
    var inflation by remember { mutableStateOf(cached.inflation) }
    var years by remember { mutableStateOf(cached.years) }
    var simulations by remember { mutableIntStateOf(cached.simulations) }
    var result by remember { mutableStateOf(cached.result) }
    var isCalculating by remember { mutableStateOf(false) }
    val simulationOptions = listOf(1_000, 5_000, 10_000)

    fun invalidateResult() {
        result = null
    }

    fun calculate() {
        val initialAssetWon = eokInputToWon(initialAssetEok)
        val monthlySpendingWon = manInputToWon(monthlySpendingMan)
        val simulationYears = years.toIntOrNull()?.coerceIn(1, 60) ?: 30
        if (initialAssetWon <= 0L) {
            Toast.makeText(context, "초기 은퇴자산을 입력해 주세요.", Toast.LENGTH_SHORT).show()
            return
        }
        scope.launch {
            isCalculating = true
            val calculated = withContext(Dispatchers.Default) {
                RetirementSuccessEngine.calculate(
                    RetirementSuccessInput(
                        initialAssetWon = initialAssetWon,
                        monthlySpendingWon = monthlySpendingWon,
                        expectedAnnualReturnPercent = expectedReturn.toDoubleOrNull() ?: 7.0,
                        annualVolatilityPercent = volatility.toDoubleOrNull() ?: 15.0,
                        annualInflationPercent = inflation.toDoubleOrNull() ?: 2.5,
                        years = simulationYears,
                        simulations = simulations
                    )
                )
            }
            result = calculated
            saveRetirementSuccessSnapshot(
                context,
                RetirementSuccessSnapshot(
                    initialAssetEok = initialAssetEok,
                    monthlySpendingMan = monthlySpendingMan,
                    expectedReturn = expectedReturn,
                    volatility = volatility,
                    inflation = inflation,
                    years = simulationYears.toString(),
                    simulations = simulations,
                    result = calculated
                )
            )
            isCalculating = false
        }
    }

    BacktestCard {
        SectionTitle("은퇴 시뮬레이션 조건")
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "매년 수익 반영 후 물가에 따라 늘어난 생활비를 차감합니다.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        FourAssetDecimalInputRow("초기 은퇴자산", initialAssetEok, "억원") {
            initialAssetEok = it
            invalidateResult()
        }
        FourAssetDecimalInputRow("월 생활비", monthlySpendingMan, "만원") {
            monthlySpendingMan = it
            invalidateResult()
        }
        FourAssetDecimalInputRow("기대수익률", expectedReturn, "%") {
            expectedReturn = it
            invalidateResult()
        }
        FourAssetDecimalInputRow("연 변동성", volatility, "%") {
            volatility = it
            invalidateResult()
        }
        FourAssetDecimalInputRow("물가상승률", inflation, "%") {
            inflation = it
            invalidateResult()
        }
        FourAssetDecimalInputRow("은퇴 기간", years, "년") {
            years = it
            invalidateResult()
        }
        Spacer(modifier = Modifier.height(14.dp))
        Text("시뮬레이션 횟수", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(7.dp))
        SegmentedLabels(
            labels = listOf("1천회", "5천회", "1만회"),
            selectedIndex = simulationOptions.indexOf(simulations).coerceAtLeast(1),
            onSelect = {
                simulations = simulationOptions[it]
                invalidateResult()
            }
        )
        Spacer(modifier = Modifier.height(18.dp))
        PrimaryActionButton(
            text = if (isCalculating) "계산 중..." else "성공확률 계산",
            enabled = !isCalculating,
            onClick = ::calculate
        )
    }

    result?.let { calculated ->
        Spacer(modifier = Modifier.height(14.dp))
        RetirementSuccessResultCard(calculated)
        Spacer(modifier = Modifier.height(14.dp))
        BacktestCard {
            SectionTitle("연차별 은퇴 생존확률")
            Spacer(modifier = Modifier.height(12.dp))
            RetirementSurvivalChart(calculated.rows)
        }
        Spacer(modifier = Modifier.height(14.dp))
        RetirementSuccessTable(calculated.rows)
    }
}

@Composable
private fun RetirementSuccessResultCard(result: RetirementSuccessResult) {
    val resultColor = when {
        result.successRatePercent >= 80.0 -> BrandGreen
        result.successRatePercent >= 60.0 -> CashOrange
        else -> PositiveRed
    }
    BacktestCard {
        Text("은퇴 성공확률", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            "${formatDecimal(result.successRatePercent)}%",
            color = resultColor,
            fontSize = 38.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Text(
            "${NumberFormat.getNumberInstance(Locale.KOREA).format(result.simulations)}개 경로 중 " +
                "${NumberFormat.getNumberInstance(Locale.KOREA).format(result.successfulPaths)}개 생존",
            color = TextSecondary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RetirementSuccessMetric("하위 10%", formatWon(result.lowerFinalAssetWon), Modifier.weight(1f))
            RetirementSuccessMetric("최종 중앙값", formatWon(result.medianFinalAssetWon), Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            RetirementSuccessMetric("상위 10%", formatWon(result.upperFinalAssetWon), Modifier.weight(1f))
            RetirementSuccessMetric(
                "실패 중앙 연차",
                result.medianDepletionYear?.let { "${it}년" } ?: "없음",
                Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun RetirementSuccessMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier.clip(RoundedCornerShape(8.dp)).background(SoftSurface).padding(12.dp)) {
        Text(label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.ExtraBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RetirementSurvivalChart(rows: List<RetirementSuccessYear>) {
    if (rows.isEmpty()) return
    var selectedIndex by remember(rows.size) { mutableIntStateOf(0) }
    val scrollState = rememberScrollState()
    Column(modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.fillMaxWidth().horizontalScroll(scrollState)) {
            Canvas(
                modifier = Modifier
                    .width((68 + rows.size * 36).dp)
                    .height(230.dp)
                    .pointerInput(rows.size) {
                        detectTapGestures { offset ->
                            val left = 58f
                            val right = (size.width - 8f).coerceAtLeast(left + 1f)
                            val groupWidth = (right - left) / rows.size
                            selectedIndex = ((offset.x - left) / groupWidth).toInt().coerceIn(rows.indices)
                        }
                    }
            ) {
                val left = 58f
                val right = size.width - 8f
                val top = 14f
                val bottom = size.height - 34f
                val chartWidth = (right - left).coerceAtLeast(1f)
                val chartHeight = (bottom - top).coerceAtLeast(1f)
                val groupWidth = chartWidth / rows.size
                val labelPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                    color = TextSecondary.toArgb()
                    textSize = 18f
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
                val yearPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                    color = TextSecondary.toArgb()
                    textSize = 18f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
                listOf(100, 75, 50, 25, 0).forEach { percent ->
                    val y = bottom - chartHeight * percent / 100f
                    drawLine(LineColor, Offset(left, y), Offset(right, y), strokeWidth = 1.2f)
                    drawContext.canvas.nativeCanvas.drawText("$percent%", left - 7f, y + 6f, labelPaint)
                }
                rows.forEachIndexed { index, row ->
                    val barWidth = groupWidth * 0.62f
                    val x = left + groupWidth * index + (groupWidth - barWidth) / 2f
                    val barHeight = chartHeight * row.survivalRatePercent.toFloat() / 100f
                    drawRect(
                        color = if (index == selectedIndex) PositiveRed else BrandGreen,
                        topLeft = Offset(x, bottom - barHeight),
                        size = androidx.compose.ui.geometry.Size(barWidth, barHeight)
                    )
                    drawContext.canvas.nativeCanvas.drawText(
                        "${row.year}년",
                        x + barWidth / 2f,
                        bottom + 25f,
                        yearPaint
                    )
                }
            }
        }
        val selected = rows[selectedIndex]
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${selected.year}년차", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "생존 ${formatDecimal(selected.survivalRatePercent)}%",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
        Spacer(modifier = Modifier.height(5.dp))
        Text(
            "하위 10% ${formatWon(selected.lowerAssetWon)} · 중앙값 ${formatWon(selected.medianAssetWon)} · 상위 10% ${formatWon(selected.upperAssetWon)}",
            color = TextSecondary,
            fontSize = 11.sp,
            lineHeight = 17.sp
        )
    }
}

@Composable
private fun RetirementSuccessTable(rows: List<RetirementSuccessYear>) {
    BacktestCard {
        SectionTitle("연차별 확률 명세")
        Spacer(modifier = Modifier.height(10.dp))
        Column(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
            val tableWidth = 520.dp
            Row(
                modifier = Modifier.width(tableWidth).clip(RoundedCornerShape(8.dp)).background(SoftSurface).padding(vertical = 9.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RetirementTableCell("연차", 52.dp, TextAlign.Center)
                RetirementTableCell("생존확률", 78.dp, TextAlign.End)
                RetirementTableCell("하위 10%", 130.dp, TextAlign.End)
                RetirementTableCell("중앙값", 130.dp, TextAlign.End)
                RetirementTableCell("상위 10%", 130.dp, TextAlign.End)
            }
            rows.forEach { row ->
                Row(
                    modifier = Modifier.width(tableWidth).padding(vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RetirementTableCell("${row.year}년", 52.dp, TextAlign.Center, TextPrimary)
                    RetirementTableCell("${formatDecimal(row.survivalRatePercent)}%", 78.dp, TextAlign.End, TextPrimary)
                    RetirementTableCell(formatWon(row.lowerAssetWon), 130.dp, TextAlign.End, TextPrimary)
                    RetirementTableCell(formatWon(row.medianAssetWon), 130.dp, TextAlign.End, TextPrimary)
                    RetirementTableCell(formatWon(row.upperAssetWon), 130.dp, TextAlign.End, TextPrimary)
                }
                Box(modifier = Modifier.width(tableWidth).height(1.dp).background(LineColor))
            }
        }
    }
}

@Composable
private fun RetirementTableCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    textAlign: TextAlign,
    color: Color = TextSecondary
) {
    Text(
        text,
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.width(width).padding(horizontal = 6.dp),
        textAlign = textAlign,
        maxLines = 1
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun BacktestScreen(
    accounts: List<AccountUi>,
    settings: AppSettings,
    usdKrw: Double,
    onLockedExit: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    BackHandler(enabled = onLockedExit != null) { onLockedExit?.invoke() }
    if (savedBacktestPresetsCache.isEmpty()) {
        savedBacktestPresetsCache.addAll(loadSavedBacktestPresets(context))
    }
    val cachedPreset = remember(context) {
        (lastBacktestPresetCache ?: loadLastBacktestPreset(context)).also {
            lastBacktestPresetCache = it
            lastBacktestResultCache = it?.result
        }
    }
    val presets = remember { mutableStateListOf<BacktestPreset>().also { it.addAll(savedBacktestPresetsCache) } }
    val assets = remember { mutableStateListOf<BacktestAssetUi>().also { cachedPreset?.assets?.let(it::addAll) } }
    var startYear by remember { mutableStateOf(cachedPreset?.startYear ?: "2010") }
    var endYear by remember { mutableStateOf(cachedPreset?.endYear ?: LocalDate.now().year.toString()) }
    var startingMoney by remember { mutableStateOf(cachedPreset?.startingMoney ?: "10000000") }
    var rebalanceEnabled by remember { mutableStateOf(cachedPreset?.rebalanceEnabled ?: true) }
    var rebalanceFrequency by remember { mutableStateOf(cachedPreset?.rebalanceFrequency ?: "매년") }
    var contributionEnabled by remember { mutableStateOf(cachedPreset?.contributionEnabled ?: false) }
    var contributionPeriod by remember { mutableStateOf(cachedPreset?.contributionPeriod ?: "월") }
    var contributionAmount by remember { mutableStateOf(cachedPreset?.contributionAmount ?: "1000000") }
    var dividendReinvest by remember { mutableStateOf(cachedPreset?.dividendReinvest ?: true) }
    var exchangeRateEnabled by remember { mutableStateOf(cachedPreset?.exchangeRateEnabled ?: true) }
    var result by remember { mutableStateOf(lastBacktestResultCache ?: cachedPreset?.result) }
    var showAssetSearch by remember { mutableStateOf(false) }
    var pendingAsset by remember { mutableStateOf<AssetOption?>(null) }
    var weightText by remember { mutableStateOf("10") }
    var deleteAsset by remember { mutableStateOf<BacktestAssetUi?>(null) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showLoadDialog by remember { mutableStateOf(false) }
    var presetName by remember { mutableStateOf("") }
    var deletePreset by remember { mutableStateOf<BacktestPreset?>(null) }
    var renamePreset by remember { mutableStateOf<BacktestPreset?>(null) }
    var isRunning by remember { mutableStateOf(false) }
    var selectedTool by remember { mutableStateOf("백테스트") }

    fun currentPreset(name: String = "마지막 백테스트", savedResult: BacktestResultUi? = result) = BacktestPreset(
        name = name,
        startYear = startYear,
        endYear = endYear,
        startingMoney = startingMoney,
        rebalanceEnabled = rebalanceEnabled,
        rebalanceFrequency = rebalanceFrequency,
        contributionEnabled = contributionEnabled,
        contributionPeriod = contributionPeriod,
        contributionAmount = contributionAmount,
        dividendReinvest = dividendReinvest,
        exchangeRateEnabled = exchangeRateEnabled,
        assets = assets.toList(),
        result = savedResult
    )

    fun saveNamedPreset(name: String) {
        val preset = currentPreset(name, result)
        presets.removeAll { it.name == preset.name }
        savedBacktestPresetsCache.removeAll { it.name == preset.name }
        presets.add(preset)
        savedBacktestPresetsCache.add(preset)
        saveSavedBacktestPresets(context, savedBacktestPresetsCache)
        lastBacktestPresetCache = preset
        lastBacktestResultCache = preset.result
        saveLastBacktestPreset(context, preset)
        presetName = ""
        showSaveDialog = false
        Toast.makeText(context, "백테스트를 저장했어요.", Toast.LENGTH_SHORT).show()
    }

    fun renameSavedPreset(preset: BacktestPreset, newName: String) {
        val renamed = preset.copy(name = newName)
        val listIndex = presets.indexOfFirst { it.name == preset.name }
        val cacheIndex = savedBacktestPresetsCache.indexOfFirst { it.name == preset.name }
        if (listIndex >= 0) presets[listIndex] = renamed
        if (cacheIndex >= 0) savedBacktestPresetsCache[cacheIndex] = renamed
        saveSavedBacktestPresets(context, savedBacktestPresetsCache)
        if (lastBacktestPresetCache?.name == preset.name) {
            lastBacktestPresetCache = renamed
            saveLastBacktestPreset(context, renamed)
        }
        renamePreset = null
    }

    fun runBacktest() {
        val totalWeight = assets.sumOf { it.weight }
        if (kotlin.math.abs(totalWeight - 100.0) > 0.1) {
            Toast.makeText(context, "자산 비중 합계가 100%가 되어야 합니다.", Toast.LENGTH_SHORT).show()
            return
        }
        scope.launch {
            isRunning = true
            val calculated = calculateBacktestResult(
                context = context,
                settings = settings,
                startYear = startYear.toIntOrNull() ?: 2010,
                endYear = endYear.toIntOrNull() ?: LocalDate.now().year,
                startingMoney = startingMoney.filter { it.isDigit() }.toLongOrNull() ?: 10_000_000L,
                contributionEnabled = contributionEnabled,
                contributionPeriod = contributionPeriod,
                contributionAmount = contributionAmount.filter { it.isDigit() }.toLongOrNull() ?: 0L,
                dividendReinvest = dividendReinvest,
                exchangeRateEnabled = exchangeRateEnabled,
                rebalanceEnabled = rebalanceEnabled,
                rebalanceFrequency = rebalanceFrequency,
                assets = assets.toList()
            )
            result = calculated
            val latestPreset = currentPreset(savedResult = calculated)
            lastBacktestPresetCache = latestPreset
            lastBacktestResultCache = calculated
            saveLastBacktestPreset(context, latestPreset)
            isRunning = false
            if (!calculated.usedHistoricalData) {
                Toast.makeText(context, "과거데이터 조회가 부족해 샘플 경로로 계산했어요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    MainTabScreenColumn {
        if (onLockedExit != null) {
            Text(
                "‹  잠금 화면으로 돌아가기",
                color = BrandGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onLockedExit)
                    .padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }
        AppHeader(title = "시뮬레이터")
        BacktestToolSwitcher(selected = selectedTool) { selectedTool = it }
        Spacer(modifier = Modifier.height(18.dp))
        when (selectedTool) {
            "배당" -> DividendSimulationContent(accounts, usdKrw)
            "자가배당" -> SelfDividendPlaceholderContent()
            "3자산 분배" -> FourAssetDistributionContent()
            "시나리오 비교" -> ScenarioComparisonContent()
            "은퇴 성공확률" -> RetirementSuccessContent()
            else -> {
        BacktestCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SectionTitle("백테스트 설정")
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    "불러오기",
                    color = BrandGreen,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { showLoadDialog = true }
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            BacktestInputRow("시작년도", startYear, "년") { startYear = it }
            BacktestInputRow("종료년도", endYear, "년") { endYear = it }
            BacktestInputRow("시작금", startingMoney, "원") { startingMoney = it }
            Spacer(modifier = Modifier.height(14.dp))
            BacktestSwitchRow("리밸런싱", rebalanceEnabled) { rebalanceEnabled = it }
            if (rebalanceEnabled) {
                BacktestChoiceRow(listOf("매월", "매분기", "매반기", "매년"), rebalanceFrequency) { rebalanceFrequency = it }
            }
            Spacer(modifier = Modifier.height(14.dp))
            BacktestSwitchRow("적립금", contributionEnabled) { contributionEnabled = it }
            if (contributionEnabled) {
                BacktestChoiceRow(listOf("월", "년"), contributionPeriod) { contributionPeriod = it }
                BacktestInputRow("적립금액", contributionAmount, "원") { contributionAmount = it }
            }
            Spacer(modifier = Modifier.height(14.dp))
            BacktestSwitchRow("배당금 재투입", dividendReinvest) { dividendReinvest = it }
            BacktestSwitchRow("환율 반영", exchangeRateEnabled) { exchangeRateEnabled = it }
            Spacer(modifier = Modifier.height(20.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("자산 설정", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.weight(1f))
                Text("+ 자산 추가", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable { showAssetSearch = true })
            }
            Spacer(modifier = Modifier.height(10.dp))
            assets.forEach { asset ->
                BacktestAssetRow(asset = asset, onLongClick = { deleteAsset = asset })
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("비중 합계 ${String.format(Locale.US, "%.1f", assets.sumOf { it.weight })}%", color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(18.dp))
            PrimaryActionButton(
                if (isRunning) "조회 중..." else "백테스트 시작",
                enabled = assets.isNotEmpty() && !isRunning,
                onClick = ::runBacktest
            )
        }

        result?.let { backtest ->
            Spacer(modifier = Modifier.height(24.dp))
            BacktestPerformanceCard(backtest)
            Spacer(modifier = Modifier.height(18.dp))
            BacktestDisciplineCard(backtest)
            Spacer(modifier = Modifier.height(18.dp))
            SectionTitle("백테스트 그래프")
            BacktestGraphCard(
                title = "누적 금액 변동 그래프",
                values = backtest.monthlyAssets.map { it.toDouble() },
                labels = backtest.monthLabels,
                valueLabel = { formatWon(it.roundToLong()) },
                yAxisLabel = { formatAxisWon(it) },
                color = PositiveRed,
                chartType = BacktestChartType.ASSET,
                allocationDetails = backtest.monthlyAllocations
            )
            BacktestGraphCard(
                title = "손실 그래프",
                values = backtest.monthlyDrawdowns,
                labels = backtest.monthLabels,
                valueLabel = { formatPercent(it / 100.0) },
                yAxisLabel = { String.format(Locale.US, "%.0f%%", it) },
                color = NegativeBlue,
                chartType = BacktestChartType.DRAWDOWN,
                allocationDetails = backtest.monthlyAllocations
            )
            BacktestAnnualReturnCard(annualReturns = backtest.annualReturns)
            Spacer(modifier = Modifier.height(24.dp))
            SectionTitle("상세 보고서")
            BacktestReportTable(backtest.rows)
            Spacer(modifier = Modifier.height(20.dp))
            PrimaryActionButton("백테스트 저장", enabled = true) { showSaveDialog = true }
        }
            }
        }
    }

    if (showAssetSearch) {
        ModalBottomSheet(
            onDismissRequest = { showAssetSearch = false },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = PanelColor,
            modifier = Modifier.fillMaxHeight(0.92f),
            shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
        ) {
            ScaledApp(scale = 0.84f) {
                AssetSearchScreen(
                    settings = settings,
                    onClose = { showAssetSearch = false },
                    onSelect = {
                        pendingAsset = it
                        weightText = "10"
                        showAssetSearch = false
                    }
                )
            }
        }
    }

    pendingAsset?.let { asset ->
        AlertDialog(
            onDismissRequest = { pendingAsset = null },
            containerColor = PanelColor,
            title = { Text("비중 입력", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = {
                Column {
                    Text("${asset.name}의 목표 비중을 입력하세요.", color = TextSecondary, fontSize = 15.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedTextField(
                        value = weightText,
                        onValueChange = { weightText = it.filter { ch -> ch.isDigit() || ch == '.' } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        colors = appTextFieldColors(),
                        suffix = { Text("%", color = TextSecondary) }
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    val weight = weightText.toDoubleOrNull() ?: 0.0
                    if (weight > 0) {
                        assets.removeAll { it.ticker == asset.ticker }
                        assets.add(BacktestAssetUi(asset.ticker, asset.name, weight, asset.color))
                    }
                    pendingAsset = null
                }) { Text("추가", color = BrandGreen, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { pendingAsset = null }) { Text("취소", color = TextSecondary) }
            }
        )
    }

    deleteAsset?.let { asset ->
        AlertDialog(
            onDismissRequest = { deleteAsset = null },
            containerColor = PanelColor,
            title = { Text("자산을 삭제할까요?", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = { Text("${asset.name}을 백테스트 자산에서 삭제합니다.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    assets.remove(asset)
                    deleteAsset = null
                }) { Text("삭제", color = PositiveRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { deleteAsset = null }) { Text("취소", color = TextSecondary) } }
        )
    }

    if (showSaveDialog) {
        SimulationPresetSaveDialog(
            title = "백테스트 저장",
            name = presetName,
            existingNames = presets.map { it.name },
            onNameChange = { presetName = it },
            onDismiss = { showSaveDialog = false },
            onSaveNew = ::saveNamedPreset,
            onOverwrite = ::saveNamedPreset
        )
    }

    if (showLoadDialog) {
        AlertDialog(
            onDismissRequest = { showLoadDialog = false },
            containerColor = PanelColor,
            title = { Text("백테스트 불러오기", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = {
                Column {
                    if (presets.isEmpty()) {
                        Text("저장된 백테스트가 없습니다.", color = TextSecondary)
                    } else {
                        presets.forEach { preset ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {
                                    startYear = preset.startYear
                                    endYear = preset.endYear
                                    startingMoney = preset.startingMoney
                                    rebalanceEnabled = preset.rebalanceEnabled
                                    rebalanceFrequency = preset.rebalanceFrequency
                                    contributionEnabled = preset.contributionEnabled
                                    contributionPeriod = preset.contributionPeriod
                                    contributionAmount = preset.contributionAmount
                                    dividendReinvest = preset.dividendReinvest
                                    exchangeRateEnabled = preset.exchangeRateEnabled
                                    assets.clear()
                                    assets.addAll(preset.assets)
                                    result = preset.result
                                    lastBacktestPresetCache = preset
                                    lastBacktestResultCache = preset.result
                                    saveLastBacktestPreset(context, preset)
                                    showLoadDialog = false
                                        },
                                        onLongClick = {
                                            showLoadDialog = false
                                            renamePreset = preset
                                        }
                                    )
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(preset.name, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.weight(1f))
                                Text("불러오기", color = BrandGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = { TextButton(onClick = { showLoadDialog = false }) { Text("닫기", color = TextSecondary) } }
        )
    }

    renamePreset?.let { preset ->
        SimulationPresetRenameDialog(
            currentName = preset.name,
            existingNames = presets.map { it.name },
            onDismiss = { renamePreset = null },
            onRename = { renameSavedPreset(preset, it) },
            onDelete = {
                renamePreset = null
                deletePreset = preset
            }
        )
    }

    deletePreset?.let { preset ->
        AlertDialog(
            onDismissRequest = { deletePreset = null },
            containerColor = PanelColor,
            title = { Text("백테스트를 삭제할까요?", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = { Text("${preset.name} 항목을 삭제합니다.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    presets.removeAll { it.name == preset.name }
                    savedBacktestPresetsCache.removeAll { it.name == preset.name }
                    saveSavedBacktestPresets(context, savedBacktestPresetsCache)
                    deletePreset = null
                }) { Text("삭제", color = PositiveRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { deletePreset = null }) { Text("취소", color = TextSecondary) }
            }
        )
    }

}

@Composable
private fun SelfDividendPlaceholderContent() {
    val context = LocalContext.current
    if (savedSelfDividendPresetsCache.isEmpty()) {
        savedSelfDividendPresetsCache.addAll(loadSavedSelfDividendPresets(context))
    }
    val cachedPreset = remember(context) {
        (lastSelfDividendPresetCache ?: loadLastSelfDividendPreset(context)).also {
            lastSelfDividendPresetCache = it
        }
    }
    val savedPresets = remember {
        mutableStateListOf<SelfDividendPreset>().also { it.addAll(savedSelfDividendPresetsCache) }
    }
    val selectedAssets = remember {
        mutableStateListOf<SelfDividendAssetUi>().also { list ->
            cachedPreset?.assets?.let(list::addAll)
        }
    }
    var showAssetPicker by remember { mutableStateOf(false) }
    var projectionRows by remember { mutableStateOf(cachedPreset?.result ?: emptyList()) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showLoadDialog by remember { mutableStateOf(false) }
    var presetName by remember { mutableStateOf("") }
    var deletePreset by remember { mutableStateOf<SelfDividendPreset?>(null) }
    var renamePreset by remember { mutableStateOf<SelfDividendPreset?>(null) }
    val assetOptions = remember(context) { manualAssetOptions(context) }

    fun updateAsset(index: Int, next: SelfDividendAssetUi) {
        if (index in selectedAssets.indices) selectedAssets[index] = next
        projectionRows = emptyList()
    }

    fun currentPreset(name: String = "마지막 자가배당") = SelfDividendPreset(
        name = name,
        assets = selectedAssets.toList(),
        result = projectionRows
    )

    fun saveNamedPreset(name: String) {
        val preset = currentPreset(name)
        savedPresets.removeAll { it.name == preset.name }
        savedSelfDividendPresetsCache.removeAll { it.name == preset.name }
        savedPresets.add(preset)
        savedSelfDividendPresetsCache.add(preset)
        saveSavedSelfDividendPresets(context, savedSelfDividendPresetsCache)
        lastSelfDividendPresetCache = preset
        saveLastSelfDividendPreset(context, preset)
        presetName = ""
        showSaveDialog = false
        Toast.makeText(context, "자가배당을 저장했어요.", Toast.LENGTH_SHORT).show()
    }

    fun renameSavedPreset(preset: SelfDividendPreset, newName: String) {
        val renamed = preset.copy(name = newName)
        val listIndex = savedPresets.indexOfFirst { it.name == preset.name }
        val cacheIndex = savedSelfDividendPresetsCache.indexOfFirst { it.name == preset.name }
        if (listIndex >= 0) savedPresets[listIndex] = renamed
        if (cacheIndex >= 0) savedSelfDividendPresetsCache[cacheIndex] = renamed
        saveSavedSelfDividendPresets(context, savedSelfDividendPresetsCache)
        if (lastSelfDividendPresetCache?.name == preset.name) {
            lastSelfDividendPresetCache = renamed
            saveLastSelfDividendPreset(context, renamed)
        }
        renamePreset = null
    }

    BacktestCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionTitle("자가배당 설정")
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "불러오기",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { showLoadDialog = true }
            )
            Spacer(modifier = Modifier.width(14.dp))
            Text(
                "+ 종목 추가",
                color = BrandGreen,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { showAssetPicker = true }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "매년 연말 수익 반영 후 세후 인출 목표액을 맞추도록 매도금액과 해외주식 양도세를 계산합니다.",
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 21.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        if (selectedAssets.isEmpty()) {
            Text("아직 추가한 종목이 없습니다.", color = MutedText, fontSize = 14.sp)
        } else {
            selectedAssets.forEachIndexed { index, asset ->
                SelfDividendAssetInputCard(
                    asset = asset,
                    onChange = { updateAsset(index, it) },
                    onDelete = {
                        selectedAssets.removeAt(index)
                        projectionRows = emptyList()
                    }
                )
                if (index != selectedAssets.lastIndex) Spacer(modifier = Modifier.height(12.dp))
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        PrimaryActionButton(
            "자가배당 계산",
            enabled = selectedAssets.isNotEmpty(),
            onClick = {
                val rows = calculateSelfDividendProjection(context, selectedAssets)
                if (rows.isEmpty()) {
                    Toast.makeText(context, "투자금과 연 인출액을 입력해 주세요.", Toast.LENGTH_SHORT).show()
                } else {
                    projectionRows = rows
                    val latestPreset = SelfDividendPreset(
                        name = "마지막 자가배당",
                        assets = selectedAssets.toList(),
                        result = rows
                    )
                    lastSelfDividendPresetCache = latestPreset
                    saveLastSelfDividendPreset(context, latestPreset)
                }
            }
        )
    }

    if (projectionRows.isNotEmpty()) {
        Spacer(modifier = Modifier.height(14.dp))
        SelfDividendProjectionResult(projectionRows)
        Spacer(modifier = Modifier.height(14.dp))
        PrimaryActionButton("자가배당 저장", enabled = selectedAssets.isNotEmpty()) {
            showSaveDialog = true
        }
    }

    if (showAssetPicker) {
        AlertDialog(
            onDismissRequest = { showAssetPicker = false },
            containerColor = PanelColor,
            title = { Text("종목 선택", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    assetOptions.forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedAssets.removeAll { it.ticker == option.ticker }
                                    selectedAssets.add(
                                        SelfDividendAssetUi(
                                            ticker = option.ticker,
                                            name = option.name,
                                            color = option.color
                                        )
                                    )
                                    projectionRows = emptyList()
                                    showAssetPicker = false
                                }
                                .padding(vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            AssetBadge(option.ticker.take(1), option.color, size = 34)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(option.ticker, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                                Text(option.name, color = TextSecondary, fontSize = 13.sp, maxLines = 1)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showAssetPicker = false }) {
                    Text("닫기", color = TextSecondary)
                }
            }
        )
    }

    if (showSaveDialog) {
        SimulationPresetSaveDialog(
            title = "자가배당 저장",
            name = presetName,
            existingNames = savedPresets.map { it.name },
            onNameChange = { presetName = it },
            onDismiss = { showSaveDialog = false },
            onSaveNew = ::saveNamedPreset,
            onOverwrite = ::saveNamedPreset
        )
    }

    if (showLoadDialog) {
        AlertDialog(
            onDismissRequest = { showLoadDialog = false },
            containerColor = PanelColor,
            title = { Text("자가배당 불러오기", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (savedPresets.isEmpty()) {
                        Text("저장된 자가배당이 없습니다.", color = TextSecondary)
                    } else {
                        savedPresets.forEach { preset ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {
                                            selectedAssets.clear()
                                            selectedAssets.addAll(preset.assets)
                                            projectionRows = preset.result
                                            lastSelfDividendPresetCache = preset
                                            saveLastSelfDividendPreset(context, preset)
                                            showLoadDialog = false
                                        },
                                        onLongClick = {
                                            showLoadDialog = false
                                            renamePreset = preset
                                        }
                                    )
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(preset.name, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                    Text("${preset.assets.size}개 종목", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Text("불러오기", color = BrandGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLoadDialog = false }) { Text("닫기", color = TextSecondary) }
            }
        )
    }

    renamePreset?.let { preset ->
        SimulationPresetRenameDialog(
            currentName = preset.name,
            existingNames = savedPresets.map { it.name },
            onDismiss = { renamePreset = null },
            onRename = { renameSavedPreset(preset, it) },
            onDelete = {
                renamePreset = null
                deletePreset = preset
            }
        )
    }

    deletePreset?.let { preset ->
        AlertDialog(
            onDismissRequest = { deletePreset = null },
            containerColor = PanelColor,
            title = { Text("자가배당을 삭제할까요?", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = { Text("${preset.name} 항목을 삭제합니다.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    savedPresets.removeAll { it.name == preset.name }
                    savedSelfDividendPresetsCache.removeAll { it.name == preset.name }
                    saveSavedSelfDividendPresets(context, savedSelfDividendPresetsCache)
                    deletePreset = null
                }) { Text("삭제", color = PositiveRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { deletePreset = null }) { Text("취소", color = TextSecondary) }
            }
        )
    }
}

@Composable
private fun FourAssetDistributionContent() {
    val context = LocalContext.current
    if (savedFourAssetDistributionPresetsCache.isEmpty()) {
        savedFourAssetDistributionPresetsCache.addAll(loadSavedFourAssetDistributionPresets(context))
    }
    val cachedPreset = remember(context) {
        (lastFourAssetDistributionPresetCache ?: loadLastFourAssetDistributionPreset(context)).also {
            lastFourAssetDistributionPresetCache = it
        }
    }
    val savedPresets = remember {
        mutableStateListOf<FourAssetDistributionPreset>().also { it.addAll(savedFourAssetDistributionPresetsCache) }
    }
    var totalCapitalEok by remember { mutableStateOf(cachedPreset?.totalCapitalEok ?: "13") }
    var monthlyExpenseMan by remember { mutableStateOf(cachedPreset?.monthlyExpenseMan ?: "400") }
    var schdRatio by remember { mutableStateOf(cachedPreset?.schdRatio ?: "73.9") }
    var jepqRatio by remember { mutableStateOf(cachedPreset?.jepqRatio ?: "17.4") }
    var qldRatio by remember { mutableStateOf(cachedPreset?.qldRatio ?: "8.7") }
    var cashRatio by remember { mutableStateOf("0") }
    var appliedAllocation by remember {
        mutableStateOf(
            FourAssetAllocation(
                schd = cachedPreset?.appliedSchdRatio ?: 73.9,
                jepq = cachedPreset?.appliedJepqRatio ?: 17.4,
                qld = cachedPreset?.appliedQldRatio ?: 8.7,
                cash = 0.0
            )
        )
    }
    var exchangeRate by remember { mutableStateOf(cachedPreset?.exchangeRate ?: "1600") }
    var schdPrice by remember { mutableStateOf(cachedPreset?.schdPrice ?: "80.0") }
    var jepqPrice by remember { mutableStateOf(cachedPreset?.jepqPrice ?: "50.0") }
    var qldPrice by remember { mutableStateOf(cachedPreset?.qldPrice ?: "90.0") }
    var schdYield by remember { mutableStateOf(cachedPreset?.schdYield ?: "3.0") }
    var schdDividendGrowth by remember { mutableStateOf(cachedPreset?.schdDividendGrowth ?: "6.0") }
    var schdPriceGrowth by remember { mutableStateOf(cachedPreset?.schdPriceGrowth ?: "5.0") }
    var jepqYield by remember { mutableStateOf(cachedPreset?.jepqYield ?: "8.0") }
    var jepqDividendGrowth by remember { mutableStateOf(cachedPreset?.jepqDividendGrowth ?: "2.0") }
    var jepqPriceGrowth by remember { mutableStateOf(cachedPreset?.jepqPriceGrowth ?: "2.0") }
    var qldPriceGrowth by remember { mutableStateOf(cachedPreset?.qldPriceGrowth ?: "15.0") }
    var cashYield by remember { mutableStateOf(cachedPreset?.cashYield ?: "3.0") }
    var inflationRate by remember { mutableStateOf(cachedPreset?.inflationRate ?: "3.0") }
    var taxAndInsuranceRate by remember { mutableStateOf(cachedPreset?.taxAndInsuranceRate ?: "23.4") }
    var stressTestEnabled by remember { mutableStateOf(cachedPreset?.stressTestEnabled ?: false) }
    var showAllocationAlert by remember { mutableStateOf(false) }
    var showSaveDialog by remember { mutableStateOf(false) }
    var showLoadDialog by remember { mutableStateOf(false) }
    var presetName by remember { mutableStateOf("") }
    var deletePreset by remember { mutableStateOf<FourAssetDistributionPreset?>(null) }
    var renamePreset by remember { mutableStateOf<FourAssetDistributionPreset?>(null) }

    fun currentAllocation(): FourAssetAllocation = FourAssetAllocation(
        schd = schdRatio.toDoubleOrNull() ?: 0.0,
        jepq = jepqRatio.toDoubleOrNull() ?: 0.0,
        qld = qldRatio.toDoubleOrNull() ?: 0.0,
        cash = 0.0
    )

    val draftAllocationTotal = currentAllocation().let { it.schd + it.jepq + it.qld }
    val appliedAllocationTotal = appliedAllocation.schd + appliedAllocation.jepq + appliedAllocation.qld

    fun applyDraftAllocation() {
        val draft = currentAllocation()
        val total = draft.schd + draft.jepq + draft.qld + draft.cash
        if (kotlin.math.abs(total - 100.0) > 0.05) {
            showAllocationAlert = true
        } else {
            appliedAllocation = draft
        }
    }

    fun currentPreset(
        name: String = "3자산 분배",
        useAppliedRatios: Boolean = false
    ) = FourAssetDistributionPreset(
        name = name,
        totalCapitalEok = totalCapitalEok,
        monthlyExpenseMan = monthlyExpenseMan,
        schdRatio = if (useAppliedRatios) formatDecimal(appliedAllocation.schd) else schdRatio,
        jepqRatio = if (useAppliedRatios) formatDecimal(appliedAllocation.jepq) else jepqRatio,
        qldRatio = if (useAppliedRatios) formatDecimal(appliedAllocation.qld) else qldRatio,
        cashRatio = "0",
        appliedSchdRatio = appliedAllocation.schd,
        appliedJepqRatio = appliedAllocation.jepq,
        appliedQldRatio = appliedAllocation.qld,
        appliedCashRatio = 0.0,
        exchangeRate = exchangeRate,
        schdPrice = schdPrice,
        jepqPrice = jepqPrice,
        qldPrice = qldPrice,
        schdYield = schdYield,
        schdDividendGrowth = schdDividendGrowth,
        schdPriceGrowth = schdPriceGrowth,
        jepqYield = jepqYield,
        jepqDividendGrowth = jepqDividendGrowth,
        jepqPriceGrowth = jepqPriceGrowth,
        qldPriceGrowth = qldPriceGrowth,
        cashYield = cashYield,
        inflationRate = inflationRate,
        taxAndInsuranceRate = taxAndInsuranceRate,
        stressTestEnabled = stressTestEnabled
    )

    fun saveNamedPreset(name: String) {
        val preset = currentPreset(name)
        savedPresets.removeAll { it.name == preset.name }
        savedFourAssetDistributionPresetsCache.removeAll { it.name == preset.name }
        savedPresets.add(preset)
        savedFourAssetDistributionPresetsCache.add(preset)
        saveSavedFourAssetDistributionPresets(context, savedFourAssetDistributionPresetsCache)
        lastFourAssetDistributionPresetCache = preset
        saveLastFourAssetDistributionPreset(context, preset)
        presetName = ""
        showSaveDialog = false
        Toast.makeText(context, "3자산 분배를 저장했어요.", Toast.LENGTH_SHORT).show()
    }

    fun renameSavedPreset(preset: FourAssetDistributionPreset, newName: String) {
        val renamed = preset.copy(name = newName)
        val listIndex = savedPresets.indexOfFirst { it.name == preset.name }
        val cacheIndex = savedFourAssetDistributionPresetsCache.indexOfFirst { it.name == preset.name }
        if (listIndex >= 0) savedPresets[listIndex] = renamed
        if (cacheIndex >= 0) savedFourAssetDistributionPresetsCache[cacheIndex] = renamed
        saveSavedFourAssetDistributionPresets(context, savedFourAssetDistributionPresetsCache)
        if (lastFourAssetDistributionPresetCache?.name == preset.name) {
            lastFourAssetDistributionPresetCache = renamed
            saveLastFourAssetDistributionPreset(context, renamed)
        }
        renamePreset = null
    }

    fun loadPreset(preset: FourAssetDistributionPreset) {
        totalCapitalEok = preset.totalCapitalEok
        monthlyExpenseMan = preset.monthlyExpenseMan
        schdRatio = preset.schdRatio
        jepqRatio = preset.jepqRatio
        qldRatio = preset.qldRatio
        cashRatio = "0"
        appliedAllocation = FourAssetAllocation(
            preset.appliedSchdRatio,
            preset.appliedJepqRatio,
            preset.appliedQldRatio,
            0.0
        )
        exchangeRate = preset.exchangeRate
        schdPrice = preset.schdPrice
        jepqPrice = preset.jepqPrice
        qldPrice = preset.qldPrice
        schdYield = preset.schdYield
        schdDividendGrowth = preset.schdDividendGrowth
        schdPriceGrowth = preset.schdPriceGrowth
        jepqYield = preset.jepqYield
        jepqDividendGrowth = preset.jepqDividendGrowth
        jepqPriceGrowth = preset.jepqPriceGrowth
        qldPriceGrowth = preset.qldPriceGrowth
        cashYield = preset.cashYield
        inflationRate = preset.inflationRate
        taxAndInsuranceRate = preset.taxAndInsuranceRate
        stressTestEnabled = preset.stressTestEnabled
        lastFourAssetDistributionPresetCache = preset
        saveLastFourAssetDistributionPreset(context, preset)
    }

    val input = FourAssetRetirementInput(
        totalCapitalWon = eokInputToWon(totalCapitalEok),
        monthlyExpenseWon = manInputToWon(monthlyExpenseMan),
        allocation = FourAssetAllocation(
            schd = appliedAllocation.schd / 100.0,
            jepq = appliedAllocation.jepq / 100.0,
            qld = appliedAllocation.qld / 100.0,
            cash = appliedAllocation.cash / 100.0
        ),
        exchangeRate = (exchangeRate.toDoubleOrNull() ?: 1600.0).coerceAtLeast(1.0),
        schdPrice = (schdPrice.toDoubleOrNull() ?: 80.0).coerceAtLeast(0.1),
        jepqPrice = (jepqPrice.toDoubleOrNull() ?: 50.0).coerceAtLeast(0.1),
        qldPrice = (qldPrice.toDoubleOrNull() ?: 90.0).coerceAtLeast(0.1),
        schdYield = (schdYield.toDoubleOrNull() ?: 3.0) / 100.0,
        schdDividendGrowth = (schdDividendGrowth.toDoubleOrNull() ?: 6.0) / 100.0,
        schdPriceGrowth = (schdPriceGrowth.toDoubleOrNull() ?: 5.0) / 100.0,
        jepqYield = (jepqYield.toDoubleOrNull() ?: 8.0) / 100.0,
        jepqDividendGrowth = (jepqDividendGrowth.toDoubleOrNull() ?: 2.0) / 100.0,
        jepqPriceGrowth = (jepqPriceGrowth.toDoubleOrNull() ?: 2.0) / 100.0,
        qldPriceGrowth = (qldPriceGrowth.toDoubleOrNull() ?: 15.0) / 100.0,
        cashYield = (cashYield.toDoubleOrNull() ?: 3.0) / 100.0,
        inflationRate = (inflationRate.toDoubleOrNull() ?: 3.0) / 100.0,
        taxAndInsuranceRate = (taxAndInsuranceRate.toDoubleOrNull() ?: 23.4) / 100.0,
        stressTestEnabled = stressTestEnabled
    )
    val result = calculateFourAssetRetirement(input)

    SideEffect {
        val latest = currentPreset(useAppliedRatios = true)
        if (latest != lastFourAssetDistributionPresetCache) {
            lastFourAssetDistributionPresetCache = latest
            saveLastFourAssetDistributionPreset(context, latest)
        }
    }

    BacktestCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionTitle("3자산 분배 설정")
            Spacer(modifier = Modifier.weight(1f))
            Text(
                "불러오기",
                color = BrandGreen,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { showLoadDialog = true }
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "SCHD, JEPQ, QLD를 20년 동안 배당·생활비·스트레스 테스트 기준으로 시뮬레이션합니다.",
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 21.sp
        )
        Spacer(modifier = Modifier.height(16.dp))
        FourAssetDecimalInputRow("총 은퇴 자산", totalCapitalEok, "억원") { totalCapitalEok = it }
        FourAssetDecimalInputRow("월 생활비", monthlyExpenseMan, "만원") { monthlyExpenseMan = it }
    }

    Spacer(modifier = Modifier.height(14.dp))
    BacktestCard {
        SectionTitle("환율 및 초기 주가")
        Spacer(modifier = Modifier.height(12.dp))
        FourAssetDecimalInputRow("기준 환율", exchangeRate, "원") { exchangeRate = it }
        FourAssetDecimalInputRow("SCHD 주가", schdPrice, "$") { schdPrice = it }
        FourAssetDecimalInputRow("JEPQ 주가", jepqPrice, "$") { jepqPrice = it }
        FourAssetDecimalInputRow("QLD 주가", qldPrice, "$") { qldPrice = it }
    }

    Spacer(modifier = Modifier.height(14.dp))
    BacktestCard {
        SectionTitle("연간 요율")
        Spacer(modifier = Modifier.height(12.dp))
        FourAssetRateGroup("SCHD", FourAssetSchdColor) {
            FourAssetDecimalInputRow("배당률", schdYield, "%") { schdYield = it }
            FourAssetDecimalInputRow("배당성장", schdDividendGrowth, "%") { schdDividendGrowth = it }
            FourAssetDecimalInputRow("주가성장", schdPriceGrowth, "%", allowNegative = true) { schdPriceGrowth = it }
        }
        Spacer(modifier = Modifier.height(10.dp))
        FourAssetRateGroup("JEPQ", FourAssetJepqColor) {
            FourAssetDecimalInputRow("배당률", jepqYield, "%") { jepqYield = it }
            FourAssetDecimalInputRow("배당성장", jepqDividendGrowth, "%") { jepqDividendGrowth = it }
            FourAssetDecimalInputRow("주가성장", jepqPriceGrowth, "%", allowNegative = true) { jepqPriceGrowth = it }
        }
        Spacer(modifier = Modifier.height(10.dp))
        FourAssetDecimalInputRow("QLD 성장", qldPriceGrowth, "%", allowNegative = true) { qldPriceGrowth = it }
        FourAssetDecimalInputRow("현금 이자", cashYield, "%") { cashYield = it }
    }

    Spacer(modifier = Modifier.height(14.dp))
    BacktestCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            SectionTitle("자산 배분")
            Spacer(modifier = Modifier.weight(1f))
            Text("입력 합계 ${String.format(Locale.US, "%.1f%%", draftAllocationTotal)}", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "현재 결과 반영 비율 ${String.format(Locale.US, "%.1f%%", appliedAllocationTotal)} · 입력 후 버튼을 눌러야 결과에 반영됩니다.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
        Spacer(modifier = Modifier.height(12.dp))
        FourAssetAllocationInputRow("SCHD", "성장배당", FourAssetSchdColor, schdRatio, input.totalCapitalWon) {
            schdRatio = it
        }
        FourAssetAllocationInputRow("JEPQ", "고배당", FourAssetJepqColor, jepqRatio, input.totalCapitalWon) {
            jepqRatio = it
        }
        FourAssetAllocationInputRow("QLD", "나스닥 2배", FourAssetQldColor, qldRatio, input.totalCapitalWon) {
            qldRatio = it
        }
        Spacer(modifier = Modifier.height(12.dp))
        PrimaryActionButton("배분 비율 반영", enabled = true) {
            applyDraftAllocation()
        }
    }

    Spacer(modifier = Modifier.height(14.dp))
    BacktestCard {
        SectionTitle("환경 변수")
        Spacer(modifier = Modifier.height(12.dp))
        FourAssetDecimalInputRow("물가상승률", inflationRate, "%") { inflationRate = it }
        FourAssetDecimalInputRow("세금/건보료", taxAndInsuranceRate, "%") { taxAndInsuranceRate = it }
        Spacer(modifier = Modifier.height(6.dp))
        BacktestSwitchRow("하락장 스트레스 테스트", stressTestEnabled) { stressTestEnabled = it }
        Text(
            "1년차 SCHD/JEPQ -30%, QLD -60%, 2~3년차 정체, JEPQ 배당 20% 삭감을 반영합니다.",
            color = TextSecondary,
            fontSize = 12.sp,
            lineHeight = 18.sp
        )
    }

    Spacer(modifier = Modifier.height(14.dp))
    FourAssetResultContent(result)
    Spacer(modifier = Modifier.height(14.dp))
    PrimaryActionButton("3자산 분배 저장", enabled = true) {
        showSaveDialog = true
    }

    if (showAllocationAlert) {
        AlertDialog(
            onDismissRequest = { showAllocationAlert = false },
            containerColor = PanelColor,
            title = { Text("배분 비율을 확인해 주세요", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = {
                Text(
                    "SCHD, JEPQ, QLD 비율 합계가 100%가 되어야 결과에 반영할 수 있습니다. 현재 합계는 ${String.format(Locale.US, "%.1f%%", draftAllocationTotal)}입니다.",
                    color = TextSecondary,
                    lineHeight = 21.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showAllocationAlert = false }) {
                    Text("확인", color = BrandGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    if (showSaveDialog) {
        SimulationPresetSaveDialog(
            title = "3자산 분배 저장",
            name = presetName,
            existingNames = savedPresets.map { it.name },
            onNameChange = { presetName = it },
            onDismiss = { showSaveDialog = false },
            onSaveNew = ::saveNamedPreset,
            onOverwrite = ::saveNamedPreset
        )
    }

    if (showLoadDialog) {
        AlertDialog(
            onDismissRequest = { showLoadDialog = false },
            containerColor = PanelColor,
            title = { Text("3자산 분배 불러오기", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    if (savedPresets.isEmpty()) {
                        Text("저장된 3자산 분배가 없습니다.", color = TextSecondary)
                    } else {
                        savedPresets.forEach { preset ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .combinedClickable(
                                        onClick = {
                                            loadPreset(preset)
                                            showLoadDialog = false
                                        },
                                        onLongClick = {
                                            showLoadDialog = false
                                            renamePreset = preset
                                        }
                                    )
                                    .padding(vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(preset.name, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                                    Text("반영 비율 SCHD ${preset.appliedSchdRatio.formatOneDecimal()}% · JEPQ ${preset.appliedJepqRatio.formatOneDecimal()}%", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                                Text("불러오기", color = BrandGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showLoadDialog = false }) { Text("닫기", color = TextSecondary) }
            }
        )
    }

    renamePreset?.let { preset ->
        SimulationPresetRenameDialog(
            currentName = preset.name,
            existingNames = savedPresets.map { it.name },
            onDismiss = { renamePreset = null },
            onRename = { renameSavedPreset(preset, it) },
            onDelete = {
                renamePreset = null
                deletePreset = preset
            }
        )
    }

    deletePreset?.let { preset ->
        AlertDialog(
            onDismissRequest = { deletePreset = null },
            containerColor = PanelColor,
            title = { Text("3자산 분배를 삭제할까요?", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
            text = { Text("${preset.name} 항목을 삭제합니다.", color = TextSecondary) },
            confirmButton = {
                TextButton(onClick = {
                    savedPresets.removeAll { it.name == preset.name }
                    savedFourAssetDistributionPresetsCache.removeAll { it.name == preset.name }
                    saveSavedFourAssetDistributionPresets(context, savedFourAssetDistributionPresetsCache)
                    deletePreset = null
                }) { Text("삭제", color = PositiveRed, fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { deletePreset = null }) { Text("취소", color = TextSecondary) }
            }
        )
    }
}

@Composable
private fun FourAssetResultContent(result: FourAssetRetirementResult) {
    BacktestCard {
        SectionTitle("3자산 분배 결과")
        Spacer(modifier = Modifier.height(12.dp))
        SelfDividendSummaryBox("20년 뒤 최종 자산", formatWon(result.finalAssetWon), modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            FourAssetShareBox("SCHD", result.initialSchdShares, modifier = Modifier.weight(1f))
            FourAssetShareBox("JEPQ", result.initialJepqShares, modifier = Modifier.weight(1f))
            FourAssetShareBox("QLD", result.initialQldShares, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(16.dp))
        FourAssetStackedChart(result.rows)
        Spacer(modifier = Modifier.height(16.dp))
        FourAssetReportTable(result.rows)
    }
}

@Composable
private fun FourAssetShareBox(label: String, shares: Double, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(PanelColor)
            .padding(12.dp)
    ) {
        Text(label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text("${NumberFormat.getNumberInstance(Locale.KOREA).format(shares.roundToLong())}주", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun FourAssetStackedChart(rows: List<FourAssetAnnualRow>) {
    var selectedIndex by remember(rows) { mutableStateOf<Int?>(null) }
    val maxValue = rows.maxOfOrNull { it.totalAssetWon }?.coerceAtLeast(1L) ?: 1L
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("연차별 포트폴리오 자산 흐름", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.weight(1f))
            Text("단위 억원", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            FourAssetLegend("SCHD", FourAssetSchdColor)
            FourAssetLegend("JEPQ", FourAssetJepqColor)
            FourAssetLegend("QLD", FourAssetQldColor)
            FourAssetLegend("현금", FourAssetCashColor)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .pointerInput(rows) {
                    detectTapGestures { offset ->
                        if (rows.isNotEmpty()) {
                            selectedIndex = fourAssetChartIndexForOffset(offset.x, size.width.toFloat(), rows.size)
                        }
                    }
                }
        ) {
            if (rows.isEmpty()) return@Canvas
            val left = 58f
            val right = size.width - 8f
            val top = 12f
            val bottom = size.height - 34f
            val chartWidth = (right - left).coerceAtLeast(1f)
            val chartHeight = (bottom - top).coerceAtLeast(1f)
            val labelPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = TextSecondary.toArgb()
                textSize = 20f
                textAlign = android.graphics.Paint.Align.RIGHT
            }
            val xPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = TextSecondary.toArgb()
                textSize = 18f
                textAlign = android.graphics.Paint.Align.CENTER
            }

            repeat(4) { index ->
                val ratio = index / 3f
                val y = top + chartHeight * ratio
                val value = maxValue * (1f - ratio)
                drawLine(LineColor, Offset(left, y), Offset(right, y), strokeWidth = 1.2f)
                drawContext.canvas.nativeCanvas.drawText("${(value / 100_000_000f).roundToInt()}억", left - 8f, y + 7f, labelPaint)
            }

            val slot = chartWidth / rows.size.coerceAtLeast(1)
            val barWidth = (slot * 0.58f).coerceAtLeast(4f)
            rows.forEachIndexed { index, row ->
                val x = left + slot * index + (slot - barWidth) / 2f
                var stackBottom = bottom
                fun drawSegment(value: Long, color: Color) {
                    val height = (value.toDouble() / maxValue.toDouble() * chartHeight).toFloat().coerceAtLeast(0f)
                    val y = stackBottom - height
                    if (height > 0f) {
                        drawRect(
                            color = color,
                            topLeft = Offset(x, y),
                            size = androidx.compose.ui.geometry.Size(barWidth, height)
                        )
                    }
                    stackBottom = y
                }
                drawSegment(row.cashWon, FourAssetCashColor)
                drawSegment(row.qldAssetWon, FourAssetQldColor)
                drawSegment(row.jepqAssetWon, FourAssetJepqColor)
                drawSegment(row.schdAssetWon, FourAssetSchdColor)

                if (index == 0 || index == 9 || index == rows.lastIndex) {
                    drawContext.canvas.nativeCanvas.drawText("${row.year}년", x + barWidth / 2f, bottom + 25f, xPaint)
                }
            }

            selectedIndex?.let { selected ->
                val row = rows.getOrNull(selected) ?: return@let
                val x = left + slot * selected + slot / 2f
                drawLine(TextPrimary.copy(alpha = 0.7f), Offset(x, top), Offset(x, bottom), strokeWidth = 2f)
                drawCircle(TextPrimary, radius = 4.5f, center = Offset(x, bottom - (row.totalAssetWon.toDouble() / maxValue.toDouble() * chartHeight).toFloat()))
            }
        }
        selectedIndex?.let { index ->
            rows.getOrNull(index)?.let { row ->
                FourAssetSelectedBreakdown(row = row, modifier = Modifier.padding(top = 10.dp))
            }
        }
    }
}

@Composable
private fun FourAssetSelectedBreakdown(row: FourAssetAnnualRow, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PanelColor)
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${row.year}년차", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.weight(1f))
            Text(formatWon(row.totalAssetWon), color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.height(9.dp))
        FourAssetBreakdownLine("SCHD", FourAssetSchdColor, row.schdAssetWon, row.totalAssetWon)
        FourAssetBreakdownLine("JEPQ", FourAssetJepqColor, row.jepqAssetWon, row.totalAssetWon)
        FourAssetBreakdownLine("QLD", FourAssetQldColor, row.qldAssetWon, row.totalAssetWon)
        FourAssetBreakdownLine("현금", FourAssetCashColor, row.cashWon, row.totalAssetWon)
        Spacer(modifier = Modifier.height(7.dp))
        Text("세후배당 ${formatManWon(row.netAnnualDividendWon)} · 생활비 ${formatManWon(row.annualExpenseWon)}", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FourAssetBreakdownLine(label: String, color: Color, amount: Long, total: Long) {
    val ratio = if (total > 0L) amount.toDouble() / total else 0.0
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(7.dp))
        Text(label, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(42.dp))
        Text(formatPercent(ratio), color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        Spacer(modifier = Modifier.width(10.dp))
        Text(formatWon(amount), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1.35f), textAlign = TextAlign.End)
    }
}

@Composable
private fun FourAssetReportTable(rows: List<FourAssetAnnualRow>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("20개년 연도별 은퇴 명세", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(8.dp))
        FourAssetReportHeader()
        rows.forEach { row ->
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 9.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("${row.year}년", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.width(44.dp))
                    Text(formatEokWon(row.totalAssetWon), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text(formatManWon(row.grossAnnualDividendWon), color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text(formatManWon(row.netAnnualDividendWon), color = BrandGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                    Text(formatManWon(row.annualExpenseWon), color = PositiveRed, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
                }
                Text(
                    "${formatEokWon(row.schdAssetWon)} / ${formatEokWon(row.jepqAssetWon)} / ${formatEokWon(row.qldAssetWon)} / 현금 ${formatEokWon(row.cashWon)}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(start = 44.dp, top = 3.dp)
                )
                Text(row.action, color = MutedText, fontSize = 11.sp, modifier = Modifier.padding(start = 44.dp, top = 2.dp))
            }
        }
    }
}

@Composable
private fun FourAssetReportHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PanelColor)
            .padding(vertical = 9.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("연차", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(44.dp), textAlign = TextAlign.Center)
        Text("총 자산", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        Text("세전배당", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        Text("세후배당", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
        Text("생활비", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.End)
    }
}

@Composable
private fun FourAssetLegend(label: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(9.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(4.dp))
        Text(label, color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun FourAssetRateGroup(label: String, color: Color, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.10f))
            .padding(12.dp)
    ) {
        Text(label, color = color, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(6.dp))
        content()
    }
}

@Composable
private fun FourAssetAllocationInputRow(
    title: String,
    caption: String,
    color: Color,
    value: String,
    totalCapitalWon: Long,
    onValueChange: (String) -> Unit
) {
    val ratio = value.toDoubleOrNull() ?: 0.0
    val amount = (totalCapitalWon * ratio / 100.0).roundToLong()
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(color))
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.width(88.dp)) {
            Text(title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Text(caption, color = TextSecondary, fontSize = 11.sp, maxLines = 1)
        }
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(sanitizeDecimalInput(it)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = appTextFieldColors(),
            shape = RoundedCornerShape(16.dp),
            suffix = { Text("%", color = TextSecondary) },
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(formatEokWon(amount), color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End, modifier = Modifier.width(66.dp))
    }
}

@Composable
private fun FourAssetDecimalInputRow(
    label: String,
    value: String,
    suffix: String,
    allowNegative: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(92.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { onValueChange(sanitizeDecimalInput(it, allowNegative)) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            colors = appTextFieldColors(),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(suffix, color = TextSecondary, fontSize = 13.sp, modifier = Modifier.width(34.dp), textAlign = TextAlign.End)
    }
}

@Composable
private fun FourAssetQuickButton(text: String, active: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (active) TextPrimary else PanelColor)
            .clickable(onClick = onClick)
            .padding(vertical = 11.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (active) PanelColor else TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
    }
}

private fun calculateFourAssetRetirement(input: FourAssetRetirementInput): FourAssetRetirementResult =
    ThreeAssetRetirementEngine.calculate(input)

private fun fourAssetRetirementInput(preset: FourAssetDistributionPreset): FourAssetRetirementInput =
    FourAssetRetirementInput(
        totalCapitalWon = eokInputToWon(preset.totalCapitalEok),
        monthlyExpenseWon = manInputToWon(preset.monthlyExpenseMan),
        allocation = FourAssetAllocation(
            schd = preset.appliedSchdRatio / 100.0,
            jepq = preset.appliedJepqRatio / 100.0,
            qld = preset.appliedQldRatio / 100.0,
            cash = 0.0
        ),
        exchangeRate = (preset.exchangeRate.toDoubleOrNull() ?: 1600.0).coerceAtLeast(1.0),
        schdPrice = (preset.schdPrice.toDoubleOrNull() ?: 80.0).coerceAtLeast(0.1),
        jepqPrice = (preset.jepqPrice.toDoubleOrNull() ?: 50.0).coerceAtLeast(0.1),
        qldPrice = (preset.qldPrice.toDoubleOrNull() ?: 90.0).coerceAtLeast(0.1),
        schdYield = (preset.schdYield.toDoubleOrNull() ?: 3.0) / 100.0,
        schdDividendGrowth = (preset.schdDividendGrowth.toDoubleOrNull() ?: 6.0) / 100.0,
        schdPriceGrowth = (preset.schdPriceGrowth.toDoubleOrNull() ?: 5.0) / 100.0,
        jepqYield = (preset.jepqYield.toDoubleOrNull() ?: 8.0) / 100.0,
        jepqDividendGrowth = (preset.jepqDividendGrowth.toDoubleOrNull() ?: 2.0) / 100.0,
        jepqPriceGrowth = (preset.jepqPriceGrowth.toDoubleOrNull() ?: 2.0) / 100.0,
        qldPriceGrowth = (preset.qldPriceGrowth.toDoubleOrNull() ?: 15.0) / 100.0,
        cashYield = 0.0,
        inflationRate = (preset.inflationRate.toDoubleOrNull() ?: 3.0) / 100.0,
        taxAndInsuranceRate = (preset.taxAndInsuranceRate.toDoubleOrNull() ?: 23.4) / 100.0,
        stressTestEnabled = preset.stressTestEnabled
    )

private fun fourAssetChartIndexForOffset(x: Float, width: Float, count: Int): Int {
    if (count <= 1) return 0
    val left = 58f
    val right = (width - 8f).coerceAtLeast(left + 1f)
    val chartWidth = (right - left).coerceAtLeast(1f)
    return (((x - left).coerceIn(0f, chartWidth) / chartWidth) * count)
        .toInt()
        .coerceIn(0, count - 1)
}

private fun eokInputToWon(value: String): Long =
    ((value.toDoubleOrNull() ?: 0.0) * 100_000_000.0).roundToLong().coerceAtLeast(0L)

private fun manInputToWon(value: String): Long =
    ((value.toDoubleOrNull() ?: 0.0) * 10_000.0).roundToLong().coerceAtLeast(0L)

private fun sanitizeDecimalInput(value: String, allowNegative: Boolean = false): String {
    val builder = StringBuilder()
    var hasDot = false
    value.forEachIndexed { index, ch ->
        when {
            ch.isDigit() -> builder.append(ch)
            ch == '.' && !hasDot -> {
                builder.append(ch)
                hasDot = true
            }
            ch == '-' && allowNegative && index == 0 -> builder.append(ch)
        }
    }
    return builder.toString()
}

private fun formatEokWon(value: Long): String =
    if (DisplayCurrency == CurrencyMode.USD) {
        CurrencyDisplayFormatter.formatCompact(value, DisplayUsdKrw, true)
    } else if (value <= 0L) {
        "0.00억"
    } else {
        "${String.format(Locale.US, "%.2f", value / 100_000_000.0)}억"
    }

private fun formatManWon(value: Long): String =
    if (DisplayCurrency == CurrencyMode.USD) {
        CurrencyDisplayFormatter.formatCompact(value, DisplayUsdKrw, true)
    } else {
        "${NumberFormat.getNumberInstance(Locale.KOREA).format((value / 10_000.0).roundToLong())}만"
    }

@Composable
private fun SelfDividendProjectionResult(rows: List<SelfDividendProjectionRow>) {
    val first = rows.first()
    val last = rows.last()
    BacktestCard {
        SectionTitle("자가배당 결과")
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            SelfDividendSummaryBox("1년 후 월 실수령", formatWon(first.monthlyTakeHome), modifier = Modifier.weight(1f))
            SelfDividendSummaryBox("20년 후 세후 자산", formatWon(last.totalAsset), modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(14.dp))
        SelfDividendProjectionChart(rows)
        Spacer(modifier = Modifier.height(14.dp))
        rows.forEach { row ->
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "${row.year}년",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.width(46.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Text("월 ${formatWon(row.monthlyTakeHome)}", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text("세후 자산 ${formatWon(row.totalAsset)} · 매도 ${formatWon(row.grossSale)}", color = TextSecondary, fontSize = 12.sp)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text("양도세", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(
                            formatWon(row.capitalGainsTax),
                            color = if (row.capitalGainsTax > 0L) PositiveRed else BrandGreen,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(row.note, color = MutedText, fontSize = 12.sp, modifier = Modifier.padding(start = 46.dp, top = 3.dp))
            }
        }
    }
}

@Composable
private fun SelfDividendProjectionChart(rows: List<SelfDividendProjectionRow>) {
    val labels = rows.map { "${it.year}년" }
    val maxValue = rows.maxOfOrNull { it.totalAsset }?.coerceAtLeast(1L) ?: 1L
    val lastTotalAsset = rows.lastOrNull()?.totalAsset ?: 0L
    val totalTax = rows.sumOf { it.capitalGainsTax }
    var selectedIndex by remember(rows) { mutableStateOf<Int?>(null) }
    val statusText = when {
        lastTotalAsset <= 0L -> "자산 고갈 위험"
        rows.any { it.annualTakeHome <= 0L } -> "인출 실패"
        rows.any { it.note.contains("일부") } -> "일부 인출"
        else -> "세후 인출 유지"
    }
    val statusColor = when {
        lastTotalAsset <= 0L -> NegativeBlue
        rows.any { it.note.contains("일부") } -> CashOrange
        else -> BrandGreen
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.weight(1f)) {
                Text("자가배당 그래프", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Text("${labels.firstOrNull().orEmpty()} ~ ${labels.lastOrNull().orEmpty()}", color = TextSecondary, fontSize = 13.sp)
            }
            Text(statusText, color = statusColor, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            FourAssetLegend("세후 총자산", PositiveRed)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .pointerInput(rows) {
                    detectTapGestures { offset ->
                        if (rows.isNotEmpty()) {
                            selectedIndex = fourAssetChartIndexForOffset(offset.x, size.width.toFloat(), rows.size)
                        }
                    }
                }
        ) {
            if (rows.isEmpty()) return@Canvas
            val left = 58f
            val right = size.width - 8f
            val top = 12f
            val bottom = size.height - 34f
            val chartWidth = (right - left).coerceAtLeast(1f)
            val chartHeight = (bottom - top).coerceAtLeast(1f)
            val labelPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = TextSecondary.toArgb()
                textSize = 20f
                textAlign = android.graphics.Paint.Align.RIGHT
            }
            val xPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
                color = TextSecondary.toArgb()
                textSize = 18f
                textAlign = android.graphics.Paint.Align.CENTER
            }

            repeat(4) { index ->
                val ratio = index / 3f
                val y = top + chartHeight * ratio
                val value = maxValue * (1f - ratio)
                drawLine(LineColor, Offset(left, y), Offset(right, y), strokeWidth = 1.2f)
                drawContext.canvas.nativeCanvas.drawText(
                    "${(value / 100_000_000f).roundToInt()}억",
                    left - 8f,
                    y + 7f,
                    labelPaint
                )
            }

            val slot = chartWidth / rows.size.coerceAtLeast(1)
            val barWidth = (slot * 0.58f).coerceAtLeast(4f)
            rows.forEachIndexed { index, row ->
                val x = left + slot * index + (slot - barWidth) / 2f
                val height = (row.totalAsset.toDouble() / maxValue.toDouble() * chartHeight)
                    .toFloat()
                    .coerceAtLeast(0f)
                val y = bottom - height
                if (height > 0f) {
                    drawRect(
                        color = PositiveRed,
                        topLeft = Offset(x, y),
                        size = androidx.compose.ui.geometry.Size(barWidth, height)
                    )
                }
                if (index == 0 || index == 9 || index == rows.lastIndex) {
                    drawContext.canvas.nativeCanvas.drawText(
                        "${row.year}년",
                        x + barWidth / 2f,
                        bottom + 25f,
                        xPaint
                    )
                }
            }

            selectedIndex?.let { selected ->
                val row = rows.getOrNull(selected) ?: return@let
                val x = left + slot * selected + slot / 2f
                val y = bottom - (row.totalAsset.toDouble() / maxValue.toDouble() * chartHeight).toFloat()
                drawLine(TextPrimary.copy(alpha = 0.7f), Offset(x, top), Offset(x, bottom), strokeWidth = 2f)
                drawCircle(TextPrimary, radius = 4.5f, center = Offset(x, y))
            }
        }
        selectedIndex?.let { index ->
            rows.getOrNull(index)?.let { row ->
                SelfDividendSelectedValue(
                    label = labels.getOrNull(index) ?: "${index + 1}년",
                    monthlyTakeHome = row.monthlyTakeHome,
                    totalAsset = row.totalAsset,
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "누적 양도세 ${formatWon(totalTax)}",
            color = TextSecondary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun SelfDividendSelectedValue(
    label: String,
    monthlyTakeHome: Long,
    totalAsset: Long,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PanelColor)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        Column(horizontalAlignment = Alignment.End) {
            Text("월 세후 배당금액 ${formatWon(monthlyTakeHome)}", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(3.dp))
            Text("총 자산 ${formatWon(totalAsset)}", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SelfDividendSummaryBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(PanelColor)
            .padding(14.dp)
    ) {
        Text(label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(5.dp))
        Text(value, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
    }
}

private fun calculateSelfDividendProjection(context: Context, assets: List<SelfDividendAssetUi>): List<SelfDividendProjectionRow> {
    val inputs = assets.mapNotNull { asset ->
        val investment = digitsToLong(asset.investmentAmount)
        val withdrawal = digitsToLong(asset.annualWithdrawal)
        if (investment <= 0L || withdrawal <= 0L) {
            null
        } else {
            val downloadedReturn = selfDividendDownloadedAnnualReturn(context, asset.ticker)
            SelfDividendAssetInput(
                taxable = asset.taxMode == "해외직투 양도세",
                expectedAnnualReturn = (downloadedReturn ?: selfDividendExpectedAnnualReturn(asset.ticker)).coerceAtLeast(-0.99),
                investmentAmount = investment.toDouble(),
                baseAnnualWithdrawal = withdrawal.toDouble(),
                withdrawalGrowthRate = (asset.withdrawalGrowthRate.toDoubleOrNull() ?: 0.0) / 100.0
            )
        }
    }
    return SelfDividendEngine.calculate(inputs)
}

private fun selfDividendExpectedAnnualReturn(ticker: String): Double =
    when (ticker.uppercase(Locale.US)) {
        "VOO", "SPY", "IVV", "QQQ" -> 0.10
        "QLD" -> 0.14
        "TQQQ", "SOXL" -> 0.16
        else -> 0.08
    }

private fun selfDividendDownloadedAnnualReturn(context: Context, ticker: String): Double? {
    val points = loadHistoricalSeries(context, ticker)
        .mapNotNull { point ->
            val date = parseAppDate(point.date) ?: return@mapNotNull null
            if (point.close <= 0.0) null else date to point.close
        }
        .sortedBy { it.first }
    val first = points.firstOrNull() ?: return null
    val last = points.lastOrNull() ?: return null
    val years = java.time.temporal.ChronoUnit.DAYS.between(first.first, last.first).toDouble() / 365.25
    if (years < 1.0 || first.second <= 0.0 || last.second <= 0.0) return null
    val cagr = (last.second / first.second).pow(1.0 / years) - 1.0
    return cagr.takeIf { it.isFinite() }
}

private fun digitsToLong(value: String): Long =
    value.filter { it.isDigit() }.toLongOrNull() ?: 0L

private fun Double.formatOneDecimal(): String =
    String.format(Locale.KOREA, "%.1f", this)

@Composable
private fun SelfDividendAssetInputCard(
    asset: SelfDividendAssetUi,
    onChange: (SelfDividendAssetUi) -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(PanelColor)
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AssetBadge(asset.ticker.take(1), asset.color, size = 36)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(asset.ticker, color = TextPrimary, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
                Text(asset.name, color = TextSecondary, fontSize = 13.sp, maxLines = 1)
            }
            Text(
                "삭제",
                color = PositiveRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable(onClick = onDelete)
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        BacktestInputRow("거치식 투자금", asset.investmentAmount, "원") {
            onChange(asset.copy(investmentAmount = it))
        }
        BacktestInputRow("연 인출액", asset.annualWithdrawal, "원") {
            onChange(asset.copy(annualWithdrawal = it))
        }
        BacktestInputRow("인출 증가율", asset.withdrawalGrowthRate, "%") {
            onChange(asset.copy(withdrawalGrowthRate = it))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text("과세 방식", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
        BacktestChoiceRow(listOf("해외직투 양도세", "세금 미반영"), asset.taxMode) {
            onChange(asset.copy(taxMode = it))
        }
    }
}

@Composable
private fun BacktestCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SoftSurface),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp), content = content)
    }
}

@Composable
private fun BacktestInputRow(label: String, value: String, suffix: String, onValueChange: (String) -> Unit) {
    val isMoney = suffix == "원"
    val displayValue = if (isMoney) formatNumberInput(value) else value
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(92.dp))
        OutlinedTextField(
            value = displayValue,
            onValueChange = { onValueChange(it.filter { ch -> ch.isDigit() }) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = appTextFieldColors(),
            shape = RoundedCornerShape(18.dp),
            modifier = Modifier.weight(1f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(suffix, color = TextSecondary, fontSize = 14.sp)
    }
}

@Composable
private fun BacktestSwitchRow(label: String, enabled: Boolean, onChange: (Boolean) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        ToggleSwitch(checked = enabled, onChange = onChange)
    }
}

@Composable
private fun ToggleSwitch(checked: Boolean, onChange: (Boolean) -> Unit) {
    Box(
        modifier = Modifier
            .width(58.dp)
            .height(32.dp)
            .clip(RoundedCornerShape(999.dp))
            .background(if (checked) TextPrimary else Color(0xFFD2D8DF))
            .clickable { onChange(!checked) }
            .padding(4.dp),
        contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Box(modifier = Modifier.size(24.dp).clip(CircleShape).background(PanelColor))
    }
}

@Composable
private fun BacktestChoiceRow(options: List<String>, selected: String, onSelect: (String) -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        options.forEach { option ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (option == selected) TextPrimary else PanelColor)
                    .clickable { onSelect(option) }
                    .padding(horizontal = 13.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(option, color = if (option == selected) PanelColor else TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun BacktestAssetRow(asset: BacktestAssetUi, onLongClick: () -> Unit) {
    val title = if (isKoreanTicker(asset.ticker) && asset.name.isNotBlank()) asset.name else asset.ticker
    val caption = if (isKoreanTicker(asset.ticker) && asset.name.isNotBlank()) asset.ticker else asset.name
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = {}, onLongClick = onLongClick)
            .padding(vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssetBadge(asset.ticker.take(1), asset.color)
        Spacer(modifier = Modifier.width(13.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text(caption, color = TextSecondary, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Text("${String.format(Locale.US, "%.1f", asset.weight)}%", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun PrimaryActionButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(28.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = TextPrimary,
            disabledContainerColor = LineColor,
            contentColor = PanelColor,
            disabledContentColor = TextSecondary
        ),
        modifier = Modifier.fillMaxWidth().height(56.dp)
    ) {
        Text(text, fontSize = 17.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun BacktestPerformanceCard(result: BacktestResultUi) {
    val initial = result.initialAmount.takeIf { it > 0L } ?: result.monthlyAssets.firstOrNull() ?: 1L
    val invested = result.investedAmount.takeIf { it > 0L } ?: initial
    val final = result.monthlyAssets.lastOrNull() ?: invested
    val totalReturn = if (invested == 0L) 0.0 else (final - invested).toDouble() / invested
    val years = (result.monthlyAssets.size / 12.0).coerceAtLeast(1.0 / 12.0)
    val cagr = ((final.toDouble() / invested.coerceAtLeast(1L).toDouble()).pow(1.0 / years) - 1.0).takeIf { it.isFinite() } ?: 0.0
    val mdd = result.monthlyDrawdowns.minOrNull() ?: 0.0
    val bestAnnual = result.annualReturns.maxOfOrNull { it.second } ?: 0.0
    val worstAnnual = result.annualReturns.minOfOrNull { it.second } ?: 0.0
    val underwaterMonths = longestUnderwaterMonths(result.monthlyDrawdowns)

    BacktestCard {
        Text("성과 지표", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(14.dp))
        BacktestMetricGrid(
            listOf(
                "투자 원금" to formatWon(invested),
                "최종 금액" to formatWon(final),
                "연평균 수익률" to formatPercent(cagr),
                "총 수익률" to formatPercent(totalReturn),
                "최고 연간 수익률" to formatPercent(bestAnnual / 100.0),
                "최저 연간 수익률" to formatPercent(worstAnnual / 100.0),
                "최대 낙폭" to String.format(Locale.US, "%.2f%%", mdd),
                "최장 회복 대기" to monthCountLabel(underwaterMonths)
            )
        )
    }
}

@Composable
private fun BacktestDisciplineCard(result: BacktestResultUi) {
    val underwaterMonths = longestUnderwaterMonths(result.monthlyDrawdowns)
    val crisisMonths = result.monthlyDrawdowns.count { it <= -20.0 }
    val worstMdd = result.monthlyDrawdowns.minOrNull() ?: 0.0
    val latestAllocation = result.monthlyAllocations.lastOrNull().orEmpty()
    BacktestCard {
        Text("하락장 체력 점검", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(12.dp))
        BacktestMetricGrid(
            listOf(
                "최장 회복 대기" to monthCountLabel(underwaterMonths),
                "-20% 이하 체류" to monthCountLabel(crisisMonths),
                "최대 낙폭 체감" to String.format(Locale.US, "%.2f%%", worstMdd),
                "최근 1위 비중" to (latestAllocation.firstOrNull()?.let { "${it.name} ${String.format(Locale.US, "%.1f%%", it.weight)}" } ?: "없음")
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            "장기투자는 수익률보다 회복 대기 시간을 알고 들어가는 쪽이 훨씬 덜 흔들립니다.",
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 21.sp
        )
    }
}

@Composable
private fun BacktestMetricGrid(items: List<Pair<String, String>>) {
    items.chunked(2).forEach { rowItems ->
        Row(modifier = Modifier.fillMaxWidth()) {
            rowItems.forEach { (label, value) ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(SoftSurface)
                        .padding(horizontal = 10.dp, vertical = 12.dp)
                ) {
                    Text(label, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(5.dp))
                    Text(value, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
            if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
        }
    }
}

private fun longestUnderwaterMonths(drawdowns: List<Double>): Int {
    var current = 0
    var longest = 0
    drawdowns.forEach { drawdown ->
        if (drawdown < -0.01) {
            current += 1
            longest = maxOf(longest, current)
        } else {
            current = 0
        }
    }
    return longest
}

private fun monthCountLabel(months: Int): String {
    if (months <= 0) return "없음"
    val years = months / 12
    val remain = months % 12
    return when {
        years > 0 && remain > 0 -> "${years}년 ${remain}개월"
        years > 0 -> "${years}년"
        else -> "${months}개월"
    }
}

@Composable
private fun BacktestGraphCard(
    title: String,
    values: List<Double>,
    labels: List<String>,
    valueLabel: (Double) -> String,
    yAxisLabel: (Double) -> String,
    color: Color,
    chartType: BacktestChartType,
    allocationDetails: List<List<BacktestAllocationPoint>> = emptyList()
) {
    BacktestCard {
        Text(title, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Text("${labels.firstOrNull().orEmpty()} ~ ${labels.lastOrNull().orEmpty()}", color = TextSecondary, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        Spacer(modifier = Modifier.height(12.dp))
        BacktestLegend(color = color)
        Spacer(modifier = Modifier.height(8.dp))
        var selectedIndex by remember(values) { mutableStateOf<Int?>(null) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .pointerInput(values, labels) {
                    detectTapGestures { offset ->
                        if (values.isNotEmpty()) {
                            val index = backtestChartIndexForOffset(offset.x, size.width.toFloat(), values.size)
                            selectedIndex = index
                        }
                    }
                }
        ) {
            DetailedBacktestLineChart(
                values = values,
                labels = labels,
                color = color,
                yAxisLabel = yAxisLabel,
                chartType = chartType,
                selectedIndex = selectedIndex,
                valueLabel = valueLabel,
                modifier = Modifier.fillMaxSize()
            )
        }
        selectedIndex?.let { index ->
            val label = labels.getOrNull(index) ?: "${index + 1}번째"
            val eventName = nasdaqStressEventForMonth(label)?.label
            BacktestSelectedValue(
                label = label,
                value = valueLabel(values.getOrElse(index) { 0.0 }),
                modifier = Modifier.padding(top = 12.dp)
            )
            BacktestAllocationSnapshot(
                allocations = allocationDetails.getOrNull(index).orEmpty(),
                eventName = eventName,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
    Spacer(modifier = Modifier.height(14.dp))
}

@Composable
private fun BacktestAllocationSnapshot(
    allocations: List<BacktestAllocationPoint>,
    eventName: String? = null,
    modifier: Modifier = Modifier
) {
    if (allocations.isEmpty() && eventName.isNullOrBlank()) return
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(PanelColor)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("해당 시점 비중", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.weight(1f))
            eventName?.takeIf { it.isNotBlank() }?.let {
                Text(it, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.End)
            }
        }
        allocations.take(5).forEach { item ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Box(
                    modifier = Modifier
                        .size(9.dp)
                        .clip(CircleShape)
                        .background(item.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    item.name,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                Text(formatWon(item.amount), color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(String.format(Locale.US, "%.1f%%", item.weight), color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun BacktestSelectedValue(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(PanelColor)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(label, color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        Text(value, color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun BacktestLegend(color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .width(58.dp)
                .height(18.dp)
                .background(color.copy(alpha = 0.12f))
                .padding(2.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Transparent))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Text("포트폴리오", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun BacktestAnnualReturnCard(annualReturns: List<Pair<Int, Double>>) {
    BacktestCard {
        Text("연도별 수익률", color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(16.dp))
        var selectedIndex by remember(annualReturns) { mutableStateOf<Int?>(null) }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .pointerInput(annualReturns) {
                    detectTapGestures { offset ->
                        if (annualReturns.isNotEmpty()) {
                            val index = ((offset.x / size.width) * annualReturns.lastIndex).roundToLong().toInt().coerceIn(0, annualReturns.lastIndex)
                            selectedIndex = index
                        }
                    }
                }
        ) {
            SimpleBarChart(values = annualReturns.map { it.second }, modifier = Modifier.fillMaxSize())
        }
        selectedIndex?.let { index ->
            val item = annualReturns.getOrNull(index)
            if (item != null) {
                val eventName = nasdaqStressEventForMonth("${item.first}-12")?.label
                Row(
                    modifier = Modifier
                        .padding(top = 12.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(PanelColor)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("${item.first}년", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        eventName?.let { Text(it, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold) }
                    }
                    Text(formatPercent(item.second / 100.0), color = if (item.second >= 0.0) PositiveRed else NegativeBlue, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
    Spacer(modifier = Modifier.height(14.dp))
}

private fun backtestChartIndexForOffset(x: Float, width: Float, count: Int, zoom: Float = 1f, pan: Float = 0f): Int {
    if (count <= 1) return 0
    val left = 82f
    val right = (width - 16f).coerceAtLeast(left + 1f)
    val chartWidth = (right - left).coerceAtLeast(1f)
    val ratio = ((x - left).coerceIn(0f, chartWidth) / chartWidth)
    val window = backtestVisibleWindow(count, zoom, pan)
    return (window.first + ratio * (window.last - window.first)).roundToInt().coerceIn(0, count - 1)
}

private fun backtestVisibleWindow(count: Int, zoom: Float, pan: Float): IntRange {
    if (count <= 1) return 0..0
    val safeZoom = zoom.coerceIn(1f, 120f)
    val visibleCount = (count / safeZoom).roundToInt().coerceIn(3.coerceAtMost(count), count)
    val maxStart = count - visibleCount
    val start = (pan.coerceIn(0f, 1f) * maxStart).roundToInt().coerceIn(0, maxStart)
    return start..(start + visibleCount - 1)
}

@Composable
private fun DetailedBacktestLineChart(
    values: List<Double>,
    labels: List<String>,
    color: Color,
    yAxisLabel: (Double) -> String,
    chartType: BacktestChartType,
    selectedIndex: Int?,
    valueLabel: (Double) -> String,
    zoom: Float = 1f,
    pan: Float = 0f,
    detailedAxes: Boolean = false,
    showInlineTooltip: Boolean = true,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (values.size < 2) return@Canvas
        val window = backtestVisibleWindow(values.size, zoom, pan)
        val visibleIndices = (window.first..window.last).toList()
        val visibleValues = visibleIndices.map { values[it] }
        if (visibleValues.size < 2) return@Canvas
        val labelPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            this.color = TextSecondary.toArgb()
            textSize = if (detailedAxes) 19f else 23f
            textAlign = android.graphics.Paint.Align.RIGHT
        }
        val xPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            this.color = TextSecondary.toArgb()
            textSize = if (detailedAxes) 16f else 22f
            textAlign = android.graphics.Paint.Align.RIGHT
        }
        val tooltipPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            this.color = Color.White.toArgb()
            textSize = 26f
            isFakeBoldText = true
        }

        val left = 82f
        val right = size.width - 16f
        val top = 16f
        val bottom = size.height - if (detailedAxes) 116f else 58f
        val chartWidth = (right - left).coerceAtLeast(1f)
        val chartHeight = (bottom - top).coerceAtLeast(1f)
        val rawMin = visibleValues.minOrNull() ?: 0.0
        val rawMax = visibleValues.maxOrNull() ?: 1.0
        val pricePadding = ((rawMax - rawMin).takeIf { it > 0.0 } ?: rawMax.coerceAtLeast(1.0)) * 0.08
        val minValue = when (chartType) {
            BacktestChartType.DRAWDOWN -> minOf(rawMin, -1.0)
            BacktestChartType.PRICE -> (rawMin - pricePadding).coerceAtLeast(0.0)
            else -> minOf(0.0, rawMin)
        }
        val maxValue = when (chartType) {
            BacktestChartType.DRAWDOWN -> 0.0
            BacktestChartType.PRICE -> rawMax + pricePadding
            else -> maxOf(rawMax, 1.0)
        }
        val range = (maxValue - minValue).takeIf { it != 0.0 } ?: 1.0

        fun xOf(index: Int): Float {
            val localIndex = (index - window.first).coerceIn(0, (window.last - window.first).coerceAtLeast(1))
            return left + chartWidth * (localIndex.toFloat() / (window.last - window.first).coerceAtLeast(1))
        }
        fun yOf(value: Double): Float = bottom - (chartHeight * ((value - minValue) / range).toFloat()).coerceIn(0f, chartHeight)

        val ySteps = if (detailedAxes && zoom >= 2f) 6 else if (detailedAxes) 5 else 4
        for (step in 0..ySteps) {
            val ratio = step / ySteps.toFloat()
            val y = top + chartHeight * ratio
            val value = maxValue - (range * ratio)
            drawLine(LineColor, Offset(left, y), Offset(right, y), strokeWidth = 1.5f)
            drawContext.canvas.nativeCanvas.drawText(yAxisLabel(value), left - 10f, y + 8f, labelPaint)
        }

        val xTickCount = minOf(if (detailedAxes) 6 else 7, visibleIndices.size)
        if (xTickCount > 1) {
            for (tick in 0 until xTickCount) {
                val index = (window.first + ((window.last - window.first).toDouble() * tick) / (xTickCount - 1)).roundToInt().coerceIn(window.first, window.last)
                val x = xOf(index)
                drawLine(LineColor, Offset(x, top), Offset(x, bottom), strokeWidth = 1f)
                val label = labels.getOrNull(index)?.replace("-", ".") ?: ""
                drawContext.canvas.nativeCanvas.save()
                drawContext.canvas.nativeCanvas.rotate(-42f, x, bottom + 48f)
                drawContext.canvas.nativeCanvas.drawText(label, x + 18f, bottom + 48f, xPaint)
                drawContext.canvas.nativeCanvas.restore()
            }
        }

        val points = visibleIndices.map { index -> Offset(xOf(index), yOf(values[index])) }
        val fillPath = Path().apply {
            moveTo(points.first().x, bottom)
            points.forEach { lineTo(it.x, it.y) }
            lineTo(points.last().x, bottom)
            close()
        }
        drawPath(fillPath, color.copy(alpha = if (chartType == BacktestChartType.DRAWDOWN) 0.22f else 0.16f))
        for (i in 0 until points.lastIndex) {
            drawLine(color, points[i], points[i + 1], strokeWidth = 4f, cap = StrokeCap.Round)
        }
        drawLine(Color(0xFFBFC6CE), Offset(left, bottom), Offset(right, bottom), strokeWidth = 2f)
        drawLine(Color(0xFFBFC6CE), Offset(left, top), Offset(left, bottom), strokeWidth = 2f)

        if (showInlineTooltip) selectedIndex?.coerceIn(0, values.lastIndex)?.takeIf { it in window }?.let { index ->
            val point = Offset(xOf(index), yOf(values[index]))
            drawCircle(Color.White, radius = 9f, center = point)
            drawCircle(color, radius = 7f, center = point)
            val text = "${labels.getOrNull(index).orEmpty()}  ${valueLabel(values[index])}"
            val rectWidth = minOf(size.width - 28f, tooltipPaint.measureText(text) + 34f)
            val rectHeight = 48f
            val rectX = (point.x - rectWidth / 2f).coerceIn(8f, size.width - rectWidth - 8f)
            val rectY = (point.y - 64f).coerceIn(8f, bottom - rectHeight - 8f)
            drawRoundRect(
                color = Color(0xDD111315),
                topLeft = Offset(rectX, rectY),
                size = androidx.compose.ui.geometry.Size(rectWidth, rectHeight),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(12f, 12f)
            )
            drawContext.canvas.nativeCanvas.drawText(text, rectX + 17f, rectY + 32f, tooltipPaint)
        }
    }
}

@Composable
private fun SimpleLineChart(values: List<Double>, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        if (values.size < 2) return@Canvas
        val minValue = values.minOrNull() ?: 0.0
        val maxValue = values.maxOrNull() ?: 1.0
        val range = (maxValue - minValue).takeIf { it != 0.0 } ?: 1.0
        val stepX = size.width / (values.size - 1).coerceAtLeast(1)
        val points = values.mapIndexed { index, value ->
            val x = index * stepX
            val y = size.height - ((value - minValue) / range * size.height).toFloat()
            Offset(x, y.coerceIn(0f, size.height))
        }
        for (i in 0 until points.lastIndex) {
            drawLine(color = color, start = points[i], end = points[i + 1], strokeWidth = 5f, cap = StrokeCap.Round)
        }
    }
}

@Composable
private fun SimpleBarChart(values: List<Double>, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        if (values.isEmpty()) return@Canvas
        val maxAbs = values.maxOf { kotlin.math.abs(it) }.takeIf { it > 0.0 } ?: 1.0
        val barWidth = size.width / values.size * 0.58f
        val centerY = size.height * 0.52f
        values.forEachIndexed { index, value ->
            val x = index * (size.width / values.size) + barWidth * 0.35f
            val height = (kotlin.math.abs(value) / maxAbs * size.height * 0.44f).toFloat()
            val top = if (value >= 0) centerY - height else centerY
            drawRoundRect(
                color = if (value >= 0) PositiveRed else NegativeBlue,
                topLeft = Offset(x, top),
                size = androidx.compose.ui.geometry.Size(barWidth, height.coerceAtLeast(3f))
            )
        }
        drawLine(color = LineColor, start = Offset(0f, centerY), end = Offset(size.width, centerY), strokeWidth = 2f)
    }
}

@Composable
private fun BacktestReportTable(rows: List<BacktestReportRow>) {
    BacktestCard {
        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("연도", "수익률", "손익", "MDD", "자산").forEach {
                Text(it, color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }
        Spacer(modifier = Modifier.height(10.dp))
        rows.forEach { row ->
            Row(modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("${row.year}", color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(formatPercent(row.finalReturn / 100.0), color = if (row.finalReturn < 0) NegativeBlue else PositiveRed, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(formatCompactWon(row.profit), color = if (row.profit < 0) NegativeBlue else PositiveRed, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(formatPercent(row.maxDrawdown / 100.0), color = NegativeBlue, fontSize = 12.sp, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
                Text(formatCompactWon(row.finalAsset), color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), textAlign = TextAlign.Center)
            }
        }
    }
}

private suspend fun calculateBacktestResult(
    context: Context,
    settings: AppSettings,
    startYear: Int,
    endYear: Int,
    startingMoney: Long,
    contributionEnabled: Boolean,
    contributionPeriod: String,
    contributionAmount: Long,
    dividendReinvest: Boolean,
    exchangeRateEnabled: Boolean,
    rebalanceEnabled: Boolean,
    rebalanceFrequency: String,
    assets: List<BacktestAssetUi>
): BacktestResultUi {
    val safeStart = startYear.coerceAtMost(endYear)
    val safeEnd = endYear.coerceAtLeast(safeStart)
    calculateCachedBacktestResult(
        context = context,
        startYear = safeStart,
        endYear = safeEnd,
        startingMoney = startingMoney,
        contributionEnabled = contributionEnabled,
        contributionPeriod = contributionPeriod,
        contributionAmount = contributionAmount,
        dividendReinvest = dividendReinvest,
        exchangeRateEnabled = exchangeRateEnabled,
        rebalanceEnabled = rebalanceEnabled,
        rebalanceFrequency = rebalanceFrequency,
        assets = assets
    )?.let { return it }

    return calculateSyntheticBacktestResult(
        context = context,
        settings = settings,
        startYear = safeStart,
        endYear = safeEnd,
        startingMoney = startingMoney,
        contributionEnabled = contributionEnabled,
        contributionPeriod = contributionPeriod,
        contributionAmount = contributionAmount,
        dividendReinvest = dividendReinvest,
        exchangeRateEnabled = exchangeRateEnabled,
        rebalanceEnabled = rebalanceEnabled,
        rebalanceFrequency = rebalanceFrequency,
        assets = assets
    )
}

private fun calculateCachedBacktestResult(
    context: Context,
    startYear: Int,
    endYear: Int,
    startingMoney: Long,
    contributionEnabled: Boolean,
    contributionPeriod: String,
    contributionAmount: Long,
    dividendReinvest: Boolean,
    exchangeRateEnabled: Boolean,
    rebalanceEnabled: Boolean,
    rebalanceFrequency: String,
    assets: List<BacktestAssetUi>
): BacktestResultUi? {
    if (assets.isEmpty() || startingMoney <= 0L) return null
    val priceMaps = assets.associate { asset ->
        asset.ticker to monthlyPricesFromDaily(loadHistoricalSeries(context, asset.ticker), startYear, endYear).toMap()
    }
    if (priceMaps.values.any { it.size < 2 }) return null
    val fxByMonth = monthlyPricesFromDaily(loadHistoricalSeries(context, "USDKRW"), startYear, endYear).toMap()
    val commonMonths = priceMaps.values
        .map { it.keys.toSet() }
        .reduceOrNull { acc, months -> acc.intersect(months) }
        .orEmpty()
        .sorted()
    if (commonMonths.size < 2) return null
    val fixedUsdKrw = commonMonths
        .firstOrNull()
        ?.let { fxByMonth[it] }
        ?: fxByMonth.values.firstOrNull()
        ?: DefaultUsdKrw

    fun monthYear(month: String): Int = month.take(4).toIntOrNull() ?: startYear
    fun monthNumber(month: String): Int = month.drop(4).take(2).toIntOrNull()?.coerceIn(1, 12) ?: 1
    fun priceKrw(asset: BacktestAssetUi, month: String): Double {
        val rawPrice = priceMaps[asset.ticker]?.get(month) ?: return 0.0
        val fx = when {
            isKoreanTicker(asset.ticker) -> 1.0
            exchangeRateEnabled -> fxByMonth[month] ?: fixedUsdKrw
            else -> fixedUsdKrw
        }
        return rawPrice * fx
    }
    fun valueAt(units: Map<String, Double>, month: String): Double =
        assets.sumOf { asset -> (units[asset.ticker] ?: 0.0) * priceKrw(asset, month) }
    fun allocationAt(units: Map<String, Double>, month: String, totalValue: Double): List<BacktestAllocationPoint> =
        assets.map { asset ->
            val amount = ((units[asset.ticker] ?: 0.0) * priceKrw(asset, month)).roundToLong()
            BacktestAllocationPoint(
                ticker = asset.ticker,
                name = asset.name,
                weight = if (totalValue > 0.0) amount.toDouble() / totalValue * 100.0 else 0.0,
                amount = amount,
                color = asset.color
            )
        }.sortedByDescending { it.amount }

    val units = mutableMapOf<String, Double>()
    val firstMonth = commonMonths.first()
    assets.forEach { asset ->
        val price = priceKrw(asset, firstMonth)
        if (price <= 0.0) return null
        units[asset.ticker] = startingMoney * (asset.weight / 100.0) / price
    }

    val monthlyAssets = mutableListOf<Long>()
    val drawdowns = mutableListOf<Double>()
    val labels = mutableListOf<String>()
    val monthlyReturns = mutableListOf<Double>()
    val monthlyAllocations = mutableListOf<List<BacktestAllocationPoint>>()
    val annualReturns = mutableListOf<Pair<Int, Double>>()
    val rows = mutableListOf<BacktestReportRow>()

    var previousValue = startingMoney.toDouble()
    var high = previousValue
    var currentYear = monthYear(firstMonth)
    var yearStart = previousValue
    var yearMdd = 0.0
    var totalContributed = 0.0

    commonMonths.forEachIndexed { index, month ->
        val year = monthYear(month)
        val monthOfYear = monthNumber(month)
        if (year != currentYear) {
            val yearReturn = if (yearStart > 0.0) (previousValue - yearStart) / yearStart * 100.0 else 0.0
            annualReturns.add(currentYear to yearReturn)
            rows.add(
                BacktestReportRow(
                    year = currentYear,
                    finalReturn = yearReturn,
                    profit = (previousValue - yearStart).roundToLong(),
                    maxDrawdown = yearMdd,
                    finalAsset = previousValue.roundToLong()
                )
            )
            currentYear = year
            yearStart = previousValue
            yearMdd = 0.0
        }

        if (index > 0 && dividendReinvest) {
            assets.forEach { asset ->
                val monthlyYield = estimatedMonthlyDividendYield(asset)
                if (monthlyYield > 0.0) {
                    units[asset.ticker] = (units[asset.ticker] ?: 0.0) * (1.0 + monthlyYield)
                }
            }
        }

        var contributed = 0.0
        if (index > 0 && contributionEnabled && contributionAmount > 0) {
            val shouldContribute = contributionPeriod == "월" || (contributionPeriod == "년" && monthOfYear == 12)
            if (shouldContribute) {
                contributed = contributionAmount.toDouble()
                totalContributed += contributed
                assets.forEach { asset ->
                    val price = priceKrw(asset, month)
                    if (price > 0.0) {
                        units[asset.ticker] = (units[asset.ticker] ?: 0.0) + contributed * (asset.weight / 100.0) / price
                    }
                }
            }
        }

        if (index > 0 && rebalanceEnabled && shouldRebalanceOnMonth(monthOfYear, rebalanceFrequency)) {
            val totalBeforeRebalance = valueAt(units, month)
            assets.forEach { asset ->
                val price = priceKrw(asset, month)
                if (price > 0.0) {
                    units[asset.ticker] = totalBeforeRebalance * (asset.weight / 100.0) / price
                }
            }
        }

        val currentValue = valueAt(units, month)
        if (!currentValue.isFinite() || currentValue <= 0.0) return null
        val monthlyReturn = if (index == 0 || previousValue <= 0.0) {
            0.0
        } else {
            (currentValue - previousValue - contributed) / previousValue
        }
        if (index > 0 && (monthlyReturn < -0.85 || monthlyReturn > 3.0)) return null
        monthlyReturns.add(monthlyReturn)
        high = maxOf(high, currentValue)
        val drawdown = ((currentValue - high) / high * 100.0).coerceAtMost(0.0)
        yearMdd = minOf(yearMdd, drawdown)
        monthlyAssets.add(currentValue.roundToLong())
        drawdowns.add(drawdown)
        labels.add("${month.take(4)}-${month.drop(4).take(2)}")
        monthlyAllocations.add(allocationAt(units, month, currentValue))
        previousValue = currentValue
    }

    val finalYearReturn = if (yearStart > 0.0) (previousValue - yearStart) / yearStart * 100.0 else 0.0
    annualReturns.add(currentYear to finalYearReturn)
    rows.add(
        BacktestReportRow(
            year = currentYear,
            finalReturn = finalYearReturn,
            profit = (previousValue - yearStart).roundToLong(),
            maxDrawdown = yearMdd,
            finalAsset = previousValue.roundToLong()
        )
    )

    return BacktestResultUi(
        monthlyAssets = monthlyAssets,
        monthlyDrawdowns = drawdowns,
        annualReturns = annualReturns.distinctBy { it.first },
        rows = rows.distinctBy { it.year },
        initialAmount = startingMoney,
        investedAmount = (startingMoney + totalContributed).roundToLong(),
        monthLabels = labels,
        monthlyReturns = monthlyReturns,
        monthlyAllocations = monthlyAllocations,
        usedHistoricalData = true
    )
}

private fun shouldRebalanceOnMonth(monthOfYear: Int, frequency: String): Boolean =
    when (frequency) {
        "매월" -> true
        "매분기" -> monthOfYear in setOf(3, 6, 9, 12)
        "매반기" -> monthOfYear in setOf(6, 12)
        else -> monthOfYear == 12
    }

private suspend fun calculateSyntheticBacktestResult(
    context: Context,
    settings: AppSettings,
    startYear: Int,
    endYear: Int,
    startingMoney: Long,
    contributionEnabled: Boolean,
    contributionPeriod: String,
    contributionAmount: Long,
    dividendReinvest: Boolean,
    exchangeRateEnabled: Boolean,
    rebalanceEnabled: Boolean,
    rebalanceFrequency: String,
    assets: List<BacktestAssetUi>
): BacktestResultUi {
    val safeStart = startYear.coerceAtMost(endYear)
    val safeEnd = endYear.coerceAtLeast(safeStart)
    val months = ((safeEnd - safeStart + 1) * 12).coerceAtLeast(12)
    val historicalReturns = fetchWeightedMonthlyReturns(context, settings, assets, safeStart, safeEnd)
    val assetValues = assets.associate { asset ->
        asset.ticker to startingMoney * (asset.weight / 100.0)
    }.toMutableMap()
    var amount = assetValues.values.sum().takeIf { it > 0.0 } ?: startingMoney.toDouble()
    var previousAmount = amount
    var high = amount
    var totalContributed = 0.0
    val monthlyAssets = mutableListOf<Long>()
    val drawdowns = mutableListOf<Double>()
    val monthLabels = mutableListOf<String>()
    val monthlyReturnValues = mutableListOf<Double>()
    val monthlyAllocations = mutableListOf<List<BacktestAllocationPoint>>()
    val annualReturns = mutableListOf<Pair<Int, Double>>()
    val rows = mutableListOf<BacktestReportRow>()
    var yearStart = amount
    var yearMdd = 0.0
    for (monthIndex in 0 until months) {
        val monthOfYear = monthIndex % 12 + 1
        val year = safeStart + monthIndex / 12
        var contributed = 0.0
        if (monthIndex > 0) {
            assets.forEach { asset ->
                val ticker = asset.ticker
                val assetReturn = syntheticAssetMonthlyReturn(
                    asset = asset,
                    monthIndex = monthIndex,
                    dividendReinvest = dividendReinvest,
                    exchangeRateEnabled = exchangeRateEnabled,
                    portfolioReturn = historicalReturns?.getOrNull(monthIndex - 1)
                )
                assetValues[ticker] = (assetValues[ticker] ?: 0.0) * (1.0 + assetReturn)
            }
        }
        if (monthIndex > 0 && contributionEnabled && contributionAmount > 0) {
            val shouldContribute = contributionPeriod == "월" || (contributionPeriod == "년" && monthOfYear == 12)
            if (shouldContribute) {
                contributed = contributionAmount.toDouble()
                totalContributed += contributed
                assets.forEach { asset ->
                    assetValues[asset.ticker] = (assetValues[asset.ticker] ?: 0.0) + contributed * (asset.weight / 100.0)
                }
            }
        }
        amount = assetValues.values.sum()
        if (monthIndex > 0 && rebalanceEnabled && shouldRebalanceOnMonth(monthOfYear, rebalanceFrequency) && amount > 0.0) {
            assets.forEach { asset ->
                assetValues[asset.ticker] = amount * (asset.weight / 100.0)
            }
            amount = assetValues.values.sum()
        }
        val monthlyReturn = if (monthIndex == 0 || previousAmount <= 0.0) {
            0.0
        } else {
            (amount - previousAmount - contributed) / previousAmount
        }
        monthlyReturnValues.add(monthlyReturn)
        high = kotlin.math.max(high, amount)
        val dd = ((amount - high) / high * 100.0).coerceAtMost(0.0)
        yearMdd = kotlin.math.min(yearMdd, dd)
        monthlyAssets.add(amount.roundToLong())
        drawdowns.add(dd)
        monthLabels.add("%04d-%02d".format(Locale.US, year, monthOfYear))
        monthlyAllocations.add(
            assets.map { asset ->
                val rawAmount = assetValues[asset.ticker] ?: 0.0
                val assetAmount = rawAmount.roundToLong()
                BacktestAllocationPoint(
                    ticker = asset.ticker,
                    name = asset.name,
                    weight = if (amount > 0.0) rawAmount / amount * 100.0 else 0.0,
                    amount = assetAmount,
                    color = asset.color
                )
            }.sortedByDescending { it.amount }
        )
        if (monthOfYear == 12) {
            val yearReturn = (amount - yearStart) / yearStart * 100.0
            annualReturns.add(year to yearReturn)
            rows.add(
                BacktestReportRow(
                    year = year,
                    finalReturn = yearReturn,
                    profit = (amount - yearStart).roundToLong(),
                    maxDrawdown = yearMdd,
                    finalAsset = amount.roundToLong()
                )
            )
            yearStart = amount
            yearMdd = 0.0
        }
        previousAmount = amount
    }
    return BacktestResultUi(
        monthlyAssets = monthlyAssets,
        monthlyDrawdowns = drawdowns,
        annualReturns = annualReturns,
        rows = rows,
        initialAmount = startingMoney,
        investedAmount = (startingMoney + totalContributed).roundToLong(),
        monthLabels = monthLabels,
        monthlyReturns = monthlyReturnValues,
        monthlyAllocations = monthlyAllocations,
        usedHistoricalData = historicalReturns != null
    )
}

private fun syntheticAssetMonthlyReturn(
    asset: BacktestAssetUi,
    monthIndex: Int,
    dividendReinvest: Boolean,
    exchangeRateEnabled: Boolean,
    portfolioReturn: Double?
): Double {
    val symbol = asset.ticker.uppercase(Locale.US)
    val leverage = when {
        symbol in setOf("TQQQ", "SOXL", "UPRO") -> 3.0
        symbol in setOf("QLD", "SSO") -> 2.0
        else -> 1.0
    }
    val base = when {
        symbol == "QLD" -> 0.010
        symbol in setOf("QQQ", "367380", "379810", "133690") -> 0.008
        symbol.contains("SCHD") -> 0.006
        else -> 0.005
    }
    val shock = when (monthIndex % 60) {
        14, 15 -> -0.055
        31 -> -0.080
        48 -> 0.045
        else -> 0.0
    }
    val hashOffset = kotlin.math.abs(symbol.hashCode() % 17)
    val noise = kotlin.math.sin((monthIndex + hashOffset) * 0.73) * 0.012
    val fxDrift = if (exchangeRateEnabled && !isKoreanTicker(symbol)) {
        kotlin.math.sin((monthIndex + 3) * 0.21) * 0.002
    } else {
        0.0
    }
    val source = portfolioReturn?.let { it * (0.55 + leverage * 0.35) + noise + fxDrift }
        ?: (base + shock * leverage + noise + fxDrift)
    return (source + if (dividendReinvest) estimatedMonthlyDividendYield(asset) else 0.0).coerceIn(-0.75, 0.65)
}

private fun estimatedMonthlyDividendYield(asset: BacktestAssetUi): Double =
    (estimatedAnnualDividendYield(asset.ticker) / 12.0).coerceIn(0.0, 0.03)

private fun estimatedAnnualDividendYield(ticker: String): Double {
    val symbol = ticker.uppercase(Locale.US)
    dividendCandidates().firstOrNull { it.ticker.equals(symbol, ignoreCase = true) }?.let {
        return (it.yieldRate / 100.0).coerceIn(0.0, 0.3)
    }
    return when (symbol) {
        "QLD" -> 0.0025
        "TQQQ", "SOXL", "UPRO" -> 0.0
        "SSO" -> 0.006
        "005930" -> 0.018
        "433880" -> 0.0
        else -> 0.0
    }
}

private fun formatCompactWon(value: Long): String {
    val abs = kotlin.math.abs(value)
    return when {
        abs >= 100_000_000L -> "${value / 100_000_000}억"
        abs >= 10_000L -> "${value / 10_000}만"
        else -> "${value}원"
    }
}

private fun formatAxisWon(value: Double): String {
    val rounded = value.roundToLong()
    val abs = kotlin.math.abs(rounded)
    return when {
        abs >= 100_000_000L -> "${NumberFormat.getNumberInstance(Locale.KOREA).format(rounded / 100_000_000)}억원"
        abs >= 10_000L -> "${NumberFormat.getNumberInstance(Locale.KOREA).format(rounded / 10_000)}만원"
        else -> "${NumberFormat.getNumberInstance(Locale.KOREA).format(rounded)}원"
    }
}

private fun formatNumberInput(value: String): String {
    val digits = value.filter { it.isDigit() }
    if (digits.isBlank()) return ""
    return NumberFormat.getNumberInstance(Locale.KOREA).format(digits.toLongOrNull() ?: 0L)
}

private val nasdaqCrisisEvents = listOf(
    NasdaqCrisisEvent(1973, "1973년 오일쇼크 긴축장", -0.31),
    NasdaqCrisisEvent(1974, "1974년 오일쇼크 침체장", -0.35),
    NasdaqCrisisEvent(1987, "1987년 블랙먼데이", -0.25),
    NasdaqCrisisEvent(1990, "1990년 경기침체 하락장", -0.24),
    NasdaqCrisisEvent(2000, "2000년 닷컴버블 붕괴", -0.39),
    NasdaqCrisisEvent(2001, "2001년 닷컴버블 후속 하락", -0.21),
    NasdaqCrisisEvent(2002, "2002년 닷컴버블 최종 투매", -0.31),
    NasdaqCrisisEvent(2008, "2008년 금융위기", -0.40),
    NasdaqCrisisEvent(2020, "2020년 코로나 쇼크", -0.30),
    NasdaqCrisisEvent(2022, "2022년 금리 인상장", -0.33)
)

private fun nasdaqStressEventForLabel(label: String): NasdaqCrisisEvent? =
    nasdaqCrisisEvents.firstOrNull { it.label == label }

private fun nasdaqStressEventForMonth(label: String): NasdaqCrisisEvent? {
    val year = label.take(4).toIntOrNull()
        ?: Regex("""\d{4}""").find(label)?.value?.toIntOrNull()
        ?: return null
    return nasdaqCrisisEvents.firstOrNull { it.year == year }
}

private fun stressScenarioDrop(ticker: String, scenario: String): Double {
    val symbol = ticker.uppercase(Locale.US)
    val leveraged = symbol in setOf("QLD", "TQQQ", "SOXL", "SSO", "UPRO")
    val nasdaqLike = symbol in setOf("QQQ", "TQQQ", "QLD", "367380")
    val domestic = isKoreanTicker(symbol)
    nasdaqStressEventForLabel(scenario)?.let { event ->
        val nasdaqDrop = event.nasdaqDrop
        return when {
            leveraged -> (nasdaqDrop * 1.85).coerceAtLeast(-0.85)
            nasdaqLike -> nasdaqDrop
            domestic -> (nasdaqDrop * 0.72).coerceAtLeast(-0.55)
            else -> (nasdaqDrop * 0.88).coerceAtLeast(-0.62)
        }
    }
    return when (scenario) {
        "코로나급 급락" -> when {
            symbol == "QLD" -> -0.52
            leveraged -> -0.65
            nasdaqLike -> -0.30
            domestic -> -0.28
            else -> -0.32
        }
        "2022년 금리인상장" -> when {
            symbol == "QLD" -> -0.58
            leveraged -> -0.70
            nasdaqLike -> -0.35
            domestic -> -0.25
            else -> -0.32
        }
        else -> when {
            symbol == "QLD" -> -0.75
            leveraged -> -0.85
            nasdaqLike -> -0.50
            domestic -> -0.45
            else -> -0.48
        }
    }
}

@Composable
private fun ReportScreen(accounts: List<AccountUi>, goalPlan: GoalPlan) {
    val holdings = portfolioHoldings(accounts)
    val total = accounts.sumOf { it.totalAmount }
    val principal = holdings.sumOf { it.principal }
    val profit = total - principal
    val profitRate = if (principal <= 0L) 0.0 else profit.toDouble() / principal
    val topHolding = holdings.maxByOrNull { it.amount }
    val topWeight = if (total <= 0L || topHolding == null) 0.0 else topHolding.amount.toDouble() / total
    val leverageWeight = if (total > 0L) {
        holdings
            .filter { it.ticker.uppercase(Locale.US) in setOf("QLD", "TQQQ", "SOXL", "SSO", "UPRO") }
            .sumOf { it.amount }
            .toDouble() / total
    } else 0.0
    val targetProgress = if (goalPlan.targetAmount > 0L) total.toDouble() / goalPlan.targetAmount else 0.0
    val stressCases = nasdaqCrisisEvents.map { it.label }
    val marketPosition = calculateMarketPosition(holdings)

    MainTabScreenColumn {
        AppHeader(title = "리포트", badge = "장기")
        LongTermStatusCard(
            profit = profit,
            profitRate = profitRate,
            goalPlan = goalPlan
        )
        Spacer(modifier = Modifier.height(22.dp))
        RiskMapCard(position = marketPosition)
        Spacer(modifier = Modifier.height(22.dp))
        MonthlyLongTermReportCard(
            holdings = holdings,
            total = total,
            principal = principal,
            goalPlan = goalPlan
        )
        Spacer(modifier = Modifier.height(22.dp))
        ReportCard {
            ReportCardTitle("장기 지속력 점검")
            Spacer(modifier = Modifier.height(10.dp))
            MetricRow("총 평가금액", formatWon(total))
            MetricRow("투입 원금", formatWon(principal))
            MetricRow("현재 수익률", formatPercent(profitRate))
            MetricRow("최대 보유 비중", topHolding?.let { "${assetDisplayName(it)} ${formatPercent(topWeight)}" } ?: "없음")
            MetricRow("최종 목표 달성률", formatPercent(targetProgress))
            MetricRow("레버리지 노출", formatPercent(leverageWeight))
            MetricRow("집중도 상태", if (topWeight >= 0.7) "높음" else if (topWeight >= 0.45) "보통" else "분산")
            MetricRow("목표까지 남은 금액", formatWon((goalPlan.targetAmount - total).coerceAtLeast(0L)))
        }
        Spacer(modifier = Modifier.height(22.dp))
        ReportCard {
            ReportCardTitle("위기 스트레스")
            Spacer(modifier = Modifier.height(10.dp))
            stressCases.forEach { label ->
                val stressLoss = holdings.sumOf { holding ->
                    (holding.amount * stressScenarioDrop(holding.ticker, label)).roundToLong()
                }
                val stressRate = if (total > 0L) stressLoss.toDouble() / total else 0.0
                MetricRow(label, "${formatSignedWon(stressLoss)} (${formatPercent(stressRate)})")
            }
            Spacer(modifier = Modifier.height(8.dp))
            DividerLine()
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "현재 보유금액에 위기별 나스닥 하락률을 종목 유형별 민감도로 곱한 스트레스 추정치입니다. 과거 데이터가 없는 종목도 제외하지 않고 레버리지, 나스닥형, 국내형, 기타형으로 분류해 보수적으로 반영합니다.",
                color = TextSecondary,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }
        Spacer(modifier = Modifier.height(22.dp))
        ReportCard {
            ReportCardTitle("이번달 점검")
            Spacer(modifier = Modifier.height(10.dp))
            MetricRow("리밸런싱 필요", if (topWeight > 0.65) "집중도 높음" else "정상")
            MetricRow("현금 비중", "${formatWon(accounts.sumOf { account -> account.holdings.filter { it.ticker == "KRW" }.sumOf { it.amount } })}")
            MetricRow("다음 확인", "가격 갱신 후 백테스트와 비교")
        }
    }
}

@Composable
private fun LongTermStatusCard(
    profit: Long,
    profitRate: Double,
    goalPlan: GoalPlan
) {
    ReportCard {
        ReportCardTitle("장기 투자 상태")
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            "${formatSignedWon(profit)} (${formatPercent(profitRate)})",
            color = if (profit >= 0L) PositiveRed else NegativeBlue,
            fontSize = 25.sp,
            lineHeight = 31.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "${goalPlan.years}년 계획 · ${elapsedPlanText(goalPlan.startDate)} · 목표 ${formatWon(goalPlan.targetAmount)}",
            color = TextSecondary,
            fontSize = 14.sp,
            lineHeight = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun MonthlyLongTermReportCard(
    holdings: List<HoldingUi>,
    total: Long,
    principal: Long,
    goalPlan: GoalPlan
) {
    val now = LocalDate.now()
    val plannedNow = expectedPlanValue(total, goalPlan)
    val pathGap = total - plannedNow
    val pathRate = if (plannedNow > 0L) pathGap.toDouble() / plannedNow else 0.0
    val topHolding = holdings.maxByOrNull { it.amount }
    val topWeight = if (total > 0L && topHolding != null) topHolding.amount.toDouble() / total else 0.0
    val profitRate = if (principal > 0L) (total - principal).toDouble() / principal else 0.0
    val worstStress = nasdaqCrisisEvents.minOfOrNull { event ->
        holdings.sumOf { holding -> (holding.amount * stressScenarioDrop(holding.ticker, event.label)).roundToLong() }
    } ?: 0L
    val worstStressRate = if (total > 0L) worstStress.toDouble() / total else 0.0
    val monthlyMessage = when {
        holdings.isEmpty() -> "먼저 자산을 입력하면 월간 리포트가 살아납니다."
        pathRate < -0.10 -> "목표 경로에서 10% 이상 이탈했습니다. 적립금, 비중, 기대수익률을 다시 점검하세요."
        topWeight > 0.70 -> "단일 종목 비중이 큽니다. 장기 보유 가능성을 리밸런싱 전제로 다시 확인하세요."
        worstStressRate < -0.55 -> "위기 구간 손실 체감이 큽니다. 백테스트와 위기 스트레스 지표로 낙폭을 미리 봐두세요."
        else -> "이번 달은 계획 유지 구간입니다. 가격보다 비중과 경로를 먼저 확인하세요."
    }

    ReportCard {
        ReportCardTitle("월간 장기투자 리포트")
        Spacer(modifier = Modifier.height(6.dp))
        Text("${now.year}년 ${now.monthValue}월 기준", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(14.dp))
        MetricRow("목표 경로 차이", "${formatSignedWon(pathGap)} (${formatPercent(pathRate)})")
        MetricRow("현재 수익률", formatPercent(profitRate))
        MetricRow("최대 비중", topHolding?.let { "${assetDisplayName(it)} ${formatPercent(topWeight)}" } ?: "없음")
        MetricRow("위기 예상 손실", "${formatSignedWon(worstStress)} (${formatPercent(worstStressRate)})")
        Spacer(modifier = Modifier.height(10.dp))
        DividerLine()
        Spacer(modifier = Modifier.height(10.dp))
        Text(monthlyMessage, color = TextPrimary, fontSize = 14.sp, lineHeight = 20.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun ReportCardTitle(title: String) {
    Text(title, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
}

@Composable
private fun ReportCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SoftSurface),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp), content = content)
    }
}

@Composable
private fun ScreenColumn(
    modifier: Modifier = Modifier,
    topPadding: Int = 18,
    scrollState: androidx.compose.foundation.ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(scrollState)
            .padding(start = 24.dp, top = topPadding.dp, end = 24.dp, bottom = 70.dp),
        content = content
    )
}

@Composable
private fun MainTabScreenColumn(
    modifier: Modifier = Modifier,
    scrollState: androidx.compose.foundation.ScrollState = rememberScrollState(),
    content: @Composable ColumnScope.() -> Unit
) {
    val statusTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppBackground)
            .verticalScroll(scrollState)
            .padding(start = 24.dp, top = statusTop + 12.dp, end = 24.dp, bottom = 70.dp),
        content = content
    )
}

@Composable
private fun PlainTopBar(title: String, onBack: () -> Unit, rightText: String, onRightClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("‹", color = TextPrimary, fontSize = 38.sp, modifier = Modifier.clickable(onClick = onBack))
        Spacer(modifier = Modifier.weight(1f))
        Text(title, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.weight(1f))
        Text(rightText, color = TextPrimary, fontSize = 18.sp, modifier = Modifier.clickable(onClick = onRightClick))
    }
    Spacer(modifier = Modifier.height(32.dp))
}

@Composable
private fun appTextFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = PanelColor,
    unfocusedContainerColor = SoftSurface,
    focusedBorderColor = BrandGreen,
    unfocusedBorderColor = Color.Transparent,
    cursorColor = BrandGreen,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedPlaceholderColor = MutedText,
    unfocusedPlaceholderColor = MutedText
)

@Composable
private fun AppHeader(
    title: String,
    badge: String? = null,
    onMenuClick: (() -> Unit)? = null,
    onRefreshClick: (() -> Unit)? = null,
    isRefreshing: Boolean = false
) {
    val refreshRotation by animateFloatAsState(
        targetValue = if (isRefreshing) 360f else 0f,
        animationSpec = tween(700),
        label = "refreshRotation"
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(38.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        LogoMark(markSize = 28)
        Spacer(modifier = Modifier.width(9.dp))
        Text(title, color = TextPrimary, fontSize = 25.sp, lineHeight = 30.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.weight(1f))
        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(22.dp))
                    .background(BrandSoftBlue.copy(alpha = if (PanelColor == Color.White) 0.42f else 0.22f))
                    .padding(horizontal = 13.dp, vertical = 7.dp)
            ) {
                Text(badge, color = BrandGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.width(16.dp))
        }
        if (onRefreshClick != null) {
            Text(
                "↻",
                color = TextPrimary,
                fontSize = 31.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .rotate(refreshRotation)
                    .clickable(onClick = onRefreshClick)
            )
            Spacer(modifier = Modifier.width(18.dp))
        }
        if (onMenuClick != null) {
            Text("☰", color = TextPrimary, fontSize = 30.sp, modifier = Modifier.clickable(onClick = onMenuClick))
        }
    }
    Spacer(modifier = Modifier.height(18.dp))
}

@Composable
private fun AccountDetailHeader(account: AccountUi, onBack: () -> Unit, onMenuClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Text("‹", color = TextPrimary, fontSize = 36.sp, modifier = Modifier.clickable(onClick = onBack))
        Spacer(modifier = Modifier.width(8.dp))
        BrokerBadge(accountBadgeText(account), account.color, size = 36)
        Spacer(modifier = Modifier.width(10.dp))
        Text(account.name, color = TextPrimary, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        Spacer(modifier = Modifier.width(10.dp))
        Text("☰", color = TextPrimary, fontSize = 32.sp, modifier = Modifier.clickable(onClick = onMenuClick))
    }
    Spacer(modifier = Modifier.height(42.dp))
}

@Composable
private fun BigAssetAmount(
    amount: Long,
    profit: Long,
    rate: Double,
    label: String,
    onToggleProfitMode: (() -> Unit)? = null
) {
    Text(formatWon(amount), color = TextPrimary, fontSize = 34.sp, lineHeight = 40.sp, fontWeight = FontWeight.ExtraBold)
    Spacer(modifier = Modifier.height(7.dp))
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = "${formatSignedWon(profit)} (${formatPercent(rate)}) $label",
            color = if (profit < 0) NegativeBlue else PositiveRed,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        if (onToggleProfitMode != null) {
            Spacer(modifier = Modifier.width(7.dp))
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(SoftSurface)
                    .clickable(onClick = onToggleProfitMode),
                contentAlignment = Alignment.Center
            ) {
                Text("!", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun QuickMetricRow() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        listOf("계좌", "수익", "세금", "배당", "비중").forEach { QuickMetric(it) }
    }
}

@Composable
private fun QuickMetric(label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text("■", color = Color(0xFFB7C0C9), fontSize = 20.sp)
        Spacer(modifier = Modifier.height(7.dp))
        Text(label, color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TabGlyph(text: String, selected: Boolean) {
    Box(
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape)
            .background(if (selected) TextPrimary else SoftSurface),
        contentAlignment = Alignment.Center
    ) {
        Text(text = text, color = if (selected) Color.White else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun LogoMark(
    markSize: Int = 30,
    alpha: Float = 1f,
    rotation: Float = 0f,
    loadingProgress: Float? = null
) {
    val painter = painterResource(id = R.drawable.snowball_mark)
    val progress = loadingProgress?.coerceIn(0f, 1f)
    if (progress == null) {
        Image(
            painter = painter,
            contentDescription = null,
            modifier = Modifier.size(markSize.dp).rotate(rotation),
            alpha = alpha
        )
    } else {
        Box(
            modifier = Modifier
                .size(markSize.dp)
                .rotate(rotation)
        ) {
            Image(
                painter = painter,
                contentDescription = null,
                modifier = Modifier.matchParentSize(),
                colorFilter = ColorFilter.tint(Color(0xFFDDE2E8))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height((markSize * progress).dp)
                    .clipToBounds()
            ) {
                Image(
                    painter = painter,
                    contentDescription = null,
                    modifier = Modifier
                        .size(markSize.dp)
                        .align(Alignment.BottomCenter),
                    colorFilter = ColorFilter.tint(Color(0xFF8E98A3))
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String) {
    Text(title, color = TextPrimary, fontSize = 25.sp, fontWeight = FontWeight.ExtraBold)
    Spacer(modifier = Modifier.height(13.dp))
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun GoalProgressCard(
    accounts: List<AccountUi>,
    totalAmount: Long,
    principal: Long,
    plan: GoalPlan,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val accountSnapshot = accounts.toList()
    val chartPoints = remember(accountSnapshot, plan, totalAmount) {
        goalChartPreviewPoints(accountSnapshot, plan, totalAmount)
    }
    val currentReturnRate = if (principal <= 0L) 0.0 else (totalAmount - principal).toDouble() / principal
    val annualTargetRate = (plan.annualTargetReturn / 100.0).takeIf { it > 0.0 } ?: 1.0
    val annualProgress = (currentReturnRate / annualTargetRate).toFloat().coerceIn(0f, 1.5f)
    val finalProgress = if (plan.targetAmount <= 0L) 0f else (totalAmount.toFloat() / plan.targetAmount).coerceIn(0f, 1.5f)
    val annualPercentText = String.format(Locale.US, "%.1f", annualProgress * 100f)
    val finalPercentText = String.format(Locale.US, "%.1f", finalProgress * 100f)
    val start = parseAppDate(plan.startDate)
    val annualRange = start?.let { "${formatKoreanDate(it)}~${formatKoreanDate(it.plusYears(1))}" }.orEmpty()
    val finalRange = start?.let { "${formatKoreanDate(it)}~${formatKoreanDate(it.plusYears(plan.years.toLong()))}" }.orEmpty()
    val elapsedYears = elapsedYearsFromStart(plan.startDate).coerceIn(0.0, plan.years.toDouble())
    val plannedNow = plannedValueAtYear(plan, elapsedYears).roundToLong().coerceAtLeast(1L)
    val pathGap = totalAmount - plannedNow
    val pathGapRate = pathGap.toDouble() / plannedNow
    val pathStatus = when {
        pathGapRate >= 0.05 -> "목표 경로보다 ${formatUnsignedPercent(pathGapRate)} 앞서고 있어요"
        pathGapRate <= -0.05 -> "목표 경로보다 ${formatUnsignedPercent(pathGapRate)} 뒤처져 있어요"
        else -> "목표 경로 안에서 움직이고 있어요"
    }
    val pathStatusColor = when {
        pathGapRate >= 0.05 -> PositiveRed
        pathGapRate <= -0.05 -> NegativeBlue
        else -> BrandGreen
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = SoftSurface),
        shape = RoundedCornerShape(22.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.combinedClickable(onClick = onClick, onLongClick = onLongClick)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${plan.years}년 계획 · 시작 ${plan.startDate}", color = TextSecondary, fontSize = 14.sp, modifier = Modifier.weight(1f))
                Text(elapsedPlanText(plan.startDate), color = BrandGreen, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(7.dp))
            Text("계획 성장률", color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(5.dp))
            Text("연 목표 ${formatDecimal(plan.annualTargetReturn)}% · 최종 목표 ${formatWon(plan.targetAmount)}", color = TextSecondary, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(PanelColor.copy(alpha = if (PanelColor == Color.White) 0.72f else 0.36f))
                    .padding(14.dp)
            ) {
                Text("목표 경로 이탈 감지", color = TextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(5.dp))
                Text(pathStatus, color = pathStatusColor, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    "현재 ${formatWon(totalAmount)} · 계획 ${formatWon(plannedNow)} · 차이 ${formatSignedWon(pathGap)}",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
            GoalProgressLine("연 목표 수익률 달성률($annualRange)", annualPercentText, annualProgress)
            Spacer(modifier = Modifier.height(12.dp))
            GoalProgressLine("최종 목표 금액 달성률($finalRange)", finalPercentText, finalProgress)
            Spacer(modifier = Modifier.height(18.dp))
            GoalGrowthChart(points = chartPoints, plan = plan)
        }
    }
}

@Composable
private fun GoalProgressLine(label: String, percentText: String, progress: Float) {
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(label, color = TextSecondary, fontSize = 13.sp, modifier = Modifier.weight(1f))
            Text("$percentText%", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        }
        Spacer(modifier = Modifier.height(7.dp))
        Box(modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(999.dp)).background(Color(0xFFE1E5EA))) {
            Box(modifier = Modifier.fillMaxWidth(progress.coerceIn(0.02f, 1f)).height(8.dp).clip(RoundedCornerShape(999.dp)).background(BrandGreen))
        }
    }
}

@Composable
private fun GoalGrowthChart(points: List<GoalChartPoint>, plan: GoalPlan) {
    val maxValue = maxOf(
        plan.targetAmount.toDouble(),
        points.maxOfOrNull { maxOf(it.target, it.actual ?: 0.0) } ?: 1.0,
        1.0
    )

    Canvas(modifier = Modifier.fillMaxWidth().height(116.dp)) {
        val left = 8f
        val right = size.width - 8f
        val top = 12f
        val bottom = size.height - 22f

        drawLine(Color(0xFFDDE2E8), Offset(left, bottom), Offset(right, bottom), strokeWidth = 2f)
        drawLine(Color(0xFFDDE2E8), Offset(left, top), Offset(left, bottom), strokeWidth = 2f)

        fun xOf(index: Int): Float = left + ((right - left) * (index.toFloat() / (points.lastIndex).coerceAtLeast(1)))
        fun yOf(value: Double): Float = bottom - ((bottom - top) * (value / maxValue).toFloat()).coerceIn(0f, bottom - top)

        val targetPoints = points.mapIndexed { index, point -> Offset(xOf(index), yOf(point.target)) }
        for (index in 0 until targetPoints.lastIndex) {
            drawLine(Color(0xFF555B64), targetPoints[index], targetPoints[index + 1], strokeWidth = 4f, cap = StrokeCap.Round)
        }

        val actualPoints = points.mapIndexedNotNull { index, point ->
            point.actual?.takeIf { it > 0.0 }?.let { Offset(xOf(index), yOf(it)) }
        }
        for (index in 0 until actualPoints.lastIndex) {
            drawLine(NegativeBlue, actualPoints[index], actualPoints[index + 1], strokeWidth = 4f, cap = StrokeCap.Round)
        }
        actualPoints.lastOrNull()?.let { drawCircle(NegativeBlue, radius = 7f, center = it) }
    }
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("0년", color = TextSecondary, fontSize = 11.sp)
        Text("${plan.years}년", color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
private fun GoalProgressDetailDialog(
    accounts: List<AccountUi>,
    plan: GoalPlan,
    fallbackTotal: Long,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val accountSnapshot = accounts.toList()
    val previewPoints = remember(accountSnapshot, plan, fallbackTotal) {
        goalChartPreviewPoints(accountSnapshot, plan, fallbackTotal)
    }
    var points by remember(accountSnapshot, plan, fallbackTotal) { mutableStateOf(previewPoints) }
    var isLoading by remember(accountSnapshot, plan, fallbackTotal) { mutableStateOf(true) }
    LaunchedEffect(accountSnapshot, plan, fallbackTotal) {
        isLoading = true
        points = previewPoints
        val safeAccounts = accountSnapshot
        points = withContext(Dispatchers.Default) {
            goalChartPoints(context.applicationContext, safeAccounts, plan, fallbackTotal)
        }
        isLoading = false
    }
    var selectedIndex by remember(points) { mutableStateOf(points.indexOfLast { it.actual != null }.coerceAtLeast(0)) }
    val selected = points.getOrNull(selectedIndex)
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelColor,
        shape = RoundedCornerShape(28.dp),
        title = { Text("계획 성장률", color = TextPrimary, fontWeight = FontWeight.ExtraBold) },
        text = {
            Column {
                Text(
                    if (isLoading) "월별 평가액을 계산하고 있어요." else "목표금액 곡선과 월별 실제 평가액을 함께 봅니다.",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    lineHeight = 19.sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                GoalDetailChart(
                    points = points,
                    selectedIndex = selectedIndex,
                    onSelect = { selectedIndex = it }
                )
                Spacer(modifier = Modifier.height(12.dp))
                selected?.let {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(SoftSurface)
                            .padding(14.dp)
                    ) {
                        Text(it.label, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("평가액 ${it.actual?.roundToLong()?.let(::formatWon) ?: "-"}", color = NegativeBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("목표액 ${formatWon(it.target.roundToLong())}", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        },
        confirmButton = { TextButton(onClick = onDismiss) { Text("닫기", color = BrandGreen) } }
    )
}

@Composable
private fun GoalDetailChart(points: List<GoalChartPoint>, selectedIndex: Int, onSelect: (Int) -> Unit) {
    val maxValue = maxOf(points.maxOfOrNull { maxOf(it.target, it.actual ?: 0.0) } ?: 1.0, 1.0)
    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(240.dp)
            .pointerInput(points) {
                detectTapGestures { offset ->
                    if (points.isNotEmpty()) {
                        onSelect(backtestChartIndexForOffset(offset.x, size.width.toFloat(), points.size))
                    }
                }
            }
    ) {
        if (points.size < 2) return@Canvas
        val left = 82f
        val right = size.width - 16f
        val top = 18f
        val bottom = size.height - 56f
        val width = (right - left).coerceAtLeast(1f)
        val height = (bottom - top).coerceAtLeast(1f)
        val labelPaint = android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG).apply {
            color = TextSecondary.toArgb()
            textSize = 22f
            textAlign = android.graphics.Paint.Align.RIGHT
        }
        fun xOf(index: Int): Float = left + width * (index.toFloat() / points.lastIndex.coerceAtLeast(1))
        fun yOf(value: Double): Float = bottom - (height * (value / maxValue).toFloat()).coerceIn(0f, height)
        repeat(4) { step ->
            val ratio = step / 3f
            val y = top + height * ratio
            drawLine(LineColor, Offset(left, y), Offset(right, y), strokeWidth = 1.3f)
            val value = maxValue * (1f - ratio)
            drawContext.canvas.nativeCanvas.drawText(formatAxisWon(value.toDouble()), left - 8f, y + 7f, labelPaint)
        }
        val target = points.mapIndexed { index, point -> Offset(xOf(index), yOf(point.target)) }
        for (index in 0 until target.lastIndex) {
            drawLine(Color(0xFF555B64), target[index], target[index + 1], strokeWidth = 4f, cap = StrokeCap.Round)
        }
        val actual = points.mapIndexedNotNull { index, point -> point.actual?.takeIf { it > 0.0 }?.let { Offset(xOf(index), yOf(it)) } }
        for (index in 0 until actual.lastIndex) {
            drawLine(NegativeBlue, actual[index], actual[index + 1], strokeWidth = 4f, cap = StrokeCap.Round)
        }
        val selectedPoint = points.getOrNull(selectedIndex)
        selectedPoint?.let {
            val x = xOf(selectedIndex)
            drawLine(Color(0x993C4652), Offset(x, top), Offset(x, bottom), strokeWidth = 2f)
            it.actual?.takeIf { actualValue -> actualValue > 0.0 }?.let { actualValue ->
                drawCircle(Color.White, radius = 8f, center = Offset(x, yOf(actualValue)))
                drawCircle(NegativeBlue, radius = 6f, center = Offset(x, yOf(actualValue)))
            }
            drawCircle(Color.White, radius = 8f, center = Offset(x, yOf(it.target)))
            drawCircle(Color(0xFF555B64), radius = 6f, center = Offset(x, yOf(it.target)))
        }
    }
}

@Composable
private fun GoalPlanDialog(initial: GoalPlan, onDismiss: () -> Unit, onSave: (GoalPlan) -> Unit) {
    var startDate by remember { mutableStateOf(initial.startDate) }
    var years by remember { mutableStateOf(initial.years.toString()) }
    var annualTargetReturn by remember { mutableStateOf(initial.annualTargetReturn.toString()) }
    var targetAmount by remember { mutableStateOf(initial.targetAmount.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PanelColor,
        shape = RoundedCornerShape(28.dp),
        title = {
            Text(
                text = "장기투자 목표 설정",
                color = TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        },
        text = {
            Column {
                GoalInputField(
                    label = "시작 날짜",
                    value = startDate,
                    onValueChange = { startDate = it },
                    placeholder = "예: 2026. 6. 28."
                )
                Spacer(modifier = Modifier.height(12.dp))
                GoalInputField(
                    label = "장기투자 계획",
                    value = years,
                    onValueChange = { years = onlyDigits(it) },
                    placeholder = "20",
                    suffix = "년",
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(12.dp))
                GoalInputField(
                    label = "연 목표 상승률",
                    value = annualTargetReturn,
                    onValueChange = { annualTargetReturn = it.filter { ch -> ch.isDigit() || ch == '.' } },
                    placeholder = "12",
                    suffix = "%",
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(12.dp))
                GoalInputField(
                    label = "목표 자산",
                    value = formatNumberInput(targetAmount),
                    onValueChange = { targetAmount = onlyDigits(it) },
                    placeholder = "1000000000",
                    suffix = "원",
                    keyboardType = KeyboardType.Number
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "계획 성장률은 장기투자 기간과 연 목표 상승률을 기준으로 현재 자산이 계획 경로를 따라가고 있는지 보여줍니다.",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 18.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        GoalPlan(
                            startDate = startDate.ifBlank { initial.startDate },
                            years = years.toIntOrNull()?.coerceIn(1, 50) ?: initial.years,
                            annualTargetReturn = annualTargetReturn.toDoubleOrNull()?.coerceIn(0.0, 100.0) ?: initial.annualTargetReturn,
                            targetAmount = targetAmount.toLongOrNull()?.coerceAtLeast(1L) ?: initial.targetAmount
                        )
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TextPrimary),
                modifier = Modifier.width(112.dp).height(52.dp)
            ) {
                Text("저장", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SoftSurface, contentColor = TextPrimary),
                modifier = Modifier.width(112.dp).height(52.dp)
            ) {
                Text("취소", fontSize = 17.sp, fontWeight = FontWeight.Bold)
            }
        }
    )
}

@Composable
private fun GoalInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    suffix: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        Text(label, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            singleLine = true,
            placeholder = { Text(placeholder) },
            trailingIcon = suffix?.let {
                { Text(it, color = TextSecondary, fontSize = 13.sp, modifier = Modifier.padding(end = 4.dp)) }
            },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            shape = RoundedCornerShape(18.dp),
            colors = appTextFieldColors(),
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun HoldingRow(holding: HoldingUi) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
        AssetBadge(holding.ticker.take(1), holding.color)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(assetDisplayName(holding), color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${formatQuantity(holding.quantity)}주", color = TextSecondary, fontSize = 15.sp)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(formatWon(holding.amount), color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text(
                "${formatSignedWon(holding.dayProfit)} (${formatPercent(holding.dayRate)})",
                color = if (holding.dayProfit < 0) NegativeBlue else PositiveRed,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun InvestmentToolbar(
    investmentMode: String,
    profitMode: String,
    sortLabel: String,
    onInvestmentModeChange: (String) -> Unit,
    onProfitModeChange: (String) -> Unit,
    onSortClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        SegmentedPill(
            left = "시세",
            right = "평가",
            selectedLeft = investmentMode == InvestmentMode.QUOTE,
            onLeftClick = { onInvestmentModeChange(InvestmentMode.QUOTE) },
            onRightClick = { onInvestmentModeChange(InvestmentMode.VALUATION) }
        )
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (investmentMode == InvestmentMode.VALUATION) {
                SortPill(
                    text = if (profitMode == ProfitMode.TOTAL) "총 수익" else "일간 수익",
                    onClick = {
                        onProfitModeChange(if (profitMode == ProfitMode.TOTAL) ProfitMode.DAY else ProfitMode.TOTAL)
                    }
                )
                Spacer(modifier = Modifier.width(8.dp))
            }
            SortPill(sortLabel, onClick = onSortClick)
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun InvestmentHoldingRow(
    holding: HoldingUi,
    investmentMode: String,
    profitMode: String,
    onClick: () -> Unit = {},
    onLongClick: () -> Unit = {}
) {
    val dayRate = holding.dayRate
    val dayProfit = holding.dayProfit
    val totalProfit = holding.amount - holding.principal
    val totalRate = if (holding.principal == 0L) 0.0 else totalProfit.toDouble() / holding.principal
    val activeProfit = if (profitMode == ProfitMode.TOTAL) totalProfit else dayProfit
    val activeRate = if (profitMode == ProfitMode.TOTAL) totalRate else dayRate
    val activeColor = if (activeProfit < 0) NegativeBlue else PositiveRed

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(onClick = onClick, onLongClick = onLongClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AssetBadge(holding.ticker.take(1), holding.color)
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(assetDisplayName(holding), color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(
                text = if (investmentMode == InvestmentMode.QUOTE) formatAssetPrice(holding.averagePrice, holding.ticker) else "${formatQuantity(holding.quantity)}주",
                color = TextSecondary,
                fontSize = 14.sp
            )
        }
        if (investmentMode == InvestmentMode.QUOTE) {
            MiniPriceChart(negative = dayRate < 0)
            Spacer(modifier = Modifier.width(20.dp))
            Column(horizontalAlignment = Alignment.End) {
                Text(formatAssetPrice(holding.currentPrice, holding.ticker), color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                Text(
                    "${formatPercent(dayRate)}",
                    color = if (dayRate < 0) NegativeBlue else PositiveRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            Column(horizontalAlignment = Alignment.End) {
                Text(formatWon(holding.amount), color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                Text("${formatSignedWon(activeProfit)} (${formatPercent(activeRate)})", color = activeColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MiniPriceChart(negative: Boolean) {
    Canvas(modifier = Modifier.width(82.dp).height(42.dp)) {
        val color = if (negative) NegativeBlue else PositiveRed
        val dash = PathEffect.dashPathEffect(floatArrayOf(5f, 7f), 0f)
        drawLine(
            color = MutedText.copy(alpha = 0.55f),
            start = Offset(0f, size.height * 0.22f),
            end = Offset(size.width, size.height * 0.22f),
            strokeWidth = 2.4f,
            pathEffect = dash
        )
        drawLine(
            color = MutedText.copy(alpha = 0.45f),
            start = Offset(0f, size.height * 0.78f),
            end = Offset(size.width, size.height * 0.78f),
            strokeWidth = 2.2f,
            pathEffect = dash
        )
        val points = if (negative) {
            listOf(0.58f, 0.44f, 0.66f, 0.50f, 0.61f, 0.30f, 0.24f, 0.37f, 0.34f, 0.64f)
        } else {
            listOf(0.68f, 0.54f, 0.58f, 0.39f, 0.44f, 0.33f, 0.26f, 0.31f, 0.22f, 0.25f)
        }
        for (index in 0 until points.lastIndex) {
            val start = Offset(size.width * index / points.lastIndex, size.height * points[index])
            val end = Offset(size.width * (index + 1) / points.lastIndex, size.height * points[index + 1])
            drawLine(color = color, start = start, end = end, strokeWidth = 5.5f, cap = StrokeCap.Round)
        }
    }
}

@Composable
private fun AssetBadge(text: String, color: Color, size: Int = 38) {
    Box(modifier = Modifier.size(size.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
        Text(text, color = Color.White, fontSize = (size * 0.37f).sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun BrokerBadge(text: String, color: Color, size: Int = 48) {
    val actualSize = (size * 4 / 5).coerceAtLeast(24)
    Box(modifier = Modifier.size(actualSize.dp).clip(CircleShape).background(color), contentAlignment = Alignment.Center) {
        Text(text.take(2), color = Color.White, fontSize = (actualSize / 4).sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun SelectionCircle(selected: Boolean) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(if (selected) TextPrimary else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        if (selected) {
            Text("✓", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        } else {
            Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(Color.Transparent))
            Canvas(modifier = Modifier.size(36.dp)) {
                drawCircle(Color(0xFFE5E8EB), style = androidx.compose.ui.graphics.drawscope.Stroke(width = 3f))
            }
        }
    }
}

@Composable
private fun DrawerMenuRow(glyph: String, title: String, enabled: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(glyph, color = if (enabled) TextSecondary else Color(0xFFC8CDD3), fontSize = 25.sp, modifier = Modifier.width(54.dp), textAlign = TextAlign.Center)
        Text(title, color = if (enabled) TextPrimary else Color(0xFFC8CDD3), fontSize = 17.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SheetActionRow(glyph: String, title: String, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 20.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(glyph, color = Color(0xFFB7C0C9), fontSize = 28.sp, modifier = Modifier.width(56.dp))
        Text(title, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun AssetOptionRow(rank: Int, asset: AssetOption, onClick: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) {
        Text("$rank", color = TextSecondary, fontSize = 19.sp, fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp))
        AssetBadge(asset.ticker.take(1), asset.color)
        Spacer(modifier = Modifier.width(15.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(asset.ticker, color = TextPrimary, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(asset.name, color = TextSecondary, fontSize = 15.sp)
        }
        Text("+", color = Color(0xFFB7C0C9), fontSize = 36.sp)
    }
}

@Composable
private fun SearchTab(label: String, selected: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(12.dp))
        Box(modifier = Modifier.width(68.dp).height(3.dp).background(if (selected) TextPrimary else Color.Transparent))
    }
}

@Composable
private fun SegmentedPill(
    left: String,
    right: String,
    selectedLeft: Boolean,
    onLeftClick: () -> Unit,
    onRightClick: () -> Unit
) {
    Row(modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(SoftSurface).padding(4.dp)) {
        Text(
            left,
            color = if (selectedLeft) TextPrimary else TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(if (selectedLeft) PanelColor else Color.Transparent)
                .clickable(onClick = onLeftClick)
                .padding(horizontal = 13.dp, vertical = 7.dp)
        )
        Text(
            right,
            color = if (selectedLeft) TextSecondary else TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .clip(RoundedCornerShape(999.dp))
                .background(if (selectedLeft) Color.Transparent else PanelColor)
                .clickable(onClick = onRightClick)
                .padding(horizontal = 13.dp, vertical = 7.dp)
        )
    }
}

@Composable
private fun SortPill(text: String, onClick: () -> Unit) {
    Text(
        text,
        color = TextPrimary,
        fontSize = 13.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(PanelColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 15.dp, vertical = 9.dp)
    )
}

@Composable
private fun SmallActionButton(text: String, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        shape = RoundedCornerShape(24.dp),
        colors = ButtonDefaults.textButtonColors(containerColor = SoftSurface, contentColor = TextPrimary),
        modifier = Modifier.height(52.dp)
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 18.dp))
    }
}

@Composable
private fun InfoChip(text: String) {
    Box(modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(PanelColor).padding(horizontal = 12.dp, vertical = 7.dp)) {
        Text(text = text, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SortSheet(
    selectedField: String,
    descending: Boolean,
    onSelect: (String, Boolean) -> Unit
) {
    var localDescending by remember { mutableStateOf(descending) }

    Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 18.dp)) {
        Box(modifier = Modifier.align(Alignment.CenterHorizontally).width(48.dp).height(5.dp).clip(RoundedCornerShape(999.dp)).background(LineColor))
        Spacer(modifier = Modifier.height(24.dp))
        Text("정렬", color = TextPrimary, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
        Spacer(modifier = Modifier.height(24.dp))
        Row(modifier = Modifier.clip(RoundedCornerShape(999.dp)).background(SoftSurface).padding(4.dp)) {
            Text(
                "높은순",
                color = if (localDescending) TextPrimary else TextSecondary,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (localDescending) PanelColor else Color.Transparent)
                    .clickable { localDescending = true }
                    .padding(vertical = 12.dp)
            )
            Text(
                "낮은순",
                color = if (localDescending) TextSecondary else TextPrimary,
                textAlign = TextAlign.Center,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(999.dp))
                    .background(if (localDescending) Color.Transparent else PanelColor)
                    .clickable { localDescending = false }
                    .padding(vertical = 12.dp)
            )
        }
        Spacer(modifier = Modifier.height(22.dp))
        listOf(
            SortField.AMOUNT to "평가액",
            SortField.PRINCIPAL to "매입금액",
            SortField.TOTAL_PROFIT to "총 수익",
            SortField.TOTAL_RETURN to "총 수익률",
            SortField.DAY_PROFIT to "일간 수익",
            SortField.DAY_RETURN to "일간 수익률",
            SortField.DIRECT to "직접설정"
        ).forEach { (field, label) ->
            SortOptionRow(
                label = label,
                selected = selectedField == field,
                onClick = { onSelect(field, localDescending) }
            )
        }
        Spacer(modifier = Modifier.height(22.dp))
    }
}

@Composable
private fun SortOptionRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick).padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        SelectionCircle(selected)
        Spacer(modifier = Modifier.width(18.dp))
        Text(label, color = TextPrimary, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
    }
}

@Composable
private fun EmptyAccount() {
    Box(modifier = Modifier.fillMaxWidth().padding(vertical = 38.dp), contentAlignment = Alignment.Center) {
        Text("아직 등록된 자산이 없어요", color = TextSecondary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun EmptyAccountsScreen(onBack: () -> Unit) {
    ScreenColumn {
        PlainTopBar(title = "계좌", onBack = onBack, rightText = "", onRightClick = {})
        Spacer(modifier = Modifier.height(80.dp))
        Text(
            text = "등록된 계좌가 없어요",
            color = TextPrimary,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "메뉴에서 계좌 추가를 눌러 새 계좌를 만들어보세요.",
            color = TextSecondary,
            fontSize = 15.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}

@Composable
private fun RiskMapCard(position: MarketPosition) {
    Card(colors = CardDefaults.cardColors(containerColor = SoftSurface), shape = RoundedCornerShape(22.dp), elevation = CardDefaults.cardElevation(0.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            ReportCardTitle("현재 시장 위치")
            Spacer(modifier = Modifier.height(14.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(position.color))
                Spacer(modifier = Modifier.width(8.dp))
                Text(position.title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.weight(1f))
                Text("${position.drawdownPercent}%", color = position.color, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(7.dp))
            Text(position.comparison, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Text(position.caption, color = TextSecondary, fontSize = 14.sp, lineHeight = 21.sp)
        }
    }
}

@Composable
private fun SummaryCard(title: String, value: String, caption: String) {
    Card(colors = CardDefaults.cardColors(containerColor = SoftSurface), shape = RoundedCornerShape(22.dp), elevation = CardDefaults.cardElevation(0.dp)) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(title, color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(7.dp))
            Text(value, color = TextPrimary, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(7.dp))
            Text(caption, color = TextSecondary, fontSize = 14.sp, lineHeight = 20.sp)
        }
    }
}

@Composable
private fun ScenarioRow(title: String, loss: String, recovery: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
        AssetBadge(loss, NegativeBlue)
        Spacer(modifier = Modifier.width(13.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text(recovery, color = TextSecondary, fontSize = 14.sp)
        }
        Text("?곸꽭", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun MetricRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 11.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = TextSecondary, fontSize = 16.sp, modifier = Modifier.weight(1f))
        Text(value, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.End)
    }
}

@Composable
private fun DividerLine() {
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(LineColor))
}

private fun mergeHolding(existing: List<HoldingUi>, added: HoldingUi): List<HoldingUi> {
    val match = existing.firstOrNull { it.ticker == added.ticker } ?: return existing + added
    val oldValue = match.quantity * match.averagePrice
    val newValue = added.quantity * added.averagePrice
    val totalQuantity = match.quantity + added.quantity
    val averagePrice = if (totalQuantity == 0.0) added.averagePrice else (oldValue + newValue) / totalQuantity
    val totalDollarCost = oldValue + newValue
    val averageExchangeRate = if (totalDollarCost == 0.0) {
        added.averageExchangeRate
    } else {
        ((oldValue * match.averageExchangeRate) + (newValue * added.averageExchangeRate)) / totalDollarCost
    }
    return existing.map {
        if (it.ticker == added.ticker) {
            it.copy(
                quantity = totalQuantity,
                averagePrice = averagePrice,
                averageExchangeRate = averageExchangeRate,
                currentPrice = added.currentPrice,
                trades = it.trades + added.trades
            )
        } else {
            it
        }
    }
}

private fun reduceHolding(existing: List<HoldingUi>, ticker: String, quantity: Double, trade: TradeUi? = null): List<HoldingUi> =
    existing.mapNotNull { holding ->
        if (holding.ticker != ticker) {
            holding
        } else {
            val nextQuantity = (holding.quantity - quantity).coerceAtLeast(0.0)
            val nextTrades = if (trade == null) holding.trades else holding.trades + trade
            if (nextQuantity <= 0.0) null else holding.copy(quantity = nextQuantity, trades = nextTrades)
        }
    }

private fun removeTrade(existing: List<HoldingUi>, ticker: String, tradeId: Long): List<HoldingUi> =
    removeTrades(existing, ticker, listOf(tradeId))

private fun removeTrades(existing: List<HoldingUi>, ticker: String, tradeIds: List<Long>): List<HoldingUi> =
    existing.mapNotNull { holding ->
        if (holding.ticker == ticker) recalculateHoldingFromTrades(holding, holding.trades.filterNot { it.id in tradeIds }) else holding
    }

private fun replaceTrade(existing: List<HoldingUi>, ticker: String, trade: TradeUi): List<HoldingUi> =
    existing.mapNotNull { holding ->
        if (holding.ticker == ticker) {
            recalculateHoldingFromTrades(
                holding,
                holding.trades.map { if (it.id == trade.id) trade else it }
            )
        } else {
            holding
        }
    }

private fun recalculateHoldingFromTrades(base: HoldingUi, trades: List<TradeUi>): HoldingUi? {
    var quantity = 0.0
    var cost = 0.0
    var wonCost = 0.0
    trades.sortedBy { it.id }.forEach { trade ->
        when (trade.side) {
            TradeSide.BUY -> {
                quantity += trade.quantity
                cost += trade.quantity * trade.price
                wonCost += trade.quantity * trade.price * trade.exchangeRate
            }
            TradeSide.SELL -> {
                val sellQuantity = trade.quantity.coerceAtMost(quantity)
                if (quantity > 0.0) {
                    val ratio = sellQuantity / quantity
                    cost *= (1.0 - ratio)
                    wonCost *= (1.0 - ratio)
                }
                quantity = (quantity - sellQuantity).coerceAtLeast(0.0)
            }
        }
    }
    if (quantity <= 0.0) return null
    val averagePrice = if (quantity == 0.0) base.averagePrice else cost / quantity
    val averageExchangeRate = if (cost == 0.0) base.averageExchangeRate else wonCost / cost
    return base.copy(
        quantity = quantity,
        averagePrice = averagePrice,
        averageExchangeRate = averageExchangeRate,
        trades = trades
    )
}

private fun portfolioHoldings(accounts: List<AccountUi>): List<HoldingUi> =
    accounts.flatMap { it.holdings }
        .groupBy { it.ticker }
        .map { (_, group) ->
            group.reduce { acc, item ->
                val totalQuantity = acc.quantity + item.quantity
                val totalCost = acc.quantity * acc.averagePrice + item.quantity * item.averagePrice
                val totalWonCost = acc.quantity * acc.averagePrice * acc.averageExchangeRate +
                    item.quantity * item.averagePrice * item.averageExchangeRate
                acc.copy(
                    quantity = totalQuantity,
                    averagePrice = if (totalQuantity == 0.0) acc.averagePrice else totalCost / totalQuantity,
                    averageExchangeRate = if (totalCost == 0.0) acc.averageExchangeRate else totalWonCost / totalCost,
                    currentPrice = item.currentPrice
                )
            }
        }

private fun calculateMarketPosition(holdings: List<HoldingUi>): MarketPosition {
    val invested = holdings.sumOf { it.principal }.takeIf { it > 0L }
    val current = holdings.sumOf { it.amount }
    val weightedReturn = if (invested == null) -0.12 else (current - invested).toDouble() / invested
    val drawdown = weightedReturn.coerceAtMost(0.0)
    val drawdownPercent = (drawdown * 100).roundToLong().toInt()
    val leveragedWeight = holdings
        .filter { it.ticker in setOf("QLD", "TQQQ", "SOXL") }
        .sumOf { it.amount }
        .let { amount -> if (current <= 0L) 0.0 else amount.toDouble() / current }
    val stressNote = when {
        leveragedWeight >= 0.5 -> "레버리지 비중이 높아 같은 하락률에도 체감 스트레스가 크게 나타납니다."
        leveragedWeight >= 0.2 -> "레버리지 자산이 섞여 있어 QQQ 단독보다 변동성이 큰 구조입니다."
        holdings.isEmpty() -> "아직 추가된 종목이 없어 QQQ 중심 장기 포트폴리오의 기본 조정 구간으로 표시합니다."
        else -> "비레버리지 비중이 높아 레버리지 ETF 중심 포트폴리오보다 충격이 완만한 구조입니다."
    }

    return when {
        drawdown > -0.10 -> MarketPosition(
            title = "정상 구간",
            drawdownPercent = drawdownPercent,
            comparison = "과거 위기 구간과 비교하면 낮은 스트레스 구간입니다.",
            caption = "고점 대비 -10% 이내로 해석합니다. $stressNote",
            color = BrandGreen
        )
        drawdown > -0.20 -> MarketPosition(
            title = "조정 구간",
            drawdownPercent = drawdownPercent,
            comparison = "2022년 하락장 초입과 비슷한 압박 구간입니다.",
            caption = "고점 대비 -10%~-20% 범위로 해석합니다. $stressNote",
            color = Color(0xFFFFA726)
        )
        drawdown > -0.35 -> MarketPosition(
            title = "약세장",
            drawdownPercent = drawdownPercent,
            comparison = "2022년 금리인상 하락장 중후반 수준의 스트레스 구간입니다.",
            caption = "고점 대비 -20%~-35% 범위로 해석합니다. $stressNote",
            color = NegativeBlue
        )
        else -> MarketPosition(
            title = "위기 구간",
            drawdownPercent = drawdownPercent,
            comparison = "코로나 급락이나 금융위기급 스트레스와 비교해야 하는 구간입니다.",
            caption = "고점 대비 -35% 이하로 해석합니다. $stressNote",
            color = PositiveRed
        )
    }
}

private fun sortHoldings(holdings: List<HoldingUi>, field: String, descending: Boolean): List<HoldingUi> {
    val sorted = when (field) {
        SortField.AMOUNT -> holdings.sortedBy { it.amount }
        SortField.PRINCIPAL -> holdings.sortedBy { it.principal }
        SortField.TOTAL_PROFIT -> holdings.sortedBy { it.amount - it.principal }
        SortField.TOTAL_RETURN -> holdings.sortedBy { if (it.principal == 0L) 0.0 else (it.amount - it.principal).toDouble() / it.principal }
        SortField.DAY_PROFIT -> holdings.sortedBy { it.dayProfit }
        SortField.DAY_RETURN -> holdings.sortedBy { it.dayRate }
        else -> holdings
    }
    return if (descending && field != SortField.DIRECT) sorted.reversed() else sorted
}

private fun sortLabel(field: String): String = when (field) {
    SortField.AMOUNT -> "평가액순"
    SortField.PRINCIPAL -> "매입금액순"
    SortField.TOTAL_PROFIT -> "총 수익순"
    SortField.TOTAL_RETURN -> "총 수익률순"
    SortField.DAY_PROFIT -> "일간 수익순"
    SortField.DAY_RETURN -> "일간 수익률순"
    else -> "직접설정순"
}

private suspend fun refreshMarketDataIfNeeded(
    context: Context,
    accounts: List<AccountUi>,
    cachedUsdKrw: Double,
    settings: AppSettings,
    force: Boolean = false
): MarketRefreshResult = withContext(Dispatchers.IO) {
    val prefs = context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
    val now = System.currentTimeMillis()
    val lastRefreshAt = prefs.getLong("market_last_refresh_at", 0L)
    if (!force && now - lastRefreshAt < MarketRefreshIntervalMs) {
        return@withContext MarketRefreshResult(accounts, cachedUsdKrw, updated = false, message = "최근에 이미 업데이트했어요")
    }

    val tickers = accounts.flatMap { it.holdings }.map { it.ticker }.distinct()
    if (tickers.isEmpty()) {
        prefs.edit().putLong("market_last_refresh_at", now).apply()
        return@withContext MarketRefreshResult(accounts, cachedUsdKrw, updated = false, message = "업데이트할 종목이 없어요")
    }

    if (settings.apiProvider == ApiProvider.KIWOOM) {
        val appKey = settings.kiwoomAppKey
        val appSecret = settings.kiwoomAppSecret
        if (appKey.isBlank() || appSecret.isBlank()) {
            return@withContext MarketRefreshResult(accounts, cachedUsdKrw, updated = false, message = "키움 API 키가 비어 있어요")
        }
        return@withContext refreshMarketDataWithKiwoom(context, accounts, cachedUsdKrw, tickers, appKey, appSecret, settings, now, prefs)
    }

    runCatching {
        val errors = mutableListOf<String>()
        val appKey = settings.kisAppKey.ifBlank { BuildConfig.KIS_APP_KEY }
        val appSecret = settings.kisAppSecret.ifBlank { BuildConfig.KIS_APP_SECRET }
        if (appKey.isBlank() || appSecret.isBlank()) {
            return@withContext MarketRefreshResult(accounts, cachedUsdKrw, updated = false, message = "한국투자 API 키가 비어 있어요")
        }
        val token = getKisAccessToken(context, appKey, appSecret)
        var usdKrw = cachedUsdKrw
        runCatching {
            fetchUsdKrwFromKis(token, appKey, appSecret, cachedUsdKrw)
        }.onFailure {
            errors.add("환율: ${it.message ?: "조회 실패"}")
        }.getOrNull()?.let { usdKrw = it }
        delay(KisRequestDelayMs)

        val quotes = mutableMapOf<String, MarketQuote>()
        tickers.forEach { ticker ->
            val quote = if (isKoreanTicker(ticker)) {
                runCatching { fetchDomesticQuoteFromKis(token, appKey, appSecret, ticker) }
            } else {
                runCatching { fetchOverseasQuoteFromKis(token, appKey, appSecret, ticker) }
            }.onFailure {
                errors.add("$ticker: ${it.message ?: "조회 실패"}")
            }.getOrNull()
            quote?.let {
                quotes[ticker] = it
                if (!isKoreanTicker(ticker)) {
                    it.exchangeRate?.takeIf { rate -> rate > 0.0 }?.let { rate -> usdKrw = rate }
                }
            }
            delay(KisRequestDelayMs)
        }

        val updatedAccounts = accounts.map { account ->
            account.copy(
                fixedAmount = null,
                holdings = account.holdings.map { holding ->
                    val quote = quotes[holding.ticker]
                    holding.copy(
                        currentPrice = quote?.price ?: holding.currentPrice,
                        previousClosePrice = quote?.previousClose ?: holding.previousClosePrice,
                        exchangeRate = quote?.exchangeRate ?: usdKrw
                    )
                }
            )
        }
        prefs.edit()
            .putLong("market_last_refresh_at", now)
            .apply()
        val updated = quotes.isNotEmpty() || usdKrw != cachedUsdKrw
        MarketRefreshResult(
            updatedAccounts,
            usdKrw,
            updated = updated,
            message = when {
                updated -> "방금 전 업데이트 했어요"
                errors.isNotEmpty() -> errors.distinct().take(2).joinToString(" / ")
                else -> "조회된 새 가격이 없어요"
            }
        )
    }.getOrElse {
        MarketRefreshResult(accounts, cachedUsdKrw, updated = false, message = it.message ?: "업데이트에 실패했어요")
    }
}

private suspend fun refreshMarketDataWithKiwoom(
    context: Context,
    accounts: List<AccountUi>,
    cachedUsdKrw: Double,
    tickers: List<String>,
    appKey: String,
    appSecret: String,
    settings: AppSettings,
    now: Long,
    prefs: android.content.SharedPreferences
): MarketRefreshResult {
    return runCatching {
        val token = getKiwoomAccessToken(context, appKey, appSecret)
        val quotes = mutableMapOf<String, MarketQuote>()
        val errors = mutableListOf<String>()
        var usdKrw = cachedUsdKrw
        tickers.filter { isKoreanTicker(it) }.forEach { ticker ->
            runCatching { fetchDomesticQuoteFromKiwoom(token, ticker) }.onFailure {
                errors.add("$ticker: ${it.message ?: "키움 조회 실패"}")
            }.getOrNull()?.let { quote ->
                quotes[ticker] = quote
            }
            delay(KiwoomRequestDelayMs)
        }

        val kisAppKey = settings.kisAppKey.ifBlank { BuildConfig.KIS_APP_KEY }
        val kisAppSecret = settings.kisAppSecret.ifBlank { BuildConfig.KIS_APP_SECRET }
        if (kisAppKey.isNotBlank() && kisAppSecret.isNotBlank()) {
            runCatching {
                val kisToken = getKisAccessToken(context, kisAppKey, kisAppSecret)
                fetchUsdKrwFromKis(kisToken, kisAppKey, kisAppSecret, cachedUsdKrw)?.let { usdKrw = it }
                delay(KisRequestDelayMs)
                tickers.filterNot { isKoreanTicker(it) }.forEach { ticker ->
                    fetchOverseasQuoteFromKis(kisToken, kisAppKey, kisAppSecret, ticker)?.let { quote ->
                        quotes[ticker] = quote
                        quote.exchangeRate?.takeIf { rate -> rate > 0.0 }?.let { rate -> usdKrw = rate }
                    }
                    delay(KisRequestDelayMs)
                }
            }.onFailure {
                errors.add("해외/환율: ${it.message ?: "한국투자 폴백 실패"}")
            }
        } else if (tickers.any { !isKoreanTicker(it) }) {
            errors.add("키움 모의투자는 해외주식/환율 미지원: 한국투자 API 키도 입력해주세요")
        }

        val updatedAccounts = accounts.map { account ->
            account.copy(
                fixedAmount = null,
                holdings = account.holdings.map { holding ->
                    val quote = quotes[holding.ticker]
                    holding.copy(
                        currentPrice = quote?.price ?: holding.currentPrice,
                        previousClosePrice = quote?.previousClose ?: holding.previousClosePrice,
                        exchangeRate = quote?.exchangeRate ?: usdKrw
                    )
                }
            )
        }
        prefs.edit()
            .putLong("market_last_refresh_at", now)
            .apply()
        val updated = quotes.isNotEmpty() || usdKrw != cachedUsdKrw
        MarketRefreshResult(
            updatedAccounts,
            usdKrw,
            updated = updated,
            message = when {
                updated -> "방금 전 업데이트 했어요"
                errors.isNotEmpty() -> errors.distinct().take(2).joinToString(" / ")
                else -> "조회된 새 가격이 없어요"
            }
        )
    }.getOrElse {
        MarketRefreshResult(accounts, cachedUsdKrw, updated = false, message = it.message ?: "키움 업데이트에 실패했어요")
    }
}

private fun getKiwoomAccessToken(context: Context, appKey: String, appSecret: String): String {
    val prefs = context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
    val cachedToken = prefs.getString("kiwoom_access_token", null)
    val cachedKey = prefs.getString("kiwoom_access_token_app_key", null)
    val expiresAt = prefs.getLong("kiwoom_access_token_expires_at", 0L)
    val now = System.currentTimeMillis()
    if (!cachedToken.isNullOrBlank() && cachedKey == appKey && now < expiresAt - 60_000L) return cachedToken

    val response = postJson(
        url = "$KiwoomBaseUrl/oauth2/token",
        body = JSONObject().apply {
            put("grant_type", "client_credentials")
            put("appkey", appKey)
            put("secretkey", appSecret)
        }.toString(),
        headers = emptyMap()
    )
    val json = JSONObject(response)
    apiErrorMessage(json)?.let { throw IllegalStateException(it) }
    val token = json.optString("token").ifBlank { json.optString("access_token") }
    if (token.isBlank()) throw IllegalStateException("키움 토큰 응답이 비어 있어요")
    prefs.edit()
        .putString("kiwoom_access_token", token)
        .putString("kiwoom_access_token_app_key", appKey)
        .putLong("kiwoom_access_token_expires_at", now + 23L * 60L * 60L * 1000L)
        .apply()
    return token
}

private fun fetchDomesticQuoteFromKiwoom(token: String, ticker: String): MarketQuote? {
    val json = postJson(
        url = "$KiwoomBaseUrl/api/dostk/stkinfo",
        body = JSONObject().apply {
            put("stk_cd", ticker)
        }.toString(),
        headers = mapOf(
            "authorization" to "Bearer $token",
            "api-id" to "ka10001"
        )
    )
    val root = JSONObject(json)
    apiErrorMessage(root)?.let { throw IllegalStateException(it) }
    val output = root.optJSONObject("output") ?: root
    val rawPrice = firstDouble(output, "cur_prc", "stck_prpr", "price", "prpr") ?: return null
    val price = kotlin.math.abs(rawPrice)
    val previous = firstDouble(output, "base_pric", "stck_sdpr", "prev_clos", "previous_close", "sdpr")
    return MarketQuote(price = price, previousClose = previous?.let { kotlin.math.abs(it) })
}

private fun getKisAccessToken(context: Context, appKey: String, appSecret: String): String {
    val prefs = context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
    val cachedToken = prefs.getString("kis_access_token", null)
    val cachedKey = prefs.getString("kis_access_token_app_key", null)
    val expiresAt = prefs.getLong("kis_access_token_expires_at", 0L)
    val now = System.currentTimeMillis()
    if (!cachedToken.isNullOrBlank() && cachedKey == appKey && now < expiresAt - 60_000L) return cachedToken

    val response = postJson(
        url = "$KisBaseUrl/oauth2/tokenP",
        body = JSONObject().apply {
            put("grant_type", "client_credentials")
            put("appkey", appKey)
            put("appsecret", appSecret)
        }.toString(),
        headers = emptyMap()
    )
    val json = JSONObject(response)
    apiErrorMessage(json)?.let { throw IllegalStateException(it) }
    val token = json.optString("access_token")
    if (token.isBlank()) throw IllegalStateException("한국투자 토큰 응답이 비어 있어요")
    val expiresIn = json.optLong("expires_in", 86_400L)
    prefs.edit()
        .putString("kis_access_token", token)
        .putString("kis_access_token_app_key", appKey)
        .putLong("kis_access_token_expires_at", now + expiresIn * 1000L)
        .apply()
    return token
}

private fun fetchOverseasQuoteFromKis(token: String, appKey: String, appSecret: String, ticker: String): MarketQuote? {
    val symbol = ticker.uppercase(Locale.US)
    val errors = mutableListOf<String>()
    kisOverseasExchangeCandidates(symbol).forEach { exchange ->
        runCatching {
            fetchOverseasQuoteFromKisEndpoint(
                token = token,
                appKey = appKey,
                appSecret = appSecret,
                ticker = symbol,
                exchange = exchange,
                detail = false
            ) ?: fetchOverseasQuoteFromKisEndpoint(
                token = token,
                appKey = appKey,
                appSecret = appSecret,
                ticker = symbol,
                exchange = exchange,
                detail = true
            )
        }.onSuccess { quote ->
            if (quote != null) return quote
            errors.add("$exchange 가격 필드 비어 있음")
        }.onFailure {
            errors.add("$exchange ${it.message ?: "조회 실패"}")
        }
        Thread.sleep(KisRequestDelayMs)
    }
    throw IllegalStateException("$symbol 현재가 조회 실패: ${errors.distinct().take(2).joinToString(" / ")}")
}

private fun fetchOverseasQuoteFromKisEndpoint(
    token: String,
    appKey: String,
    appSecret: String,
    ticker: String,
    exchange: String,
    detail: Boolean
): MarketQuote? {
    val endpoint = if (detail) "price-detail" else "price"
    val trId = if (detail) "HHDFS76200200" else "HHDFS00000300"
    val json = getJson(
        url = "$KisBaseUrl/uapi/overseas-price/v1/quotations/$endpoint?AUTH=&EXCD=$exchange&SYMB=$ticker",
        headers = kisHeaders(token, appKey, appSecret, trId = trId)
    )
    val root = JSONObject(json)
    apiErrorMessage(root)?.let { throw IllegalStateException(it) }
    val output = root.optJSONObject("output")
        ?: root.optJSONArray("output")?.optJSONObject(0)
        ?: root.optJSONObject("output1")
        ?: throw IllegalStateException("해외주식 응답에 가격 데이터가 없어요")
    return parseKisOverseasQuote(output)
}

private fun parseKisOverseasQuote(output: JSONObject): MarketQuote? {
    val price = firstDouble(
        output,
        "last",
        "stck_prpr",
        "ovrs_nmix_prpr",
        "ovrs_last_prpr",
        "ovrs_stck_prpr",
        "trdprc_1",
        "t_xprc",
        "xprc",
        "prpr",
        "close",
        "price"
    ) ?: return null
    val previous = firstDouble(
        output,
        "base",
        "ovrs_nmix_prdy_clpr",
        "stck_sdpr",
        "ovrs_stck_sdpr",
        "prdy_clpr",
        "base_pric",
        "prev",
        "previous_close"
    )
    val exchangeRate = firstDouble(
        output,
        "t_rate",
        "p_rate",
        "frst_bltn_exrt",
        "base_exrt",
        "bass_exrt",
        "ovrs_excg_rt",
        "ovrs_excg_rate",
        "usd_krw",
        "deal_bas_r",
        "tt_seln_exrt",
        "tt_buy_exrt",
        "ttb",
        "tts",
        "aply_exrt",
        "exrt",
        "exchange_rate"
    )?.takeIf { it > 100.0 }
    return MarketQuote(price = price, previousClose = previous, exchangeRate = exchangeRate)
}

private fun fetchDomesticQuoteFromKis(token: String, appKey: String, appSecret: String, ticker: String): MarketQuote? {
    val json = getJson(
        url = "$KisBaseUrl/uapi/domestic-stock/v1/quotations/inquire-price?FID_COND_MRKT_DIV_CODE=J&FID_INPUT_ISCD=$ticker",
        headers = kisHeaders(token, appKey, appSecret, trId = "FHKST01010100")
    )
    val root = JSONObject(json)
    apiErrorMessage(root)?.let { throw IllegalStateException(it) }
    val output = root.optJSONObject("output") ?: throw IllegalStateException("국내주식 응답에 가격 데이터가 없어요")
    val price = firstDouble(output, "stck_prpr", "prpr", "price") ?: return null
    val previous = firstDouble(output, "stck_sdpr", "sdpr", "base", "prev", "previous_close")
    return MarketQuote(price = price, previousClose = previous)
}

private fun fetchOverseasPriceFromKis(token: String, appKey: String, appSecret: String, ticker: String): Double? {
    return fetchOverseasQuoteFromKis(token, appKey, appSecret, ticker)?.price
}

private fun fetchDomesticPriceFromKis(token: String, appKey: String, appSecret: String, ticker: String): Double? {
    return fetchDomesticQuoteFromKis(token, appKey, appSecret, ticker)?.price
}

private suspend fun fetchWeightedMonthlyReturns(
    context: Context,
    settings: AppSettings,
    assets: List<BacktestAssetUi>,
    startYear: Int,
    endYear: Int
): List<Double>? = withContext(Dispatchers.IO) {
    monthlyReturnsFromCachedHistory(context, assets, startYear, endYear)?.let { return@withContext it }
    val appKey = settings.kisAppKey.ifBlank { BuildConfig.KIS_APP_KEY }
    val appSecret = settings.kisAppSecret.ifBlank { BuildConfig.KIS_APP_SECRET }
    if (appKey.isBlank() || appSecret.isBlank() || assets.isEmpty()) return@withContext null
    runCatching {
        val token = getKisAccessToken(context, appKey, appSecret)
        val monthlyByTicker = mutableMapOf<String, List<Pair<String, Double>>>()
        assets.forEach { asset ->
            val series = if (isKoreanTicker(asset.ticker)) {
                fetchDomesticMonthlyPricesFromKis(token, appKey, appSecret, asset.ticker, startYear, endYear)
            } else {
                fetchOverseasMonthlyPricesFromKis(token, appKey, appSecret, asset.ticker, startYear, endYear)
            }
            if (series.size >= 2) monthlyByTicker[asset.ticker] = series
            delay(KisRequestDelayMs)
        }
        if (monthlyByTicker.isEmpty()) return@runCatching null
        val months = monthlyByTicker.values.flatten().map { it.first }.distinct().sorted()
        if (months.size < 2) return@runCatching null
        val returns = mutableListOf<Double>()
        for (index in 1 until months.size) {
            val previousMonth = months[index - 1]
            val currentMonth = months[index]
            var weightedReturn = 0.0
            var usedWeight = 0.0
            assets.forEach { asset ->
                val prices = monthlyByTicker[asset.ticker]?.toMap() ?: return@forEach
                val previous = prices[previousMonth]
                val current = prices[currentMonth]
            if (previous != null && current != null && previous > 0.0) {
                    val assetReturn = (current / previous) - 1.0
                    if (assetReturn < -0.85 || assetReturn > 3.0) return@runCatching null
                    weightedReturn += assetReturn * (asset.weight / 100.0)
                    usedWeight += asset.weight
                }
            }
            if (usedWeight > 0.0) returns.add(weightedReturn * (100.0 / usedWeight)) else returns.add(0.0)
        }
        returns.takeIf { it.isNotEmpty() }
    }.getOrNull()
}

private fun monthlyReturnsFromCachedHistory(context: Context, assets: List<BacktestAssetUi>, startYear: Int, endYear: Int): List<Double>? {
    val monthlyByTicker = assets.associate { asset ->
        asset.ticker to monthlyPricesFromDaily(loadHistoricalSeries(context, asset.ticker), startYear, endYear)
    }
    if (monthlyByTicker.values.any { it.size < 2 }) return null
    val months = monthlyByTicker.values.flatten().map { it.first }.distinct().sorted()
    if (months.size < 2) return null
    val returns = mutableListOf<Double>()
    for (index in 1 until months.size) {
        val previousMonth = months[index - 1]
        val currentMonth = months[index]
        var weightedReturn = 0.0
        var usedWeight = 0.0
        assets.forEach { asset ->
            val prices = monthlyByTicker[asset.ticker]?.toMap() ?: return@forEach
            val previous = prices[previousMonth]
            val current = prices[currentMonth]
            if (previous != null && current != null && previous > 0.0) {
                val assetReturn = (current / previous) - 1.0
                if (assetReturn < -0.85 || assetReturn > 3.0) return null
                weightedReturn += assetReturn * (asset.weight / 100.0)
                usedWeight += asset.weight
            }
        }
        if (usedWeight <= 0.0) return null
        returns.add(weightedReturn * (100.0 / usedWeight))
    }
    return returns.takeIf { it.isNotEmpty() }
}

private fun monthlyPricesFromDaily(points: List<HistoricalPoint>, startYear: Int, endYear: Int): List<Pair<String, Double>> {
    val start = "%04d01".format(Locale.US, startYear)
    val end = "%04d12".format(Locale.US, endYear)
    val byMonth = mutableMapOf<String, HistoricalPoint>()
    points.forEach { point ->
        val month = point.date.filter { it.isDigit() }.take(6)
        if (month.length == 6 && month in start..end) {
            val current = byMonth[month]
            if (current == null || point.date > current.date) byMonth[month] = point
        }
    }
    return normalizeSplitLikeMonthlyJumps(byMonth.toSortedMap().map { it.key to it.value.close })
}

private fun normalizeSplitLikeMonthlyJumps(series: List<Pair<String, Double>>): List<Pair<String, Double>> {
    if (series.size < 2) return series
    var factor = 1.0
    var previousAdjusted = series.first().second
    val normalized = mutableListOf(series.first().first to previousAdjusted)
    series.drop(1).forEach { (month, rawPrice) ->
        var adjusted = rawPrice * factor
        val ratio = if (previousAdjusted > 0.0) adjusted / previousAdjusted else 1.0
        if (ratio in 0.01..0.25 || ratio >= 4.0) {
            factor *= previousAdjusted / adjusted
            adjusted = rawPrice * factor
        }
        normalized.add(month to adjusted)
        previousAdjusted = adjusted
    }
    return normalized
}

private fun fetchDomesticMonthlyPricesFromKis(token: String, appKey: String, appSecret: String, ticker: String, startYear: Int, endYear: Int): List<Pair<String, Double>> {
    val start = "${startYear}0101"
    val end = "${endYear}1231"
    val json = getJson(
        url = "$KisBaseUrl/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice?FID_COND_MRKT_DIV_CODE=J&FID_INPUT_ISCD=$ticker&FID_INPUT_DATE_1=$start&FID_INPUT_DATE_2=$end&FID_PERIOD_DIV_CODE=M&FID_ORG_ADJ_PRC=0",
        headers = kisHeaders(token, appKey, appSecret, trId = "FHKST03010100")
    )
    val output = JSONObject(json).optJSONArray("output2") ?: JSONObject(json).optJSONArray("output") ?: return emptyList()
    return parseMonthlyPriceArray(output, dateKeys = listOf("stck_bsop_date", "xymd", "date"), priceKeys = listOf("stck_clpr", "clos", "close", "ovrs_nmix_prpr"))
}

private fun fetchOverseasMonthlyPricesFromKis(token: String, appKey: String, appSecret: String, ticker: String, startYear: Int, endYear: Int): List<Pair<String, Double>> {
    val start = "${startYear}0101"
    val end = "${endYear}1231"
    kisOverseasExchangeCandidates(ticker).forEach { exchange ->
        val series = runCatching {
            val json = getJson(
                url = "$KisBaseUrl/uapi/overseas-price/v1/quotations/inquire-daily-chartprice?AUTH=&EXCD=$exchange&SYMB=${ticker.uppercase(Locale.US)}&GUBN=1&BYMD=$end&MODP=1",
                headers = kisHeaders(token, appKey, appSecret, trId = "HHDFS76240000")
            )
            val output = JSONObject(json).optJSONArray("output2") ?: JSONObject(json).optJSONArray("output") ?: return@runCatching emptyList()
            parseMonthlyPriceArray(output, dateKeys = listOf("xymd", "stck_bsop_date", "date"), priceKeys = listOf("clos", "last", "stck_clpr", "ovrs_nmix_prpr", "close"))
                .filter { it.first >= start.take(6) && it.first <= end.take(6) }
        }.getOrDefault(emptyList())
        if (series.isNotEmpty()) return series
    }
    return emptyList()
}

private fun parseMonthlyPriceArray(output: JSONArray, dateKeys: List<String>, priceKeys: List<String>): List<Pair<String, Double>> {
    val byMonth = mutableMapOf<String, Pair<String, Double>>()
    for (index in 0 until output.length()) {
        val item = output.optJSONObject(index) ?: continue
        val date = dateKeys.firstNotNullOfOrNull { key -> item.optString(key).takeIf { it.length >= 6 } } ?: continue
        val price = firstDouble(item, *priceKeys.toTypedArray()) ?: continue
        val month = date.take(6)
        val current = byMonth[month]
        if (current == null || date > current.first) {
            byMonth[month] = date to price
        }
    }
    return normalizeSplitLikeMonthlyJumps(byMonth.toSortedMap().map { it.key to it.value.second })
}

private suspend fun lookupKisDomesticAsset(context: Context, settings: AppSettings, ticker: String): AssetOption? =
    withContext(Dispatchers.IO) {
        val appKey = settings.kisAppKey.ifBlank { BuildConfig.KIS_APP_KEY }
        val appSecret = settings.kisAppSecret.ifBlank { BuildConfig.KIS_APP_SECRET }
        if (appKey.isBlank() || appSecret.isBlank()) return@withContext null
        runCatching {
            val token = getKisAccessToken(context, appKey, appSecret)
            val json = getJson(
                url = "$KisBaseUrl/uapi/domestic-stock/v1/quotations/search-stock-info?PDNO=$ticker&PRDT_TYPE_CD=300",
                headers = kisHeaders(token, appKey, appSecret, trId = "CTPF1002R")
            )
            val output = JSONObject(json).optJSONObject("output") ?: return@runCatching null
            val name = output.optString("prdt_name")
                .ifBlank { output.optString("prdt_abrv_name") }
                .ifBlank { output.optString("kor_isnm") }
                .ifBlank { ticker }
            AssetOption(ticker, name, 0.0, Color(0xFF6F321D))
        }.getOrNull()
    }

private suspend fun fetchUsdKrwForDate(context: Context, settings: AppSettings, dateText: String, fallback: Double): Double? =
    withContext(Dispatchers.IO) {
        findHistoricalCloseOnOrBefore(context, "USDKRW", dateText)?.let { return@withContext it }
        val appKey = settings.kisAppKey.ifBlank { BuildConfig.KIS_APP_KEY }
        val appSecret = settings.kisAppSecret.ifBlank { BuildConfig.KIS_APP_SECRET }
        if (appKey.isBlank() || appSecret.isBlank()) return@withContext fallback
        runCatching {
            val token = getKisAccessToken(context, appKey, appSecret)
            fetchUsdKrwFromKis(token, appKey, appSecret, fallback)
        }.getOrNull() ?: fallback
    }

private fun fetchUsdKrwFromKis(token: String, appKey: String, appSecret: String, fallback: Double): Double? {
    val symbols = listOf("QQQ", "AAPL", "SPY", "QLD")
    val errors = mutableListOf<String>()
    symbols.forEach { symbol ->
        kisOverseasExchangeCandidates(symbol).forEach { exchange ->
            runCatching {
                fetchOverseasQuoteFromKisEndpoint(
                    token = token,
                    appKey = appKey,
                    appSecret = appSecret,
                    ticker = symbol,
                    exchange = exchange,
                    detail = true
                )?.exchangeRate
            }.onSuccess { rate ->
                if (rate != null && rate > 100.0) return rate
            }.onFailure {
                errors.add("$symbol/$exchange ${it.message ?: "조회 실패"}")
            }
            Thread.sleep(KisRequestDelayMs)
        }
    }
    throw IllegalStateException("한국투자 가격상세 환율 필드를 찾지 못했어요: ${errors.distinct().take(2).joinToString(" / ")}")
}

private suspend fun downloadHistoricalSeries(
    context: Context,
    settings: AppSettings,
    candidate: DownloadCandidate,
    interval: String = HistoryInterval.MONTHLY
): HistoricalDownloadResult = withContext(Dispatchers.IO) {
    if (candidate.symbol.isBlank()) {
        return@withContext HistoricalDownloadResult(
            status = DownloadStatus.UNSUPPORTED_SYMBOL,
            message = "심볼 미지원: 다운로드할 종목 코드가 비어 있어요."
        )
    }

    val yahooPoints = runCatching {
        downloadYahooHistoricalSeries(candidate.symbol, interval)
    }.getOrDefault(emptyList())
    if (yahooPoints.size >= 2) {
        val saved = saveDownloadedHistoricalSeries(
            context = context,
            candidate = candidate,
            interval = interval,
            points = yahooPoints,
            sourceName = "장기 가격 데이터"
        )
        runCatching { downloadAndStoreDividendPaymentSeries(context, candidate.symbol) }
        return@withContext saved
    }

    val appKey = settings.kisAppKey.ifBlank { BuildConfig.KIS_APP_KEY }
    val appSecret = settings.kisAppSecret.ifBlank { BuildConfig.KIS_APP_SECRET }
    if (appKey.isBlank() || appSecret.isBlank()) {
        return@withContext HistoricalDownloadResult(
            status = DownloadStatus.API_KEY_MISSING,
            message = "API 키 없음: 장기 데이터 소스에서 찾지 못했고, 설정에 한국투자 API 키도 없어요."
        )
    }

    val token = runCatching { getKisAccessToken(context, appKey, appSecret) }.getOrNull()
    if (token.isNullOrBlank()) {
        return@withContext HistoricalDownloadResult(
            status = DownloadStatus.NO_RESPONSE,
            message = "응답 없음: 한국투자 토큰 발급에 실패했어요."
        )
    }

    if (candidate.symbol == "USDKRW") {
        val current = runCatching { fetchUsdKrwFromKis(token, appKey, appSecret, DefaultUsdKrw) }.getOrNull()
        if (current == null || current <= 0.0) {
            return@withContext HistoricalDownloadResult(
                status = DownloadStatus.NO_DATA,
                message = "데이터 없음: 원달러 환율을 가져오지 못했어요."
            )
        }
        val point = HistoricalPoint(LocalDate.now().toString(), current)
        mergeHistoricalSeries(context, candidate.symbol, listOf(point))
        return@withContext HistoricalDownloadResult(
            status = DownloadStatus.INSUFFICIENT_SOURCE,
            points = listOf(point),
            message = "데이터 소스 부족: 한국투자에서 현재 원달러만 저장했어요. 과거 환율은 별도 데이터 소스가 필요합니다."
        )
    }

    val existing = loadHistoricalSeries(context, candidate.symbol)
    val points = runCatching {
        if (isKoreanTicker(candidate.symbol)) {
            if (interval == HistoryInterval.MONTHLY) {
                downloadDomesticMonthlySeriesFromKis(token, appKey, appSecret, candidate.symbol, null)
            } else {
                downloadDomesticDailyPricesFromKis(token, appKey, appSecret, candidate.symbol, null)
            }
        } else {
            if (interval == HistoryInterval.MONTHLY) {
                downloadOverseasMonthlySeriesFromKis(token, appKey, appSecret, candidate.symbol, null)
            } else {
                downloadOverseasDailyPricesFromKis(token, appKey, appSecret, candidate.symbol, null)
            }
        }
    }.getOrElse {
        return@withContext HistoricalDownloadResult(
            status = DownloadStatus.NO_RESPONSE,
            message = "응답 없음: ${candidate.label} 가격 데이터를 가져오지 못했어요."
        )
    }

    if (points.isEmpty()) {
        if (existing.isNotEmpty()) {
            return@withContext saveDownloadedHistoricalSeries(
                context = context,
                candidate = candidate,
                interval = interval,
                points = existing,
                sourceName = "저장된 데이터"
            )
        }
        return@withContext HistoricalDownloadResult(
            status = if (candidate.symbol.isBlank()) DownloadStatus.UNSUPPORTED_SYMBOL else DownloadStatus.NO_DATA,
            message = "데이터 없음: ${candidate.label} 종목 데이터를 찾지 못했어요."
        )
    }

    val saved = saveDownloadedHistoricalSeries(
        context = context,
        candidate = candidate,
        interval = interval,
        points = points,
        sourceName = "한국투자 API"
    )
    runCatching { downloadAndStoreDividendPaymentSeries(context, candidate.symbol) }
    return@withContext saved
}

private suspend fun downloadDomesticDailyPricesFromKis(
    token: String,
    appKey: String,
    appSecret: String,
    ticker: String,
    afterDate: LocalDate? = null
): List<HistoricalPoint> {
    val now = LocalDate.now()
    val startDate = afterDate?.plusDays(1) ?: LocalDate.of(1990, 1, 1)
    val result = mutableListOf<HistoricalPoint>()
    if (startDate.isAfter(now)) return emptyList()
    for (year in startDate.year..now.year) {
        val start = if (year == startDate.year) startDate else LocalDate.of(year, 1, 1)
        val end = if (year == now.year) now else LocalDate.of(year, 12, 31)
        val json = getJson(
            url = "$KisBaseUrl/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice?FID_COND_MRKT_DIV_CODE=J&FID_INPUT_ISCD=$ticker&FID_INPUT_DATE_1=${start.toKisDate()}&FID_INPUT_DATE_2=${end.toKisDate()}&FID_PERIOD_DIV_CODE=D&FID_ORG_ADJ_PRC=0",
            headers = kisHeaders(token, appKey, appSecret, trId = "FHKST03010100")
        )
        val output = JSONObject(json).optJSONArray("output2") ?: JSONObject(json).optJSONArray("output")
        if (output != null) result.addAll(parseDailyPriceArray(output))
        delay(KisRequestDelayMs)
    }
    return result
}

private suspend fun downloadOverseasDailyPricesFromKis(
    token: String,
    appKey: String,
    appSecret: String,
    ticker: String,
    afterDate: LocalDate? = null
): List<HistoricalPoint> {
    kisOverseasExchangeCandidates(ticker).forEach { exchange ->
        val result = mutableListOf<HistoricalPoint>()
        var cursor = LocalDate.now()
        var lastOldest: String? = null
        var page = 0
        while (page < 80) {
            val json = runCatching {
                getJson(
                    url = "$KisBaseUrl/uapi/overseas-price/v1/quotations/inquire-daily-chartprice?AUTH=&EXCD=$exchange&SYMB=${ticker.uppercase(Locale.US)}&GUBN=0&BYMD=${cursor.toKisDate()}&MODP=1",
                    headers = kisHeaders(token, appKey, appSecret, trId = "HHDFS76240000")
                )
            }.getOrNull() ?: break
            val output = JSONObject(json).optJSONArray("output2") ?: JSONObject(json).optJSONArray("output")
            val points = output?.let { parseDailyPriceArray(it) }.orEmpty()
            if (points.isEmpty()) break
            result.addAll(points)
            val oldest = points.minByOrNull { it.date }?.date ?: break
            val oldestDate = parseAppDate(oldest) ?: break
            if (afterDate != null && !oldestDate.isAfter(afterDate)) break
            if (oldest == lastOldest) break
            lastOldest = oldest
            cursor = oldestDate.minusDays(1)
            delay(KisRequestDelayMs)
            page += 1
        }
        val filtered = if (afterDate == null) result else result.filter { point ->
            parseAppDate(point.date)?.isAfter(afterDate) == true
        }
        if (filtered.isNotEmpty()) return filtered
    }
    return emptyList()
}

private suspend fun downloadDomesticMonthlySeriesFromKis(
    token: String,
    appKey: String,
    appSecret: String,
    ticker: String,
    afterDate: LocalDate? = null
): List<HistoricalPoint> {
    val now = LocalDate.now()
    val startYear = afterDate?.year ?: 1990
    val result = mutableListOf<HistoricalPoint>()
    for (year in startYear..now.year) {
        val start = if (year == startYear && afterDate != null) afterDate.plusDays(1) else LocalDate.of(year, 1, 1)
        val end = if (year == now.year) now else LocalDate.of(year, 12, 31)
        if (start.isAfter(end)) continue
        val json = getJson(
            url = "$KisBaseUrl/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice?FID_COND_MRKT_DIV_CODE=J&FID_INPUT_ISCD=$ticker&FID_INPUT_DATE_1=${start.toKisDate()}&FID_INPUT_DATE_2=${end.toKisDate()}&FID_PERIOD_DIV_CODE=M&FID_ORG_ADJ_PRC=0",
            headers = kisHeaders(token, appKey, appSecret, trId = "FHKST03010100")
        )
        val output = JSONObject(json).optJSONArray("output2") ?: JSONObject(json).optJSONArray("output")
        if (output != null) result.addAll(parseDailyPriceArray(output))
        delay(KisRequestDelayMs)
    }
    return result
}

private suspend fun downloadOverseasMonthlySeriesFromKis(
    token: String,
    appKey: String,
    appSecret: String,
    ticker: String,
    afterDate: LocalDate? = null
): List<HistoricalPoint> {
    kisOverseasExchangeCandidates(ticker).forEach { exchange ->
        val result = mutableListOf<HistoricalPoint>()
        var cursor = LocalDate.now()
        var lastOldest: String? = null
        var page = 0
        while (page < 80) {
            val json = runCatching {
                getJson(
                    url = "$KisBaseUrl/uapi/overseas-price/v1/quotations/inquire-daily-chartprice?AUTH=&EXCD=$exchange&SYMB=${ticker.uppercase(Locale.US)}&GUBN=1&BYMD=${cursor.toKisDate()}&MODP=1",
                    headers = kisHeaders(token, appKey, appSecret, trId = "HHDFS76240000")
                )
            }.getOrNull() ?: break
            val output = JSONObject(json).optJSONArray("output2") ?: JSONObject(json).optJSONArray("output")
            val points = output?.let { parseDailyPriceArray(it) }.orEmpty()
            if (points.isEmpty()) break
            result.addAll(points)
            val oldest = points.minByOrNull { it.date }?.date ?: break
            val oldestDate = parseAppDate(oldest) ?: break
            if (afterDate != null && !oldestDate.isAfter(afterDate)) break
            if (oldest == lastOldest) break
            lastOldest = oldest
            cursor = oldestDate.minusDays(1)
            delay(KisRequestDelayMs)
            page += 1
        }
        val filtered = if (afterDate == null) result else result.filter { point ->
            parseAppDate(point.date)?.isAfter(afterDate) == true
        }
        if (filtered.isNotEmpty()) return filtered
    }
    return emptyList()
}

private fun parseDailyPriceArray(output: JSONArray): List<HistoricalPoint> {
    val points = mutableListOf<HistoricalPoint>()
    for (index in 0 until output.length()) {
        val item = output.optJSONObject(index) ?: continue
        val rawDate = listOf("stck_bsop_date", "xymd", "date", "bass_dt")
            .firstNotNullOfOrNull { key -> item.optString(key).takeIf { it.length >= 8 } }
            ?: continue
        val close = firstDouble(item, "stck_clpr", "clos", "close", "ovrs_nmix_prpr", "last") ?: continue
        val date = rawDate.filter { it.isDigit() }.take(8).let {
            if (it.length == 8) "${it.take(4)}-${it.substring(4, 6)}-${it.substring(6, 8)}" else rawDate
        }
        points.add(HistoricalPoint(date, close))
    }
    return points
}

private fun LocalDate.toKisDate(): String =
    "%04d%02d%02d".format(Locale.US, year, monthValue, dayOfMonth)

private fun loadHistoricalSeries(context: Context, symbol: String): List<HistoricalPoint> {
    val raw = context.getSharedPreferences("long_run_history", Context.MODE_PRIVATE).getString(symbol, null) ?: return emptyList()
    return runCatching {
        val array = JSONArray(raw)
        (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            HistoricalPoint(item.optString("date"), item.optDouble("close")).takeIf { it.date.isNotBlank() && it.close > 0.0 }
        }.sortedBy { it.date }
    }.getOrDefault(emptyList())
}

private fun saveHistoricalSeries(context: Context, symbol: String, points: List<HistoricalPoint>) {
    val array = JSONArray()
    points.sortedBy { it.date }.forEach { point ->
        array.put(JSONObject().apply {
            put("date", point.date)
            put("close", point.close)
        })
    }
    context.getSharedPreferences("long_run_history", Context.MODE_PRIVATE)
        .edit()
        .putString(symbol, array.toString())
        .apply()
}

private fun loadVolatilityHistoricalSeries(context: Context, symbol: String): List<HistoricalPoint> {
    val key = symbol.trim().uppercase(Locale.US)
    val raw = context.getSharedPreferences(VolatilityHistoryPreferences, Context.MODE_PRIVATE)
        .getString(key, null)
        ?: return emptyList()
    return runCatching {
        val array = JSONArray(raw)
        (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            HistoricalPoint(item.optString("date"), item.optDouble("close"))
                .takeIf { it.date.isNotBlank() && it.close > 0.0 }
        }.sortedBy { it.date }
    }.getOrDefault(emptyList())
}

private fun saveVolatilityHistoricalSeries(context: Context, symbol: String, points: List<HistoricalPoint>) {
    val key = symbol.trim().uppercase(Locale.US)
    val array = JSONArray()
    points.filter { it.date.isNotBlank() && it.close > 0.0 }
        .distinctBy { it.date }
        .sortedBy { it.date }
        .forEach { point ->
            array.put(JSONObject().apply {
                put("date", point.date)
                put("close", point.close)
            })
        }
    context.getSharedPreferences(VolatilityHistoryPreferences, Context.MODE_PRIVATE)
        .edit()
        .putString(key, array.toString())
        .apply()
    context.getSharedPreferences(VolatilityHistoryMetaPreferences, Context.MODE_PRIVATE)
        .edit()
        .putLong(key, System.currentTimeMillis())
        .apply()
}

private suspend fun loadHistoricalVolatility(
    context: Context,
    candidate: ScenarioComparisonCandidate
): HistoricalVolatilityEstimate? {
    val allocations = candidate.historicalAllocations
        .filter { it.ticker.isNotBlank() && it.weight > 0.0 }
    if (allocations.isEmpty()) return null

    val priceHistory = mutableMapOf<String, List<HistoricalPricePoint>>()
    allocations.map { it.ticker.trim().uppercase(Locale.US) }.distinct().forEach { ticker ->
        val cached = loadVolatilityHistoricalSeries(context, ticker)
        val cachedAt = context.getSharedPreferences(VolatilityHistoryMetaPreferences, Context.MODE_PRIVATE)
            .getLong(ticker, 0L)
        val cacheIsFresh = cached.size >= 13 &&
            System.currentTimeMillis() - cachedAt < VolatilityHistoryCacheMs
        val downloaded = if (cacheIsFresh) {
            emptyList()
        } else {
            runCatching { downloadYahooHistoricalSeries(ticker, HistoryInterval.MONTHLY) }
                .getOrDefault(emptyList())
        }
        val points = when {
            downloaded.size >= 2 -> downloaded.also {
                saveVolatilityHistoricalSeries(context, ticker, it)
                mergeHistoricalSeries(context, ticker, it)
            }
            cached.isNotEmpty() -> cached
            else -> loadHistoricalSeries(context, ticker)
        }
        priceHistory[ticker] = points.mapNotNull { point ->
            runCatching { LocalDate.parse(point.date) }.getOrNull()?.let { date ->
                HistoricalPricePoint(date, point.close)
            }
        }
    }
    return HistoricalVolatilityEngine.calculate(allocations, priceHistory)
}

private fun mergeHistoricalSeries(context: Context, symbol: String, newPoints: List<HistoricalPoint>): List<HistoricalPoint> {
    val merged = (loadHistoricalSeries(context, symbol) + newPoints)
        .filter { it.date.isNotBlank() && it.close > 0.0 }
        .associateBy { it.date }
        .values
        .sortedBy { it.date }
    saveHistoricalSeries(context, symbol, merged)
    return merged
}

private fun loadDividendPaymentSeries(context: Context, symbol: String): List<DividendPaymentPoint> {
    val raw = context.getSharedPreferences("long_run_dividend_history", Context.MODE_PRIVATE).getString(symbol, null) ?: return emptyList()
    return runCatching {
        val array = JSONArray(raw)
        (0 until array.length()).mapNotNull { index ->
            val item = array.optJSONObject(index) ?: return@mapNotNull null
            DividendPaymentPoint(item.optString("date"), item.optDouble("amount"))
                .takeIf { it.date.isNotBlank() && it.amount > 0.0 }
        }.sortedBy { it.date }
    }.getOrDefault(emptyList())
}

private fun saveDividendPaymentSeries(context: Context, symbol: String, points: List<DividendPaymentPoint>) {
    val array = JSONArray()
    points.sortedBy { it.date }.forEach { point ->
        array.put(JSONObject().apply {
            put("date", point.date)
            put("amount", point.amount)
        })
    }
    context.getSharedPreferences("long_run_dividend_history", Context.MODE_PRIVATE)
        .edit()
        .putString(symbol, array.toString())
        .apply()
}

private fun mergeDividendPaymentSeries(context: Context, symbol: String, newPoints: List<DividendPaymentPoint>): List<DividendPaymentPoint> {
    val merged = (loadDividendPaymentSeries(context, symbol) + newPoints)
        .filter { it.date.isNotBlank() && it.amount > 0.0 }
        .associateBy { it.date }
        .values
        .sortedBy { it.date }
    saveDividendPaymentSeries(context, symbol, merged)
    return merged
}

private fun historicalSummary(context: Context, symbol: String): String {
    val series = loadHistoricalSeries(context, symbol)
    val first = series.firstOrNull()?.date ?: return "미다운로드"
    val last = series.lastOrNull()?.date ?: return "미다운로드"
    val firstDate = parseAppDate(first)
    val lastDate = parseAppDate(last)
    val years = if (firstDate != null && lastDate != null) ChronoUnit.YEARS.between(firstDate, lastDate) else 0L
    return if (years < 5L) {
        "데이터 소스 부족 · $first ~ $last"
    } else {
        "$first ~ $last"
    }
}

private fun findHistoricalCloseOnOrBefore(context: Context, symbol: String, dateText: String): Double? {
    val target = parseAppDate(dateText) ?: return null
    return loadHistoricalSeries(context, symbol)
        .asSequence()
        .mapNotNull { point -> parseAppDate(point.date)?.let { it to point.close } }
        .filter { it.first <= target }
        .maxByOrNull { it.first }
        ?.second
}

private fun apiErrorMessage(root: JSONObject): String? {
    val rtCode = root.optString("rt_cd")
    val returnCode = root.optString("return_code")
    val code = root.optString("code")
    val messageCode = root.optString("msg_cd")
        .ifBlank { root.optString("return_msg_code") }
    val message = root.optString("msg1")
        .ifBlank { root.optString("message") }
        .ifBlank { root.optString("msg") }
        .ifBlank { root.optString("return_msg") }
    val failed = when {
        rtCode.isNotBlank() -> rtCode != "0"
        returnCode.isNotBlank() -> returnCode != "0" && returnCode != "0000"
        code.isNotBlank() -> code != "0" && code != "0000" && !code.equals("OK", ignoreCase = true)
        else -> false
    }
    return if (failed) {
        listOf(messageCode, message).filter { it.isNotBlank() }.joinToString(" ")
    } else {
        null
    }
}

private fun kisHeaders(token: String, appKey: String, appSecret: String, trId: String): Map<String, String> = mapOf(
    "authorization" to "Bearer $token",
    "appkey" to appKey,
    "appsecret" to appSecret,
    "tr_id" to trId,
    "custtype" to "P"
)

private fun postJson(url: String, body: String, headers: Map<String, String>): String =
    httpRequest(url, method = "POST", body = body, headers = headers + ("content-type" to "application/json"))

private fun getJson(url: String, headers: Map<String, String>): String =
    httpRequest(url, method = "GET", body = null, headers = headers)

private fun httpRequest(url: String, method: String, body: String?, headers: Map<String, String>): String {
    val connection = (URL(url).openConnection() as HttpURLConnection).apply {
        requestMethod = method
        connectTimeout = 8000
        readTimeout = 8000
        headers.forEach { (key, value) -> setRequestProperty(key, value) }
        if (body != null) {
            doOutput = true
            outputStream.use { it.write(body.toByteArray(Charsets.UTF_8)) }
        }
    }
    val responseCode = connection.responseCode
    val stream = if (responseCode in 200..299) connection.inputStream else connection.errorStream
    val response = stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
    if (responseCode !in 200..299) {
        throw IllegalStateException("HTTP $responseCode ${response.take(160)}")
    }
    return response
}

private fun firstDouble(json: JSONObject, vararg keys: String): Double? =
    keys.firstNotNullOfOrNull { key ->
        json.optString(key).replace(",", "").toDoubleOrNull()
    }

private fun historyIntervalLabel(interval: String): String =
    if (interval == HistoryInterval.DAILY) "일별" else "월별"

private fun saveDownloadedHistoricalSeries(
    context: Context,
    candidate: DownloadCandidate,
    interval: String,
    points: List<HistoricalPoint>,
    sourceName: String
): HistoricalDownloadResult {
    val distinctPoints = points
        .filter { it.close > 0.0 && it.date.isNotBlank() }
        .distinctBy { it.date }
        .sortedBy { it.date }
    if (distinctPoints.isEmpty()) {
        return HistoricalDownloadResult(
            status = DownloadStatus.NO_DATA,
            message = "데이터 없음: ${candidate.label} 종목 데이터를 찾지 못했어요."
        )
    }
    val mergedPoints = mergeHistoricalSeries(context, candidate.symbol, distinctPoints)
    val first = mergedPoints.first().date
    val last = mergedPoints.last().date
    val firstDate = parseAppDate(first)
    val lastDate = parseAppDate(last)
    val years = if (firstDate != null && lastDate != null) {
        ChronoUnit.YEARS.between(firstDate, lastDate).coerceAtLeast(0L)
    } else {
        0L
    }
    return if (years < 5L) {
        HistoricalDownloadResult(
            status = DownloadStatus.INSUFFICIENT_SOURCE,
            points = mergedPoints,
            message = "데이터 소스 부족: $sourceName 에서 $first ~ $last 까지만 저장했어요."
        )
    } else {
        HistoricalDownloadResult(
            status = DownloadStatus.SUCCESS,
            points = mergedPoints,
            message = "${candidate.label} ${historyIntervalLabel(interval)} 데이터를 저장했어요. $first ~ $last"
        )
    }
}

private suspend fun downloadYahooHistoricalSeries(symbol: String, interval: String): List<HistoricalPoint> {
    val intervalParam = if (interval == HistoryInterval.DAILY) "1d" else "1mo"
    val endEpoch = Instant.now().epochSecond
    yahooHistorySymbols(symbol).forEach { yahooSymbol ->
        val json = runCatching {
            getJson(
                url = "https://query1.finance.yahoo.com/v8/finance/chart/$yahooSymbol?period1=0&period2=$endEpoch&interval=$intervalParam&events=history&includeAdjustedClose=true",
                headers = mapOf(
                    "User-Agent" to "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 Chrome/126.0 Mobile Safari/537.36",
                    "Accept" to "application/json"
                )
            )
        }.getOrNull()
        val points = json?.let { parseYahooChartPoints(it) }.orEmpty()
        if (points.size >= 2) return points
        delay(250L)
    }
    return emptyList()
}

private suspend fun downloadAndStoreDividendPaymentSeries(context: Context, symbol: String): List<DividendPaymentPoint> {
    if (symbol == "USDKRW" || isKoreanTicker(symbol)) return emptyList()
    val payments = downloadYahooDividendSeries(symbol)
    return if (payments.isNotEmpty()) mergeDividendPaymentSeries(context, symbol, payments) else emptyList()
}

private suspend fun downloadYahooDividendSeries(symbol: String): List<DividendPaymentPoint> {
    val endEpoch = Instant.now().epochSecond
    yahooHistorySymbols(symbol).forEach { yahooSymbol ->
        val json = runCatching {
            getJson(
                url = "https://query1.finance.yahoo.com/v8/finance/chart/$yahooSymbol?period1=0&period2=$endEpoch&interval=1mo&events=div",
                headers = mapOf(
                    "User-Agent" to "Mozilla/5.0 (Linux; Android 14) AppleWebKit/537.36 Chrome/126.0 Mobile Safari/537.36",
                    "Accept" to "application/json"
                )
            )
        }.getOrNull()
        val points = json?.let { parseYahooDividendPoints(it) }.orEmpty()
        if (points.isNotEmpty()) return points
        delay(250L)
    }
    return emptyList()
}

private fun yahooHistorySymbols(symbol: String): List<String> {
    val clean = symbol.trim().uppercase(Locale.US)
    return when {
        clean == "USDKRW" -> listOf("KRW=X")
        clean.isBlank() -> emptyList()
        clean.contains("=") || clean.endsWith(".KS") || clean.endsWith(".KQ") -> listOf(clean)
        isKoreanTicker(clean) -> listOf("$clean.KS", "$clean.KQ")
        else -> listOf(clean)
    }
}

private fun parseYahooDividendPoints(json: String): List<DividendPaymentPoint> {
    val chart = JSONObject(json).optJSONObject("chart") ?: return emptyList()
    chart.optJSONObject("error")?.let { error ->
        throw IllegalStateException(error.optString("description").ifBlank { "Yahoo 배당 응답 오류" })
    }
    val result = chart.optJSONArray("result")?.optJSONObject(0) ?: return emptyList()
    val dividends = result.optJSONObject("events")?.optJSONObject("dividends") ?: return emptyList()
    val points = mutableListOf<DividendPaymentPoint>()
    val keys = dividends.keys()
    val zone = ZoneId.of("UTC")
    while (keys.hasNext()) {
        val item = dividends.optJSONObject(keys.next()) ?: continue
        val epoch = item.optLong("date", 0L)
        val amount = item.optDouble("amount", Double.NaN)
        if (epoch <= 0L || !amount.isFinite() || amount <= 0.0) continue
        val date = Instant.ofEpochSecond(epoch)
            .atZone(zone)
            .toLocalDate()
            .toString()
        points.add(DividendPaymentPoint(date, amount))
    }
    return points.distinctBy { it.date }.sortedBy { it.date }
}

private fun parseYahooChartPoints(json: String): List<HistoricalPoint> {
    val chart = JSONObject(json).optJSONObject("chart") ?: return emptyList()
    chart.optJSONObject("error")?.let { error ->
        throw IllegalStateException(error.optString("description").ifBlank { "Yahoo 응답 오류" })
    }
    val result = chart.optJSONArray("result")?.optJSONObject(0) ?: return emptyList()
    val timestamps = result.optJSONArray("timestamp") ?: return emptyList()
    val indicators = result.optJSONObject("indicators") ?: return emptyList()
    val closeArray = indicators
        .optJSONArray("adjclose")
        ?.optJSONObject(0)
        ?.optJSONArray("adjclose")
        ?: indicators.optJSONArray("quote")?.optJSONObject(0)?.optJSONArray("close")
        ?: return emptyList()
    val points = mutableListOf<HistoricalPoint>()
    val zone = ZoneId.of("UTC")
    val size = minOf(timestamps.length(), closeArray.length())
    for (index in 0 until size) {
        if (closeArray.isNull(index)) continue
        val close = closeArray.optDouble(index, Double.NaN)
        if (!close.isFinite() || close <= 0.0) continue
        val date = Instant.ofEpochSecond(timestamps.optLong(index))
            .atZone(zone)
            .toLocalDate()
            .toString()
        points.add(HistoricalPoint(date, close))
    }
    return points.distinctBy { it.date }.sortedBy { it.date }
}

private fun kisOverseasExchangeCandidates(ticker: String): List<String> {
    val symbol = ticker.uppercase(Locale.US)
    return (listOfNotNull(KisOverseasExchangeHints[symbol]) + KisOverseasExchangeOrder).distinct()
}

private fun expectedPlanValue(currentAmount: Long, plan: GoalPlan): Long {
    val elapsedYears = elapsedYearsFromStart(plan.startDate).coerceAtLeast(0.0)
    return plannedValueAtYear(plan, elapsedYears).roundToLong().coerceAtLeast(1L)
}

private fun goalChartPreviewPoints(
    accounts: List<AccountUi>,
    plan: GoalPlan,
    fallbackTotal: Long
): List<GoalChartPoint> {
    val start = parseAppDate(plan.startDate) ?: LocalDate.now()
    val end = start.plusYears(plan.years.toLong().coerceAtLeast(1L))
    val today = LocalDate.now()
    val months = monthEndsBetween(start, end)
    if (months.isEmpty()) {
        return listOf(
            GoalChartPoint("시작", fallbackTotal.toDouble(), plannedValueAtYear(plan, 0.0)),
            GoalChartPoint("${plan.years}년", null, plan.targetAmount.toDouble())
        )
    }
    return months.map { monthEnd ->
        val elapsedYears = ChronoUnit.DAYS.between(start, monthEnd).coerceAtLeast(0L) / 365.25
        val actual = if (!monthEnd.isAfter(today)) {
            portfolioCurrentValueAtDate(accounts, monthEnd, fallbackTotal)
        } else {
            null
        }
        GoalChartPoint(
            label = "${monthEnd.year}.${monthEnd.monthValue.toString().padStart(2, '0')}",
            actual = actual,
            target = plannedValueAtYear(plan, elapsedYears)
        )
    }.let { points ->
        if (points.size >= 2) points else points + GoalChartPoint("${plan.years}년", null, plan.targetAmount.toDouble())
    }
}

private fun goalChartPoints(
    context: Context,
    accounts: List<AccountUi>,
    plan: GoalPlan,
    fallbackTotal: Long
): List<GoalChartPoint> {
    val start = parseAppDate(plan.startDate) ?: LocalDate.now()
    val end = start.plusYears(plan.years.toLong().coerceAtLeast(1L))
    val today = LocalDate.now()
    val months = monthEndsBetween(start, end)
    if (months.isEmpty()) {
        return listOf(
            GoalChartPoint("시작", fallbackTotal.toDouble(), plannedValueAtYear(plan, 0.0)),
            GoalChartPoint("${plan.years}년", null, plan.targetAmount.toDouble())
        )
    }
    return months.map { monthEnd ->
        val elapsedYears = ChronoUnit.DAYS.between(start, monthEnd).coerceAtLeast(0L) / 365.25
        val actualDate = if (monthEnd.isAfter(today)) today else monthEnd
        val actual = if (!monthEnd.isAfter(today)) {
            portfolioValueAtDate(context, accounts, actualDate, fallbackTotal)
        } else {
            null
        }
        GoalChartPoint(
            label = "${monthEnd.year}.${monthEnd.monthValue.toString().padStart(2, '0')}",
            actual = actual,
            target = plannedValueAtYear(plan, elapsedYears)
        )
    }.let { points ->
        if (points.size >= 2) points else points + GoalChartPoint("${plan.years}년", null, plan.targetAmount.toDouble())
    }
}

private fun monthEndsBetween(start: LocalDate, end: LocalDate): List<LocalDate> {
    val result = mutableListOf<LocalDate>()
    var cursor = start.withDayOfMonth(1)
    val lastMonth = end.withDayOfMonth(1)
    while (!cursor.isAfter(lastMonth)) {
        val monthEnd = cursor.withDayOfMonth(cursor.lengthOfMonth())
        result.add(
            when {
                monthEnd.isBefore(start) -> start
                monthEnd.isAfter(end) -> end
                else -> monthEnd
            }
        )
        cursor = cursor.plusMonths(1)
    }
    return result.distinct()
}

private fun portfolioCurrentValueAtDate(
    accounts: List<AccountUi>,
    date: LocalDate,
    fallbackTotal: Long
): Double? {
    val currentMonth = LocalDate.now().withDayOfMonth(1)
    var value = 0.0
    var usedAny = false
    accounts.flatMap { it.holdings }.forEach { holding ->
        val quantity = holdingQuantityAt(holding, date)
        if (quantity <= 0.0) return@forEach
        val price = holding.currentPrice.takeIf { it > 0.0 } ?: holding.averagePrice
        if (price <= 0.0) return@forEach
        val fx = if (isKoreanTicker(holding.ticker)) {
            1.0
        } else {
            holding.exchangeRate.takeIf { it > 0.0 } ?: DefaultUsdKrw
        }
        value += quantity * price * fx
        usedAny = true
    }
    return when {
        usedAny && value > 0.0 -> value
        !date.isBefore(currentMonth) && fallbackTotal > 0L -> fallbackTotal.toDouble()
        else -> null
    }
}

private fun portfolioValueAtDate(
    context: Context,
    accounts: List<AccountUi>,
    date: LocalDate,
    fallbackTotal: Long
): Double? {
    val today = LocalDate.now()
    var value = 0.0
    var usedAny = false
    accounts.flatMap { it.holdings }.forEach { holding ->
        val quantity = holdingQuantityAt(holding, date)
        if (quantity <= 0.0) return@forEach
        val price = if (!date.isBefore(today.withDayOfMonth(1)) && holding.currentPrice > 0.0) {
            holding.currentPrice
        } else {
            historicalPriceOnOrBefore(context, holding.ticker, date)
        } ?: return@forEach
        val fx = if (isKoreanTicker(holding.ticker)) {
            1.0
        } else {
            historicalPriceOnOrBefore(context, "USDKRW", date)
                ?: if (!date.isBefore(today.withDayOfMonth(1))) holding.exchangeRate else DefaultUsdKrw
        }
        value += quantity * price * fx
        usedAny = true
    }
    return when {
        usedAny && value > 0.0 -> value
        !date.isBefore(today.withDayOfMonth(1)) && fallbackTotal > 0L -> fallbackTotal.toDouble()
        else -> null
    }
}

private fun holdingQuantityAt(holding: HoldingUi, date: LocalDate): Double {
    if (holding.trades.isEmpty()) {
        return if (!date.isBefore(LocalDate.now().withDayOfMonth(1))) holding.quantity else 0.0
    }
    var quantity = 0.0
    holding.trades
        .sortedWith(compareBy<TradeUi> { parseAppDate(it.date) ?: LocalDate.MIN }.thenBy { it.id })
        .forEach { trade ->
            val tradeDate = parseAppDate(trade.date) ?: return@forEach
            if (tradeDate.isAfter(date)) return@forEach
            when (trade.side) {
                TradeSide.BUY, TradeSide.HOLD -> quantity += trade.quantity
                TradeSide.SELL -> quantity = (quantity - trade.quantity).coerceAtLeast(0.0)
            }
        }
    return quantity
}

private fun historicalPriceOnOrBefore(context: Context, symbol: String, date: LocalDate): Double? =
    loadHistoricalSeries(context, symbol)
        .asSequence()
        .mapNotNull { point -> parseAppDate(point.date)?.let { it to point.close } }
        .filter { it.first <= date }
        .maxByOrNull { it.first }
        ?.second

private fun plannedValueAtYear(plan: GoalPlan, year: Double): Double {
    val annualRate = (plan.annualTargetReturn / 100.0).coerceAtLeast(0.0)
    val remainingYears = (plan.years - year.coerceIn(0.0, plan.years.toDouble())).coerceAtLeast(0.0)
    return plan.targetAmount / (1.0 + annualRate).pow(remainingYears)
}

private fun portfolioProfitForPeriod(context: Context, holdings: List<HoldingUi>, selectedPeriod: Int): Long =
    holdings.sumOf { holdingProfitForPeriod(context, it, selectedPeriod) }

private fun portfolioBaseForPeriod(context: Context, holdings: List<HoldingUi>, selectedPeriod: Int): Long =
    holdings.sumOf { holdingBaseForPeriod(context, it, selectedPeriod) }

private fun holdingProfitForPeriod(context: Context, holding: HoldingUi, selectedPeriod: Int): Long =
    when (selectedPeriod) {
        0 -> holding.dayProfit
        1 -> holding.amount - holding.principal
        else -> {
            val startDate = periodStartDate(selectedPeriod)
            val startValue = holdingValueAtDate(context, holding, startDate)
            val cashFlow = holdingCashFlowAfter(holding, startDate, buysOnly = false)
            holding.amount - startValue - cashFlow
        }
    }

private fun holdingBaseForPeriod(context: Context, holding: HoldingUi, selectedPeriod: Int): Long =
    when (selectedPeriod) {
        0 -> holding.amount
        1 -> holding.principal
        else -> {
            val startDate = periodStartDate(selectedPeriod)
            val startValue = holdingValueAtDate(context, holding, startDate)
            val buyCashFlow = holdingCashFlowAfter(holding, startDate, buysOnly = true)
            (startValue + buyCashFlow).coerceAtLeast(0L)
        }
    }

private fun holdingValueAtDate(context: Context, holding: HoldingUi, date: LocalDate): Long {
    val quantity = holdingQuantityAt(holding, date)
    if (quantity <= 0.0) return 0L
    val currentMonth = LocalDate.now().withDayOfMonth(1)
    val price = historicalPriceOnOrBefore(context, holding.ticker, date)
        ?: if (!date.isBefore(currentMonth) && holding.previousClosePrice > 0.0) holding.previousClosePrice else holding.averagePrice
    val exchangeRate = if (isKoreanTicker(holding.ticker)) {
        1.0
    } else {
        historicalPriceOnOrBefore(context, "USDKRW", date)
            ?: if (!date.isBefore(currentMonth)) holding.exchangeRate else holding.averageExchangeRate
    }
    return (quantity * price * holdingExchangeRate(holding.ticker, exchangeRate)).roundToLong()
}

private fun holdingCashFlowAfter(holding: HoldingUi, startDate: LocalDate, buysOnly: Boolean): Long =
    holding.trades
        .asSequence()
        .mapNotNull { trade -> parseAppDate(trade.date)?.let { it to trade } }
        .filter { (date, _) -> date.isAfter(startDate) }
        .filter { (_, trade) -> !buysOnly || trade.side == TradeSide.BUY || trade.side == TradeSide.HOLD }
        .sumOf { (_, trade) ->
            val amount = (trade.quantity * trade.price * holdingExchangeRate(holding.ticker, trade.exchangeRate)).roundToLong()
            when (trade.side) {
                TradeSide.SELL -> -amount
                else -> amount
            }
        }

private val HoldingUi.dayRate: Double
    get() = if (previousClosePrice > 0.0) {
        (currentPrice - previousClosePrice) / previousClosePrice
    } else {
        0.0
    }

private val HoldingUi.dayProfit: Long
    get() = if (previousClosePrice > 0.0) {
        (quantity * (currentPrice - previousClosePrice) * holdingExchangeRate(ticker, exchangeRate)).roundToLong()
    } else {
        0L
    }

private fun periodStartDate(selectedPeriod: Int): LocalDate {
    val now = LocalDate.now()
    return when (selectedPeriod) {
        2 -> now.minusDays((now.dayOfWeek.value - 1).toLong())
        3 -> now.withDayOfMonth(1)
        4 -> now.withDayOfYear(1)
        else -> now
    }
}

private fun parseAppDate(value: String): LocalDate? {
    val numbers = Regex("\\d+").findAll(value).map { it.value.toInt() }.toList()
    if (numbers.size < 3) return null
    return runCatching {
        val year = numbers[0]
        val month = numbers[1].coerceIn(1, 12)
        val lastDay = LocalDate.of(year, month, 1).lengthOfMonth()
        LocalDate.of(year, month, numbers[2].coerceIn(1, lastDay))
    }.getOrNull()
}

private fun formatKoreanDate(date: LocalDate): String =
    "${date.year}년 ${date.monthValue}월 ${date.dayOfMonth}일"

private fun trendRangeStart(selectedRange: Int): LocalDate {
    val now = LocalDate.now()
    return when (selectedRange) {
        0 -> now.withDayOfYear(1)
        1 -> now.withDayOfMonth(1)
        2 -> now.minusMonths(1)
        3 -> now.minusMonths(6)
        4 -> now.minusYears(1)
        5 -> now.minusYears(5)
        6 -> now.minusYears(10)
        7 -> now.minusYears(15)
        8 -> now.minusYears(20)
        else -> now.withDayOfYear(1)
    }
}

private fun elapsedPlanText(startDate: String): String {
    val start = parseAppDate(startDate) ?: return "시작 전"
    val days = ChronoUnit.DAYS.between(start, LocalDate.now()).coerceAtLeast(0L)
    return when {
        days < 30L -> "${days}일 차"
        days < 365L -> "${days / 30L}개월 차"
        else -> {
            val years = days / 365L
            val months = (days % 365L) / 30L
            if (months == 0L) "${years}년 차" else "${years}년 ${months}개월 차"
        }
    }
}

private fun compactSearchText(value: String): String =
    value.replace(" ", "").replace("-", "").uppercase(Locale.US)

private fun elapsedYearsFromStart(startDate: String): Double {
    val numbers = Regex("\\d+").findAll(startDate).map { it.value.toInt() }.toList()
    if (numbers.size < 3) return 0.0
    return runCatching {
        val start = LocalDate.of(numbers[0], numbers[1].coerceIn(1, 12), numbers[2].coerceIn(1, 28))
        ChronoUnit.DAYS.between(start, LocalDate.now()).coerceAtLeast(0L) / 365.25
    }.getOrDefault(0.0)
}

private fun onlyDigits(value: String): String = value.filter { it.isDigit() }

private fun initialAccounts(): List<AccountUi> = emptyList()

private fun accountBadgeText(account: AccountUi): String = account.iconText.ifBlank { account.broker }

private fun displayModeLabel(mode: String): String = when (mode) {
    DisplayMode.LIGHT -> "밝게"
    DisplayMode.DARK -> "어둡게"
    else -> "시스템"
}

private fun protectionModeLabel(mode: ProtectionMode): String = when (mode) {
    ProtectionMode.NORMAL -> "일반 모드"
    ProtectionMode.WEAK -> "약한 보호 모드"
    ProtectionMode.MEDIUM -> "중간 보호 모드"
    ProtectionMode.STRONG -> "강한 보호 모드"
}

private fun protectionModeDescription(mode: ProtectionMode): String = when (mode) {
    ProtectionMode.NORMAL -> "보호 모드를 적용하지 않습니다."
    ProtectionMode.WEAK -> "10분 대기 · 하루 24회 · 앱을 나가면 다시 잠금"
    ProtectionMode.MEDIUM -> "1시간 대기 · 하루 12회 · 투자 원칙 문장 입력"
    ProtectionMode.STRONG -> "4시간 대기 · 하루 4회 · 앱을 나가면 다시 잠금 · 등록 QR 코드 스캔"
}

private fun formatProtectionCountdown(milliseconds: Long): String {
    val totalSeconds = ((milliseconds + 999L) / 1_000L).coerceAtLeast(0L)
    val hours = totalSeconds / 3_600L
    val minutes = totalSeconds % 3_600L / 60L
    val seconds = totalSeconds % 60L
    return if (hours > 0L) {
        "%02d:%02d:%02d".format(Locale.US, hours, minutes, seconds)
    } else {
        "%02d:%02d".format(Locale.US, minutes, seconds)
    }
}

private fun currencyLabel(currency: String): String = when (currency) {
    CurrencyMode.USD -> "달러"
    else -> "원"
}

private fun apiProviderLabel(provider: String): String = when (provider) {
    ApiProvider.KIWOOM -> "키움"
    else -> "한국투자"
}

private fun providerAppKey(settings: AppSettings, provider: String): String = when (provider) {
    ApiProvider.KIWOOM -> settings.kiwoomAppKey
    else -> settings.kisAppKey
}

private fun providerAppSecret(settings: AppSettings, provider: String): String = when (provider) {
    ApiProvider.KIWOOM -> settings.kiwoomAppSecret
    else -> settings.kisAppSecret
}

private fun providerHasKeys(settings: AppSettings, provider: String): Boolean =
    providerAppKey(settings, provider).isNotBlank() && providerAppSecret(settings, provider).isNotBlank()

private fun apiKeyStatusLabel(settings: AppSettings): String =
    if (providerHasKeys(settings, settings.apiProvider)) "입력됨" else "미입력"

private fun todayText(): String {
    val now = LocalDate.now()
    return "${now.year}. ${now.monthValue}. ${now.dayOfMonth}."
}

private fun applyAppPalette(dark: Boolean) {
    AppBackground = if (dark) Color(0xFF0F1115) else Color.White
    SoftSurface = if (dark) Color(0xFF1B1F26) else Color(0xFFF5F6F8)
    LineColor = if (dark) Color(0xFF2B313A) else Color(0xFFE8EAEE)
    TextPrimary = if (dark) Color(0xFFF2F4F7) else Color(0xFF111315)
    TextSecondary = if (dark) Color(0xFFAAB2BD) else Color(0xFF737A84)
    MutedText = if (dark) Color(0xFF68717D) else Color(0xFFB9C0C8)
    PanelColor = if (dark) Color(0xFF151922) else Color.White
}

private const val CustomAssetOptionsKey = "custom_asset_options"

private fun manualAssetOptions(context: Context): List<AssetOption> {
    val defaultTickers = assetOptions.map { it.ticker.uppercase(Locale.US) }.toSet()
    val customAssets = loadCustomAssetOptions(context)
        .filterNot { it.ticker.uppercase(Locale.US) in defaultTickers }
    return assetOptions + customAssets
}

private fun loadCustomAssetOptions(context: Context): List<AssetOption> =
    runCatching {
        val raw = context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
            .getString(CustomAssetOptionsKey, null)
        if (raw.isNullOrBlank()) return@runCatching emptyList()
        val json = JSONArray(raw)
        buildList {
            for (index in 0 until json.length()) {
                assetOptionFromJson(json.optJSONObject(index))?.let(::add)
            }
        }.distinctBy { it.ticker.uppercase(Locale.US) }
    }.getOrElse { emptyList() }

private fun saveCustomAssetOptions(context: Context, assets: List<AssetOption>) {
    val defaultTickers = assetOptions.map { it.ticker.uppercase(Locale.US) }.toSet()
    val json = JSONArray()
    assets
        .filter { it.ticker.isNotBlank() && it.name.isNotBlank() }
        .filterNot { it.ticker.uppercase(Locale.US) in defaultTickers }
        .distinctBy { it.ticker.uppercase(Locale.US) }
        .forEach { json.put(assetOptionToJson(it)) }

    context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
        .edit()
        .putString(CustomAssetOptionsKey, json.toString())
        .apply()
}

private fun assetOptionToJson(asset: AssetOption): JSONObject =
    JSONObject().apply {
        put("ticker", asset.ticker.uppercase(Locale.US))
        put("name", asset.name)
        put("color", asset.color.toArgb())
    }

private fun assetOptionFromJson(item: JSONObject?): AssetOption? {
    if (item == null) return null
    val ticker = item.optString("ticker").trim().uppercase(Locale.US)
    val name = item.optString("name").trim()
    if (ticker.isBlank() || name.isBlank()) return null
    return AssetOption(
        ticker = ticker,
        name = name,
        price = 0.0,
        color = Color(item.optInt("color", colorForCustomAsset(ticker).toArgb()))
    )
}

private fun colorForCustomAsset(ticker: String): Color {
    val palette = listOf(
        Color(0xFF0DA9D7),
        Color(0xFF234DFF),
        Color(0xFF6F321D),
        Color(0xFF2C7BE5),
        Color(0xFF1C9FC9),
        Color(0xFFB65C31),
        Color(0xFF76B900),
        Color(0xFF7B61FF)
    )
    val index = ((ticker.hashCode().toLong() and 0x7FFFFFFF) % palette.size).toInt()
    return palette[index]
}

private val assetOptions = listOf(
    AssetOption("QLD", "ProShares Ultra QQQ", 89.12, NasdaqBlue),
    AssetOption("QQQ", "Invesco QQQ Trust", 522.40, Color(0xFF234DFF)),
    AssetOption("TQQQ", "ProShares UltraPro QQQ", 82.00, NasdaqBlue),
    AssetOption("SOXL", "Direxion Daily Semiconductor Bull 3X", 40.00, Color(0xFF1266B0)),
    AssetOption("SPY", "SPDR S&P 500 ETF Trust", 590.00, Color(0xFF4E78A8)),
    AssetOption("VOO", "Vanguard S&P 500 ETF", 545.00, Color(0xFFB65C31)),
    AssetOption("SCHD", "Schwab US Dividend Equity ETF", 80.00, Color(0xFF1C9FC9)),
    AssetOption("QQQI", "NEOS Nasdaq-100 High Income ETF", 50.00, Color(0xFF4C6FFF)),
    AssetOption("SPYI", "NEOS S&P 500 High Income ETF", 50.00, Color(0xFF2C7BE5)),
    AssetOption("AAPL", "Apple", 210.00, Color(0xFF111315)),
    AssetOption("MSFT", "Microsoft", 480.00, Color(0xFF2176FF)),
    AssetOption("NVDA", "NVIDIA", 150.00, Color(0xFF76B900)),
    AssetOption("TSLA", "Tesla", 190.00, Color(0xFFE82127)),
    AssetOption("GOOGL", "Alphabet Class A", 180.00, Color(0xFF4285F4)),
    AssetOption("AMZN", "Amazon", 220.00, Color(0xFFFF9900)),
    AssetOption("META", "Meta Platforms", 640.00, Color(0xFF0866FF)),
    AssetOption("005930", "삼성전자", 0.0, BrokerBlue),
    AssetOption("367380", "ACE 미국나스닥100", 0.0, Color(0xFF6F321D)),
    AssetOption("433880", "PLUS TDF2060액티브 적격", 0.0, Color(0xFFFF7A00)),
    AssetOption("379810", "KODEX 미국나스닥100TR", 0.0, Color(0xFF2350A9)),
    AssetOption("133690", "TIGER 미국나스닥100", 0.0, Color(0xFFFF8B00)),
    AssetOption("449190", "ACE 미국S&P500", 0.0, Color(0xFF6F321D)),
    AssetOption("360750", "TIGER 미국S&P500", 0.0, Color(0xFFFF8B00))
)

private val accountLogoOptions = listOf(
    AccountIconOption("Mirae Asset", "MI", Color(0xFFFF762B)),
    AccountIconOption("ACE", "ACE", Color(0xFF6F321D)),
    AccountIconOption("Jinyoung ISA", "IS", CashOrange),
    AccountIconOption("Samsung", "삼성", BrokerBlue),
    AccountIconOption("KB", "KB", Color(0xFFF5B500)),
    AccountIconOption("Korea", "K", Color(0xFF12336E)),
    AccountIconOption("Shinhan", "S", Color(0xFF0C66E4)),
    AccountIconOption("Hana", "하나", Color(0xFF00846B)),
    AccountIconOption("Meritz", "M", Color(0xFFE7352C)),
    AccountIconOption("BNK", "BNK", Color(0xFFE0202D)),
    AccountIconOption("NH", "NH", Color(0xFF163D91)),
    AccountIconOption("Kiwoom", "키움", Color(0xFF0D4EA6)),
    AccountIconOption("Hanwha", "한화", Color(0xFFF37A15)),
    AccountIconOption("Samsung Card", "삼카", Color(0xFF0873C9)),
    AccountIconOption("Toss", "T", Color(0xFF0064FF)),
    AccountIconOption("Kakao", "카", Color(0xFFF9D716)),
    AccountIconOption("Kbank", "K", Color(0xFF16006A)),
    AccountIconOption("Citi", "citi", Color(0xFF003B64))
)

private val accountEtcIconOptions = listOf(
    AccountIconOption("Green", "S", Color(0xFF62B69A)),
    AccountIconOption("Blue", "S", Color(0xFF7387BC)),
    AccountIconOption("Pink", "S", Color(0xFFD77BC7)),
    AccountIconOption("Triangle", "▲", Color(0xFF9A60C8)),
    AccountIconOption("Gear", "＊", Color(0xFFE8934C)),
    AccountIconOption("Pentagon", "⬟", Color(0xFF64AFC6))
)

private val defaultAccountIcon = accountLogoOptions.first { it.label == "Samsung" }

private fun loadAppSettings(context: Context): AppSettings {
    val prefs = context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
    return AppSettings(
        displayMode = prefs.getString("display_mode", DisplayMode.SYSTEM) ?: DisplayMode.SYSTEM,
        currency = prefs.getString("currency", CurrencyMode.KRW) ?: CurrencyMode.KRW,
        apiProvider = prefs.getString("api_provider", ApiProvider.KIS) ?: ApiProvider.KIS,
        kisAppKey = prefs.getString("kis_app_key", "") ?: "",
        kisAppSecret = prefs.getString("kis_app_secret", "") ?: "",
        kiwoomAppKey = prefs.getString("kiwoom_app_key", "") ?: "",
        kiwoomAppSecret = prefs.getString("kiwoom_app_secret", "") ?: "",
        homeProfitMode = prefs.getString("home_profit_mode", ProfitMode.DAY) ?: ProfitMode.DAY
    )
}

private fun saveAppSettings(context: Context, settings: AppSettings) {
    val prefs = context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
    val previousKey = prefs.getString("kis_app_key", "") ?: ""
    val previousSecret = prefs.getString("kis_app_secret", "") ?: ""
    val previousKiwoomKey = prefs.getString("kiwoom_app_key", "") ?: ""
    val previousKiwoomSecret = prefs.getString("kiwoom_app_secret", "") ?: ""
    val editor = prefs.edit()
        .putString("display_mode", settings.displayMode)
        .putString("currency", settings.currency)
        .putString("api_provider", settings.apiProvider)
        .putString("kis_app_key", settings.kisAppKey)
        .putString("kis_app_secret", settings.kisAppSecret)
        .putString("kiwoom_app_key", settings.kiwoomAppKey)
        .putString("kiwoom_app_secret", settings.kiwoomAppSecret)
        .putString("home_profit_mode", settings.homeProfitMode)
    if (previousKey != settings.kisAppKey || previousSecret != settings.kisAppSecret) {
        editor
            .remove("kis_access_token")
            .remove("kis_access_token_expires_at")
    }
    if (previousKiwoomKey != settings.kiwoomAppKey || previousKiwoomSecret != settings.kiwoomAppSecret) {
        editor
            .remove("kiwoom_access_token")
            .remove("kiwoom_access_token_expires_at")
    }
    editor.apply()
}

private fun loadAppState(context: Context): SavedAppState {
    val raw = context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE).getString("app_state", null)
        ?: return SavedAppState(initialAccounts(), GoalPlan())
    return runCatching {
        val root = JSONObject(raw)
        val planJson = root.optJSONObject("goalPlan")
        val goalPlan = if (planJson == null) {
            GoalPlan()
        } else {
            GoalPlan(
                startDate = planJson.optString("startDate", "2026. 6. 28."),
                years = planJson.optInt("years", 20),
                annualTargetReturn = planJson.optDouble("annualTargetReturn", 12.0),
                targetAmount = planJson.optLong("targetAmount", 1_000_000_000L)
            )
        }
        val accounts = root.optJSONArray("accounts")?.let { array ->
            (0 until array.length()).map { index ->
                val item = array.getJSONObject(index)
                AccountUi(
                    id = item.optInt("id"),
                    name = item.optString("name"),
                    broker = item.optString("broker", "삼성증권"),
                    manual = true,
                    color = Color(item.optLong("color", BrokerBlue.value.toLong()).toULong()),
                    iconText = item.optString("iconText", ""),
                    fixedAmount = item.optLongOrNull("fixedAmount"),
                    sortField = item.optString("sortField", SortField.DIRECT),
                    sortDescending = item.optBoolean("sortDescending", true),
                    holdings = item.optJSONArray("holdings")?.let { holdings ->
                        (0 until holdings.length()).map { hIndex ->
                            val holding = holdings.getJSONObject(hIndex)
                            HoldingUi(
                                ticker = holding.optString("ticker"),
                                name = holding.optString("name"),
                                quantity = holding.optDouble("quantity"),
                                averagePrice = holding.optDouble("averagePrice"),
                                currentPrice = holding.optDouble("currentPrice"),
                                color = Color(holding.optLong("color", NasdaqBlue.value.toLong()).toULong()),
                                exchangeRate = holding.optDouble("exchangeRate", root.optDouble("usdKrw", DefaultUsdKrw)),
                                averageExchangeRate = holding.optDouble(
                                    "averageExchangeRate",
                                    inferPurchaseExchangeRate(holding, holding.optDouble("exchangeRate", root.optDouble("usdKrw", DefaultUsdKrw)))
                                ),
                                previousClosePrice = holding.optDouble("previousClosePrice", 0.0),
                                trades = holding.optJSONArray("trades")?.let { trades ->
                                    (0 until trades.length()).map { tIndex ->
                                        val trade = trades.getJSONObject(tIndex)
                                        TradeUi(
                                            id = trade.optLong("id"),
                                            date = trade.optString("date"),
                                            side = trade.optString("side", TradeSide.BUY),
                                            quantity = trade.optDouble("quantity"),
                                            price = trade.optDouble("price"),
                                            exchangeRate = trade.optDouble(
                                                "exchangeRate",
                                                inferTradeExchangeRate(trade.optString("date"), holding.optDouble("exchangeRate", root.optDouble("usdKrw", DefaultUsdKrw)))
                                            )
                                        )
                                    }
                                } ?: emptyList()
                            )
                        }
                    } ?: emptyList()
                )
            }
        } ?: emptyList()
        SavedAppState(accounts, goalPlan, root.optDouble("usdKrw", DefaultUsdKrw))
    }.getOrElse { SavedAppState(initialAccounts(), GoalPlan()) }
}

private fun saveAppState(context: Context, accounts: List<AccountUi>, goalPlan: GoalPlan, usdKrw: Double) {
    val root = JSONObject()
    root.put("usdKrw", usdKrw)
    root.put("goalPlan", JSONObject().apply {
        put("startDate", goalPlan.startDate)
        put("years", goalPlan.years)
        put("annualTargetReturn", goalPlan.annualTargetReturn)
        put("targetAmount", goalPlan.targetAmount)
    })
    root.put("accounts", JSONArray().apply {
        accounts.forEach { account ->
            put(JSONObject().apply {
                put("id", account.id)
                put("name", account.name)
                put("broker", account.broker)
                put("color", account.color.value.toLong())
                put("iconText", account.iconText)
                if (account.fixedAmount != null) put("fixedAmount", account.fixedAmount)
                put("sortField", account.sortField)
                put("sortDescending", account.sortDescending)
                put("holdings", JSONArray().apply {
                    account.holdings.forEach { holding ->
                        put(JSONObject().apply {
                            put("ticker", holding.ticker)
                            put("name", holding.name)
                            put("quantity", holding.quantity)
                            put("averagePrice", holding.averagePrice)
                            put("currentPrice", holding.currentPrice)
                            put("previousClosePrice", holding.previousClosePrice)
                            put("color", holding.color.value.toLong())
                            put("exchangeRate", holding.exchangeRate)
                            put("averageExchangeRate", holding.averageExchangeRate)
                            put("trades", JSONArray().apply {
                                holding.trades.forEach { trade ->
                                    put(JSONObject().apply {
                                        put("id", trade.id)
                                        put("date", trade.date)
                                        put("side", trade.side)
                                        put("quantity", trade.quantity)
                                        put("price", trade.price)
                                        put("exchangeRate", trade.exchangeRate)
                                    })
                                }
                            })
                        })
                    }
                })
            })
        }
    })
    context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
        .edit()
        .putString("app_state", root.toString())
        .apply()
}

private const val AppBackupVersion = 1

private val BackupPreferenceNames = listOf(
    "long_run_portfolio",
    "long_run_history",
    "long_run_dividend_history",
    VolatilityHistoryPreferences,
    ProtectionStore.PreferencesName
)

private val BackupExcludedKeys = setOf(
    "market_last_refresh_at",
    "kiwoom_access_token",
    "kiwoom_access_token_app_key",
    "kiwoom_access_token_expires_at",
    "kis_access_token",
    "kis_access_token_app_key",
    "kis_access_token_expires_at",
    ProtectionStore.UsageDateKey,
    ProtectionStore.UsageCountKey,
    ProtectionStore.LockStartedAtKey,
    ProtectionStore.OneTimeWaitBypassKey
)

private data class BackupResult(val success: Boolean, val message: String)

private fun appBackupFileName(): String = "snowball-backup-${LocalDate.now()}.json"

private fun exportAppBackup(
    context: Context,
    uri: Uri,
    accounts: List<AccountUi>,
    goalPlan: GoalPlan,
    usdKrw: Double,
    settings: AppSettings
): BackupResult {
    return runCatching {
        saveAppState(context, accounts, goalPlan, usdKrw)
        saveAppSettings(context, settings)
        val backupJson = JSONObject().apply {
            put("version", AppBackupVersion)
            put("createdAt", Instant.now().toString())
            put("app", "스노우볼")
            put("preferences", JSONObject().apply {
                BackupPreferenceNames.forEach { prefName ->
                    put(prefName, preferenceToJson(context, prefName))
                }
            })
        }
        val output = context.contentResolver.openOutputStream(uri)
            ?: return BackupResult(false, "백업 파일을 열 수 없어요.")
        output.use { stream ->
            stream.write(backupJson.toString(2).toByteArray(Charsets.UTF_8))
        }
        BackupResult(true, "백업 파일을 저장했어요.")
    }.getOrElse {
        BackupResult(false, "백업 저장에 실패했어요. ${it.message.orEmpty()}")
    }
}

private fun importAppBackup(context: Context, uri: Uri): BackupResult {
    return runCatching {
        val input = context.contentResolver.openInputStream(uri)
            ?: return BackupResult(false, "백업 파일을 열 수 없어요.")
        val raw = input.use { stream ->
            stream.bufferedReader(Charsets.UTF_8).readText()
        }
        val root = JSONObject(raw)
        val preferences = root.optJSONObject("preferences")
            ?: return BackupResult(false, "스노우볼 백업 파일이 아니에요.")
        BackupPreferenceNames.forEach { prefName ->
            preferences.optJSONObject(prefName)?.let { prefJson ->
                restorePreferenceFromJson(context, prefName, prefJson)
            }
        }
        BackupResult(true, "백업 데이터를 불러왔어요.")
    }.getOrElse {
        BackupResult(false, "백업 불러오기에 실패했어요. ${it.message.orEmpty()}")
    }
}

private fun preferenceToJson(context: Context, name: String): JSONObject {
    val json = JSONObject()
    context.getSharedPreferences(name, Context.MODE_PRIVATE).all.forEach { (key, value) ->
        if (key !in BackupExcludedKeys) {
            preferenceValueToJson(value)?.let { json.put(key, it) }
        }
    }
    return json
}

private fun preferenceValueToJson(value: Any?): JSONObject? = when (value) {
    is String -> JSONObject().put("type", "string").put("value", value)
    is Int -> JSONObject().put("type", "int").put("value", value)
    is Long -> JSONObject().put("type", "long").put("value", value)
    is Float -> JSONObject().put("type", "float").put("value", value.toDouble())
    is Boolean -> JSONObject().put("type", "boolean").put("value", value)
    is Set<*> -> JSONObject()
        .put("type", "string_set")
        .put("value", JSONArray().apply {
            value.filterIsInstance<String>().forEach { put(it) }
        })
    else -> null
}

private fun restorePreferenceFromJson(context: Context, name: String, json: JSONObject) {
    val editor = context.getSharedPreferences(name, Context.MODE_PRIVATE).edit().clear()
    val keys = json.keys()
    while (keys.hasNext()) {
        val key = keys.next()
        if (key in BackupExcludedKeys) continue
        val valueJson = json.optJSONObject(key) ?: continue
        when (valueJson.optString("type")) {
            "string" -> editor.putString(key, valueJson.optString("value", ""))
            "int" -> editor.putInt(key, valueJson.optInt("value"))
            "long" -> editor.putLong(key, valueJson.optLong("value"))
            "float" -> editor.putFloat(key, valueJson.optDouble("value").toFloat())
            "boolean" -> editor.putBoolean(key, valueJson.optBoolean("value"))
            "string_set" -> {
                val array = valueJson.optJSONArray("value") ?: JSONArray()
                val values = (0 until array.length())
                    .mapNotNull { index -> array.optString(index).takeIf { it.isNotBlank() } }
                    .toSet()
                editor.putStringSet(key, values)
            }
        }
    }
    editor.apply()
}

private const val SimulationPreferenceName = "long_run_portfolio"
private const val LastBacktestPresetKey = "last_backtest_preset"
private const val LastDividendPresetKey = "last_dividend_preset"
private const val LastDividendChartUpdateSnapshotKey = "last_dividend_chart_update_snapshot"
private const val LastSelfDividendPresetKey = "last_self_dividend_preset"
private const val LastFourAssetDistributionPresetKey = "last_four_asset_distribution_preset"
private const val RetirementSuccessSnapshotKey = "retirement_success_snapshot"
private const val ScenarioComparisonSelectionKey = "scenario_comparison_selection"
private const val ScenarioComparisonMetricKey = "scenario_comparison_metric"
private const val RetirementSuccessSelectionKey = "retirement_success_selection"

private fun loadScenarioComparisonSelection(context: Context): Set<String> =
    context.getSharedPreferences(SimulationPreferenceName, Context.MODE_PRIVATE)
        .getStringSet(ScenarioComparisonSelectionKey, emptySet())
        ?.toSet()
        .orEmpty()

private fun saveScenarioComparisonSelection(context: Context, ids: Set<String>) {
    context.getSharedPreferences(SimulationPreferenceName, Context.MODE_PRIVATE)
        .edit()
        .putStringSet(ScenarioComparisonSelectionKey, ids)
        .apply()
}

private fun loadRetirementSuccessSelection(context: Context): Set<String> =
    context.getSharedPreferences(SimulationPreferenceName, Context.MODE_PRIVATE)
        .getStringSet(RetirementSuccessSelectionKey, emptySet())
        ?.toSet()
        .orEmpty()

private fun saveRetirementSuccessSelection(context: Context, ids: Set<String>) {
    context.getSharedPreferences(SimulationPreferenceName, Context.MODE_PRIVATE)
        .edit()
        .putStringSet(RetirementSuccessSelectionKey, ids)
        .apply()
}

private fun loadScenarioComparisonMetric(context: Context): Int =
    context.getSharedPreferences(SimulationPreferenceName, Context.MODE_PRIVATE)
        .getInt(ScenarioComparisonMetricKey, 0)
        .coerceIn(0, 1)

private fun saveScenarioComparisonMetric(context: Context, index: Int) {
    context.getSharedPreferences(SimulationPreferenceName, Context.MODE_PRIVATE)
        .edit()
        .putInt(ScenarioComparisonMetricKey, index.coerceIn(0, 1))
        .apply()
}

private fun loadLastSimulationJson(context: Context, key: String): JSONObject? =
    runCatching {
        context.getSharedPreferences(SimulationPreferenceName, Context.MODE_PRIVATE)
            .getString(key, null)
            ?.let(::JSONObject)
    }.getOrNull()

private fun saveLastSimulationJson(context: Context, key: String, json: JSONObject) {
    context.getSharedPreferences(SimulationPreferenceName, Context.MODE_PRIVATE)
        .edit()
        .putString(key, json.toString())
        .apply()
}

private fun loadLastBacktestPreset(context: Context): BacktestPreset? =
    loadLastSimulationJson(context, LastBacktestPresetKey)?.let(::backtestPresetFromJson)

private fun saveLastBacktestPreset(context: Context, preset: BacktestPreset) {
    saveLastSimulationJson(context, LastBacktestPresetKey, backtestPresetToJson(preset))
}

private fun loadLastDividendPreset(context: Context): DividendSimulationPreset? =
    loadLastSimulationJson(context, LastDividendPresetKey)?.let(::dividendPresetFromJson)

private fun saveLastDividendPreset(context: Context, preset: DividendSimulationPreset) {
    saveLastSimulationJson(context, LastDividendPresetKey, dividendPresetToJson(preset))
}

private fun loadLastDividendChartUpdateSnapshot(context: Context): DividendChartUpdateSnapshot? =
    loadLastSimulationJson(context, LastDividendChartUpdateSnapshotKey)?.let(::dividendChartUpdateSnapshotFromJson)

private fun saveLastDividendChartUpdateSnapshot(context: Context, snapshot: DividendChartUpdateSnapshot) {
    saveLastSimulationJson(
        context,
        LastDividendChartUpdateSnapshotKey,
        dividendChartUpdateSnapshotToJson(snapshot)
    )
}

private fun dividendChartUpdateSnapshotToJson(snapshot: DividendChartUpdateSnapshot): JSONObject = JSONObject().apply {
    put("inputKey", snapshot.inputKey)
    put("ticker", snapshot.ticker)
    put("targetMonthlyDividend", snapshot.targetMonthlyDividend)
    put("dividendGrowthMetric", dividendMetricToJson(snapshot.dividendGrowthMetric))
    put("priceGrowthMetric", dividendMetricToJson(snapshot.priceGrowthMetric))
    put("pricePoints", dividendChartPointsToJson(snapshot.pricePoints))
    put("growthPoints", dividendChartPointsToJson(snapshot.growthPoints))
    put("projectionRows", dividendProjectionRowsToJson(snapshot.projectionRows))
}

private fun dividendChartUpdateSnapshotFromJson(item: JSONObject): DividendChartUpdateSnapshot =
    DividendChartUpdateSnapshot(
        inputKey = item.optString("inputKey", ""),
        ticker = item.optString("ticker", "SCHD"),
        targetMonthlyDividend = item.optLong("targetMonthlyDividend", 0L),
        dividendGrowthMetric = dividendMetricFromJson(item.optJSONObject("dividendGrowthMetric")),
        priceGrowthMetric = dividendMetricFromJson(item.optJSONObject("priceGrowthMetric")),
        pricePoints = dividendChartPointsFromJson(item.optJSONArray("pricePoints")),
        growthPoints = dividendChartPointsFromJson(item.optJSONArray("growthPoints")),
        projectionRows = dividendProjectionRowsFromJson(item.optJSONArray("projectionRows"))
    )

private fun dividendProjectionRowsToJson(rows: List<DividendGrowthProjectionRow>): JSONArray = JSONArray().apply {
    rows.forEach { row ->
        put(JSONObject().apply {
            put("year", row.year)
            put("monthlyDividend", row.monthlyDividend)
            put("totalAsset", row.totalAsset)
        })
    }
}

private fun dividendProjectionRowsFromJson(array: JSONArray?): List<DividendGrowthProjectionRow> {
    if (array == null) return emptyList()
    return (0 until array.length()).mapNotNull { index ->
        array.optJSONObject(index)?.let { row ->
            DividendGrowthProjectionRow(
                year = row.optInt("year"),
                monthlyDividend = row.optLong("monthlyDividend"),
                totalAsset = row.optLong("totalAsset")
            )
        }
    }
}

private fun dividendMetricToJson(metric: DividendMetricUi): JSONObject = JSONObject().apply {
    put("label", metric.label)
    put("value", metric.value)
    metric.numericValue?.let { put("numericValue", it) }
}

private fun dividendMetricFromJson(item: JSONObject?): DividendMetricUi = DividendMetricUi(
    label = item?.optString("label", "평균 성장률") ?: "평균 성장률",
    value = item?.optString("value", "-") ?: "-",
    numericValue = item?.takeIf { it.has("numericValue") }?.optDouble("numericValue")
)

private fun dividendChartPointsToJson(points: List<Pair<LocalDate, Double>>): JSONArray = JSONArray().apply {
    points.forEach { (date, value) ->
        put(JSONObject().put("date", date.toString()).put("value", value))
    }
}

private fun dividendChartPointsFromJson(array: JSONArray?): List<Pair<LocalDate, Double>> {
    if (array == null) return emptyList()
    return (0 until array.length()).mapNotNull { index ->
        array.optJSONObject(index)?.let { point ->
            runCatching {
                LocalDate.parse(point.optString("date")) to point.optDouble("value")
            }.getOrNull()
        }
    }
}

private fun loadLastSelfDividendPreset(context: Context): SelfDividendPreset? =
    loadLastSimulationJson(context, LastSelfDividendPresetKey)?.let(::selfDividendPresetFromJson)

private fun saveLastSelfDividendPreset(context: Context, preset: SelfDividendPreset) {
    saveLastSimulationJson(context, LastSelfDividendPresetKey, selfDividendPresetToJson(preset))
}

private fun loadLastFourAssetDistributionPreset(context: Context): FourAssetDistributionPreset? =
    loadLastSimulationJson(context, LastFourAssetDistributionPresetKey)?.let(::fourAssetDistributionPresetFromJson)

private fun saveLastFourAssetDistributionPreset(context: Context, preset: FourAssetDistributionPreset) {
    saveLastSimulationJson(context, LastFourAssetDistributionPresetKey, fourAssetDistributionPresetToJson(preset))
}

private fun loadRetirementSuccessSnapshot(context: Context): RetirementSuccessSnapshot? =
    loadLastSimulationJson(context, RetirementSuccessSnapshotKey)?.let(::retirementSuccessSnapshotFromJson)

private fun saveRetirementSuccessSnapshot(context: Context, snapshot: RetirementSuccessSnapshot) {
    saveLastSimulationJson(context, RetirementSuccessSnapshotKey, retirementSuccessSnapshotToJson(snapshot))
}

private fun retirementSuccessSnapshotToJson(snapshot: RetirementSuccessSnapshot): JSONObject = JSONObject().apply {
    put("initialAssetEok", snapshot.initialAssetEok)
    put("monthlySpendingMan", snapshot.monthlySpendingMan)
    put("expectedReturn", snapshot.expectedReturn)
    put("volatility", snapshot.volatility)
    put("inflation", snapshot.inflation)
    put("years", snapshot.years)
    put("simulations", snapshot.simulations)
    snapshot.result?.let { put("result", retirementSuccessResultToJson(it)) }
}

private fun retirementSuccessSnapshotFromJson(item: JSONObject): RetirementSuccessSnapshot =
    RetirementSuccessSnapshot(
        initialAssetEok = item.optString("initialAssetEok", "10"),
        monthlySpendingMan = item.optString("monthlySpendingMan", "400"),
        expectedReturn = item.optString("expectedReturn", "7"),
        volatility = item.optString("volatility", "15"),
        inflation = item.optString("inflation", "2.5"),
        years = item.optString("years", "30"),
        simulations = item.optInt("simulations", 5_000),
        result = item.optJSONObject("result")?.let(::retirementSuccessResultFromJson)
    )

private fun retirementSuccessResultToJson(result: RetirementSuccessResult): JSONObject = JSONObject().apply {
    put("successRatePercent", result.successRatePercent)
    put("successfulPaths", result.successfulPaths)
    put("simulations", result.simulations)
    put("medianFinalAssetWon", result.medianFinalAssetWon)
    put("lowerFinalAssetWon", result.lowerFinalAssetWon)
    put("upperFinalAssetWon", result.upperFinalAssetWon)
    result.medianDepletionYear?.let { put("medianDepletionYear", it) }
    put("rows", JSONArray().apply {
        result.rows.forEach { row ->
            put(JSONObject().apply {
                put("year", row.year)
                put("survivalRatePercent", row.survivalRatePercent)
                put("lowerAssetWon", row.lowerAssetWon)
                put("medianAssetWon", row.medianAssetWon)
                put("upperAssetWon", row.upperAssetWon)
            })
        }
    })
}

private fun retirementSuccessResultFromJson(item: JSONObject): RetirementSuccessResult {
    val rowsJson = item.optJSONArray("rows")
    val rows = if (rowsJson == null) emptyList() else (0 until rowsJson.length()).mapNotNull { index ->
        rowsJson.optJSONObject(index)?.let { row ->
            RetirementSuccessYear(
                year = row.optInt("year"),
                survivalRatePercent = row.optDouble("survivalRatePercent"),
                lowerAssetWon = row.optLong("lowerAssetWon"),
                medianAssetWon = row.optLong("medianAssetWon"),
                upperAssetWon = row.optLong("upperAssetWon")
            )
        }
    }
    return RetirementSuccessResult(
        successRatePercent = item.optDouble("successRatePercent"),
        successfulPaths = item.optInt("successfulPaths"),
        simulations = item.optInt("simulations", 5_000),
        medianFinalAssetWon = item.optLong("medianFinalAssetWon"),
        lowerFinalAssetWon = item.optLong("lowerFinalAssetWon"),
        upperFinalAssetWon = item.optLong("upperFinalAssetWon"),
        medianDepletionYear = item.takeIf { it.has("medianDepletionYear") }?.optInt("medianDepletionYear"),
        rows = rows
    )
}

private fun loadSavedBacktestPresets(context: Context): List<BacktestPreset> =
    runCatching {
        val raw = context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
            .getString("saved_backtest_presets", null)
            ?: return emptyList()
        val array = JSONArray(raw)
        (0 until array.length()).mapNotNull { index ->
            array.optJSONObject(index)?.let(::backtestPresetFromJson)
        }
    }.getOrElse { emptyList() }

private fun saveSavedBacktestPresets(context: Context, presets: List<BacktestPreset>) {
    val array = JSONArray()
    presets.forEach { array.put(backtestPresetToJson(it)) }
    context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
        .edit()
        .putString("saved_backtest_presets", array.toString())
        .apply()
}

private fun backtestPresetToJson(preset: BacktestPreset): JSONObject = JSONObject().apply {
    put("name", preset.name)
    put("startYear", preset.startYear)
    put("endYear", preset.endYear)
    put("startingMoney", preset.startingMoney)
    put("rebalanceEnabled", preset.rebalanceEnabled)
    put("rebalanceFrequency", preset.rebalanceFrequency)
    put("contributionEnabled", preset.contributionEnabled)
    put("contributionPeriod", preset.contributionPeriod)
    put("contributionAmount", preset.contributionAmount)
    put("dividendReinvest", preset.dividendReinvest)
    put("exchangeRateEnabled", preset.exchangeRateEnabled)
    put("assets", JSONArray().apply { preset.assets.forEach { put(backtestAssetToJson(it)) } })
    preset.result?.let { put("result", backtestResultToJson(it)) }
}

private fun backtestPresetFromJson(item: JSONObject): BacktestPreset = BacktestPreset(
    name = item.optString("name", "저장된 백테스트"),
    startYear = item.optString("startYear", "2010"),
    endYear = item.optString("endYear", LocalDate.now().year.toString()),
    startingMoney = item.optString("startingMoney", "10000000"),
    rebalanceEnabled = item.optBoolean("rebalanceEnabled", true),
    rebalanceFrequency = item.optString("rebalanceFrequency", "매년"),
    contributionEnabled = item.optBoolean("contributionEnabled", false),
    contributionPeriod = item.optString("contributionPeriod", "월"),
    contributionAmount = item.optString("contributionAmount", "1000000"),
    dividendReinvest = item.optBoolean("dividendReinvest", true),
    exchangeRateEnabled = item.optBoolean("exchangeRateEnabled", true),
    assets = item.optJSONArray("assets")?.let { array ->
        (0 until array.length()).mapNotNull { array.optJSONObject(it)?.let(::backtestAssetFromJson) }
    } ?: emptyList(),
    result = item.optJSONObject("result")?.let(::backtestResultFromJson)
)

private fun backtestAssetToJson(asset: BacktestAssetUi): JSONObject = JSONObject().apply {
    put("ticker", asset.ticker)
    put("name", asset.name)
    put("weight", asset.weight)
    put("color", asset.color.value.toLong())
}

private fun backtestAssetFromJson(item: JSONObject): BacktestAssetUi = BacktestAssetUi(
    ticker = item.optString("ticker"),
    name = item.optString("name", item.optString("ticker")),
    weight = item.optDouble("weight", 0.0),
    color = Color(item.optLong("color", BrandGreen.value.toLong()).toULong())
)

private fun backtestResultToJson(result: BacktestResultUi): JSONObject = JSONObject().apply {
    put("monthlyAssets", JSONArray().apply { result.monthlyAssets.forEach { put(it) } })
    put("monthlyDrawdowns", JSONArray().apply { result.monthlyDrawdowns.forEach { put(it) } })
    put("annualReturns", JSONArray().apply {
        result.annualReturns.forEach { put(JSONObject().put("year", it.first).put("return", it.second)) }
    })
    put("rows", JSONArray().apply { result.rows.forEach { put(backtestReportRowToJson(it)) } })
    put("initialAmount", result.initialAmount)
    put("investedAmount", result.investedAmount)
    put("monthLabels", JSONArray().apply { result.monthLabels.forEach { put(it) } })
    put("monthlyReturns", JSONArray().apply { result.monthlyReturns.forEach { put(it) } })
    put("monthlyAllocations", JSONArray().apply {
        result.monthlyAllocations.forEach { allocations ->
            put(JSONArray().apply { allocations.forEach { put(backtestAllocationToJson(it)) } })
        }
    })
    put("usedHistoricalData", result.usedHistoricalData)
}

private fun backtestResultFromJson(item: JSONObject): BacktestResultUi = BacktestResultUi(
    monthlyAssets = item.optJSONArray("monthlyAssets")?.let { array ->
        (0 until array.length()).map { array.optLong(it) }
    } ?: emptyList(),
    monthlyDrawdowns = item.optJSONArray("monthlyDrawdowns")?.let { array ->
        (0 until array.length()).map { array.optDouble(it) }
    } ?: emptyList(),
    annualReturns = item.optJSONArray("annualReturns")?.let { array ->
        (0 until array.length()).mapNotNull { index ->
            array.optJSONObject(index)?.let { it.optInt("year") to it.optDouble("return") }
        }
    } ?: emptyList(),
    rows = item.optJSONArray("rows")?.let { array ->
        (0 until array.length()).mapNotNull { array.optJSONObject(it)?.let(::backtestReportRowFromJson) }
    } ?: emptyList(),
    initialAmount = item.optLong("initialAmount", 0L),
    investedAmount = item.optLong("investedAmount", 0L),
    monthLabels = item.optJSONArray("monthLabels")?.let { array ->
        (0 until array.length()).map { array.optString(it) }
    } ?: emptyList(),
    monthlyReturns = item.optJSONArray("monthlyReturns")?.let { array ->
        (0 until array.length()).map { array.optDouble(it) }
    } ?: emptyList(),
    monthlyAllocations = item.optJSONArray("monthlyAllocations")?.let { outer ->
        (0 until outer.length()).map { index ->
            val allocations = outer.optJSONArray(index) ?: JSONArray()
            (0 until allocations.length()).mapNotNull { allocations.optJSONObject(it)?.let(::backtestAllocationFromJson) }
        }
    } ?: emptyList(),
    usedHistoricalData = item.optBoolean("usedHistoricalData", false)
)

private fun backtestReportRowToJson(row: BacktestReportRow): JSONObject = JSONObject().apply {
    put("year", row.year)
    put("finalReturn", row.finalReturn)
    put("profit", row.profit)
    put("maxDrawdown", row.maxDrawdown)
    put("finalAsset", row.finalAsset)
}

private fun backtestReportRowFromJson(item: JSONObject): BacktestReportRow = BacktestReportRow(
    year = item.optInt("year"),
    finalReturn = item.optDouble("finalReturn"),
    profit = item.optLong("profit"),
    maxDrawdown = item.optDouble("maxDrawdown"),
    finalAsset = item.optLong("finalAsset")
)

private fun backtestAllocationToJson(allocation: BacktestAllocationPoint): JSONObject = JSONObject().apply {
    put("ticker", allocation.ticker)
    put("name", allocation.name)
    put("weight", allocation.weight)
    put("amount", allocation.amount)
    put("color", allocation.color.value.toLong())
}

private fun backtestAllocationFromJson(item: JSONObject): BacktestAllocationPoint = BacktestAllocationPoint(
    ticker = item.optString("ticker"),
    name = item.optString("name", item.optString("ticker")),
    weight = item.optDouble("weight", 0.0),
    amount = item.optLong("amount", 0L),
    color = Color(item.optLong("color", BrandGreen.value.toLong()).toULong())
)

private fun loadSavedSelfDividendPresets(context: Context): List<SelfDividendPreset> =
    runCatching {
        val raw = context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
            .getString("saved_self_dividend_presets", null)
            ?: return emptyList()
        val array = JSONArray(raw)
        (0 until array.length()).mapNotNull { index ->
            array.optJSONObject(index)?.let(::selfDividendPresetFromJson)
        }
    }.getOrElse { emptyList() }

private fun saveSavedSelfDividendPresets(context: Context, presets: List<SelfDividendPreset>) {
    val array = JSONArray()
    presets.forEach { array.put(selfDividendPresetToJson(it)) }
    context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
        .edit()
        .putString("saved_self_dividend_presets", array.toString())
        .apply()
}

private fun selfDividendPresetToJson(preset: SelfDividendPreset): JSONObject = JSONObject().apply {
    put("name", preset.name)
    put("assets", JSONArray().apply { preset.assets.forEach { put(selfDividendAssetToJson(it)) } })
    put("result", JSONArray().apply { preset.result.forEach { put(selfDividendProjectionRowToJson(it)) } })
}

private fun selfDividendPresetFromJson(item: JSONObject): SelfDividendPreset = SelfDividendPreset(
    name = item.optString("name", "자가배당"),
    assets = item.optJSONArray("assets")?.let { array ->
        (0 until array.length()).mapNotNull { array.optJSONObject(it)?.let(::selfDividendAssetFromJson) }
    } ?: emptyList(),
    result = item.optJSONArray("result")?.let { array ->
        (0 until array.length()).mapNotNull { array.optJSONObject(it)?.let(::selfDividendProjectionRowFromJson) }
    } ?: emptyList()
)

private fun selfDividendAssetToJson(asset: SelfDividendAssetUi): JSONObject = JSONObject().apply {
    put("ticker", asset.ticker)
    put("name", asset.name)
    put("color", asset.color.value.toLong())
    put("investmentAmount", asset.investmentAmount)
    put("annualWithdrawal", asset.annualWithdrawal)
    put("withdrawalGrowthRate", asset.withdrawalGrowthRate)
    put("taxMode", asset.taxMode)
}

private fun selfDividendAssetFromJson(item: JSONObject): SelfDividendAssetUi = SelfDividendAssetUi(
    ticker = item.optString("ticker"),
    name = item.optString("name", item.optString("ticker")),
    color = Color(item.optLong("color", BrandGreen.value.toLong()).toULong()),
    investmentAmount = item.optString("investmentAmount", ""),
    annualWithdrawal = item.optString("annualWithdrawal", ""),
    withdrawalGrowthRate = item.optString("withdrawalGrowthRate", "3"),
    taxMode = item.optString("taxMode", "해외직투 양도세")
)

private fun selfDividendProjectionRowToJson(row: SelfDividendProjectionRow): JSONObject = JSONObject().apply {
    put("year", row.year)
    put("monthlyTakeHome", row.monthlyTakeHome)
    put("annualTakeHome", row.annualTakeHome)
    put("assetBeforeWithdrawal", row.assetBeforeWithdrawal)
    put("grossSale", row.grossSale)
    put("realizedGain", row.realizedGain)
    put("capitalGainsTax", row.capitalGainsTax)
    put("totalAsset", row.totalAsset)
    put("remainingCostBasis", row.remainingCostBasis)
    put("note", row.note)
}

private fun selfDividendProjectionRowFromJson(item: JSONObject): SelfDividendProjectionRow = SelfDividendProjectionRow(
    year = item.optInt("year"),
    monthlyTakeHome = item.optLong("monthlyTakeHome"),
    annualTakeHome = item.optLong("annualTakeHome"),
    assetBeforeWithdrawal = item.optLong("assetBeforeWithdrawal"),
    grossSale = item.optLong("grossSale"),
    realizedGain = item.optLong("realizedGain"),
    capitalGainsTax = item.optLong("capitalGainsTax"),
    totalAsset = item.optLong("totalAsset"),
    remainingCostBasis = item.optLong("remainingCostBasis"),
    note = item.optString("note")
)

private fun loadSavedFourAssetDistributionPresets(context: Context): List<FourAssetDistributionPreset> =
    runCatching {
        val raw = context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
            .getString("saved_four_asset_distribution_presets", null)
            ?: return emptyList()
        val array = JSONArray(raw)
        (0 until array.length()).mapNotNull { index ->
            array.optJSONObject(index)?.let(::fourAssetDistributionPresetFromJson)
        }
    }.getOrElse { emptyList() }

private fun saveSavedFourAssetDistributionPresets(context: Context, presets: List<FourAssetDistributionPreset>) {
    val array = JSONArray()
    presets.forEach { array.put(fourAssetDistributionPresetToJson(it)) }
    context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
        .edit()
        .putString("saved_four_asset_distribution_presets", array.toString())
        .apply()
}

private fun fourAssetDistributionPresetToJson(preset: FourAssetDistributionPreset): JSONObject = JSONObject().apply {
    put("name", preset.name)
    put("totalCapitalEok", preset.totalCapitalEok)
    put("monthlyExpenseMan", preset.monthlyExpenseMan)
    put("schdRatio", preset.schdRatio)
    put("jepqRatio", preset.jepqRatio)
    put("qldRatio", preset.qldRatio)
    put("cashRatio", preset.cashRatio)
    put("appliedSchdRatio", preset.appliedSchdRatio)
    put("appliedJepqRatio", preset.appliedJepqRatio)
    put("appliedQldRatio", preset.appliedQldRatio)
    put("appliedCashRatio", preset.appliedCashRatio)
    put("exchangeRate", preset.exchangeRate)
    put("schdPrice", preset.schdPrice)
    put("jepqPrice", preset.jepqPrice)
    put("qldPrice", preset.qldPrice)
    put("schdYield", preset.schdYield)
    put("schdDividendGrowth", preset.schdDividendGrowth)
    put("schdPriceGrowth", preset.schdPriceGrowth)
    put("jepqYield", preset.jepqYield)
    put("jepqDividendGrowth", preset.jepqDividendGrowth)
    put("jepqPriceGrowth", preset.jepqPriceGrowth)
    put("qldPriceGrowth", preset.qldPriceGrowth)
    put("cashYield", preset.cashYield)
    put("inflationRate", preset.inflationRate)
    put("taxAndInsuranceRate", preset.taxAndInsuranceRate)
    put("stressTestEnabled", preset.stressTestEnabled)
}

private fun fourAssetDistributionPresetFromJson(item: JSONObject): FourAssetDistributionPreset {
    val schdRatio = item.optString("schdRatio", "73.9")
    val jepqRatio = item.optString("jepqRatio", "17.4")
    val qldRatio = item.optString("qldRatio", "8.7")
    val cashRatio = "0"
    return FourAssetDistributionPreset(
        name = item.optString("name", "3자산 분배"),
        totalCapitalEok = item.optString("totalCapitalEok", "13"),
        monthlyExpenseMan = item.optString("monthlyExpenseMan", "400"),
        schdRatio = schdRatio,
        jepqRatio = jepqRatio,
        qldRatio = qldRatio,
        cashRatio = cashRatio,
        appliedSchdRatio = item.optDouble("appliedSchdRatio", schdRatio.toDoubleOrNull() ?: 73.9),
        appliedJepqRatio = item.optDouble("appliedJepqRatio", jepqRatio.toDoubleOrNull() ?: 17.4),
        appliedQldRatio = item.optDouble("appliedQldRatio", qldRatio.toDoubleOrNull() ?: 8.7),
        appliedCashRatio = 0.0,
        exchangeRate = item.optString("exchangeRate", "1600"),
        schdPrice = item.optString("schdPrice", "80.0"),
        jepqPrice = item.optString("jepqPrice", "50.0"),
        qldPrice = item.optString("qldPrice", "90.0"),
        schdYield = item.optString("schdYield", "3.0"),
        schdDividendGrowth = item.optString("schdDividendGrowth", "6.0"),
        schdPriceGrowth = item.optString("schdPriceGrowth", "5.0"),
        jepqYield = item.optString("jepqYield", "8.0"),
        jepqDividendGrowth = item.optString("jepqDividendGrowth", "2.0"),
        jepqPriceGrowth = item.optString("jepqPriceGrowth", "2.0"),
        qldPriceGrowth = item.optString("qldPriceGrowth", "15.0"),
        cashYield = item.optString("cashYield", "3.0"),
        inflationRate = item.optString("inflationRate", "3.0"),
        taxAndInsuranceRate = item.optString("taxAndInsuranceRate", "23.4"),
        stressTestEnabled = item.optBoolean("stressTestEnabled", false)
    )
}

private fun loadSavedDividendPresets(context: Context): List<DividendSimulationPreset> =
    runCatching {
        val raw = context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
            .getString("saved_dividend_presets", null)
            ?: return emptyList()
        val array = JSONArray(raw)
        (0 until array.length()).mapNotNull { index ->
            array.optJSONObject(index)?.let(::dividendPresetFromJson)
        }
    }.getOrElse { emptyList() }

private fun saveSavedDividendPresets(context: Context, presets: List<DividendSimulationPreset>) {
    val array = JSONArray()
    presets.forEach { array.put(dividendPresetToJson(it)) }
    context.getSharedPreferences("long_run_portfolio", Context.MODE_PRIVATE)
        .edit()
        .putString("saved_dividend_presets", array.toString())
        .apply()
}

private fun dividendPresetToJson(preset: DividendSimulationPreset): JSONObject = JSONObject().apply {
    put("name", preset.name)
    put("ticker", preset.ticker)
    put("modeIndex", preset.modeIndex)
    put("targetInput", preset.targetInput)
    put("showAfterTax", preset.showAfterTax)
    put("selectedPersonId", preset.selectedPersonId)
    put("people", JSONArray().apply { preset.people.forEach { put(dividendPersonToJson(it)) } })
    put("projectionRows", dividendProjectionRowsToJson(preset.projectionRows))
}

private fun dividendPresetFromJson(item: JSONObject): DividendSimulationPreset {
    val people = item.optJSONArray("people")?.let { array ->
        (0 until array.length()).mapNotNull { array.optJSONObject(it)?.let(::dividendPersonFromJson) }
    }?.takeIf { it.isNotEmpty() } ?: listOf(DividendPersonUi(1L, "본인"))
    return DividendSimulationPreset(
        name = item.optString("name", "배당 시뮬레이션"),
        ticker = item.optString("ticker", "SCHD"),
        modeIndex = item.optInt("modeIndex", 0),
        targetInput = item.optString("targetInput", "3000000"),
        showAfterTax = item.optBoolean("showAfterTax", true),
        selectedPersonId = item.optLong("selectedPersonId", people.first().id),
        people = people,
        projectionRows = dividendProjectionRowsFromJson(item.optJSONArray("projectionRows"))
    )
}

private fun dividendPersonToJson(person: DividendPersonUi): JSONObject = JSONObject().apply {
    put("id", person.id)
    put("name", person.name)
    put("incomeTaxRate", person.incomeTaxRate)
    put("healthInsuranceRate", 0.0)
    put("annualPensionIncome", person.annualPensionIncome)
}

private fun dividendPersonFromJson(item: JSONObject): DividendPersonUi = DividendPersonUi(
    id = item.optLong("id", System.currentTimeMillis()),
    name = item.optString("name", "인원"),
    incomeTaxRate = item.optDouble("incomeTaxRate", 0.0),
    healthInsuranceRate = 0.0,
    annualPensionIncome = item.optLong("annualPensionIncome", 0L)
)

private fun JSONObject.optLongOrNull(name: String): Long? =
    if (has(name) && !isNull(name)) optLong(name) else null

private fun inferPurchaseExchangeRate(holding: JSONObject, fallback: Double): Double {
    if (holding.has("averageExchangeRate")) return holding.optDouble("averageExchangeRate", fallback)
    val trades = holding.optJSONArray("trades") ?: return fallback
    var dollarCost = 0.0
    var wonCost = 0.0
    for (index in 0 until trades.length()) {
        val trade = trades.optJSONObject(index) ?: continue
        if (trade.optString("side", TradeSide.BUY) != TradeSide.BUY) continue
        val quantity = trade.optDouble("quantity", 0.0)
        val price = trade.optDouble("price", 0.0)
        val exchangeRate = trade.optDouble("exchangeRate", inferTradeExchangeRate(trade.optString("date"), fallback))
        val tradeDollarCost = quantity * price
        dollarCost += tradeDollarCost
        wonCost += tradeDollarCost * exchangeRate
    }
    return if (dollarCost > 0.0) wonCost / dollarCost else fallback
}

private fun inferTradeExchangeRate(date: String, fallback: Double): Double =
    when (date.filter { it.isDigit() }) {
        "202658" -> 1471.0
        "20260508" -> 1471.0
        else -> fallback
    }

private fun isKoreanTicker(ticker: String): Boolean =
    ticker.length == 6 && ticker.all { it.isDigit() }

private fun holdingExchangeRate(ticker: String, exchangeRate: Double): Double =
    if (isKoreanTicker(ticker)) 1.0 else exchangeRate

private fun assetDisplayName(holding: HoldingUi): String =
    if (isKoreanTicker(holding.ticker) && holding.name.isNotBlank()) holding.name else holding.ticker

private fun formatAssetPrice(value: Double, ticker: String): String =
    if (isKoreanTicker(ticker)) formatWon(value.roundToLong()) else "$${formatDecimal(value)}"

private fun applyCurrencyDisplay(currency: String, usdKrw: Double) {
    DisplayCurrency = currency
    DisplayUsdKrw = usdKrw.coerceAtLeast(1.0)
}

private fun formatWon(value: Long): String =
    CurrencyDisplayFormatter.format(value, DisplayUsdKrw, DisplayCurrency == CurrencyMode.USD)

private fun formatSignedWon(value: Long): String =
    CurrencyDisplayFormatter.formatSigned(value, DisplayUsdKrw, DisplayCurrency == CurrencyMode.USD)

private fun formatDecimal(value: Double): String =
    NumberFormat.getNumberInstance(Locale.US).apply { maximumFractionDigits = 2 }.format(value)

private fun formatPercent(value: Double): String =
    "${if (value >= 0) "+" else ""}${String.format(Locale.US, "%.2f", value * 100)}%"

private fun formatUnsignedPercent(value: Double): String =
    "${String.format(Locale.US, "%.2f", kotlin.math.abs(value) * 100)}%"

private fun formatQuantity(value: Double): String =
    if (value % 1.0 == 0.0) value.toLong().toString() else formatDecimal(value)

@Preview(showBackground = true)
@Composable
private fun LongRunAppPreview() {
    LongRunPortfolioTheme {
        LongRunApp()
    }
}
