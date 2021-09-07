package com.nts.dispatch_cc.helpers

import android.app.Activity
import android.view.View
import java.text.SimpleDateFormat
import java.util.*

object ViewHelper {

    fun formatDateUtcIso(date: Date?): String {
            if (date == null) return ""
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val formattedDateString = sdf.format(date)
            val fS = formattedDateString.removeSuffix("+0000")
            return fS.plus("Z")
    }

    fun hideSystemUI(activity: Activity) {
        //  Enables regular immersive mode.
        //   For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        //  Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        activity.window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
    }
}