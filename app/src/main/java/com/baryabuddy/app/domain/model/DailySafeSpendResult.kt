package com.baryabuddy.app.domain.model

enum class SafeSpendStatus { GREEN, YELLOW, RED }

data class DailySafeSpendResult(
    val dailySafeSpendAmount: Double,
    val totalRemaining: Double,
    val statusColor: SafeSpendStatus
)

