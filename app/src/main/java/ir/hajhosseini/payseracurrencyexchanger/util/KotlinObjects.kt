package ir.hajhosseini.payseracurrencyexchanger.util

import java.math.BigDecimal
import java.math.RoundingMode

object KotlinObjects {

    fun Double.removeDecimal(num: Double): BigDecimal? {
        return BigDecimal(num).setScale(2, RoundingMode.HALF_EVEN)
    }
}