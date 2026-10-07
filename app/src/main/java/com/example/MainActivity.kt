package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.components.AppTopBar
import com.example.ui.components.BottomNavBar
import com.example.ui.components.NavScreen
import com.example.ui.screens.CreatorModeScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.VoiceViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                KaranVoiceAiApp()
            }
        }
    }
}

@Composable
fun KaranVoiceAiApp(
    viewModel: VoiceViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf(NavScreen.HOME) }
    val uiState by viewModel.uiState.collectAsState()

    // Handle back button to return to HOME screen when on other screens
    if (currentScreen != NavScreen.HOME) {
        BackHandler {
            currentScreen = NavScreen.HOME
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(CyberBackground),
        topBar = {
            AppTopBar(
                isCreatorMode = currentScreen == NavScreen.CREATOR || uiState.isCreatorMode
            )
        },
        bottomBar = {
            BottomNavBar(
                currentScreen = currentScreen,
                onNavigate = { currentScreen = it }
            )
        },
        containerColor = CyberBackground
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Crossfade(
                targetState = currentScreen,
                label = "screen_crossfade"
            ) { screen ->
                when (screen) {
                    NavScreen.HOME, NavScreen.GENERATE -> {
                        HomeScreen(
                            viewModel = viewModel,
                            onNavigateToCreatorStudio = { currentScreen = NavScreen.CREATOR }
                        )
                    }
                    NavScreen.CREATOR -> {
                        CreatorModeScreen(
                            viewModel = viewModel
                        )
                    }
                    NavScreen.HISTORY -> {
                        HistoryScreen(
                            viewModel = viewModel,
                            onNavigateToHome = { currentScreen = NavScreen.HOME }
                        )
                    }
                    NavScreen.SETTINGS -> {
                        SettingsScreen(
                            viewModel = viewModel
                        )
                    }
                }
            }
        }
    }
}
