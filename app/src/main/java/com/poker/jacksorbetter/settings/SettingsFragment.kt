package com.poker.jacksorbetter.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.poker.jacksorbetter.R
import com.poker.jacksorbetter.cardgame.dialog.ResetMoneyDialog
import com.poker.jacksorbetter.leaderboard.SignInViewModel
import com.poker.jacksorbetter.stats.StatisticsManager


class SettingsFragment : PreferenceFragmentCompat() , ResetMoneyDialog.MoneyButton{

    companion object {
        val NAME: String = SettingsFragment::class.java.simpleName

        fun newInstance() = SettingsFragment()
    }

    private var viewModelSignIn: SignInViewModel? = null

    private var signin: Preference? = null
    private var signout: Preference? = null


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val resetMoneyButton: Preference? = findPreference(SettingsUtils.Keys.RESET_MONEY)
        resetMoneyButton?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            ResetMoneyDialog.showDialog(requireContext(), this)
            true
        }

        val resetStatsButton: Preference? = findPreference(SettingsUtils.Keys.RESET_STATS)
        resetStatsButton?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            StatisticsManager.deleteStatisticsOnDisk()
            Toast.makeText(requireContext(),getString(R.string.reset_stats), Toast.LENGTH_LONG).show()
            true
        }

        val shareStats: Preference? = findPreference(SettingsUtils.Keys.SHARE_STATS)
        shareStats?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            StatisticsManager.shareStatistics(requireContext())
            Toast.makeText(requireContext(),getString(R.string.share_stats), Toast.LENGTH_LONG).show()
            true
        }

        val cardBack: Preference? = findPreference(SettingsUtils.Keys.CHOOSE_CARD_BACK)
        cardBack?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            SettingsUtils.showChangeCardBackDialog(requireContext())
            Toast.makeText(requireContext(),getString(R.string.cardback_changed), Toast.LENGTH_LONG).show()
            true
        }

        signin = findPreference(SettingsUtils.Keys.SIGN_IN)
        signin?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            viewModelSignIn?.startSignInIntent(requireActivity(), 8008)
            showSignOutButton()
            true
        }

        signout = findPreference(SettingsUtils.Keys.SIGN_OUT)
        signout?.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            viewModelSignIn?.signOut(requireActivity())
            showSignInButton()
            true
        }

        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModelSignIn = ViewModelProvider(this).get(SignInViewModel::class.java)

        if(viewModelSignIn?.isSignedIn() == true){
            showSignOutButton()
        } else {
//            // todo use Google's fancy sign in button
            showSignInButton()
        }
    }

    override fun setMoney(amount: Int) {
        SettingsUtils.setMoney(amount)
        Toast.makeText(requireContext(), getString(R.string.money_set, amount), Toast.LENGTH_LONG).show()
    }

    private fun showSignInButton() {
        signin?.isVisible = true
        signout?.isVisible = false
        signin?.summary = getString(R.string.signed_out_status)
    }

    private fun showSignOutButton() {
        signin?.isVisible = false
        signout?.isVisible = true
        signout?.summary = getString(R.string.signed_in_status)
    }
}