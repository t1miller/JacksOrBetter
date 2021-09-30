package com.poker.jacksorbetter.handstatui

import Card

data class HandStat(val recommendedHand: List<Card>,
                    val fullHand: List<Card>,
                    val expectedValue: Double)