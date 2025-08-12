package com.robertolopezaguilera.gimnasio.admob

import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView

@Composable
fun AdBanner(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        modifier = modifier.height(50.dp),
        factory = { context ->
            AdView(context).apply {
                setAdSize(AdSize.BANNER)
                adUnitId = "ca-app-pub-1678389918256405/4872645547" // ID Real
                loadAd(AdRequest.Builder().build())
            }
        },
        update = { adView ->
            adView.loadAd(AdRequest.Builder().build())
        }
    )
}