package com.poker.jacksorbetter.main

object CommonUiUtils {

    fun Double.toFormattedStringThreeDecimals() : Double{
        return String.format("%.3f", this).toDouble()
    }
}