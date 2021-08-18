package com.nts.dispatch_cc.Helpers

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.nts.dispatch_cc.Model.PaymentInfo
import com.nts.dispatch_cc.Model.ReceiptPaymentInfo
import com.nts.dispatch_cc.Model.TripPayment

object VehicleTripArrayHolder {
    private  var receiptPaymentInfo: ReceiptPaymentInfo? = null
    private var paymentInfo: PaymentInfo? = null
    var tripPayment: TripPayment? = null
    var broadcastReceived = false
    var broadcastReceivedMLD = MutableLiveData<Boolean>()

    @JvmName("setReceiptPaymentInfo1")
    fun setReceiptPaymentInfo(info: ReceiptPaymentInfo){
        receiptPaymentInfo = info
    }

    fun getReceiptPaymentInfo(tripId: String): ReceiptPaymentInfo? {
        if(receiptPaymentInfo != null && tripId == receiptPaymentInfo?.tripId){
            return receiptPaymentInfo as ReceiptPaymentInfo
        }
        return null
    }

    fun updateReceiptPaymentInfo(tripId: String, pimPayAmount: Double?, owedPrice: Double?, tipAmt: Double?, tipPercent: Double?, airPortFee: Double?, discountAmt: Double?, toll: Double?, discountPercent: Double?, destLat: Double?, destLon: Double?, transactionId: String?){
        val receiptPaymentInfo = getReceiptPaymentInfo(tripId)
        if(receiptPaymentInfo != null){
            if(pimPayAmount != null){
                receiptPaymentInfo.pimPayAmount = pimPayAmount
            }
            if(owedPrice != null){
                receiptPaymentInfo.owedPrice = owedPrice
            }
            if(tipAmt != null){
                receiptPaymentInfo.tipAmt = tipAmt
            }
            if(tipPercent != null){
                receiptPaymentInfo.tipPercent = tipPercent
            }
            if(airPortFee != null){
                receiptPaymentInfo.airPortFee = airPortFee
            }
            if(discountAmt != null){
                receiptPaymentInfo.discountAmt = discountAmt
            }
            if(toll != null){
                receiptPaymentInfo.toll = toll
            }
            if(discountPercent != null){
                receiptPaymentInfo.discountPercent = discountPercent
            }
            if(destLat != null && destLat != receiptPaymentInfo.destLat){
                receiptPaymentInfo.destLat = destLat
            }
            if(destLon != null && destLon != receiptPaymentInfo.destLon){
                receiptPaymentInfo.destLon = destLon
            }
            if(transactionId != null){
                receiptPaymentInfo.transactionId = transactionId
            }


        }
        if(receiptPaymentInfo == null && pimPayAmount
            != null && owedPrice != null
            && airPortFee != null && discountAmt != null
            && toll != null && discountPercent != null &&
            destLat != null && destLon != null){
            setReceiptPaymentInfo(ReceiptPaymentInfo(tripId, pimPayAmount,owedPrice,0.0,0.0,airPortFee, discountAmt, toll, discountPercent,destLat, destLon, null))
        }
    }

    fun setTripPaymentInfo(tripId: String,  driverId: Int, vehicleId: String, tripNumber: Int, amountPassedToSquareWithTip: Double?, pimPayAmount: Double?, receiptPaymentInfo: ReceiptPaymentInfo? ){
        if(pimPayAmount != 0.0){
            tripPayment =  TripPayment(tripId, driverId, vehicleId, tripNumber, amountPassedToSquareWithTip, pimPayAmount, receiptPaymentInfo )
            Log.i("Payment","Set trip payment info. $tripId, $driverId, $vehicleId, $tripNumber, $amountPassedToSquareWithTip, $pimPayAmount, $receiptPaymentInfo")
            broadcastReceived = true
            broadcastReceivedMLD.postValue(broadcastReceived)
        }
    }

    fun getTripPaymentInfo() = tripPayment

    fun updateAmountPassedToSquareAfterPayment(amount: Double){
        tripPayment?.amountPassedToSquareWithTip = amount
    }

    fun getAmountPassedToSquare() = tripPayment?.amountPassedToSquareWithTip


    fun setPaymentInfo(mPaymentInfo: PaymentInfo){
        paymentInfo = mPaymentInfo
    }

    fun getPaymentInfo() = paymentInfo



    fun clearTripValues(){
        tripPayment = null
        receiptPaymentInfo = null
        broadcastReceived = false
        broadcastReceivedMLD.postValue(broadcastReceived)
    }


}

