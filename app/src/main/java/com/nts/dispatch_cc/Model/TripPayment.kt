package com.nts.dispatch_cc.Model


// we will need trip number, driver Id, tripId, vehicleId
data class TripPayment(val tripId: String,
                       val driverId: Int,
                       val vehicleId: String,
                       val tripNumber: Int,
                       var amountPassedToSquareWithTip: Double?,
                       val pimPayAmount: Double?,
                       var receiptPaymentInfo: ReceiptPaymentInfo?
                       )