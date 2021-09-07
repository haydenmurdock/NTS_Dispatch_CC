package com.nts.dispatch_cc.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import com.nts.dispatch_cc.helpers.BroadcastHelper
import com.nts.dispatch_cc.helpers.LoggerHelper
import com.nts.dispatch_cc.helpers.PermissionHelper
import com.nts.dispatch_cc.helpers.VehicleTripArrayHolder
import com.nts.dispatch_cc.R
import kotlinx.android.synthetic.main.start_up_permission_fragment.*


class StartUpPermissionFragment : Fragment(){

    var micPermission = false
    var storagePermission = false
    var locationPermission = false
    var requestPhoneState = false
    var receivedBroadcast = false
    var viewTimer: CountDownTimer? = null


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
        back_to_driver_app_from_btn.setOnClickListener {
            BroadcastHelper.sendBroadcast(requireActivity())
        }
        requestMic()
        locationPermission()
        hideUI()
        startViewTimer()
    }

    override fun onResume() {
        super.onResume()
        PermissionHelper.getPermissions(requireActivity())
        if(micPermission && requestPhoneState && storagePermission && locationPermission) {
            VehicleTripArrayHolder.broadcastReceivedMLD.observe(
                this.viewLifecycleOwner, { broadcastReceived ->
                    if (broadcastReceived) {
                        receivedBroadcast = broadcastReceived
                        toCheckOauthFragment()
                    }
                })
        } else {
            requestPhoneState(requireActivity())
            requestStorage(requireActivity())
        }

    }

    private fun startViewTimer(){
        viewTimer = object : CountDownTimer(5000, 1000){
            override fun onTick(p0: Long) {
            }

            override fun onFinish() {
              showUI()
            }
        }.start()
    }

    private fun stopViewTimer(){
        viewTimer?.cancel()
        viewTimer = null
    }

    override fun onStop() {
        super.onStop()
        hideUI()
    }

    //Navigation
    private  fun toCheckOauthFragment(){
         stopViewTimer()
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if(navController.currentDestination?.id == (R.id.startUpPermissionFragment)){
            navController.navigate(R.id.action_startUpPermissionFragment_to_checkOAuthFragment)
        }
    }

    private fun requestMic() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            micPermission = true
        } else {
            requestPermissions(
                arrayOf(
                    Manifest.permission.RECORD_AUDIO
                ), 11
            )
        }
    }
    private fun requestStorage(activity: FragmentActivity) {
        val REQUEST_STORAGE_PERMSSION_CODE = 1
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                REQUEST_STORAGE_PERMSSION_CODE
            )
        } else {
            storagePermission = true
            Log.i("Test", "storage permission is true")
        }
    }

  private  fun locationPermission() {
      if (ActivityCompat.checkSelfPermission(
              requireContext(),
              Manifest.permission.ACCESS_FINE_LOCATION
          ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
              requireContext(),
              Manifest.permission.ACCESS_COARSE_LOCATION
          ) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(
              requireContext(),
              Manifest.permission.ACCESS_BACKGROUND_LOCATION
          ) == PackageManager.PERMISSION_GRANTED
      ) {
          Log.i("Permissions", "all location permissions granted")
          locationPermission = true

      } else {
          requestPermissions(
              arrayOf(
                  Manifest.permission.ACCESS_COARSE_LOCATION,
                  Manifest.permission.ACCESS_FINE_LOCATION,
              ),
              10
          )
      }


  }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 10) {
            if(!ActivityCompat.shouldShowRequestPermissionRationale(
                    this.requireActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                locationPermission = true
            } else {
                locationPermission = true
            }
        }
        if (requestCode == 11) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(
                    this.requireActivity(),
                    Manifest.permission.RECORD_AUDIO
                )
            ) {
                micPermission = true
                locationPermission()
            }else {
                micPermission = true
                locationPermission()
            }
        }
    }

    private fun hideUI(){
        LoggerHelper.writeToLog("Hiding UI.", "StartUp", this.requireActivity())
        val show = View.VISIBLE
        val hide = View.INVISIBLE

        if(back_to_driver_app_from_btn.visibility == show){
            back_to_driver_app_from_btn.visibility = hide
        }
        if(how_to_charge_title_textView.visibility == show){
           how_to_charge_title_textView.visibility = hide
        }

        if(with_driver_app_textView.visibility == show){
            with_driver_app_textView.visibility = hide
        }
        if(active_trip_TextView.visibility == show){
            active_trip_TextView.visibility = hide
        }
        if(you_must_textView.visibility == show){
            you_must_textView.visibility = hide
        }
        if(set_your_meter_textView.visibility == show){
            set_your_meter_textView.visibility = hide
        }

        if(tap_options_textView.visibility == show){
            tap_options_textView.visibility = hide
        }
        if(tap_the_charge_textView.visibility == show){
            tap_the_charge_textView.visibility = hide
        }
        if(imageView2.visibility == show){
            imageView2.visibility = hide
        }

        if(imageView3.visibility == show){
            imageView3.visibility = hide
        }
        if(imageView4.visibility == show){
            imageView4.visibility = hide
        }
        if(imageView5.visibility == show){
            imageView5.visibility = hide
        }
        startup_progressBar.animate()
        startup_progressBar.visibility = show
    }

    fun showUI(){
        LoggerHelper.writeToLog("Showing U.I.", "StartUp", this.requireActivity())
        val show = View.INVISIBLE
        val hide = View.VISIBLE

        if(back_to_driver_app_from_btn.visibility == show){
            back_to_driver_app_from_btn.visibility = hide
        }
        if(how_to_charge_title_textView.visibility == show){
            how_to_charge_title_textView.visibility = hide
        }

        if(with_driver_app_textView.visibility == show){
            with_driver_app_textView.visibility = hide
        }
        if(active_trip_TextView.visibility == show){
            active_trip_TextView.visibility = hide
        }
        if(you_must_textView.visibility == show){
            you_must_textView.visibility = hide
        }
        if(set_your_meter_textView.visibility == show){
            set_your_meter_textView.visibility = hide
        }

        if(tap_options_textView.visibility == show){
            tap_options_textView.visibility = hide
        }
        if(tap_the_charge_textView.visibility == show){
            tap_the_charge_textView.visibility = hide
        }
        if(imageView2.visibility == show){
            imageView2.visibility = hide
        }

        if(imageView3.visibility == show){
            imageView3.visibility = hide
        }
        if(imageView4.visibility == show){
            imageView4.visibility = hide
        }
        if(imageView5.visibility == show){
            imageView5.visibility = hide
        }
        startup_progressBar.animate()
        startup_progressBar.visibility = show
    }

    @SuppressLint("HardwareIds")
    private fun requestPhoneState(activity: FragmentActivity){
        val REQUEST_PHONE_STATE_PERMSSION_CODE = 1
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.READ_PHONE_STATE),
                REQUEST_PHONE_STATE_PERMSSION_CODE
            )
        } else {
             requestPhoneState = true
            Log.i("Test", "request phoneState permission is true")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopViewTimer()
    }
}
