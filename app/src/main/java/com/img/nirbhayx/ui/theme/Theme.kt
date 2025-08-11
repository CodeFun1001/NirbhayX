package com.img.nirbhayx.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

@Composable
fun NirbhayXTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = BrightOrange,
            onPrimary = PureWhite,
            primaryContainer = ElectricOrange,
            onPrimaryContainer = PureWhite,
            inversePrimary = NeonOrange,

            secondary = ElectricYellow,
            onSecondary = CharcoalBlack,
            secondaryContainer = SunBurst,
            onSecondaryContainer = CharcoalBlack,

            tertiary = ActionGreen,
            onTertiary = CharcoalBlack,
            tertiaryContainer = PowerGreen,
            onTertiaryContainer = CharcoalBlack,

            background = JetBlack,
            onBackground = SnowWhite,
            surface = GunmetalGray,
            onSurface = SnowWhite,
            surfaceVariant = DarkSlate,
            onSurfaceVariant = LightGray,
            surfaceTint = BrightOrange,
            inverseSurface = LightGray,
            inverseOnSurface = CharcoalBlack,

            error = ElectricRed,
            onError = PureWhite,
            errorContainer = FireOrange,
            onErrorContainer = PureWhite,

            outline = NeonOrange,
            outlineVariant = VibrantOrange,
            scrim = RichBlack.copy(alpha = 0.8f),

            surfaceBright = ModernGray,
            surfaceDim = CharcoalBlack,
            surfaceContainer = GunmetalGray,
            surfaceContainerHigh = CharcoalBlack,
            surfaceContainerHighest = JetBlack,
            surfaceContainerLow = DarkSlate,
            surfaceContainerLowest = CharcoalBlack
        )
    } else {
        lightColorScheme(
            primary = BrightOrange,
            onPrimary = PureWhite,
            primaryContainer = VibrantOrange,
            onPrimaryContainer = PureWhite,
            inversePrimary = ElectricOrange,

            secondary = SunBurst,
            onSecondary = CharcoalBlack,
            secondaryContainer = LightningYellow,
            onSecondaryContainer = CharcoalBlack,

            tertiary = PowerGreen,
            onTertiary = PureWhite,
            tertiaryContainer = ActionGreen,
            onTertiaryContainer = PureWhite,

            background = SnowWhite,
            onBackground = CharcoalBlack,
            surface = PureWhite,
            onSurface = CharcoalBlack,
            surfaceVariant = LightGray,
            onSurfaceVariant = DarkSlate,
            surfaceTint = BrightOrange,
            inverseSurface = CharcoalBlack,
            inverseOnSurface = SnowWhite,

            error = ElectricRed,
            onError = PureWhite,
            errorContainer = FireOrange,
            onErrorContainer = PureWhite,

            outline = VibrantOrange,
            outlineVariant = SunsetOrange,
            scrim = CharcoalBlack.copy(alpha = 0.6f),

            surfaceBright = SnowWhite,
            surfaceDim = LightGray,
            surfaceContainer = PureWhite,
            surfaceContainerHigh = SnowWhite,
            surfaceContainerHighest = PureWhite,
            surfaceContainerLow = LightGray,
            surfaceContainerLowest = SilverMist
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}