package com.poker.jacksorbetter.handstatui

import com.poker.jacksorbetter.cardgame.Card

data class HandStat(val recommendedHand: List<Card>, val fullHand: List<Card>, val expectedValue: Double)