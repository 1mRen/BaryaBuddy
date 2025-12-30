package com.baryabuddy.app.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import kotlinx.coroutines.flow.first
import com.baryabuddy.app.data.repository.BaryaBuddyRepository
import com.baryabuddy.app.domain.usecase.CalculateDailySafeSpend
import com.baryabuddy.app.presentation.addincome.AddIncomeScreen
import com.baryabuddy.app.presentation.addincome.AddIncomeViewModel
import com.baryabuddy.app.presentation.addexpense.AddExpenseScreen
import com.baryabuddy.app.presentation.addexpense.AddExpenseViewModel
import com.baryabuddy.app.presentation.home.HomeScreen
import com.baryabuddy.app.presentation.home.HomeViewModel
import com.baryabuddy.app.presentation.onboarding.OnboardingScreen
import com.baryabuddy.app.presentation.onboarding.OnboardingViewModel
import com.baryabuddy.app.presentation.settings.SettingsScreen
import com.baryabuddy.app.presentation.settings.SettingsViewModel

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AddExpense : Screen("add_expense")
    object AddIncome : Screen("add_income")
    object EditExpense : Screen("edit_expense/{transactionId}") {
        fun createRoute(transactionId: Long) = "edit_expense/$transactionId"
    }
    object EditIncome : Screen("edit_income/{transactionId}") {
        fun createRoute(transactionId: Long) = "edit_income/$transactionId"
    }
    object Settings : Screen("settings")
    object Onboarding : Screen("onboarding")
}

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    repository: BaryaBuddyRepository
) {
    val calculateDailySafeSpend = CalculateDailySafeSpend()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Home.route) {
            val viewModel: HomeViewModel = viewModel(
                factory = HomeViewModelFactory(repository, calculateDailySafeSpend)
            )
            HomeScreen(
                viewModel = viewModel,
                onAddExpenseClick = {
                    navController.navigate(Screen.AddExpense.route)
                },
                onAddIncomeClick = {
                    navController.navigate(Screen.AddIncome.route)
                },
                onSettingsClick = {
                    navController.navigate(Screen.Settings.route)
                },
                onEditTransaction = { transactionId ->
                    // We'll determine the transaction type in the composable
                    // For now, navigate to a route that will check the type
                    navController.navigate("edit_transaction/$transactionId")
                }
            )
        }

        composable(Screen.AddExpense.route) {
            val viewModel: AddExpenseViewModel = viewModel(
                factory = AddExpenseViewModelFactory(repository)
            )
            AddExpenseScreen(
                viewModel = viewModel,
                onDismiss = {
                    navController.popBackStack()
                },
                onExpenseAdded = {
                    // Refresh home screen data
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.AddIncome.route) {
            val viewModel: AddIncomeViewModel = viewModel(
                factory = AddIncomeViewModelFactory(repository)
            )
            AddIncomeScreen(
                viewModel = viewModel,
                onDismiss = {
                    navController.popBackStack()
                },
                onIncomeAdded = {
                    // Refresh home screen data
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = "edit_transaction/{transactionId}",
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: return@composable
            
            // Load transaction and determine type
            LaunchedEffect(transactionId) {
                val transaction = repository.getTransactionById(transactionId)
                if (transaction != null) {
                    if (transaction.categoryId == null) {
                        // It's income - navigate to edit income
                        navController.navigate(Screen.EditIncome.createRoute(transactionId)) {
                            popUpTo("edit_transaction/{transactionId}") { inclusive = true }
                        }
                    } else {
                        // It's expense - navigate to edit expense
                        navController.navigate(Screen.EditExpense.createRoute(transactionId)) {
                            popUpTo("edit_transaction/{transactionId}") { inclusive = true }
                        }
                    }
                } else {
                    // Transaction not found, go back
                    navController.popBackStack()
                }
            }
        }

        composable(
            route = Screen.EditExpense.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: return@composable
            val viewModel: AddExpenseViewModel = viewModel(
                factory = AddExpenseViewModelFactory(repository)
            )
            
            // Load transaction data
            LaunchedEffect(transactionId) {
                val transaction = repository.getTransactionById(transactionId)
                if (transaction != null) {
                    viewModel.loadTransaction(transaction)
                }
            }
            
            AddExpenseScreen(
                viewModel = viewModel,
                onDismiss = {
                    navController.popBackStack()
                },
                onExpenseAdded = {
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Screen.EditIncome.route,
            arguments = listOf(navArgument("transactionId") { type = NavType.LongType })
        ) { backStackEntry ->
            val transactionId = backStackEntry.arguments?.getLong("transactionId") ?: return@composable
            val viewModel: AddIncomeViewModel = viewModel(
                factory = AddIncomeViewModelFactory(repository)
            )
            
            // Load transaction data
            LaunchedEffect(transactionId) {
                val transaction = repository.getTransactionById(transactionId)
                if (transaction != null) {
                    viewModel.loadTransaction(transaction)
                }
            }
            
            AddIncomeScreen(
                viewModel = viewModel,
                onDismiss = {
                    navController.popBackStack()
                },
                onIncomeAdded = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = viewModel(
                factory = SettingsViewModelFactory(repository)
            )
            SettingsScreen(
                viewModel = viewModel,
                onDismiss = {
                    navController.popBackStack()
                },
                onSettingsSaved = {
                    // Refresh home screen data
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Onboarding.route) {
            val viewModel: OnboardingViewModel = viewModel(
                factory = OnboardingViewModelFactory(repository)
            )
            OnboardingScreen(
                viewModel = viewModel,
                onComplete = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

// ViewModel Factories
class HomeViewModelFactory(
    private val repository: BaryaBuddyRepository,
    private val calculateDailySafeSpend: CalculateDailySafeSpend
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(repository, calculateDailySafeSpend) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AddExpenseViewModelFactory(
    private val repository: BaryaBuddyRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddExpenseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddExpenseViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class OnboardingViewModelFactory(
    private val repository: BaryaBuddyRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OnboardingViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class AddIncomeViewModelFactory(
    private val repository: BaryaBuddyRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AddIncomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return AddIncomeViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class SettingsViewModelFactory(
    private val repository: BaryaBuddyRepository
) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

