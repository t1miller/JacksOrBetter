package com.poker.jacksorbetter.main

import java.lang.Exception
import java.math.BigDecimal
import java.math.RoundingMode

object CommonUiUtils {

    fun Double.toFormattedStringThreeDecimals() : Double{
        var result = this
        try {
            result = BigDecimal.valueOf(this)
                .setScale(3, RoundingMode.HALF_UP).toDouble()
        } catch (e: Exception){}
        return result
    }
}