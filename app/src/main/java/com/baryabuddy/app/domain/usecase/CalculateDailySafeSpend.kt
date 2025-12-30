package com.baryabuddy.app.domain.usecase

import com.baryabuddy.app.data.database.entities.IncomeFrequency
import com.baryabuddy.app.data.database.entities.Transaction
import com.baryabuddy.app.data.database.entities.TransactionType
import com.baryabuddy.app.data.database.entities.UserProfile
import com.baryabuddy.app.domain.model.DailySafeSpendResult
import com.baryabuddy.app.domain.model.SafeSpendStatus
import java.time.LocalDate

class CalculateDailySafeSpend {

    operator fun invoke(
        userProfile: UserProfile,
        transactions: List<Transaction>,
        currentDate: LocalDate = LocalDate.now()
    ): DailySafeSpendResult {
        
        // 1. 🆕 STUDENT LOGIC PREP
        // If frequency is WEEKLY, normalize to Monthly for the calculation
        val effectiveMonthlyIncomeCentavos = when (userProfile.incomeFrequency) {
            IncomeFrequency.WEEKLY -> userProfile.incomeAmount * 4
            IncomeFrequency.MONTHLY -> userProfile.incomeAmount
            IncomeFrequency.IRREGULAR -> userProfile.incomeAmount // User handles manually
        }
        
        // 2. 🛡️ Amounts are already in Centavos (Safe Math)
        val billsCentavos = userProfile.fixedBillsAmount
        val savingsCentavos = userProfile.savingsGoalAmount
        
        // 3. Calculate Disposable Budget
        val disposableCentavos = effectiveMonthlyIncomeCentavos - billsCentavos - savingsCentavos

        // 4. Calculate "Ideal" Daily Spend (The baseline) in centavos
        val lengthOfMonth = currentDate.lengthOfMonth()
        val idealDailyCentavos = disposableCentavos / lengthOfMonth
        
        // 5. Calculate Actual Spending (filter transactions for current month)
        val currentMonthStart = currentDate.withDayOfMonth(1)
        val nextMonthStart = currentMonthStart.plusMonths(1)
        
        val currentMonthTransactions = transactions.filter { transaction ->
            val transactionDate = transaction.date.toLocalDate()
            transactionDate >= currentMonthStart && transactionDate < nextMonthStart
        }
        
        // 6. 🧠 Smart Loop: Calculate Net Spend
        // Income reduces total spent (refills bucket); Expenses increase it.
        val netSpentCentavos = currentMonthTransactions.sumOf { transaction ->
            if (transaction.type == TransactionType.INCOME) {
                -transaction.amountCentavos // Income increases budget (subtract from spent)
            } else {
                transaction.amountCentavos // Expenses decrease budget (add to spent)
            }
        }

        // 7. Calculate Remaining
        // Note: This can be > disposableCentavos if user added extra income!
        val remainingCentavos = disposableCentavos - netSpentCentavos
        
        // 8. Time Logic
        val daysRemaining = lengthOfMonth - currentDate.dayOfMonth + 1
        
        // 9. 🚨 Safety Check: Avoid Division by Zero
        val safeDays = if (daysRemaining < 1) 1 else daysRemaining
        
        // 10. Calculate DSS (Integer Division first)
        val dailySafeSpendCentavos = if (remainingCentavos > 0) {
            remainingCentavos / safeDays
        } else {
            0L // Budget Blown!
        }

        // 11. 🎨 Determine Status Color
        // Compare against "Ideal" baseline (Disposable / Total Days in Month)
        val status = when {
            dailySafeSpendCentavos >= (idealDailyCentavos * 0.7) -> SafeSpendStatus.GREEN
            dailySafeSpendCentavos >= (idealDailyCentavos * 0.3) -> SafeSpendStatus.YELLOW
            else -> SafeSpendStatus.RED
        }

        // 12. Final Convert to Doubles for UI
        return DailySafeSpendResult(
            dailySafeSpendAmount = dailySafeSpendCentavos / 100.0,
            totalRemaining = remainingCentavos / 100.0,
            statusColor = status
        )
    }
}

