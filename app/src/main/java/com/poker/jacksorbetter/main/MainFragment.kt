package com.poker.jacksorbetter.main

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.poker.jacksorbetter.HandFragment
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.cardgame.*
import com.poker.jacksorbetter.cardgame.dialog.GoldenGodDialog
import com.poker.jacksorbetter.cardgame.dialog.ResetMoneyDialog
import com.poker.jacksorbetter.cardgame.dialog.SnackBarUtils.snack
import com.poker.jacksorbetter.cardgame.ui.AdHelper
import com.poker.jacksorbetter.cardgame.ui.CardUiUtils
import com.poker.jacksorbetter.cardgame.ui.PayTableUiUtils
import com.poker.jacksorbetter.handstatui.StatDialogUtils
import com.poker.jacksorbetter.leaderboard.HighScoreFragment
import com.poker.jacksorbetter.leaderboard.HighScoreViewModel
import com.poker.jacksorbetter.leaderboard.SignInViewModel
import com.poker.jacksorbetter.settings.SettingsUtils
import com.poker.jacksorbetter.stats.StatisticsManager
import com.wajahatkarim3.easyflipview.EasyFlipView
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_fragment.view.*
import timber.log.Timber
import kotlin.math.abs


class MainFragment : Fragment(), ResetMoneyDialog.MoneyButton {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var highscoreViewModel: HighScoreViewModel

    private lateinit var tableLayout: TableLayout
    private var highLightedRow = -1
    private var highLightedColumn = 1

    private lateinit var mainLayout: ConstraintLayout
    private lateinit var winnningTextLayout: ConstraintLayout
    private lateinit var winningBannerTextLayout: ConstraintLayout

    private lateinit var handEvalText: TextView
    private lateinit var totalMoneyText: TextView
    private lateinit var betText: TextView
    private lateinit var wonLostText: TextView
    private lateinit var helpText: TextView
    private lateinit var winningHandText: TextView
    private lateinit var winningHandPayText: TextView
    private lateinit var topTextLayout: ConstraintLayout

    private lateinit var doubleDownButtons: View
    private lateinit var normalButtons: View
    private lateinit var betMaxButton: Button
    private lateinit var betOneButton: Button
    private lateinit var dealButton: Button
    private lateinit var doubleButton: Button
    private lateinit var doubleRedButton: Button
    private lateinit var doubleBlackButton: Button
    private lateinit var explainButton: Button

    private lateinit var autoHold: CheckBox
    private lateinit var minusButton: Button
    private lateinit var plusButton: Button

    private lateinit var wrongText: TextView
    private lateinit var correctText: TextView
    private lateinit var correctCountText: TextView
    private lateinit var wrongCountText: TextView
    private lateinit var accuracyText: TextView
    private lateinit var optimalExpectedText: TextView
    private lateinit var yourExpectedText: TextView
    private lateinit var betCircleText: TextView
    private lateinit var betCircleBack: TextView

    private lateinit var numberOfHands: TextView
    private lateinit var handsFragContainer: FrameLayout

    private var cardLayouts: MutableList<EasyFlipView> = mutableListOf()
    private var cardViews: MutableList<ImageView> = mutableListOf()
    private var cardBackViews: MutableList<ImageView> = mutableListOf()
    private var cardsHoldOverlay: MutableList<TextView> = mutableListOf()

