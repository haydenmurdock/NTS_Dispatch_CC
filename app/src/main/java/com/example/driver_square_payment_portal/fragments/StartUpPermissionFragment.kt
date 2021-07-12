package com.example.driver_square_payment_portal.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.Navigation
import com.example.driver_square_payment_portal.Helpers.PermissionHelper
import com.example.driver_square_payment_portal.Helpers.VehicleTripArrayHolder

import com.example.driver_square_payment_portal.R
import java.util.*

class StartUpPermissionFragment : Fragment() {
    var micPermission = true
    var storagePermission = true
    var locationPermission = true
    var requestPhoneState = true

    private lateinit var viewModel: StartUpPermissionViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.start_up_permission_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(StartUpPermissionViewModel::class.java)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        insertTestTrip()
    }

    private fun insertTestTrip(){
        VehicleTripArrayHolder.setTripPaymentInfo("123", "9999", "ccsi_U_1496", "321", null, 1.00, null)
    }

    override fun onResume() {
        super.onResume()
             PermissionHelper.getPermissions(requireActivity())
        if(micPermission && requestPhoneState && locationPermission && storagePermission) {
            toCheckOauthFragment()
        } else {
            requestMic(requireActivity())
            requestPhoneState(requireActivity())
            requestStorage(requireActivity())
            requestLocation(requireActivity())
        }

    }

    //Navigation
    private  fun toCheckOauthFragment(){
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if(navController.currentDestination?.id == (R.id.startUpPermissionFragment)){
            navController.navigate(R.id.action_startUpPermissionFragment_to_checkOAuthFragment)
        }
    }

    private fun requestMic(activity: FragmentActivity) {
        val REQUEST_RECORD_AUDIO_PERMISSION = 200
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        } else {
            micPermission = true
            Log.i("Test", "Mic permission is true")
        }
    }
    private fun requestStorage(activity: FragmentActivity){
        val REQUEST_STORAGE_PERMSSION_CODE = 1
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMSSION_CODE)
        } else {
            storagePermission = true
            Log.i("Test", "storage permission is true")
        }

    }
    private fun requestLocation(activity: FragmentActivity) {
        val REQUEST_FINE_LOCATION_PERMSSION_CODE = 1
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOCATION_PERMSSION_CODE)
        } else {
           locationPermission = true
            Log.i("Test", "Location permission is true")
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
        } else {
             requestPhoneState = true
            Log.i("Test", "request phoneState permission is true")
        }
    }
}
