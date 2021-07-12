package com.example.driver_square_payment_portal.fragments.viewModels

import androidx.lifecycle.ViewModel

class SettingsKeyboardViewModel(
    private val keyboardWatcher : KeyboardWatcher
): ViewModel() {
    internal fun isQwertyKeyboardUp() = keyboardWatcher.getQwertyKeyboardStatus()

    internal fun qwertyKeyboardIsUp() = keyboardWatcher.qwertyKeyboardisUp()

    internal fun isPhoneKeyboardUp() = keyboardWatcher.getPhoneKeyboardStatus()

    internal fun phoneKeyboardIsUp() = keyboardWatcher.phoneKeyboardisUp()

    internal fun bothKeyboardsDown() = keyboardWatcher.bothKeyboardsareDown()

    internal fun keyboardIsGoingBackward() = keyboardWatcher.phoneNumberKeyboardIsGoingBackward()

    internal fun keyboardIsGoingForward() = keyboardWatcher.phoneNumberIsGoingForward()

    internal fun isKeyboardGoingForward() = keyboardWatcher.isphoneNumberKeyboardIsGoingForward()
}