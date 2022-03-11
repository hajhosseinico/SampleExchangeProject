package ir.hajhosseini.payseracurrencyexchanger

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import ir.hajhosseini.payseracurrencyexchanger.util.Prefs

val prefs: Prefs by lazy {
    App.prefs!!
}

const val startAmount = 10000.0
const val startAmountBase = "EUR"
const val accessKey = "a873d00b309e7b845aec4277e8aa6cd0"
const val format = "1"
const val commissionPercent = 0.7
const val commissionMoreThanThisAmountIsFreeOfCommission = 200
const val everyXTransactionIsFreeOfCommission = 10
const val firstXTransactionIsFreeOfCommission = 5
const val callRateApiInterval = 5000L // milli seconds
const val PREFS = "app_prefs"

@HiltAndroidApp
class App : Application() {

    companion object {
        var prefs: Prefs? = null
        lateinit var instance: App
            private set
    }

    override fun onCreate() {
        super.onCreate()

        instance = this
        prefs = Prefs(applicationContext)
    }
}