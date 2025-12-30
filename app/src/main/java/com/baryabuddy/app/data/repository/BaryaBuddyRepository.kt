package com.baryabuddy.app.data.repository

import com.baryabuddy.app.data.database.AppDatabase
import com.baryabuddy.app.data.database.entities.Category
import com.baryabuddy.app.data.database.entities.Transaction
import com.baryabuddy.app.data.database.entities.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset

class BaryaBuddyRepository(private val database: AppDatabase) {

    // Transactions
    fun getAllTransactions(): Flow<List<Transaction>> {
        return database.transactionDao().getAll()
    }

    fun getRecentTransactions(limit: Int = 10): Flow<List<Transaction>> {
        return database.transactionDao().getRecent(limit)
    }

    suspend fun getTransactionsByMonth(year: Int, month: Int): List<Transaction> {
        val startOfMonth = LocalDate.of(year, month, 1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        
        val startOfNextMonth = LocalDate.of(year, month, 1)
            .plusMonths(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        return database.transactionDao().getByMonth(startOfMonth, startOfNextMonth).first()
    }

    suspend fun getTotalByMonth(year: Int, month: Int): Double {
        val startOfMonth = LocalDate.of(year, month, 1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        
        val startOfNextMonth = LocalDate.of(year, month, 1)
            .plusMonths(1)
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

        val totalCentavos = database.transactionDao().getTotalByMonth(startOfMonth, startOfNextMonth) ?: 0L
        return totalCentavos / 100.0
    }

    suspend fun addTransaction(transaction: Transaction): Long {
        return database.transactionDao().insert(transaction)
    }

    suspend fun getTransactionById(id: Long): Transaction? {
        return database.transactionDao().getById(id)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        database.transactionDao().update(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        database.transactionDao().delete(transaction)
    }

    // Categories
    fun getCategories(): Flow<List<Category>> {
        return database.categoryDao().getAll()
    }

    suspend fun getCategoryById(id: Long): Category? {
        return database.categoryDao().getById(id)
    }

    // User Profile
    fun getUserProfile(): Flow<UserProfile?> {
        return database.userProfileDao().getProfile()
    }

    suspend fun getUserProfileOnce(): UserProfile? {
        return database.userProfileDao().getProfileOnce()
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        database.userProfileDao().update(profile)
    }

    suspend fun insertUserProfile(profile: UserProfile) {
        database.userProfileDao().insert(profile)
    }
}

