package com.example.jacksorbetter.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.jacksorbetter.R
import com.example.jacksorbetter.cardgame.Card
import com.example.jacksorbetter.cardgame.Evaluate
import com.wajahatkarim3.easyflipview.EasyFlipView
import com.example.jacksorbetter.handstatui.StatDialogUtils
import com.example.jacksorbetter.settings.SettingsUtils
import kotlinx.android.synthetic.main.main_fragment.view.*
import timber.log.Timber
import java.util.*


class MainFragment : Fragment() {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel

    private lateinit var tableLayout: TableLayout
    private var highLightedRow = -1
    private var highLightedColumn = 1

    private lateinit var handEvalText: TextView

    private lateinit var totalMoneyText: TextView

    private lateinit var wonLostText: TextView

    private lateinit var helpText: TextView

    private lateinit var statsLayout: ConstraintLayout

    private lateinit var optimalReturnText: TextView

    private lateinit var yourReturnText: TextView

    private lateinit var doubleDownButtons: View

    private lateinit var normalButtons: View

    private lateinit var betMaxButton: Button

    private lateinit var betOneButton: Button

    private lateinit var dealButton: Button

    private lateinit var doubleButton: Button

    private lateinit var doubleRedButton: Button

    private lateinit var doubleBlackButton: Button

    private lateinit var explainDecisionButton: Button

    private lateinit var autoHold: CheckBox

    private lateinit var showStats: CheckBox

    private lateinit var training: CheckBox

    private var cardLayouts: MutableList<EasyFlipView> = mutableListOf()

    private var cardViews: MutableList<ImageView> = mutableListOf()

    private var cardsHoldOverlay: MutableList<TextView> = mutableListOf()

//    private var flipStatus = CardFlipStatus.NOT_FLIPPING

//    enum class CardFlipStatus {
//        NOT_FLIPPING,
//        FLIPPING
//    }

