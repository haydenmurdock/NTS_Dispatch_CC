package com.example.driver_square_payment_portal.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.example.driver_square_payment_portal.Helpers.VehicleTripArrayHolder
import com.example.driver_square_payment_portal.Helpers.ViewHelper
import com.example.driver_square_payment_portal.R
import com.example.driver_square_payment_portal.internal.ScopedFragment
import kotlinx.android.synthetic.main.email_or_text_fragment.*
import kotlinx.android.synthetic.main.tip_screen_fragment.*
import java.text.DecimalFormat

class EmailOrTextFragment : ScopedFragment() {
    private val tripTotalDF = DecimalFormat("####00.00")
    private val tripTotalDFUnderTen = DecimalFormat("###0.00")


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.email_or_text_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUI()

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
        val tipAmountFromSquare = VehicleTripArrayHolder.getAmountPassedToSquare() ?: 0.0
        updateTripTotalTextField(tipAmountFromSquare)
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