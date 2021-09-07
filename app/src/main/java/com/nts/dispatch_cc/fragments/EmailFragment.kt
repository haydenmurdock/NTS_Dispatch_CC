package com.nts.dispatch_cc.fragments

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.amazonaws.amplify.generated.graphql.SavePaymentDetailsMutation
import com.amazonaws.amplify.generated.graphql.UpdateTripMutation
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.nts.dispatch_cc.helpers.LoggerHelper
import com.nts.dispatch_cc.helpers.ReceiptHelper
import com.nts.dispatch_cc.helpers.VehicleTripArrayHolder
import com.nts.dispatch_cc.helpers.ViewHelper
import com.nts.dispatch_cc.R
import com.nts.dispatch_cc.internal.ClientFactory
import com.nts.dispatch_cc.internal.ScopedFragment
import kotlinx.android.synthetic.main.email_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import type.SavePaymentDetailsInput
import type.UpdateTripInput

class EmailFragment: ScopedFragment() {
    private var mAWSAppSyncClient: AWSAppSyncClient? = null
    private var vehicleId = ""
    private var tripId = ""
    private var tripTotal = 0.0
    private var tripNumber = 0
    private var paymentType = ""
    private var transactionId = ""
    private var email = ""
    private var inactiveScreenTimer: CountDownTimer? = null
    private val logFragment = "Email Receipt"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.email_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val greyColor = ContextCompat.getColor(requireContext(),R.color.grey)
        showSoftKeyboard()
        activity?.actionBar?.hide()
        mAWSAppSyncClient = ClientFactory.getInstance(context)
        paymentType = "CARD"
        tripId = VehicleTripArrayHolder.getTripPaymentInfo()?.tripId ?: ""
        tripNumber = VehicleTripArrayHolder.getTripPaymentInfo()?.tripNumber ?: 0
        vehicleId = VehicleTripArrayHolder.getTripPaymentInfo()?.vehicleId ?: ""
        tripTotal = VehicleTripArrayHolder.getAmountPassedToSquare() ?: 0.0
        transactionId = VehicleTripArrayHolder.getTripPaymentInfo()?.receiptPaymentInfo?.transactionId ?: ""
        startInactivityTimeout()
        email_editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {}
            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int) {
            }
            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int) {
                if (s.contains("@") && s.contains(".")) {
                    enableSendEmailBtn()
                }
                if(inactiveScreenTimer != null){
                    inactiveScreenTimer?.cancel()
                    Log.i("Email Receipt", "inactivity time was canceled and started")
                    inactiveScreenTimer?.start()
                }
            }
        })
        email_editText.setOnFocusChangeListener { _, hasFocus ->
            if(!hasFocus){
                closeSoftKeyboard()
            }
        }
        view.setOnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                 inactiveScreenTimer?.cancel()
                    inactiveScreenTimer?.start()
                }
            }
            v?.onTouchEvent(event) ?: true
        }
        send_email_btn_receipt.setOnTouchListener((View.OnTouchListener { v, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    send_email_btn_receipt.setTextColor(greyColor)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    v.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }))
        no_receipt_btn_receipt_email.setOnTouchListener((View.OnTouchListener { v, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    no_receipt_btn_receipt_email.setTextColor(greyColor)
                    true
                }
                MotionEvent.ACTION_UP -> {
                    v.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }))

        send_email_btn_receipt.setOnClickListener {
            email = email_editText.text.toString().trim()
            updatePaymentDetails(transactionId, tripNumber,vehicleId, mAWSAppSyncClient!!,paymentType,tripId, requireActivity())
        }

        back_btn_email_receipt.setOnClickListener {
           backToEmailOrText()
        }
        no_receipt_btn_receipt_email.setOnClickListener {
            toThankYou()
        }
    }

    private fun showSoftKeyboard() {
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(email_editText, InputMethodManager.SHOW_IMPLICIT)
        email_editText.requestFocus()
    }

    private fun closeSoftKeyboard(){
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
        ViewHelper.hideSystemUI(requireActivity())
    }

    private fun enableSendEmailBtn(){
        if(send_email_btn_receipt != null){
            send_email_btn_receipt.isEnabled = true
        }
    }

    @SuppressLint("MissingPermission")
    private fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected

    }
    private fun sendEmail(email: String){
        if(vehicleId.isNotEmpty()&&
            tripId.isNotEmpty()&&
            paymentType.isNotEmpty()&&
            tripTotal > 0){
            updateCustomerEmail(email)
        } else {
            Log.i("Email Receipt", "Did not update customer email because one of the following was empty or zero. vehicle ID: $vehicleId, tripId: $tripId paymentType: $paymentType, tripTotal: $tripTotal")
        }
        toConfirmation()
    }

    private fun updateCustomerEmail(email:String) = launch(Dispatchers.IO) {
        updateCustomerEmail(vehicleId, email,mAWSAppSyncClient!!,tripId)
    }

    private fun updateCustomerEmail(vehicleId: String, custEmail: String, appSyncClient: AWSAppSyncClient, tripId: String){
        val updatePaymentTypeInput = UpdateTripInput.builder().vehicleId(vehicleId).tripId(tripId).custEmail(custEmail).build()
        if(isOnline(requireContext())){
            appSyncClient.mutate(UpdateTripMutation.builder().parameters(updatePaymentTypeInput).build())
                ?.enqueue(mutationCustomerEmailCallback )
        } else {
            Log.i("Email Receipt", "Not connected to internet")
        }
    }

    private val mutationCustomerEmailCallback = object : GraphQLCall.Callback<UpdateTripMutation.Data>() {
        override fun onResponse(response: Response<UpdateTripMutation.Data>) {
             Log.i("Email Receipt", "Meter Table Updated Customer Email}")
            val tripId = VehicleTripArrayHolder.getTripPaymentInfo()?.tripId ?: ""
            val updatedCustEmail = response.data()?.updateTrip()?.custEmail()

            if (response.hasErrors()) {
                Log.i("Email Receipt", "Response from Aws had errors so did not send email")
                return
            }
            if (!response.hasErrors()) {
                    launch(Dispatchers.IO) {
                        ReceiptHelper.sendReceiptInfoToAWS(tripId, paymentType, null, updatedCustEmail, false, requireActivity())
                    }
            }
        }
        override fun onFailure(e: ApolloException) {
            Log.e("Error", "There was an issue updating the MeterTable: $e")
        }
    }

    private fun updatePaymentDetails(transactionId: String, tripNumber: Int, vehicleId: String, appSyncClient: AWSAppSyncClient, paymentMethod: String, tripId: String, act: Activity){
        LoggerHelper.writeToLog("Updating Payment details for email receipt- transactionId: $transactionId, tripNumber: $tripNumber, vehicleId: $vehicleId, paymentMethod: $paymentMethod, tripId: $tripId", "Receipt", act)
        val updatePaymentInput = SavePaymentDetailsInput.builder().paymentId(transactionId).tripNbr(tripNumber).vehicleId(vehicleId).paymentMethod(paymentMethod).tripId(tripId).build()
        appSyncClient.mutate(SavePaymentDetailsMutation.builder().parameters(updatePaymentInput).build())
            ?.enqueue(mutationCallbackPaymentDetails)
    }
    private val mutationCallbackPaymentDetails = object : GraphQLCall.Callback<SavePaymentDetailsMutation.Data>() {
        override fun onResponse(response: Response<SavePaymentDetailsMutation.Data>) {

            if(!response.hasErrors()){
                launch(Dispatchers.IO) {
                    sendEmail(email)
                }
            }
        }

        override fun onFailure(e: ApolloException) {
        }
    }

    private fun startInactivityTimeout(){
        inactiveScreenTimer = object: CountDownTimer(60000, 1000) {
            // this is set to 1 min and will finish if a new trip is started.
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {

                   if (no_receipt_btn_receipt_email != null) {
                    LoggerHelper.writeToLog("Inactivity Timer finished on email screen", "Receipt", this@EmailFragment.requireActivity())
                    no_receipt_btn_receipt_email.performClick()
                }
            }
        }.start()
    }

    override fun onResume() {
        super.onResume()
        ViewHelper.hideSystemUI(requireActivity())
        activity?.window?.decorView?.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN

    }

    private fun toConfirmation() = launch(Dispatchers.Main) {

        val action = EmailFragmentDirections.actionEmailFragmentToConfirmationFragment(email_editText.text.toString(),"Email",tripTotal.toFloat())
            .setEmailOrPhoneNumber(email_editText.text.toString())
            .setTripTotal(tripTotal.toFloat())
            .setReceiptType("Email")
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if (navController.currentDestination?.id == R.id.emailFragment){
            navController.navigate(action)
        }
    }

    private fun toThankYou(){
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if (navController.currentDestination?.id == R.id.emailFragment){
            navController.navigate(R.id.action_emailFragment_to_thankYouFragment)
        }

    }

    private fun backToEmailOrText(){
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if (navController.currentDestination?.id == R.id.emailFragment){
            navController.navigate(R.id.action_emailFragment_to_emailOrTextFragment)
        }
    }

}