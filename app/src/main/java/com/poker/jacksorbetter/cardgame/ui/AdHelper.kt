package com.poker.jacksorbetter.cardgame.ui

import android.app.Activity
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.widget.FrameLayout
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.poker.jacksorbetter.R

object AdHelper {

    fun setupAd(activity: Activity, rootView: View, adUnitId: String) {
        MobileAds.initialize(activity)
        val adContainer = rootView.findViewById<FrameLayout>(R.id.ad_view_container)

        val adView = AdView(activity)
        adView.adUnitId = adUnitId
        adContainer.addView(adView)

        val adRequest = AdRequest.Builder().build()
        val adSize = getAdSize(activity)

        adView.adSize = adSize
        adView.loadAd(adRequest)
    }

    private fun getAdSize(activity: Activity): AdSize {
        val display: Display = activity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)

        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density

        val adWidth = (widthPixels / density).toInt()
        return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
    }
}