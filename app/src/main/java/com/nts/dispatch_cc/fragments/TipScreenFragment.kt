package com.nts.dispatch_cc.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.navigation.Navigation
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.nts.dispatch_cc.helpers.*
import com.nts.dispatch_cc.model.PaymentInfo
import com.nts.dispatch_cc.R
import com.nts.dispatch_cc.internal.ClientFactory
import com.nts.dispatch_cc.internal.ScopedFragment
import com.squareup.sdk.reader.ReaderSdk
import com.squareup.sdk.reader.checkout.*
import com.squareup.sdk.reader.core.CallbackReference
import com.squareup.sdk.reader.core.Result
import com.squareup.sdk.reader.core.ResultError
import kotlinx.android.synthetic.main.tip_screen_fragment.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.text.DecimalFormat
import java.util.*
import kotlin.math.roundToLong

class TipScreenFragment : ScopedFragment() {
    private var tripTotal = 00.00
    private var tripTotalReset = 00.00
    private var amountForSquare = 00.00
    private val tripTotalDF = DecimalFormat("####00.00")
    private val tripTotalDFUnderTen = DecimalFormat("###0.00")
    private var tripTotalOption1 = 00.00
    private var tripTotalOption2 = 00.00
    private var tripTotalOption3 = 00.00
    private var tripTotalOption4 = 00.00
    private var fifteenPercent = 00.00
    private var twentyPercent = 00.00
    private var twentyFivePercent = 00.00
    private var thirtyPercent = 00.00
    private var tipAmountPassedToSquare = 00.00
    private var tripTotalBackFromSquare = 00.00
    private var tipPercentPicked = 00.00
    private var mAWSAppSyncClient: AWSAppSyncClient? = null
    private var checkoutCallbackRef: CallbackReference? = null
    private var vehicleId = ""
    private var tripId:String? = null
    private var tripNumber = 0
    var cardInfo = ""
    private var transactionDate: Date? = null
    private var transactionId = ""
    private var paymentSentForSquare = false
    private var driverId: Int? = null
    private var checkedBroadcast: Boolean = false
    private var payment: Double? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.tip_screen_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getArgsFromBroadcastSetup()
        getArgsFromCustomTipScreen()
        updateUI()
        tripTotalReset = tripTotal
        mAWSAppSyncClient = ClientFactory.getInstance(context)
        driverId = VehicleTripArrayHolder.getTripPaymentInfo()?.driverId ?: 0
        tripId = VehicleTripArrayHolder.getTripPaymentInfo()?.tripId ?: ""
        vehicleId = VehicleTripArrayHolder.getTripPaymentInfo()?.vehicleId ?: ""
        tripNumber = VehicleTripArrayHolder.getTripPaymentInfo()?.tripNumber ?: 0

