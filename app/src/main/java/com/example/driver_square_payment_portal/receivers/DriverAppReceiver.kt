package com.example.driver_square_payment_portal.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.driver_square_payment_portal.Helpers.VehicleTripArrayHolder
import com.example.driver_square_payment_portal.Model.ReceiptPaymentInfo

class DriverAppReceiver: BroadcastReceiver() {
    //vehicleId
    //driverId
    //tripId
    //tripNumber
    //pimPayAmount
    //owedPrice
    //airportFee
    //discountAmt
    //discountPercent
    //toll

    //Will send back tipAmt and tipPercent
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null){
            val vehicleId: String = intent.getStringExtra("vehicleId") ?: ""
            val driverId = intent.getStringExtra("driverId") ?: ""
            val tripId = intent.getStringExtra("tripId") ?: ""
            val tripNumber = intent.getStringExtra("tripNumber") ?: ""
            val pimPayAmount = intent.getDoubleExtra("pimPayAmount", 0.0)
            val owedPrice = intent.getDoubleExtra("owedPrice", 0.0)
            val airportFee = intent.getDoubleExtra("airportFee", 0.0)
            val discountAmt = intent.getDoubleExtra("discountAmt", 0.0)
            val discountPercent = intent.getDoubleExtra("discountPercent", 0.0)
            val toll = intent.getDoubleExtra("toll", 0.0)
            val destLat = intent.getDoubleExtra("destLat", 0.0)
            val destLng = intent.getDoubleExtra("destLng", 0.0 )
            val receiptPaymentInfo = ReceiptPaymentInfo(tripId, pimPayAmount, owedPrice, 0.0, 0.0, airportFee, discountAmt, toll, discountPercent, destLat, destLng)
            VehicleTripArrayHolder.setReceiptPaymentInfo(receiptPaymentInfo)
            VehicleTripArrayHolder.setTripPaymentInfo(tripId, driverId, vehicleId, tripNumber, null, pimPayAmount, receiptPaymentInfo)
        }
    }

}