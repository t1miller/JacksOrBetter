package com.poker.jacksorbetter.main


import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.poker.jacksorbetter.settings.SettingsFragment
import com.poker.jacksorbetter.simulator.SimulatorFragment
import com.poker.jacksorbetter.stats.StatisticsManager
import com.poker.jacksorbetter.stats.StatsFragment
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.leaderboard.SignInViewModel
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 8008
    private val RC_LEADERBOARD_UI = 9004

    private lateinit var viewModel: SignInViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.main_activity)
        MobileAds.initialize(this) {}

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, MainFragment.newInstance())
                    .commitNow()
        }
        viewModel = ViewModelProvider(this).get(SignInViewModel::class.java)
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.isSignedIn()) {
            viewModel.state.value = SignInViewModel.SignInState.SIGNED_OUT
            Timber.d("onResume(): Not signed in")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)
        } else if (requestCode == RC_LEADERBOARD_UI) {
        }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account = completedTask.getResult(ApiException::class.java)
            viewModel.googleAccount.value = account
            viewModel.state.value = SignInViewModel.SignInState.SIGNED_IN
            Timber.d("sign in: $account")
            Toast.makeText(applicationContext, "Sign in: Success", Toast.LENGTH_LONG).show()
        } catch (e: ApiException) {
            Timber.d("handleSignInResult() error ${e.statusCode}")
//            Toast.makeText(applicationContext, "Sign in: Error", Toast.LENGTH_LONG).show()
        }
    }

    override fun onStart() {
        // save pending statistics (won, loss, etc) to disk
        StatisticsManager.readStatisticsFromDisk()
        super.onStart()
    }
    override fun onPause() {
        // save pending statistics (won, loss, etc) to disk
        StatisticsManager.writeStatisticsToDisk()
        super.onPause()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_items, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.simulations -> {
            supportFragmentManager.beginTransaction().replace(R.id.container, SimulatorFragment(), SimulatorFragment.NAME).commitNow()
            true
        }

        R.id.about -> {
            supportFragmentManager.beginTransaction().replace(R.id.container, AboutFragment.newInstance(), AboutFragment.NAME).commitNow()
            true
        }

        R.id.stats -> {
            loadStatFragment()
            true
        }

        R.id.settings -> {
            supportFragmentManager.beginTransaction().replace(R.id.container, SettingsFragment(), SettingsFragment.NAME).commitNow()
            true
        }
        android.R.id.home -> {
//            if (isSettingsVisible() || isSimulatorVisible() || isStatsVisible()){
//                loadMainFragment()
//            }
            loadMainFragment()
            true
        }

        else -> {
            super.onOptionsItemSelected(item)
        }
    }

    private fun loadMainFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, MainFragment.newInstance())
            .commitNow()
    }


    private fun loadStatFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, StatsFragment.newInstance(), StatsFragment.NAME)
            .commitNow()
    }

}