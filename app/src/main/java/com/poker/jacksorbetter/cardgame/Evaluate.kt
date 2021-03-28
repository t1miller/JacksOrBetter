
package com.poker.jacksorbetter.cardgame


object Evaluate {

    enum class Hand(val readableName: String){
        ROYAL_FLUSH("Royal flush"),
        STRAIGHT_FLUSH("Straight flush"),
        FOUR_OF_A_KIND("Four of a kind"),
        FULL_HOUSE("Full house"),
        FLUSH("Flush"),
        STRAIGHT("Straight"),
        THREE_OF_A_KIND("Three of a kind"),
        TWO_PAIRS("Two pairs"),
        JACKS_OR_BETTER("Jacks or better"),
        NOTHING("Nothing");
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

    fun isPairJackOrBetter(cards: List<Card>) : Boolean {
        for (card1 in cards) {
            for (card2 in cards){
                if (card1 != card2 &&
                    card1.rank == card2.rank &&
                    card1.rank > 10 ) {
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
            4 -> return if (isPairJackOrBetter(
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

//    fun testEval(){
//        // generate a bunch of hands
//        val evals = mutableListOf<Hand>()
//        for(i in 0..100000) {
//            evals.add(
//                analyzeHand(
//                    Deck.draw5Random(mutableListOf())
//                )
//            )
//        }
//        val evalCounts = evals.groupingBy { it }.eachCount()
//        Timber.d("$evalCounts")
//    }
//
//    fun testIsWinninngCards(){
//        val royalflush = mutableListOf<Card>()
//        for (i in 10..14) {
//            royalflush.add(Card(i,'s'))
//        }
//
//        val straightflush = royalflush.toMutableList()
//        straightflush[4].face = 9
//
//        val straight = straightflush.toMutableList()
//        straight[2].suit = 's'
//
//        val flush = straightflush.toMutableList()
//        flush[0].face = 2
//
//        val fullhouse = listOf(Card(10,'h'), Card(10,'d'), Card(10,'c'), Card(3,'s'), Card(3,'c'))
//
//        val fourofakind = fullhouse.toMutableList()
//        fourofakind[3].face = 2
//
//        val twoPair = fullhouse.toMutableList()
//        twoPair[2].face = 8
//
//        val jacksorbetter = twoPair.toMutableList()
//        jacksorbetter[2].face = 9
//
//        val nothing = jacksorbetter.toMutableList()
//        nothing[0].face = 11
//    }
}