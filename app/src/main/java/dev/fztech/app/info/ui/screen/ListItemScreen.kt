package dev.fztech.app.info.ui.screen

import android.content.pm.PackageInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmapOrNull
import dev.fztech.app.info.R
import dev.fztech.app.info.ui.component.DefaultSpacer
import dev.fztech.app.info.ui.theme.AppInfoTheme
import dev.fztech.app.info.ui.theme.Dimens
import io.github.imanfz.jetpackcomposedoc.ui.component.SafeClick
import io.github.imanfz.jetpackcomposedoc.ui.component.get

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    list: List<PackageInfo>,
    onItemClick: (PackageInfo) -> Unit
) {
    val state = rememberLazyListState()

    if (list.isNotEmpty())
        LazyColumn(
            state = state,
            modifier = modifier.fillMaxSize()
        ) {
            items(list) {
                ItemView(data = it, onItemClick = onItemClick)
                Divider()
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
        data.applicationInfo.loadIcon(pm).toBitmapOrNull()?.let {
            Image(
                bitmap = it.asImageBitmap(),
            contentDescription = "icon",
            modifier = Modifier
                .size(Dimens.XXXLarge)
            )
        } ?: run {
            Image(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_broken_image_60),
            contentDescription = "icon",
            modifier = Modifier
                .size(Dimens.XXXLarge)
            )
        }

        DefaultSpacer()
        Column {
            Text(
                text = data.applicationInfo.loadLabel(pm).toString(),
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(text = data.applicationInfo.packageName, maxLines = 1)
            Text(text = "Version: ${data.versionName}", fontSize = 12.sp, maxLines = 1)
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