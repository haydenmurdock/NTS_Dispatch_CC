package com.example.driver_square_payment_portal

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.squareup.sdk.reader.ReaderSdk

@SuppressLint("Registered")
class DriverSquarePaymentPortal : Application() {
    companion object {
        lateinit var instance: DriverSquarePaymentPortal
        lateinit var context: Context
    }

    override fun onCreate() {
        ReaderSdk.initialize(this)
        super.onCreate()
        try {
            instance = this
            context = instance.applicationContext
        }catch (e: Exception){}
        registerActivityLifecycleCallbacks(LifeCycleCallbacks())
    }
}