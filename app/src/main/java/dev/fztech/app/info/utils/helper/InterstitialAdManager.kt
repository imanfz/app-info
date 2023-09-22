package dev.fztech.app.info.utils.helper

import android.content.Context
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import dev.fztech.app.info.utils.extenstions.findActivity

class InterstitialAdManager {
    private var interstitialAd: InterstitialAd? = null

    fun loadInterstitial(context: Context) {
        InterstitialAd.load(
            context,
            "ca-app-pub-3940256099942544/1033173712", //Change this with your own AdUnitID!
            AdRequest.Builder().build(),
            object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    interstitialAd = null
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    this@InterstitialAdManager.interstitialAd = interstitialAd
                }
            }
        )
    }

    fun showInterstitial(context: Context, onAdDismissed: () -> Unit) {
        val activity = context.findActivity()

        if (interstitialAd != null) {
            interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdFailedToShowFullScreenContent(e: AdError) {
                    interstitialAd = null
                }

                override fun onAdDismissedFullScreenContent() {
                    interstitialAd = null

                    loadInterstitial(context)
                    onAdDismissed()
                }
            }
            interstitialAd?.show(activity)
        }
    }

    fun removeInterstitial() {
        interstitialAd?.fullScreenContentCallback = null
        interstitialAd = null
    }
}