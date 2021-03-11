package com.poker.jacksorbetter.stats

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.poker.jacksorbetter.R


class HandEvalCountAdapter(
    private val values: List<HandEvalCountContent.HandEvalCount>
) : RecyclerView.Adapter<HandEvalCountAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_hand_eval_count, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = values[position]
        holder.evalView.text = item.eval?.readableName
        holder.evalCountView.text = "${item.count}"
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val evalView: TextView = view.findViewById(R.id.item_eval)
        val evalCountView: TextView = view.findViewById(R.id.item_eval_count)

        override fun toString(): String {
            return super.toString() + " '" + evalView.text + "'"
        }
    }
}