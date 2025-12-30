package com.baryabuddy.app.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baryabuddy.app.data.repository.BaryaBuddyRepository
import com.baryabuddy.app.domain.model.DailySafeSpendResult
import com.baryabuddy.app.domain.model.SafeSpendStatus
import com.baryabuddy.app.domain.model.TransactionUi
import com.baryabuddy.app.domain.usecase.CalculateDailySafeSpend
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.time.LocalDate

data class HomeUiState(
    val dailySafeSpend: DailySafeSpendResult? = null,
    val recentTransactions: List<TransactionUi> = emptyList(),
    val isLoading: Boolean = false,
    val currency: String = "₱"
)

class HomeViewModel(
    private val repository: BaryaBuddyRepository,
    private val calculateDailySafeSpend: CalculateDailySafeSpend
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            // Combine user profile, transactions, and categories
            combine(
                repository.getUserProfile(),
                repository.getRecentTransactions(10),
                repository.getCategories(),
                repository.getAllTransactions()
            ) { profile, transactions, categories, allTransactions ->
                if (profile == null) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    return@combine
                }

                // Calculate DSS
                val dssResult = calculateDailySafeSpend(
                    userProfile = profile,
                    transactions = allTransactions,
                    currentDate = LocalDate.now()
                )

                // Map transactions to TransactionUi
                val transactionUiList = transactions.map { transaction ->
                    val category = transaction.categoryId?.let { catId ->
                        categories.find { it.id.toInt() == catId }
                    }
                    TransactionUi(transaction, category)
                }

                _uiState.value = HomeUiState(
                    dailySafeSpend = dssResult,
                    recentTransactions = transactionUiList,
                    isLoading = false,
                    currency = profile.currency
                )
            }.collect { }
        }
    }

    fun refreshData() {
        loadDashboard()
    }

    suspend fun deleteTransaction(transactionId: Long): Boolean {
        return try {
            val transaction = repository.getTransactionById(transactionId)
            if (transaction != null) {
                repository.deleteTransaction(transaction)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getTransactionById(transactionId: Long): com.baryabuddy.app.data.database.entities.Transaction? {
        return repository.getTransactionById(transactionId)
    }
}

