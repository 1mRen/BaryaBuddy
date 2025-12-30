package com.baryabuddy.app.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.baryabuddy.app.data.database.entities.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {
    @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC")
    fun getAll(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date >= :startOfMonth AND date < :startOfNextMonth ORDER BY date DESC, createdAt DESC")
    fun getByMonth(startOfMonth: Long, startOfNextMonth: Long): Flow<List<Transaction>>

    @Query("SELECT SUM(amountCentavos) FROM transactions WHERE date >= :startOfMonth AND date < :startOfNextMonth")
    suspend fun getTotalByMonth(startOfMonth: Long, startOfNextMonth: Long): Long?

    @Query("SELECT * FROM transactions ORDER BY date DESC, createdAt DESC LIMIT :limit")
    fun getRecent(limit: Int = 10): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE id = :id")
    suspend fun getById(id: Long): Transaction?

    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction)

    @Delete
    suspend fun delete(transaction: Transaction)
}

