package com.poker.jacksorbetter.main

import java.math.BigDecimal
import java.math.RoundingMode

object CommonUiUtils {

    fun Double.toFormattedStringThreeDecimals() : Double{
        return BigDecimal.valueOf(this)
            .setScale(3, RoundingMode.HALF_UP).toDouble()
    }
}