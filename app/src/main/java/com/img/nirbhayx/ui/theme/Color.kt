package com.img.nirbhayx.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)
val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val ElectricOrange = Color(0xFFFF4500)
val VibrantOrange = Color(0xFFFF7700)
val SunsetOrange = Color(0xFFFF8800)
val TangerineOrange = Color(0xFFFF8C00)
val BrightOrange = Color(0xFFFF6000)
val SaffronGlow = Color(0xFFFF6F00)
val NeonOrange = Color(0xFFFF5500)
val ApricotGlow = Color(0xFFFFB366)
val PeachSoft = Color(0xFFFFD4B3)

val LemonYellow = Color(0xFFFFFF00)
val SunshineYellow = Color(0xFFF4C430)
val ElectricYellow = Color(0xFFFFD600)
val AmberGold = Color(0xFFFFBF00)
val GoldenRod = Color(0xFFDAA520)
val SunBurst = Color(0xFFFFA500)
val LightningYellow = Color(0xFFFFE135)
val GoldenSun = Color(0xFFFFD000)

val AlertRed = Color(0xFFFF5252)
val FireOrange = Color(0xFFFF3300)
val BoldCrimson = Color(0xFFD50000)
val CrimsonRed = Color(0xFFDC143C)
val FireRed = Color(0xFFFF2400)
val DeepRed = Color(0xFF8B0000)
val ElectricRed = Color(0xFFFF1744)

val HopeGreen = Color(0xFF00E676)
val RescueGreen = Color(0xFF00C853)
val NeonLime = Color(0xFF39FF14)
val SpringGreen = Color(0xFF00FF7F)
val EmeraldGreen = Color(0xFF50C878)
val ForestGreen = Color(0xFF228B22)
val ActionGreen = Color(0xFF00FF7F)
val PowerGreen = Color(0xFF32CD32)
val EmeraldForce = Color(0xFF50C878)

val ElectricBlue = Color(0xFF0080FF)
val CyberPurple = Color(0xFF8A2BE2)
val HotPink = Color(0xFFFF0080)
val CyberBlue = Color(0xFF00BFFF)
val ElectricPurple = Color(0xFF8000FF)
val GoldRush = Color(0xFFFFD700)
val NeonGreen = Color(0xFF00FF41)

val PureWhite = Color(0xFFFFFFFF)
val SnowWhite = Color(0xFFFFFAFA)
val GrayWhite = Color(0xFFF3F3F3)
val Smoke = Color(0xFFF5F5F5)
val CreamWhite = Color(0xFFFFFDF0)
val LightGray = Color(0xFFF5F5F5)
val SilverGray = Color(0xFFC0C0C0)
val SilverMist = Color(0xFFE8E8E8)
val DarkText = Color(0xFF212121)
val ModernGray = Color(0xFF6B7280)
val GunmetalGray = Color(0xFF1A1A1A)
val CharcoalGray = Color(0xFF36454F)
val CharcoalBlack = Color(0xFF1F2937)
val DarkSlate = Color(0xFF374151)
val JetBlack = Color(0xFF0F0F0F)
val RichBlack = Color(0xFF000000)

val OrangeGradient = Brush.horizontalGradient(
    colors = listOf(ElectricOrange, VibrantOrange, SunsetOrange)
)

val HeroGradient = Brush.horizontalGradient(
    colors = listOf(ElectricOrange, FireOrange, ElectricRed)
)

val PowerGradient = Brush.verticalGradient(
    colors = listOf(BrightOrange, NeonOrange, VibrantOrange)
)

val EnergyGradient = Brush.radialGradient(
    colors = listOf(ElectricYellow, SunBurst, BrightOrange)
)

val LightBackgroundGradient = Brush.verticalGradient(
    colors = listOf(SnowWhite, LightGray.copy(alpha = 0.3f), PureWhite)
)

val DarkBackgroundGradient = Brush.verticalGradient(
    colors = listOf(CharcoalBlack, DarkSlate, JetBlack)
)

val LightSurfaceGradient = Brush.linearGradient(
    colors = listOf(PureWhite, SnowWhite.copy(alpha = 0.8f))
)

val DarkSurfaceGradient = Brush.linearGradient(
    colors = listOf(DarkSlate, CharcoalBlack.copy(alpha = 0.9f))
)

val PrimaryButtonGradient = Brush.horizontalGradient(
    colors = listOf(BrightOrange, FireOrange, ElectricRed)
)

val SecondaryButtonGradient = Brush.horizontalGradient(
    colors = listOf(CyberBlue, ElectricPurple)
)

val SuccessButtonGradient = Brush.horizontalGradient(
    colors = listOf(PowerGreen, ActionGreen, NeonLime)
)

val DangerButtonGradient = Brush.horizontalGradient(
    colors = listOf(ElectricRed, FireOrange)
)

val GlowGradient = Brush.radialGradient(
    colors = listOf(
        BrightOrange.copy(alpha = 0.8f),
        NeonOrange.copy(alpha = 0.4f),
        Color.Transparent
    )
)

val ShineGradient = Brush.linearGradient(
    colors = listOf(
        Color.Transparent,
        PureWhite.copy(alpha = 0.3f),
        Color.Transparent
    )
)

val TopBarGradient = Brush.horizontalGradient(
    colors = listOf(BrightOrange, NeonOrange, VibrantOrange)
)

val BottomBarGradient = Brush.horizontalGradient(
    colors = listOf(VibrantOrange, BrightOrange, SunsetOrange)
)

val HeroCardGradient = Brush.linearGradient(
    colors = listOf(
        BrightOrange.copy(alpha = 0.1f),
        NeonOrange.copy(alpha = 0.05f)
    )
)

val DarkHeroCardGradient = Brush.linearGradient(
    colors = listOf(
        BrightOrange.copy(alpha = 0.2f),
        FireOrange.copy(alpha = 0.1f)
    )
)
