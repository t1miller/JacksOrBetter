package com.poker.jacksorbetter.cardgame.dialog

import android.view.View
import com.google.android.material.snackbar.Snackbar

object SnackBarUtils {

    fun View.snack(message: String, duration: Int = Snackbar.LENGTH_SHORT) {
        Snackbar.make(this, message, duration).show()
    }
}