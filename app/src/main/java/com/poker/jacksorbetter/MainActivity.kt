package com.poker.jacksorbetter


import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.poker.jacksorbetter.settings.SettingsFragment
import com.poker.jacksorbetter.simulator.SimulatorFragment
import com.poker.jacksorbetter.stats.StatisticsManager
import com.poker.jacksorbetter.stats.StatsFragment
import com.poker.jacksorbetter.main.MainFragment
import com.google.android.gms.ads.MobileAds

class MainActivity : AppCompatActivity() {

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

        R.id.stats -> {
            loadStatFragment()
            true
        }

        R.id.settings -> {
            supportFragmentManager.beginTransaction().replace(R.id.container, SettingsFragment(), SettingsFragment.NAME).commitNow()
            true
        }
        android.R.id.home -> {
            if (isSettingsVisible() || isSimulatorVisible() || isStatsVisible()){
                loadMainFragment()
            } 
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

//    private fun loadSettingsFragment() {
//        supportFragmentManager.beginTransaction()
//            .replace(R.id.container, MainFragment.newInstance())
//            .commitNow()
//    }

    private fun loadStatFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.container, StatsFragment.newInstance(), StatsFragment.NAME)
            .commitNow()
    }

    private fun isStatsVisible() : Boolean{
        val frag: StatsFragment? =
            supportFragmentManager.findFragmentByTag(StatsFragment.NAME) as StatsFragment?
        return (frag != null && frag.isVisible)
    }

    private fun isSettingsVisible() : Boolean{
        val frag: SettingsFragment? =
            supportFragmentManager.findFragmentByTag(SettingsFragment.NAME) as SettingsFragment?
        return (frag != null && frag.isVisible)
    }

    private fun isSimulatorVisible() : Boolean{
        val frag: SimulatorFragment? =
            supportFragmentManager.findFragmentByTag(SimulatorFragment.NAME) as SimulatorFragment?
        return (frag != null && frag.isVisible)
    }
}