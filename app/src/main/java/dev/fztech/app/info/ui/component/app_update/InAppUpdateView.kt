package dev.fztech.app.info.ui.component.app_update

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.clientVersionStalenessDays
import com.google.android.play.core.ktx.isFlexibleUpdateAllowed
import com.google.android.play.core.ktx.isImmediateUpdateAllowed
import com.google.android.play.core.ktx.updatePriority
import dev.fztech.app.info.R
import dev.fztech.app.info.ui.theme.AppInfoTheme
import dev.fztech.app.info.ui.theme.Dimens
import kotlinx.coroutines.launch

private const val TAG = "InAppUpdate"

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun InAppUpdateView(content: @Composable () -> Unit) {
    val snackBarHostState = remember { SnackbarHostState() }
    val inAppUpdateState = rememberInAppUpdateState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) {
        Log.d(TAG,"onActivityResult: code: $it.resultCode" )
        when (it.resultCode) {
            ComponentActivity.RESULT_OK -> {
                Log.d(TAG, context.getString(R.string.update_downloaded))
            }
            ComponentActivity.RESULT_CANCELED -> {
                Log.d(TAG, context.getString(R.string.update_cancelled))
            }
            ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> {
                //  handle update failure
                scope.launch {
                    snackBarHostState.showSnackbar(
                        message = context.getString(R.string.update_app_failed),
                        actionLabel = context.getString(R.string.try_update),
                        duration = SnackbarDuration.Long
                    ).also { it1 ->
                        if (it1 == SnackbarResult.ActionPerformed ) {
                            inAppUpdateState.tryUpdate()
                        }
                    }
                }
                Log.d(TAG, context.getString(R.string.update_app_failed))
            }
        }
    }

    val isImmediateUpdate = remember { mutableStateOf(false) }

    LaunchedEffect(key1 = "InAppUpdate") {
        when (val appUpdateResult = inAppUpdateState.appUpdateResult) {
            is AppUpdateResult.NotAvailable ->
                Log.d(TAG, "Not Available.")
            is AppUpdateResult.Available -> {
                appUpdateResult.updateInfo.apply {
                    Log.d(
                        TAG, "Update Available" +
                                "\n\tversionCode: ${availableVersionCode()}" +
                                "\n\tpriority: $updatePriority" +
                                "\n\tstalenessDays: $clientVersionStalenessDays" +
                                "\n\tflexible: $isFlexibleUpdateAllowed" +
                                "\n\timmediate: $isImmediateUpdateAllowed" +
                                "\n\tin progress: ${updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS}"
                    )

                    if (updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS ||
                        (isImmediateUpdateAllowed && updatePriority >= 4)) {
                        isImmediateUpdate.value = true
                    } else {
                        isImmediateUpdate.value = false
                        scope.launch {
                            snackBarHostState.showSnackbar(
                                message = context.getString(R.string.flexible_update_available),
                                actionLabel = context.getString(R.string.update),
                                withDismissAction = true
                            ).also {
                                if (it == SnackbarResult.ActionPerformed ) {
                                    appUpdateResult.startFlexibleUpdate(launcher)
                                }
                            }
                        }
                    }
                }
            }
            is AppUpdateResult.InProgress -> {
                val progress = (appUpdateResult.installState.bytesDownloaded() * 100 / appUpdateResult.installState.totalBytesToDownload()).toInt()
                Log.d(TAG, "InAppUpdateView: progress $progress")
                scope.launch {
                    snackBarHostState.showSnackbar(
                        message = context.getString(R.string.downloading, "$progress%")
                    )
                }
            }
            is AppUpdateResult.Downloaded -> scope.launch {
                snackBarHostState.showSnackbar(
                    message = context.getString(R.string.update_downloaded),
                    actionLabel = context.getString(R.string.restart),
                    withDismissAction = true
                ).also {
                    if (it == SnackbarResult.ActionPerformed ) {
                        appUpdateResult.completeUpdate()
                    }
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackBarHostState) }
    ) {
        if (isImmediateUpdate.value) {
            UpdateRequired()
        } else {
            content()
        }
    }
}

@Composable
fun UpdateRequired(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(),
    onStartUpdate: () -> Unit = {},
) {
    Column(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primary)
            .testTag("UpdateRequired")
            .padding(contentPadding)
            .padding(horizontal = Dimens.Default, vertical = Dimens.XLarge),
    ) {
        Spacer(modifier = Modifier.weight(1f))

        Image(
            bitmap = ImageBitmap.imageResource(id = R.mipmap.ic_launcher),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(Dimens.XXXLarge)
        )

        Spacer(Modifier.height(Dimens.Large))

        Text(
            text = stringResource(id = R.string.immediate_update_title),
            style = MaterialTheme.typography.titleLarge,
            color = Color.LightGray.copy(alpha = 0.9f)
        )

        Spacer(Modifier.height(Dimens.Medium))

        Text(
            stringResource(id = R.string.immediate_update_message),
            color = Color.LightGray.copy(alpha = 0.9f)
        )

        Spacer(modifier = Modifier.weight(1f))

        ElevatedButton(
            onClick = onStartUpdate,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(stringResource(id = R.string.update))
        }
    }
}

@Preview
@Composable
fun UpdateRequiredPreview() {
    AppInfoTheme {
        UpdateRequired()
    }
}