package com.nts.dispatch_cc.helpers

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import androidx.core.content.ContextCompat
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.ConcurrentModificationException

object LoggerHelper {
    var logging = false
    @SuppressLint("SimpleDateFormat")
    private val dateTimeStamp = SimpleDateFormat("yyyy_MM_dd").format(Date())
    @SuppressLint("SimpleDateFormat")
    private val timeTimeStamp = SimpleDateFormat("HH:mm:ss")
    val charset = Charsets.UTF_8
    private var logToSendAWS: String? = null
    private var vehicleId: String? = null
    private const val logFragmentStartStamp = "-LOG FRAGMENT START-"
    private const val logFragmentEndStamp = "-LOG FRAGMENT END-"
    private const val permissionGranted = 0
    private var logArray: MutableList<String?>? = null
    private const val logLimit = 201
    private var canAddToLog = false

    internal fun writeToLog(log: String, tag: String?, activity: Activity) {
        if (!canAddToLog) {
            Log.i(
                tag,
                "Can't add: $log to official logs since it hasn't been cleared of previous logs"
            )
            return
        }
        if (tag != null) {
            Log.i(tag, log)
        }

        val readPermission =
            ContextCompat.checkSelfPermission(activity.applicationContext, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writePermission = ContextCompat.checkSelfPermission(
            activity.applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val logTimeStamp = timeTimeStamp.format(Date())
        addLogToInternalLogs(logTimeStamp + "_" + log + "\n")
        if (logging && writePermission == permissionGranted && readPermission == permissionGranted) {
            vehicleId = VehicleTripArrayHolder.getTripPaymentInfo()?.vehicleId
            if (vehicleId == null) {
                return
            }
                if (logToSendAWS == null) {
                    logToSendAWS = "com.nts.dispatch_cc" + logFragmentStartStamp + logTimeStamp + "_" + log + "\n"
                } else {
                    logToSendAWS +=  logTimeStamp + "_" + log + "\n"
                }
            }
        }

     fun sendLogToAWS(enteredVehicleId: String) {
            var vehicleIdForLog = enteredVehicleId
            if (enteredVehicleId == "") {
                vehicleIdForLog = vehicleId ?: ""
            }
            if (logToSendAWS == null) {
                return
            }
            logToSendAWS += logFragmentEndStamp

            val client = OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .build()
            val JSON = "application/json; charset=utf-8".toMediaType()
            val json = JSONObject()
            try {
                json.put("fileName", "PIM/$vehicleIdForLog" + "_" + "$dateTimeStamp.txt")
                json.put("text", "$logToSendAWS" + "\n")
            } catch (e: JSONException) {
                Log.i("ERROR", "JSON error $e")
            }
            val body = json.toString().toRequestBody(JSON)
            Log.i("LOGGER", "Json body :  $json")
            val url = URL("https://y22euz5gjh.execute-api.us-east-2.amazonaws.com/prod/uploadLogs")
            val request = Request.Builder()
                .url(url)
                .addHeader("x-api-key", "AdxSCAHcze6iyaJZJZIEgaEgThUGW78LaSemE2US")
                .addHeader("token", "GPAVNHeKTCvG4FFz")
                .post(body)
                .build()
            try {
                client.newCall(request).execute().use { response ->
                    if (response.isSuccessful) {
                        Log.i(
                            "LOGGER",
                            "response code : ${response.code} response message: ${response.message}"
                        )
                    }
                    when (response.code) {
                        200 -> {
                            logToSendAWS = null
                            Log.i(
                                "LOGGER",
                                "Successful log uploaded to AWS. Clearing Out local log"
                            )
                        }
                        else -> {
                            Log.i(
                                "LOGGER",
                                "Log response code unexpected. ${response.code}. Local Log was not cleared"
                            )
                        }
                    }

                }
            } catch (e: IOException) {
                Log.i(
                    "LOGGER", "Error from Log Upload $e"
                )
            }
        }
    private fun addLogToInternalLogs(log:String){
        try {
            logArray?.add(log)
        } catch (e: ConcurrentModificationException) {

        }
    }

    fun turnOnLogs(){
        logging = true
        canAddToLog = true
    }

    fun turnOffLogs(){
        logging = false
        canAddToLog = false
    }

}


