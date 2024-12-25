package com.example.lostandfound.Utility

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBox
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material.icons.outlined.Draw
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
        subCategories = listOf("Wallet", "Key", "Watch", "Umbrella", "Eyeglasses", "ID/Personal documents"),
        leadingIcon = Icons.Outlined.AccountBox
    ),
    Category(name = "Electronics",
        subCategories = listOf("Earphones/Headphones", "Laptop/tablet", "Camera", "Charger", "USB", "Mouse", "Digital pen"),
        leadingIcon = Icons.Outlined.Headset
    ),
    Category(name = "Bags and Suitcases",
        subCategories = listOf("backpack", "handbag", "shopping bag", "suitcase"),
        leadingIcon = Icons.Outlined.ShoppingBag
    ),
    Category(name = "Books and Stationaries",
        subCategories = listOf("Stationaries", "Pencil cases", "Books"),
        leadingIcon = Icons.Outlined.Edit
    ),
    Category(name = "Clothings",
        subCategories = listOf("Hats", "Gloves", "Scarf", "Jacket"),
        leadingIcon = Icons.Outlined.Checkroom
    ),
    Category(name = "Sports",
        subCategories = listOf("Balls", "Helmets", "Rackets"),
        leadingIcon = Icons.Outlined.SportsBasketball
    ),
    Category(name = "Food and Drink",
        subCategories = listOf("Lunchboxes", "Utensils", "Water bottle", "Cups"),
        leadingIcon = Icons.Outlined.Restaurant
    ),
    Category(name = "Others",
        subCategories = listOf(),
        leadingIcon = Icons.Outlined.QuestionMark
    )

)