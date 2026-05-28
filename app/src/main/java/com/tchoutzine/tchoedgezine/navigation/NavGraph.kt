package com.tchoutzine.tchoedgezine.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tchoutzine.tchoedgezine.ui.screens.*

@Composable
fun TchoNavGraph(navController: NavHostController, startDestination: String = Screen.Splash.route) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Screen.Splash.route) {
            SplashScreen(onReady = {
                navController.navigate(Screen.Onboarding.route) {
                    popUpTo(Screen.Splash.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Onboarding.route) {
            OnboardingScreen(onContinue = {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Onboarding.route) { inclusive = true }
                }
            })
        }

        composable(Screen.Home.route) {
            HomeScreen(
                onHealthClick   = { navController.navigate(Screen.HealthDx.route) },
                onCropClick     = { navController.navigate(Screen.CropDx.route) },
                onVoiceClick    = { navController.navigate(Screen.Voice.route) },
                onChatbotClick  = { navController.navigate(Screen.Chatbot.route) },
                onTelemedClick  = { navController.navigate(Screen.Telemed.route) },
                onHistoryClick  = { navController.navigate(Screen.History.route) },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
            )
        }

        composable(Screen.HealthDx.route) {
            HealthDiagnosisScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.CropDx.route) {
            CropDetectionScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.History.route) {
            HistoryScreen(
                onBack          = { navController.popBackStack() },
                onHomeClick     = { navController.navigate(Screen.Home.route) { launchSingleTop = true } },
                onSettingsClick = { navController.navigate(Screen.Settings.route) },
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onBack         = { navController.popBackStack() },
                onHomeClick    = { navController.navigate(Screen.Home.route) { launchSingleTop = true } },
                onHistoryClick = { navController.navigate(Screen.History.route) { launchSingleTop = true } },
            )
        }

        composable(Screen.Voice.route) {
            VoiceScreen(onClose = { navController.popBackStack() })
        }

        composable(Screen.Chatbot.route) {
            ChatbotScreen(onBack = { navController.popBackStack() })
        }

        composable(Screen.Telemed.route) {
            TelemedicineScreen(onBack = { navController.popBackStack() })
        }
    }
}
