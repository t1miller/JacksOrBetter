package com.poker.jacksorbetter.stats

import com.poker.jacksorbetter.cardgame.Evaluate
import java.util.ArrayList


object HandEvalCountContent {

    val ITEMS: MutableList<HandEvalCount> = ArrayList()

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

    private fun addItem(item: HandEvalCount) {
        ITEMS.add(item)
    }

    private fun createDummyItem(position: Int): HandEvalCount {
        val handTypes = Evaluate.Hand.values()
        return HandEvalCount(
            handTypes[position],
            0
        )
    }

    data class HandEvalCount(val eval: Evaluate.Hand?, val count: Int?) {
        override fun toString(): String = eval?.readableName + "count $count"
    }
}