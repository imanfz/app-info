package dev.fztech.app.info

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import dev.fztech.app.info.utils.helper.ConsentInfoManager
import dev.fztech.app.info.utils.helper.FirebaseManager
import dev.fztech.app.info.utils.helper.OpenAppAdManager

class App: Application() {
    override fun onCreate() {
        super.onCreate()
        getSharedPreferences(packageName+"_preferences", Context.MODE_PRIVATE).apply {
            edit(true) {
                // U.S. states privacy laws compliance
                putInt("gad_rdp", 1)
                // IABString
                putString("IABUSPrivacy_String", "1---")
            }
        }
        ConsentInfoManager(this)
        OpenAppAdManager(this)
        FirebaseManager(this)
    }
}