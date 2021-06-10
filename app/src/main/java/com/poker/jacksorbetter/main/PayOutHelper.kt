package com.poker.jacksorbetter.main

import Card
import android.content.Context
import com.poker.jacksorbetter.cardgame.Deck
import com.poker.jacksorbetter.cardgame.Evaluate
import com.poker.jacksorbetter.settings.SettingsUtils
import timber.log.Timber

object PayOutHelper {

    enum class PAY_TABLE_TYPES{
        _6_5_95,
        _7_5_96,
        _8_5_97,
        _9_5_98,
        _9_6_99,
    }


    private val payoutMap = mapOf(
        PAY_TABLE_TYPES._9_6_99 to mapOf(
            Evaluate.Hand.ROYAL_FLUSH to listOf(250, 500, 750, 1000, 4000),
            Evaluate.Hand.STRAIGHT_FLUSH to listOf(50, 100, 150, 200, 250),
            Evaluate.Hand.FOUR_OF_A_KIND to listOf(25, 50, 75, 100, 125),
            Evaluate.Hand.FULL_HOUSE to listOf(9, 18, 27, 36, 45),
            Evaluate.Hand.FLUSH to listOf(6, 12, 18, 24, 30),
            Evaluate.Hand.STRAIGHT to listOf(4, 8, 12, 16, 20),
            Evaluate.Hand.THREE_OF_A_KIND to listOf(3, 6, 9, 12, 15),
            Evaluate.Hand.TWO_PAIRS to listOf(2, 4, 6, 8, 10),
            Evaluate.Hand.JACKS_OR_BETTER to listOf(1, 2, 3, 4, 5),
            Evaluate.Hand.NOTHING to listOf(-1, -2, -3, -4, -5)),

        PAY_TABLE_TYPES._9_5_98 to mapOf(
            Evaluate.Hand.ROYAL_FLUSH to listOf(250, 500, 750, 1000, 4000),
            Evaluate.Hand.STRAIGHT_FLUSH to listOf(50, 100, 150, 200, 250),
            Evaluate.Hand.FOUR_OF_A_KIND to listOf(25, 50, 75, 100, 125),
            Evaluate.Hand.FULL_HOUSE to listOf(9, 18, 27, 36, 45),
            Evaluate.Hand.FLUSH to listOf(5, 10, 15, 20, 25),
            Evaluate.Hand.STRAIGHT to listOf(4, 8, 12, 16, 20),
            Evaluate.Hand.THREE_OF_A_KIND to listOf(3, 6, 9, 12, 15),
            Evaluate.Hand.TWO_PAIRS to listOf(2, 4, 6, 8, 10),
            Evaluate.Hand.JACKS_OR_BETTER to listOf(1, 2, 3, 4, 5),
            Evaluate.Hand.NOTHING to listOf(-1, -2, -3, -4, -5)),

        PAY_TABLE_TYPES._8_5_97 to mapOf(
            Evaluate.Hand.ROYAL_FLUSH to listOf(250, 500, 750, 1000, 4000),
            Evaluate.Hand.STRAIGHT_FLUSH to listOf(50, 100, 150, 200, 250),
            Evaluate.Hand.FOUR_OF_A_KIND to listOf(25, 50, 75, 100, 125),
            Evaluate.Hand.FULL_HOUSE to listOf(8, 16, 24, 32, 40),
            Evaluate.Hand.FLUSH to listOf(5, 10, 15, 20, 25),
            Evaluate.Hand.STRAIGHT to listOf(4, 8, 12, 16, 20),
            Evaluate.Hand.THREE_OF_A_KIND to listOf(3, 6, 9, 12, 15),
            Evaluate.Hand.TWO_PAIRS to listOf(2, 4, 6, 8, 10),
            Evaluate.Hand.JACKS_OR_BETTER to listOf(1, 2, 3, 4, 5),
            Evaluate.Hand.NOTHING to listOf(-1, -2, -3, -4, -5)),

        PAY_TABLE_TYPES._7_5_96 to mapOf(
            Evaluate.Hand.ROYAL_FLUSH to listOf(250, 500, 750, 1000, 4000),
            Evaluate.Hand.STRAIGHT_FLUSH to listOf(50, 100, 150, 200, 250),
            Evaluate.Hand.FOUR_OF_A_KIND to listOf(25, 50, 75, 100, 125),
            Evaluate.Hand.FULL_HOUSE to listOf(7, 14, 21, 28, 35),
            Evaluate.Hand.FLUSH to listOf(5, 10, 15, 20, 25),
            Evaluate.Hand.STRAIGHT to listOf(4, 8, 12, 16, 20),
            Evaluate.Hand.THREE_OF_A_KIND to listOf(3, 6, 9, 12, 15),
            Evaluate.Hand.TWO_PAIRS to listOf(2, 4, 6, 8, 10),
            Evaluate.Hand.JACKS_OR_BETTER to listOf(1, 2, 3, 4, 5),
            Evaluate.Hand.NOTHING to listOf(-1, -2, -3, -4, -5)),

        PAY_TABLE_TYPES._6_5_95 to mapOf(
            Evaluate.Hand.ROYAL_FLUSH to listOf(250, 500, 750, 1000, 4000),
            Evaluate.Hand.STRAIGHT_FLUSH to listOf(50, 100, 150, 200, 250),
            Evaluate.Hand.FOUR_OF_A_KIND to listOf(25, 50, 75, 100, 125),
            Evaluate.Hand.FULL_HOUSE to listOf(6, 12, 18, 24, 30),
            Evaluate.Hand.FLUSH to listOf(5, 10, 15, 20, 25),
            Evaluate.Hand.STRAIGHT to listOf(4, 8, 12, 16, 20),
            Evaluate.Hand.THREE_OF_A_KIND to listOf(3, 6, 9, 12, 15),
            Evaluate.Hand.TWO_PAIRS to listOf(2, 4, 6, 8, 10),
            Evaluate.Hand.JACKS_OR_BETTER to listOf(1, 2, 3, 4, 5),
            Evaluate.Hand.NOTHING to listOf(-1, -2, -3, -4, -5))
    )

    fun getPayTableRow(payTableType: PAY_TABLE_TYPES, index: Int) : List<Int> {
        val hand = Evaluate.Hand.values()[index]
        return getPayTableRow(payTableType, hand)
    }

    private fun getPayTableRow(payTableType: PAY_TABLE_TYPES, eval: Evaluate.Hand) : List<Int> {
        return payoutMap[payTableType]?.get(eval) ?: listOf(0,0,0,0,0)
    }

    fun calculatePayout(context: Context?, bet: Int?, evalHand: Evaluate.Hand?) : Int {
        if(bet == null) return -1
        return payoutMap[SettingsUtils.getPayoutTable(context)]?.get(evalHand)?.get(bet-1) ?: -1*bet
    }

    fun calculateBonusPayout(bet: Int?, card: Card?, isGuessRed: Boolean) : Int {
        if(card == null || bet == null) return -1
        val isCorrectGuess = Deck.isSuitRed(card.suit) && isGuessRed || !Deck.isSuitRed(card.suit) && !isGuessRed

        Timber.d("Calculate Bonus: card: %s guess: %s isCorrect: %s", card.toString(), if (!isGuessRed) "black" else "red", isCorrectGuess)

        return if (isCorrectGuess) {
            bet*2
        } else {
            0
        }
    }
}