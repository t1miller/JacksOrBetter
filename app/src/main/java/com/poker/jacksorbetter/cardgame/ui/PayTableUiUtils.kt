package com.poker.jacksorbetter.cardgame.ui

import android.animation.ValueAnimator
import android.content.Context
import android.view.animation.AnimationUtils
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.core.view.children
import com.poker.jacksorbetter.R


object PayTableUiUtils {


//    fun initBlinkAnimation(context: Context, tableLayout: TableLayout) {
//        val animBlink = AnimationUtils.loadAnimation(context, R.anim.blink)
//        for(row in tableLayout.children){
//            for (rowElement in (row as TableRow).children) {
//                val rowTextView = rowElement as TextView
//                rowTextView.animation = animBlink
//            }
//        }
//    }

    fun animateWinningTextViewFont(
        context: Context,
        tableLayout: TableLayout,
        rowIndex: Int?,
        bet: Int = 1
    ) {
        if(rowIndex == null || rowIndex < 0 || rowIndex > tableLayout.children.count()){
            return
        }

        val startSize = 42f // Size in pixels
        val endSize = 12f
        val animationDuration: Long = 600 // Animation duration in ms
        val animator = ValueAnimator.ofFloat(startSize, endSize)
        animator.duration = animationDuration
        val winTV = (tableLayout.children.toList()[rowIndex] as TableRow).children.toList()[bet] as TextView

        animator.addUpdateListener { valueAnimator ->
            val animatedValue = valueAnimator.animatedValue as Float
            winTV.textSize = animatedValue
        }
        animator.start()
    }

    fun blinkRow(context: Context, tableLayout: TableLayout, rowIndex: Int?) {
        val animBlink = AnimationUtils.loadAnimation(context, R.anim.blink)
        for((j, row) in tableLayout.children.withIndex()){
            if(j == rowIndex) {
                for ((i, rowElement) in (row as TableRow).children.withIndex()) {
                    val rowTextView = rowElement as TextView
                    rowTextView.animation = animBlink
                }
            }
        }
    }

    fun unblink(tableLayout: TableLayout) {
        for(row in tableLayout.children){
            for (rowElement in (row as TableRow).children) {
                val rowTextView = rowElement as TextView
                rowTextView.clearAnimation()
            }
        }
    }
}