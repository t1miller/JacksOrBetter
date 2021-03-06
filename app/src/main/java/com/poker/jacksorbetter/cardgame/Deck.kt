package com.poker.jacksorbetter.cardgame

import Card



object Deck {

    private var cards = mutableListOf<Card>()

    init {
        newDeck()
    }

    fun newDeck() {
        cards.clear()
        for (suit in Card.SUITS) {
            for (face in Card.FACES){
                cards.add(
                    Card(
                        Card.FACES.indexOf(face) + 2,
                        suit
                    )
                )
            }
        }
        cards.shuffle()
    }

    fun draw5(): MutableList<Card> {
        val hand = mutableListOf<Card>()
        for (i in 0..4) {
            hand.add(draw1())
        }
        return hand
    }

    fun draw1(): Card {
        return cards.removeAt(0)
    }

    fun draw5Random(hand: MutableList<Card>) : List<Card>{
        while (hand.size < 5) {
            val suit = Card.SUITS[(0..3).random()]
            val rank = (2..14).random()
            val randomCard = Card(rank, suit)
            if (!hand.contains(randomCard)){
                hand.add(randomCard)
            }
        }
        return hand
    }

    fun suitColor(suit: Char) : String {
        return when(suit) {
            's' -> "black"
            'h' -> "red"
            'd' -> "red"
            'c' -> "black"
            else -> "green"
        }
    }

    fun isSuitRed(suit: Char) : Boolean {
        return suitColor(suit) == "red"
    }

}

class Deck2{


    private var cards = mutableListOf<Card>()


    init {
        newDeck()
    }

    private fun newDeck() {
        cards.clear()
        for (suit in Card.SUITS) {
            for (face in Card.FACES){
                val card = Card(Card.FACES.indexOf(face) + 2, suit)
                cards.add(card)
            }
        }
        cards.shuffle()
    }

    fun draw1(): Card {
        return cards.removeAt(0)
    }

    fun draw5(): MutableList<Card> {
        val hand = mutableListOf<Card>()
        for (i in 0..4) {
            hand.add(draw1())
        }
        return hand
    }

    fun removeCards(cardsToRemove: MutableList<Card>) {
        cards.removeAll(cardsToRemove)
    }

    fun getCards() : MutableList<Card> {
        return cards
    }
}