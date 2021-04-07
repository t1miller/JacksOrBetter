package com.poker.threecardpoker.cardgame.dialog

import android.view.View
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

object SnackBarUtils {

//    fun onSNACK(view: View){
//        //Snackbar(view)
//        val snackbar = Snackbar.make(view, "Replace with your own action",
//                Snackbar.LENGTH_LONG).setAction("Action", null)
//        snackbar.setActionTextColor(Color.BLUE)
//        val snackbarView = snackbar.view
//        snackbarView.setBackgroundColor(Color.LTGRAY)
//        val textView =
//                snackbarView.findViewById(com.google.android.material.R.id.snackbar_text) as TextView
//        textView.setTextColor(Color.BLUE)
//        textView.textSize = 28f
//        snackbar.show()
//    }

    fun View.snack(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(this, message, duration).show()
    }
}