package com.example.lostandfound.Utility

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Headset
import androidx.compose.ui.graphics.vector.ImageVector

data class Category(
    val name: String,
    val subCategories: List<String>,
    val leadingIcon: ImageVector
)

// data are stored here as a list of categories
// subcategories should be in the plural format
val categories: List<Category> = listOf(
    Category(name = "Personal items",
        subCategories = listOf("Wallet", "Key", "Watch", "Umbrella", "Eyeglasses", "ID/Personal documents"),
        leadingIcon = Icons.Outlined.AccountBox
    ),
    Category(name = "Electronics",
        subCategories = listOf("Earphones/Headphones", "Laptop/tablet", "Camera", "Charger", "USB", "Mouse", "Digital pen"),
        leadingIcon = Icons.Outlined.Headset
    )


)