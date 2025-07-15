package com.example.gimnasio.admob

import android.content.Context
import androidx.compose.runtime.*
import androidx.core.app.ComponentActivity
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback

class InterstitialAdManager(private val context: Context) {
//    var interstitialAd by mutableStateOf<InterstitialAd?>(null)
//
//    fun loadInterstitial(adUnitId: String) {
//        InterstitialAd.load(
//            context,
//            adUnitId,
//            AdRequest.Builder().build(),
//            object : InterstitialAdLoadCallback() {
//                override fun onAdLoaded(ad: InterstitialAd) {
//                    interstitialAd = ad
//                }
//
//                override fun onAdFailedToLoad(adError: LoadAdError) {
//                    interstitialAd = null
//                }
//            }
//        )
//    }
//
//    fun showInterstitial() {
//        interstitialAd?.show(context as ComponentActivity)
//        interstitialAd = null // Limpiar después de mostrar
//    }
}