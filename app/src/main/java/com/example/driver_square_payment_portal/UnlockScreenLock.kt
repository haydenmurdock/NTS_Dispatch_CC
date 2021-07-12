package com.example.driver_square_payment_portal

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import java.lang.Exception

@Suppress("DEPRECATION")
class UnlockScreenLock: Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            val w = window
            w.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
            w.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            val h = Handler()
            h.postDelayed(finishTask, 300)
        } catch (e: Exception) {
            println(e)
        }
    }
    private val finishTask =  Runnable {
        run {
            try {
                finish()
            }
            catch (e: Exception) {
                println(e)
            }
        }
    }
} // end of class WakeUp.
