package com.poker.jacksorbetter.main

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poker.jacksorbetter.cardgame.Card
import com.poker.jacksorbetter.cardgame.Deck
import com.poker.jacksorbetter.cardgame.Deck2
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

    val numberOfHands: MutableLiveData<Int> by lazy {
        MutableLiveData(1)
    }

    var otherDecks: MutableList<Deck2>? = null

    val bonusHand: MutableLiveData<MutableList<Card>> = MutableLiveData()

    val hands: MutableLiveData<MutableList<MutableList<Card>>> = MutableLiveData()

    val handEvals: MutableLiveData<MutableList<Evaluate.Hand>> = MutableLiveData(mutableListOf())

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

    val cardsHeld: MutableLiveData<MutableList<Card>> by lazy {
        MutableLiveData(mutableListOf<Card>())
    }

    private var cardsKept: BooleanArray = listOf(false, false, false, false, false).toBooleanArray()
    private var lastCardsKept: List<Card>? = null
    private var originalHand: List<Card>? = null


    private fun updateTotalMoney(newAmount: Int, isBonus: Boolean) {

        SettingsUtils.setMoney(newAmount, getApplication())
        val oldAmount = getMoney()
        if(newAmount > oldAmount) {
            if (isBonus && SettingsUtils.isBonusSoundEnabled(getApplication())) {
                SoundManager.playSound(getApplication(), SoundManager.SoundType.ROOSTER_CROWING)
            } else {
                SoundManager.playSound(getApplication(), SoundManager.SoundType.COLLECTING_COINS)
            }
        }
        totalMoney.value = newAmount
    }

    private fun updateLastEvaluatedHand(handEval: Evaluate.Hand?) {
        lastEvaluatedHand.value = handEval
    }

    private fun getMoney() : Int {
        return totalMoney.value ?: SettingsUtils.getMoney(getApplication())
    }

    fun incrementHands() {
        val currentHands = numberOfHands.value ?: 1
        if(currentHands < 10 || SettingsUtils.isCrazyMode(getApplication())){
            numberOfHands.value = currentHands + 1
        } else {
            Toast.makeText(getApplication(), "Maximum: 10 hands", Toast.LENGTH_LONG).show()
        }
        Timber.d("incrementHands() hand ${hands.value}")
    }

    fun decreaseHands() {
        val currentHands = numberOfHands.value ?: 1
        if(currentHands > 1){
            numberOfHands.value = currentHands - 1
        } else {
            Toast.makeText(getApplication(), "Minimum: 1 hand", Toast.LENGTH_LONG).show()
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

    fun newGame() {
        val deck = Deck2(null)
        val hand = deck.draw5()
        hands.value = MutableList(numberOfHands.value?:1){ hand }
        otherDecks = MutableList(numberOfHands.value?:1){ Deck2(deck.getCards()) }
        otherDecks?.forEach { it.shuffle() }
        gameState.value = GameState.DEAL
        cardsKept = listOf(false, false, false, false, false).toBooleanArray()
    }

    fun collect() {
        val payout = calculatePayout()
        Timber.d("payout $payout")
        val toteMoney = getMoney() + payout
        wonLostMoney.value = payout
        updateTotalMoney(toteMoney, false)

        // done w/ game get statistics for game (NON BONUS flow)
        StatisticsManager.addStatistic(Game(bet.value, payout, handEvals.value?.get(0)?.readableName, originalHand, lastCardsKept, hands.value?.get(0)))
    }

    fun calculatePayout() : Int{
        var payout = 0
        hands.value?.forEachIndexed { index, _ ->
            payout += PayOutHelper.calculatePayout(getApplication(), bet.value, handEvals.value?.get(index))
        }
        return payout
    }
    /**
     *  C[2] is the index of the card to guess
     */
    fun collectBonus(isGuessRed: Boolean) {
        Deck.newDeck()
        bonusHand.value = Deck.draw5()

        val payout = calculatePayout()
        val totalPayout = PayOutHelper.calculateBonusPayout(payout, hands.value?.get(0)?.get(2), isGuessRed)
        val toteMoney = getMoney() + totalPayout
        wonLostMoney.value = totalPayout
        updateTotalMoney(toteMoney,true)

        // done w/ game get statistics for game (BONUS flow)
        StatisticsManager.addStatistic(Game(bet.value, totalPayout, handEvals.value?.get(0)?.readableName, originalHand, lastCardsKept, hands.value?.get(0)))
    }

    fun evaluateHand(cardsToKeep: BooleanArray, cards: List<Card>) {

        cardsKept = cardsToKeep
        lastCardsKept = cards
        originalHand = hands.value?.get(0)?.toList()
        val handEvalsTemp = mutableListOf<Evaluate.Hand>()
        val handTemp = MutableList(numberOfHands.value ?: 1){ mutableListOf<Card>()}

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
        hands.value = handTemp
        handEvals.value = handEvalsTemp
        cardFLipState.value = CardFlipState.FULL_FLIP


        Timber.d("hand evals ${handEvals.value}")
        Timber.d("hands ${hands.value}")

        updateLastEvaluatedHand(handEvals.value?.get(0))

        if (calculatePayout() <= 0) {
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
            hands.value?.let {
                val timeInMillis = measureTimeMillis {
                    Timber.d("bet = ${(bet.value ?: DEFAULT_BET) * (numberOfHands.value ?: 1)}")
                    aiDecision.postValue(AIPlayer.calculateBestHands(getApplication(), (bet.value ?: DEFAULT_BET) ,it[0], numTrials, (numberOfHands.value ?: 1)))
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