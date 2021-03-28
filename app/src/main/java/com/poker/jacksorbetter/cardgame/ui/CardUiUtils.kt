package com.poker.jacksorbetter.cardgame.ui

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.poker.jacksorbetter.PokerApplication
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.cardgame.Card
import com.poker.jacksorbetter.settings.SettingsUtils

object CardUiUtils {

    fun showCards(cardViews: List<ImageView>?, fullHand: List<Card>?) {
        fullHand?.forEachIndexed { index, card ->
            cardViews?.get(index)?.setImageResource(cardToImage(card))
        }
    }

    fun showCardBacks(cardViews: List<ImageView>?) {
        cardViews?.forEach {
            it.setImageResource(SettingsUtils.getCardBack(PokerApplication.applicationContext()))
        }
    }

    fun makeCardsVisibile(cardViews: List<ImageView>?) {
        cardViews?.forEach {
            it.visibility = View.VISIBLE
        }
    }

    fun highlightHeldCards(holdsViews: List<TextView>?, fullHand: List<Card>?, heldHand: List<Card>?) {
        fullHand?.forEachIndexed { index, card ->
            if(heldHand?.contains(card) == true){
                holdsViews?.get(index)?.visibility = View.VISIBLE
            }
        }
    }

    fun cardToImage(card: Card?) : Int{
        if(card == null) return R.drawable.card_back_default
        return when(card.rank) {
            2 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.two_of_spades
                    }
                    'h' -> {
                        R.drawable.two_of_hearts
                    }
                    'd' -> {
                        R.drawable.two_of_diamonds
                    }
                    'c' -> {
                        R.drawable.two_of_clubs
                    }
                    else -> {
                        -1
                    }
                }
            }
            3 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.three_of_spades
                    }
                    'h' -> {
                        R.drawable.three_of_hearts
                    }
                    'd' -> {
                        R.drawable.three_of_diamonds
                    }
                    'c' -> {
                        R.drawable.three_of_clubs
                    }
                    else -> {
                        -1
                    }
                }
            }
            4 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.four_of_spades
                    }
                    'h' -> {
                        R.drawable.four_of_hearts
                    }
                    'd' -> {
                        R.drawable.four_of_diamonds
                    }
                    'c' -> {
                        R.drawable.four_of_clubs
                    }
                    else -> {
                        -1
                    }
                }
            }
            5 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.five_of_spades
                    }
                    'h' -> {
                        R.drawable.five_of_hearts
                    }
                    'd' -> {
                        R.drawable.five_of_diamonds
                    }
                    'c' -> {
                        R.drawable.five_of_clubs
                    }
                    else -> {
                        -1
                    }
                }
            }
            6 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.six_of_spades
                    }
                    'h' -> {
                        R.drawable.six_of_hearts
                    }
                    'd' -> {
                        R.drawable.six_of_diamonds
                    }
                    'c' -> {
                        R.drawable.six_of_clubs
                    }
                    else -> {
                        -1
                    }
                }
            }
            7 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.seven_of_spades
                    }
                    'h' -> {
                        R.drawable.seven_of_hearts
                    }
                    'd' -> {
                        R.drawable.seven_of_diamonds
                    }
                    'c' -> {
                        R.drawable.seven_of_clubs
                    }
                    else -> {
                        -1
                    }
                }
            }
            8 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.eight_of_spades
                    }
                    'h' -> {
                        R.drawable.eight_of_hearts
                    }
                    'd' -> {
                        R.drawable.eight_of_diamonds
                    }
                    'c' -> {
                        R.drawable.eight_of_clubs
                    }
                    else -> {
                        -1
                    }
                }
            }
            9 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.nine_of_spades
                    }
                    'h' -> {
                        R.drawable.nine_of_hearts
                    }
                    'd' -> {
                        R.drawable.nine_of_diamonds
                    }
                    'c' -> {
                        R.drawable.nine_of_clubs
                    }
                    else -> {
                        -1
                    }
                }
            }
            10 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.ten_of_spades
                    }
                    'h' -> {
                        R.drawable.ten_of_hearts
                    }
                    'd' -> {
                        R.drawable.ten_of_diamonds
                    }
                    'c' -> {
                        R.drawable.ten_of_clubs
                    }
                    else -> {
                        -1
                    }
                }
            }
            11 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.jack_of_spades
                    }
                    'h' -> {
                        R.drawable.jack_of_hearts
                    }
                    'd' -> {
                        R.drawable.jack_of_diamonds
                    }
                    'c' -> {
                        R.drawable.jack_of_clubs
                    }
                    else -> {
                        -1
                    }
                }
            }
            12 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.queen_of_spades
                    }
                    'h' -> {
                        R.drawable.queen_of_hearts
                    }
                    'd' -> {
                        R.drawable.queen_of_diamonds
                    }
                    'c' -> {
                        R.drawable.queen_of_clubs
                    }
                    else -> {
                        -1
                    }
                }
            }
            13 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.king_of_spades
                    }
                    'h' -> {
                        R.drawable.king_of_hearts
                    }
                    'd' -> {
                        R.drawable.king_of_diamonds
                    }
                    'c' -> {
                        R.drawable.king_of_clubs
                    }
                    else -> {
                        -1
                    }
                }
            }
            14 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.ace_of_spades
                    }
                    'h' -> {
                        R.drawable.ace_of_hearts
                    }
                    'd' -> {
                        R.drawable.ace_of_diamonds
                    }
                    'c' -> {
                        R.drawable.ace_of_clubs
                    }
                    else -> {
                        -1
                    }
                }
            }
            else -> {
                SettingsUtils.getCardBack(PokerApplication.applicationContext())
            }
        }
    }
}