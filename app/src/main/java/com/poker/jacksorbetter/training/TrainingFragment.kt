package com.poker.jacksorbetter.training

import Card
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.cardgame.dialog.SnackBarUtils.snack
import com.poker.jacksorbetter.cardgame.ui.CardUiUtils
import com.poker.jacksorbetter.handstatui.StatDialogUtils
import com.poker.jacksorbetter.main.MainViewModel
import com.poker.jacksorbetter.stats.StatisticsManager


class TrainingFragment : Fragment() {

    companion object {
        val NAME: String = TrainingFragment::class.java.simpleName

        fun newInstance() = TrainingFragment()
    }

    private lateinit var viewModelTrain: TrainingViewModel
    private lateinit var viewModelMain: MainViewModel

    private lateinit var correctText: TextView
    private lateinit var wrongText: TextView
    private lateinit var percentText: TextView

    private lateinit var greenImage: ImageView
    private lateinit var redImage: ImageView
    private lateinit var mainLayout: LinearLayout


    private var cardViews = mutableListOf<ImageView>()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.training_fragment, container, false)
        correctText = view.findViewById(R.id.correctCountAmountText)
        wrongText = view.findViewById(R.id.wrongCountAmountText)
        percentText = view.findViewById(R.id.accuracyCountAmountText)
        greenImage = view.findViewById(R.id.green)
        redImage = view.findViewById(R.id.red)
        mainLayout = view.findViewById(R.id.trainingModeLayout)

        cardViews.clear()
        cardViews.add(view.findViewById(R.id.card1Small))
        cardViews.add(view.findViewById(R.id.card2Small))
        cardViews.add(view.findViewById(R.id.card3Small))
        cardViews.add(view.findViewById(R.id.card4Small))
        cardViews.add(view.findViewById(R.id.card5Small))

        val atom = view.findViewById<ImageView>(R.id.atom)
        atom.setOnClickListener {
            when(viewModelMain.gameState.value) {
                MainViewModel.GameState.DEAL -> {
                    StatDialogUtils.showDialog(
                        requireActivity(),
                        viewModelMain.aiDecision.value,
                        viewModelMain.aiDecision.value?.hand,
                        viewModelMain.cardsHeld.value,
                        viewModelMain.lookupExpectedValue(viewModelMain.cardsHeld.value?.toList() ?: emptyList())
                    )
                }
                MainViewModel.GameState.EVALUATE_WITH_BONUS,
                MainViewModel.GameState.EVALUATE_NO_BONUS -> {
                    StatDialogUtils.showDialog(
                        requireActivity(),
                        viewModelMain.aiDecision.value,
                        viewModelMain.aiDecision.value?.hand,
                        viewModelMain.lastKeptCards(),
                        viewModelMain.lookupExpectedValue(viewModelMain.lastKeptCards() ?: emptyList())
                    )
                }
                else -> {
                    mainLayout.snack(getString(R.string.deal_first), Snackbar.LENGTH_LONG)
                }
            }
        }
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModelMain = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        viewModelTrain = ViewModelProvider(requireActivity()).get(TrainingViewModel::class.java)

        viewModelMain.handEvals.observe(viewLifecycleOwner, { handEvals ->
            handEvals?.let {
                viewModelMain.aiDecision.value?.let {
                    viewModelMain.lastKeptCards()?.let { it1 ->
                        updateTrainingView(it.hand, it.sortedRankedHands[0].first,
                            it1
                        )
                    }
                }
            }
        })

        viewModelMain.gameState.observe(viewLifecycleOwner, { state ->
            if(state == MainViewModel.GameState.START){
                clearTrainingView()
            }
        })
    }

    private fun clearTrainingView() {
        clearTrafficLight()
        CardUiUtils.unTintCards(cardViews)
        CardUiUtils.showCardBacksSmall(cardViews)
        showStats()
    }

    private fun showStats() {
        correctText.text = "${StatisticsManager.getStatistics()?.correctCount}"
        wrongText.text = "${StatisticsManager.getStatistics()?.wrongCount}"
        percentText.text = getString(R.string.two_decimal, StatisticsManager.getAccuracy())
    }

    private fun clearTrafficLight() {
        greenImage.setImageDrawable(null)
        redImage.setImageDrawable(null)
    }

    private fun updateTrainingView(newCards: List<Card>, correctCards: List<Card>, yourCards: List<Card>) {
        clearTrainingView()
        CardUiUtils.showSmallCards(cardViews, newCards)
        CardUiUtils.tintCards(cardViews, newCards, newCards.minus(correctCards))
        if(yourCards.toSet() == correctCards.toSet()){
            showGreen()
            StatisticsManager.increaseCorrectCount()
        } else {
            showRed()
            StatisticsManager.increaseIncorrectCount()
        }
        showStats()
    }

    private fun showGreen() {
        clearTrafficLight()
        greenImage.setImageResource(R.drawable.circle_green)
    }

    private fun showRed() {
        clearTrafficLight()
        redImage.setImageResource(R.drawable.circle_red)
    }
}