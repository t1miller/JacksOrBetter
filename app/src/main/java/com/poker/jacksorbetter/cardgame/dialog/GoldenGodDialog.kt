package com.poker.jacksorbetter.cardgame.dialog

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.facebook.shimmer.ShimmerFrameLayout
import com.poker.jacksorbetter.R


object GoldenGodDialog {


    fun showDialog(context: Context, money: Int) {
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.golden_god_dialog_layout)


        val container = dialog.findViewById(R.id.shimmer_view_container) as ShimmerFrameLayout
        container.baseAlpha = 0.8F // opacity of non shimmer part of image
        container.intensity = 0.1F
        container.startShimmerAnimation() // If auto-start is set to false


        val dismiss = dialog.findViewById<Button>(R.id.dismiss)
        dismiss.setOnClickListener {
            dialog.dismiss()
        }

        val moneyText = dialog.findViewById<TextView>(R.id.highscore)
        moneyText.text = java.text.NumberFormat.getCurrencyInstance().format(money)
        dialog.show()
    }
}