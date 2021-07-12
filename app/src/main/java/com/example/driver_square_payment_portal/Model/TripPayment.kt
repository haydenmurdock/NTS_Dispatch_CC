package com.example.driver_square_payment_portal.Model


// we will need trip number, driver Id, tripId, vehicleId
data class TripPayment(val tripId: String,
                       val driverId: String,
                       val vehicleId: String,
                       val tripNumber: String,
                       var amountPassedToSquareWithTip: Double?,
                       val pimPayAmount: Double?,
                       var receiptPaymentInfo: ReceiptPaymentInfo?
                       )