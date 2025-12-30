package com.baryabuddy.app.presentation.addexpense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.baryabuddy.app.data.database.entities.Transaction
import com.baryabuddy.app.data.database.entities.Category
import com.baryabuddy.app.data.repository.BaryaBuddyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class AddExpenseUiState(
    val amount: String = "",
    val selectedCategoryId: Long? = null,
    val description: String = "",
    val date: LocalDateTime = LocalDateTime.now(),
    val categories: List<Category> = emptyList(),
    val isLoading: Boolean = false,
    val isEditing: Boolean = false,
    val transactionId: Long? = null
)

class AddExpenseViewModel(
    private val repository: BaryaBuddyRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    init {
        loadCategories()
    }

    private fun loadCategories() {
        viewModelScope.launch {
            repository.getCategories().collect { categories ->
                _uiState.value = _uiState.value.copy(categories = categories)
            }
        }
    }

    fun setAmount(amount: String) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    fun selectCategory(categoryId: Long) {
        _uiState.value = _uiState.value.copy(selectedCategoryId = categoryId)
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
            selectedCategoryId = transaction.categoryId?.toLong(),
            description = transaction.description ?: "",
            date = transaction.date,
            isEditing = true,
            transactionId = transaction.id
        )
    }

    suspend fun saveTransaction(): Boolean {
        val state = _uiState.value
        val amount = state.amount.toDoubleOrNull() ?: return false
        val categoryId = state.selectedCategoryId ?: return false

        if (amount <= 0) return false

        // Convert amount to centavos (Long)
        val amountCentavos = (amount * 100).toLong()
        
        // Convert categoryId from Long to Int
        val categoryIdInt = categoryId.toInt()

        val transaction = if (state.isEditing && state.transactionId != null) {
            // Update existing transaction
            Transaction(
                id = state.transactionId,
                amountCentavos = amountCentavos,
                categoryId = categoryIdInt,
                description = state.description.takeIf { it.isNotBlank() },
                date = state.date,
                createdAt = System.currentTimeMillis() // Keep original or update?
            )
        } else {
            // Create new transaction
            Transaction(
                amountCentavos = amountCentavos,
                categoryId = categoryIdInt,
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