    private var isMidFlip = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.main_fragment, container, false)

        tableLayout = view.findViewById(R.id.payoutTable)
        handEvalText = view.findViewById(R.id.handEval)
        totalMoneyText = view.findViewById(R.id.totalMoney)
        wonLostText = view.findViewById(R.id.winlose)
        helpText = view.findViewById(R.id.doubleDownInstructions)
        betMaxButton = view.findViewById(R.id.betmax)
        betOneButton = view.findViewById(R.id.betone)
        dealButton = view.findViewById(R.id.Deal)
        doubleButton = view.findViewById(R.id.doubleDown)
        doubleRedButton = view.findViewById(R.id.doubleDownRed)
        doubleBlackButton = view.findViewById(R.id.doubleDownBlack)
        normalButtons = view.findViewById(R.id.buttons)
        doubleDownButtons = view.findViewById(R.id.bonusButtons)
        autoHold = view.findViewById(R.id.autoHold)
        showStats = view.findViewById(R.id.showStats)
        training = view.findViewById(R.id.trainingMode)
        optimalReturnText = view.findViewById(R.id.optimalExpectedValue)
        yourReturnText = view.findViewById(R.id.yourExpectedValue)
        statsLayout = view.findViewById(R.id.statsUi)
        explainDecisionButton = view.findViewById(R.id.explainDecision)

        cardLayouts.add(view.findViewById(R.id.card1layout))
        cardLayouts.add(view.findViewById(R.id.card2layout))
        cardLayouts.add(view.findViewById(R.id.card3layout))
        cardLayouts.add(view.findViewById(R.id.card4layout))
        cardLayouts.add(view.findViewById(R.id.card5layout))
        cardLayouts.add(view.findViewById(R.id.card6layout))


        cardViews.add(view.findViewById(R.id.cardfront1))
        cardViews.add(view.findViewById(R.id.cardfront2))
        cardViews.add(view.findViewById(R.id.cardfront3))
        cardViews.add(view.findViewById(R.id.cardfront4))
        cardViews.add(view.findViewById(R.id.cardfront5))

        cardsHoldOverlay.add(view.findViewById(R.id.card1Hold))
        cardsHoldOverlay.add(view.findViewById(R.id.card2Hold))
        cardsHoldOverlay.add(view.findViewById(R.id.card3Hold))
        cardsHoldOverlay.add(view.findViewById(R.id.card4Hold))
        cardsHoldOverlay.add(view.findViewById(R.id.card5Hold))

        for (i in 0..4) {
            cardViews[i].setOnClickListener {
                when(viewModel.gameState.value){
                    MainViewModel.GameState.DEAL -> {
                        toggleHoldUi(i)
                        updateYourExpectedValue(viewModel.lookupExpectedValue(getCardsToKeep()))
                    }
                    else -> {}
                }
            }
        }

        betMaxButton.setOnClickListener {
            SoundManager.playSound(requireActivity(), SoundManager.SoundType.INSERT_COIN)
            viewModel.maxBet()
        }

        betOneButton.betone.setOnClickListener {
            SoundManager.playSound(requireActivity(), SoundManager.SoundType.INSERT_COIN)
            viewModel.incrementBet()
        }

        dealButton.setOnClickListener {
            when (viewModel.gameState.value) {
                MainViewModel.GameState.START -> {
                    viewModel.newGame()
                    viewModel.cardFLipState.value = MainViewModel.CardFlipState.FACE_UP
                }
                MainViewModel.GameState.DEAL -> {
                    viewModel.evaluateHand(getCardsToKeepBooleanArray(), getCardsToKeep())
                }
                MainViewModel.GameState.EVALUATE_NO_BONUS -> {
                    viewModel.gameState.value = MainViewModel.GameState.START
                    viewModel.newGame()
                    viewModel.cardFLipState.value = MainViewModel.CardFlipState.FULL_FLIP
                }
                MainViewModel.GameState.EVALUATE_WITH_BONUS -> {
                    // user is opting out of double down
                    viewModel.collect()
                    viewModel.gameState.value = MainViewModel.GameState.START
                    viewModel.cardFLipState.value = MainViewModel.CardFlipState.FACE_DOWN
                }
                MainViewModel.GameState.BONUS -> {
                    viewModel.gameState.value = MainViewModel.GameState.START
                    viewModel.newGame()
                    viewModel.cardFLipState.value = MainViewModel.CardFlipState.FULL_FLIP
                }
                else -> {}
            }
        }

        doubleButton.setOnClickListener {
            viewModel.gameState.value = MainViewModel.GameState.BONUS
        }

        doubleRedButton.setOnClickListener {
            viewModel.collectBonus(true)
            showBonusDoneUi()
        }

        doubleBlackButton.setOnClickListener {
            viewModel.collectBonus(false)
            showBonusDoneUi()
        }

        autoHold.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked && viewModel.gameState.value == MainViewModel.GameState.DEAL) {
                clearHoldUi()
                helpText.visibility = View.VISIBLE
                viewModel.getBestHand(SettingsUtils.getNumTrials(activity))
            }
        }

        showStats.setOnCheckedChangeListener { _, isChecked ->
            if(isChecked) {
                statsLayout.visibility = View.VISIBLE
            } else {
                statsLayout.visibility = View.GONE
            }
        }

        explainDecisionButton.setOnClickListener {

            when(viewModel.gameState.value) {
                MainViewModel.GameState.DEAL -> {
                    StatDialogUtils.showDialog(
                        requireActivity(),
                        viewModel.aiDecision.value,
                        viewModel.aiDecision.value?.hand,
                        getCardsToKeep(),
                        viewModel.lookupExpectedValue(getCardsToKeep())
                    )
                }
                MainViewModel.GameState.EVALUATE_WITH_BONUS,
                MainViewModel.GameState.EVALUATE_NO_BONUS -> {
                    StatDialogUtils.showDialog(
                        requireActivity(),
                        viewModel.aiDecision.value,
                        viewModel.aiDecision.value?.hand,
                        viewModel.lastKeptCards(),
                        viewModel.lookupExpectedValue(viewModel.lastKeptCards() ?: emptyList())
                    )
                }
                else -> {
                    Toast.makeText(activity, "Deal First", Toast.LENGTH_LONG).show()
                }
            }
        }

        setupFlipListener()
        populatePayoutTable()

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        SoundManager.load(requireActivity())

        viewModel.bet.observe(viewLifecycleOwner, Observer { currentBet ->
            currentBet?.let {
                updateHighlightedColumnUi()
            }
        })

        viewModel.hand.observe(viewLifecycleOwner, Observer { hand ->
            hand?.let {
                updateYourExpectedValue(viewModel.lookupExpectedValue(getCardsToKeep()))
            }
        })


        viewModel.lastEvaluatedHand.observe(viewLifecycleOwner, Observer { evalHand ->
            evalHand?.let {
                updateHandEvalUi(evalHand)
                updateHighlightedRowUi(evalHand)

                // todo play sound
                SoundManager.playSound(requireContext(), evalHand)
            }
        })

        viewModel.totalMoney.observe(viewLifecycleOwner, Observer { totalMoney ->
            updateTotalMoneyUi(totalMoney)
        })

        viewModel.wonLostMoney.observe(viewLifecycleOwner, Observer { wonLostMoney ->
            updateWonLostMoneyUi(wonLostMoney)
        })

        viewModel.gameState.observe(viewLifecycleOwner, Observer { state ->
            updateUi(state)
        })

        viewModel.cardFLipState.observe(viewLifecycleOwner, Observer { state ->
            flip(state, viewModel.hand.value?.toList() ?: emptyList())
        })

        viewModel.aiDecision.observe(viewLifecycleOwner, Observer { decision ->
            val sortedHands = decision.sortedRankedHands
            if(autoHold.isChecked) {
                CardUiUtils.highlightHeldCards(cardsHoldOverlay, viewModel.hand.value ,sortedHands[0].first)
                SoundManager.playSound(requireActivity(), SoundManager.SoundType.CHIME)
            }
            updateOptimalExpectedValue(sortedHands[0].second)
            updateYourExpectedValue(viewModel.lookupExpectedValue(getCardsToKeep()))
        })
    }

    override fun onDestroy() {
        super.onDestroy()

        //todo is this correct?
        SoundManager.release()
    }

    private fun disableBetChangingUi() {
        betMaxButton.isEnabled = false
        betOneButton.isEnabled = false
    }

    private fun enableBetChangingUi() {
        betMaxButton.isEnabled = true
        betOneButton.isEnabled = true
    }

    private fun updateUi(state: MainViewModel.GameState) {
        Timber.d("UI State: %s", state)
        when(state) {
            MainViewModel.GameState.START -> {
                unHiglightRowsUi()
                CardUiUtils.makeCardsVisibile(cardViews)
                clearWonLostMoneyUi()
                clearHandEvalUi()
                clearWinningCardsUi()
                showNormalButtonsUi()
            }
            MainViewModel.GameState.DEAL -> {
                showHelpText()
                disableBetChangingUi()
                viewModel.getBestHand(SettingsUtils.getNumTrials(activity))
            }
            MainViewModel.GameState.EVALUATE_NO_BONUS -> {
                clearHoldUi()
                enableBetChangingUi()
            }
            MainViewModel.GameState.EVALUATE_WITH_BONUS -> {
                clearHoldUi()
                disableBetChangingUi()
                showBonusAndCollect()
                updateWonLostMoneyUi(PayOutHelper.calculatePayout(context, viewModel.bet.value ?: 1, viewModel.lastEvaluatedHand.value)) // show money won although user hasnnt collected yet (they might double)
                showWinningCardsUi()
            }
            MainViewModel.GameState.BONUS -> {
                clearWinningCardsUi()
                showDoubleCardUi()
                showRedAndBlack()
            }
        }
    }

    private fun showWinningCardsUi() {
        for ((idx,isWinning) in Evaluate.getWinningCards(viewModel.hand.value).withIndex()){
            if(isWinning) {
                cardViews[idx].setBackgroundResource(R.color.colorYellow)
            }
        }
        Timber.d("winning cards: %s", Evaluate.getWinningCards(viewModel.hand.value))
    }

    private fun clearWinningCardsUi() {
        for(i in 0..4) {
            cardViews[i].background = null
        }
    }

    private fun showHelpText() {
        helpText.visibility = View.VISIBLE
        helpText.text = getString(R.string.tap_card_to_hold)
    }

    private fun showBonusAndCollect() {
        dealButton.text = getString(R.string.collect_button)
        doubleButton.isEnabled = true
    }

    private fun showRedAndBlack() {
        normalButtons.visibility = View.INVISIBLE
        doubleDownButtons.visibility = View.VISIBLE
        helpText.visibility = View.VISIBLE
    }

    private fun showBonusDoneUi() {
        cardViews[2].setImageResource(CardUiUtils.cardToImage(viewModel.hand.value?.get(2)))
        cardLayouts[2].flipTheView()

        // if win show chicken dinner
        if (viewModel.wonLostMoney.value!! > 0) {
            handEvalText.text = getString(R.string.bonus_correct_guess)
        } else {
            handEvalText.text = getString(R.string.bonus_wrong_guess)
        }
        helpText.visibility = View.INVISIBLE
        showNormalButtonsUi()
    }

    private fun showNormalButtonsUi() {
        enableBetChangingUi()
        dealButton.text = getString(R.string.deal_button)
        doubleButton.isEnabled = false

        normalButtons.visibility = View.VISIBLE
        doubleDownButtons.visibility = View.INVISIBLE
    }


    private fun updateWonLostMoneyUi(wonLost: Int) {
        if (wonLost < 0) {
            wonLostText.text = String.format("Lost: %s", wonLost)
        } else {
            wonLostText.text = String.format("Won: %s", wonLost)
        }
    }

    private fun clearWonLostMoneyUi() {
        wonLostText.text = ""
    }

    private fun updateTotalMoneyUi(totalMoney: Int) {
        totalMoneyText.text = String.format("Total: %s", totalMoney)
    }

    private fun updateHandEvalUi(evalHand: Evaluate.Hand) {
        handEvalText.text = evalHand.readableName.toUpperCase(Locale.getDefault())
    }

    private fun clearHandEvalUi() {
        handEvalText.text = ""
    }

    private fun updateOptimalExpectedValue(expectedReturn: Double) {
        optimalReturnText.text = getString(R.string.stats_optimal_return, expectedReturn)
    }

    private fun updateYourExpectedValue(expectedReturn: Double) {
        yourReturnText.text = getString(R.string.stats_your_return, expectedReturn)
    }

    private fun updateHighlightedColumnUi() {

        // unhighlight higlighted columns
        for(row in tableLayout.children) {
            val columns = (row as ViewGroup)
            columns[highLightedColumn].setBackgroundResource(R.color.colorDarkBlue)
        }

        // highlight column
        for (row in tableLayout.children) {
            for (columnIndex in 0 until ((row as ViewGroup).childCount)) {
                if (columnIndex == getHighlightedColumn()) {
                    row[columnIndex].setBackgroundResource(R.color.colorRed)
                }
            }
        }
        highLightedColumn = getHighlightedColumn()
    }

    private fun getHighlightedColumn() : Int {
        return viewModel.bet.value ?: 1 - 1
    }

    private fun toggleHoldUi(cardPos: Int) {
        when (cardsHoldOverlay[cardPos].visibility) {
            View.GONE -> {
                cardsHoldOverlay[cardPos].visibility = View.VISIBLE
            } else -> {
                cardsHoldOverlay[cardPos].visibility = View.GONE
            }
        }
    }

    private fun clearHoldUi() {
        helpText.visibility = View.INVISIBLE
        for (card in cardsHoldOverlay) {
            card.visibility = View.GONE
        }
    }

    private fun updateHighlightedRowUi(handEval: Evaluate.Hand) {
        val rowIndex = handEval.ordinal
        if(rowIndex < tableLayout.childCount) {
            tableLayout[rowIndex].setBackgroundResource(R.color.colorRed)
            highLightedRow = rowIndex
            for(rowElement in (tableLayout[rowIndex] as TableRow).children) {
                rowElement.setBackgroundResource(R.color.colorRed)
            }
        }
    }

    private fun unHiglightRowsUi() {
        if (highLightedRow >= 0) {
            tableLayout[highLightedRow].setBackgroundResource(R.color.colorDarkBlue)
            for ((i,rowElement) in (tableLayout[highLightedRow] as TableRow).children.withIndex()) {
                if (i != getHighlightedColumn()) {
                    rowElement.setBackgroundResource(R.color.colorDarkBlue)
                }
            }
        }
    }

    private fun showDoubleCardUi() {
        helpText.text = getString(R.string.bonus_title)
        cardViews[2].setImageResource(R.drawable.cardback)
        for (i in 0..4) {
            if(i == 2){
                cardLayouts[2].flipTheView()
            } else {
                cardViews[i].visibility = View.GONE
            }
        }
    }

