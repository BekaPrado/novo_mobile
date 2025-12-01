package com.example.app_journey.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryPurple,
    secondary = PurpleLighter,
    tertiary = PurpleMedium,
    background = PrimaryPurple,
    surface = PurpleDarker,
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = White,
    onSurface = White
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryPurple,
    secondary = PurpleMedium,
    tertiary = PurpleLighter,
    background = Color(0xFFF0E5FF),
    surface = Color(0xFFE4C9F5),
    onPrimary = White,
    onSecondary = White,
    onTertiary = White,
    onBackground = Color(0xFF1A002E),
    onSurface = Color(0xFF1A002E)
)

@Composable
fun JourneyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        darkTheme -> darkColorScheme(
            primary = PrimaryPurple,
            secondary = PurpleLighter,
            tertiary = PurpleMedium,
            background = ReallyDarkPrimaryPurple,  // fundo escuro
            surface = DarkPrimaryPurple,
            onPrimary = White,
            onSecondary = White,
            onTertiary = White,
            onBackground = White,
            onSurface = White
        )
        else -> lightColorScheme(
            primary = PrimaryPurple,
            secondary = PurpleMedium,
            tertiary = PurpleLighter,
            background = LightAccent,            // fundo claro
            surface = BackgroundWhite,
            onPrimary = White,
            onSecondary = White,
            onTertiary = White,
            onBackground = DarkPrimaryPurple,
            onSurface = DarkPrimaryPurple
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
