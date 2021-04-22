package com.poker.jacksorbetter

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.poker.jacksorbetter.cardgame.Evaluate
import com.poker.jacksorbetter.main.PayOutHelper
import com.poker.jacksorbetter.main.PokerApplication


class HandPayAdapter (val context: Context) : RecyclerView.Adapter<HandPayAdapter.ViewHolder>() {

    var values = populatePayTable(1)

    private var evals: MutableList<Evaluate.Hand>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.fragment_pay_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]

        holder.evalName.text = item.first
        holder.evalAmount.text = "${item.second}"

        if(evals == null) {
            holder.evalName.setBackgroundColor(PokerApplication.applicationContext().resources.getColor(R.color.colorBlue))
            holder.evalAmount.setBackgroundColor(PokerApplication.applicationContext().resources.getColor(R.color.goldenYellow))
            holder.evalContainer.setBackgroundColor(PokerApplication.applicationContext().resources.getColor(R.color.red))
        } else if(position < evals?.size ?: 0 && evals?.filter { it.littleName == item.first }?.count() ?: 0 > 0) {
            holder.evalName.setBackgroundColor(PokerApplication.applicationContext().resources.getColor(R.color.red))
            holder.evalAmount.setBackgroundColor(PokerApplication.applicationContext().resources.getColor(R.color.red))
            holder.evalContainer.setBackgroundColor(PokerApplication.applicationContext().resources.getColor(R.color.colorYellow))
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val evalName: TextView = view.findViewById(R.id.payName)
        val evalAmount: TextView = view.findViewById(R.id.payAmount)
        val evalContainer: ConstraintLayout = view.findViewById(R.id.payLayout)
    }

    private fun populatePayTable(bet: Int): MutableList<Pair<String, Int>>{
        val values = mutableListOf<Pair<String, Int>>()
        for (hand in Evaluate.Hand.values()){
            val pay = PayOutHelper.calculatePayout(context, bet, hand)
            values.add(Pair(hand.littleName, pay))
        }
        return values.dropLast(1).toMutableList()
    }

    fun setBetAmount(bet: Int) {
        evals = null
        values = populatePayTable(bet)
        notifyDataSetChanged()
    }

    fun highlightEvals(evals: MutableList<Evaluate.Hand>) {
        this.evals = evals
        notifyDataSetChanged()
    }

    fun unhighlightEvals() {
        this.evals = null
        notifyDataSetChanged()
    }
}