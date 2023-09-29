package dev.fztech.app.info.ui.screen

import android.content.pm.PackageInfo
import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.core.graphics.drawable.toBitmapOrNull
import coil.compose.AsyncImage
import dev.fztech.app.info.R
import dev.fztech.app.info.ui.component.BannerAdView
import dev.fztech.app.info.ui.component.DefaultSpacer
import dev.fztech.app.info.ui.theme.AppInfoTheme
import dev.fztech.app.info.ui.theme.Dimens
import dev.fztech.app.info.utils.extenstions.toDate
import io.github.imanfz.jetpackcomposedoc.ui.component.SafeClick
import io.github.imanfz.jetpackcomposedoc.ui.component.get

@Composable
fun DetailScreen(data: PackageInfo, onBackPressed: () -> Unit) {
    val pm = LocalContext.current.packageManager
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
        ConstraintLayout(modifier = Modifier
            .fillMaxSize()
            .padding(it)
        ) {
            val (detail, ads) = createRefs()

            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = Dimens.XLarge)
                    .constrainAs(detail) {
                        top.linkTo(parent.top)
                        bottom.linkTo(ads.top,  Dimens.Small)
                        height = Dimension.fillToConstraints
                    }
            ) {
                data.apply {
                    Box(modifier = Modifier.align(Alignment.CenterHorizontally)) {
                        AsyncImage(
                            model = applicationInfo.loadIcon(pm),
                            contentDescription = "icon",
                            modifier = Modifier
                                .size(60.dp)
                                .align(Alignment.Center),
                            contentScale = ContentScale.FillBounds
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
            BannerAdView(Modifier.constrainAs(ads) {
                bottom.linkTo(parent.bottom)
            })
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

@Preview(showBackground = true)
@Composable
fun IconPreview() {
    AppInfoTheme {
        Box(modifier = Modifier) {
            Image(
                bitmap = ImageBitmap.imageResource(id = R.drawable.logo),
                contentDescription = "icon",
                modifier = Modifier
                    .size(60.dp)
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(Color.Black)
            )
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.baseline_save_alt_24),
                contentDescription = "download",
                modifier = Modifier
                    .size(Dimens.Default)
                    .align(Alignment.BottomEnd)
                    .background(Color.LightGray, CircleShape)
                    .padding(3.dp)
                    .clickable {

                    }
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ItemDetailPreview(@PreviewParameter(LoremIpsum::class) text: String) {
    AppInfoTheme {
        ItemDetail(
            title = text.split(" ").subList(0,2).joinToString(" "),
            desc = text.split(" ").subList(0,4).joinToString(" "))
    }
}

@Preview(showBackground = true)
@Composable
fun DoubleItemDetailPreview(@PreviewParameter(LoremIpsum::class) text: String) {
    AppInfoTheme {
        DoubleItemDetail(
            firstTitle = text.split(" ").subList(0,2).joinToString(" "),
            firstDesc = text.split(" ").subList(0,4).joinToString(" "),
            secondTitle = text.split(" ").subList(0,2).joinToString(" "),
            secondDesc = text.split(" ").subList(0,4).joinToString(" ")
        )
    }
}