package com.poker.jacksorbetter.leaderboard

import com.poker.jacksorbetter.cardgame.Evaluate
import java.util.ArrayList

object HighScoreContent {

    val ITEMS: MutableList<ThreeCardHandEvalCount> = ArrayList()

    private val COUNT = Evaluate.Hand.values().count()

    init {
        // Add some sample items.
        for (i in 0 until COUNT) {
            addItem(
                    createDummyItem(
                            i
                    )
            )
        }
    }

    private fun addItem(item: ThreeCardHandEvalCount) {
        ITEMS.add(item)
    }

    private fun createDummyItem(position: Int): ThreeCardHandEvalCount {
        val handTypes = Evaluate.Hand.values()
        return ThreeCardHandEvalCount(handTypes[position], 0)
    }

    data class ThreeCardHandEvalCount(val eval: Evaluate.Hand?, val count: Int?) {
        override fun toString(): String = eval?.readableName + "count $count"
    }
}