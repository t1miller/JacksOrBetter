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


class HandAdapter(
    private var values: MutableList<MutableList<Card>>,
    private var hold: MutableList<Card>,
    private var evals: MutableList<Evaluate.Hand>,
    private var bet: Int,
    private var state: State
) : RecyclerView.Adapter<HandAdapter.ViewHolder>() {

    companion object {
        private const val LITTLE_CARD_THRESHOLD = 9
    }


    interface Callback {
        fun onComplete()
    }

    private fun showEval(evalLayout: ConstraintLayout) {
        evalLayout.visibility = View.VISIBLE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_hand_item, parent, false)
        return ViewHolder(view)
    }

//    todo go through and remove as much setimage calls as possible
//    todo reduce calls to notify data set changed
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val timeInMillis = measureTimeMillis {
            val item = values[position]

            resizeImage(holder.cardFronts)
            resizeEvalText(holder.handEval, holder.handEvalPay, holder.handEvalLayout, holder.betText)

            when(state){

                State.CARD_BACK -> {
                    showBigOrSmallCardBack(holder.cardFronts)
                    holder.handEvalLayout.visibility = View.INVISIBLE
                    hold = mutableListOf()
                }

                State.HOLD -> {
                    item.forEachIndexed { index, it ->
                        if (it in hold) {
                            showBigOrSmallCards(holder.cardFronts[index], it)
                        } else {
                            showBigOrSmallCardBack(holder.cardFronts[index])
                        }
                    }
                }

                State.FLIP -> {
                    showBigOrSmallCardBack(holder.cardFronts)
                    item.forEachIndexed { index, it ->
                        showBigOrSmallCards(holder.cardFronts[index], it)
                    }

                    if (position < evals.size && evals[position] != Evaluate.Hand.NOTHING) {
                        holder.handEvalPay.text = "${PayOutHelper.calculatePayout(bet, evals[position])}"

                        if (itemCount > LITTLE_CARD_THRESHOLD) {
                            holder.handEval.text = evals[position].littleName
                        } else {
                            holder.handEval.text = evals[position].readableName
                        }

                        showEval(holder.handEvalLayout)
                    } else {
                        holder.handEvalLayout.visibility = View.INVISIBLE
                    }
                }
            }
            holder.betText.text = "$bet"
        }
        Timber.d("onBindViewHolder() took $timeInMillis ms")
    }

    override fun getItemCount() : Int{
        return values.size
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

    private fun resizeEvalText(
        handEval: TextView,
        handEvalPay: TextView,
        handEvalLayout: ConstraintLayout,
        betTextView: TextView
    ) {
        when(itemCount){
            0,1 -> {
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
            } 5,6,7,8,9 -> {
                handEval.textSize = 10F
                handEvalPay.textSize = 12F
                handEval.layoutParams.width = densityToPx(70F)
                handEval.layoutParams.height = densityToPx(20F)
                handEvalPay.layoutParams.height = densityToPx(20F)
                handEvalLayout.setBackgroundResource(R.color.colorBlue)
                betTextView.textSize = 12F
                betTextView.layoutParams.width = densityToPx(15F)
                betTextView.layoutParams.height = densityToPx(15F)
            } 10, 11, 12, 13, 14, 15, 16 -> {
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
                handEval.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
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
                0,1 -> {
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
                } 5,6,7,8,9 -> {
                    imageView.layoutParams.height = densityToPx(60F)
                    imageView.layoutParams.width = densityToPx(50F)
                    imageView.setMarginLeft(densityToPx(20F * i))
                } 10, 11, 12, 13, 14, 15, 16 -> {
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

    fun hold(holdCards: List<Card>) {
        hold = holdCards.toMutableList()
        setState(State.HOLD)
    }

    fun setState(s: State) {
        state = s
        notifyDataSetChanged()
    }

    fun setBetAmount(b: Int) {
        this.bet = b
        setState(State.CARD_BACK)
    }

    fun setEvals(evals: MutableList<Evaluate.Hand>) {
        this.evals = evals.drop(1).toMutableList()
        notifyDataSetChanged()
    }

    fun setHands(hand: MutableList<MutableList<Card>>) {
        values = hand
        notifyDataSetChanged()
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
}