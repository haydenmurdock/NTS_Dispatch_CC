package com.nts.dispatch_cc.model

import android.app.Activity
import com.nts.dispatch_cc.helpers.LoggerHelper
import java.time.LocalTime
import java.time.temporal.ChronoUnit

class MAC(id: String, receivedTime: LocalTime, activity: Activity) {
    private var mId = id
    private var mReceivedTime = receivedTime
    private val logTag = "SQUARE"
    private val mActivity = activity

    init {
        LoggerHelper.writeToLog("MAC Address: $mId, Time: $mReceivedTime", logTag, activity)
    }

    fun isMACExpired(checkTime: LocalTime): Boolean {
        val timeBetween = ChronoUnit.HOURS.between(mReceivedTime, checkTime)
        LoggerHelper.writeToLog("MAC - Hours between last use: $timeBetween", logTag, mActivity)
        if (timeBetween > 0) {
            LoggerHelper.writeToLog("MAC is expired", logTag, mActivity)
            return true
        }
        LoggerHelper.writeToLog("MAC is not expired", logTag, mActivity)
        return false
    }
    fun getId(): String {
        return mId
    }
}