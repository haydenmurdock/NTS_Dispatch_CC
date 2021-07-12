package com.example.driver_square_payment_portal.fragments

import android.content.Context
import androidx.lifecycle.ViewModelProvider
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
import android.widget.AdapterView
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import com.amazonaws.amplify.generated.graphql.SavePaymentDetailsMutation
import com.amazonaws.amplify.generated.graphql.UpdateTripMutation
import com.amazonaws.mobile.auth.core.internal.util.ThreadUtils
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.exception.ApolloException
import com.example.driver_square_payment_portal.Helpers.ReceiptHelper
import com.example.driver_square_payment_portal.Helpers.VehicleTripArrayHolder
import com.example.driver_square_payment_portal.R
import com.example.driver_square_payment_portal.fragments.viewModels.SettingsKeyboardViewModel
import com.example.driver_square_payment_portal.internal.ClientFactory
import com.example.driver_square_payment_portal.internal.InjectorUtilites
import com.example.driver_square_payment_portal.internal.ScopedFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.text_message_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.*
import type.SavePaymentDetailsInput
import type.UpdateTripInput
import java.io.IOException
import java.lang.Error
import java.util.*

class TextMessageFragment : ScopedFragment(){
    var vehicleId = ""
    var tripId = ""
    var paymentType = ""
    private var tripNumber = 0
    private var tripTotal = 00.00
    private var enteredPhoneNumber = ""
    private var transactionId = ""
    private var updatedPhoneNumber = ""
    private var countryListIsShowing = false
    private var internationalNumber = false
    private var inactiveScreenTimer: CountDownTimer? = null
    private val logFragment = "Text Fragment"
    private var mAWSAppSyncClient: AWSAppSyncClient? = null
    private lateinit var keyboardViewModel: SettingsKeyboardViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.text_message_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val keyboardFactory = InjectorUtilites.provideSettingKeyboardModelFactory()
        keyboardViewModel = ViewModelProvider(this, keyboardFactory)
            .get(SettingsKeyboardViewModel::class.java)
        mAWSAppSyncClient = ClientFactory.getInstance(context)
        activity?.actionBar?.hide()
        transactionId = ""
        val tripIdForPayment = VehicleTripArrayHolder.getTripPaymentInfo()?.tripId ?: ""
        getTripDetails()
        startInactivityTimeout()
        view.setOnTouchListener(object : View.OnTouchListener {
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                when (event?.action) {
                    MotionEvent.ACTION_DOWN -> {
                        closeSoftKeyboard()
                        inactiveScreenTimer?.cancel()
                        inactiveScreenTimer?.start()
                    }
                    MotionEvent.ACTION_BUTTON_RELEASE ->{
                        closeSoftKeyboard()
                    }
                }
                return v?.onTouchEvent(event) ?: true
            }
        })
        text_editText.setOnClickListener {
            closeSoftKeyboard()
        }
        text_editText.addTextChangedListener(object : TextWatcher {
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s != null &&
                    s.length > 11) {
                    enableSendTextBtn()
                }

                if (s != null &&
                    s.length < 11){
                    disableSendTextBtn()
                }
                // length is 7 and going forward.
                val isKeyboardGoingForward = keyboardViewModel.isKeyboardGoingForward()

                if (s?.length == 3 && isKeyboardGoingForward){
                    enteredPhoneNumber += "-"
                    text_editText.setText(enteredPhoneNumber)
                }

                if (s?.length == 7 &&
                    isKeyboardGoingForward) {
                    enteredPhoneNumber += "-"
                    text_editText.setText(enteredPhoneNumber)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val isKeyboardGoingForward = keyboardViewModel.isKeyboardGoingForward()
                if (enteredPhoneNumber.endsWith("-") && !isKeyboardGoingForward){
                    deleteLastNumberInEditText()
                }
                text_editText.setSelection(enteredPhoneNumber.length)
            }
        })
        no_receipt_btn_text.setOnTouchListener((View.OnTouchListener { v, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    text_editText.performClick()
                    setTextToGrey(no_receipt_btn_text)
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
        //On touchListeners
        text_receipt_screen_one_btn.setOnTouchListener((View.OnTouchListener { v, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    setTextToGrey(text_receipt_screen_one_btn)
                    keyboardViewModel.keyboardIsGoingForward()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    setTextToWhite(text_receipt_screen_one_btn)
                    v.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }))
        text_receipt_screen_two_btn.setOnTouchListener((View.OnTouchListener { v, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    setTextToGrey(text_receipt_screen_two_btn)
                    keyboardViewModel.keyboardIsGoingForward()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    setTextToWhite(text_receipt_screen_two_btn)
                    v.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }))
        text_receipt_screen_three_btn.setOnTouchListener((View.OnTouchListener { v, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    setTextToGrey(text_receipt_screen_three_btn)
                    keyboardViewModel.keyboardIsGoingForward()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    setTextToWhite(text_receipt_screen_three_btn)
                    v.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }))
        text_receipt_screen_four_btn.setOnTouchListener((View.OnTouchListener { v, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    setTextToGrey(text_receipt_screen_four_btn)
                    keyboardViewModel.keyboardIsGoingForward()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    setTextToWhite(text_receipt_screen_four_btn)
                    v.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }))
        text_receipt_screen_five_btn.setOnTouchListener((View.OnTouchListener { v, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    setTextToGrey(text_receipt_screen_five_btn)
                    keyboardViewModel.keyboardIsGoingForward()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    setTextToWhite(text_receipt_screen_five_btn)
                    v.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }))
        text_receipt_screen_six_btn.setOnTouchListener((View.OnTouchListener { v, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    setTextToGrey(text_receipt_screen_six_btn)
                    keyboardViewModel.keyboardIsGoingForward()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    setTextToWhite(text_receipt_screen_six_btn)
                    v.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }))
        text_receipt_screen_seven_btn.setOnTouchListener((View.OnTouchListener { v, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    setTextToGrey(text_receipt_screen_seven_btn)
                    keyboardViewModel.keyboardIsGoingForward()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    setTextToWhite(text_receipt_screen_seven_btn)
                    v.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }))
        text_receipt_screen_eight_btn.setOnTouchListener((View.OnTouchListener { v, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    setTextToGrey(text_receipt_screen_eight_btn)
                    keyboardViewModel.keyboardIsGoingForward()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    setTextToWhite(text_receipt_screen_eight_btn)
                    v.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }))
        text_receipt_screen_nine_btn.setOnTouchListener((View.OnTouchListener { v, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    setTextToGrey(text_receipt_screen_nine_btn)
                    keyboardViewModel.keyboardIsGoingForward()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    setTextToWhite(text_receipt_screen_nine_btn)
                    v.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }))
        text_receipt_screen_zero_btn.setOnTouchListener((View.OnTouchListener { v, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    setTextToGrey(text_receipt_screen_zero_btn)
                    keyboardViewModel.keyboardIsGoingForward()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    setTextToWhite(text_receipt_screen_zero_btn)
                    v.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }))
        text_receipt_screen_backspace_btn.setOnTouchListener((View.OnTouchListener { v, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    keyboardViewModel.keyboardIsGoingBackward()
                    text_receipt_screen_backspace_btn.setImageDrawable(
                        ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_backspace_arrow_grey))
                    true
                }
                MotionEvent.ACTION_UP -> {
                    text_receipt_screen_backspace_btn.setImageDrawable(
                        ContextCompat.getDrawable(
                        requireContext(),
                        R.drawable.ic_backspace_arrow_white))
                    v.performClick()
                    true
                }
                else -> {
                    false
                }
            }
        }))

        send_text_btn_receipt.setOnTouchListener((View.OnTouchListener { v, event ->
            when(event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    if(send_text_btn_receipt.isEnabled){
                        setTextToGrey(send_text_btn_receipt)
                    }
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
        listView.onItemClickListener =
            AdapterView.OnItemClickListener { parent, view, position, id -> // value of item that is clicked
                listView.visibility = View.GONE
            }

        //on clickListeners phoneKeyboard
        text_receipt_screen_one_btn.setOnClickListener {
            val buttonValue = "1"
            addNumberInEditText(buttonValue)
        }
        text_receipt_screen_two_btn.setOnClickListener {
            val buttonValue = "2"
            addNumberInEditText(buttonValue)
        }
        text_receipt_screen_three_btn.setOnClickListener {
            val buttonValue = "3"
            addNumberInEditText(buttonValue)
        }
        text_receipt_screen_four_btn.setOnClickListener {
            val buttonValue = "4"
            addNumberInEditText(buttonValue)
        }
        text_receipt_screen_five_btn.setOnClickListener {
            val buttonValue = "5"
            addNumberInEditText(buttonValue)
        }
        text_receipt_screen_six_btn.setOnClickListener {
            val buttonValue = "6"
            addNumberInEditText(buttonValue)
        }
        text_receipt_screen_seven_btn.setOnClickListener {
            val buttonValue = "7"
            addNumberInEditText(buttonValue)
        }
        text_receipt_screen_eight_btn.setOnClickListener {
            val buttonValue = "8"
            addNumberInEditText(buttonValue)
        }
        text_receipt_screen_nine_btn.setOnClickListener {
            val buttonValue = "9"
            addNumberInEditText(buttonValue)
        }
        text_receipt_screen_zero_btn.setOnClickListener {
            val buttonValue = "0"
            addNumberInEditText(buttonValue)
        }
        text_receipt_screen_backspace_btn.setOnClickListener {
            text_receipt_screen_backspace_btn.isEnabled = false
            if (enteredPhoneNumber.isNotEmpty()) {
                deleteLastNumberInEditText()
            }
            text_receipt_screen_backspace_btn.isEnabled = true
        }
        //on clickListeners for other buttons
        no_receipt_btn_text.setOnClickListener {
            toThankYou()
        }
        send_text_btn_receipt.setOnClickListener {
            if(paymentType == "cash" ||
                paymentType == "CASH"){
                updatePaymentDetails(transactionId, tripNumber, vehicleId,mAWSAppSyncClient!!, paymentType, tripId)
            } else {
                sendTextReceipt()
            }
        }
        back_btn_text_receipt.setOnClickListener {
            backToEmailOrText()
        }
    }

    private fun getTripDetails(){
        vehicleId = VehicleTripArrayHolder.getTripPaymentInfo()?.vehicleId ?: ""
        tripId = VehicleTripArrayHolder.getTripPaymentInfo()?.tripId?: ""
        tripNumber = VehicleTripArrayHolder.getTripPaymentInfo()?.tripNumber?.toInt() ?: -0
        val tripPriceArgs = arguments?.getFloat("tripTotal")
        val paymentTypeArgs = arguments?.getString("paymentType")
        val previousPhoneNumber = arguments?.getString("previousPhoneNumber")
        if (tripPriceArgs != null && paymentTypeArgs != null) {
            tripTotal = tripPriceArgs.toDouble()
            paymentType = paymentTypeArgs
        }
        if(paymentType == "CASH"){
            transactionId = UUID.randomUUID().toString()
        }
        if(!previousPhoneNumber.isNullOrBlank()){
            val autoFillPhoneNumber = formatPreviousPhoneNumber(previousPhoneNumber)
            if(autoFillPhoneNumber.length == 10){
                val firstPart = autoFillPhoneNumber.substring(0, 3)
                val middlePart = autoFillPhoneNumber.substring(3, 6)
                val lastPart = autoFillPhoneNumber.substring(6, 10)
                val newFullNumber = "$firstPart-$middlePart-$lastPart"
                enteredPhoneNumber = newFullNumber
                text_editText.setText(newFullNumber)
                text_editText.setSelection(newFullNumber.length)
                enableSendTextBtn()
                text_receipt_screen_backspace_btn.isEnabled = true
                keyboardViewModel.keyboardIsGoingForward()
            }
        }
    }
    private fun formatPreviousPhoneNumber(phoneNumber: String): String {
        val updatedNumber = if(phoneNumber[0].toString() == "1"){
            phoneNumber.removePrefix("1")
        } else {
            phoneNumber
        }
        val onlyNumbers = updatedNumber.digitsOnly()
        if(onlyNumbers.length == 10){
            return onlyNumbers
        }
        val phoneNumberLength = onlyNumbers.length
        return onlyNumbers.removeRange(10, phoneNumberLength)
    }
    private fun String.digitsOnly(): String{
        val regex = Regex("[^0-9]")
        return regex.replace(this, "")
    }
    private fun enableSendTextBtn(){
        if(send_text_btn_receipt != null){
            send_text_btn_receipt.isEnabled = true
        }
    }
    private fun disableSendTextBtn(){
        if(send_text_btn_receipt != null){
            send_text_btn_receipt.isEnabled = false
        }
    }
    private fun setTextToGrey(button: Button){
        button.setTextColor((ContextCompat.getColor(requireContext(), R.color.grey)))
    }
    private fun setTextToWhite(button: Button){
        button.setTextColor((ContextCompat.getColor(requireContext(), R.color.whiteTextColor)))
    }
    private fun addNumberInEditText(enteredNumber: String){
        enteredPhoneNumber += enteredNumber
        text_editText.setText(enteredPhoneNumber)
    }
    private fun deleteLastNumberInEditText(){
        if(enteredPhoneNumber.last().toString() == "-"){
            updatedPhoneNumber = enteredPhoneNumber.dropLast(2)
        } else {
            updatedPhoneNumber = enteredPhoneNumber.dropLast(1)
        }
        enteredPhoneNumber = updatedPhoneNumber
        text_editText.setText(enteredPhoneNumber)
        text_receipt_screen_backspace_btn.isEnabled = true
    }
    private fun closeSoftKeyboard(){
        val imm =
            requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.windowToken, 0)
       com.example.driver_square_payment_portal.Helpers.ViewHelper.hideSystemUI(requireActivity())
    }
    private fun startInactivityTimeout(){
        inactiveScreenTimer = object: CountDownTimer(60000, 1000) {
            // this is set to 1 min and will finish if a new trip is started.
            override fun onTick(millisUntilFinished: Long) {

            }
            override fun onFinish() {
                no_receipt_btn_text.performClick()
            }
        }.start()
    }
    private fun sendTextReceipt(){
        //callBackViewModel.setTransactionId(transactionId)
        val phoneNumber = text_editText.text.toString()
        val countryCode = country_code_editText.text.toString()
        var combinedNumber = ""
        if (countryCode != "1"){
            internationalNumber = true
        }
        combinedNumber = if(internationalNumber){
            //formatting of international numbers if it works.
            //combinedNumber = countryCode + "." + phoneNumber
            countryCode + phoneNumber
        } else {
            countryCode + phoneNumber
        }
        val trimmedPhoneNumber = combinedNumber.replace("-", "").trim()

        updateCustomerPhoneNumber(trimmedPhoneNumber)
        toConfirmation()
    }

    private fun updateCustomerPhoneNumber(phoneNumber:String) = launch(Dispatchers.IO) {
            updateCustomerPhoneNumber(vehicleId, phoneNumber,mAWSAppSyncClient!!,tripId)
    }
    //Makes sure phone is correctly formatted.

    private fun updatePaymentDetails(transactionId: String, tripNumber: Int, vehicleId: String, appSyncClient: AWSAppSyncClient, paymentMethod: String, tripId: String){
        Log.i("Payment AWS", "Trying to send the following to Payment AWS. TransactionId: $transactionId, tripNumber: $tripNumber, vehicleId: $vehicleId, paymentMethod: $paymentMethod, tripID: $tripId")
        val updatePaymentInput = SavePaymentDetailsInput.builder().paymentId(transactionId).tripNbr(tripNumber).vehicleId(vehicleId).paymentMethod(paymentMethod).tripId(tripId).build()

        appSyncClient.mutate(SavePaymentDetailsMutation.builder().parameters(updatePaymentInput).build())?.enqueue(mutationCallbackPaymentDetails)
    }
    private val mutationCallbackPaymentDetails = object : GraphQLCall.Callback<SavePaymentDetailsMutation.Data>() {
        override fun onResponse(response: com.apollographql.apollo.api.Response<SavePaymentDetailsMutation.Data>) {
            if(!response.hasErrors()){
                launch(Dispatchers.IO) {
                    sendTextReceipt()
                }
            }

        }

        override fun onFailure(e: ApolloException) {
        }
    }
    private fun updateCustomerPhoneNumber(vehicleId: String, custPhoneNumber: String, appSyncClient: AWSAppSyncClient, tripId: String){
        val updatePaymentTypeInput = UpdateTripInput.builder().vehicleId(vehicleId).tripId(tripId).custPhoneNbr(custPhoneNumber).build()
        appSyncClient.mutate(UpdateTripMutation.builder().parameters(updatePaymentTypeInput).build())
            ?.enqueue(mutationCustomerPhoneNumberCallback )
    }

    private val mutationCustomerPhoneNumberCallback = object : GraphQLCall.Callback<UpdateTripMutation.Data>() {
        override fun onResponse(response: com.apollographql.apollo.api.Response<UpdateTripMutation.Data>) {
            val tripId = VehicleTripArrayHolder.getTripPaymentInfo()?.tripId ?: ""
            val transactionId = ""
            val custPhoneNumber = response.data()?.updateTrip()?.custPhoneNbr()

            if(!response.hasErrors()){
                if(response.data() != null){
                    launch(Dispatchers.IO) {
                   ReceiptHelper.sendReceiptInfoToAWS(tripId, "Card", transactionId, custPhoneNumber, null, false)
                    }
                }
            }
        }
        override fun onFailure(e: ApolloException) {
        }
    }


    //Navigation
    private fun toConfirmation() = launch(Dispatchers.Main){
        inactiveScreenTimer?.cancel()
        val action = TextMessageFragmentDirections.actionTextMessageFragmentToConfirmationFragment(text_editText.text.toString(),"Text",tripTotal.toFloat())
            .setEmailOrPhoneNumber(text_editText.text.toString())
            .setTripTotal(tripTotal.toFloat())
            .setReceiptType("Text")
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if (navController.currentDestination?.id == R.id.textMessageFragment){
            navController.navigate(action)
        }

    }

    private fun toThankYou(){
        inactiveScreenTimer?.cancel()
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if (navController.currentDestination?.id == R.id.textMessageFragment){
            navController.navigate(R.id.action_textMessageFragment_to_thankYouFragment)
        }
    }
    private fun backToEmailOrText(){
        inactiveScreenTimer?.cancel()
        val action = TextMessageFragmentDirections.actionTextMessageFragmentToEmailOrTextFragment(tripTotal.toFloat())
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if (navController.currentDestination?.id == R.id.textMessageFragment){
            navController.navigate(action)
        }
    }

    override fun onDestroy() {
        inactiveScreenTimer?.cancel()
        Log.i("Text Receipt", "Inactivity timer canceled")
        super.onDestroy()
    }

}