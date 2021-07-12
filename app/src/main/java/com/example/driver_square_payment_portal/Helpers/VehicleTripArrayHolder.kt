package com.example.driver_square_payment_portal.Helpers

import android.util.Log
import com.example.driver_square_payment_portal.Model.PaymentInfo
import com.example.driver_square_payment_portal.Model.ReceiptPaymentInfo
import com.example.driver_square_payment_portal.Model.TripPayment

object VehicleTripArrayHolder {
    private  var receiptPaymentInfo: ReceiptPaymentInfo? = null
    private var paymentInfo: PaymentInfo? = null
    var tripPayment: TripPayment? = null

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

    fun updateReceiptPaymentInfo(tripId: String, pimPayAmount: Double?, owedPrice: Double?, tipAmt: Double?, tipPercent: Double?, airPortFee: Double?, discountAmt: Double?, toll: Double?, discountPercent: Double?, destLat: Double?, destLon: Double?){
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
        }
        if(receiptPaymentInfo == null && pimPayAmount
            != null && owedPrice != null
            && airPortFee != null && discountAmt != null
            && toll != null && discountPercent != null &&
            destLat != null && destLon != null){
            setReceiptPaymentInfo(ReceiptPaymentInfo(tripId, pimPayAmount,owedPrice,0.0,0.0,airPortFee, discountAmt, toll, discountPercent,destLat, destLon))
        }
    }

    fun setTripPaymentInfo(tripId: String,  driverId: String, vehicleId: String, tripNumber: String, amountPassedToSquareWithTip: Double?, pimPayAmount: Double?, receiptPaymentInfo: ReceiptPaymentInfo? ){

       tripPayment =  TripPayment(tripId, driverId, vehicleId, tripNumber, amountPassedToSquareWithTip, pimPayAmount, receiptPaymentInfo )
        Log.i("Payment","Set trip payment info. $tripId, $driverId, $vehicleId, $tripNumber, $amountPassedToSquareWithTip, $pimPayAmount, $receiptPaymentInfo")
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
    }


}

