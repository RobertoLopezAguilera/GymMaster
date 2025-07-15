package com.example.gimnasio.admob

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

//@Composable
//fun rememberInterstitialAdManager(
//    adUnitId: String = "ca-app-pub-3940256099942544/1033173712" // ID de prueba
//): InterstitialAdManager {
//    val context = LocalContext.current
//    val adManager = remember { InterstitialAdManager(context) }
//
//    LaunchedEffect(adUnitId) {
//        adManager.loadInterstitial(adUnitId)
//    }
//
//    DisposableEffect(Unit) {
//        onDispose {
//            adManager.interstitialAd = null
//        }
//    }
//
//    return adManager
//}