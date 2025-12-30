package com.baryabuddy.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfile(
    @PrimaryKey
    val id: Long = 1, // Single row, always id = 1
    
    // 💰 Stored as Centavos (Long)
    val incomeAmount: Long = 0L,
    val fixedBillsAmount: Long = 0L,
    val savingsGoalAmount: Long = 0L,
    
    // 🆕 FOR STUDENTS: How often do they receive this money?
    // If WEEKLY, logic will do: incomeAmount * 4
    val incomeFrequency: IncomeFrequency = IncomeFrequency.MONTHLY,
    
    // 🆕 FOR STUDENTS: When does the "month" reset?
    // Students might get allowance on Fridays, not the 1st of the month.
    val resetDay: Int = 1,
    
    val currency: String = "₱",
    val setupCompleted: Boolean = false
)

