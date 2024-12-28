package com.example.lostandfound.Data

import androidx.compose.ui.graphics.Color

data class Colors(
    val name: String,
    val color: Color       // will be displayed as a circle filled with that color
)

val itemColors = listOf<Colors>(
    Colors(name = "Red", color = Color.Red),
    Colors(name = "Orange", color = Color(0xFFFF9D47)),
    Colors(name = "Yellow", color = Color.Yellow),
    Colors(name = "Green", color = Color.Green),
    Colors(name = "Cyan", color = Color.Cyan),
    Colors(name = "Blue", color = Color.Blue),
    Colors(name = "Pink", color = Color(0xFFFFA3CE)),
    Colors(name = "Magenta", color = Color.Magenta),
    Colors(name = "Brown", color = Color(0xFF775944)),
    Colors(name = "Gray", color = Color.Gray),
    Colors(name = "Black", color = Color.Black),
    Colors(name = "White", color = Color.White),
    Colors(name = "Other", color = Color.Transparent)
)