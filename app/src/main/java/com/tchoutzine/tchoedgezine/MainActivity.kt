package com.tchoutzine.tchoedgezine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.*
import androidx.navigation.compose.rememberNavController
import com.tchoutzine.tchoedgezine.navigation.TchoNavGraph
import com.tchoutzine.tchoedgezine.ui.theme.TchoEdgeZineTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TchoEdgeZineTheme {
                val navController = rememberNavController()
                TchoNavGraph(navController = navController)
            }
        }
    }
}
