package com.poker.jacksorbetter.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.PokerApplication


interface CardTapped {
    fun onCardTapped(position: Int)
}

class CardBackAdapter(private val cardTapped: CardTapped, private val isGoldenGod: Boolean): RecyclerView.Adapter<CardBackAdapter.ViewHolder>() {


    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val cardback: ImageView = itemView.findViewById(R.id.card1)
        val lock: ImageView = itemView.findViewById(R.id.lock)
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

        if(position != itemCount - 1 || SettingsUtils.isGoldenGod()){
            viewHolder.lock.visibility = View.GONE
        }

        viewHolder.cardback.setOnClickListener {
            if (position == itemCount - 1 && !isGoldenGod){
                Toast.makeText(
                    PokerApplication.applicationContext(),
                    PokerApplication.applicationContext().getString(R.string.golden_god_desc2),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                cardTapped.onCardTapped(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return SettingsUtils.CardBacks.cardbacks.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}