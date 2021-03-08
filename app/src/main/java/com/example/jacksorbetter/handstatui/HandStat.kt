package com.example.jacksorbetter.handstatui

import com.example.jacksorbetter.cardgame.Card

data class HandStat(val recommendedHand: List<Card>, val fullHand: List<Card>, val expectedValue: Double)