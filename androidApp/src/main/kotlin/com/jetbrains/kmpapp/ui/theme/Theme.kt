package com.jetbrains.kmpapp.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// 用精灵球红当主色，其余走 Material3 默认色板
private val DarkColors = darkColorScheme(primary = PokeRed, secondary = PokeRedDark)
private val LightColors = lightColorScheme(primary = PokeRed, secondary = PokeRedDark)

@Composable
fun KmpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as android.app.Activity).window
            // 沉浸式：状态栏图标颜色跟主题走（深色主题用浅色图标）
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            // 关掉系统底栏的半透明遮罩，让我们的底栏颜色一直铺到屏幕边
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                window.isNavigationBarContrastEnforced = false
            }
        }
    }
    MaterialTheme(colorScheme = colorScheme, typography = AppTypography, content = content)
}
