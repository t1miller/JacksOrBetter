package com.poker.jacksorbetter.main

import androidx.fragment.app.Fragment
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import com.poker.jacksorbetter.R

/**
 * A fragment representing a list of Items.
 */
class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_about, container, false)

        val buttonEmail = view.findViewById<Button>(R.id.emailButton)
        buttonEmail.setOnClickListener {
            sendEmail()
        }

//        val jacks = view.findViewById<ImageView>(R.id.jacks)
//        jacks.setOnClickListener {
//            loadJacksPlaystore()
//        }

        val deuces = view.findViewById<ImageView>(R.id.deuces)
        deuces.setOnClickListener {
            loadDeucesPlaystore()
        }

        val pokerHandBuddy = view.findViewById<ImageView>(R.id.pokerHandBuddy)
        pokerHandBuddy.setOnClickListener {
            loadPokerHandBuddyPlaystore()
        }

        val letItRide = view.findViewById<ImageView>(R.id.letItRide)
        letItRide.setOnClickListener {
            loadLetItRidePlaystore()
        }

        val threeCardPoker = view.findViewById<ImageView>(R.id.threeCardPoker)
        threeCardPoker.setOnClickListener {
            loadThreeCardPokerPlaystore()
        }


        return view
    }

    private fun sendEmail() {
        val intent = Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto", "trentrobison@gmail.com", null)
        )
        intent.putExtra(Intent.EXTRA_SUBJECT, "Wild Idea")
        intent.putExtra(Intent.EXTRA_TEXT, "Hi Trent, I have a fun feature idea.")
        startActivity(Intent.createChooser(intent, "Choose an Email client :"))
    }

    private fun loadJacksPlaystore() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=com.poker.jacksorbetter")
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }

    private fun loadDeucesPlaystore() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=com.poker.deuceswild")
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }

    private fun loadPokerHandBuddyPlaystore() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=com.poker.pokerhandbuddy")
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }

    private fun loadLetItRidePlaystore() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=com.poker.letitride")
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }

    private fun loadThreeCardPokerPlaystore() {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse(
                "https://play.google.com/store/apps/details?id=com.poker.threecardpoker")
            setPackage("com.android.vending")
        }
        startActivity(intent)
    }
}
