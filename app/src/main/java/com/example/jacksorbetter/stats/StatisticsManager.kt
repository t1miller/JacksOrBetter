package com.example.jacksorbetter.stats

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import com.example.jacksorbetter.PokerApplication
import com.example.jacksorbetter.cardgame.Card
import com.example.jacksorbetter.cardgame.Evaluate
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import timber.log.Timber
import java.io.File


object StatisticsManager {
    private const val filename = "/Statistics.txt"

    private val fullFileName = PokerApplication.applicationContext().filesDir.absolutePath + filename

    private var statistics: Statistics? = null
    private val INITIALIZE = Statistics(
        0,
        0,
        mutableListOf(
        EvalCount(Evaluate.Hand.ROYAL_FLUSH.readableName,0),
        EvalCount(Evaluate.Hand.STRAIGHT_FLUSH.readableName,0),
        EvalCount(Evaluate.Hand.FOUR_OF_A_KIND.readableName,0),
        EvalCount(Evaluate.Hand.FULL_HOUSE.readableName,0),
        EvalCount(Evaluate.Hand.FLUSH.readableName,0),
        EvalCount(Evaluate.Hand.STRAIGHT.readableName,0),
        EvalCount(Evaluate.Hand.THREE_OF_A_KIND.readableName,0),
        EvalCount(Evaluate.Hand.TWO_PAIRS.readableName,0),
        EvalCount(Evaluate.Hand.JACKS_OR_BETTER.readableName,0),
        EvalCount(Evaluate.Hand.NOTHING.readableName,0)
        ),
        mutableListOf(),
        LastGame(0,0, null, null, null, null)
        )

//    init {
//        loadStatisticsFromDisk()
//    }

    fun readStatisticsFromDisk() {
        statistics = fromFile()
    }

    fun writeStatisticsToDisk() {
        statistics?.toFile()
    }

    fun deleteStatisticsOnDisk() {
        deleteFile()
        statistics = INITIALIZE
    }

    fun addStatistic(lastGame: LastGame?) {
        updateLastGame(lastGame)

        // todo this is way too much writing to disk
        writeStatisticsToDisk()
    }


    private fun updateLastGame(lastGame: LastGame?) {
        if(lastGame == null) return
        if(lastGame.bet == null) return
        if(lastGame.won == null) return
        if(lastGame.eval == null) return
        if(lastGame.handOriginal == null) return
        if(lastGame.heldCards == null) return
        if(lastGame.handFinal == null) return

        statistics?.let { stat ->
            stat.lastGame = lastGame
            stat.hands.add(lastGame.handFinal)

            if(lastGame.won > 0){
                stat.totalWon = stat.totalWon + lastGame.won
            } else {
                stat.totalLost = stat.totalLost + lastGame.won
            }

            stat.evalCounts.forEach {
                if(it.evalType == lastGame.eval) {
                    it.count = it.count + 1
                }
            }
            Timber.d("Updating statistic:\ntotal won: ${stat.totalWon}\ntotal lost: ${stat.totalLost}\nhands: ${stat.hands}\neval counts: ${stat.evalCounts}")
        }
    }

    fun shareStatistics(context: Context) {
        writeStatisticsToDisk() //todo hopefully finishes writing before send :(

        val statURI = FileProvider.getUriForFile(
            context,
            context.applicationContext.packageName.toString() + ".provider",
            File(fullFileName)
        )

        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/*"
        sharingIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        sharingIntent.putExtra(Intent.EXTRA_STREAM, statURI)
        context.startActivity(Intent.createChooser(sharingIntent, "share file with"))
    }

    fun getStatistics() : Statistics?{
        return statistics
    }

    private fun Statistics.toJSON() : String{
        val gson = GsonBuilder().setPrettyPrinting().create()
        return gson.toJson(this)  // json string
    }

    private fun String.toStatistics() : Statistics{
//        val gson = GsonBuilder().setPrettyPrinting().create()
        return Gson().fromJson<Statistics>(this, Statistics::class.java)
    }

    private fun Statistics.toFile() {
        try {
            File(fullFileName).printWriter().use { out ->
                out.write(this.toJSON())
            }
        } catch (e: java.lang.Exception) {
            Timber.e("Problem writing statistic file $fullFileName")
        }

    }

    private fun deleteFile() {
        try {
            File(fullFileName).delete()
        } catch (e: Exception) {
            Timber.e("Problem deleting statistic file $fullFileName")
        }
    }

    private fun fromFile() : Statistics? {
        val rawJson: String
        try {
            rawJson = File(fullFileName).readText()
        } catch (e: java.lang.Exception) {
            Timber.e("Problem loading statistic file $fullFileName")
            return INITIALIZE
        }
        return rawJson.toStatistics()
    }
}

data class Statistics(
    var totalWon: Int,
    var totalLost: Int,
    val evalCounts: MutableList<EvalCount>,
    var hands: MutableList<List<Card>>,
    var lastGame: LastGame?

)

data class EvalCount(
    val evalType: String,
    var count: Int
)

data class LastGame(
    val bet: Int?,
    val won: Int?,
    val eval: String?,
    val handOriginal: List<Card>?,
    val heldCards: List<Card>?,
    val handFinal: List<Card>?
)

