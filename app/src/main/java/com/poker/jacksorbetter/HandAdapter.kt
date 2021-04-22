package com.poker.jacksorbetter

import android.content.res.Resources
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.poker.jacksorbetter.cardgame.Card
import com.poker.jacksorbetter.cardgame.Evaluate
import com.poker.jacksorbetter.cardgame.ui.CardUiUtils
import com.poker.jacksorbetter.main.PayOutHelper
import com.poker.jacksorbetter.main.PokerApplication
import timber.log.Timber
import java.util.*
import kotlin.concurrent.schedule


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
    private var state: State,
    private var callback: Callback
) : RecyclerView.Adapter<HandAdapter.ViewHolder>() {

    private var itemCount = 0


    public interface Callback {
        fun onComplete()
    }

    fun delayShowCard(cardView: ImageView, card: Card, delay: Long) {
        val handler = Handler(Looper.getMainLooper())
        Timer().schedule(delay){
            handler.post(Runnable {
                CardUiUtils.showCard(cardView, card)
            })
        }
    }

    fun delayShowEval(evalLayout: ConstraintLayout, delay: Long) {
        val handler = Handler(Looper.getMainLooper())
        Timer().schedule(delay){
            handler.post(Runnable {
                evalLayout.visibility = View.VISIBLE
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_hand_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        val cardFronts = listOf(
            holder.card1,
            holder.card2,
            holder.card3,
            holder.card4,
            holder.card5
        )

        resizeImage(cardFronts)
        resizeEvalText(holder.handEval, holder.handEvalPay, holder.handEvalLayout)

        when(state){
            State.CARD_BACK -> {
                CardUiUtils.showCardBacks(cardFronts)
                holder.handEvalLayout.visibility = View.INVISIBLE
                itemCount = 0
            }
            State.HOLD -> {
                CardUiUtils.showCardBacks(cardFronts)
                item.forEachIndexed { index, it ->
                    if (it in hold) {
                        CardUiUtils.showCard(cardFronts[index], it)
                    }
                }
            }
            State.FLIP -> {
                val delayPerHand = 1000 / ( itemCount + 1).toLong()
                val delayPerCard = delayPerHand/5
                CardUiUtils.showCardBacks(cardFronts)
                item.forEachIndexed { index, it ->
                    if (it in hold) {
                        CardUiUtils.showCard(cardFronts[index], it)
                    } else {
                        delayShowCard(cardFronts[index], it, delayPerHand * position.toLong() + delayPerCard * index)
                    }
                }

                itemCount += 1
                if (position < evals.size && evals[position] != Evaluate.Hand.NOTHING) {
                    holder.handEvalPay.text = "${
                        PayOutHelper.calculatePayout(
                            PokerApplication.applicationContext(), bet, evals.get(
                                position
                            )
                        )
                    }"
                    holder.handEval.text = evals[position].readableName
                    delayShowEval(holder.handEvalLayout, delayPerHand * position.toLong() + delayPerCard * 5)
                }
                if(itemCount == getItemCount()){
                    callback.onComplete()
                }
            }
        }
        holder.betText.text = "$bet"
    }

    override fun getItemCount() : Int{
        return values.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val card1: ImageView = view.findViewById(R.id.cardfront1)
        val card2: ImageView = view.findViewById(R.id.cardfront2)
        val card3: ImageView = view.findViewById(R.id.cardfront3)
        val card4: ImageView = view.findViewById(R.id.cardfront4)
        val card5: ImageView = view.findViewById(R.id.cardfront5)

        val betText: TextView = view.findViewById(R.id.betCircleText)
        val handEval: TextView = view.findViewById(R.id.handEval)
        val handEvalPay: TextView = view.findViewById(R.id.handEvalPay)
        val handEvalLayout: ConstraintLayout = view.findViewById(R.id.winningLayout)
    }

    private fun resizeEvalText(
        handEval: TextView,
        handEvalPay: TextView,
        handEvalLayout: ConstraintLayout
    ) {
        Timber.d("resize text, list size $itemCount")
        when(getItemCount()){
            0,1 -> {
                handEval.textSize = 16F
                handEvalPay.textSize = 20F
                handEval.layoutParams.width = densityToPx(150F)
                handEval.layoutParams.height = densityToPx(28F)
                handEvalPay.layoutParams.height = densityToPx(28F)
                handEvalLayout.setMarginBottom(20)
            }
            2 -> {
                handEval.textSize = 14F
                handEvalPay.textSize = 18F
                handEval.layoutParams.width = densityToPx(100F)
                handEval.layoutParams.height = densityToPx(25F)
                handEvalPay.layoutParams.height = densityToPx(25F)
                handEvalLayout.setMarginBottom(20)
            }
            3, 4 -> {
                handEval.textSize = 12F
                handEvalPay.textSize = 14F
                handEval.layoutParams.width = densityToPx(90F)
                handEval.layoutParams.height = densityToPx(21F)
                handEvalPay.layoutParams.height = densityToPx(21F)
                handEvalLayout.setMarginBottom(20)
            } else -> {
                handEval.textSize = 10F
                handEvalPay.textSize = 12F
                handEval.layoutParams.width = densityToPx(70F)
                handEval.layoutParams.height = densityToPx(20F)
                handEvalPay.layoutParams.height = densityToPx(20F)
                handEvalLayout.setMarginBottom(15)
            }
        }
    }

    private fun resizeImage(imageViews: List<ImageView>) {
        Timber.d("resize image, list size ${itemCount}")
        for((i, imageView) in imageViews.withIndex()) {
            when (getItemCount()) {
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
                }
                5, 6 -> {
                    imageView.layoutParams.height = densityToPx(60F)
                    imageView.layoutParams.width = densityToPx(50F)
                    imageView.setMarginLeft(densityToPx(25F * i))
                }
                else -> {
                    imageView.layoutParams.height = densityToPx(60F)
                    imageView.layoutParams.width = densityToPx(50F)
                    imageView.setMarginLeft(densityToPx(16F * i))
                }
            }
        }
    }

    fun hold(holdCards: List<Card>) {
        hold = holdCards.toMutableList()
        state = State.HOLD
        notifyDataSetChanged()
    }

    fun unhold() {
        state = State.FLIP
//        hold = mutableListOf()
        notifyDataSetChanged()
    }

    fun setState(s: State) {
        state = s
        notifyDataSetChanged()
    }

//    fun getState() : State {
//        return state
//    }

    fun setBetAmount(b: Int) {
        this.bet = b
        state = State.CARD_BACK
        notifyDataSetChanged()
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

    private fun View.setMarginBottom(bottomMargin: Int) {
        val params = layoutParams as ViewGroup.MarginLayoutParams
        params.setMargins(params.leftMargin, params.topMargin, params.rightMargin, bottomMargin)
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
}