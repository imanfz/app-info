package dev.fztech.app.info.ui.screen

import android.content.pm.PackageInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import dev.fztech.app.info.ui.component.DefaultSpacer
import dev.fztech.app.info.ui.theme.AppInfoTheme
import dev.fztech.app.info.ui.theme.Dimens
import io.github.imanfz.jetpackcomposedoc.ui.component.SafeClick
import io.github.imanfz.jetpackcomposedoc.ui.component.get
import kotlinx.coroutines.launch

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    list: List<PackageInfo>,
    onItemClick: (PackageInfo) -> Unit
) {
    val state = rememberLazyListState()
    val scope = rememberCoroutineScope()
    // Show the button if the first visible item is past
    // the first item. We use a remembered derived state to
    // minimize unnecessary compositions
    val showButton by remember {
        derivedStateOf {
            state.firstVisibleItemIndex > 0
        }
    }

    if (list.isNotEmpty())
        Box {
            LazyColumn(
                modifier = modifier.fillMaxWidth(),
                state = state
            ) {
                items(list) {
                    ItemView(data = it, onItemClick = onItemClick)
                    Divider()
                }
            }

            AnimatedVisibility(
                visible = showButton,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                ScrollToTopButton {
                    scope.launch {
                        state.animateScrollToItem(0)
                    }
                }
            }
        }
    else EmptyScreen()
}

@Composable
fun ItemView(data: PackageInfo, onItemClick: (PackageInfo) -> Unit) {
    val pm = LocalContext.current.packageManager
    val safeClick = remember { SafeClick.get() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { safeClick.processEvent { onItemClick(data) } }
            .padding(Dimens.Default, Dimens.Small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = data.applicationInfo.loadIcon(pm),
            contentDescription = "icon",
            modifier = Modifier
                .size(Dimens.XXLarge),
            contentScale = ContentScale.FillBounds
        )
        DefaultSpacer()
        Column {
            Text(
                text = data.applicationInfo.loadLabel(pm).toString(),
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(text = data.applicationInfo.packageName, maxLines = 1)
            Text(text = "Version: ${data.versionName ?: "-"}", fontSize = 12.sp, maxLines = 1)
        }

    }
}

@Composable
fun ScrollToTopButton(onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .padding(bottom = Dimens.XXXLarge, end = Dimens.Default),
        Alignment.BottomEnd
    ) {
        IconButton(
            onClick = { onClick() },
            modifier = Modifier
                .shadow(1.dp, shape = CircleShape, true)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        ) {
            Icon(
                Icons.Filled.KeyboardArrowUp,
                "Scroll to top",
                tint = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Preview (showBackground = true)
@Composable
fun ItemPreview(@PreviewParameter(LoremIpsum::class) text: String) {
    AppInfoTheme {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.Default, Dimens.Small),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Info,
                contentDescription = null,
                modifier = Modifier.size(Dimens.XXXLarge)
            )
            DefaultSpacer()
            Column {
                Text(
                    text = text.split(" ").subList(0,2).joinToString(" "),
                    fontWeight = FontWeight.Bold,
                )
                Text(text = text.split(" ").subList(0,4).joinToString(".").lowercase())
                Text(text = "Version: 1.4", fontSize = 12.sp)
            }

        }
    }
}