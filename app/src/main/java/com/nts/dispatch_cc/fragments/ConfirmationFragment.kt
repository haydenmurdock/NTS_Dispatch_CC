package com.nts.dispatch_cc.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.nts.dispatch_cc.Helpers.BroadcastHelper
import com.nts.dispatch_cc.Helpers.VehicleTripArrayHolder
import com.nts.dispatch_cc.R
import com.nts.dispatch_cc.internal.ClientFactory
import com.nts.dispatch_cc.internal.ScopedFragment
import kotlinx.android.synthetic.main.confirmation_fragment.*
import java.text.DecimalFormat

class ConfirmationFragment : ScopedFragment() {
    private var vehicleId = ""
    private var tipAmount = 0.0
    private var mAWSAppSyncClient: AWSAppSyncClient? = null
    var tripTotal:Float = 0.0.toFloat()
    private val decimalFormatter = DecimalFormat("####00.00")
    private val tripTotalDFUnderTen = DecimalFormat("###0.00")

    private val backToDriverAppTimer = object: CountDownTimer(10000, 1000) {
        override fun onTick(millisUntilFinished: Long) {
            if(confirmation_type_textView == null){
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
        return inflater.inflate(R.layout.confirmation_fragment, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAWSAppSyncClient = ClientFactory.getInstance(context)
        vehicleId = VehicleTripArrayHolder.tripPayment?.vehicleId ?: ""
        getTripDetails()
        changeEndTripInternalBool()
        backToDriverAppTimer.start()
    }

    @SuppressLint("SetTextI18n")
    private fun getTripDetails(){
        val messageTypeArgs = arguments?.getString("emailOrPhoneNumber")
        val receiptTypeArgs = arguments?.getString("receiptType")
            if(receiptTypeArgs != null && messageTypeArgs != null){
            if (receiptTypeArgs == "Text"){
                if (phoneNumberGroup != null){
                    phoneNumberGroup.visibility = View.VISIBLE
                }
                val (areaCode, firstThree, lastFour) = maskPhoneNumber(messageTypeArgs)
                areaCode_textView.text = areaCode
                middle_three_phone_number_textView.text = " $firstThree"
                if(last_four_phone_number_textView != null){
                    last_four_phone_number_textView.visibility = View.VISIBLE
                    last_four_phone_number_textView.text = " - $lastFour"
                }
            }

            if(receiptTypeArgs == "Email"){
                if (phoneNumberGroup != null){
                    phoneNumberGroup.visibility = View.GONE
                }
                email_detail_textView.visibility =  View.VISIBLE
                email_detail_textView.text = maskEmailType(messageTypeArgs)
            }
            formatUIForTripTotal()
            if (receiptTypeArgs == "Text"){
                confirmation_type_textView.text = "$receiptTypeArgs message sent. You're all done!"
            } else {
                confirmation_type_textView.text = "$receiptTypeArgs sent. You're all done!"
            }

        }
    }
    @SuppressLint("SetTextI18n")
    private fun formatUIForTripTotal(){
        // WIll need to fix this logic

        val tipAmt = VehicleTripArrayHolder.getPaymentInfo()?.tipAmount ?: 0.0
        val pimPayAmount = VehicleTripArrayHolder.getTripPaymentInfo()?.pimPayAmount ?: 0.0
        val tripTotal =  pimPayAmount + tipAmt

        if (tripTotal < 10.00){
            val formattedTripTotal = tripTotalDFUnderTen.format(tripTotal)
            val formattedTipAmount = tripTotalDFUnderTen.format(tipAmt)
            val formattedOriginalTripAmount = tripTotalDFUnderTen.format(pimPayAmount)
            if(tipAmt > 0){
                tripTotal_textView.text = "$$formattedTripTotal ($formattedOriginalTripAmount + $formattedTipAmount)"
            } else {
                tripTotal_textView.text = "$$formattedTripTotal"
            }

        } else {
            if (tipAmt > 0){
                val formattedTripTotal = tripTotalDFUnderTen.format(tripTotal)
                val formattedTipAmount = tripTotalDFUnderTen.format(tipAmt)
                val formattedOriginalTripAmount = tripTotalDFUnderTen.format(pimPayAmount)
                tripTotal_textView.text = "$$formattedTripTotal ($formattedOriginalTripAmount + $formattedTipAmount)"
            } else {
                val formattedTripTotal = decimalFormatter.format(tripTotal)
                tripTotal_textView.text = "$$formattedTripTotal"
            }

        }
    }

    private fun maskEmailType(message: String):String{
        return message
    }

    private fun maskPhoneNumber(phoneNumber: String): Triple<String, String, String>{
        val trimmedPhoneNumber = phoneNumber.replace("-", "")
        val lastFour = trimmedPhoneNumber.substring(trimmedPhoneNumber.length - 4)
        val updatedNumber = trimmedPhoneNumber.dropLast(4)
        val middleThree = updatedNumber.substring(updatedNumber.length - 3)
        val areaCode = trimmedPhoneNumber.dropLast(7)
        return Triple(areaCode,middleThree,lastFour)
    }
    private fun changeEndTripInternalBool(){
    }
    private fun restartApp() {
        BroadcastHelper.sendBroadcast(requireActivity())
    }

    override fun onDestroy() {
        super.onDestroy()
        backToDriverAppTimer.cancel()
    }
}
