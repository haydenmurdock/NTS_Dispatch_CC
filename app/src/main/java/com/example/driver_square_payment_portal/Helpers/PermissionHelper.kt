package com.example.driver_square_payment_portal.Helpers

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.*

object PermissionHelper {


    fun getPermissions(activity: FragmentActivity){
        requestMic(activity)
        requestStorage(activity)
        requestLocation(activity)
        requestPhoneState(activity)
        allOtherPermissions(activity)
    }

    private fun requestMic(activity: FragmentActivity) {
        val REQUEST_RECORD_AUDIO_PERMISSION = 200
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        }
    }
    private fun requestStorage(activity: FragmentActivity){
        val REQUEST_STORAGE_PERMSSION_CODE = 1
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMSSION_CODE)
        }
    }
    private fun requestLocation(activity: FragmentActivity) {
        val REQUEST_FINE_LOCATION_PERMSSION_CODE = 1
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION_PERMSSION_CODE)
        }
    }
    @SuppressLint("HardwareIds")
    private fun requestPhoneState(activity: FragmentActivity){
        val REQUEST_PHONE_STATE_PERMSSION_CODE = 1
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                REQUEST_PHONE_STATE_PERMSSION_CODE)
        }
    }
    private fun allOtherPermissions(activity: FragmentActivity){
        val REQUEST_CODE_ASK_PERMISSIONS = 1
        val REQUIRED_SDK_PERMISSIONS = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.EXPAND_STATUS_BAR,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_NUMBERS,
            Manifest.permission.READ_PHONE_STATE
        )

        val missingPermissions = ArrayList<String>()
        // check all required dynamic permissions
        for (permission in REQUIRED_SDK_PERMISSIONS) {
            val result = ContextCompat.checkSelfPermission(activity, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                missingPermissions.add(permission)
            }
        }
        if (missingPermissions.isNotEmpty()) {
            // request all missing permissions
            val permissions = missingPermissions
                .toTypedArray()
            ActivityCompat.requestPermissions(activity, permissions, REQUEST_CODE_ASK_PERMISSIONS)
        } else {
            val grantResults = IntArray(REQUIRED_SDK_PERMISSIONS.size)
            Arrays.fill(grantResults, PackageManager.PERMISSION_GRANTED)
            activity.onRequestPermissionsResult(
                REQUEST_CODE_ASK_PERMISSIONS, REQUIRED_SDK_PERMISSIONS,
                grantResults)
        }
    }
}