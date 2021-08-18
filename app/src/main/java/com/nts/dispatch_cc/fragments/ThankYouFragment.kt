package com.nts.dispatch_cc.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.nts.dispatch_cc.Helpers.BroadcastHelper
import com.nts.dispatch_cc.Helpers.PIMMutationHelper
import com.nts.dispatch_cc.Helpers.ReceiptHelper
import com.nts.dispatch_cc.Helpers.VehicleTripArrayHolder
import com.nts.dispatch_cc.R
import com.nts.dispatch_cc.internal.ClientFactory
import com.nts.dispatch_cc.internal.ScopedFragment
import kotlinx.android.synthetic.main.thank_you_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ThankYouFragment : ScopedFragment() {
    private var vehicleId = ""
    private var tripId = ""
    private var tripNumber = 0
    private var transactionId = ""
    private var transactionType = ""
    private var mAWSAppSyncClient: AWSAppSyncClient? = null
   // private val logFragment = "Thank you screen"

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
        tripNumber = VehicleTripArrayHolder.getTripPaymentInfo()?.tripNumber ?: 0
        transactionId = VehicleTripArrayHolder.getPaymentInfo()?.transId ?: UUID.randomUUID().toString()
        transactionType = "card"
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
       ReceiptHelper.sendReceiptInfoToAWS(tripId, transactionType, null, null, true, requireActivity())
    }
    private fun restartApp() {
        BroadcastHelper.sendBroadcast(requireActivity())
    }
}

