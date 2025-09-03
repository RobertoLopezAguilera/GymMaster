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
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
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
            repeatMode = Player.REPEAT_MODE_OFF
            prepare()
            playWhenReady = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GymDarkBlue)
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.Center),
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = false

                    // Configuración avanzada para el ajuste de video
                    resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL // Llena toda la pantalla

                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )

                    // Asegurar que el video ocupe toda la pantalla
                    setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL)
                }
            }
        )
    }

    LaunchedEffect(Unit) {
        // Esperar a que el video comience a reproducirse
        delay(300)

        // Usar la duración real del video o un valor por defecto
        val duration = if (exoPlayer.duration != C.TIME_UNSET && exoPlayer.duration > 0) {
            exoPlayer.duration + 500 // Pequeño margen adicional
        } else {
            3000L // Duración por defecto de 3 segundos
        }

        // Esperar la duración del video
        delay(duration)

        // Verificar si el video todavía se está reproduciendo
        if (exoPlayer.isPlaying) {
            exoPlayer.stop()
        }

        exoPlayer.release()
        onFinish()
    }
}