package dev.fztech.app.info.ui.screen

import android.content.pm.PackageInfo
import android.os.Build
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmapOrNull
import dev.fztech.app.info.R
import dev.fztech.app.info.ui.component.DefaultSpacer
import dev.fztech.app.info.ui.component.ExtraLargeSpacer
import dev.fztech.app.info.ui.theme.Dimens
import dev.fztech.app.info.utils.extenstions.toDate
import io.github.imanfz.jetpackcomposedoc.ui.component.SafeClick
import io.github.imanfz.jetpackcomposedoc.ui.component.get

@Composable
fun DetailScreen(data: PackageInfo, onBackPressed: () -> Unit) {
    val pm = LocalContext.current.packageManager
    val scrollState = rememberScrollState()
    val safeClick = remember { SafeClick.get() }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(
                Modifier
                    .fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                shadowElevation = Dimens.XSmall) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        safeClick.processEvent { onBackPressed() }
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "close icon",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    Text(
                        text = "Detail",
                        modifier = Modifier.padding(horizontal = Dimens.Medium),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    ) {
        Log.d("TAG", "DetailScreen: $data")
        Column(
            Modifier
                .padding(it)
                .verticalScroll(scrollState)
        ) {
            ExtraLargeSpacer()
            data.apply {
                applicationInfo.loadIcon(pm).toBitmapOrNull()?.let { bitmap ->
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "icon",
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                } ?: run {
                    Image(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_broken_image_60),
                        contentDescription = "icon",
                        modifier = Modifier
                            .size(60.dp)
                            .align(Alignment.CenterHorizontally)
                    )
                }
                DefaultSpacer()
                ItemDetail(title = "Label", desc = applicationInfo.loadLabel(pm).toString())
                ItemDetail(title = "Package Name", desc = packageName)
                ItemDetail(title = "Version Name", desc = versionName)
                @Suppress("DEPRECATION")
                ItemDetail(title = "Version Code", desc = "${
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) longVersionCode
                    else versionCode
                }")
                ItemDetail(title = "Status", desc = if (applicationInfo.enabled) "Enable" else "Disable")
                DoubleItemDetail(
                    firstTitle = "First Install Time", firstDesc = firstInstallTime.toDate(),
                    secondTitle = "Last Update", secondDesc = lastUpdateTime.toDate()
                )
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    DoubleItemDetail(
                        firstTitle = "Min SDK", firstDesc = applicationInfo.minSdkVersion.toString(),
                        secondTitle = "Target SDK", secondDesc = applicationInfo.targetSdkVersion.toString()
                    )
                } else ItemDetail(title = "Target SDK", desc = applicationInfo.targetSdkVersion.toString())
                ItemDetail(title = "Data Directory", desc = this.applicationInfo.dataDir)
                ItemDetail(title = "Source Directory", desc = this.applicationInfo.sourceDir)
            }
        }
    }
}

@Composable
fun Details(title: String, desc: String) {
    Text(
        text = title,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 16.sp
    )
    Text(text = desc)
}

@Composable
fun ItemDetail(title: String, desc: String) {
    Column(Modifier.padding(Dimens.Default, Dimens.Small)) {
        Details(title, desc)
    }

    Divider()
}

@Composable
fun DoubleItemDetail(
    firstTitle: String, firstDesc: String,
    secondTitle: String, secondDesc: String
) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(Dimens.Default, Dimens.Small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Details(title = firstTitle, desc = firstDesc)
        }
        Column(horizontalAlignment = Alignment.End) {
            Details(title = secondTitle, desc = secondDesc)
        }
    }
    Divider()
}