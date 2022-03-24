package com.poker.jacksorbetter.main

import Card
import android.content.res.Resources
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.poker.jacksorbetter.PokerApplication
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.cardgame.Evaluate
import com.poker.jacksorbetter.cardgame.ui.CardUiUtils
import timber.log.Timber
import kotlin.system.measureTimeMillis


enum class State {
    CARD_BACK,
    HOLD,
    FLIP,
}

/**
 *  Examples of different Hands in adapter:
 *
 *  (this is slow on updates)
 *  hand1 = [2s,2h,2d,2c,8s] bet = 1 held = [2s,2h,2d,2c] state = HOLD eval=4_OF_A_KIND
 *  hand2 = [2s,2h,2d,2c,9s] bet = 1 held = [2s,2h,2d,2c] state = HOLD eval=4_OF_A_KIND
 *  hand3 = [2s,2h,2d,2c,10s] bet = 1 held = [2s,2h,2d,2c] state = HOLD eval=4_OF_A_KIND
 *  ...
 *
 *   OR
 *
 *  hand1 = [] bet = 1 held = [] state = CARD_BACK eval=NONE
 *  hand2 = [] bet = 1 held = [] state = CARD_BACK eval=NONE
 *  hand3 = [] bet = 1 held = [] state = CARD_BACK eval=NONE
 *  ...
 * */
data class Hand(
    var cards: List<Card> = List(5){ Card() },
    var held: List<Card> = listOf(),
    var eval: Evaluate.Hand = Evaluate.Hand.NOTHING,
    var bet: Int = 1,
    var state: State = State.CARD_BACK
)

class HandsAdapter : RecyclerView.Adapter<HandsAdapter.ViewHolder>() {

    private val differCallback = object : DiffUtil.ItemCallback<Hand>() {
        override fun areItemsTheSame(oldItem: Hand, newItem: Hand): Boolean {
            return false
        }
        override fun areContentsTheSame(oldItem: Hand, newItem: Hand): Boolean {
            return oldItem.eval == newItem.eval &&
                    oldItem.bet == newItem.bet &&
                    oldItem.state == newItem.state &&
                    oldItem.held.toSet() == newItem.held.toSet() &&
                    oldItem.cards.toSet() == newItem.cards.toSet()
        }
    }
    private val mDiffer: AsyncListDiffer<Hand> = AsyncListDiffer(this, differCallback)

    companion object {
        private const val LITTLE_CARD_THRESHOLD = 9
    }

    private fun submitList(list: List<Hand>) {
        mDiffer.submitList(list)
    }

    fun setHold(heldCards: List<Card>) {
        val hands = mDiffer.currentList.toList()
        hands.forEach { hand ->
            hand.held = heldCards
        }
        setState(State.HOLD)
    }

    fun setState(s: State) {
        val hands = mDiffer.currentList.toList()
        hands.forEach { hand ->
            hand.state = s
        }
        submitList(hands)
    }

    fun setBetAmount(b: Int) {
        val hands = mDiffer.currentList.toList()
        hands.forEach { hand ->
            hand.bet = b
        }
        setState(State.CARD_BACK)
    }

    fun setEvals(evals: MutableList<Evaluate.Hand>) {
        // the first eval is not part of the HandAdapter, its the main hand which is
        // handled in the MainFragment
        val evalsNoFirst = evals.drop(1).toMutableList()
        val hands = mDiffer.currentList.toList()
        hands.forEachIndexed { idx, hand ->
            hand.eval = evalsNoFirst[idx]
        }
        submitList(hands)
    }


    fun initHands(numHands: Int) {
        val hands = List(numHands){ Hand() }
        submitList(hands)
    }

