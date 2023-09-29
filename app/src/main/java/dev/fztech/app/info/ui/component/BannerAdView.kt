package dev.fztech.app.info.ui.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.view.doOnLayout
import androidx.lifecycle.Lifecycle
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import dev.fztech.app.info.R
import dev.fztech.app.info.utils.extenstions.rememberLifecycleEvent
import dev.fztech.app.info.utils.isSmallScreen

@Composable
fun BannerAdView(modifier: Modifier = Modifier) {
    val lifeCycleState = rememberLifecycleEvent()
    val tag = "BannerAdView"
    val isSmallDevice = isSmallScreen()
    if (LocalInspectionMode.current) {
        Box(modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center) {
            Text(
                modifier = Modifier
                    .width(320.dp)
                    .height((if (isSmallDevice) 50 else 100).dp)
                    .background(Color.Red),
                textAlign = TextAlign.Center,
                color = White,
                text = "AdMob Banner",
            )
        }
    } else {
        if (lifeCycleState == Lifecycle.Event.ON_RESUME) {
            Log.d(tag, "ON_RESUME")
            AndroidView(
                modifier = modifier.fillMaxWidth(),
                factory = { context ->
                    AdView(context).apply {
                        setAdSize(
                            if (isSmallDevice) AdSize.BANNER
                            else AdSize.LARGE_BANNER
                        )
                        // add listener
                        adListener = object : AdListener() {
                            override fun onAdClicked() {
                                // Code to be executed when the user clicks on an ad.
                                Log.d(tag, "onAdClicked")
                            }

                            override fun onAdClosed() {
                                // Code to be executed when the user is about to return
                                // to the app after tapping on an ad.
                                Log.d(tag, "onAdClosed")
                            }

                            override fun onAdFailedToLoad(adError: LoadAdError) {
                                // Code to be executed when an ad request fails.
                                Log.d(tag, "onAdFailedToLoad: ${adError.message}")
                            }

                            override fun onAdImpression() {
                                // Code to be executed when an impression is recorded
                                // for an ad.
                                Log.d(tag, "onAdImpression")
                            }

                            override fun onAdLoaded() {
                                // Code to be executed when an ad finishes loading.
                                Log.d(tag, "onAdLoaded")
                            }

                            override fun onAdOpened() {
                                // Code to be executed when an ad opens an overlay that
                                // covers the screen.
                                Log.d(tag, "onAdOpened")
                            }
                        }
                    }
                }, update = {
                    Log.d(tag, "onUpdate")
                    it.apply {
                        adUnitId = context.getString(R.string.banner_ad_id)
                        doOnLayout {
                            // calling load ad to load our ad.
                            loadAd(AdRequest.Builder().build())
                        }
                    }
                }
            )
        }
    }
}

@Preview
@Composable
fun Preview() {
    BannerAdView()
}