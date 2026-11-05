package com.example.ui.ambient

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.DisplayMode
import com.example.model.PhotoItem
import com.example.model.TransitionEffect

@Composable
fun AmbientPhotoRenderer(
    photo: PhotoItem?,
    displayMode: DisplayMode,
    transitionEffect: TransitionEffect,
    dimmerPercent: Float,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Infinite Ken Burns motion transition for slow, cinematic drift
    val infiniteTransition = rememberInfiniteTransition(label = "ken_burns")
    val kenBurnsScale by infiniteTransition.animateFloat(
        initialValue = 1.00f,
        targetValue = 1.07f,
        animationSpec = infiniteRepeatable(
            animation = tween(12000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )
    val kenBurnsPanX by infiniteTransition.animateFloat(
        initialValue = -12f,
        targetValue = 12f,
        animationSpec = infiniteRepeatable(
            animation = tween(15000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "panX"
    )

    val scaleValue = if (transitionEffect == TransitionEffect.KEN_BURNS) kenBurnsScale else 1.0f
    val panXValue = if (transitionEffect == TransitionEffect.KEN_BURNS) kenBurnsPanX else 0f

    Box(
        modifier = modifier
            .testTag("ambient_photo_canvas")
            .fillMaxSize()
            .background(Color(0xFF07090E))
    ) {
        Crossfade(
            targetState = photo,
            animationSpec = tween(durationMillis = 900),
            label = "photo_crossfade"
        ) { currentPhoto ->
            if (currentPhoto != null) {
                val imageModel = currentPhoto.imageUri ?: currentPhoto.drawableResId

                when (displayMode) {
                    DisplayMode.FULL_BLEED -> {
                        // Full edge-to-edge cinematic bleed with Ken Burns motion
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data(imageModel)
                                .crossfade(true)
                                .build(),
                            contentDescription = currentPhoto.title,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                    scaleX = scaleValue
                                    scaleY = scaleValue
                                    translationX = panXValue
                                }
                        )
                    }

                    DisplayMode.FIT_WITH_BLUR -> {
                        // Ambient blurred backdrop + centered sharp photo
                        Box(modifier = Modifier.fillMaxSize()) {
                            // Atmospheric blurred background
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imageModel)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .blur(40.dp)
                                    .graphicsLayer { alpha = 0.55f }
                            )

                            // Foreground crisp photo
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imageModel)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = currentPhoto.title,
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp)
                                    .graphicsLayer {
                                        scaleX = scaleValue
                                        scaleY = scaleValue
                                    }
                            )
                        }
                    }

                    DisplayMode.MATTED_FRAME -> {
                        // Gallery picture frame with matting
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color(0xFF141720))
                                .padding(36.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .shadow(elevation = 24.dp, shape = RoundedCornerShape(8.dp))
                                    .border(width = 12.dp, color = Color(0xFFF5F3EF), shape = RoundedCornerShape(8.dp))
                                    .padding(12.dp)
                                    .clip(RoundedCornerShape(4.dp))
                            ) {
                                AsyncImage(
                                    model = ImageRequest.Builder(context)
                                        .data(imageModel)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = currentPhoto.title,
                                    contentScale = ContentScale.Fit,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }
                }
            } else {
                // Empty state fallback
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    androidx.compose.material3.Text(
                        text = "No favorite photos available",
                        color = Color.White
                    )
                }
            }
        }

        // Ambient Screen Dimmer overlay (e.g. for low-light night viewing)
        if (dimmerPercent < 1.0f) {
            val darknessAlpha = (1.0f - dimmerPercent).coerceIn(0f, 0.85f)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = darknessAlpha))
            )
        }
    }
}
