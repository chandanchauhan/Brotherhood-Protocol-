package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val DarkColorScheme = darkColorScheme(
    primary = Sun,
    onPrimary = Soil,
    secondary = Wheat,
    onSecondary = Soil,
    tertiary = Clay,
    background = Soil,
    onBackground = Cream,
    surface = Mud,
    onSurface = Cream,
    outline = Clay
)

private val LightColorScheme = lightColorScheme(
    primary = Soil,
    onPrimary = Cream,
    secondary = Clay,
    onSecondary = Cream,
    tertiary = Wheat,
    background = LightBackground,
    onBackground = LightOnSurface,
    surface = LightSurface,
    onSurface = LightOnSurface,
    outline = LightBorder
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
