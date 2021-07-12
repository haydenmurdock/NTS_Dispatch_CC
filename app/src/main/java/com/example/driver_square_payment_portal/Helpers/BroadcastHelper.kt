package com.example.driver_square_payment_portal.Helpers

import android.app.Activity
import android.content.Intent



object BroadcastHelper {
    val packageName = "com.ccsi.taxidispatch"

    fun sendBroadcast(activity: Activity) {
        val i = Intent()
        val paymentInfo = VehicleTripArrayHolder.getPaymentInfo()
        if (paymentInfo != null) {
            i.putExtra("pimPaidAmt", paymentInfo.pimPaidAmount)
            i.putExtra("tipAmt", paymentInfo.tipAmount)
            i.putExtra("cardInfo", paymentInfo.cardInfo)
            i.putExtra("payType", "card")
            i.putExtra("transDate", paymentInfo.transDate)
            i.putExtra("transId", paymentInfo.transId)
            i.putExtra("tripId", paymentInfo.tripId)
            i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES)
            i.action = packageName
            activity.applicationContext.sendBroadcast(i)
           val  launchIntent = activity.packageManager.getLaunchIntentForPackage(packageName);
            if (launchIntent != null) {
                activity.startActivity(launchIntent);
                VehicleTripArrayHolder.clearTripValues()
            }
        }
    }
}
