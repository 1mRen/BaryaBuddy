package com.baryabuddy.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.baryabuddy.app.data.database.AppDatabase
import com.baryabuddy.app.data.database.DatabaseModule
import com.baryabuddy.app.data.repository.BaryaBuddyRepository
import com.baryabuddy.app.domain.usecase.CalculateDailySafeSpend
import com.baryabuddy.app.presentation.addexpense.AddExpenseViewModel
import com.baryabuddy.app.presentation.home.HomeViewModel
import com.baryabuddy.app.presentation.navigation.NavGraph
import com.baryabuddy.app.presentation.navigation.Screen
import com.baryabuddy.app.presentation.onboarding.OnboardingViewModel
import com.baryabuddy.app.ui.theme.BaryaBuddyTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var database: AppDatabase
    private lateinit var repository: BaryaBuddyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize database
        database = AppDatabase.getDatabase(applicationContext)
        DatabaseModule.initializeDatabase(applicationContext)
        repository = BaryaBuddyRepository(database)

        enableEdgeToEdge()

        setContent {
            BaryaBuddyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppContent(repository)
                }
            }
        }
    }
}

@Composable
fun AppContent(repository: BaryaBuddyRepository) {
    val navController = rememberNavController()
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val profile = repository.getUserProfileOnce()
        startDestination = if (profile?.setupCompleted == true) {
            Screen.Home.route
        } else {
            Screen.Onboarding.route
        }
    }

    startDestination?.let { destination ->
        NavGraph(
            navController = navController,
            startDestination = destination,
            repository = repository
        )
    }
}

