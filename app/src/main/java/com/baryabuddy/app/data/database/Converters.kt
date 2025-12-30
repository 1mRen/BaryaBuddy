package com.baryabuddy.app.data.database

import androidx.room.TypeConverter
import com.baryabuddy.app.data.database.entities.IncomeFrequency
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

class Converters {
    // --- 📅 Date Converters ---
    @TypeConverter
    fun fromTimestamp(value: Long?): LocalDateTime? {
        return value?.let { 
            Instant.ofEpochMilli(it)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime() 
        }
    }

    @TypeConverter
    fun dateToTimestamp(date: LocalDateTime?): Long? {
        return date?.atZone(ZoneId.systemDefault())
            ?.toInstant()
            ?.toEpochMilli()
    }

    // --- 🔠 Enum Converters (Safe String Storage) ---
    @TypeConverter
    fun fromIncomeFrequency(value: String): IncomeFrequency {
        return try {
            IncomeFrequency.valueOf(value)
        } catch (e: IllegalArgumentException) {
            IncomeFrequency.MONTHLY // Fallback safety
        }
    }

    @TypeConverter
    fun incomeFrequencyToString(frequency: IncomeFrequency): String {
        return frequency.name // Stores "WEEKLY", "MONTHLY", or "IRREGULAR"
    }
}

