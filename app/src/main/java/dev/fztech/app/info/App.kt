package dev.fztech.app.info

import android.app.Application
import dev.fztech.app.info.utils.helper.ConsentInfoManager
import dev.fztech.app.info.utils.helper.FirebaseManager
import dev.fztech.app.info.utils.helper.OpenAppAdManager

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        ConsentInfoManager(this)
        OpenAppAdManager(this)
        FirebaseManager(this)
    }
}