//    private fun resetCardVisibiltyUi() {
//        cardViews.forEach {
//            it.visibility = View.VISIBLE
//        }
//    }

    private fun getCardsToKeepBooleanArray() : BooleanArray {
        val cardsHeld = cardsHoldOverlay.map { it.visibility == View.VISIBLE }.toMutableList()
        cardsHeld.add(false) // hacky :(
        return cardsHeld.toBooleanArray()
    }
    
    private fun getCardsToKeep() : List<Card>{
        val cardsHeldBool = getCardsToKeepBooleanArray()
        val  filteredHand = viewModel.hand.value?.toList()?.filterIndexed { index, _ ->
            cardsHeldBool[index]
        }
        return filteredHand ?: emptyList()
    }

//    private fun showAiCardsToHold(bestHand: List<Card>) {
//        viewModel.hand.value?.let {
//            for ((idx, card) in it.withIndex()) {
//                if (bestHand.contains(card)) {
//                    cardsHoldOverlay[idx].visibility = View.VISIBLE
//                }
//            }
//        }
//    }

//    private fun isFlipping(): Boolean {
////        for (layout in cardLayouts) {
////            if (layout.animation != null && !layout.animation.hasEnded()) {
////                return true
////            }
////        }
////        return false
//        return  flipStatus == CardFlipStatus.FLIPPING
//    }

    private fun setupFlipListener() {
        for (i in 0..4) {
            cardLayouts[i].setOnFlipListener { easyFlipView, newCurrentSide ->
                Timber.d("Flipping done ${easyFlipView.tag}")
            }
        }
    }

