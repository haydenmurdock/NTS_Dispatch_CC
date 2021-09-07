package com.nts.dispatch_cc.fragments

import androidx.lifecycle.ViewModel
import com.nts.dispatch_cc.helpers.SquareHelper

class CheckOAuthViewModel : ViewModel() {
    fun isSquareAuthorized() = SquareHelper.isSquareAuthorized()
}
