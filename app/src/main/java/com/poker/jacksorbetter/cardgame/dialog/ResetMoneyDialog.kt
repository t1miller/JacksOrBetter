package com.poker.jacksorbetter.cardgame.dialog

import android.app.Dialog
import android.content.Context
import android.view.Gravity
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import com.poker.jacksorbetter.R

object ResetMoneyDialog {

    enum class AMOUNTS(var value: Int) {
        ONE(200),
        TWO(500),
        THREE(1000)
    }

    interface MoneyButton{
        fun setMoney(amount: Int)
    }

    fun showDialog(context: Context, callbackMoney: MoneyButton) {
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.reset_money_dialog_layout)

        val cancel = dialog.findViewById<ImageView>(R.id.cancel)
        cancel.setOnClickListener {
            dialog.dismiss()
        }

        val button1 = dialog.findViewById<Button>(R.id.button1)
        button1.setOnClickListener {
            callbackMoney.setMoney(AMOUNTS.ONE.value)
            dialog.dismiss()
        }
        button1.text = "$${AMOUNTS.ONE.value}"

        val button2 = dialog.findViewById<Button>(R.id.button2)
        button2.setOnClickListener {
            callbackMoney.setMoney(AMOUNTS.TWO.value)
            dialog.dismiss()
        }
        button2.text = "$${AMOUNTS.TWO.value}"


        val button3 = dialog.findViewById<Button>(R.id.button3)
        button3.setOnClickListener {
            callbackMoney.setMoney(AMOUNTS.THREE.value)
            dialog.dismiss()
        }
        button3.text = "$${AMOUNTS.THREE.value}"

        dialog.show()
        dialog.window?.setGravity(Gravity.TOP)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
    }
}