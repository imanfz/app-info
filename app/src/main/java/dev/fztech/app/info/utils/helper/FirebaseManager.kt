package dev.fztech.app.info.utils.helper

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.perf.ktx.performance
import dev.fztech.app.info.BuildConfig
import dev.fztech.app.info.utils.isDevelopment

class FirebaseManager(application: Application) : Application.ActivityLifecycleCallbacks  {

    private val firebaseAnalytics: FirebaseAnalytics
    private val firebaseCrashlytics: FirebaseCrashlytics

    init {
        Firebase.initialize(application)
        Firebase.performance.isPerformanceCollectionEnabled = !isDevelopment() && !BuildConfig.DEBUG
        firebaseAnalytics = Firebase.analytics.apply {
            setAnalyticsCollectionEnabled(!isDevelopment())
        }
        firebaseCrashlytics = Firebase.crashlytics.apply {
            setCrashlyticsCollectionEnabled(!isDevelopment())
        }
        application.registerActivityLifecycleCallbacks(this)
    }

    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}

    override fun onActivityStarted(activity: Activity) {}

    override fun onActivityResumed(activity: Activity) {}

    override fun onActivityPaused(activity: Activity) {}

    override fun onActivityStopped(activity: Activity) {}

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}

    override fun onActivityDestroyed(activity: Activity) {}
}