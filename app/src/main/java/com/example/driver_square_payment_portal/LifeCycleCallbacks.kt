package com.example.driver_square_payment_portal

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup

class LifeCycleCallbacks : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {

    }

    override fun onActivityDestroyed(activity: Activity?) {
        val name = activity?.localClassName
    }

    override fun onActivityPaused(activity: Activity?) {
        val name = activity?.localClassName
        if (name == "com.squareup.ui.main.ApiMainActivity"){
            fullScreenMode(activity)
        }

    }

    override fun onActivityResumed(activity: Activity?) {
        val name = activity?.localClassName
        if (name == "com.squareup.ui.main.ApiMainActivity"){
            fullScreenMode(activity)
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityStopped(activity: Activity?) {

    }
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

}