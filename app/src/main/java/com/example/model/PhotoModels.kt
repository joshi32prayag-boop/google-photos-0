package com.example.model

enum class TransitionEffect(val displayName: String) {
    KEN_BURNS("Ken Burns (Pan & Zoom)"),
    CROSSFADE("Gentle Crossfade"),
    CINEMATIC_ZOOM("Cinematic Slow Zoom"),
    ART_FRAME("Art Gallery Matting")
}

enum class DisplayMode(val displayName: String) {
    FULL_BLEED("Full Bleed (Edge to Edge)"),
    FIT_WITH_BLUR("Fit with Blurred Ambient Backdrop"),
    MATTED_FRAME("Matted Picture Frame")
}

data class PhotoItem(
    val id: String,
    val title: String,
    val location: String,
    val date: String,
    val memoryTag: String,
    val drawableResId: Int? = null,
    val imageUri: String? = null,
    val isFavorite: Boolean = true,
    val album: String = "Favorites"
)

data class AmbientSettings(
    val slideDurationSeconds: Int = 8,
    val transitionEffect: TransitionEffect = TransitionEffect.KEN_BURNS,
    val displayMode: DisplayMode = DisplayMode.FULL_BLEED,
    val showClock: Boolean = true,
    val showWeather: Boolean = true,
    val showPhotoInfo: Boolean = true,
    val screenDimmerPercent: Float = 1.0f,
    val clockFormat24h: Boolean = false,
    val isShuffle: Boolean = false,
    val autoStartOnOpen: Boolean = true
)
