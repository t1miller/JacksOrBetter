package com.poker.jacksorbetter.stats

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.cardgame.Evaluate
import com.poker.jacksorbetter.cardgame.ui.CardUiUtils

/**
 * A fragment representing a list of Items.
 */
class StatsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_stats, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.list)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = HandEvalCountAdapter(
                StatisticsManager.getStatistics()?.evalCounts?.map {
                    HandEvalCountContent.HandEvalCount(Evaluate.Hand.handFromReadableName(it.evalType),it.count)
                } ?: HandEvalCountContent.ITEMS
            )
        }

        val wonText = view.findViewById<TextView>(R.id.won)
        val lossText = view.findViewById<TextView>(R.id.loss)
        val totalText = view.findViewById<TextView>(R.id.total)

        val lastHandBet = view.findViewById<TextView>(R.id.bet)
        val lastHandWon = view.findViewById<TextView>(R.id.lastHandwon)
        val lastHandEval = view.findViewById<TextView>(R.id.lastHandEval)

        val accuracyText = view.findViewById<TextView>(R.id.strategyYourAccuracy)
        val correctText = view.findViewById<TextView>(R.id.strategyCorrectCount)
        val wrongText = view.findViewById<TextView>(R.id.strategyWrongCount)

        accuracyText.text = getString(R.string.your_accuracy, StatisticsManager.getAccuracy())
        correctText.text = getString(R.string.correct_count, StatisticsManager.getStatistics()?.correctCount)
        wrongText.text = getString(R.string.wrong_count, StatisticsManager.getStatistics()?.wrongCount)

        wonText.text = getString(R.string.won, StatisticsManager.getStatistics()?.totalWon)
        lossText.text = getString(R.string.loss, StatisticsManager.getStatistics()?.totalLost)
        totalText.text = getString(R.string.total, (StatisticsManager.getStatistics()?.totalWon ?: 0) + (StatisticsManager.getStatistics()?.totalLost ?: 0))


        val cardViewsOriginal = mutableListOf<ImageView>()
        cardViewsOriginal.add(view.findViewById(R.id.card1))
        cardViewsOriginal.add(view.findViewById(R.id.card2))
        cardViewsOriginal.add(view.findViewById(R.id.card3))
        cardViewsOriginal.add(view.findViewById(R.id.card4))
        cardViewsOriginal.add(view.findViewById(R.id.card5))

        val cardViewsAfterDiscard = mutableListOf<ImageView>()
        cardViewsAfterDiscard.add(view.findViewById(R.id.card12))
        cardViewsAfterDiscard.add(view.findViewById(R.id.card22))
        cardViewsAfterDiscard.add(view.findViewById(R.id.card32))
        cardViewsAfterDiscard.add(view.findViewById(R.id.card42))
        cardViewsAfterDiscard.add(view.findViewById(R.id.card52))

        val cardHeldViews = mutableListOf<TextView>()
        cardHeldViews.add(view.findViewById(R.id.card1Hold))
        cardHeldViews.add(view.findViewById(R.id.card2Hold))
        cardHeldViews.add(view.findViewById(R.id.card3Hold))
        cardHeldViews.add(view.findViewById(R.id.card4Hold))
        cardHeldViews.add(view.findViewById(R.id.card5Hold))

        // set last game stats
        CardUiUtils.showCards(cardViewsOriginal, StatisticsManager.getStatistics()?.lastGame?.handOriginal)
        CardUiUtils.highlightHeldCards(cardHeldViews, StatisticsManager.getStatistics()?.lastGame?.handOriginal, StatisticsManager.getStatistics()?.lastGame?.heldCards)
        CardUiUtils.showCards(cardViewsAfterDiscard, StatisticsManager.getStatistics()?.lastGame?.handFinal)
        lastHandBet.text = getString(R.string.bet,StatisticsManager.getStatistics()?.lastGame?.bet ?: 0)
        lastHandWon.text = getString(R.string.won,StatisticsManager.getStatistics()?.lastGame?.won ?: 0)
        lastHandEval.text = getString(R.string.eval,StatisticsManager.getStatistics()?.lastGame?.eval ?: "")

        return view
    }

    companion object {

        val NAME = StatsFragment::class.java.simpleName

        @JvmStatic
        fun newInstance() = StatsFragment()
    }
}
