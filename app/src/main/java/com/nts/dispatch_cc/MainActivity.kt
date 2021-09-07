package com.nts.dispatch_cc


import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.nts.dispatch_cc.helpers.LoggerHelper
import com.nts.dispatch_cc.helpers.VehicleTripArrayHolder
import com.nts.dispatch_cc.helpers.ViewHelper
import com.nts.dispatch_cc.model.ReceiptPaymentInfo
import com.nts.dispatch_cc.receivers.DriverAppReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext


open class MainActivity : AppCompatActivity(), CoroutineScope {
    private lateinit var mJob: Job
    override val coroutineContext: CoroutineContext
        get() = mJob + Dispatchers.Main
    private var loggingTimer: CountDownTimer? = null
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
        mJob = Job()
        navigationController = Navigation.findNavController(this, R.id.nav_host_fragment)
        UnlockScreenLock()
        val vehicleId = this.intent.getStringExtra("vehicleId") ?: ""
        val driverId: Int = this.intent.getIntExtra("driverId", 0)
        val tripId: String = this.intent.getStringExtra("tripId") ?: ""
        val tripNumber: Int = this.intent.getIntExtra("tripNumber", 0)
        val pimPayAmount: Double = this.intent.getDoubleExtra("pimPayAmount", 0.0)
        val owedPrice: Double = this.intent.getDoubleExtra("owedPrice", 0.0)
        val airportFee: Double = this.intent.getDoubleExtra("airportFee", 0.0)
        val discountAmt: Double = this.intent.getDoubleExtra("discountAmt", 0.0)
        val discountPercent: Double = this.intent.getDoubleExtra("discountPercent", 0.0)
        val toll: Double = this.intent.getDoubleExtra("toll", 0.0)
        val destLat: Double = this.intent.getDoubleExtra("destLat", 0.0)
        val destLng: Double = this.intent.getDoubleExtra("destLng", 0.0 )
        val logging: Boolean = this.intent.getBooleanExtra("logging", true)
        if(vehicleId != "" && logging) {
            LoggerHelper.turnOnLogs()
            LoggerHelper.writeToLog(
                "vehicleId: $vehicleId," +
                        " driverId: $driverId," +
                        " tripId: $tripId," +
                        " tripNumber: $tripNumber," +
                        " pimPayAmount: $pimPayAmount," +
                        " owedPrice: $owedPrice, " +
                        "airportFee" + " $airportFee," +
                        " discountAmt:" + " $discountAmt, " +
                        "discountPercent:$discountPercent, " +
                        "toll: $toll," +
                        "destLat: $destLat," +
                        "destLng: $destLng," +
                        "logging: $logging", "Broadcast", this
            )
            startTimerToSendLogsToAWS(vehicleId, this.applicationContext)
        } else {
            LoggerHelper.turnOffLogs()
        }

        val receiptPaymentInfo = ReceiptPaymentInfo(tripId, pimPayAmount, owedPrice, 0.0, 0.0, airportFee, discountAmt,toll,discountPercent, destLat, destLng, null)
        VehicleTripArrayHolder.setReceiptPaymentInfo(receiptPaymentInfo)
        VehicleTripArrayHolder.setTripPaymentInfo(tripId, driverId, vehicleId, tripNumber, null, pimPayAmount, receiptPaymentInfo)
    }

    private fun startTimerToSendLogsToAWS(vehicleId: String, context: Context){
        loggingTimer = object: CountDownTimer(30000, 15000){
            override fun onTick(millisUntilFinished: Long) {}
            override fun onFinish() {
                launch(Dispatchers.IO) {
                    LoggerHelper.sendLogToAWS(vehicleId)
                }
                startTimerToSendLogsToAWS(vehicleId, context)
            }
        }.start()
    }
    private fun stopLogTimer(){
        if(loggingTimer != null){
            Log.i("LOGGER", "Log Timer Stopped")
            loggingTimer!!.cancel()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK &&
            navigationController.currentDestination?.id != R.id.startUpPermissionFragment) {
            true
        } else super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(DriverAppReceiver());
    }

    override fun onResume() {
        super.onResume()
        if(navigationController.currentDestination?.id != R.id.startUpPermissionFragment){

        }
        actionBar?.hide()
        supportActionBar?.hide()
    }

    override fun onPause() {
        super.onPause()
        if(navigationController.currentDestination?.id != R.id.startUpPermissionFragment){
            ViewHelper.hideSystemUI(this)
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if(navigationController.currentDestination?.id != R.id.startUpPermissionFragment){
            ViewHelper.hideSystemUI(this)
        }
    }


}


