package dev.fztech.app.info.ui.screen

import android.content.pm.PackageInfo
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import dev.fztech.app.info.ui.component.DefaultSpacer
import dev.fztech.app.info.ui.theme.Dimens
import dev.fztech.app.info.utils.extenstions.toDate

@Composable
fun DetailScreen(navController: NavController, data: PackageInfo) {
    val pm = LocalContext.current.packageManager

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Surface(shadowElevation = Dimens.XSmall) {
                Row(Modifier.fillMaxWidth()) {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBack,
                            contentDescription = "close icon",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                        Text(
                            text = data.applicationInfo.loadLabel(pm).toString(),
                            modifier = Modifier.padding(horizontal = Dimens.Default),
                            maxLines = 1
                        )
                    }
                }
            }
        }
    ) {
        Column(Modifier.padding(it)) {
            data.apply {
                AsyncImage(
                    model = applicationInfo.loadIcon(pm),
                    contentDescription = null,
                    modifier = Modifier
                        .size(60.dp)
                        .align(Alignment.CenterHorizontally)
                )
                DefaultSpacer()
                ItemDetail(title = "Label", desc = applicationInfo.loadLabel(pm).toString())
                ItemDetail(title = "Package Name", desc = packageName)
                ItemDetail(title = "Version Name", desc = versionName)
                @Suppress("DEPRECATION")
                ItemDetail(title = "Version Code", desc = "${
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) longVersionCode
                    else versionCode
                }")
                DoubleItemDetail(
                    firstTitle = "First Install", firstDesc = firstInstallTime.toDate(),
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
    Row(horizontalArrangement = Arrangement.SpaceBetween) {
        Column(Modifier.padding(Dimens.Default, Dimens.Small)) {
            Details(title = firstTitle, desc = firstDesc)
        }
        Column {
            Details(title = secondTitle, desc = secondDesc)
        }
    }
    Divider()
}