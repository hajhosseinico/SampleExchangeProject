package ir.hajhosseini.payseracurrencyexchanger.util

import android.content.Context
import android.net.ConnectivityManager
import javax.inject.Inject

/**
 * Checking if device is connected into internet of not
 */
class InternetStatus
@Inject
constructor(private val context: Context) {
    fun isInternetAvailable(): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return !cm.isActiveNetworkMetered
    }
}