    fun setHands(cards: List<List<Card>>) {
        val hands = mDiffer.currentList.toList()
        hands.forEachIndexed { idx, hand ->
            hand.cards = cards[idx]
        }
        submitList(hands)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_hand_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() : Int{
        return mDiffer.currentList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timeInMillis = measureTimeMillis {

            val hand = mDiffer.currentList[position]
            Timber.d("HandsAdapter onBindViewHolder() hand = $hand")

            resizeImage(holder.cardFronts)
            resizeEvalText(holder.handEval, holder.handEvalPay, holder.handEvalLayout, holder.betText)

            when(hand.state){
                State.CARD_BACK -> {
                    showBigOrSmallCardBack(holder.cardFronts)
                    holder.handEvalLayout.visibility = View.INVISIBLE
                    hand.held = listOf()
                }
                State.HOLD -> {
                    hand.cards.forEachIndexed { idx, card ->
                        if(card in hand.held){
                            showBigOrSmallCards(holder.cardFronts[idx], card)
                        } else {
                            showBigOrSmallCardBack(holder.cardFronts[idx])
                        }
                    }
                }
                State.FLIP -> {
                    showBigOrSmallCardBack(holder.cardFronts)
                    hand.cards.forEachIndexed { idx, card ->
                        showBigOrSmallCards(holder.cardFronts[idx], card)
                    }

                    if(hand.eval != Evaluate.Hand.NOTHING){
                        holder.handEvalPay.text = "${PayOutHelper.calculatePayout(hand.bet, hand.eval)}"
                        if (itemCount > LITTLE_CARD_THRESHOLD) {
                            holder.handEval.text = hand.eval.littleName
                        } else {
                            holder.handEval.text = hand.eval.readableName
                        }
                        showEval(holder.handEvalLayout)
                    } else {
                        holder.handEvalLayout.visibility = View.INVISIBLE
                    }
                }
            }
            holder.betText.text = "${hand.bet}"
        }
        Timber.d("onBindViewHolder() took $timeInMillis ms")
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cardFronts = listOf<ImageView>(
            view.findViewById(R.id.cardfront1),
            view.findViewById(R.id.cardfront2),
            view.findViewById(R.id.cardfront3),
            view.findViewById(R.id.cardfront4),
            view.findViewById(R.id.cardfront5)
        )
        val betText: TextView = view.findViewById(R.id.betCircleText)
        val handEval: TextView = view.findViewById(R.id.handEval)
        val handEvalPay: TextView = view.findViewById(R.id.handEvalPay)
        val handEvalLayout: ConstraintLayout = view.findViewById(R.id.winningLayout)
    }

    private fun resizeEvalText(handEval: TextView, handEvalPay: TextView, handEvalLayout: ConstraintLayout, betTextView: TextView) {
        when(itemCount){
            0, 1 -> {
                handEval.textSize = 16F
                handEvalPay.textSize = 20F
                handEval.layoutParams.width = densityToPx(150F)
                handEval.layoutParams.height = densityToPx(28F)
                handEvalPay.layoutParams.height = densityToPx(28F)
                handEvalPay.setBackgroundResource(R.color.goldenYellow)
                handEval.setBackgroundResource(R.color.red)
                handEvalLayout.setBackgroundResource(R.color.colorBlue)
                betTextView.textSize = 12F
                betTextView.layoutParams.width = densityToPx(15F)
                betTextView.layoutParams.height = densityToPx(15F)
            }
            2 -> {
                handEval.textSize = 14F
                handEvalPay.textSize = 18F
                handEval.layoutParams.width = densityToPx(100F)
                handEval.layoutParams.height = densityToPx(25F)
                handEvalPay.layoutParams.height = densityToPx(25F)
                handEvalLayout.setBackgroundResource(R.color.colorBlue)
                betTextView.textSize = 12F
                betTextView.layoutParams.width = densityToPx(15F)
                betTextView.layoutParams.height = densityToPx(15F)
            }
            3, 4 -> {
                handEval.textSize = 12F
                handEvalPay.textSize = 14F
                handEval.layoutParams.width = densityToPx(90F)
                handEval.layoutParams.height = densityToPx(21F)
                handEvalPay.layoutParams.height = densityToPx(21F)
                handEvalLayout.setBackgroundResource(R.color.colorBlue)
                betTextView.textSize = 12F
                betTextView.layoutParams.width = densityToPx(15F)
                betTextView.layoutParams.height = densityToPx(15F)
            }
            5, 6, 7, 8, 9 -> {
                handEval.textSize = 10F
                handEvalPay.textSize = 12F
                handEval.layoutParams.width = densityToPx(70F)
                handEval.layoutParams.height = densityToPx(20F)
                handEvalPay.layoutParams.height = densityToPx(20F)
                handEvalLayout.setBackgroundResource(R.color.colorBlue)
                betTextView.textSize = 12F
                betTextView.layoutParams.width = densityToPx(15F)
                betTextView.layoutParams.height = densityToPx(15F)
            }
            10, 11, 12, 13, 14, 15, 16 -> {
                handEval.textSize = 9F
                handEvalPay.textSize = 11F
                handEval.layoutParams.width = densityToPx(50F)
                handEval.layoutParams.height = densityToPx(12F)
                handEvalPay.layoutParams.height = densityToPx(12F)
                handEvalLayout.setBackgroundResource(R.color.colorBlueTransparent)
                betTextView.textSize = 11F
                betTextView.layoutParams.width = densityToPx(13F)
                betTextView.layoutParams.height = densityToPx(13F)
            } else -> {
                handEval.textSize = 6F
                handEvalPay.textSize = 8F
                handEval.layoutParams = ConstraintLayout.LayoutParams(
                    ConstraintLayout.LayoutParams.WRAP_CONTENT,
                    ConstraintLayout.LayoutParams.WRAP_CONTENT
                )
                handEval.layoutParams.height = densityToPx(10F)
                handEvalPay.layoutParams.height = densityToPx(10F)
                handEvalLayout.setBackgroundResource(R.color.colorBlueTransparent)
                betTextView.textSize = 9F
                betTextView.layoutParams.width = densityToPx(11F)
                betTextView.layoutParams.height = densityToPx(11F)
            }
        }
    }

    private fun resizeImage(imageViews: List<ImageView>) {
        for((i, imageView) in imageViews.withIndex()) {
            when (itemCount) {
                0, 1 -> {
                    imageView.layoutParams.height = densityToPx(100F)
                    imageView.layoutParams.width = densityToPx(70F)
                    imageView.setMarginLeft(densityToPx(70F * i))
                }
                2 -> {
                    imageView.layoutParams.height = densityToPx(75F)
                    imageView.layoutParams.width = densityToPx(50F)
                    imageView.setMarginLeft(densityToPx(50F * i))
                }
                3, 4 -> {
                    imageView.layoutParams.height = densityToPx(75F)
                    imageView.layoutParams.width = densityToPx(50F)
                    imageView.setMarginLeft(densityToPx(25F * i))
                }
                5, 6, 7, 8, 9 -> {
                    imageView.layoutParams.height = densityToPx(60F)
                    imageView.layoutParams.width = densityToPx(50F)
                    imageView.setMarginLeft(densityToPx(20F * i))
                }
                10, 11, 12, 13, 14, 15, 16 -> {
                    imageView.layoutParams.height = densityToPx(40F)
                    imageView.layoutParams.width = densityToPx(30F)
                    imageView.setMarginLeft(densityToPx(17F * i))
                } else -> {
                    imageView.layoutParams.height = densityToPx(30F)
                    imageView.layoutParams.width = densityToPx(20F)
                    imageView.setMarginLeft(densityToPx(10F * i))
                }
            }
        }
    }

    private fun View.setMarginLeft(leftMargin: Int) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(leftMargin, params.topMargin, params.rightMargin, params.bottomMargin)
        layoutParams = params
    }

    private fun densityToPx(dip: Float): Int{
        val r: Resources = PokerApplication.applicationContext().resources
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dip,
            r.displayMetrics
        ).toInt()
    }

    private fun showBigOrSmallCards(cardView: ImageView, card: Card){
        if(itemCount > LITTLE_CARD_THRESHOLD){
            CardUiUtils.showSmallCard(cardView, card)
        } else {
            CardUiUtils.showCard(cardView, card)
        }
    }

    private fun showBigOrSmallCardBack(cardViews: List<ImageView>?){
        if(itemCount > LITTLE_CARD_THRESHOLD){
            CardUiUtils.showCardBacksSmall(cardViews)
        } else {
            CardUiUtils.showCardBacks(cardViews)
        }
    }

    private fun showBigOrSmallCardBack(cardView: ImageView){
        if(itemCount > LITTLE_CARD_THRESHOLD){
            CardUiUtils.showCardBacksSmall(cardView)
        } else {
            CardUiUtils.showCardBack(cardView)
        }
    }

    private fun showEval(evalLayout: ConstraintLayout) {
        evalLayout.visibility = View.VISIBLE
    }
}