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
        val expectedValueText: TextView = itemView.findViewById(R.id.expectedHandValue)
        val cards = mutableListOf<ImageView>(
            itemView.findViewById(R.id.card1),
            itemView.findViewById(R.id.card2),
            itemView.findViewById(R.id.card3),
            itemView.findViewById(R.id.card4),
            itemView.findViewById(R.id.card5)
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HandStatAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val handStatView = inflater.inflate(R.layout.item_hand_stat, parent, false)
        return ViewHolder(handStatView)
    }

    override fun onBindViewHolder(viewHolder: HandStatAdapter.ViewHolder, position: Int) {
        val stat: HandStat = mStats[position]
        val evFormatted = String.format("%.3f", stat.expectedValue).toDouble()

        viewHolder.apply {
            expectedValueText.text = "$${evFormatted}"
            cards.forEachIndexed { index, cardImageView ->
                cardImageView.setImageResource(CardUiUtils.cardToImage(stat.fullHand[index]))
                unDimImageView(cardImageView)

                // tint cards NOT in our hand
                if(stat.recommendedHand.contains(stat.fullHand[index])){
                    dimImageView(cardImageView)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return mStats.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    private fun dimImageView(imageView: ImageView) {
        imageView.setColorFilter(
            ContextCompat.getColor(context,
            R.color.colorGrey), android.graphics.PorterDuff.Mode.MULTIPLY)
    }

    private fun unDimImageView(imageView: ImageView) {
        imageView.colorFilter = null
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