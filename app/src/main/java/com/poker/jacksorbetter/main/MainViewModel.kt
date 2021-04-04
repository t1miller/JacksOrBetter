package com.poker.jacksorbetter.main

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poker.jacksorbetter.cardgame.Card
import com.poker.jacksorbetter.cardgame.Deck
import com.poker.jacksorbetter.cardgame.Evaluate
import com.poker.jacksorbetter.settings.SettingsUtils
import com.poker.jacksorbetter.stats.Game
import com.poker.jacksorbetter.stats.StatisticsManager
import com.poker.jacksorbetter.main.CommonUiUtils.toFormattedStringThreeDecimals
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

    val cardFLipState: MutableLiveData<CardFlipState> by lazy {
        MutableLiveData(CardFlipState.FACE_DOWN)
    }

    private var cardsKept: BooleanArray = listOf(false, false, false, false, false).toBooleanArray()
    private var lastCardsKept: List<Card>? = null
    private var originalHand: List<Card>? = null


    private fun updateTotalMoney(newAmount: Int, isBonus: Boolean) {

        var money = newAmount
        if(money < 0) {
            // no money, top off their money for them
            SettingsUtils.resetMoney(getApplication())
            money = SettingsUtils.Defaults.MONEY // reset cached money value too
        } else {
            SettingsUtils.setMoney(newAmount, getApplication())
        }

        val oldAmount = getMoney()
        if(money > oldAmount) {
            if (isBonus && SettingsUtils.isBonusSoundEnabled(getApplication())) {
                SoundManager.playSound(getApplication(), SoundManager.SoundType.ROOSTER_CROWING)
            } else {
                SoundManager.playSound(getApplication(), SoundManager.SoundType.COLLECTING_COINS)
            }
        } else {
            if(isBonus && SettingsUtils.isBonusSoundEnabled(getApplication())){
                SoundManager.playSound(getApplication(), SoundManager.SoundType.SAD_TROMBONE_4_WOMP)
            } else if(SettingsUtils.isLoseSoundEnabled(getApplication())) {
                SoundManager.playSound(getApplication(), SoundManager.SoundType.SAD_TROMBONE_3_WOMP)
            }
        }
        totalMoney.value = money
    }

    private fun updateLastEvaluatedHand(handEval: Evaluate.Hand) {
        lastEvaluatedHand.value = handEval
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
        cardsKept = listOf(false, false, false, false, false).toBooleanArray()
    }

    fun collect() {
        val money = PayOutHelper.calculatePayout(getApplication(), bet.value, lastEvaluatedHand.value)
        val toteMoney = getMoney() + money
        wonLostMoney.value = money
        updateTotalMoney(toteMoney, false)

        // done w/ game get statistics for game (NON BONUS flow)
        StatisticsManager.addStatistic(Game(bet.value, money, lastEvaluatedHand.value?.readableName, originalHand, lastCardsKept, hand.value))
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
        updateTotalMoney(toteMoney,true)

        // done w/ game get statistics for game (BONUS flow)
        StatisticsManager.addStatistic(Game(bet.value, money, lastEvaluatedHand.value?.readableName, originalHand, lastCardsKept, hand.value))
    }

    fun evaluateHand(cardsToKeep: BooleanArray, cards: List<Card>) {

        cardsKept = cardsToKeep
        lastCardsKept = cards
        originalHand = hand.value?.toList()
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

    fun getOriginalHand() : List<Card>? {
        return originalHand
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