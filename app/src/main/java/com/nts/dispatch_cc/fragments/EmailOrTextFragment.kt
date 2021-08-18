package com.nts.dispatch_cc.fragments

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.nts.dispatch_cc.Helpers.LoggerHelper
import com.nts.dispatch_cc.Helpers.VehicleTripArrayHolder
import com.nts.dispatch_cc.Helpers.ViewHelper
import com.nts.dispatch_cc.R
import com.nts.dispatch_cc.internal.ScopedFragment
import kotlinx.android.synthetic.main.email_or_text_fragment.*
import kotlinx.android.synthetic.main.text_message_fragment.*
import java.text.DecimalFormat

class EmailOrTextFragment : ScopedFragment() {
    private val tripTotalDF = DecimalFormat("####00.00")
    private val tripTotalDFUnderTen = DecimalFormat("###0.00")
    private var inactiveScreenTimer: CountDownTimer? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.email_or_text_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()
        startInactivityTimeout()
        email_btn.setOnClickListener {
            toEmailFragment()
        }
        text_message_btn.setOnClickListener {
            toTextFragment()
        }
        no_receipt_btn.setOnClickListener {
            toThankYouFragment()
        }

    }

    private fun updateUI(){
        activity?.actionBar?.hide()
        ViewHelper.hideSystemUI(requireActivity())
        val tipAmountFromSquare = VehicleTripArrayHolder.getPaymentInfo()?.tipAmount ?: 0.0
        val pimPayAmount = VehicleTripArrayHolder.getTripPaymentInfo()?.pimPayAmount ?: 0.0
        val totalPaid = pimPayAmount + tipAmountFromSquare

        updateTripTotalTextField(totalPaid)
    }
    private fun updateTripTotalTextField(tripTotalEntered: Double){
        if (tripTotalEntered < 10.00){
            val formattedArgs = tripTotalDFUnderTen.format(tripTotalEntered)
            val tripTotalToString = formattedArgs.toString()
            if (amount_text_View != null){
                amount_text_View.text = "$$tripTotalToString"
            }
        }else {
            val formattedArgs = tripTotalDF.format(tripTotalEntered)
            val tripTotalToString = formattedArgs.toString()
            if (amount_text_View != null){
               amount_text_View.text = "$$tripTotalToString"
            }
        }
    }

    private fun startInactivityTimeout(){
        inactiveScreenTimer = object: CountDownTimer(60000, 1000) {
            // this is set to 1 min and will finish if a new trip is started.
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {

                if (no_receipt_btn != null) {
                    LoggerHelper.writeToLog("Inactivity Timer finished on text or email screen", "Receipt", this@EmailOrTextFragment.requireActivity())
                    no_receipt_btn.performClick()
                }
            }
        }.start()
    }

    // Navigation
    private fun toEmailFragment(){
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if(navController.currentDestination?.id == (R.id.emailOrTextFragment)){
            navController.navigate(R.id.action_emailOrTextFragment_to_emailFragment)
        }
    }

    private fun toTextFragment(){
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if(navController.currentDestination?.id == (R.id.emailOrTextFragment)){
            navController.navigate(R.id.action_emailOrTextFragment_to_textMessageFragment)
        }
    }

    private fun toThankYouFragment(){
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if(navController.currentDestination?.id == (R.id.emailOrTextFragment)){
            navController.navigate(R.id.action_emailOrTextFragment_to_thankYouFragment)
        }
    }
}