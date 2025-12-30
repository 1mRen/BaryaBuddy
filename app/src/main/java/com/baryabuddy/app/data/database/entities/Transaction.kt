package com.baryabuddy.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    // 💰 Money Logic: Stored as Centavos (Long)
    // ₱100.50 -> 10050
    val amountCentavos: Long,
    
    // 🏷️ Category Logic: 
    // NULL = Income (Global source)
    // INT = Expense (Specific category)
    val categoryId: Int? = null,
    
    val description: String? = null,
    
    // 📅 Time Logic: Stored as LocalDateTime via TypeConverter
    val date: LocalDateTime,
    
    val createdAt: Long = System.currentTimeMillis()
) {
    // 🧠 Computed Property (Not stored in DB)
    val type: TransactionType
        get() = if (categoryId == null) TransactionType.INCOME else TransactionType.EXPENSE
}

