package dev.fztech.app.info.ui.component.app_update

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.testing.FakeAppUpdateManager
import com.google.android.play.core.ktx.AppUpdateResult
import com.google.android.play.core.ktx.requestUpdateFlow
import dev.fztech.app.info.utils.isDevelopment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

@Composable
internal fun rememberMutableInAppUpdateState(): InAppUpdateState {

    val context = LocalContext.current
    val appUpdateManager = if (isDevelopment())
        FakeAppUpdateManager(context).apply {
            setUpdateAvailable(2)
            setUpdatePriority(3)
            setTotalBytesToDownload(20000000L)
        }
    else AppUpdateManagerFactory.create(context)

    val scope = rememberCoroutineScope()

    val inAppUpdateState = remember {
        MutableInAppUpdateState(scope, appUpdateManager)
    }

    return inAppUpdateState
}

internal class MutableInAppUpdateState(
    private val scope: CoroutineScope,
    private val appUpdateManager: AppUpdateManager
) : InAppUpdateState {

    private var _appUpdateResult by mutableStateOf<AppUpdateResult>(AppUpdateResult.NotAvailable)

    override val appUpdateResult: AppUpdateResult
        get() = _appUpdateResult

    init {
        scope.launch {
            appUpdateManager.requestUpdateFlow().catch { e ->
                Log.e("InAppUpdate", "error", e)
            }.collect {
                _appUpdateResult = it
            }
        }
    }

    override fun tryUpdate() {
        scope.launch {
            appUpdateManager.requestUpdateFlow().catch { e ->
                Log.e("InAppUpdate", "error", e)
            }.collect {
                _appUpdateResult = it
            }
        }
    }
}