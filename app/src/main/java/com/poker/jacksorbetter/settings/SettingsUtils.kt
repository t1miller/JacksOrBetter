package com.poker.jacksorbetter.settings

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.poker.jacksorbetter.PokerApplication
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.main.PayOutHelper
import timber.log.Timber

object SettingsUtils {

    object Defaults{
        const val CHOOSE_CARD_BACK = 2
        const val MONEY = 500
        const val MONTE_CARLO_TRIALS = 4500
        const val SOUND = true
        const val SOUND_FLIP = true
        const val SOUND_BONUS = true
        const val PAYOUT_TABLE = "9/6 – 99.54%"
        const val GOLDEN_GOD = false
        const val NUM_HANDS = 1
    }

    object Keys{
        const val SIGN_OUT = "sign_out"
        const val SIGN_IN = "sign_in"
        const val MONTE_CARLO_TRIALS = "montecarlo_trials_config6"
        const val PAYOUT_TABLE = "payout_table"
        const val RESET_MONEY = "reset_money"
        const val RESET_STATS = "reset_stats"
        const val SHARE_STATS = "share_stats"
        const val CHOOSE_CARD_BACK = "choose_cardback"
        const val TOTAL_MONEY = "money"
        const val SOUND = "sound"
        const val SOUND_FLIP = "sound_flip"
        const val SOUND_BONUS = "sound_bonus"
        const val GOLDEN_GOD = "golden_god"
        const val NUM_HANDS = "num_hands"
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
            R.drawable.card_back_mountain,
            R.drawable.cardback_empty,
            R.drawable.card_back_gold
        )
    }

    private val prefs = PreferenceManager.getDefaultSharedPreferences(PokerApplication.applicationContext())

    fun getNumHands(): Int {
        return prefs.getInt(
            Keys.NUM_HANDS,
            Defaults.NUM_HANDS
        )
    }

    fun setNumHands(numHands: Int?) {
        with(prefs.edit()){
            putInt(Keys.NUM_HANDS, numHands ?: getNumHands())
            apply()
        }
    }

    fun getNumTrials() : Int{
        return prefs.getInt(
            Keys.MONTE_CARLO_TRIALS,
            Defaults.MONTE_CARLO_TRIALS
        )
    }

    fun resetNumTrials() {
        with (prefs.edit()) {
            putInt(Keys.MONTE_CARLO_TRIALS, Defaults.MONTE_CARLO_TRIALS)
            apply()
        }
    }

    fun setMoney(money: Int) {
        with (prefs.edit()) {
            putInt(Keys.TOTAL_MONEY, money)
            apply()
        }
    }

    fun isSoundEnabled() : Boolean{
        return prefs.getBoolean(
            Keys.SOUND,
            Defaults.SOUND
        )
    }

    fun getMoney() : Int {
        return prefs.getInt(Keys.TOTAL_MONEY, Defaults.MONEY)
    }

    fun getPayoutTable() : PayOutHelper.PAY_TABLE_TYPES{
        return when(prefs.getString(Keys.PAYOUT_TABLE, Defaults.PAYOUT_TABLE)){
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

    fun setCardBack(position: Int) {
        with(prefs.edit()) {
            putInt(Keys.CHOOSE_CARD_BACK, position)
            apply()
        }
    }

    fun getCardBack() : Int {
        val position = prefs.getInt(
            Keys.CHOOSE_CARD_BACK,
            Defaults.CHOOSE_CARD_BACK
        )
        return CardBacks.cardbacks[position]
    }

    fun isFlipSoundEnabled() : Boolean{
        return prefs.getBoolean(
            Keys.SOUND_FLIP,
            Defaults.SOUND_FLIP
        )
    }

    fun isBonusSoundEnabled() : Boolean{
        return prefs.getBoolean(
            Keys.SOUND_BONUS,
            Defaults.SOUND_BONUS
        )
    }

    fun setGoldenGod(isGoldenGold: Boolean) : Boolean {
        if(isGoldenGold){
            setCardBack(CardBacks.cardbacks.size - 1)
        }
        with(prefs.edit()) {
            putBoolean(Keys.GOLDEN_GOD, isGoldenGold)
            apply()
        }
        return prefs.getBoolean(Keys.GOLDEN_GOD, Defaults.GOLDEN_GOD)
    }

    fun isGoldenGod() : Boolean {
        return prefs.getBoolean(Keys.GOLDEN_GOD, Defaults.GOLDEN_GOD)
    }

    fun showChangeCardBackDialog(context: Context)  {

        val dialog = Dialog(context).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setCancelable(true)
            setContentView(R.layout.change_card_dialog_layout)
        }

        val yesBtn = dialog.findViewById(R.id.btn_yes) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }

        val recyclerView = dialog.findViewById(R.id.recyclerView) as RecyclerView
        val adapter = CardBackAdapter(object : CardTapped {
            override fun onCardTapped(position: Int) {
                setCardBack(position)
                dialog.dismiss()
            }
        }, isGoldenGod())

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        Timber.d("showing cardback dialog")
        dialog.show()
        val window: Window? = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}