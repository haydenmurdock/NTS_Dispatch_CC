package com.example.driver_square_payment_portal.fragments

import androidx.lifecycle.ViewModel
import com.example.driver_square_payment_portal.Helpers.SquareHelper

class CheckOAuthViewModel : ViewModel() {
    fun isSquareAuthorized() = SquareHelper.isSquareAuthorized()
}
