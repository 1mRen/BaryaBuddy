package com.baryabuddy.app.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.rounded.DirectionsCar
import androidx.compose.material.icons.rounded.LocalHospital
import androidx.compose.material.icons.rounded.Movie
import androidx.compose.material.icons.rounded.PhoneAndroid
import androidx.compose.material.icons.rounded.Receipt
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.School
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Subscriptions
import androidx.compose.ui.graphics.vector.ImageVector

fun getIconResource(iconName: String): ImageVector {
    return when(iconName.lowercase()) {
        "food" -> Icons.Rounded.Restaurant
        "transport" -> Icons.Rounded.DirectionsCar
        "bills" -> Icons.Rounded.Receipt
        "data" -> Icons.Rounded.PhoneAndroid // For Load & Data
        "entertainment" -> Icons.Rounded.Movie
        "shopping" -> Icons.Rounded.ShoppingCart
        "healthcare" -> Icons.Rounded.LocalHospital
        "school" -> Icons.Rounded.School
        "education" -> Icons.Rounded.School
        "sub" -> Icons.Rounded.Subscriptions // For Subscriptions
        "other" -> Icons.Filled.AttachMoney
        else -> Icons.Filled.AttachMoney
    }
}

