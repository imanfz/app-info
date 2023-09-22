package dev.fztech.app.info.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import dev.fztech.app.info.BuildConfig
import dev.fztech.app.info.utils.extenstions.findActivity

fun isDevelopment(): Boolean {
    return BuildConfig.FLAVOR == "dev"
}

/**
 * DPI
 *
 * MDPI = 1x
 *
 * HDPI = 1.5x
 *
 * XHDPI = 2x
 *
 * XXHDPI = 3x
 *
 * XXXHDPI = 4x
 *
 *
 * @return [Boolean]
 */
@Composable
fun getDpi() = LocalConfiguration.current.densityDpi / 160f

@Composable
fun getContext() = LocalContext.current

@Composable
fun getActivity() = getContext().findActivity()

@Composable
fun isSmallScreen(): Boolean {
    val dm = LocalContext.current.resources.displayMetrics
    val widthPixels = dm.widthPixels
    val heightPixels = dm.heightPixels
//    val densityDpi = dm.densityDpi
    val density = dm.density

    val widthDp = widthPixels / density
    val heightDp = heightPixels / density

    return widthDp < 600f && heightDp < 891f
}