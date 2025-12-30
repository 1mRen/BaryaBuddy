package com.baryabuddy.app.presentation.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baryabuddy.app.data.database.entities.IncomeFrequency
import com.baryabuddy.app.data.database.entities.UserProfile
import com.baryabuddy.app.data.repository.BaryaBuddyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class OnboardingUiState(
    val currentStep: Int = 1,
    val monthlyIncome: String = "",
    val fixedBills: String = "",
    val savingsGoal: String = "",
    val incomeError: String? = null,
    val billsError: String? = null,
    val goalError: String? = null,
    val validationError: String? = null
)

class OnboardingViewModel(
    private val repository: BaryaBuddyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun setMonthlyIncome(income: String) {
        _uiState.value = _uiState.value.copy(
            monthlyIncome = income,
            incomeError = null
        )
    }

    fun setFixedBills(bills: String) {
        _uiState.value = _uiState.value.copy(
            fixedBills = bills,
            billsError = null
        )
    }

    fun setSavingsGoal(goal: String) {
        _uiState.value = _uiState.value.copy(
            savingsGoal = goal,
            goalError = null,
            validationError = null
        )
    }

    fun nextStep(): Boolean {
        val state = _uiState.value
        return when (state.currentStep) {
            1 -> {
                val income = state.monthlyIncome.toDoubleOrNull()
                if (income == null || income <= 0) {
                    _uiState.value = state.copy(incomeError = "Please enter a valid income")
                    false
                } else {
                    _uiState.value = state.copy(currentStep = 2)
                    true
                }
            }
            2 -> {
                val bills = state.fixedBills.toDoubleOrNull()
                if (bills == null || bills < 0) {
                    _uiState.value = state.copy(billsError = "Please enter a valid amount")
                    false
                } else {
                    _uiState.value = state.copy(currentStep = 3)
                    true
                }
            }
            else -> false
        }
    }

    fun previousStep() {
        val state = _uiState.value
        if (state.currentStep > 1) {
            _uiState.value = state.copy(currentStep = state.currentStep - 1)
        }
    }

    suspend fun validateAndSave(): Boolean {
        val state = _uiState.value
        val income = state.monthlyIncome.toDoubleOrNull() ?: 0.0
        val bills = state.fixedBills.toDoubleOrNull() ?: 0.0
        val goal = state.savingsGoal.toDoubleOrNull() ?: 0.0

        // Validate all values
        if (income <= 0) {
            _uiState.value = state.copy(incomeError = "Income must be greater than 0")
            return false
        }
        if (bills < 0) {
            _uiState.value = state.copy(billsError = "Bills cannot be negative")
            return false
        }
        if (goal < 0) {
            _uiState.value = state.copy(goalError = "Savings goal cannot be negative")
            return false
        }

        // Validate that income >= bills + goal
        if (income < bills + goal) {
            _uiState.value = state.copy(
                validationError = "Income must be at least equal to Fixed Bills + Savings Goal"
            )
            return false
        }

        // Save to database - Convert to centavos
        return try {
            val incomeAmountCentavos = (income * 100).toLong()
            val billsAmountCentavos = (bills * 100).toLong()
            val savingsGoalAmountCentavos = (goal * 100).toLong()
            
            val profile = UserProfile(
                id = 1,
                incomeAmount = incomeAmountCentavos,
                fixedBillsAmount = billsAmountCentavos,
                savingsGoalAmount = savingsGoalAmountCentavos,
                incomeFrequency = IncomeFrequency.MONTHLY, // Default to monthly for Sprint 1
                resetDay = 1, // Default to 1st of month
                currency = "₱",
                setupCompleted = true
            )
            repository.updateUserProfile(profile)
            true
        } catch (e: Exception) {
            _uiState.value = state.copy(validationError = "Error saving profile: ${e.message}")
            false
        }
    }
}

