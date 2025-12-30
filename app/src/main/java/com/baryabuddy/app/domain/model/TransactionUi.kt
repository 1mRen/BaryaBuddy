package com.baryabuddy.app.domain.model

import com.baryabuddy.app.data.database.entities.Category
import com.baryabuddy.app.data.database.entities.Transaction

data class TransactionUi(
    val transaction: Transaction,
    val category: Category?
)

