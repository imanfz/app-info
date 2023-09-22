package dev.fztech.app.info.utils.helper

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.ump.ConsentDebugSettings
import com.google.android.ump.ConsentForm
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import dev.fztech.app.info.R
import dev.fztech.app.info.utils.isDevelopment
import java.util.concurrent.atomic.AtomicBoolean

class ConsentInfoManager(application: Application) : Application.ActivityLifecycleCallbacks  {

    companion object {
        const val LOG_TAG = "CONSENT INFO MANAGER"
        const val ADS_TAG = "MOBILE ADS"
    }

    private val application: Application
    private val consentInformation: ConsentInformation
    private val consentRequestParameters: ConsentRequestParameters
    // Use an atomic boolean to initialize the Google Mobile Ads SDK and load ads once.
    private var isMobileAdsInitializeCalled = AtomicBoolean(false)

    init {
        this.application = application
        application.registerActivityLifecycleCallbacks(this)

        // Set tag for under age of consent. false means users are not under age
        // of consent.
        consentRequestParameters = ConsentRequestParameters.Builder().apply {
            setAdMobAppId(application.getString(R.string.app_ad_id))
            setTagForUnderAgeOfConsent(false)
            if (isDevelopment()) {
                setConsentDebugSettings(
                    ConsentDebugSettings
                        .Builder(application)
                        .addTestDeviceHashedId(AdRequest.DEVICE_ID_EMULATOR)
                        .build()
                )
            }
        }.build()

        consentInformation = UserMessagingPlatform.getConsentInformation(application)

        if (isDevelopment()) {
            MobileAds.setRequestConfiguration(
                RequestConfiguration.Builder()
                    .setTestDeviceIds(
                        listOf(AdRequest.DEVICE_ID_EMULATOR)
                    ).build()
            )
        }
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
        Log.d(LOG_TAG, "request consent info")
        requestConsentInfo(activity)

        // Check if you can initialize the Google Mobile Ads SDK in parallel
        // while checking for new consent information. Consent obtained in
        // the previous session can be used to request ads.
        if (consentInformation.canRequestAds()) {
            initializeMobileAdsSdk()
        }
    }

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {
        if (isDevelopment()) consentInformation.reset()
    }

    private fun requestConsentInfo(activity: Activity) {
        consentInformation.requestConsentInfoUpdate(
            activity,
            consentRequestParameters,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(
                    activity
                ) { loadAndShowError ->
                    // Consent gathering failed.
                    loadAndShowError?.apply {
                        Log.w(
                            LOG_TAG,
                            String.format(
                                "%d: %s",
                                errorCode,
                                message
                            )
                        )
                    }

                    // Consent has been gathered.
                    if (consentInformation.canRequestAds()) {
                        initializeMobileAdsSdk()
                    }
                }
            },
            { requestConsentError ->
                // Consent gathering failed.
                Log.e(
                    LOG_TAG,
                    String.format(
                        "%d: %s",
                        requestConsentError.errorCode,
                        requestConsentError.message
                    )
                )
            })
    }

    private fun initializeMobileAdsSdk() {
        // Log the Mobile Ads SDK version.
        Log.d(ADS_TAG, "Google Mobile Ads SDK Version: " + MobileAds.getVersion())

        if (isMobileAdsInitializeCalled.get()) {
            return
        }
        isMobileAdsInitializeCalled.set(true)

        MobileAds.initialize(application) {
            Log.d(ADS_TAG, "MobileAds initialize status: $it")
            val statusMap = it.adapterStatusMap
            for (adapterClass in statusMap.keys) {
                val status = statusMap[adapterClass]
                Log.d(
                    ADS_TAG, String.format(
                    "Adapter name: %s, Description: %s, Latency: %d",
                    adapterClass, status?.description, status?.latency)
                )
            }
        }
    }

    fun showPrivacyOptionsForm(
        activity: Activity,
        onConsentFormDismissedListener: ConsentForm.OnConsentFormDismissedListener
    ) {
        UserMessagingPlatform.showPrivacyOptionsForm(activity, onConsentFormDismissedListener)
    }
}