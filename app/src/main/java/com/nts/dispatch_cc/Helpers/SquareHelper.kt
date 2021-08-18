package com.nts.dispatch_cc.Helpers

import android.app.Activity
import androidx.annotation.MainThread
import androidx.lifecycle.MutableLiveData
import com.nts.dispatch_cc.Model.AuthCode
import com.google.gson.Gson
import com.squareup.sdk.reader.ReaderSdk
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.lang.Error

object SquareHelper {

   private var isSquareAuthorized = false
   private var isSquareAuthorizedMLD: MutableLiveData<Boolean>? = null
    private var SquareAuthErrorMLD: MutableLiveData<String>? = null



    fun getAuthStatus():Boolean {
        isSquareAuthorized = ReaderSdk.authorizationManager().authorizationState.isAuthorized
        isSquareAuthorizedMLD?.postValue(isSquareAuthorized)
        return isSquareAuthorized
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
        if(vehicleId.isNotEmpty()){
            getAuthorizationCode(vehicleId, mainActivity)
        }
    }

    fun deauthorizeSquare(activity: Activity){
       activity.runOnUiThread {
            ReaderSdk.authorizationManager().deauthorize()
        }
    }

    private fun getAuthorizationCode(vehicleId: String, mainActivity: Activity) {
        val url = "https://i8xgdzdwk5.execute-api.us-east-2.amazonaws.com/prod/CheckOAuthToken?vehicleId=$vehicleId"
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
        mainActivity.runOnUiThread {
            ReaderSdk.authorizationManager().authorize(authorizationCode)
        }
        isSquareAuthorized = true
        isSquareAuthorizedMLD?.postValue(isSquareAuthorized)
    }

    fun isSquareAuthorized() = isSquareAuthorizedMLD
}