package com.poker.jacksorbetter.main

import android.content.Context
import com.poker.jacksorbetter.cardgame.Card
import com.poker.jacksorbetter.cardgame.Deck
import com.poker.jacksorbetter.cardgame.Evaluate
import com.poker.jacksorbetter.handevaluator.*
import timber.log.Timber

data class AIDecision (
    var numTrials: Int,
    var bet: Int,
    var hand: List<Card>,
    var sortedRankedHands: List<Pair<List<Card>, Double>>
)

object AIPlayer {

    const val DEBUG = false

    fun calculateBestHands(context: Context, bet: Int, hand: List<Card>, numTrials: Int) : AIDecision {

        val hands = hand.powerset()
        val handsEvaluated = mutableListOf<Pair<List<Card>, Double>>()


        for (h in hands) {
            val expectedValue =
                monteCarloEvaluation(
                    context,
                    bet,
                    h.toList(),
                    numTrials
                )
            handsEvaluated.add(Pair(h.toMutableList(), expectedValue))
        }

        handsEvaluated.sortByDescending { it.second }

        for((idx,h) in handsEvaluated.take(if(DEBUG) 32 else 5).withIndex()) {
            Timber.d("$idx) hand ${h.first} score ${h.second}")
        }
        return AIDecision(
            numTrials,
            bet,
            hand,
            handsEvaluated
        )
    }

    private fun monteCarloEvaluation(context: Context, bet: Int, hand: List<Card>, numTrials: Int) : Double {
        if(DEBUG) {
            Timber.d("====== Monte Carlo Simulation ======")
        }

        var trial = 0
        var expectedPayout = 0.0
        val evals = mutableListOf<HandRank>()
        while (trial < numTrials) {
            val tempHand = Deck.draw5Random(hand.toMutableList())
            val tempHandPC = tempHand.map { card -> PokerCard(Rank(card.rank), Suit.parse(card.suit)) }.toTypedArray()

            val eval = HandEvaluator.evaluateSpecificHand(tempHandPC)
            evals.add(eval)
            val payout = PayOutHelper.calculatePayout(context, bet,
                convertEvalRank(
                    hand,
                    eval
                )
            )

            expectedPayout += payout
            trial += 1
        }

        if(DEBUG) {
            val evalCounts = evals.groupingBy { it }.eachCount()
            Timber.d("${hand.joinToString { it.toString() }} $evalCounts ${expectedPayout/numTrials}")
            Timber.d("====================================")
        }

        return expectedPayout/numTrials
    }

    private fun convertEvalRank(hand: List<Card>, rank: HandRank) : Evaluate.Hand{
        return when(rank) {
            HandRank.ROYAL_FLUSH -> Evaluate.Hand.ROYAL_FLUSH // todo
            HandRank.STRAIGHT_FLUSH -> Evaluate.Hand.STRAIGHT_FLUSH
            HandRank.FOUR_OF_A_KIND -> Evaluate.Hand.FOUR_OF_A_KIND
            HandRank.FULL_HOUSE -> Evaluate.Hand.FULL_HOUSE
            HandRank.FLUSH -> Evaluate.Hand.FLUSH
            HandRank.STRAIGHT -> Evaluate.Hand.STRAIGHT
            HandRank.THREE_OF_A_KIND -> Evaluate.Hand.THREE_OF_A_KIND
            HandRank.TWO_PAIR -> Evaluate.Hand.TWO_PAIRS
            HandRank.ONE_PAIR -> {
                // todo this has to be done. A better way is
                // to put this logic in HandEvaluator.java
                if (Evaluate.isPairJacksOrBetter(hand)) {
                    Evaluate.Hand.JACKS_OR_BETTER
                } else {
                    Evaluate.Hand.NOTHING
                }
            }
            HandRank.HIGH_CARD -> Evaluate.Hand.NOTHING
        }
    }

    private fun <T> Collection<T>.powerset(): Set<Set<T>> = when {
        isEmpty() -> setOf(setOf())
        else -> drop(1).powerset().let { it + it.map { it + first() } }
    }
}