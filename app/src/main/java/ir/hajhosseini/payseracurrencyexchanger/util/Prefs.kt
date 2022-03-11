package ir.hajhosseini.payseracurrencyexchanger.util

import android.content.Context
import android.content.SharedPreferences
import ir.hajhosseini.payseracurrencyexchanger.PREFS

class Prefs(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    var firstTimeOpenApp: Boolean
        get() = preferences.getBoolean(PREFS, true)
        set(value) = preferences.edit().putBoolean(PREFS, value).apply()
}