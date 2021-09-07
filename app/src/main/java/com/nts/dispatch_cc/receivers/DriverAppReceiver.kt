package com.nts.dispatch_cc.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class DriverAppReceiver: BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null){
//            val vehicleId: String = intent.getStringExtra("vehicleId") ?: ""
//            val driverId: Int = intent.getIntExtra("driverId", 0)
//            val tripId: String = intent.getStringExtra("tripId") ?: ""
//            val tripNumber: Int = intent.getIntExtra("tripNumber", 0)
//            val pimPayAmount: Double = intent.getDoubleExtra("pimPayAmount", 0.0)
//            val owedPrice: Double = intent.getDoubleExtra("owedPrice", 0.0)
//            val airportFee: Double = intent.getDoubleExtra("airportFee", 0.0)
//            val discountAmt: Double = intent.getDoubleExtra("discountAmt", 0.0)
//            val discountPercent: Double = intent.getDoubleExtra("discountPercent", 0.0)
//            val toll: Double = intent.getDoubleExtra("toll", 0.0)
//            val destLat: Double = intent.getDoubleExtra("destLat", 0.0)
//            val destLng: Double = intent.getDoubleExtra("destLng", 0.0 )
//            val receiptPaymentInfo = ReceiptPaymentInfo(tripId, pimPayAmount, owedPrice, 0.0, 0.0, airportFee, discountAmt, toll, discountPercent, destLat, destLng, null)
//            Log.i("Broadcast_Received", "vehicleId: $vehicleId, driverId: $driverId, tripId: $tripId, tripNumber: $tripNumber, pimPayAmount: $pimPayAmount, owedPrice: $owedPrice, airportFee: $airportFee, discountAmt: $discountAmt, toll: $toll, destLat: $destLat, destLng: $destLng")
//            VehicleTripArrayHolder.setReceiptPaymentInfo(receiptPaymentInfo)
//            VehicleTripArrayHolder.setTripPaymentInfo(tripId, driverId, vehicleId, tripNumber, null, pimPayAmount, receiptPaymentInfo)
        }
    }
}