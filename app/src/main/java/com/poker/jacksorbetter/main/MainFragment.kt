package com.poker.jacksorbetter.main

import Card
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
import androidx.lifecycle.ViewModelProvider
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.snackbar.Snackbar
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.cardgame.*
import com.poker.jacksorbetter.cardgame.dialog.GoldenGodDialog
import com.poker.jacksorbetter.cardgame.dialog.ResetMoneyDialog
import com.poker.jacksorbetter.cardgame.dialog.SnackBarUtils.snack
import com.poker.jacksorbetter.cardgame.ui.AdHelper
import com.poker.jacksorbetter.cardgame.ui.CardUiUtils
import com.poker.jacksorbetter.cardgame.ui.PayTableUiUtils
import com.poker.jacksorbetter.leaderboard.HighScoreFragment
import com.poker.jacksorbetter.leaderboard.HighScoreViewModel
import com.poker.jacksorbetter.leaderboard.SignInViewModel
import com.poker.jacksorbetter.settings.SettingsUtils
import com.poker.jacksorbetter.training.TrainingFragment
import com.poker.jacksorbetter.training.TrainingViewModel
import com.wajahatkarim3.easyflipview.EasyFlipView
import kotlinx.android.synthetic.main.main_fragment.*
import kotlinx.android.synthetic.main.main_fragment.view.*
import timber.log.Timber


class MainFragment : Fragment(), ResetMoneyDialog.MoneyButton {

    companion object {
        fun newInstance() = MainFragment()
    }

    private lateinit var viewModel: MainViewModel
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var highscoreViewModel: HighScoreViewModel
    private lateinit var trainingViewModel: TrainingViewModel

    private var handFragment: HandFragment? = null

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
    private lateinit var plusTenButton: Button
    private lateinit var minusTenButton: Button

    private lateinit var autoHold: CheckBox
    private lateinit var minusButton: Button
    private lateinit var plusButton: Button

    private lateinit var betCircleText: TextView
    private lateinit var betCircleBack: TextView

    private lateinit var numberOfHands: TextView
    private lateinit var handsFragContainer: FrameLayout
    private lateinit var shimmerLayout: ShimmerFrameLayout
    private lateinit var shimmerLayout2: ShimmerFrameLayout
    private lateinit var shimmerLayout3: ShimmerFrameLayout
    private lateinit var shimmerLayout4: ShimmerFrameLayout
    private lateinit var shimmerLayout5: ShimmerFrameLayout


    private var cardLayouts: MutableList<EasyFlipView> = mutableListOf()
    private var cardViews: MutableList<ImageView> = mutableListOf()
    private var cardBackViews: MutableList<ImageView> = mutableListOf()
    private var cardsHoldOverlay: MutableList<TextView> = mutableListOf()

