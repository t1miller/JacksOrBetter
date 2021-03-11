package com.poker.jacksorbetter.simulator

import android.app.ProgressDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.cardgame.Card
import com.poker.jacksorbetter.handstatui.HandStatAdapter
import com.poker.jacksorbetter.main.CardUiUtils
import com.poker.jacksorbetter.main.CommonUiUtils.toFormattedStringThreeDecimals
import com.poker.jacksorbetter.main.MainViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [SimulatorFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SimulatorFragment : Fragment() {

    private lateinit var cardsEditText: EditText

    private var cardViews = mutableListOf<ImageView>()

    private lateinit var startButton: Button

    private lateinit var seekBar: SeekBar

    private lateinit var viewModel: MainViewModel

    private lateinit var adapter: HandStatAdapter

    private lateinit var seekBarText: TextView

    private lateinit var errorInputHandText: TextView

    private lateinit var expectedValueText: TextView

    private lateinit var cardsLayout: ConstraintLayout

    private lateinit var progress: ProgressDialog

    private var cardsHoldOverlay: MutableList<TextView> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_simulator, container, false)

        cardsEditText = view.findViewById(R.id.editTextCard)
        startButton = view.findViewById(R.id.button)
        seekBar = view.findViewById(R.id.seekBar)
        seekBarText = view.findViewById(R.id.seekbarValue)
        progress = ProgressDialog(activity)
        errorInputHandText = view.findViewById(R.id.errorText)
        cardsLayout = view.findViewById(R.id.cardsLayout)
        expectedValueText = view.findViewById(R.id.expectedHandValue)

        cardViews.add(view.findViewById(R.id.card1))
        cardViews.add(view.findViewById(R.id.card2))
        cardViews.add(view.findViewById(R.id.card3))
        cardViews.add(view.findViewById(R.id.card4))
        cardViews.add(view.findViewById(R.id.card5))

        cardsHoldOverlay.add(view.findViewById(R.id.card1Hold))
        cardsHoldOverlay.add(view.findViewById(R.id.card2Hold))
        cardsHoldOverlay.add(view.findViewById(R.id.card3Hold))
        cardsHoldOverlay.add(view.findViewById(R.id.card4Hold))
        cardsHoldOverlay.add(view.findViewById(R.id.card5Hold))

        startButton.setOnClickListener {
            clearHoldUi()
            showSimulationLoadingDialog(seekBar.progress)

            viewModel.getBestHand(seekBar.progress)
        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar, progress: Int, fromUser: Boolean) {
                seekBarText.text = "$progress"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}

            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        cardsEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                parseHandAndShowOrError()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })

        val recyclerView = view.findViewById(R.id.handList) as RecyclerView
        adapter = HandStatAdapter(requireContext(), mutableListOf())
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.setHasFixedSize(true)
        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        viewModel.aiDecision.observe(viewLifecycleOwner, Observer { aiDecision ->
            adapter.updateData(aiDecision.sortedRankedHands, viewModel.hand.value)
            dismissSimulationLoadingDialog()
            showAiCardsToHold(aiDecision.sortedRankedHands.first().first)
            updateYourExpectedValue(aiDecision.sortedRankedHands.first().second)
        })

        parseHandAndShowOrError()
    }

    fun parseHandAndShowOrError() {
        val handTxt = cardsEditText.text.toString()
        val (hand, error) = Card.parseHand(handTxt)

        if(error != Card.ParseError.NONE) {
            //make children invisible
            for(child in cardsLayout.children) {
                child.visibility = View.INVISIBLE
            }

            errorInputHandText.visibility = View.VISIBLE
            errorInputHandText.text = "Error: ${error.humanReadableError}"
        } else {
            //make children visible
            for(child in cardsLayout.children) {
                child.visibility = View.VISIBLE
            }
            clearHoldUi()
            errorInputHandText.visibility = View.GONE
            viewModel.hand.value = hand.toMutableList()
            for ((i,c) in hand.withIndex()) {
                cardViews[i].setImageResource(CardUiUtils.cardToImage(c))
            }
        }

    }

    private fun showSimulationLoadingDialog(numTrials: Int) {
        progress.setTitle("Monte Carlo Simulation")

        var trialText = "Possible Decisions: 32\n"
        trialText += String.format("Trials Per Decision: %,d\n",numTrials)
        trialText += String.format("Total Games: %,d\n\n",32*numTrials)

        if(numTrials > TRIAL_THRESHOLD) {
            trialText += randomSuprisedExpression()
            trialText += ", that's a lot of trials. Let's Go!"
        }
        progress.setMessage(trialText)
        progress.setCancelable(false) // disable dismiss by tapping outside of the dialog
        progress.show()
    }

    private fun dismissSimulationLoadingDialog() {
        progress.dismiss()
    }

    private fun showAiCardsToHold(bestHand: List<Card>) {
        viewModel.hand.value?.let {
            for ((idx, card) in it.withIndex()) {
                if (bestHand.contains(card)) {
                    cardsHoldOverlay[idx].visibility = View.VISIBLE
                }
            }
        }
    }

    private fun updateYourExpectedValue(expectedValue: Double?) {
        expectedValueText.text = "$${expectedValue?.toFormattedStringThreeDecimals()}"
    }

    private fun clearHoldUi() {
        for (card in cardsHoldOverlay) {
            card.visibility = View.GONE
        }
    }

    companion object {

//        val NAME = SimulatorFragment.javaClass.name
        val NAME = SimulatorFragment::class.java.simpleName

        const val TRIAL_THRESHOLD = 10000

        @JvmStatic
        fun newInstance() = SimulatorFragment()

        fun randomSuprisedExpression() : String {
            val expressions = mutableListOf<String>()
            expressions.add("Holly cannoli")
            expressions.add("Gee willikers")
            expressions.add("Gee whiz")
            expressions.add("Holy toledo")
            expressions.add("Gosh almighty")
            expressions.add("Holy pretzel")
            expressions.add("I'll be jitterBugged")
            expressions.add("Zookers")
            expressions.add("Holy pretzel")
            expressions.add("Hot diggity")
            expressions.add("Jeepers creepers")
            expressions.add("Well call me a biscuit")
            return expressions.random()
        }
    }
}