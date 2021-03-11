package com.poker.jacksorbetter.handstatui

import android.app.Dialog
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.cardgame.Card
import com.poker.jacksorbetter.main.AIDecision
import com.poker.jacksorbetter.main.CardUiUtils
import com.poker.jacksorbetter.main.CommonUiUtils.toFormattedStringThreeDecimals
import timber.log.Timber

object StatDialogUtils {

    fun showDialog(activity: FragmentActivity, aiDecision: AIDecision?, fullHand: List<Card>?, selectedHand: List<Card>?, expectedValue: Double) {
        if(fullHand == null || aiDecision == null) return

        val dialog = Dialog(activity)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.stat_dialog_layout)

        val yesBtn = dialog.findViewById(R.id.btn_yes) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }

        val cardViews = mutableListOf<ImageView>()
        cardViews.add(dialog.findViewById(R.id.card1))
        cardViews.add(dialog.findViewById(R.id.card2))
        cardViews.add(dialog.findViewById(R.id.card3))
        cardViews.add(dialog.findViewById(R.id.card4))
        cardViews.add(dialog.findViewById(R.id.card5))

        fullHand.forEachIndexed { index, card ->
            cardViews[index].setImageResource(CardUiUtils.cardToImage(card))
            if(selectedHand != null && !selectedHand.contains(card)) {
                cardViews[index].setColorFilter(
                    ContextCompat.getColor(activity,
                        R.color.colorGrey), android.graphics.PorterDuff.Mode.MULTIPLY)
            }
        }
        val yourExpectedValueText = dialog.findViewById(R.id.expectedHandValue) as TextView
        yourExpectedValueText.text = "$${expectedValue.toFormattedStringThreeDecimals()}"

        val recyclerView = dialog.findViewById(R.id.handList) as RecyclerView

        val handStats = aiDecision.sortedRankedHands.map { HandStat(it.first, fullHand, it.second) }.toMutableList()
        val adapter = HandStatAdapter(activity, handStats)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)

        Timber.d("showing stat dialog")
//        Timber.d(handStats.joinToString { "hand: ${it.recommendedHand.joinToString { its->its.toString() }}\n" })
        dialog.show()
    }
}