    private var actionBar: ActionBar? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        SettingsUtils.resetNumTrials()
    }


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
        numberOfHands = view.findViewById(R.id.numHands)
        handsFragContainer = view.findViewById(R.id.hand_container)
        winnningTextLayout = view.findViewById(R.id.winnningTextLayout)
        betCircleText = view.findViewById(R.id.betCircleText)
        betCircleBack = view.findViewById<View>(R.id.back5).findViewById(R.id.betCircleBackText)
        winningBannerTextLayout = view.findViewById(R.id.winningLayout)
        topTextLayout = view.findViewById(R.id.topText)
        shimmerLayout = view.findViewById(R.id.shimmer_view_container)
        shimmerLayout2 = view.findViewById(R.id.shimmer_view_container2)
        shimmerLayout3 = view.findViewById(R.id.shimmer_view_container3)
        shimmerLayout4 = view.findViewById(R.id.shimmer_view_container4)
        shimmerLayout5 = view.findViewById(R.id.shimmer_view_container5)
        plusTenButton = view.findViewById(R.id.plusTen)
        minusTenButton = view.findViewById(R.id.minusTen)


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
//                        updateTrainingYourExpected(viewModel.lookupExpectedValue(getCardsToKeep()))
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
                    if (viewModel.calculateBet() > (viewModel.totalMoney.value ?: 0)) {
                        ResetMoneyDialog.showDialog(requireContext(), this)
                    } else {
                        startShimmer()
                        viewModel.newGame()
                        viewModel.cardFLipState.value = MainViewModel.CardFlipState.FACE_UP
                    }
                }
                MainViewModel.GameState.DEAL -> {
                    viewModel.evaluateHand(getCardsToKeepBooleanArray(), getCardsToKeep())
                }
                MainViewModel.GameState.EVALUATE_NO_BONUS -> {
                    if (viewModel.calculateBet() > (viewModel.totalMoney.value ?: 0)) {
                        ResetMoneyDialog.showDialog(requireContext(), this)
                    } else {
                        viewModel.gameState.value = MainViewModel.GameState.START
                        viewModel.newGame()
                        startShimmer()
                        viewModel.cardFLipState.value = MainViewModel.CardFlipState.FULL_FLIP
                    }
                }
                MainViewModel.GameState.EVALUATE_WITH_BONUS -> {
                    // user is opting out of double down
                    viewModel.collect(null, null)
                    viewModel.gameState.value = MainViewModel.GameState.START
                    viewModel.cardFLipState.value = MainViewModel.CardFlipState.FACE_DOWN
                }
                MainViewModel.GameState.BONUS -> {
                    if (viewModel.calculateBet() > (viewModel.totalMoney.value ?: 0)) {
                        ResetMoneyDialog.showDialog(requireContext(), this)
                    } else {
                        viewModel.gameState.value = MainViewModel.GameState.START
                        viewModel.newGame()
                        startShimmer()
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
                viewModel.getBestHand(SettingsUtils.getNumTrials())
            }
        }

        minusButton = view.findViewById(R.id.minusButton)
        minusButton.setOnClickListener {
            if(viewModel?.gameState.value == MainViewModel.GameState.START
                || viewModel?.gameState.value == MainViewModel.GameState.EVALUATE_NO_BONUS) {
                viewModel.decreaseHands()
            } else {
                mainLayout.snack(getString(R.string.hand_increase_error), Snackbar.LENGTH_LONG)
            }
        }

        minusTenButton.setOnClickListener {
            if(viewModel?.gameState.value == MainViewModel.GameState.START
                || viewModel?.gameState.value == MainViewModel.GameState.EVALUATE_NO_BONUS) {
                for(i in 0..9){
                    if(viewModel.numberOfHands.value?:SettingsUtils.getNumHands() > 1) {
                        viewModel.decreaseHands()
                    }
                }
            } else {
                mainLayout.snack(getString(R.string.hand_increase_error), Snackbar.LENGTH_LONG)
            }
        }

        plusButton = view.findViewById(R.id.plusButton)
        plusButton.setOnClickListener {
            if(viewModel?.gameState.value == MainViewModel.GameState.START
                || viewModel?.gameState.value == MainViewModel.GameState.EVALUATE_NO_BONUS) {
                viewModel.incrementHands()
            } else {
                mainLayout.snack(getString(R.string.hand_increase_error), Snackbar.LENGTH_LONG)
            }
        }

        plusTenButton.setOnClickListener {
            if(viewModel?.gameState.value == MainViewModel.GameState.START
                || viewModel?.gameState.value == MainViewModel.GameState.EVALUATE_NO_BONUS) {
                for(i in 0..9){
                    viewModel.incrementHands()
                }
            } else {
                mainLayout.snack(getString(R.string.hand_increase_error), Snackbar.LENGTH_LONG)
            }
        }

        actionBar?.hide()
        val threeDots = view.findViewById<ImageView>(R.id.dots)
        threeDots.setOnClickListener {
            if(actionBar?.isShowing == true){
                actionBar?.hide()
            } else {
                actionBar?.show()
            }
        }


        //todo move this somewhere else and have variable
//        if(handFragment == null){
//        }
        handFragment = HandFragment.newInstance()
        handFragment?.let {
            childFragmentManager.beginTransaction()
                .replace(R.id.hand_container, it)
                .commitNow()
        }


        PayTableUiUtils.populatePayTable(
            tableLayout,
            SettingsUtils.getPayoutTable()
        )

