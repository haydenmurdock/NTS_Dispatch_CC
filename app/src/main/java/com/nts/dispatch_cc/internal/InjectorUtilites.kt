package com.nts.dispatch_cc.internal

import com.nts.dispatch_cc.fragments.viewModels.KeyboardWatcher
import com.nts.dispatch_cc.fragments.viewModels.SettingsKeyboardViewModelFactory

object InjectorUtilites {
    fun provideSettingKeyboardModelFactory(): SettingsKeyboardViewModelFactory {
        val keyboardWatcher = KeyboardWatcher
        return SettingsKeyboardViewModelFactory(
            keyboardWatcher
        )
    }
}