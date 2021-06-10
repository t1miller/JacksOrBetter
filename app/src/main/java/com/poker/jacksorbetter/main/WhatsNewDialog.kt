package com.poker.jacksorbetter.main

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.poker.jacksorbetter.BuildConfig
import com.poker.jacksorbetter.R


object WhatsNewDialog {


    fun showDialog(context: Context) {
        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.whats_new_layout)

        val dismiss = dialog.findViewById<Button>(R.id.dismiss)
        dismiss.setOnClickListener {
            dialog.dismiss()
        }

        val versionCode = BuildConfig.VERSION_CODE
        val titleText = dialog.findViewById<TextView>(R.id.subTitle)
        titleText.text = context.getString(R.string.whats_new_title, versionCode.toString())

        dialog.show()
    }
}