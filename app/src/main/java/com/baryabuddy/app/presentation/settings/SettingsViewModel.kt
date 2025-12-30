package com.baryabuddy.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baryabuddy.app.data.database.entities.IncomeFrequency
import com.baryabuddy.app.data.database.entities.UserProfile
import com.baryabuddy.app.data.repository.BaryaBuddyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class SettingsUiState(
    val monthlyIncome: String = "",
    val fixedBills: String = "",
    val savingsGoal: String = "",
    val incomeError: String? = null,
    val billsError: String? = null,
    val goalError: String? = null,
    val validationError: String? = null,
    val isLoading: Boolean = false
)

class SettingsViewModel(
    private val repository: BaryaBuddyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val profile = repository.getUserProfile().first()
            if (profile != null) {
                // Convert centavos back to display amounts
                _uiState.value = SettingsUiState(
                    monthlyIncome = (profile.incomeAmount / 100.0).toString(),
                    fixedBills = (profile.fixedBillsAmount / 100.0).toString(),
                    savingsGoal = (profile.savingsGoalAmount / 100.0).toString(),
                    isLoading = false
                )
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

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

    suspend fun saveProfile(): Boolean {
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
            
            // Get existing profile to preserve incomeFrequency and resetDay
            val existingProfile = repository.getUserProfileOnce()
            val profile = UserProfile(
                id = 1,
                incomeAmount = incomeAmountCentavos,
                fixedBillsAmount = billsAmountCentavos,
                savingsGoalAmount = savingsGoalAmountCentavos,
                incomeFrequency = existingProfile?.incomeFrequency ?: IncomeFrequency.MONTHLY,
                resetDay = existingProfile?.resetDay ?: 1,
                currency = existingProfile?.currency ?: "₱",
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

