package com.tchoutzine.tchoedgezine.navigation

sealed class Screen(val route: String) {
    object Splash      : Screen("splash")
    object Onboarding  : Screen("onboarding")
    object Home        : Screen("home")
    object HealthDx    : Screen("health_diagnosis")
    object CropDx      : Screen("crop_detection")
    object History     : Screen("history")
    object Settings    : Screen("settings")
    object Voice       : Screen("voice_interface")
    object Chatbot     : Screen("chatbot")
    object Telemed     : Screen("telemedicine")
}
