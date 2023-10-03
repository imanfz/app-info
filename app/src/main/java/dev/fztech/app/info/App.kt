package dev.fztech.app.info

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import androidx.core.content.edit
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import dev.fztech.app.info.utils.helper.ConsentInfoManager
import dev.fztech.app.info.utils.helper.FirebaseManager
import dev.fztech.app.info.utils.helper.OpenAppAdManager

class App: Application(), ImageLoaderFactory {
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

    override fun newImageLoader(): ImageLoader {
        val darkModeFlags = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK// Retrieve the Mode of the App.
        val isDarkModeOn = darkModeFlags == Configuration.UI_MODE_NIGHT_YES//Check if the Dark Mode is On
        return ImageLoader.Builder(this)
            .crossfade(true)
            .allowHardware(true)
            .diskCachePolicy(CachePolicy.ENABLED)
            .addLastModifiedToFileCacheKey(true)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(this.cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.02)
                    .build()
            }
            .placeholder(if (isDarkModeOn) R.drawable.baseline_image_24_grey else R.drawable.baseline_image_24)
            .error(if (isDarkModeOn) R.drawable.baseline_broken_image_24_grey else R.drawable.baseline_broken_image_24)
            .bitmapFactoryMaxParallelism(3)
            .build()
    }
}