    private var actionBar: ActionBar? = null


//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setHasOptionsMenu(true)
//    }
//
//    override fun onPrepareOptionsMenu(menu: Menu) {
////        menu.findItem(R.id.action_search).isVisible = false
//        super.onPrepareOptionsMenu(menu)
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        // TODO Add your menu entries here
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.menu_items, menu)
//    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.main_fragment, container, false)

        mainLayout = view.findViewById(R.id.main)
        tableLayout = view.findViewById(R.id.payoutTable)
        handEvalText = view.findViewById(R.id.handEval)
        totalMoneyText = view.findViewById(R.id.totalMoney)
        betText = view.findViewById(R.id.bet)
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
        winningHandText = view.findViewById(R.id.handEval2)
        winningHandPayText = view.findViewById(R.id.handEvalPay)
        wrongText = view.findViewById(R.id.wrongTrainingText)
        correctText = view.findViewById(R.id.correctTrainingText)
        correctCountText = view.findViewById(R.id.correctCount)
        wrongCountText = view.findViewById(R.id.wrongCount)
        accuracyText = view.findViewById(R.id.accuracy)
        optimalExpectedText = view.findViewById(R.id.optimalChoice)
        yourExpectedText = view.findViewById(R.id.yourChoice)
        explainButton = view.findViewById(R.id.explainButton)
        numberOfHands = view.findViewById(R.id.numHands)
        handsFragContainer = view.findViewById(R.id.hand_container)
        winnningTextLayout = view.findViewById(R.id.winnningTextLayout)
        betCircleText = view.findViewById(R.id.betCircleText)
        betCircleBack = view.findViewById<View>(R.id.back5).findViewById(R.id.betCircleBackText)
        winningBannerTextLayout = view.findViewById(R.id.winningLayout)
        topTextLayout = view.findViewById(R.id.topText)

        betCircleBack.visibility = View.VISIBLE


        cardLayouts.clear()
        cardLayouts.add(view.findViewById(R.id.card1layout))
        cardLayouts.add(view.findViewById(R.id.card2layout))
        cardLayouts.add(view.findViewById(R.id.card3layout))
        cardLayouts.add(view.findViewById(R.id.card4layout))
        cardLayouts.add(view.findViewById(R.id.card5layout))

        cardViews.clear()
        cardViews.add(view.findViewById(R.id.cardfront1))
        cardViews.add(view.findViewById(R.id.cardfront2))
        cardViews.add(view.findViewById(R.id.cardfront3))
        cardViews.add(view.findViewById(R.id.cardfront4))
        cardViews.add(view.findViewById(R.id.cardfront5))

        cardBackViews.clear()
        cardBackViews.add(view.findViewById<View>(R.id.back1).findViewById(R.id.cardback1))
        cardBackViews.add(view.findViewById<View>(R.id.back2).findViewById(R.id.cardback1))
        cardBackViews.add(view.findViewById<View>(R.id.back3).findViewById(R.id.cardback1))
        cardBackViews.add(view.findViewById<View>(R.id.back4).findViewById(R.id.cardback1))
        cardBackViews.add(view.findViewById<View>(R.id.back5).findViewById(R.id.cardback1))

        cardsHoldOverlay.clear()
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
                        updateTrainingYourExpected(viewModel.lookupExpectedValue(getCardsToKeep()))
                        viewModel.cardsHeld.value = getCardsToKeep().toMutableList()
                    }
                    else -> {}
                }
            }
        }

        val reset = view.findViewById<TextView>(R.id.totalMoneyReset)
        reset.setOnClickListener {
            ResetMoneyDialog.showDialog(requireContext(), this)
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
                    if ((viewModel.bet.value ?: 1) > (viewModel.totalMoney.value ?: 0)) {
                        ResetMoneyDialog.showDialog(requireContext(), this)
                    } else {
                        viewModel.newGame()
                        viewModel.cardFLipState.value = MainViewModel.CardFlipState.FACE_UP
                    }
                }
                MainViewModel.GameState.DEAL -> {
                    viewModel.evaluateHand(getCardsToKeepBooleanArray(), getCardsToKeep())
                }
                MainViewModel.GameState.EVALUATE_NO_BONUS -> {
                    if ((viewModel.bet.value ?: 1) > (viewModel.totalMoney.value ?: 0)) {
                        ResetMoneyDialog.showDialog(requireContext(), this)
                    } else {
                        viewModel.gameState.value = MainViewModel.GameState.START
                        viewModel.newGame()
                        viewModel.cardFLipState.value = MainViewModel.CardFlipState.FULL_FLIP
                    }
                }
                MainViewModel.GameState.EVALUATE_WITH_BONUS -> {
                    // user is opting out of double down
                    viewModel.collect()
                    viewModel.gameState.value = MainViewModel.GameState.START
                    viewModel.cardFLipState.value = MainViewModel.CardFlipState.FACE_DOWN
                }
                MainViewModel.GameState.BONUS -> {
                    if ((viewModel.bet.value ?: 1) > (viewModel.totalMoney.value ?: 0)) {
                        ResetMoneyDialog.showDialog(requireContext(), this)
                    } else {
                        viewModel.gameState.value = MainViewModel.GameState.START
                        viewModel.newGame()
                        viewModel.cardFLipState.value = MainViewModel.CardFlipState.FULL_FLIP
                    }
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

        explainButton.setOnClickListener {
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
                    mainLayout.snack("Deal First", Snackbar.LENGTH_LONG)
                }
            }
        }

        minusButton = view.findViewById<Button>(R.id.minusButton)
        minusButton.setOnClickListener {
            if(viewModel?.gameState.value == MainViewModel.GameState.START
                || viewModel?.gameState.value == MainViewModel.GameState.EVALUATE_NO_BONUS) {
                viewModel.decreaseHands()
            } else {
                mainLayout.snack("Can't change mid game", Snackbar.LENGTH_LONG)
            }
        }

        plusButton = view.findViewById<Button>(R.id.plusButton)
        plusButton.setOnClickListener {
            if(viewModel?.gameState.value == MainViewModel.GameState.START
                || viewModel?.gameState.value == MainViewModel.GameState.EVALUATE_NO_BONUS) {
                viewModel.incrementHands()
            } else {
                mainLayout.snack("Can't change mid game", Snackbar.LENGTH_LONG)
            }
        }

        val threeDots = view.findViewById<ImageView>(R.id.dots)
        threeDots.setOnClickListener {
            if(actionBar?.isShowing == true){
                actionBar?.hide()
            } else {
                actionBar?.show()
            }
        }

        topTextLayout.setOnClickListener {
            if(actionBar?.isShowing == true){
                actionBar?.hide()
            } else {
                actionBar?.show()
            }
        }

        childFragmentManager.beginTransaction()
            .replace(R.id.hand_container, HandFragment.newInstance())
            .commitNow()

        PayTableUiUtils.populatePayTable(
            tableLayout,
            SettingsUtils.getPayoutTable(requireContext())
        )
        CardUiUtils.showCardBacks(cardBackViews)
        AdHelper.setupAd(requireActivity(), view, "ca-app-pub-7137320034166109/9607206136")
        return view
    }

    override fun onPause() {
        super.onPause()
        StatisticsManager.writeStatisticsToDisk()
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        actionBar = (activity as AppCompatActivity?)?.supportActionBar
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        signInViewModel = ViewModelProvider(requireActivity()).get(SignInViewModel::class.java)
        highscoreViewModel = ViewModelProvider(requireActivity()).get(HighScoreViewModel::class.java)

        SoundManager.load(requireActivity())

        signInViewModel.setPopUpView(requireContext(), mainLayout)
        signInViewModel.googleAccount.observe(viewLifecycleOwner, Observer {
            loadHighScoreFragment()
        })

        viewModel.bet.observe(viewLifecycleOwner, Observer { currentBet ->
            currentBet?.let {
                updateHighlightedColumnUi()
                updateBetText(currentBet * (viewModel.numberOfHands.value ?: 1))
            }
            betCircleText.text = "$currentBet"
            betCircleBack.text = "$currentBet"
        })

        viewModel.numberOfHands.observe(viewLifecycleOwner, Observer { numHands ->
            numberOfHands.text = "$numHands"
            updateBetText( numHands * (viewModel.bet.value ?: 1))

            if (numHands == 1) {
                showPayTable()
            } else {
                showMultiHandView()
            }
        })

        viewModel.hands.observe(viewLifecycleOwner, Observer { hand ->
            hand?.let {
                updateTrainingYourExpected(viewModel.lookupExpectedValue(getCardsToKeep()))
            }
        })

        viewModel.handEvals.observe(viewLifecycleOwner, Observer { handEvals ->
            handEvals?.let {
                if (handEvals.size > 0) {
                    updateHighlightedRowUi(handEvals[0])
                    SoundManager.playSound(requireContext(), handEvals[0])
                    updateWinningTextUi(handEvals[0])
                }

                viewModel.aiDecision.value?.let {
                    updateTrainingOptimalExpected(it.sortedRankedHands[0].second)
                    val isCorrect =
                        it.sortedRankedHands[0].first.toSet() == getCardsToKeep().toSet()
                    updateTrainingCorrect(isCorrect)
                }
            }
        })

        viewModel.totalMoney.observe(viewLifecycleOwner, Observer { totalMoney ->
            updateTotalMoneyUi(totalMoney)
        })

        viewModel.wonLostMoney.observe(viewLifecycleOwner, Observer { wonLostMoney ->
            updateWonLostMoneyUi(wonLostMoney)

            if (wonLostMoney > 0) {
                highscoreViewModel.submitHighScore(requireContext(), wonLostMoney.toLong())
            }

            if (highscoreViewModel.isGoldenGodScore(wonLostMoney) && !SettingsUtils.isGoldenGod(
                    requireContext()
                )
            ) {
                // check if they are a Golden God
                GoldenGodDialog.showDialog(requireContext(), wonLostMoney)
                SettingsUtils.setGoldenGod(requireContext(), true)
                CardUiUtils.showCardBacks(cardBackViews)
                loadHighScoreFragment()
            }
        })

        viewModel.gameState.observe(viewLifecycleOwner, Observer { state ->
            updateUi(state)
        })

        viewModel.cardFLipState.observe(viewLifecycleOwner, Observer { state ->
            flip(state, viewModel.hands.value?.get(0)?.toList() ?: emptyList())
        })

        viewModel.aiDecision.observe(viewLifecycleOwner, Observer { decision ->
            val sortedHands = decision.sortedRankedHands
            if (autoHold.isChecked && viewModel.gameState.value == MainViewModel.GameState.DEAL) {
                CardUiUtils.highlightHeldCards(
                    cardsHoldOverlay,
                    viewModel.hands.value?.get(0),
                    sortedHands[0].first
                )
                SoundManager.playSound(requireActivity(), SoundManager.SoundType.CHIME)
                updateTrainingYourExpected(sortedHands[0].second)
                viewModel.cardsHeld.value = getCardsToKeep().toMutableList()
            }
            updateTrainingOptimalExpected(sortedHands[0].second)
        })

        loadHighScoreFragment()
    }

    private fun showMultiHandView() {
//        val constraintSet = ConstraintSet()
//        constraintSet.clone(mainLayout)
//        constraintSet.connect(
//            R.id.winnningTextLayout,
//            ConstraintSet.TOP,
//            R.id.hand_container,
//            ConstraintSet.BOTTOM,
//            0
//        )
//        constraintSet.connect(
//            R.id.winnningTextLayout,
//            ConstraintSet.START,
//            ConstraintSet.PARENT_ID,
//            ConstraintSet.START,
//            0
//        )
//        constraintSet.connect(
//            R.id.winnningTextLayout,
//            ConstraintSet.END,
//            ConstraintSet.PARENT_ID,
//            ConstraintSet.END,
//            0
//        )
//        constraintSet.connect(
//            R.id.winnningTextLayout,
//            ConstraintSet.BOTTOM,
//            R.id.hand,
//            ConstraintSet.TOP,
//            0
//        )
//        constraintSet.applyTo(mainLayout)

        handsFragContainer.visibility = View.VISIBLE
        payoutTable.visibility = View.GONE
    }

    private fun showPayTable() {
//        val constraintSet = ConstraintSet()
//        constraintSet.clone(mainLayout)
//        constraintSet.connect(
//            R.id.winnningTextLayout,
//            ConstraintSet.TOP,
//            R.id.payoutTable,
//            ConstraintSet.BOTTOM,
//            0
//        )
//        constraintSet.connect(
//            R.id.winnningTextLayout,
//            ConstraintSet.START,
//            ConstraintSet.PARENT_ID,
//            ConstraintSet.START,
//            0
//        )
//        constraintSet.connect(
//            R.id.winnningTextLayout,
//            ConstraintSet.END,
//            ConstraintSet.PARENT_ID,
//            ConstraintSet.END,
//            0
//        )
//        constraintSet.connect(
//            R.id.winnningTextLayout,
//            ConstraintSet.BOTTOM,
//            R.id.hand,
//            ConstraintSet.TOP,
//            0
//        )
//        constraintSet.applyTo(mainLayout)

        handsFragContainer.visibility = View.GONE
        payoutTable.visibility = View.VISIBLE
    }

    private fun updateBetText(currentBet: Int?) {
        betText.text = getString(R.string.bet, currentBet)
    }


    override fun onDestroy() {
        super.onDestroy()
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
                clearWinningTextUi()
                PayTableUiUtils.unblink(tableLayout)
                initializeTrainingView()
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
                updateWonLostMoneyUi(viewModel.calculatePayout()) // show money won although user hasnnt collected yet (they might double)
                showWinningCardsUi()
                PayTableUiUtils.blinkRow(
                    requireContext(), tableLayout, viewModel.handEvals.value?.get(
                        0
                    )?.ordinal
                )
            }
            MainViewModel.GameState.BONUS -> {
                clearWinningCardsUi()
                showDoubleCardUi()
                showRedAndBlack()
                clearWinningTextUi()
            }
        }
    }

    private fun showWinningCardsUi() {
        for ((idx, isWinning) in Evaluate.getWinningCards(viewModel.hands.value?.get(0)).withIndex()){
            if(isWinning) {
                cardViews[idx].setBackgroundResource(R.color.colorYellow)
            }
        }
        Timber.d("winning cards: %s", Evaluate.getWinningCards(viewModel.hands.value?.get(0)))
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
        cardViews[2].setImageResource(
            CardUiUtils.cardToImage(
                viewModel.hands.value?.get(0)?.get(2)
            )
        )
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
            wonLostText.text = getString(R.string.loss, abs(wonLost))
        } else {
            wonLostText.text = getString(R.string.won, wonLost)
        }
    }

    private fun clearWonLostMoneyUi() {
        wonLostText.text = ""
    }

    private fun updateTotalMoneyUi(totalMoney: Int) {
        totalMoneyText.text = getString(R.string.total, totalMoney)
    }

    private fun clearHandEvalUi() {
        handEvalText.text = ""
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
            for ((i, rowElement) in (tableLayout[highLightedRow] as TableRow).children.withIndex()) {
                if (i != getHighlightedColumn()) {
                    rowElement.setBackgroundResource(R.color.colorDarkBlue)
                }
            }
        }
    }

    private fun showDoubleCardUi() {
        helpText.text = getString(R.string.bonus_title)
        cardViews[2].setImageResource(SettingsUtils.getCardBack(requireContext()))
        for (i in 0..4) {
            if(i != 2){
                cardViews[i].visibility = View.GONE
            } else {
                cardLayouts[i].isAutoFlipBack = false
                cardLayouts[i].flipTheView()
            }
        }
    }

    private fun getCardsToKeepBooleanArray() : BooleanArray {
        val cardsHeld = cardsHoldOverlay.map { it.visibility == View.VISIBLE }.toMutableList()
        return cardsHeld.toBooleanArray()
    }
    
    private fun getCardsToKeep() : List<Card>{
        val cardsHeldBool = getCardsToKeepBooleanArray()
        val  filteredHand = viewModel.hands.value?.get(0)?.toList()?.filterIndexed { index, _ ->
            cardsHeldBool[index]
        }
        return filteredHand ?: emptyList()
    }

    private fun flip(state: MainViewModel.CardFlipState, cards: List<Card>) {
        if(SettingsUtils.isFlipSoundEnabled(requireContext())){
            SoundManager.playSound(requireActivity(), SoundManager.SoundType.FLIP)
        }

        when(state) {
            MainViewModel.CardFlipState.FACE_DOWN -> {
                for (i in 0..4) {
                    cardLayouts[i].isAutoFlipBack = false
                    cardLayouts[i].flipTheView()
                    cardLayouts[i].setOnFlipListener { _, _ ->
//                        enableDealButtonUi()
                    }
                }
            }
            MainViewModel.CardFlipState.FACE_UP -> {
                for (i in 0..4) {
                    cardLayouts[i].isAutoFlipBack = false
                    if (!viewModel.getKeptCardIndeces()[i]) {
                        cardViews[i].setImageResource(CardUiUtils.cardToImage(cards[i]))
                        cardLayouts[i].flipTheView()
                        cardLayouts[i].setOnFlipListener { _, _ ->
//                            enableDealButtonUi()
                        }
                    }
                }
            }
            MainViewModel.CardFlipState.FULL_FLIP -> {
                for (i in 0..4) {
                    cardLayouts[i].isAutoFlipBack = true
                    if (!viewModel.getKeptCardIndeces()[i]) {
                        cardLayouts[i].autoFlipBackTime = 0
                        cardLayouts[i].flipTheView()
                        cardLayouts[i].setOnFlipListener { _, newCurrentSide ->
                            if (newCurrentSide == EasyFlipView.FlipState.BACK_SIDE) {
                                cardViews[i].setImageResource(CardUiUtils.cardToImage(cards[i]))
                            }
//                            enableDealButtonUi()
                        }
                    }
                }
            }
        }
    }

    private fun updateTrainingYourExpected(your: Double) {
        yourExpectedText.visibility = View.VISIBLE
        yourExpectedText.text = getString(
            R.string.training_your_expected, java.text.NumberFormat.getCurrencyInstance().format(
                your
            )
        )
    }

    private fun updateTrainingOptimalExpected(optimal: Double) {
        optimalExpectedText.visibility = View.VISIBLE
        optimalExpectedText.text = getString(
            R.string.training_optimal_expected, java.text.NumberFormat.getCurrencyInstance().format(
                optimal
            )
        )
    }

    private fun updateTrainingCorrect(isCorrect: Boolean) {
        if (isCorrect) {
            wrongText.visibility = View.INVISIBLE
            correctText.visibility = View.VISIBLE
            StatisticsManager.increaseCorrectCount()
        } else {
            wrongText.visibility = View.VISIBLE
            correctText.visibility = View.INVISIBLE
            StatisticsManager.increaseIncorrectCount()
        }
    }

    private fun loadHighScoreFragment() {
        childFragmentManager.beginTransaction().replace(
            R.id.highscore_container,
            HighScoreFragment.newInstance(),
            HighScoreFragment.NAME
        ).commit()
    }

    private fun initializeTrainingView(){
        wrongText.visibility = View.INVISIBLE
        correctText.visibility = View.INVISIBLE
        optimalExpectedText.visibility = View.INVISIBLE
        yourExpectedText.visibility = View.INVISIBLE

        correctCountText.text = context?.getString(
            R.string.correct_count,
            StatisticsManager.getStatistics()?.correctCount
        )
        wrongCountText.text = context?.getString(
            R.string.wrong_count,
            StatisticsManager.getStatistics()?.wrongCount
        )
        accuracyText.text = context?.getString(
            R.string.training_accuracy,
            StatisticsManager.getAccuracy()
        )
    }

    private fun updateWinningTextUi(first: Evaluate.Hand) {
        winningBannerTextLayout.visibility = View.VISIBLE
        winningHandText.text = first.readableName
        winningHandPayText.text = "${PayOutHelper.calculatePayout(
            requireContext(),
            viewModel.bet.value ?: 1,
            first
        )}"
    }

    private fun clearWinningTextUi() {
        winningBannerTextLayout.visibility = View.INVISIBLE
        winningHandText.text = ""
        winningHandPayText.text = ""
    }


    override fun setMoney(amount: Int) {
        viewModel.totalMoney.value = amount
        SettingsUtils.setMoney(amount, requireContext())
        Toast.makeText(requireContext(), "Money set: $$amount", Toast.LENGTH_LONG).show()
    }
}