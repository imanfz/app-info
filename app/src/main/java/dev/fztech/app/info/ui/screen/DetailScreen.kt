package dev.fztech.app.info.ui.screen

import android.Manifest
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import coil.compose.AsyncImage
import dev.fztech.app.info.ui.component.BannerAdView
import dev.fztech.app.info.ui.component.DefaultSpacer
import dev.fztech.app.info.ui.component.PermissionDialog
import dev.fztech.app.info.ui.component.TopBar
import dev.fztech.app.info.ui.theme.AppInfoTheme
import dev.fztech.app.info.ui.theme.Dimens
import dev.fztech.app.info.utils.extenstions.findActivity
import dev.fztech.app.info.utils.extenstions.hasPermission
import dev.fztech.app.info.utils.extenstions.saveApk
import dev.fztech.app.info.utils.extenstions.saveBitmapImage
import dev.fztech.app.info.utils.extenstions.toDate
import io.github.imanfz.jetpackcomposedoc.ui.component.SafeClick
import io.github.imanfz.jetpackcomposedoc.ui.component.get
import kotlinx.coroutines.launch

@Composable
fun DetailScreen(data: PackageInfo, onBackPressed: () -> Unit) {
    val context = LocalContext.current
    val pm = context.packageManager
    val safeClick = remember { SafeClick.get() }
    val hasPermission = rememberSaveable { mutableStateOf(false) }
    val deniedForever = rememberSaveable { mutableStateOf(false) }
    val showMenu = rememberSaveable { mutableStateOf(false) }
    val showDialogPermission = rememberSaveable { mutableStateOf(false) }
    val storagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        Manifest.permission.READ_MEDIA_IMAGES
    else Manifest.permission.WRITE_EXTERNAL_STORAGE
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()) { isGranted ->
        hasPermission.value = isGranted
        if (!isGranted) {
            deniedForever.value = !ActivityCompat.shouldShowRequestPermissionRationale(
                context.findActivity(),
                storagePermission
            )
        }
    }

    LaunchedEffect("detail") {
        launch {
            hasPermission.value = context.hasPermission(storagePermission)
            deniedForever.value = !ActivityCompat.shouldShowRequestPermissionRationale(
                context.findActivity(),
                storagePermission
            )
        }
    }


    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopBar(
                title = "Detail",
                hasNavigationIcon = true,
                onBack = {
                    safeClick.processEvent { onBackPressed() }
                },
                actionButton = {
                    IconButton(onClick = { showMenu.value = true }) {
                        Icon(imageVector = Icons.Filled.Menu, contentDescription = "menu")
                    }
                    DropdownMenu(
                        expanded = showMenu.value,
                        onDismissRequest = {
                            showMenu.value = false
                        }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Text(text = "Save Icon")
                            },
                            onClick = {
                                safeClick.processEvent {
                                    showMenu.value = false
                                    if (hasPermission.value) {
                                        val fileName = data.applicationInfo.loadLabel(pm)
                                        val bmp = data.applicationInfo
                                            .loadIcon(pm)
                                            .toBitmap()
                                        context.saveBitmapImage(bmp, fileName.toString())
                                    } else {
                                        if (deniedForever.value) showDialogPermission.value =
                                            true
                                        else permissionLauncher.launch(storagePermission)
                                    }
                                }
                            }
                        )

                        DropdownMenuItem(
                            text = {
                                Text(text = "Extract Apk")
                            },
                            onClick = {
                                safeClick.processEvent {
                                    showMenu.value = false
                                    context.saveApk(
                                        data.applicationInfo.sourceDir,
                                        "${data.applicationInfo.loadLabel(pm).toString().replace("/", "-")}_${data.versionName}"
                                    )
                                }
                            }
                        )

                        /*DropdownMenuItem(
                            text = {
                                Text(text = "Uninstall")
                            },
                            onClick = {
                                safeClick.processEvent {
                                    showMenu.value = false
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                        pm.packageInstaller.apply {
                                            val params = PackageInstaller.SessionParams(PackageInstaller.SessionParams.MODE_FULL_INSTALL)
                                            params.setAppPackageName(data.applicationInfo.packageName)
                                            var session = 0
                                            try {
                                                session = createSession(params)
                                            } catch (e: Exception) {
                                                e.printStackTrace()
                                            }
                                            uninstall(data.packageName, PendingIntent.getBroadcast(context, session, Intent(Intent.ACTION_MAIN), PendingIntent.FLAG_IMMUTABLE).intentSender)
                                        }
                                    } else {
                                        Intent(Intent.ACTION_UNINSTALL_PACKAGE).apply {
                                            this.data =
                                                Uri.fromParts("package", data.packageName, null)
                                            uninstallLauncher.launch(this)
                                        }
                                    }
                                }
                            }
                        )*/
                    }
                }
            )
        }
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(it)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(top = Dimens.XLarge)
                    .align(Alignment.TopStart)
            ) {
                data.apply {
                    AsyncImage(
                        model = applicationInfo.loadIcon(pm),
                        contentDescription = "icon",
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally),
                        contentScale = ContentScale.FillBounds
                    )
//                    Text(text = "${}")
                    DefaultSpacer()
                    ItemDetail(title = "Label", desc = applicationInfo.loadLabel(pm).toString())
                    ItemDetail(title = "Package Name", desc = packageName)
                    @Suppress("DEPRECATION")
                    DoubleItemDetail(
                        firstTitle = "Version Name",
                        firstDesc = versionName,
                        secondTitle = "Version Code",
                        secondDesc = "${
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) longVersionCode.toInt()
                            else versionCode
                        }")
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
                    ItemDetail(title = "Status", desc = if (applicationInfo.enabled) "Enable" else "Disable")
                    ItemDetail(title = "Data Directory", desc = applicationInfo.dataDir  ?: "")
                    ItemDetail(title = "Source Directory", desc = applicationInfo.sourceDir  ?: "")
                    ItemDetailScrollable(
                        title = "Requested Permissions",
                        desc = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS)?.requestedPermissions?.joinToString("\n") ?: "")
                    ItemDetailScrollable(
                        title = "Shared Library Files",
                        desc = applicationInfo.sharedLibraryFiles?.joinToString("\n") ?: ""
                    )
                }
            }
            BannerAdView(Modifier.align(Alignment.BottomCenter))
        }

        if (showDialogPermission.value) {
            PermissionDialog(
                title = "Missing Permission",
                message = "Storage permission is currently disabled. To be able to save the icon you need to accept this permission.",
                actionButtonText = "Setting",
                onAction = {
                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        this.data = Uri.fromParts("package", context.packageName, null)
                        context.startActivity(this)
                    }
                    showDialogPermission.value = false
                },
                onDismiss = {
                    showDialogPermission.value = false
                }
            )
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
    if (desc.isNotEmpty()) {
        Column(Modifier.padding(Dimens.Default, Dimens.Small)) {
            Details(title, desc)
        }

        Divider()
    }
}

@Composable
fun ItemDetailScrollable(title: String, desc: String) {
    if (desc.isNotEmpty()) {
        Column(Modifier.padding(Dimens.Default, Dimens.Small)) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 16.sp
            )
            LazyRow {
                items(1) {
                    Text(text = desc)
                }
            }
        }

        Divider()
    }
}

@Composable
fun DoubleItemDetail(
    firstTitle: String, firstDesc: String,
    secondTitle: String, secondDesc: String
) {
    Row(
        Modifier
            .height(IntrinsicSize.Min)
            .fillMaxWidth()
            .padding(Dimens.Default, Dimens.Small),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (firstDesc.isNotEmpty()) {
            Column(Modifier.weight(1f)) {
                Details(title = firstTitle, desc = firstDesc)
            }
        }
        if (secondDesc.isNotEmpty()) {
            Divider(
                Modifier
                    .fillMaxHeight()
                    .width(DividerDefaults.Thickness)
            )

            Column(Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Details(title = secondTitle, desc = secondDesc)
            }
        }
    }
    Divider()
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