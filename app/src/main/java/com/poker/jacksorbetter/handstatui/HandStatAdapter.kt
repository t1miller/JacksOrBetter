package com.poker.jacksorbetter.handstatui

import Card
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.cardgame.ui.CardUiUtils


class HandStatAdapter(private val context: Context, private val mStats: MutableList<HandStat>): RecyclerView.Adapter<HandStatAdapter.ViewHolder>() {


    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {

        val expectedValueText = itemView.findViewById<TextView>(R.id.expectedHandValue)

        // todo this should be some sort of array
        val card1 = itemView.findViewById<ImageView>(R.id.card1)
        val card2 = itemView.findViewById<ImageView>(R.id.card2)
        val card3 = itemView.findViewById<ImageView>(R.id.card3)
        val card4 = itemView.findViewById<ImageView>(R.id.card4)
        val card5 = itemView.findViewById<ImageView>(R.id.card5)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HandStatAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val handStatView = inflater.inflate(R.layout.item_hand_stat, parent, false)
        return ViewHolder(handStatView)
    }

    override fun onBindViewHolder(viewHolder: HandStatAdapter.ViewHolder, position: Int) {
        val stat: HandStat = mStats[position]

        val evFormated = String.format("%.3f", stat.expectedValue).toDouble()
        viewHolder.expectedValueText.text = "$${evFormated}"
        viewHolder.card1.setImageResource(CardUiUtils.cardToImage(stat.fullHand[0]))
        viewHolder.card2.setImageResource(CardUiUtils.cardToImage(stat.fullHand[1]))
        viewHolder.card3.setImageResource(CardUiUtils.cardToImage(stat.fullHand[2]))
        viewHolder.card4.setImageResource(CardUiUtils.cardToImage(stat.fullHand[3]))
        viewHolder.card5.setImageResource(CardUiUtils.cardToImage(stat.fullHand[4]))

        unDimImageView(viewHolder.card1)
        unDimImageView(viewHolder.card2)
        unDimImageView(viewHolder.card3)
        unDimImageView(viewHolder.card4)
        unDimImageView(viewHolder.card5)

        // tint cards NOT in our hand
        if(!stat.recommendedHand.contains(stat.fullHand[0])){
            dimImageView(viewHolder.card1)
        }
        if(!stat.recommendedHand.contains(stat.fullHand[1])){
            dimImageView(viewHolder.card2)
        }
        if(!stat.recommendedHand.contains(stat.fullHand[2])){
            dimImageView(viewHolder.card3)
        }
        if(!stat.recommendedHand.contains(stat.fullHand[3])){
            dimImageView(viewHolder.card4)
        }
        if(!stat.recommendedHand.contains(stat.fullHand[4])){
            dimImageView(viewHolder.card5)
        }
    }

    override fun getItemCount(): Int {
        return mStats.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun dimImageView(imageView: ImageView) {
        imageView.setColorFilter(
            ContextCompat.getColor(context,
            R.color.colorGrey), android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    fun unDimImageView(imageView: ImageView) {
        imageView.setColorFilter(null)
    }

    fun updateData(sortedHands: List<Pair<List<Card>, Double>>?, fullHand: List<Card>?) {
        if(fullHand == null || sortedHands == null){
            return
        }


        val handStat = sortedHands.map { HandStat(it.first, fullHand, it.second) }
        mStats.clear()
        mStats.addAll(handStat)
        notifyDataSetChanged()
    }
}