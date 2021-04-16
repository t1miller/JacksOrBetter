package com.poker.jacksorbetter.cardgame


object Evaluate {

    enum class Hand(val readableName: String, val paytableName: String){
        ROYAL_FLUSH("Royal Flush", "Royal Flush..........."),
        STRAIGHT_FLUSH("Straight Flush","Straight Flush......."),
        FOUR_OF_A_KIND("Four of a Kind", "Four of a Kind......."),
        FULL_HOUSE("Full House", "Full House............"),
        FLUSH("Flush", "Flush....................."),
        STRAIGHT("Straight", "Straight................."),
        THREE_OF_A_KIND("Three of a Kind", "Three of a Kind....."),
        TWO_PAIRS("Two pairs", "Two pairs.............."),
        JACKS_OR_BETTER("Jacks or Better", "Jacks or Better....."),
        NOTHING("Nothing", "Nothing...");
        // Dont change order

        companion object {
            fun handFromReadableName(name: String?) : Hand {
                for (mune in values()) {
                    if(name == mune.readableName) {
                        return mune
                    }
                }
                return NOTHING
            }
        }
    }

    private fun isStraight(cards: List<Card>): Boolean {
        val sorted = cards.sortedBy { it.rank }
        if (sorted[0].rank + 4 == sorted[4].rank) return true
        if (sorted[4].rank == 14 && sorted[0].rank == 2 && sorted[3].rank == 5) return true
        return false
    }

    private fun isFlush(cards: List<Card>): Boolean {
        val suit = cards[0].suit
        if (cards.drop(1).all { it.suit == suit }) return true
        return false
    }

    private fun checkIfFlushIsRoyalFlush(cards: List<Card>): Boolean {
        // 10 + 11 + 12 + 13 + 14 = 60
        return cards.sumBy { it.rank } == 60
    }

    fun isPairJacksOrBetter(cards: List<Card>) : Boolean {
        for (card1 in cards) {
            for (card2 in cards){
                if (card1 != card2 &&
                    card1.rank == card2.rank &&
                    card1.rank >= 11 ) {
                    return true
                }
            }
        }
        return false
    }

    fun analyzeHand(hand: List<Card>): Hand {
        val groups = hand.groupBy { it.rank }
        when (groups.size) {
            2 -> {
                if (groups.any { it.value.size == 4 }) return Hand.FOUR_OF_A_KIND
                return Hand.FULL_HOUSE
            }
            3 -> {
                if (groups.any { it.value.size == 3 }) return Hand.THREE_OF_A_KIND
                return Hand.TWO_PAIRS
            }
            4 -> return if (isPairJacksOrBetter(
                    hand
                )
            ) {
                Hand.JACKS_OR_BETTER
            } else {
                Hand.NOTHING
            }
            else -> {
                val flush =
                    isFlush(hand)
                val straight =
                    isStraight(hand)
                val royal = flush && straight && checkIfFlushIsRoyalFlush(
                    hand
                )
                return when {
                    royal -> Hand.ROYAL_FLUSH
                    flush && straight -> Hand.STRAIGHT_FLUSH
                    flush             -> Hand.FLUSH
                    straight          -> Hand.STRAIGHT
                    else              -> Hand.NOTHING
                }
            }
        }
    }

    fun getWinningCards(hand: List<Card>?): List<Boolean> {
        if(hand == null) return listOf(false, false, false, false, false)
        return when (analyzeHand(hand)) {
            Hand.ROYAL_FLUSH,
            Hand.STRAIGHT_FLUSH,
            Hand.FULL_HOUSE,
            Hand.FLUSH,
            Hand.STRAIGHT -> listOf(true,true,true,true,true)
            Hand.FOUR_OF_A_KIND,
            Hand.THREE_OF_A_KIND,
            Hand.JACKS_OR_BETTER -> repeatedRank(
                hand
            )
            Hand.TWO_PAIRS -> findTwoPairIndex(
                hand
            )
            Hand.NOTHING -> listOf(false, false, false, false, false)
        }
    }

    private fun repeatedRank(hand: List<Card>) : List<Boolean> {
        val repeatedList = mutableListOf(false, false, false, false, false)
        for (i in 0..4) {
            for (j in 0..4) {
                if (i != j && hand[i].rank == hand[j].rank){
                    val repeatedFace = hand[i].rank
                    for ((k,c) in hand.withIndex()) {
                        repeatedList[k] = c.rank == repeatedFace
                    }
                }
            }
        }
        return repeatedList
    }

    private fun findTwoPairIndex(hand: List<Card>) : List<Boolean> {
        val oddCardOut = mutableListOf<Boolean>()
        for (i in 0..4) {
            oddCardOut.add(
                hasPair(
                    hand[i],
                    hand
                )
            )
        }
        return oddCardOut
    }

    private fun hasPair(card: Card, hand: List<Card>) : Boolean {
        for (i in 0..4) {
            if (card.rank == hand[i].rank && card != hand[i]) {
                return true
            }
        }
        return false
    }
}