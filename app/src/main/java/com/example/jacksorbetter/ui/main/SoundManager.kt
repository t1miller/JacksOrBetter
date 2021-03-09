package com.example.jacksorbetter.ui.main

import android.content.Context
import android.media.SoundPool
import com.example.jacksorbetter.R
import com.example.jacksorbetter.cardgame.Evaluate
import com.example.jacksorbetter.settings.SettingsUtils


object SoundManager {

    private const val DEFAULT_ID = 1

    private var soundPool: SoundPool? = null

    enum class SoundType {
        SHUFFLE,
        SHUFFLE_SHEEP,
        FLIP,
        INSERT_COIN,
        INSERT_COIN_SHEEP,
        COLLECTING_COINS,
        COLLECTING_COINS_SHEEP,
        BIG_WIN,
        BIG_WIN_SHEEP,
        MEDIUM_WIN,
        MEDIUM_WIN_SHEEP,
        CHIME
    }

    private val soundTypeToId = mutableMapOf<SoundType,Int>()

    fun load(context: Context) {
        soundPool = SoundPool.Builder().build()
//        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundTypeToId[SoundType.SHUFFLE] = soundPool?.load(context, R.raw.shuffling,1) ?: DEFAULT_ID
        soundTypeToId[SoundType.SHUFFLE_SHEEP] = soundPool?.load(context, R.raw.shuffle_sheep,1) ?: DEFAULT_ID
        soundTypeToId[SoundType.FLIP] = soundPool?.load(context, R.raw.card_flip,1) ?: DEFAULT_ID
        soundTypeToId[SoundType.INSERT_COIN] = soundPool?.load(context, R.raw.insert_coin,1) ?: DEFAULT_ID
        soundTypeToId[SoundType.INSERT_COIN_SHEEP] = soundPool?.load(context, R.raw.increase_bet_sheep,1) ?: DEFAULT_ID
        soundTypeToId[SoundType.COLLECTING_COINS] = soundPool?.load(context, R.raw.collecting_coins,1) ?: DEFAULT_ID
        soundTypeToId[SoundType.COLLECTING_COINS_SHEEP] = soundPool?.load(context, R.raw.collect_coins_sheep,1) ?: DEFAULT_ID
        soundTypeToId[SoundType.BIG_WIN] = soundPool?.load(context, R.raw.big_win,1) ?: DEFAULT_ID
        soundTypeToId[SoundType.BIG_WIN_SHEEP] = soundPool?.load(context, R.raw.big_win_sheep,1) ?: DEFAULT_ID
        soundTypeToId[SoundType.MEDIUM_WIN] = soundPool?.load(context, R.raw.medium_win,1) ?: DEFAULT_ID
        soundTypeToId[SoundType.MEDIUM_WIN_SHEEP] = soundPool?.load(context, R.raw.medium_win_sheep,1) ?: DEFAULT_ID
        soundTypeToId[SoundType.CHIME] = soundPool?.load(context, R.raw.chime,1) ?: DEFAULT_ID
    }

    fun release() {
        soundPool?.release()
    }

    fun playSound(context: Context, sound: SoundType) {
        if(SettingsUtils.isSoundEnabled(context)) {
            if(soundPool == null){
                load(context)
            }

            var soundTypeT = sound
            if(SettingsUtils.isSheepModeEnabled(context)){
                when(sound) {
                    SoundType.SHUFFLE -> soundTypeT = SoundType.SHUFFLE_SHEEP
                    SoundType.BIG_WIN -> soundTypeT = SoundType.BIG_WIN_SHEEP
                    SoundType.MEDIUM_WIN -> soundTypeT = SoundType.MEDIUM_WIN_SHEEP
                    SoundType.INSERT_COIN -> soundTypeT = SoundType.INSERT_COIN_SHEEP
                    SoundType.COLLECTING_COINS -> soundTypeT = SoundType.COLLECTING_COINS_SHEEP
                    else -> {}
                }
            }

            val soundId = soundTypeToId[soundTypeT] ?: DEFAULT_ID

            soundPool?.play(soundId, 1F, 1F, 0, 0, 1F)
        }
    }

    fun playSound(context: Context, eval: Evaluate.Hand) {
        when(eval) {
            Evaluate.Hand.ROYAL_FLUSH,
            Evaluate.Hand.STRAIGHT_FLUSH,
            Evaluate.Hand.FOUR_OF_A_KIND,
            Evaluate.Hand.FULL_HOUSE,
            Evaluate.Hand.FLUSH,
            Evaluate.Hand.STRAIGHT -> {
                return playSound(context, SoundType.BIG_WIN)
            }
            Evaluate.Hand.THREE_OF_A_KIND,
            Evaluate.Hand.TWO_PAIRS,
            Evaluate.Hand.JACKS_OR_BETTER -> {
                return playSound(context, SoundType.MEDIUM_WIN)
            }
            Evaluate.Hand.NOTHING -> {/*no sound*/}
        }
    }
}