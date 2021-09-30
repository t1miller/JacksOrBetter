package com.poker.jacksorbetter.main

import Card
import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.cardgame.Deck
import com.poker.jacksorbetter.cardgame.Deck2
import com.poker.jacksorbetter.cardgame.Evaluate
import com.poker.jacksorbetter.main.CommonUiUtils.toFormattedStringThreeDecimals
import com.poker.jacksorbetter.settings.SettingsUtils
import com.poker.jacksorbetter.stats.Game
import com.poker.jacksorbetter.stats.StatisticsManager
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

    val numberOfHands: MutableLiveData<Int> by lazy {
        MutableLiveData(SettingsUtils.getNumHands())
    }

    private var otherDecks: MutableList<Deck2>? = null

    private val bonusHand: MutableLiveData<MutableList<Card>> = MutableLiveData()

    val hands: MutableLiveData<MutableList<MutableList<Card>>> = MutableLiveData()

    val handEvals: MutableLiveData<MutableList<Evaluate.Hand>> = MutableLiveData(mutableListOf())

    var aiDecision : MutableLiveData<AIDecision> = MutableLiveData()

    val totalMoney: MutableLiveData<Int> by lazy {
        MutableLiveData(SettingsUtils.getMoney())
    }

    val wonLostMoney: MutableLiveData<Int> = MutableLiveData()

    private val lastEvaluatedHand: MutableLiveData<Evaluate.Hand> = MutableLiveData()

    val gameState: MutableLiveData<GameState> by lazy {
        MutableLiveData(GameState.START)
    }

    val cardFLipState: MutableLiveData<CardFlipState> by lazy {
        MutableLiveData(CardFlipState.FACE_DOWN)
    }

    val cardsHeld: MutableLiveData<MutableList<Card>> by lazy {
        MutableLiveData(mutableListOf())
    }

    private var cardsKept: BooleanArray = listOf(false, false, false, false, false).toBooleanArray()
    private var lastCardsKept: List<Card>? = null
    private var originalHand: List<Card>? = null


    private fun updateTotalMoney(newAmount: Int, isBonus: Boolean) {

        SettingsUtils.setMoney(newAmount)
        val oldAmount = getMoney()
        if(newAmount > oldAmount) {
            if (isBonus && SettingsUtils.isBonusSoundEnabled()) {
                SoundManager.playSound(getApplication(), SoundManager.SoundType.ROOSTER_CROWING)
            } else {
                SoundManager.playSound(getApplication(), SoundManager.SoundType.COLLECTING_COINS)
            }
        }
        totalMoney.postValue(newAmount)
    }

    private fun getMoney() : Int {
        return totalMoney.value ?: SettingsUtils.getMoney()
    }

    fun incrementHands() {
        val currentHands = numberOfHands.value ?: SettingsUtils.getNumHands()
        numberOfHands.value = currentHands + 1
        SettingsUtils.setNumHands(numberOfHands.value)
        Timber.d("incrementHands() hand ${hands.value}")
    }

    fun decreaseHands() {
        val currentHands = numberOfHands.value ?: SettingsUtils.getNumHands()
        if(currentHands > 1){
            numberOfHands.value = currentHands - 1
            SettingsUtils.setNumHands(numberOfHands.value)
        } else {
            Toast.makeText(getApplication(), getApplication<Application>().resources.getString(R.string.hand_decrease_error), Toast.LENGTH_LONG).show()
        }
        Timber.d("decreaseHands() ${hands.value}")
    }

    fun incrementBet() {
        val currentBet = bet.value
        if (currentBet == null || currentBet == 5) {
            bet.value = 1
        } else {
            bet.value = (currentBet + 1)
        }
    }

    fun maxBet() {
        bet.value = 5
    }

    fun calculateBet() : Int{
        return (bet.value ?: 1) * (numberOfHands.value ?: SettingsUtils.getNumHands())
    }

    fun newGame() {
        val deck = Deck2()
        val hand = deck.draw5()
        hands.value = MutableList(numberOfHands.value?:SettingsUtils.getNumHands()){ hand }
        otherDecks = MutableList(numberOfHands.value?:SettingsUtils.getNumHands()){ Deck2() }
        otherDecks?.forEach {
            it.removeCards(hand)
        }

        gameState.value = GameState.DEAL
        cardsKept = listOf(false, false, false, false, false).toBooleanArray()
        subtractBet()
    }

    private fun subtractBet() {
        val toteMoney = getMoney() - calculateBet()
        updateTotalMoney(toteMoney, false)
    }

    fun collect(handEvalsTemp: List<Evaluate.Hand>?, handsTemp: MutableList<MutableList<Card>>?) {
        val payout = calculatePayout(handEvalsTemp)
        val toteMoney = getMoney() + payout
        wonLostMoney.postValue(payout)
        updateTotalMoney(toteMoney, false)

        // done w/ game get statistics for game (NON BONUS flow)
        if(handEvalsTemp == null || handsTemp == null){
            StatisticsManager.addStatistic(Game(bet.value, payout, handEvals.value?.get(0)?.readableName, originalHand, lastCardsKept, hands.value?.get(0)))
        } else {
            StatisticsManager.addStatistic(Game(bet.value, payout, handEvalsTemp[0].readableName, originalHand, lastCardsKept, handsTemp[0]))
        }
    }

    /**
     *  C[2] is the index of the card to guess
     */
    fun collectBonus(isGuessRed: Boolean) {
        Deck.newDeck()
        bonusHand.value = Deck.draw5()

        val payout = calculatePayout(null)
        val totalPayout = PayOutHelper.calculateBonusPayout(payout, hands.value?.get(0)?.get(2), isGuessRed)
        val toteMoney = getMoney() + totalPayout
        wonLostMoney.value = totalPayout
        updateTotalMoney(toteMoney,true)

        // done w/ game get statistics for game (BONUS flow)
        StatisticsManager.addStatistic(Game(bet.value, totalPayout, handEvals.value?.get(0)?.readableName, originalHand, lastCardsKept, hands.value?.get(0)))
    }

    fun calculatePayout(handEvalsTemp: List<Evaluate.Hand>?) : Int {
        var payout = 0
        repeat(numberOfHands.value ?: 0) {
            val handEval = if (handEvalsTemp != null) handEvalsTemp[it] else handEvals.value?.get(it)
            var tempPayout = PayOutHelper.calculatePayout(bet.value, handEval)
            if (tempPayout < 0) {
                tempPayout = 0
            }
            payout += tempPayout
        }
        return payout
    }

    fun evaluateHand(cardsToKeep: BooleanArray, cards: List<Card>) {
        viewModelScope.launch {
            runEvaluateHand(cardsToKeep, cards)
        }
    }

    private suspend fun runEvaluateHand(cardsToKeep: BooleanArray, cards: List<Card>) {
        withContext(Dispatchers.IO) {
            cardsKept = cardsToKeep
            lastCardsKept = cards
            originalHand = hands.value?.get(0)?.toList()
            val handEvalsTemp = mutableListOf<Evaluate.Hand>()
            val handTemp = MutableList(numberOfHands.value ?: SettingsUtils.getNumHands()){ mutableListOf<Card>()}
            hands.value?.forEachIndexed { index, han ->
                for (i in 0 until 5) {
                    if (!cardsToKeep[i]) {
                        handTemp[index].add(otherDecks?.get(index)?.draw1() ?: Card())
                    } else {
                        handTemp[index].add(han[i])
                    }
                }
                handEvalsTemp.add(Evaluate.analyzeHand(handTemp[index]))
            }
            hands.postValue(handTemp)
            handEvals.postValue(handEvalsTemp)
            lastEvaluatedHand.postValue(handEvalsTemp[0])
            cardFLipState.postValue(CardFlipState.FULL_FLIP)

            if (calculatePayout(handEvalsTemp) <= 0) {
                collect(handEvalsTemp, handTemp)
                gameState.postValue(GameState.EVALUATE_NO_BONUS)
            } else {
                gameState.postValue(GameState.EVALUATE_WITH_BONUS)
            }
        }
    }

    fun getKeptCardIndices() : BooleanArray {
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
            hands.value?.let {
                val timeInMillis = measureTimeMillis {
                    Timber.d("bet = ${(bet.value ?: DEFAULT_BET) * (numberOfHands.value ?: 1)}")
                    aiDecision.postValue(AIPlayer.calculateBestHands((bet.value ?: DEFAULT_BET) ,it[0], numTrials, (numberOfHands.value ?: SettingsUtils.getNumHands())))
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