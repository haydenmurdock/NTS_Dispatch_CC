package com.example.driver_square_payment_portal.Helpers

import android.util.Log
import com.example.driver_square_payment_portal.Model.ReceiptPaymentInfo
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.net.URL
import java.util.concurrent.TimeUnit

object ReceiptHelper {

    fun sendReceiptInfoToAWS(tripId: String, paymentMethod: String, transactionId: String, custPhoneNumber: String?, custEmail: String?, driverReceipt: Boolean){
        val receiptPaymentInfo: ReceiptPaymentInfo? =
            VehicleTripArrayHolder.getReceiptPaymentInfo(tripId)
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
            json.put("sendMethod","text")
            json.put("tripId",tripId)
            json.put("src", "pim")
            json.put("paymentId",transactionId)
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
            json.put("airPortFee", receiptPaymentInfo?.airPortFee)
            json.put("discountAmt", receiptPaymentInfo?.discountAmt)
            json.put("toll", receiptPaymentInfo?.toll)
            json.put("discountPercent", receiptPaymentInfo?.discountAmt)
            json.put("destLat", receiptPaymentInfo?.destLat)
            json.put("destLon", receiptPaymentInfo?.destLon)
        } catch (e: JSONException){
            Log.i("ERROR", "JSON error $e")
        }

        val body = json.toString().toRequestBody(jSON)
        Log.i("URL","Json body : $json")
        val url = URL("https://5s27urxc78.execute-api.us-east-2.amazonaws.com/prod/sendReceipt")
        //val url = URL("https://5s27urxc78.execute-api.us-east-2.amazonaws.com/test/sendReceipt")
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()
        try {
            client.newCall(request).execute().use { response ->
                if (response.isSuccessful){


                } else {
                    Log.i("Text_Receipt", "Send Text receipt unsuccessful. Step 3: Fail")
                    Log.i("Text_Receipt", "${response.message} ${response.code}")
                }
            }
        } catch (e: Error){

        }
    }
}