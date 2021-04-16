package com.poker.jacksorbetter.settings

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.main.PayOutHelper
import timber.log.Timber

object SettingsUtils {

    object Defaults{
        const val CHOOSE_CARDBACK = 0
        const val MONEY = 500
        const val MONTE_CARLO_TRIALS = 5000
        const val SOUND = true
        const val SOUND_FLIP = true
        const val SOUND_LOSE = true
        const val SOUND_BONUS = true
        const val SHEEP_MODE = false
        const val PAYOUT_TABLE = "9/6 – 99.54%"
        const val GOLDEN_GOD = false
    }

    object Keys{
        const val SIGN_OUT = "sign_out"
        const val SIGN_IN = "sign_in"
        const val MONTE_CARLO_TRIALS = "montecarlo_trials"
        const val PAYOUT_TABLE = "payout_table"
        const val RESET_MONEY = "reset_money"
        const val RESET_STATS = "reset_stats"
        const val SHARE_STATS = "share_stats"
        const val CHOOSE_CARDBACK = "choose_cardback"
        const val TOTAL_MONEY = "money"
        const val SOUND = "sound"
        const val SOUND_LOSE = "sound_lose"
        const val SOUND_FLIP = "sound_flip"
        const val SOUND_BONUS = "sound_bonus"
        const val SHEEP_MODE = "sheep_mode"
        const val GOLDEN_GOD = "golden_god"
    }

    object CardBacks{
        val cardbacks = listOf(
            R.drawable.card_back_default,
            R.drawable.card_back_electric,
            R.drawable.card_back_flower,
            R.drawable.card_back_foot,
            R.drawable.card_back_gay,
            R.drawable.card_back_olympics,
            R.drawable.card_back_pinstriped,
            R.drawable.card_back_red,
            R.drawable.card_back_t_rex,
            R.drawable.cardback_empty,
            R.drawable.card_back_gold
        )
    }


    fun getNumTrials(context: Context?) : Int{
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(
            Keys.MONTE_CARLO_TRIALS,
            Defaults.MONTE_CARLO_TRIALS
        )
    }

    fun resetNumTrials(context: Context?) {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        with (preferences.edit()) {
            putInt(Keys.MONTE_CARLO_TRIALS, Defaults.MONTE_CARLO_TRIALS)
            apply()
        }
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

    fun isSheepModeEnabled(context: Context) : Boolean{
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(
            Keys.SHEEP_MODE,
            Defaults.SHEEP_MODE
        )
    }

//    fun resetMoney(context: Context) {
//        setMoney(Defaults.MONEY, context)
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

    fun setCardBack(position: Int, context: Context) {
        val preferences: SharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context)
        with(preferences.edit()) {
            putInt(Keys.CHOOSE_CARDBACK, position)
            apply()
        }
    }

    fun getCardBack(context: Context) : Int {
        val preferences: SharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context)
        val position = preferences.getInt(
            Keys.CHOOSE_CARDBACK,
            Defaults.CHOOSE_CARDBACK
        )
        return CardBacks.cardbacks[position]
    }

    fun isLoseSoundEnabled(context: Context) : Boolean{
        val preferences: SharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(
            Keys.SOUND_LOSE,
            Defaults.SOUND_LOSE
        )
    }

    fun isFlipSoundEnabled(context: Context) : Boolean{
        val preferences: SharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(
            Keys.SOUND_FLIP,
            Defaults.SOUND_FLIP
        )
    }

    fun isBonusSoundEnabled(context: Context) : Boolean{
        val preferences: SharedPreferences = android.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(
            Keys.SOUND_BONUS,
            Defaults.SOUND_BONUS
        )
    }

    fun setGoldenGod(context: Context, isGoldenGold: Boolean) : Boolean {
        if(isGoldenGold){
            setCardBack(CardBacks.cardbacks.size - 1, context)
        }
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        with(preferences.edit()) {
            putBoolean(Keys.GOLDEN_GOD, isGoldenGold)
            apply()
        }
        return preferences.getBoolean(Keys.GOLDEN_GOD, Defaults.GOLDEN_GOD)
    }

    fun isGoldenGod(context: Context) : Boolean {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(Keys.GOLDEN_GOD, Defaults.GOLDEN_GOD)
    }

    fun showChangeCardBackDialog(context: Context)  {

        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.change_card_dialog_layout)

        val yesBtn = dialog.findViewById(R.id.btn_yes) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }

        val recyclerView = dialog.findViewById(R.id.recyclerView) as RecyclerView
        val adapter = CardBackAdapter(object : CardTapped {
            override fun onCardTapped(position: Int) {
                setCardBack(position, context)
                dialog.dismiss()
                Toast.makeText(context, "cardback selected", Toast.LENGTH_LONG).show()
            }
        }, isGoldenGod(context))

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        Timber.d("showing cardback dialog")
        dialog.show()
        val window: Window? = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}