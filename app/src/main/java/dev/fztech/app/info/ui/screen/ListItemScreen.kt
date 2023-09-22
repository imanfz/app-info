package dev.fztech.app.info.ui.screen

import android.content.pm.PackageInfo
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
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import dev.fztech.app.info.ui.component.DefaultSpacer
import dev.fztech.app.info.ui.theme.Dimens

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    list: List<PackageInfo>
) {
    val state = rememberLazyListState()

    LazyColumn(
        state = state,
        modifier = modifier.fillMaxSize()
    ) {
        items(list) {
            ItemView(data = it)
            Divider()
        }
    }
}

@Composable
fun ItemView(data: PackageInfo) {
    val pm = LocalContext.current.packageManager

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {  }
            .padding(Dimens.Default, Dimens.Small),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val label = data.applicationInfo.loadLabel(pm).toString()
        val packageName = data.applicationInfo.packageName
        val same = label == packageName
            AsyncImage(
                model = data.applicationInfo.loadIcon(pm),
                contentDescription = null,
                modifier = Modifier.size(Dimens.XXXLarge)
            )
            DefaultSpacer()
            Column {
                Text(
                    text = data.applicationInfo.loadLabel(pm).toString(),
                    fontWeight = FontWeight.Bold,
                )
                if (!same)
                    Text(text = data.applicationInfo.packageName)
//                Text(text = "Version: ${
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) data.longVersionCode
//                    else data.versionCode
//                }")
                Text(text = "Version: ${data.versionName}")
            }

    }
}