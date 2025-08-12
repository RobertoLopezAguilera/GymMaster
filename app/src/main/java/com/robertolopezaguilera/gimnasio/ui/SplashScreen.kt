package com.robertolopezaguilera.gimnasio.ui

import android.net.Uri
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.robertolopezaguilera.gimnasio.R
import kotlinx.coroutines.delay
import com.robertolopezaguilera.gimnasio.ui.theme.GymDarkBlue

@Composable
fun VideoSplashScreen(onFinish: () -> Unit) {
    val context = LocalContext.current
    val videoUri = remember {
        "android.resource://${context.packageName}/${R.raw.gym_manager_logo}"
    }

    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(Uri.parse(videoUri)))
            prepare()
            playWhenReady = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GymDarkBlue) // Color de Compose
    ) {
        AndroidView(
            modifier = Modifier.align(Alignment.Center),
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
    }

    LaunchedEffect(Unit) {
        delay(3000)
        exoPlayer.release()
        onFinish()
    }
}

