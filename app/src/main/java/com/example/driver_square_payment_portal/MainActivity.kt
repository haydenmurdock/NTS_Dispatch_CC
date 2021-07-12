package com.example.driver_square_payment_portal

import android.content.IntentFilter
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.driver_square_payment_portal.Helpers.ViewHelper
import com.example.driver_square_payment_portal.receivers.DriverAppReceiver


class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var navigationController: NavController
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        supportActionBar?.hide()
        navigationController = Navigation.findNavController(this, R.id.nav_host_fragment)
        UnlockScreenLock()
        val intentFilter = IntentFilter("com.example.driver_square_payment_portal")
        registerReceiver(DriverAppReceiver(), intentFilter)

    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK) {
            //preventing default implementation previous to android.os.Build.VERSION_CODES.ECLAIR
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(DriverAppReceiver());
    }

    override fun onResume() {
        super.onResume()
        actionBar?.hide()
        supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        ViewHelper.hideSystemUI(this)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        ViewHelper.hideSystemUI(this)
    }
}