//        WhatsNewDialog.showDialog(requireContext())

        CardUiUtils.showCardBacks(cardBackViews)
        AdHelper.setupAd(requireActivity(), view, "ca-app-pub-7137320034166109/9607206136")
        return view
    }

    override fun onPause() {
        super.onPause()
        handFragment?.onDestroy()
        viewModel.numberOfHands.value = SettingsUtils.getNumHands()
    }

    override fun onDestroy() {
        super.onDestroy()
        SoundManager.release()
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
        trainingViewModel = ViewModelProvider(requireActivity()).get(TrainingViewModel::class.java)

        SoundManager.load(requireActivity())

        signInViewModel.setPopUpView(requireContext(), mainLayout)
        signInViewModel.googleAccount.observe(viewLifecycleOwner,  {
            loadHighScoreFragment()
        })

        viewModel.bet.observe(viewLifecycleOwner,  { currentBet ->
            currentBet?.let {
                updateHighlightedColumnUi()
                updateBetText(currentBet * (viewModel.numberOfHands.value ?: SettingsUtils.getNumHands()))
            }
            betCircleText.text = "$currentBet"
            betCircleBack.text = "$currentBet"
        })

        viewModel.numberOfHands.observe(viewLifecycleOwner,  { numHands ->
            numberOfHands.text = "$numHands"
            updateBetText( numHands * (viewModel.bet.value ?: 1))

            if (numHands == 1) {
                showPayTable()
            } else {
                showMultiHandView()
            }
        })

        viewModel.handEvals.observe(viewLifecycleOwner,  { handEvals ->
            handEvals?.let {
                if (handEvals.size > 0) {
                    updateHighlightedRowUi(handEvals[0])
                    SoundManager.playSound(requireContext(), handEvals[0])
                    updateWinningTextUi(handEvals[0])
                }

                viewModel.aiDecision.value?.let {
                    trainingViewModel.state.value = TrainingViewModel.State.DONE
                }
            }
        })

        viewModel.totalMoney.observe(viewLifecycleOwner,  { totalMoney ->
            updateTotalMoneyUi(totalMoney)
        })

        viewModel.wonLostMoney.observe(viewLifecycleOwner,  { wonLostMoney ->

            updateWonLostMoneyUi(wonLostMoney)
            if (wonLostMoney > 0) {
                highscoreViewModel.submitHighScore(requireContext(), wonLostMoney.toLong())
            }

            if (highscoreViewModel.isGoldenGodScore(wonLostMoney) && !SettingsUtils.isGoldenGod()) {
                // check if they are a Golden God
                GoldenGodDialog.showDialog(requireContext(), wonLostMoney)
                SettingsUtils.setGoldenGod(true)
                CardUiUtils.showCardBacks(cardBackViews)
                loadHighScoreFragment()
            }
        })

        viewModel.gameState.observe(viewLifecycleOwner,  { state ->
            updateUi(state)
        })

        viewModel.cardFLipState.observe(viewLifecycleOwner,  { state ->
            flip(state, viewModel.hands.value?.get(0)?.toList() ?: emptyList())
        })

        viewModel.aiDecision.observe(viewLifecycleOwner,  { decision ->
            val sortedHands = decision.sortedRankedHands
            if (autoHold.isChecked && viewModel.gameState.value == MainViewModel.GameState.DEAL) {
                CardUiUtils.highlightHeldCards(
                    cardsHoldOverlay,
                    viewModel.hands.value?.get(0),
                    sortedHands[0].first
                )

                SoundManager.playSound(requireActivity(), SoundManager.SoundType.CHIME)
                viewModel.cardsHeld.value = getCardsToKeep().toMutableList()
            }
            stopShimmer()
        })

        loadHighScoreFragment()
        loadTrainingFragment()
    }

    private fun showMultiHandView() {
        handsFragContainer.visibility = View.VISIBLE
        payoutTable.visibility = View.GONE
    }

    private fun showPayTable() {
        handsFragContainer.visibility = View.GONE
        payoutTable.visibility = View.VISIBLE
    }

    private fun updateBetText(currentBet: Int?) {
        betText.text = getString(R.string.bet, currentBet)
    }

    private fun disableBetChangingUi() {
        betMaxButton.isEnabled = false
        betOneButton.isEnabled = false
        betMaxButton.text = ""
        betOneButton.text = ""
    }

    private fun enableBetChangingUi() {
        betMaxButton.isEnabled = true
        betOneButton.isEnabled = true
        betMaxButton.text = getString(R.string.bet_max_button)
        betOneButton.text = getString(R.string.bet_one_button)
    }

    private fun startShimmer() {
        if(autoHold.isChecked) {
            shimmerLayout.baseAlpha = 0.8F // opacity of non shimmer part of image
            shimmerLayout.intensity = 0.1F
            shimmerLayout2.baseAlpha = 0.8F // opacity of non shimmer part of image
            shimmerLayout2.intensity = 0.1F
            shimmerLayout3.baseAlpha = 0.8F // opacity of non shimmer part of image
            shimmerLayout3.intensity = 0.1F
            shimmerLayout4.baseAlpha = 0.8F // opacity of non shimmer part of image
            shimmerLayout4.intensity = 0.1F
            shimmerLayout5.baseAlpha = 0.8F // opacity of non shimmer part of image
            shimmerLayout5.intensity = 0.1F

            shimmerLayout.startShimmerAnimation()
            shimmerLayout2.startShimmerAnimation()
            shimmerLayout3.startShimmerAnimation()
            shimmerLayout4.startShimmerAnimation()
            shimmerLayout5.startShimmerAnimation()
        }
    }

    private fun stopShimmer() {
        shimmerLayout.stopShimmerAnimation()
        shimmerLayout2.stopShimmerAnimation()
        shimmerLayout3.stopShimmerAnimation()
        shimmerLayout4.stopShimmerAnimation()
        shimmerLayout5.stopShimmerAnimation()
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
            }
            MainViewModel.GameState.DEAL -> {
                clearWonLostMoneyUi()
                showHelpText()
                disableBetChangingUi()
                viewModel.getBestHand(SettingsUtils.getNumTrials())
            }
            MainViewModel.GameState.EVALUATE_NO_BONUS -> {
                clearHoldUi()
                enableBetChangingUi()
            }
            MainViewModel.GameState.EVALUATE_WITH_BONUS -> {
                clearHoldUi()
                disableBetChangingUi()
                showBonusAndCollect()
                updateWonLostMoneyUi(viewModel.calculatePayout(null)) // show money won although user hasnnt collected yet (they might double)
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
        doubleButton.text = getString(R.string.double_button)
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
        doubleButton.text = ""
        normalButtons.visibility = View.VISIBLE
        doubleDownButtons.visibility = View.INVISIBLE
    }


    private fun updateWonLostMoneyUi(wonLost: Int) {
        if (wonLost == 0) {
            wonLostText.text = ""
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
        cardViews[2].setImageResource(SettingsUtils.getCardBack())
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
        if(SettingsUtils.isFlipSoundEnabled()){
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
                    if (!viewModel.getKeptCardIndices()[i]) {
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
                    if (!viewModel.getKeptCardIndices()[i]) {
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

    private fun loadHighScoreFragment() {
        childFragmentManager.beginTransaction().replace(
            R.id.highscore_container,
            HighScoreFragment.newInstance(),
            HighScoreFragment.NAME
        ).commit()
    }

    private fun loadTrainingFragment() {
        childFragmentManager.beginTransaction().replace(
            R.id.training_container,
            TrainingFragment.newInstance(),
            TrainingFragment.NAME
        ).commit()
    }


    private fun updateWinningTextUi(first: Evaluate.Hand) {
        if (first != Evaluate.Hand.NOTHING || viewModel.numberOfHands.value == 1) {
            winningBannerTextLayout.visibility = View.VISIBLE
            winningHandText.text = first.readableName
            val payout = PayOutHelper.calculatePayout(viewModel.bet.value ?: 1, first)
            winningHandPayText.text = "$payout"
        }
    }

    private fun clearWinningTextUi() {
        winningBannerTextLayout.visibility = View.INVISIBLE
        winningHandText.text = ""
        winningHandPayText.text = ""
    }


    override fun setMoney(amount: Int) {
        viewModel.totalMoney.value = amount
        SettingsUtils.setMoney(amount)
        Toast.makeText(requireContext(), getString(R.string.money_set, amount), Toast.LENGTH_LONG).show()
    }
}