package com.poker.jacksorbetter.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.poker.jacksorbetter.R

interface CardTapped {
    fun onCardTapped(position: Int)
}

class CardBackAdapter(private val cardTapped: CardTapped): RecyclerView.Adapter<CardBackAdapter.ViewHolder>() {


    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val cardback = itemView.findViewById<ImageView>(R.id.card1)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val handStatView = inflater.inflate(R.layout.item_cardback, parent, false)
        return ViewHolder(handStatView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val image = SettingsUtils.CardBacks.cardbacks[position]
        viewHolder.cardback.setImageResource(image)
        viewHolder.cardback.setOnTouchListener { _, event ->
            cardTapped.onCardTapped(position)
            true
        }
    }

    override fun getItemCount(): Int {
        return SettingsUtils.CardBacks.cardbacks.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

//    fun dimImageView(imageView: ImageView) {
//        imageView.setColorFilter(
//            ContextCompat.getColor(
//                context,
//                R.color.colorGrey
//            ), android.graphics.PorterDuff.Mode.MULTIPLY
//        );
//    }
//
//    fun unDimImageView(imageView: ImageView) {
//        imageView.setColorFilter(null)
//    }

//    fun updateData(sortedHands: List<Pair<List<Card>, Double>>?, fullHand: List<Card>?) {
//        if(fullHand == null || sortedHands == null){
//            return
//        }
//        val handStat = sortedHands.map { HandStat(it.first, fullHand, it.second) }
//        mStats.clear()
//        mStats.addAll(handStat)
//        notifyDataSetChanged()
//    }
}