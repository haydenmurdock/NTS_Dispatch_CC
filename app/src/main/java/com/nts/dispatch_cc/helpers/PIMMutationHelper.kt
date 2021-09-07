package com.nts.dispatch_cc.helpers

import android.util.Log
import com.amazonaws.amplify.generated.graphql.PimPaymentMadeMutation
import com.amazonaws.amplify.generated.graphql.SavePaymentDetailsMutation
import com.amazonaws.mobileconnectors.appsync.AWSAppSyncClient
import com.apollographql.apollo.GraphQLCall
import com.apollographql.apollo.api.Response
import com.apollographql.apollo.exception.ApolloException
import com.nts.dispatch_cc.DispatchCC
import com.nts.dispatch_cc.internal.ClientFactory
import type.PimPaymentMadeInput
import type.SavePaymentDetailsInput

object PIMMutationHelper {

    private var mAppSyncClient: AWSAppSyncClient? = null

    init {
        mAppSyncClient = ClientFactory.getInstance(DispatchCC.context)
    }

    fun updatePaymentType(
        vehicleId: String,
        paymentType: String,
        appSyncClient: AWSAppSyncClient,
        tripId: String
    ) {
        val updatePaymentTypeInput =
            PimPaymentMadeInput.builder().vehicleId(vehicleId).tripId(tripId)
                .paymentType(paymentType).build()

        appSyncClient.mutate(
            PimPaymentMadeMutation.builder().parameters(updatePaymentTypeInput).build()
        ).enqueue(
            mutationCallbackPaymentType
        )

    }

    private val mutationCallbackPaymentType =
        object : GraphQLCall.Callback<PimPaymentMadeMutation.Data>() {
            override fun onResponse(response: Response<PimPaymentMadeMutation.Data>) {
                Log.i("Results", "Meter Table Updated ${response.data()}")
            }

            override fun onFailure(e: ApolloException) {
                Log.e("Error", "There was an issue updating the MeterTable: $e")
            }
        }
    fun updatePaymentDetails(
        transactionId: String,
        tripNumber: Int,
        vehicleId: String,
        appSyncClient: AWSAppSyncClient,
        paymentMethod: String,
        tripId: String
    ) {
        val updatePaymentInput =
            SavePaymentDetailsInput.builder().paymentId(transactionId).tripNbr(tripNumber)
                .vehicleId(vehicleId).paymentMethod(paymentMethod).paymentSource("CC").tripId(tripId).build()

        appSyncClient.mutate(
            SavePaymentDetailsMutation.builder().parameters(updatePaymentInput).build()
        ).enqueue(
            mutationCallbackPaymentDetails
        )
    }

    private val mutationCallbackPaymentDetails =
        object : GraphQLCall.Callback<SavePaymentDetailsMutation.Data>() {
            override fun onResponse(response: Response<SavePaymentDetailsMutation.Data>) {
                Log.i(
                    "Payment AWS",
                    "payment details have been updated to ${response.data()?.savePaymentDetails()
                        .toString()}"
                )

            }

            override fun onFailure(e: ApolloException) {
                Log.e("Payment AWS", "There was an issue updating payment api: $e")
            }
        }

    fun pimPaymentSquareMutation(vehicleId: String, tripId: String, tipAmt: Double, cardInfo: String, tipPercent: Double, paidAmount: Double, transactionDate: String, transactionId: String){
        val pimPaymentInput = PimPaymentMadeInput.builder()
            .vehicleId(vehicleId)
            .tripId(tripId)
            .tipAmt(tipAmt)
            .cardInfo(cardInfo)
            .tipPercent(tipPercent)
            .pimPaidAmt(paidAmount)
            .pimTransDate(transactionDate)
            .pimTransId(transactionId)
            .paymentType("card")
            .build()

        mAppSyncClient?.mutate(PimPaymentMadeMutation.builder().parameters(pimPaymentInput).build())
            ?.enqueue(pimPaymentMadeCallback)
    }
    private val pimPaymentMadeCallback = object : GraphQLCall.Callback<PimPaymentMadeMutation.Data>() {
        override fun onResponse(response: Response<PimPaymentMadeMutation.Data>) {
            if(!response.hasErrors()){
            }
            if(response.hasErrors()){
            }
        }

        override fun onFailure(e: ApolloException) {
        }
    }
}