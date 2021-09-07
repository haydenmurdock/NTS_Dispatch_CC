package com.nts.dispatch_cc.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import com.nts.dispatch_cc.helpers.LoggerHelper
import com.nts.dispatch_cc.R
import com.nts.dispatch_cc.helpers.SquareHelper
import com.nts.dispatch_cc.helpers.VehicleTripArrayHolder
import com.nts.dispatch_cc.internal.ScopedFragment
import com.squareup.sdk.reader.ReaderSdk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CheckOAuthFragment : ScopedFragment(){
   private val currentFragmentId = R.id.checkOAuthFragment

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.check_o_auth_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val isSquareAuthorized = ReaderSdk.authorizationManager().authorizationState.isAuthorized
        val vehicleId = VehicleTripArrayHolder.getTripPaymentInfo()?.vehicleId ?: ""
        if(!isSquareAuthorized){
            if(vehicleId != ""){
                LoggerHelper.writeToLog("vehicle Id: $vehicleId was not authorized with square. Re-authorizing", "Square", this.requireActivity())
                launch(Dispatchers.Main) {
                    val result = runBlocking(Dispatchers.IO) {
                        SquareHelper.reauthorizeSquare(vehicleId, requireActivity())
                    }
                }
                toEnterEnterCardScreen()
            }
        } else {
            LoggerHelper.writeToLog("vehicle Id: $vehicleId was authorized with square at startup", "Square", this.requireActivity())
            toEnterEnterCardScreen()
        }

    }

    //Navigation
    private fun toEnterEnterCardScreen(){
        val navController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        if (navController.currentDestination?.id == currentFragmentId){
            navController.navigate(R.id.action_checkOAuthFragment_to_tipScreenFragment)
        }
    }
}
