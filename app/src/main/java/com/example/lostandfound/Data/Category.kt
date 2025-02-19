package com.example.lostandfound.Data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Headset
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material.icons.outlined.Restaurant
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.SportsBasketball
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
        subCategories = listOf("Wallet", "Key", "Watch", "Umbrella", "Eyeglasses", "ID/Personal document"),
        leadingIcon = Icons.Outlined.AccountBox
    ),
    Category(name = "Electronics",
        subCategories = listOf("Earphone", "Headphone", "Laptop", "Phone", "Tablet", "Calculator", "Camera", "Charger", "USB", "Mouse", "Digital Pen", "EReader"),
        leadingIcon = Icons.Outlined.Headset
    ),
    Category(name = "Bags and Suitcases",
        subCategories = listOf("Backpack", "Handbag", "Shopping Bag", "Suitcase"),
        leadingIcon = Icons.Outlined.ShoppingBag
    ),
    Category(name = "Books and Stationery",
        subCategories = listOf("Stationery", "Pencil Cases", "Books"),
        leadingIcon = Icons.Outlined.Edit
    ),
    Category(name = "Clothing",
        subCategories = listOf("Hat", "Gloves", "Scarf", "Jacket"),
        leadingIcon = Icons.Outlined.Checkroom
    ),
    Category(name = "Sports",
        subCategories = listOf("Ball", "Helmet", "Racket", "Boxing Gloves"),
        leadingIcon = Icons.Outlined.SportsBasketball
    ),
    Category(name = "Food and Drink",
        subCategories = listOf("Lunchbox", "Utensils", "Water bottle", "Cup"),
        leadingIcon = Icons.Outlined.Restaurant
    ),
    Category(name = "Others",
        subCategories = listOf(),
        leadingIcon = Icons.Outlined.QuestionMark
    )

)