package com.poker.jacksorbetter.leaderboard

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.games.Games
import com.google.android.gms.games.leaderboard.LeaderboardVariant
import com.poker.jacksorbetter.R
import timber.log.Timber
import java.util.*


class HighScoreViewModel(application: Application) : AndroidViewModel(application) {


    var highScores : MutableLiveData<List<Pair<String, Long>>> = MutableLiveData()
    var goldenScore : MutableLiveData<Pair<Long, Long>> = MutableLiveData()
    var uiState: MutableLiveData<STATE> = MutableLiveData(STATE.SIGN_IN)


    enum class STATE {
        SIGN_IN,
        LOADING,
        SHOW_HIGHSCORE,
        SHOW_NO_RESULTS
    }


    fun getTopFiveHighScores(activity: Activity) {
        GoogleSignIn.getLastSignedInAccount(activity)?.let {
            Games.getLeaderboardsClient(activity, it)
                .loadTopScores(
                        activity.getString(R.string.highscore_id),
                        LeaderboardVariant.TIME_SPAN_ALL_TIME,
                        LeaderboardVariant.COLLECTION_PUBLIC,
                        5,
                        true
                ).addOnCompleteListener{ task ->
                    if(task.isSuccessful){
                        highScores.value = task.result.get()?.scores?.toList()?.map { Pair(
                                it.scoreHolderDisplayName,
                                it.rawScore
                        ) }

                        if(highScores.value?.size ?: 0 > 0){
                            goldenScore.value = Pair(highScores.value?.get(0)?.second ?: 0, dayOfYear())
                        } else if (highScores.value?.size ?: 0 == 0) {
                            // no scores yet, your score is a highscore
                            goldenScore.value = Pair(0, dayOfYear())
                        }
                    }

                    // todo
                }
        }
    }

     fun submitHighScore(context: Context, score: Long) {
        Timber.d("submitting highscore: score: $score")
         GoogleSignIn.getLastSignedInAccount(context)?.let {
             Games.getLeaderboardsClient(context, it).submitScore(context.getString(R.string.highscore_id), score)
         }
    }

    fun showHighScoreLeaderboard(activity: Activity) {
        GoogleSignIn.getLastSignedInAccount(activity)?.let {
            Games.getLeaderboardsClient(activity, it)
                .getLeaderboardIntent(activity.getString(R.string.highscore_id))
                .addOnFailureListener {
                    Timber.e("error loading leaderboard $it")
                }
                .addOnSuccessListener { intent ->
                    Timber.d("showing leaderboard")
                    ActivityCompat.startActivityForResult(
                            activity,
                            intent,
                            8008,
                            Bundle()
                    )
                }
        }
    }

    private fun isGoldenGodExpired() : Boolean{
        Timber.d("isGoldenGodExpired() first:${goldenScore.value?.first} second: ${goldenScore.value?.second}")
        if(goldenScore.value == null
                || goldenScore.value?.first == 0L
                || goldenScore.value?.second == 0L
                || dayOfYear() > goldenScore.value?.second ?: 0){
            // uninitialized or stale
            Timber.d("God is expired")
            return true
        }
        Timber.d("God is fresh")
        return false
    }

    private fun dayOfYear(): Long{
        val cal = Calendar.getInstance(TimeZone.getTimeZone("Europe/Paris"))
        cal.time = Date(System.currentTimeMillis())
        val day = cal[Calendar.DAY_OF_YEAR]
        return day.toLong()
    }

    fun isGoldenGodScore(money: Int) : Boolean {
        // todo congratulations you are a golden gold
        //      you have the current all time highscore ()
        //      lookout for more collectable cards
        Timber.d("God score: ${goldenScore.value?.first}")
        return !isGoldenGodExpired() && money > goldenScore.value?.first ?: 0
    }
}