package com.nts.dispatch_cc.helpers

import android.app.Activity
import android.util.Log
import com.nts.dispatch_cc.model.ReceiptPaymentInfo
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.TimeUnit

object ReceiptHelper {

    fun sendReceiptInfoToAWS(tripId: String, paymentMethod: String, custPhoneNumber: String?, custEmail: String?, driverReceipt: Boolean, activity: Activity){
        val sendMethod = when {
            driverReceipt -> {
                "none"
            }
            custPhoneNumber != null -> {
                "text"
            }
            else -> {
                "email"
            }
        }
        val sendingAddress = custPhoneNumber ?: custEmail

        val receiptPaymentInfo: ReceiptPaymentInfo? =
            VehicleTripArrayHolder.getReceiptPaymentInfo(tripId)
        LoggerHelper.writeToLog("Sending to receipt API." +
                " tripId: $tripId," +
                " paymentMethod: ${paymentMethod.toLowerCase()}," +
                " sendMethod: $sendMethod,"+
                " transactionId: ${receiptPaymentInfo?.transactionId}," +
                "custPhoneNumber/custEmail: $sendingAddress," +
                " pimPayAmount: ${receiptPaymentInfo?.pimPayAmount}," +
                " owedPrice: ${receiptPaymentInfo?.owedPrice}," +
                " tipAmt: ${receiptPaymentInfo?.tipAmt}," +
                " tipPercent: ${receiptPaymentInfo?.tipPercent}," +
                " airportFee: ${receiptPaymentInfo?.airPortFee}, " +
                " discountAmt: ${receiptPaymentInfo?.discountAmt}," +
                " toll: ${receiptPaymentInfo?.toll}," +
                " discountPercent: ${receiptPaymentInfo?.discountPercent}," +
                " destLat: ${receiptPaymentInfo?.destLat}, " +
                " destLon: ${receiptPaymentInfo?.destLon}", "Receipt", activity)
        val client = OkHttpClient().newBuilder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(false)
            .build()
        val jSON = "application/json; charset=utf-8".toMediaType()
        val json = JSONObject()
        try{
            json.put("paymentMethod", "${paymentMethod.toLowerCase()}")
            json.put("sendMethod", "$sendMethod")
            json.put("tripId",tripId)
            json.put("src", "pim")
            json.put("paymentId",receiptPaymentInfo?.transactionId)
            if(driverReceipt){
                json.put("custPhoneNbr", null)
                json.put("custEmail", null)
            } else {
                json.put("custPhoneNbr", custPhoneNumber)
                json.put("custEmail", custEmail)
            }
            json.put("pimPayAmt", receiptPaymentInfo?.pimPayAmount)
            json.put("owedPrice", receiptPaymentInfo?.owedPrice)
            json.put("tipAmt", receiptPaymentInfo?.tipAmt)
            json.put("tipPercent", receiptPaymentInfo?.tipPercent)
            json.put("airportFee", receiptPaymentInfo?.airPortFee)
            json.put("discountAmt", receiptPaymentInfo?.discountAmt)
            json.put("toll", receiptPaymentInfo?.toll)
            json.put("discountPercent", receiptPaymentInfo?.discountPercent)
            json.put("destLat", receiptPaymentInfo?.destLat)
            json.put("destLon", receiptPaymentInfo?.destLon)
        } catch (e: JSONException){
            Log.i("ERROR", "JSON error $e")
        }

        val body = json.toString().toRequestBody(jSON)
        Log.i("URL","Json body : $json")
        val url = URL("https://5s27urxc78.execute-api.us-east-2.amazonaws.com/prod/sendReceipt")
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful){
                    LoggerHelper.writeToLog("response for receipt was successful. ${response.body},${response.code}, ${response.message}", "Receipt", activity)

                } else {
                    LoggerHelper.writeToLog("response for receipt was not successful. ${response.body}, ${response.code}, ${response.message}", "Receipt", activity)
                }
            }
        } catch (e: Error){

        }
    }
}