package com.example.driver_square_payment_portal.internal

import com.example.driver_square_payment_portal.fragments.viewModels.KeyboardWatcher
import com.example.driver_square_payment_portal.fragments.viewModels.SettingsKeyboardViewModelFactory

object InjectorUtilites {
    fun provideSettingKeyboardModelFactory(): SettingsKeyboardViewModelFactory {
        val keyboardWatcher = KeyboardWatcher
        return SettingsKeyboardViewModelFactory(
            keyboardWatcher
        )
    }
}