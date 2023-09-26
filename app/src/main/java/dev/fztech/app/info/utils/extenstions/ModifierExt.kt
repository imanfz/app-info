package dev.fztech.app.info.utils.extenstions

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.semantics.Role
import io.github.imanfz.jetpackcomposedoc.ui.component.SafeClick
import io.github.imanfz.jetpackcomposedoc.ui.component.get

/**
 * Safe click
 *
 * @param enabled
 * @param onClickLabel
 * @param role
 * @param onClick
 * @return [Modifier]
 */
@Suppress("unused")
fun Modifier.safeClick(
    enabled: Boolean = true,
    onClickLabel: String? = null,
    role: Role? = null,
    onClick: () -> Unit
) = composed(
    inspectorInfo = debugInspectorInfo {
        name = "clickable"
        properties["enabled"] = enabled
        properties["onClickLabel"] = onClickLabel
        properties["role"] = role
        properties["onClick"] = onClick
    }
) {
    val safeClick = remember { SafeClick.get() }

    Modifier.clickable(
        enabled = enabled,
        onClickLabel = onClickLabel,
        onClick = { safeClick.processEvent { onClick() } },
        role = role,
        indication = LocalIndication.current,
        interactionSource = remember { MutableInteractionSource() }
    )
}