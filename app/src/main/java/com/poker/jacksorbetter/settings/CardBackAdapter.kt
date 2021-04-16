package com.poker.jacksorbetter.settings

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.main.PokerApplication


interface CardTapped {
    fun onCardTapped(position: Int)
}

class CardBackAdapter(private val cardTapped: CardTapped, private val isGoldenGod: Boolean): RecyclerView.Adapter<CardBackAdapter.ViewHolder>() {


    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val cardback = itemView.findViewById<ImageView>(R.id.card1)
        val lock = itemView.findViewById<ImageView>(R.id.lock)
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

        if(position != itemCount - 1 || SettingsUtils.isGoldenGod(PokerApplication.applicationContext())){
            viewHolder.lock.visibility = View.GONE
        }


        viewHolder.cardback.setOnClickListener {
            if (position == itemCount - 1 && !isGoldenGod){
                Toast.makeText(
                    PokerApplication.applicationContext(),
                    "Golden Gods only",
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