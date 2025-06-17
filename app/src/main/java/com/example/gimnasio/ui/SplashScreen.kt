package com.example.gimnasio.ui

import android.net.Uri
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.navigation.NavController
import com.example.gimnasio.R
import kotlinx.coroutines.delay

@Composable
fun VideoSplashScreen(onFinish: () -> Unit) {
    val context = LocalContext.current
    val videoUri = remember {
        "android.resource://${context.packageName}/${R.raw.calabozo_trim}"
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(videoUri)))
            prepare()
            playWhenReady = true
        }
    }

    AndroidView(
        factory = {
            PlayerView(it).apply {
                player = exoPlayer
                useController = false
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            }
        }
    )

    LaunchedEffect(Unit) {
        delay(1000) // Dura 1 segundo el video
        exoPlayer.release()
        onFinish()
    }
}

