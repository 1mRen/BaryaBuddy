package com.baryabuddy.app.data.database

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.baryabuddy.app.data.database.entities.Category
import com.baryabuddy.app.data.database.entities.IncomeFrequency
import com.baryabuddy.app.data.database.entities.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

object DatabaseModule {
    fun initializeDatabase(context: Context) {
        val database = AppDatabase.getDatabase(context)
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        scope.launch {
            // Pre-populate categories with student-friendly names
            val categories = listOf(
                Category(
                    id = 1,
                    name = "Food & Canteen",
                    icon = "food",
                    color = Color(0xFFFF6B6B).toArgb()
                ),
                Category(
                    id = 2,
                    name = "Commute",
                    icon = "transport",
                    color = Color(0xFF4ECDC4).toArgb()
                ),
                Category(
                    id = 3,
                    name = "Load & Data",
                    icon = "data",
                    color = Color(0xFFFFE66D).toArgb()
                ),
                Category(
                    id = 4,
                    name = "Gimik / Fun",
                    icon = "entertainment",
                    color = Color(0xFF95E1D3).toArgb()
                ),
                Category(
                    id = 5,
                    name = "Lazada / Shopee",
                    icon = "shopping",
                    color = Color(0xFFF38181).toArgb()
                ),
                Category(
                    id = 6,
                    name = "Academics",
                    icon = "school",
                    color = Color(0xFFAA96DA).toArgb()
                ),
                Category(
                    id = 7,
                    name = "Subscriptions",
                    icon = "sub",
                    color = Color(0xFFFCBAD3).toArgb()
                ),
                Category(
                    id = 8,
                    name = "Other",
                    icon = "other",
                    color = Color(0xFFC7CEEA).toArgb()
                )
            )

            // Always update categories to ensure correct colors (REPLACE strategy)
            categories.forEach { category ->
                try {
                    database.categoryDao().insert(category)
                } catch (e: Exception) {
                    // Category might already exist, ignore
                }
            }

            // Initialize UserProfile if it doesn't exist
            val profile = database.userProfileDao().getProfileOnce()
            if (profile == null) {
                database.userProfileDao().insert(
                    UserProfile(
                        id = 1,
                        incomeAmount = 0L,
                        fixedBillsAmount = 0L,
                        savingsGoalAmount = 0L,
                        incomeFrequency = IncomeFrequency.MONTHLY,
                        resetDay = 1,
                        currency = "₱",
                        setupCompleted = false
                    )
                )
            }
        }
    }
}

