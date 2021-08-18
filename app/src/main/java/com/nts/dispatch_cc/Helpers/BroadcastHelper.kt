package com.nts.dispatch_cc.Helpers

import android.app.Activity
import android.content.Intent
import android.util.Log
import kotlin.system.exitProcess


object BroadcastHelper {
    val packageName = "com.ccsi.taxidispatch"

    fun sendBroadcast(activity: Activity) {
        val i = activity.packageManager.getLaunchIntentForPackage(packageName)
        val paymentInfo = VehicleTripArrayHolder.getPaymentInfo()
        val pimPaidAmount = paymentInfo?.pimPaidAmount ?: 0
        val tipAmt = paymentInfo?.tipAmount ?: 0
        val cardInfo = paymentInfo?.cardInfo ?: ""
        val transDate = paymentInfo?.transDate ?: ""
        val transId = paymentInfo?.transId ?: ""
        val tripId = paymentInfo?.tripId ?: ""
        if (i != null) {
            i.putExtra("externalApp", "com.nts.dispatch_cc")
            i.putExtra("pimPaidAmt", pimPaidAmount )
            i.putExtra("tipAmt", tipAmt)
            i.putExtra("cardInfo", cardInfo)
            i.putExtra("payType", "card")
            i.putExtra("transDate", transDate)
            i.putExtra("transId", transId)
            i.putExtra("tripId", tripId)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            Log.i("Broadcast_Sent", "pimPayAmount: $pimPaidAmount, tipAmt: ${paymentInfo?.tipAmount}, cardInfo: ${paymentInfo?.cardInfo}, transDate: ${paymentInfo?.transDate}, transId: ${paymentInfo?.transId}, tripId: ${paymentInfo?.tripId}")
            activity.startActivity(i)
            VehicleTripArrayHolder.clearTripValues()
            closeApp(activity)
        }
    }

    fun closeApp(activity: Activity){
        activity.finish()
        exitProcess(0)
    }
}
