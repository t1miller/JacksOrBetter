package com.poker.jacksorbetter.leaderboard

import android.app.Activity
import android.app.Application
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.gms.games.Games
import timber.log.Timber
import java.util.*


class SignInViewModel(application: Application) : AndroidViewModel(application) {

    enum class SignInState {
        SIGNED_IN,
        SIGNED_OUT
    }

    var googleAccount : MutableLiveData<GoogleSignInAccount> = MutableLiveData()
    var state : MutableLiveData<SignInState> = MutableLiveData(SignInState.SIGNED_OUT)

    private var mGoogleSignInClient: GoogleSignInClient? = null
    lateinit var gso: GoogleSignInOptions


    fun startMeUp(activity: Activity) {
        gso  =  GoogleSignInOptions
//            .Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestScopes(Games.SCOPE_GAMES_LITE)
            .build()
        if((mGoogleSignInClient == null)){
            mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
        }
    }

    fun setPopUpView(context: Context, view: View) {
        GoogleSignIn.getLastSignedInAccount(context)?.let {
            val gamesClient = Games.getGamesClient(context, it)
            gamesClient.setViewForPopups(view)
            gamesClient.setGravityForPopups(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
        }
    }

//    fun stopYouDown(activity: Activity) {
////        gso  =  GoogleSignInOptions
////                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
////                .requestScopes(Games.SCOPE_GAMES_LITE)
////                .build();
////        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso)
//        GoogleSignIn.getClient(activity, gso).
//    }


    fun signInSilently(activity: Activity) {
        Timber.d("signInSilently()")
        if(isSignedIn()){
            Timber.d("already signed in")
        } else {
            startMeUp(activity)
            mGoogleSignInClient?.silentSignIn()
            ?.addOnCompleteListener { task ->
                googleAccount.value = task.result
                if (task.isSuccessful) {
                    state.value = SignInState.SIGNED_IN
                    Timber.d("signInSilently(): success")
                } else {
                    state.value = SignInState.SIGNED_OUT
                    Timber.d("signInSilently(): failure ${task.exception}")
                }
            }
        }
    }

    fun isSignedIn() : Boolean {
//        return state.value == SignInState.SIGNED_IN
        // todo think about this
        return GoogleSignIn.getLastSignedInAccount(getApplication()) != null
//        return GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(getApplication()), gso.scopeArray)
    }

    fun startSignInIntent(activity: Activity, resultCode: Int) {
        Timber.d("startSignInIntent()")
        startMeUp(activity)
        activity.startActivityForResult(mGoogleSignInClient?.signInIntent, resultCode)
    }

    fun signOut(activity: Activity) {
        startMeUp(activity)
        mGoogleSignInClient?.signOut()?.addOnCompleteListener { task ->
            googleAccount.value = null
            if (task.isSuccessful) {
                Timber.d("signOut() success")
                Toast.makeText(getApplication(), "Sign out: Success", Toast.LENGTH_LONG).show()
                state.value = SignInState.SIGNED_OUT
            } else {
                Timber.d("signOut() failure ${task.exception}")
                Toast.makeText(getApplication(), "Sign out: Failure", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun checkForUpdates(activity: Activity) {
        // todo incorporate this into signinn flow
        val availability = GoogleApiAvailability.getInstance()
        val resultCode = availability.isGooglePlayServicesAvailable(activity)
        if (resultCode == ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED) {
            availability.getErrorDialog(activity, resultCode, 100)?.show()
        }
    }
}