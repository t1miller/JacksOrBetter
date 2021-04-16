package com.poker.jacksorbetter.leaderboard

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.poker.jacksorbetter.R

interface HighScoreTapped {
    fun onTapped()
}

class HighScoreAdapter(
    private val values: List<Pair<String, Long>>,
    private val callback: HighScoreTapped
) : RecyclerView.Adapter<HighScoreAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_highscore, parent, false)

        view.setOnClickListener {
            callback.onTapped()
        }

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
//        holder.rank.text = "$position)"
        holder.player.text = item.first
        holder.money.text = java.text.NumberFormat.getCurrencyInstance().format(item.second)
//        Timber.d("Adapter $item")
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val rank: TextView = view.findViewById(R.id.rank)
        val player: TextView = view.findViewById(R.id.name)
        val money: TextView = view.findViewById(R.id.score)
    }
}