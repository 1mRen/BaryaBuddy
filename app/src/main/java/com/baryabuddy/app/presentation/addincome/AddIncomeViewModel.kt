package com.baryabuddy.app.presentation.addincome

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baryabuddy.app.data.database.entities.Transaction
import com.baryabuddy.app.data.repository.BaryaBuddyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class AddIncomeUiState(
    val amount: String = "",
    val description: String = "",
    val date: LocalDateTime = LocalDateTime.now(),
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val transactionId: Long? = null
)

class AddIncomeViewModel(
    private val repository: BaryaBuddyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddIncomeUiState())
    val uiState: StateFlow<AddIncomeUiState> = _uiState.asStateFlow()

    fun setAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun setDescription(description: String) {
        _uiState.value = _uiState.value.copy(description = description)
    }

    fun setDate(date: LocalDateTime) {
        _uiState.value = _uiState.value.copy(date = date)
    }

    fun loadTransaction(transaction: Transaction) {
        _uiState.value = _uiState.value.copy(
            amount = (transaction.amountCentavos / 100.0).toString(),
            description = transaction.description ?: "",
            date = transaction.date,
            isEditing = true,
            transactionId = transaction.id
        )
    }

    suspend fun saveIncome(): Boolean {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull() ?: return false

        if (amount <= 0) return false

        // Convert amount to centavos (Long)
        val amountCentavos = (amount * 100).toLong()

        val transaction = if (state.isEditing && state.transactionId != null) {
            // Update existing transaction
            Transaction(
                id = state.transactionId,
                amountCentavos = amountCentavos,
                categoryId = null, // NULL = Income
                description = state.description.takeIf { it.isNotBlank() },
                date = state.date,
                createdAt = System.currentTimeMillis()
            )
        } else {
            // Create new transaction
            Transaction(
                amountCentavos = amountCentavos,
                categoryId = null, // NULL = Income
                description = state.description.takeIf { it.isNotBlank() },
                date = state.date,
                createdAt = System.currentTimeMillis()
            )
        }

        return try {
            if (state.isEditing && state.transactionId != null) {
                repository.updateTransaction(transaction)
            } else {
                repository.addTransaction(transaction)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}

