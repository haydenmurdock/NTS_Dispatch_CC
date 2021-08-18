package com.nts.dispatch_cc

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.View

class LifeCycleCallbacks : Application.ActivityLifecycleCallbacks {

    private fun fullScreenMode(activity: Activity){
        val decorView = activity.window.decorView
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                View.SYSTEM_UI_FLAG_LOW_PROFILE or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
    }

    override fun onActivityCreated(p0: Activity, p1: Bundle?) {

    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityResumed(p0: Activity) {
        val name = p0.localClassName
        if (name == "com.squareup.ui.main.ApiMainActivity"){
            fullScreenMode(p0)
        }
    }

    override fun onActivityPaused(p0: Activity) {
        val name = p0.localClassName
        if (name == "com.squareup.ui.main.ApiMainActivity"){
            fullScreenMode(p0)
        }
    }

    override fun onActivityStopped(p0: Activity) {

    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {

    }

    override fun onActivityDestroyed(p0: Activity) {

    }

}