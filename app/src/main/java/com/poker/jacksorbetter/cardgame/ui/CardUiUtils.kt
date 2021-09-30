package com.poker.jacksorbetter.cardgame.ui

import Card
import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.facebook.shimmer.ShimmerFrameLayout
import com.poker.jacksorbetter.PokerApplication
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.settings.SettingsUtils
import kotlin.random.Random

object CardUiUtils {

    fun startShimmer(context: Context, shimmerLayouts: MutableList<ShimmerFrameLayout>) {
        if(SettingsUtils.getCardBack(context) == R.drawable.card_back_gold) {
            for (shimmer in shimmerLayouts){
                shimmer.baseAlpha = 0.9F// opacity of non shimmer part of image
                shimmer.duration = Random.nextInt(900, 1400)
                shimmer.startShimmerAnimation()
            }
        }
    }

    fun tintCards(cardViews: List<ImageView>?, fullHand: List<Card>?, cardsToTint: List<Card>) {
        if(cardsToTint.isEmpty()) return
        fullHand?.forEachIndexed { index, c ->
            if (cardsToTint.contains(c)){
                cardViews?.get(index)?.setColorFilter(ContextCompat.getColor(PokerApplication.applicationContext(), R.color.colorGrey), android.graphics.PorterDuff.Mode.MULTIPLY)
            }
        }
    }

    fun unTintCards(cardViews: List<ImageView>?) {
        cardViews?.forEachIndexed { index, _ ->
            cardViews[index].colorFilter = null
        }
    }

    fun showCard(cardView: ImageView, card: Card?) {
        cardView.setImageResource(cardToImage(card))
    }

    fun showCards(cardViews: List<ImageView>?, fullHand: List<Card>?) {
        fullHand?.forEachIndexed { index, card ->
            cardViews?.get(index)?.setImageResource(cardToImage(card))
        }
    }

    fun showSmallCard(cardView: ImageView, card: Card?) {
        if(card == null) return
        cardView.setImageResource(cardSmallToImage(card))
    }

    fun showSmallCards(cardViews: List<ImageView>?, fullHand: List<Card>?) {
        if(cardViews == null || fullHand == null || cardViews.size != 5 || fullHand.size != 5) return
        fullHand.forEachIndexed { index, card ->
            cardViews[index].setImageResource(cardSmallToImage(card))
        }
    }

    fun showCardBack(cardView: ImageView) {
        cardView.setImageResource(SettingsUtils.getCardBack(PokerApplication.applicationContext()))
    }

    fun showCardBacks(cardViews: List<ImageView>?) {
        cardViews?.forEachIndexed { _,it ->
            it.setImageResource(SettingsUtils.getCardBack(PokerApplication.applicationContext()))
        }
    }

    fun showCardBacksSmall(cardViews: List<ImageView>?) {
        cardViews?.forEach {
            it.setImageResource(R.drawable.cardback_two_small)
        }
    }

