package com.poker.jacksorbetter.leaderboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.SignInButton
import com.poker.jacksorbetter.R
import timber.log.Timber


class HighScoreFragment : Fragment(), HighScoreTapped {


    private lateinit var highScoreViewModel: HighScoreViewModel
    private lateinit var signInViewModel: SignInViewModel
    private var highscoreDate = mutableListOf<Pair<String, Long>>()
    private var mAdapter: HighScoreAdapter? = null

    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var highscoreText: TextView
    private lateinit var signinLayout: LinearLayout

    companion object {

        val NAME = HighScoreFragment::class.java.simpleName

        @JvmStatic
        fun newInstance() = HighScoreFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view = inflater.inflate(R.layout.fragment_highscore, container, false)
        progressBar = view.findViewById(R.id.progressBar)
        highscoreText = view.findViewById(R.id.noHighScore)
        signinLayout = view.findViewById(R.id.sign_in_button_layout)

        mAdapter = HighScoreAdapter(highscoreDate, this)
        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        with(recyclerView){
            adapter = mAdapter
        }
        recyclerView.setOnClickListener {
            highScoreViewModel.showHighScoreLeaderboard(requireActivity())
        }

        val refreshIcon = view.findViewById<ImageView>(R.id.refresh)
        refreshIcon.setOnClickListener {
            showProgressBar()
            highScoreViewModel.uiState.value = HighScoreViewModel.STATE.LOADING
            if(signInViewModel.isSignedIn()) {
                highScoreViewModel.getTopFiveHighScores(requireActivity())
            }else{
                highScoreViewModel.uiState.value = HighScoreViewModel.STATE.SIGN_IN
            }
        }

        val signIn = view.findViewById<SignInButton>(R.id.sign_in_button)
        signIn.setSize(SignInButton.SIZE_ICON_ONLY)
        signIn.setOnClickListener {
            signInViewModel.startSignInIntent(requireActivity(), 8008)
        }


        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        highScoreViewModel = ViewModelProvider(requireActivity()).get(HighScoreViewModel::class.java)
        signInViewModel = ViewModelProvider(requireActivity()).get(SignInViewModel::class.java)

        if(!signInViewModel.isSignedIn()){
            showSignIn()
            Timber.d("HighScoreFragment Signed In")
        } else {
            highScoreViewModel.getTopFiveHighScores(requireActivity())
        }

        highScoreViewModel.highScores.observe(viewLifecycleOwner, Observer { highScores ->
            Timber.d("High scores: $highScores")
            highscoreDate.clear()
            highscoreDate.addAll(highScores)
            mAdapter?.notifyDataSetChanged()

            progressBar.visibility = View.GONE
            if(!signInViewModel.isSignedIn()) {
                highScoreViewModel.uiState.value = HighScoreViewModel.STATE.SIGN_IN
            }else if(highScores.isNotEmpty()){
                highScoreViewModel.uiState.value = HighScoreViewModel.STATE.SHOW_HIGHSCORE
            } else {
                highScoreViewModel.uiState.value = HighScoreViewModel.STATE.SHOW_NO_RESULTS
            }
        })

        highScoreViewModel.uiState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                HighScoreViewModel.STATE.SIGN_IN -> {
                    showSignIn()
                }
                HighScoreViewModel.STATE.LOADING -> {
                    showProgressBar()
                }
                HighScoreViewModel.STATE.SHOW_HIGHSCORE -> {
                    showHighScore()
                }
                HighScoreViewModel.STATE.SHOW_NO_RESULTS -> {
                    showNoResults()
                }
            }
        })

        highScoreViewModel.goldenScore.observe(viewLifecycleOwner, Observer { goldScore ->
            Timber.d("Golden score: ${goldScore}")
        })
    }

    override fun onTapped() {
        highScoreViewModel.showHighScoreLeaderboard(requireActivity())
    }


    fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
        recyclerView.visibility = View.INVISIBLE
        highscoreText.visibility = View.INVISIBLE
        signinLayout.visibility = View.INVISIBLE
    }

    fun showHighScore() {
        progressBar.visibility = View.INVISIBLE
        recyclerView.visibility = View.VISIBLE
        highscoreText.visibility = View.INVISIBLE
        signinLayout.visibility = View.INVISIBLE
    }

    fun showNoResults() {
        progressBar.visibility = View.INVISIBLE
        recyclerView.visibility = View.INVISIBLE
        highscoreText.visibility = View.VISIBLE
        signinLayout.visibility = View.INVISIBLE
    }

    fun showSignIn() {
        progressBar.visibility = View.INVISIBLE
        recyclerView.visibility = View.INVISIBLE
        highscoreText.visibility = View.INVISIBLE
        signinLayout.visibility = View.VISIBLE
    }
}