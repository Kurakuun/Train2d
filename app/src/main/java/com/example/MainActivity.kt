package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.audio.TrainSoundManager
import com.example.data.model.ContractJob
import com.example.data.model.GameContent
import com.example.data.model.TrainModel
import com.example.data.storage.GamePreferences
import com.example.ui.codes.PromoCodesDialog
import com.example.ui.game.TrainGameScreen
import com.example.ui.intro.IntroScreen
import com.example.ui.jobs.JobsDialog
import com.example.ui.loading.LoadingScreen
import com.example.ui.maintenance.MaintenanceScreen
import com.example.ui.menu.TitleMenuScreen
import com.example.ui.shop.ShopDialog
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.workshop.PaintShopScreen
import com.example.ui.workshop.WorkshopScreen

enum class AppScreen {
    INTRO,
    TITLE,
    LOADING,
    GAME,
    WORKSHOP,
    MAINTENANCE,
    PAINT_SHOP
}

class MainActivity : ComponentActivity() {

    private lateinit var gamePrefs: GamePreferences
    private lateinit var soundManager: TrainSoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        hideSystemBars()

        gamePrefs = GamePreferences(applicationContext)
        soundManager = TrainSoundManager()

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TrainAppRoot(gamePrefs = gamePrefs, soundManager = soundManager)
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemBars()
        }
    }

    private fun hideSystemBars() {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}

@Composable
fun TrainAppRoot(
    gamePrefs: GamePreferences,
    soundManager: TrainSoundManager
) {
    val gameState by gamePrefs.gameState.collectAsStateWithLifecycle()

    var currentScreen by remember { mutableStateOf(AppScreen.INTRO) }
    var selectedContract by remember { mutableStateOf<ContractJob?>(null) }

    // Modals
    var showCodesDialog by remember { mutableStateOf(false) }
    var showJobsDialog by remember { mutableStateOf(false) }
    var showShopDialog by remember { mutableStateOf(false) }
    var trainToPaint by remember { mutableStateOf<TrainModel?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        when (currentScreen) {
            AppScreen.INTRO -> {
                IntroScreen(
                    onIntroFinished = {
                        currentScreen = AppScreen.TITLE
                    }
                )
            }
            AppScreen.TITLE -> {
                TitleMenuScreen(
                    gameState = gameState,
                    gamePrefs = gamePrefs,
                    soundManager = soundManager,
                    onStartDrive = {
                        currentScreen = AppScreen.LOADING
                    },
                    onOpenWorkshop = {
                        currentScreen = AppScreen.WORKSHOP
                    },
                    onOpenJobs = {
                        showJobsDialog = true
                    },
                    onOpenCodes = {
                        showCodesDialog = true
                    },
                    onOpenShop = {
                        showShopDialog = true
                    },
                    onOpenMaintenance = {
                        currentScreen = AppScreen.MAINTENANCE
                    }
                )
            }
            AppScreen.LOADING -> {
                LoadingScreen(
                    gameState = gameState,
                    onLoadingComplete = {
                        currentScreen = AppScreen.GAME
                    }
                )
            }
            AppScreen.GAME -> {
                TrainGameScreen(
                    gameState = gameState,
                    gamePrefs = gamePrefs,
                    soundManager = soundManager,
                    selectedContract = selectedContract,
                    onBackToWorkshop = {
                        currentScreen = AppScreen.WORKSHOP
                    },
                    onBackToMenu = {
                        currentScreen = AppScreen.TITLE
                    },
                    onOpenMaintenance = {
                        currentScreen = AppScreen.MAINTENANCE
                    }
                )
            }
            AppScreen.WORKSHOP -> {
                WorkshopScreen(
                    gameState = gameState,
                    gamePrefs = gamePrefs,
                    soundManager = soundManager,
                    onBackToMenu = {
                        currentScreen = AppScreen.TITLE
                    },
                    onOpenMap = {
                        showJobsDialog = true
                    },
                    onOpenShop = {
                        showShopDialog = true
                    },
                    onOpenPaintShop = { train ->
                        trainToPaint = train
                        currentScreen = AppScreen.PAINT_SHOP
                    },
                    onOpenMaintenance = {
                        currentScreen = AppScreen.MAINTENANCE
                    }
                )
            }
            AppScreen.MAINTENANCE -> {
                MaintenanceScreen(
                    gameState = gameState,
                    gamePrefs = gamePrefs,
                    soundManager = soundManager,
                    onBackToMenu = {
                        currentScreen = AppScreen.TITLE
                    },
                    onBackToWorkshop = {
                        currentScreen = AppScreen.WORKSHOP
                    },
                    onStartDrive = {
                        currentScreen = AppScreen.LOADING
                    }
                )
            }
            AppScreen.PAINT_SHOP -> {
                val train = trainToPaint
                    ?: GameContent.ALL_TRAINS.find { it.id == gameState.selectedTrainId }
                    ?: GameContent.ALL_TRAINS.first()
                PaintShopScreen(
                    train = train,
                    gameState = gameState,
                    gamePrefs = gamePrefs,
                    soundManager = soundManager,
                    onBackToWorkshop = {
                        currentScreen = AppScreen.WORKSHOP
                    }
                )
            }
        }

        // Dialogs & Modals Overlays
        if (showCodesDialog) {
            PromoCodesDialog(
                gameState = gameState,
                gamePrefs = gamePrefs,
                soundManager = soundManager,
                onDismiss = { showCodesDialog = false }
            )
        }

        if (showJobsDialog) {
            JobsDialog(
                gameState = gameState,
                onSelectJob = { job ->
                    selectedContract = job
                    showJobsDialog = false
                    currentScreen = AppScreen.LOADING
                },
                onDismiss = { showJobsDialog = false }
            )
        }

        if (showShopDialog) {
            ShopDialog(
                gameState = gameState,
                gamePrefs = gamePrefs,
                soundManager = soundManager,
                onDismiss = { showShopDialog = false }
            )
        }
    }
}
