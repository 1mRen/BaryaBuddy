package com.baryabuddy.app.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val icon: String, // String resource name (e.g., "food", "transport")
    val color: Int // Color as Int (ARGB)
)