        checkCheckoutManagerRef()
        fifteen_percent_btn.setOnTouchListener((View.OnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    setTextToGreyForButtonPress(fifteen_percent_tip_amount_text_view)
                    setTextToGreyForButtonPress(fifteen_percent_text_view)
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

        twenty_percent_btn.setOnTouchListener((View.OnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    setTextToGreyForButtonPress(twenty_percent_tip_amount_text_view)
                    setTextToGreyForButtonPress(twenty_percent_text_view)
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

        twenty_five_percent_btn.setOnTouchListener((View.OnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    setTextToGreyForButtonPress(twenty_five_percent_tip_amount_text_view)
                    setTextToGreyForButtonPress(twenty_five_percent_text_view)
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

        thirty_percent_btn.setOnTouchListener((View.OnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    setTextToGreyForButtonPress(thirty_percent_tip_amount_text_view)
                    setTextToGreyForButtonPress(thirty_percent_text_view)
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

        customTipAmountBtn.setOnTouchListener((View.OnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    customTipAmountBtn.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            R.color.grey
                        )
                    )
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

        no_tip_btn.setOnTouchListener((View.OnTouchListener { v, event ->
            when (event?.action) {
                MotionEvent.ACTION_DOWN -> {
                    no_tip_btn.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
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

        fifteen_percent_btn.setOnClickListener {
            amountForSquare = tripTotalOption1
            squareCheckout(amountForSquare)
            updateTripTotalTextField(tripTotalOption1)
            tipAmountPassedToSquare = fifteenPercent
          //  callbackViewModel.setTipAmount(tipAmountPassedToSquare)
            tipPercentPicked = if(tripTotal < 10.00){
                0.0
            } else {
                00.15
            }
            lowerAlpha()
        }
        twenty_percent_btn.setOnClickListener {
            amountForSquare = tripTotalOption2
            squareCheckout(amountForSquare)
            updateTripTotalTextField(amountForSquare)
            tipAmountPassedToSquare = twentyPercent
           // callbackViewModel.setTipAmount(tipAmountPassedToSquare)
            tipPercentPicked = if(tripTotal < 10.00){
                0.0
            } else {
                00.20
            }
            lowerAlpha()
        }
        twenty_five_percent_btn.setOnClickListener {
            amountForSquare= tripTotalOption3
            squareCheckout(amountForSquare)
            updateTripTotalTextField(amountForSquare)
            tipAmountPassedToSquare = twentyFivePercent
            //callbackViewModel.setTipAmount(tipAmountPassedToSquare)
            tipPercentPicked = if(tripTotal < 10.00){
                0.0
            } else {
                00.25
            }
            lowerAlpha()

        }
        thirty_percent_btn.setOnClickListener {
            amountForSquare = tripTotalOption4
            squareCheckout(amountForSquare)
            updateTripTotalTextField(amountForSquare)
            tipAmountPassedToSquare = thirtyPercent
           // callbackViewModel.setTipAmount(tipAmountPassedToSquare)
            tipPercentPicked = if(tripTotal < 10.00){
                0.0
            } else {
                00.30
            }
            lowerAlpha()
        }
        customTipAmountBtn.setOnClickListener {
            customTipAmountBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            toCustomTip()
        }
        no_tip_btn.setOnClickListener {
            lowerAlpha()
            squareCheckout(tripTotal)
        }

        back_to_driver_app_from_tip_screen_btn.setOnClickListener {
            BroadcastHelper.sendBroadcast(requireActivity())
        }

    }
    @SuppressLint("SetTextI18n")
    private fun updateUI() {
        if(tripTotal < 10.00){
            if (fifteen_percent_text_view != null &&
                fifteen_percent_tip_amount_text_view != null){
                fifteen_percent_text_view.visibility = View.GONE
                fifteen_percent_tip_amount_text_view.text = "$ 1"
                fifteenPercent = 01.00
                tripTotalOption1 = tripTotal + 1

                fifteen_percent_tip_amount_text_view.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }
            if(twenty_percent_text_view != null &&
                twenty_percent_tip_amount_text_view != null){
                twenty_percent_text_view.visibility = View.GONE
                twenty_percent_tip_amount_text_view.text = "$ 2"
                twentyPercent = 02.00
                tripTotalOption2 = tripTotal + 2

                twenty_percent_tip_amount_text_view.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }

            if(twenty_five_percent_text_view != null &&
                twenty_five_percent_tip_amount_text_view != null){
                twenty_five_percent_text_view.visibility = View.GONE
                twenty_five_percent_tip_amount_text_view.text = "$ 3"
                twentyFivePercent = 03.00
                tripTotalOption3 = tripTotal + 3

                twenty_five_percent_tip_amount_text_view.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }
            if (thirty_percent_text_view != null &&
                thirty_percent_tip_amount_text_view != null){
                thirty_percent_text_view.visibility = View.GONE
                thirty_percent_tip_amount_text_view.text = "$ 4"
                thirtyPercent = 04.00
                tripTotalOption4 = tripTotal + 4

                thirty_percent_tip_amount_text_view.layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
                )
            }

        }else {
            //This logic is for either 00.00 or 0.00 on the tipPercent amount
            fifteenPercent = tripTotal * 0.15
            tripTotalOption1 = fifteenPercent + tripTotal
            if (fifteenPercent < 10.00){
                val fifteenPercentFormatted = tripTotalDFUnderTen.format(fifteenPercent)
                if (fifteen_percent_text_view != null){
                    fifteen_percent_text_view.text = "$$fifteenPercentFormatted"
                }
            }else {
                val fifteenPercentFormatted = tripTotalDF.format(fifteenPercent)
                if (fifteen_percent_text_view != null){
                    fifteen_percent_text_view.text = "$$fifteenPercentFormatted"
                }
            }
            twentyPercent = tripTotal * 0.20
            tripTotalOption2 = twentyPercent + tripTotal
            if (twentyPercent < 10.00){
                val twentyPercentFormatted = tripTotalDFUnderTen.format(twentyPercent)
                if(twenty_percent_text_view != null){
                    twenty_percent_text_view.text = "$$twentyPercentFormatted"
                }
            } else {
                val twentyPercentFormatted = tripTotalDF.format(twentyPercent)
                if(twenty_percent_text_view != null){
                    twenty_percent_text_view.text = "$$twentyPercentFormatted"
                }
            }
            twentyFivePercent = tripTotal * 0.25
            tripTotalOption3 = twentyFivePercent + tripTotal
            if(twentyFivePercent < 10.00){
                val twentyFivePercentFormatted = tripTotalDFUnderTen.format(twentyFivePercent)
                if(twenty_five_percent_text_view != null){
                    twenty_five_percent_text_view.text = "$$twentyFivePercentFormatted"
                }
            } else {
                val twentyFivePercentFormatted = tripTotalDF.format(twentyFivePercent)
                if(twenty_five_percent_text_view != null){
                    twenty_five_percent_text_view.text = "$$twentyFivePercentFormatted"
                }
            }

            thirtyPercent = tripTotal * 0.30
            tripTotalOption4 = thirtyPercent + tripTotal
            if (thirtyPercent < 10.00){
                val thirtyPercentFormatted = tripTotalDFUnderTen.format(thirtyPercent)
                if(thirty_percent_text_view != null) {
                    thirty_percent_text_view.text = "$$thirtyPercentFormatted"
                }
            }else {
                val thirtyPercentFormatted = tripTotalDF.format(thirtyPercent)
                if(thirty_percent_text_view != null){
                    thirty_percent_text_view.text = "$$thirtyPercentFormatted"
                }
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private fun getArgsFromBroadcastSetup(){
        val tripPayment = VehicleTripArrayHolder.getTripPaymentInfo()
        val args = tripPayment?.pimPayAmount
        if(args != null){
            if (args < 10.00){
                val formattedArgs = tripTotalDFUnderTen.format(args)
                tripTotal = formattedArgs.toDouble()
                val tripTotalToString = formattedArgs.toString()
                tip_screen_trip_total_textView.text = "$$tripTotalToString"
            }else {
                val formattedArgs = tripTotalDF.format(args)
                tripTotal = formattedArgs.toDouble()
                val tripTotalToString = formattedArgs.toString()
                tip_screen_trip_total_textView.text = "$$tripTotalToString"
            }
        }
        checkedBroadcast = true
    }
    @SuppressLint("SetTextI18n")
    private fun getArgsFromCustomTipScreen(){
        val hasCustomTipBeingPicked = arguments?.getBoolean("doneButtonTouchedOnCustomTipScreen")
        val tripTotalBeforeTip = arguments?.getFloat("tipScreenTripTotal")
        val tipAmount = arguments?.getFloat("tipChosenFromCustomerTipScreen")
        val hasCustomerPickedPercentage = arguments?.getBoolean("percentagePickedForCustomTip")
        val amountForSquareArgs = tripTotalBeforeTip!! + tipAmount!!.toFloat()
        amountForSquare = tripTotalBeforeTip.toDouble() + tipAmount
        if(tipAmount != 00.00.toFloat()){
            if (amountForSquare < 10.00) {
                tripTotal = tripTotalBeforeTip.toDouble()
                amountForSquare = amountForSquareArgs.toDouble()
                tipAmountPassedToSquare = tipAmount.toDouble()
                val formattedArgs = tripTotalDFUnderTen.format(amountForSquare)
                val tripTotalToString = formattedArgs.toString()
                tip_screen_trip_total_textView.text = "$$tripTotalToString"
            } else {
                tripTotal = tripTotalBeforeTip.toDouble()
                amountForSquare = amountForSquareArgs.toDouble()
                tipAmountPassedToSquare = tipAmount.toDouble()
                val formattedArgs = tripTotalDF.format(amountForSquare)
                val tripTotalToString = formattedArgs.toString()
                tip_screen_trip_total_textView.text = "$$tripTotalToString"
            }

            if(hasCustomTipBeingPicked!!) {
                tipAmountPassedToSquare = tipAmount.toDouble()
                if(hasCustomerPickedPercentage != null && hasCustomerPickedPercentage){
                    tipPercentPicked = tipAmountPassedToSquare/amountForSquare
                }
                squareCheckout(amountForSquare)
                lowerAlpha()
            }
        }


    }
    private fun squareCheckout(checkOutAmount: Double) {
        //Function for square
        checkCheckoutManagerRef()
        val p = checkOutAmount * 100.00
        val checkOutTotal = p.roundToLong()
        val amountMoney = Money(checkOutTotal, CurrencyCode.current())
        val parametersBuilder = CheckoutParameters.newBuilder(amountMoney)
        parametersBuilder.additionalPaymentTypes(AdditionalPaymentType.MANUAL_CARD_ENTRY)
        parametersBuilder.skipReceipt(true)
        // if trip number is 0 we use the last 8 of trip id
        if (tripNumber != 0){
            parametersBuilder.note("[$tripNumber] [$vehicleId] [$driverId]")
        } else {
            parametersBuilder.note(
                "[${
                    tripId?.length?.minus(8)?.let { tripId?.substring(it) }
                }] [$vehicleId] [$driverId]"
            )
        }
        val checkoutManager = ReaderSdk.checkoutManager()
        checkoutManager.startCheckoutActivity(requireContext(), parametersBuilder.build())

    }

    private fun checkCheckoutManagerRef(){
        //With usage errors, we make the checkoutCallbackRef clear. We need to double check before we take a payment
        if(checkoutCallbackRef == null){
            LoggerHelper.writeToLog("checkoutCallbackRef was null at time of payment. Added new checkoutCallbackRef before payment", "SQUARE", requireActivity())
            val checkoutManager = ReaderSdk.checkoutManager()
            checkoutCallbackRef = checkoutManager.addCheckoutActivityCallback(this::onCheckoutResult)
        }
    }
    private fun lowerAlpha() {
        val alpha = 0.5f
        val isClickable = false
        //Changes the alpha
        fifteen_percent_frameLayout.alpha = alpha
        twenty_percent_frameLayout.alpha = alpha
        twenty_five_percent_frameLayout.alpha = alpha
        thirty_percent_frameLayout.alpha = alpha
        customTipAmountBtn.alpha = alpha
        no_tip_btn.alpha = alpha
        give_tip_textView.alpha = alpha
        tip_screen_trip_total_textView.alpha = alpha
        back_to_driver_app_from_tip_screen_btn.alpha = alpha
        //Shows/animates the activity indicator
        square_activity_indicator.animate()
        square_activity_indicator.visibility = View.VISIBLE
        //disables buttons
        fifteen_percent_btn.isEnabled = isClickable
        twenty_percent_btn.isEnabled = isClickable
        twenty_five_percent_btn.isEnabled = isClickable
        thirty_percent_btn.isEnabled = isClickable
        customTipAmountBtn.isEnabled = isClickable
        no_tip_btn.isEnabled = isClickable
        back_to_driver_app_from_tip_screen_btn.isEnabled = isClickable
    }
    private fun raiseAlphaUI(){
        val alpha = 1.0f
        val isClickable = true
        //Changes the alpha
        if(fifteen_percent_frameLayout != null){
            fifteen_percent_frameLayout.alpha = alpha
        }
        if (twenty_percent_frameLayout != null){
            twenty_percent_frameLayout.alpha = alpha
        }
        if(twenty_five_percent_frameLayout != null){
            twenty_five_percent_frameLayout.alpha = alpha
        }
        if(thirty_percent_frameLayout != null){
            thirty_percent_frameLayout.alpha = alpha
        }
        if (customTipAmountBtn != null){
            customTipAmountBtn.alpha = alpha
            customTipAmountBtn.isEnabled = isClickable
        }
        if(no_tip_btn != null){
            no_tip_btn.alpha = alpha
            no_tip_btn.isEnabled = isClickable
        }

        if(give_tip_textView != null){
            give_tip_textView.alpha = alpha
        }
        if (tip_screen_trip_total_textView != null){
            tip_screen_trip_total_textView.alpha = alpha
        }

        if(back_to_driver_app_from_tip_screen_btn != null){
            back_to_driver_app_from_tip_screen_btn.alpha = alpha
            back_to_driver_app_from_tip_screen_btn.isEnabled = isClickable
        }
        //hides the activity indicator
        if (square_activity_indicator != null){
            square_activity_indicator.visibility = View.INVISIBLE
        }
        //enables buttons and sets text views back to white
        if(fifteen_percent_btn != null){
            fifteen_percent_btn.isEnabled = isClickable
            setTextBackToWhiteForUIReset(fifteen_percent_tip_amount_text_view)
            setTextBackToWhiteForUIReset(fifteen_percent_text_view)
        }
        if (twenty_percent_btn != null){
            twenty_percent_btn.isEnabled = isClickable
            setTextBackToWhiteForUIReset(twenty_percent_tip_amount_text_view)
            setTextBackToWhiteForUIReset(twenty_percent_text_view)
        }
        if (twenty_five_percent_btn != null){
            twenty_five_percent_btn.isEnabled = isClickable
            setTextBackToWhiteForUIReset(twenty_five_percent_tip_amount_text_view)
            setTextBackToWhiteForUIReset(twenty_five_percent_text_view)
        }
        if (thirty_percent_btn != null){
            thirty_percent_btn.isEnabled = isClickable
            setTextBackToWhiteForUIReset(thirty_percent_tip_amount_text_view)
            setTextBackToWhiteForUIReset(thirty_percent_text_view)
        }

        if(no_tip_btn != null){
            no_tip_btn.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.whiteTextColor
                )
            )
        }
    }
    private fun onCheckoutResult(result: Result<CheckoutResult, ResultError<CheckoutErrorCode>>) {
        if (result.isSuccess) {
            val checkoutResult = result.successValue
            showCheckoutResult(checkoutResult)
        } else {
            val error = result.error
            LoggerHelper.writeToLog("Error == ${error.code}, ${error.message}", "SQUARE", this.requireActivity())
            raiseAlphaUI()
            resetScreen()
            when(error.code){
                CheckoutErrorCode.SDK_NOT_AUTHORIZED -> {
                    Toast.makeText(
                        context,
                        "SDK not authorized, try again",
                        Toast.LENGTH_LONG
                    ).show()
                    SquareHelper.reauthorizeSquare(vehicleId, this.requireActivity())
                }
                CheckoutErrorCode.CANCELED -> {
                    val toast = Toast.makeText(
                        context,
                        "Checkout canceled",
                        Toast.LENGTH_SHORT
                    )
                    toast.setGravity(Gravity.TOP, 0, 0)
                    toast.show()
                }
                CheckoutErrorCode.USAGE_ERROR -> {
                    Toast.makeText(
                        context,
                        "Usage ERROR: ${error.message}, ErrorDebug Message: ${error.debugMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                    checkoutCallbackRef?.clear()
                }
                else -> {
                    Toast.makeText(
                        context,
                        "ERROR: ${error.message}, ErrorDebug Message: ${error.debugMessage}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
    private fun showCheckoutResult(checkoutResult: CheckoutResult) {
        val tenders = checkoutResult.tenders
        transactionDate = checkoutResult.createdAt
        val updatedTransactionDate = ViewHelper.formatDateUtcIso(transactionDate)
        val tripIdForPayment = VehicleTripArrayHolder.getTripPaymentInfo()?.tripId ?: ""
        for (i in tenders){
            transactionId = i.tenderId
            val cardName = i.cardDetails.card.brand.name
            cardInfo = cardName + " " + i.cardDetails.card.lastFourDigits
            tripTotalBackFromSquare = i.totalMoney.amount.toDouble()
            payment = BigDecimal(tripTotalBackFromSquare).movePointLeft(2).toDouble()
            VehicleTripArrayHolder.updateAmountPassedToSquareAfterPayment(payment!!)
        }
        VehicleTripArrayHolder.updateReceiptPaymentInfo(
            tripIdForPayment,
            null,
            null,
            tipAmountPassedToSquare,
            tipPercentPicked,
            null,
            null,
            null,
            null,
            null,
            null,
            transactionId
        )
        val paymentInfo = PaymentInfo(
            tripTotalBackFromSquare,
            tipAmountPassedToSquare,
            cardInfo,
            updatedTransactionDate,
            transactionId,
            tripIdForPayment
        )
        VehicleTripArrayHolder.setPaymentInfo(paymentInfo)
        if(cardInfo != "" ) {
            updateTransactionInfo(
                tipAmountPassedToSquare,
                cardInfo,
                tipPercentPicked,
                amountForSquare,
                updatedTransactionDate,
                transactionId,
                tripIdForPayment
            )
        }

        if(cardInfo != "") {
            updatePaymentDetail(
                transactionId,
                tripNumber,
                vehicleId,
                mAWSAppSyncClient!!,
                tripIdForPayment
            )
        }
        toEmailOrText()
    }

    private fun updatePaymentDetail(
        transactionId: String,
        tripNumber: Int,
        vehicleId: String,
        awsAppSyncClient: AWSAppSyncClient,
        tripId: String
    ){
       launch(Dispatchers.IO) {
            PIMMutationHelper.updatePaymentDetails(
                transactionId,
                tripNumber,
                vehicleId,
                awsAppSyncClient,
                "CARD",
                tripId
            )
        }
        LoggerHelper.writeToLog("Updating Payment details: $transactionId, tripNumber: $tripNumber, $vehicleId, $tripId", "Square", this.requireActivity())
    }

    private fun resetScreen() {
        //run this on main UI thread
        tripTotal = tripTotalReset
        updateTripTotalTextField(tripTotal)
        tipAmountPassedToSquare = 00.00
        updateUI()
    }
    private fun setTextToGreyForButtonPress(textView: TextView){
        if (textView.isVisible){
            textView.setTextColor((ContextCompat.getColor(requireContext(), R.color.grey)))
        }
    }
    private fun setTextBackToWhiteForUIReset(textView: TextView){
        if(textView.isVisible){
            textView.setTextColor(ContextCompat.getColor(requireContext(), R.color.whiteTextColor))
        }
    }
    @SuppressLint("SetTextI18n")
    private fun updateTripTotalTextField(tripTotalEntered: Double){
        if (tripTotalEntered < 10.00){
            val formattedArgs = tripTotalDFUnderTen.format(tripTotalEntered)
             tripTotal= formattedArgs.toDouble()
            val tripTotalToString = formattedArgs.toString()
            if (tip_screen_trip_total_textView != null){
                tip_screen_trip_total_textView.text = "$$tripTotalToString"
            }
        }else {
            val formattedArgs = tripTotalDF.format(tripTotalEntered)
            tripTotal = formattedArgs.toDouble()
            val tripTotalToString = formattedArgs.toString()
            if (tip_screen_trip_total_textView != null){
                tip_screen_trip_total_textView.text = "$$tripTotalToString"
            }
        }
    }
    private fun updateTransactionInfo(
        tipAmt: Double,
        cardInfo: String,
        tipPercent: Double,
        paidAmount: Double,
        transactionDate: String,
        transactionId: String,
        tripId: String
    ){
        if (!paymentSentForSquare) {
            paymentSentForSquare = true
            val vehicleId = ""

            launch(Dispatchers.IO)  {
                PIMMutationHelper.pimPaymentSquareMutation(
                    vehicleId,
                    tripId,
                    tipAmt,
                    cardInfo,
                    tipPercent,
                    paidAmount,
                    transactionDate,
                    transactionId
                )
            }
        }
    }

    private fun toCustomTip(){
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if(navController.currentDestination?.id == (R.id.tipScreenFragment)){
            val testTripTotal:Float = 0.0.toFloat()
            val action = TipScreenFragmentDirections.actionTipScreenFragmentToCustomTipFragment2(
                testTripTotal
            )
            navController.navigate(action)
        }
    }

    private  fun toEmailOrText() = launch(Dispatchers.Main.immediate){
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if(navController.currentDestination?.id == (R.id.tipScreenFragment)){
            val action = TipScreenFragmentDirections.actionTipScreenFragmentToEmailOrTextFragment(
                tipAmountPassedToSquare.toFloat()
            )
                .setAmountFromSquarePayment(tipAmountPassedToSquare.toFloat())
            navController.navigate(action)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        checkoutCallbackRef?.clear()
    }
}
