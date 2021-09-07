package com.nts.dispatch_cc.helpers

import android.app.Activity
import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.nts.dispatch_cc.model.AuthCode
import com.google.gson.Gson
import com.nts.dispatch_cc.model.MAC
import com.nts.dispatch_cc.internal.ModelPreferences
import com.squareup.sdk.reader.ReaderSdk
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Error
import java.time.LocalTime
import java.util.*

object SquareHelper {

    private var isSquareAuthorized = false
    private var isSquareAuthorizedMLD: MutableLiveData<Boolean>? = null
    private var SquareAuthErrorMLD: MutableLiveData<String>? = null
    private const val macKey = "MOBILE_AUTH_CODE"
    private const val logTag = "SQUARE"


    fun saveMAC(id: String, context: Context, activity: Activity){
        val lastMAC = getLastMAC(context)
        val isExpired = lastMAC?.isMACExpired(LocalTime.now()) ?: true
        LoggerHelper.writeToLog("Saving MAC. Checking to see if it's expired. Last MAC expired: $isExpired", logTag, activity)
        if(!isExpired){
            val mac = MAC(id, LocalTime.now(),activity)
            ModelPreferences(context).putObject(macKey, mac)
        }
    }

   private fun getLastMAC(context: Context):MAC? {
        return ModelPreferences(context).getObject(macKey, MAC::class.java)
    }

   private fun isMACExpired(context: Context):Boolean {
        return getLastMAC(context)?.isMACExpired(LocalTime.now()) ?: true
    }


    fun reauthorizeSquare(vehicleId: String?, mainActivity: Activity){
        if(vehicleId == null){
            return
        }
        if(ReaderSdk.authorizationManager().authorizationState.canDeauthorize()){
            mainActivity.runOnUiThread {
                ReaderSdk.authorizationManager().deauthorize()
            }
        }
        val isLastMACExpired = isMACExpired(mainActivity.applicationContext)
        if(isLastMACExpired){
            LoggerHelper.writeToLog("MAC was expired or didn't exist. Getting NEW MAC for authorization.", logTag, mainActivity)
            getMobileAuthCode(vehicleId, mainActivity)
        } else {
            val lastMACid = getLastMAC(mainActivity.applicationContext)?.getId() ?: ""
            LoggerHelper.writeToLog("Last mac id was less than 1 hour old. Using OLD MAC for authorization. Id: $lastMACid", logTag, mainActivity)
            if(lastMACid != ""){
                mainActivity.runOnUiThread {
                    ReaderSdk.authorizationManager().authorize(lastMACid)
                }
            } else {
                LoggerHelper.writeToLog("Last mac id was less than 1 hour old, but MAC wasn't formatted correctly. Getting new MAC", logTag, mainActivity)
                getMobileAuthCode(vehicleId, mainActivity)
            }
        }
    }


    private fun getMobileAuthCode(vehicleId: String, mainActivity: Activity) {
        val dateTime = ViewHelper.formatDateUtcIso(Date())
        val url = "https://i8xgdzdwk5.execute-api.us-east-2.amazonaws.com/prod/CheckOAuthToken?vehicleId=$vehicleId&source=DispatchCC&eventTimeStamp=2021-08-26T$dateTime&extraInfo=SquareHelper"
        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()
        try {
            client.newCall(request).enqueue(object : Callback {
                override fun onResponse(call: Call, response: okhttp3.Response) {
                    if (response.code == 200) {
                        val gson = Gson()
                        val convertedObject =
                            gson.fromJson(response.body?.string(), AuthCode::class.java)
                        val authCode = convertedObject.authCode
                        onAuthorizationCodeRetrieved(authCode, mainActivity)
                    }
                    if(response.code == 404) {
                        SquareAuthErrorMLD?.postValue("Error Code: 404 - Vehicle not found in fleet, check fleet management portal")

                    }
                    if (response.code == 401) {
                        SquareAuthErrorMLD?.postValue("Error code: 401 - Need to authorize fleet with login")
                    }
                    if (response.code == 422){
                        SquareAuthErrorMLD?.postValue("Error code: 422 - Missing vehicle Id from Dispatch app")
                    }
                    if(response.code == 500){
                        SquareAuthErrorMLD?.postValue("Error code: 500 - Error getting AuthCode from AWS. Please contact ")
                    }
                }
                override fun onFailure(call: Call, e: IOException) {}
            })
        } catch (e: Error) {

        }
    }

    private fun onAuthorizationCodeRetrieved(authorizationCode: String, mainActivity: Activity) {
        saveMAC(authorizationCode, mainActivity.applicationContext, mainActivity)
        mainActivity.runOnUiThread {
            ReaderSdk.authorizationManager().authorize(authorizationCode)
        }
        isSquareAuthorized = true
        isSquareAuthorizedMLD?.postValue(isSquareAuthorized)
    }

    fun isSquareAuthorized() = isSquareAuthorizedMLD
}