package com.example.driver_square_payment_portal.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.example.driver_square_payment_portal.Helpers.BroadcastHelper
import com.example.driver_square_payment_portal.Helpers.VehicleTripArrayHolder
import com.example.driver_square_payment_portal.R
import com.example.driver_square_payment_portal.internal.ClientFactory
import com.example.driver_square_payment_portal.internal.ScopedFragment
import kotlinx.android.synthetic.main.confirmation_fragment.*
import java.text.DecimalFormat
import java.util.*

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
        var transactionId = ""
        if(transactionId == ""){
            transactionId = UUID.randomUUID().toString()
        }
        getTripDetails()
        changeEndTripInternalBool()
        backToDriverAppTimer.start()
    }

    private fun getTripDetails(){
        val messageTypeArgs = arguments?.getString("emailOrPhoneNumber")
        val tripTotalArgs = VehicleTripArrayHolder.getTripPaymentInfo()?.pimPayAmount ?: 0
        val receiptTypeArgs = arguments?.getString("receiptType")
        tipAmount = VehicleTripArrayHolder.tripPayment?.receiptPaymentInfo?.tipAmt ?: 0.0
        if(messageTypeArgs != null &&
            tripTotalArgs != null &&
            receiptTypeArgs != null){
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
            tripTotal = tripTotalArgs.toFloat()
            formatUIForTripTotal(tripTotalArgs.toFloat())
            if (receiptTypeArgs == "Text"){
                confirmation_type_textView.text = "$receiptTypeArgs message sent. You're all done!"
            } else {
                confirmation_type_textView.text = "$receiptTypeArgs sent. You're all done!"
            }

        }
    }
    private fun formatUIForTripTotal(tripTotal: Float){
        // WIll need to fix this logic
        val originalTripTotal = tripTotal - tipAmount
        if (tripTotal < 10.00){
            val formattedTripTotal = tripTotalDFUnderTen.format(tripTotal)
            val formattedTipAmount = tripTotalDFUnderTen.format(tipAmount)
            val formattedOriginalTipAmount = tripTotalDFUnderTen.format(originalTripTotal)
            if(tipAmount > 0){
                tripTotal_textView.text = "$$formattedTripTotal($formattedOriginalTipAmount + $formattedTipAmount)"
            } else {
                tripTotal_textView.text = "$$formattedTripTotal"
            }

        } else {
            if (tipAmount > 0){
                val formattedTripTotal = decimalFormatter.format(tripTotal)
                val formattedOriginalTipAmount = decimalFormatter.format(originalTripTotal)
                val formattedTipAmount = decimalFormatter.format(tipAmount)
                tripTotal_textView.text = "$$formattedTripTotal($formattedOriginalTipAmount + $formattedTipAmount)"
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
