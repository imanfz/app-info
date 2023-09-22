package dev.fztech.app.info.ui.component

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.fztech.app.info.ui.theme.Dimens

/**
 * Default [Spacer]
 *
 * with [size] = [Dimens.Default]
 */
@Composable
fun DefaultSpacer() = Spacer(
    modifier = Modifier
        .size(Dimens.Default)
)

/**
 * Extra small [Spacer]
 *
 * with [size] = [Dimens.XSmall]
 */
@Composable
fun ExtraSmallSpacer() = Spacer(
    modifier = Modifier
        .size(Dimens.XSmall)
)

/**
 * Small [[Spacer]]
 *
 * with [size] = [Dimens.Small]
 */
@Composable
fun SmallSpacer() = Spacer(
    modifier = Modifier
        .size(Dimens.Small)
)

/**
 * Medium [Spacer]
 *
 * with [size] = [Dimens.Medium]
 */
@Composable
fun MediumSpacer() = Spacer(
    modifier = Modifier
        .size(Dimens.Medium)
)

/**
 * Large [Spacer]
 *
 * with [size] = [Dimens.Large]
 */
@Composable
fun LargeSpacer() = Spacer(
    modifier = Modifier
        .size(Dimens.Large)
)

/**
 * Extra large [Spacer]
 *
 * with [size] = [Dimens.XLarge]
 */
@Composable
fun ExtraLargeSpacer() = Spacer(
    modifier = Modifier
        .size(Dimens.XLarge)
)