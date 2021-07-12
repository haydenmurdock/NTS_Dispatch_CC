package com.example.driver_square_payment_portal.fragments.viewModels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class SettingsKeyboardViewModelFactory (
    private val keyBoardWatcher: KeyboardWatcher
): ViewModelProvider.NewInstanceFactory() {

    // We have to suppress the function because we are casting the ViewModel as a Generic T

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return SettingsKeyboardViewModel(
            keyBoardWatcher
        ) as T
    }
}