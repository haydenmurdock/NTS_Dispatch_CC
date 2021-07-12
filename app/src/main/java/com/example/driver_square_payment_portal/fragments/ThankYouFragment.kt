package com.example.driver_square_payment_portal.fragments

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.CountDownTimer
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.example.driver_square_payment_portal.Helpers.BroadcastHelper
import com.example.driver_square_payment_portal.Helpers.PIMMutationHelper
import com.example.driver_square_payment_portal.Helpers.ReceiptHelper
import com.example.driver_square_payment_portal.Helpers.VehicleTripArrayHolder

import com.example.driver_square_payment_portal.R
import com.example.driver_square_payment_portal.internal.ClientFactory
import com.example.driver_square_payment_portal.internal.ScopedFragment
import kotlinx.android.synthetic.main.thank_you_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.util.*

class ThankYouFragment : ScopedFragment() {
    private var vehicleId = ""
    private var tripId = ""
    private var tripNumber = 0
    private var transactionId = ""
    private var transactionType = ""
    private var paymentMethod: String? = null
    private var mAWSAppSyncClient: AWSAppSyncClient? = null
    private val logFragment = "Thank you screen"
    private var tripStatus:String? = ""
    private val payByApp = "PAY_BY_APP"

    private val restartAppTimer = object: CountDownTimer(10000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            if(thank_you_textView == null){
                cancel()
            }
        }
        override fun onFinish() {
            VehicleTripArrayHolder.clearTripValues()
            restartApp()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.thank_you_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAWSAppSyncClient = ClientFactory.getInstance(context)
        tripId = VehicleTripArrayHolder.getTripPaymentInfo()?.tripId ?: ""
        vehicleId = VehicleTripArrayHolder.getTripPaymentInfo()?.vehicleId ?: ""
        tripNumber = VehicleTripArrayHolder.getTripPaymentInfo()?.tripNumber?.toInt() ?: -0
        var transactionId = ""
        if(transactionId == ""){
            transactionId = UUID.randomUUID().toString()
        }
        transactionType = "Card"
        if(transactionId == ""){
            transactionId = UUID.randomUUID().toString()
        }

        sendDriverReceipt()

        thank_you_textView.isVisible = false
        thank_you_textView.isVisible = true
        updatePaymentDetailsAPI()
        restartAppTimer.start()
    }

    private fun updatePaymentDetailsAPI() = launch(Dispatchers.IO){
        // An empty string means that transaction Id has not been from a square payment so they hit cash and did not send a receipt so we need to update
        PIMMutationHelper.updatePaymentDetails(transactionId,tripNumber,vehicleId,mAWSAppSyncClient!!, transactionType, tripId)
    }

    private fun sendDriverReceipt() = launch(Dispatchers.IO){
       ReceiptHelper.sendReceiptInfoToAWS(tripId, transactionType, transactionId, null, null, true)
    }
    private fun restartApp() {
        BroadcastHelper.sendBroadcast(requireActivity())
    }
}