    fun showCardBacksSmall(cardView: ImageView) {
        cardView.setImageResource(R.drawable.cardback_two_small)
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
        if(card == null) return SettingsUtils.getCardBack(PokerApplication.applicationContext())
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
                        SettingsUtils.getCardBack(PokerApplication.applicationContext())
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
                        SettingsUtils.getCardBack(PokerApplication.applicationContext())
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
                        SettingsUtils.getCardBack(PokerApplication.applicationContext())
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
                        SettingsUtils.getCardBack(PokerApplication.applicationContext())
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
                        SettingsUtils.getCardBack(PokerApplication.applicationContext())
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
                        SettingsUtils.getCardBack(PokerApplication.applicationContext())
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
                        SettingsUtils.getCardBack(PokerApplication.applicationContext())
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
                        SettingsUtils.getCardBack(PokerApplication.applicationContext())
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
                        SettingsUtils.getCardBack(PokerApplication.applicationContext())
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
                        SettingsUtils.getCardBack(PokerApplication.applicationContext())
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
                        SettingsUtils.getCardBack(PokerApplication.applicationContext())
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
                        SettingsUtils.getCardBack(PokerApplication.applicationContext())
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
                        SettingsUtils.getCardBack(PokerApplication.applicationContext())
                    }
                }
            }
            else -> {
                SettingsUtils.getCardBack(PokerApplication.applicationContext())
            }
        }
    }

    private fun cardSmallToImage(card: Card?) : Int{
        if(card == null) return R.drawable.cardback_two_small
        return when(card.rank) {
            2 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.two_spade_small
                    }
                    'h' -> {
                        R.drawable.two_hearts_small
                    }
                    'd' -> {
                        R.drawable.two_diamonds_small
                    }
                    'c' -> {
                        R.drawable.two_clubs_small
                    }
                    else -> {
                        R.drawable.cardback_two_small
                    }
                }
            }
            3 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.three_spade_small
                    }
                    'h' -> {
                        R.drawable.three_hearts_small
                    }
                    'd' -> {
                        R.drawable.three_diamonds_small
                    }
                    'c' -> {
                        R.drawable.three_clubs_small
                    }
                    else -> {
                        R.drawable.cardback_two_small
                    }
                }
            }
            4 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.four_spade_small
                    }
                    'h' -> {
                        R.drawable.four_hearts_small
                    }
                    'd' -> {
                        R.drawable.four_diamonds_small
                    }
                    'c' -> {
                        R.drawable.four_clubs_small
                    }
                    else -> {
                        R.drawable.cardback_two_small
                    }
                }
            }
            5 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.five_spade_small
                    }
                    'h' -> {
                        R.drawable.five_hearts_small
                    }
                    'd' -> {
                        R.drawable.five_diamonds_small
                    }
                    'c' -> {
                        R.drawable.five_clubs_small
                    }
                    else -> {
                        R.drawable.cardback_two_small
                    }
                }
            }
            6 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.six_spade_small
                    }
                    'h' -> {
                        R.drawable.six_hearts_small
                    }
                    'd' -> {
                        R.drawable.six_diamonds_small
                    }
                    'c' -> {
                        R.drawable.six_clubs_small
                    }
                    else -> {
                        R.drawable.cardback_two_small
                    }
                }
            }
            7 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.seven_spade_small
                    }
                    'h' -> {
                        R.drawable.seven_hearts_small
                    }
                    'd' -> {
                        R.drawable.seven_diamonds_small
                    }
                    'c' -> {
                        R.drawable.seven_clubs_small
                    }
                    else -> {
                        R.drawable.cardback_two_small
                    }
                }
            }
            8 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.eight_spade_small
                    }
                    'h' -> {
                        R.drawable.eight_hearts_small
                    }
                    'd' -> {
                        R.drawable.eight_diamonds_small
                    }
                    'c' -> {
                        R.drawable.eight_clubs_small
                    }
                    else -> {
                        R.drawable.cardback_two_small
                    }
                }
            }
            9 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.nine_spade_small
                    }
                    'h' -> {
                        R.drawable.nine_hearts_small
                    }
                    'd' -> {
                        R.drawable.nine_diamonds_small
                    }
                    'c' -> {
                        R.drawable.nine_clubs_small
                    }
                    else -> {
                        R.drawable.cardback_two_small
                    }
                }
            }
            10 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.ten_spade_small
                    }
                    'h' -> {
                        R.drawable.ten_hearts_small
                    }
                    'd' -> {
                        R.drawable.ten_diamonds_small
                    }
                    'c' -> {
                        R.drawable.ten_clubs_small
                    }
                    else -> {
                        R.drawable.cardback_two_small
                    }
                }
            }
            11 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.jack_spade_small
                    }
                    'h' -> {
                        R.drawable.jack_hearts_small
                    }
                    'd' -> {
                        R.drawable.jack_diamonds_small
                    }
                    'c' -> {
                        R.drawable.jack_clubs_small
                    }
                    else -> {
                        R.drawable.cardback_two_small
                    }
                }
            }
            12 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.queen_spade_small
                    }
                    'h' -> {
                        R.drawable.queen_heart_small
                    }
                    'd' -> {
                        R.drawable.queen_diamonds_small
                    }
                    'c' -> {
                        R.drawable.queen_clubs_small
                    }
                    else -> {
                        R.drawable.cardback_two_small
                    }
                }
            }
            13 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.king_spade_small
                    }
                    'h' -> {
                        R.drawable.king_hearts_small
                    }
                    'd' -> {
                        R.drawable.king_diamonds_small
                    }
                    'c' -> {
                        R.drawable.king_clubs_small
                    }
                    else -> {
                        R.drawable.cardback_two_small
                    }
                }
            }
            14 -> {
                when(card.suit) {
                    's' -> {
                        R.drawable.ace_spade_small
                    }
                    'h' -> {
                        R.drawable.ace_hearts_small
                    }
                    'd' -> {
                        R.drawable.ace_diamonds_small
                    }
                    'c' -> {
                        R.drawable.ace_clubs_small
                    }
                    else -> {
                        R.drawable.cardback_two_small
                    }
                }
            }
            else -> {
                R.drawable.cardback_two_small
            }
        }
    }
}