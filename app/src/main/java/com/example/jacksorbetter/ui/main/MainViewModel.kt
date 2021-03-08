package com.example.jacksorbetter.ui.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.jacksorbetter.cardgame.Card
import com.example.jacksorbetter.cardgame.Deck
import com.example.jacksorbetter.cardgame.Evaluate
import com.example.jacksorbetter.settings.SettingsUtils
import com.example.jacksorbetter.stats.LastGame
import com.example.jacksorbetter.stats.StatisticsManager
import com.example.jacksorbetter.ui.main.CommonUiUtils.toFormattedStringThreeDecimals
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.system.measureTimeMillis

class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        const val DEFAULT_BET = 1
    }

    enum class GameState {
        START,
        DEAL,
        EVALUATE_NO_BONUS,
        EVALUATE_WITH_BONUS,
        BONUS,
    }

    enum class CardFlipState {
        FACE_UP,
        FACE_DOWN,
        FULL_FLIP
    }

    val bet: MutableLiveData<Int> by lazy {
        MutableLiveData(DEFAULT_BET)
    }

    val hand: MutableLiveData<MutableList<Card>> = MutableLiveData()

    var aiDecision : MutableLiveData<AIDecision> = MutableLiveData()

    val totalMoney: MutableLiveData<Int> by lazy {
        MutableLiveData(SettingsUtils.getMoney(application))
    }

    val wonLostMoney: MutableLiveData<Int> = MutableLiveData()

    val lastEvaluatedHand: MutableLiveData<Evaluate.Hand> = MutableLiveData()

    val gameState: MutableLiveData<GameState> by lazy {
        MutableLiveData(GameState.START)
    }

    val cardFLipState: MutableLiveData<CardFlipState> = MutableLiveData()

    private var cardsKept: BooleanArray = listOf(false, false, false, false, false, false).toBooleanArray()
    private var lastCardsKept: List<Card>? = null


    private fun updateMoney(money: Int) {
        SettingsUtils.setMoney(money, getApplication())

        val oldAmount = getMoney()
        if(money > oldAmount) {
            SoundManager.playSound(getApplication(), SoundManager.SoundType.COLLECTING_COINS)
//            StatisticsManager.addStatistic(money-oldAmount, 0, null, null)
        } else {
//            StatisticsManager.addStatistic(0, money-oldAmount, null, null)
        }
        // todo update stats when user win losses bonus round


        totalMoney.value = money
    }

    private fun updateLastEvaluatedHand(handEval: Evaluate.Hand) {
        lastEvaluatedHand.value = handEval
//        StatisticsManager.addStatistic(0, 0, handEval.readableName, null)
    }

    private fun getMoney() : Int {
        return totalMoney.value ?: SettingsUtils.getMoney(getApplication())
    }

    fun incrementBet() {
        val currentBet = bet.value
        if (currentBet == null || currentBet == 5) {
            bet.value = 1
        } else {
            bet.value = currentBet + 1
        }
    }

    fun maxBet() {
        bet.value = 5
    }

    fun newGame() {
        Deck.newDeck()
        hand.value = Deck.draw5()
        gameState.value = GameState.DEAL
        cardsKept = listOf(false, false, false, false, false, false).toBooleanArray()
    }

    fun collect() {
        val money = PayOutHelper.calculatePayout(getApplication(), bet.value, lastEvaluatedHand.value)
        val toteMoney = getMoney() + money
        wonLostMoney.value = money
        updateMoney(toteMoney)
    }

    /**
     *  C[2] is the index of the card to guess
     */
    fun collectBonus(isGuessRed: Boolean) {
        Deck.newDeck()
        hand.value = Deck.draw5()

        val tempPot = PayOutHelper.calculatePayout(getApplication(), bet.value, lastEvaluatedHand.value)
        val money = PayOutHelper.calculateBonusPayout(tempPot, hand.value?.get(2), isGuessRed)
        val toteMoney = getMoney() + money
        wonLostMoney.value = money
        updateMoney(toteMoney)
    }

    fun evaluateHand(cardsToKeep: BooleanArray, cards: List<Card>) {

        cardsKept = cardsToKeep
        lastCardsKept = cards
        val originalHand = hand.value?.toList()
        if(cardsToKeep.toList().contains(false)) {
            val tempHand = mutableListOf<Card>()
            for (i in 0 until 5) {
                if (!cardsToKeep[i]) {
                    tempHand.add(Deck.draw1())
                } else {
                    tempHand.add(hand.value?.get(i) ?: Deck.draw1())
                }
            }
            hand.value = tempHand
            cardFLipState.value = CardFlipState.FULL_FLIP
        }

        val evaluation = Evaluate.analyzeHand(hand.value ?: emptyList())
        updateLastEvaluatedHand(evaluation)

        StatisticsManager.addStatistic(LastGame(bet.value, PayOutHelper.calculatePayout(getApplication(), bet.value, evaluation), evaluation.readableName, originalHand, cards, hand.value))

        if (evaluation == Evaluate.Hand.NOTHING) {
            collect()
            gameState.value = GameState.EVALUATE_NO_BONUS
        } else {
            gameState.value = GameState.EVALUATE_WITH_BONUS
        }
    }

    fun getKeptCardIndeces() : BooleanArray {
        return cardsKept
    }

    fun lastKeptCards() : List<Card>? {
        return lastCardsKept
    }

    fun getBestHand(numTrials: Int) {
        viewModelScope.launch {
            runGetBestHand(numTrials)
        }
    }

    private suspend fun runGetBestHand(numTrials: Int) {
        withContext(Dispatchers.IO) {
            hand.value?.let {
                val timeInMillis = measureTimeMillis {
                    aiDecision.postValue(AIPlayer.calculateBestHands(getApplication(), bet.value ?: DEFAULT_BET,it, numTrials))
                }
                Timber.d("MonteCarlo simulation took $timeInMillis ms")
            }
        }
    }

    fun lookupExpectedValue(hand: List<Card>) : Double {
        val sortedHands = aiDecision.value?.sortedRankedHands
        if(sortedHands == null){
            Timber.d("Nothing to lookup")
            return 0.0
        }

        for(sortedHand in sortedHands) {
            if(sortedHand.first.toSet() == hand.toSet()) {
                return sortedHand.second.toFormattedStringThreeDecimals()
            }
        }
        return 0.0
    }
}