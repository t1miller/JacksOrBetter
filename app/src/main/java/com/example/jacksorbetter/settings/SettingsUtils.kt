package com.example.jacksorbetter.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.example.jacksorbetter.ui.main.PayOutHelper

object SettingsUtils {

    object Defaults{
        const val MONEY = 2000
        const val MONTE_CARLO_TRIALS = 4000
        const val SOUND = true
        const val PAYOUT_TABLE = "9/6 – 99.54%"
    }

    object Keys{
        const val MONTE_CARLO_TRIALS = "montecarlo_trials"
        const val PAYOUT_TABLE = "payout_table"
        const val RESET_MONEY = "reset_money"
        const val RESET_STATS = "reset_stats"
        const val SHARE_STATS = "share_stats"
        const val TOTAL_MONEY = "money"
        const val SOUND = "sound"
    }

    fun getNumTrials(context: Context?) : Int{
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(
            Keys.MONTE_CARLO_TRIALS,
            Defaults.MONTE_CARLO_TRIALS
        )
    }

    fun setMoney(money: Int, context: Context) {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        with (preferences.edit()) {
            putInt(Keys.TOTAL_MONEY, money)
            apply()
        }
    }

    fun isSoundEnabled(context: Context) : Boolean{
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(
            Keys.SOUND,
            Defaults.SOUND
        )
    }

    fun resetMoney(context: Context) {
        setMoney(Defaults.MONEY, context)
    }

//    fun resetStats(context: Context) {
////        setMoney(Defaults.MONEY, context)
//        Toast.makeText(context, "TODO", Toast.LENGTH_LONG).show()
//    }

    fun getMoney(context: Context) : Int {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(Keys.TOTAL_MONEY, Defaults.MONEY)
    }

    fun getPayoutTable(context: Context?) : PayOutHelper.PAY_TABLE_TYPES{
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

        return when(preferences.getString(Keys.PAYOUT_TABLE, Defaults.PAYOUT_TABLE)){
            "6/5 – 95.12%" -> PayOutHelper.PAY_TABLE_TYPES._6_5_95
            "7/5 – 96.17%" -> PayOutHelper.PAY_TABLE_TYPES._7_5_96
            "8/5 – 97.25%" -> PayOutHelper.PAY_TABLE_TYPES._8_5_97
            "9/5 – 98.33%" -> PayOutHelper.PAY_TABLE_TYPES._9_5_98
            "9/6 – 99.54%" -> PayOutHelper.PAY_TABLE_TYPES._9_6_99
            else -> {
                PayOutHelper.PAY_TABLE_TYPES._9_6_99
            }
        }
    }
}