package org.qxteam.madartask

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import org.koin.androidx.compose.koinViewModel
import org.qxteam.madartask.core.navigation.Screen
import org.qxteam.madartask.feature.display.DisplayScreen
import org.qxteam.madartask.feature.display.DisplayViewModel
import org.qxteam.madartask.feature.input.InputScreen
import org.qxteam.madartask.feature.input.InputViewModel
import org.qxteam.madartask.ui.theme.MadarTaskTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MadarTaskTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        navController = navController,
                        startDestination = Screen.Input.route,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        composable(Screen.Input.route) {
                            val viewModel: InputViewModel = koinViewModel()
                            InputScreen(
                                viewModel = viewModel,
                                onNavigateToDisplay = {
                                    navController.navigate(Screen.Display.route)
                                }
                            )
                        }
                        composable(Screen.Display.route) {
                            val viewModel: DisplayViewModel = koinViewModel()
                            DisplayScreen(
                                viewModel = viewModel,
                                onNavigateBack = {
                                    navController.popBackStack()
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}