//    private fun flip(state: MainViewModel.CardFlipState, cards: List<Card>) {
//        SoundManager.playSound(requireActivity(), SoundManager.SoundType.FLIP)
//        Timber.d("Flip State: %s", state)
//
//        isMidFlip = true
//
//        when(state) {
//            MainViewModel.CardFlipState.FACE_DOWN -> {
//                for (i in 0..4) {
//                    cardLayouts[i].flipTheView()
//                }
//            }
//            MainViewModel.CardFlipState.FACE_UP -> {
//                for (i in 0..4) {
//                    if(!viewModel.getKeptCardIndeces()[i]) {
//                        cardViews[i].setImageResource(CardUiUtils.cardToImage(cards[i]))
//                        cardLayouts[i].flipTheView()
//                    }
//                }
//            }
//            MainViewModel.CardFlipState.FULL_FLIP -> {
//                /**
//                 * Hacky - had to add a 6th invisible view :(
//                 * */
//                var numCardsFlipped = 0
//                for ((i,card) in cardLayouts.withIndex()) {
//                    if(!viewModel.getKeptCardIndeces()[i]) {
//                        card.flipTheView()
//                    }
//                    card.setOnFlipListener { _, _ ->
//                        numCardsFlipped += 1
//                        if(numCardsFlipped == (viewModel.getKeptCardIndeces().count { !it })) {
//                            viewModel.cardFLipState.value = MainViewModel.CardFlipState.FACE_UP
//                        }
//                    }
//                }
//            }
//        }
//    }

    private fun flip(state: MainViewModel.CardFlipState, cards: List<Card>) {
        SoundManager.playSound(requireActivity(), SoundManager.SoundType.FLIP)
        Timber.d("Flip State: %s", state)

        isMidFlip = true


//        for (i in 0..4) {
//            cardLayouts[i].setOnFlipListener { easyFlipView, newCurrentSide ->
//                Timber.d("Flipper: ${newCurrentSide.name}")
//            }
//        }
//

        when(state) {
            MainViewModel.CardFlipState.FACE_DOWN -> {
                for (i in 0..4) {
                    cardLayouts[i].flipTheView()
//                    cardLayouts[i].setOnFlipListener { _, _ ->
//                        flipStatus = CardFlipStatus.NOT_FLIPPING
//                    }
                }
            }
            MainViewModel.CardFlipState.FACE_UP -> {
                for (i in 0..4) {
                    if(!viewModel.getKeptCardIndeces()[i]) {
                        cardViews[i].setImageResource(CardUiUtils.cardToImage(cards[i]))
                        cardLayouts[i].flipTheView()
//                        cardLayouts[i].setOnFlipListener { _, _ ->
//                            flipStatus = CardFlipStatus.NOT_FLIPPING
//                        }
                    }
                }
            }
            MainViewModel.CardFlipState.FULL_FLIP -> {
                /**
                 * Hacky - had to add a 6th invisible view :(
                 * */
                for ((i,card) in cardLayouts.withIndex()) {
                    if(!viewModel.getKeptCardIndeces()[i]) {
                        card.isAutoFlipBack = true
                        card.autoFlipBackTime = 200
                        card.flipTheView()
                    }
                }
            }
        }
    }

    private fun populatePayoutTable() {
        val payTable = PayOutHelper.getPayoutMap(context)?.toList()
        for((j,row) in tableLayout.children.withIndex()){
            for ((i,rowElement) in (row as TableRow).children.withIndex()) {
                if(i!=0){
                    val rowTextView = rowElement as TextView
                    rowTextView.text = "${payTable?.get(j)?.second?.get(i-1)}"
                }
            }
        }
    }
}