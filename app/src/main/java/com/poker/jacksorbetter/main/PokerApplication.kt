package com.poker.jacksorbetter.main

import android.app.Application
import android.content.Context
import com.poker.jacksorbetter.BuildConfig
import timber.log.Timber

import timber.log.Timber.DebugTree




class PokerApplication : Application() {

    init {
        instance = this
    }

    companion object {
        private var instance: PokerApplication? = null

        fun applicationContext() : Context {
            return instance!!.applicationContext
        }
    }


    override fun onCreate() {
        super.onCreate()

        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

}