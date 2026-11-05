package com.example.ui.ambient

import android.view.KeyEvent
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.auth.GoogleSignInDialog
import com.example.ui.theme.AmbientPrimary
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.TextPrimary
import kotlinx.coroutines.delay

@Composable
fun AmbientSlideshowScreen(
    viewModel: AmbientViewModel = viewModel()
) {
    val context = LocalContext.current
    val view = LocalView.current

    // Keep TV and phone screen on during dynamic background display
    DisposableEffect(Unit) {
        view.keepScreenOn = true
        onDispose {
            view.keepScreenOn = false
        }
    }

    val photos by viewModel.photos.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val currentIndex by viewModel.currentIndex.collectAsState()
    val isPlaying by viewModel.isPlaying.collectAsState()
    val isHudVisible by viewModel.isHudVisible.collectAsState()
    val timeString by viewModel.currentTimeString.collectAsState()
    val dateString by viewModel.currentDateString.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val bannerMessage by viewModel.bannerMessage.collectAsState()

    val showSettings by viewModel.showSettingsDialog.collectAsState()
    val showPhotosSheet by viewModel.showPhotosSheet.collectAsState()
    val showGuide by viewModel.showScreensaverGuide.collectAsState()
    val showSignIn by viewModel.showSignInDialog.collectAsState()

    val displayPhotos = viewModel.getDisplayPhotos()
    val currentPhoto = viewModel.currentPhoto()

    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    // Auto-dismiss banner after 4 seconds
    LaunchedEffect(bannerMessage) {
        if (bannerMessage != null) {
            delay(4000)
            viewModel.dismissBanner()
        }
    }

    // Back handling: dismiss modal/HUD first
    BackHandler {
        if (showSignIn) {
            viewModel.setSignInDialogVisible(false)
        } else if (showSettings) {
            viewModel.setSettingsDialogVisible(false)
        } else if (showPhotosSheet) {
            viewModel.setPhotosSheetVisible(false)
        } else if (showGuide) {
            viewModel.setScreensaverGuideVisible(false)
        } else if (isHudVisible) {
            viewModel.hideHud()
        } else {
            viewModel.showHud()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .testTag("ambient_root_screen")
            .focusRequester(focusRequester)
            .focusable()
            .onKeyEvent { keyEvent ->
                if (keyEvent.nativeKeyEvent.action == KeyEvent.ACTION_DOWN) {
                    when (keyEvent.nativeKeyEvent.keyCode) {
                        KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                            viewModel.togglePlayPause()
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_LEFT -> {
                            viewModel.prevPhoto()
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_RIGHT -> {
                            viewModel.nextPhoto()
                            true
                        }
                        KeyEvent.KEYCODE_DPAD_UP, KeyEvent.KEYCODE_DPAD_DOWN -> {
                            viewModel.toggleHud()
                            true
                        }
                        KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE -> {
                            viewModel.togglePlayPause()
                            true
                        }
                        KeyEvent.KEYCODE_MEDIA_NEXT -> {
                            viewModel.nextPhoto()
                            true
                        }
                        KeyEvent.KEYCODE_MEDIA_PREVIOUS -> {
                            viewModel.prevPhoto()
                            true
                        }
                        else -> false
                    }
                } else {
                    false
                }
            }
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                viewModel.toggleHud()
            }
    ) {
        // 1. DYNAMIC PHOTO CANVAS BACKGROUND
        AmbientPhotoRenderer(
            photo = currentPhoto,
            displayMode = settings.displayMode,
            transitionEffect = settings.transitionEffect,
            dimmerPercent = settings.screenDimmerPercent,
            modifier = Modifier.fillMaxSize()
        )

        // 2. AMBIENT CLOCK & WEATHER WIDGET (Top Left)
        if (settings.showClock) {
            AmbientClockWidget(
                timeString = timeString,
                dateString = dateString,
                showWeather = settings.showWeather,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 24.dp, top = 36.dp)
            )
        }

        // 3. FLOATING SYNC BANNER (Top Center)
        AnimatedVisibility(
            visible = bannerMessage != null,
            enter = fadeIn() + slideInVertically { -it },
            exit = fadeOut() + slideOutVertically { -it },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 28.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = Color(0xDD121A28),
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                modifier = Modifier.testTag("banner_google_sync")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = bannerMessage ?: "",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // 4. PHOTO LOCATION & MEMORY BADGE (Bottom Left, above HUD)
        if (settings.showPhotoInfo && currentPhoto != null) {
            AnimatedVisibility(
                visible = !isHudVisible,
                enter = fadeIn() + slideInVertically { it / 2 },
                exit = fadeOut() + slideOutVertically { it / 2 },
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 24.dp, bottom = 32.dp)
            ) {
                PhotoMetadataBadge(photo = currentPhoto)
            }
        }

        // 5. ON-SCREEN HUD CONTROLS
        AmbientHudControls(
            isVisible = isHudVisible,
            isPlaying = isPlaying,
            currentPhoto = currentPhoto,
            currentIndex = currentIndex,
            totalPhotos = displayPhotos.size,
            slideDurationSeconds = settings.slideDurationSeconds,
            currentUser = currentUser,
            onTogglePlay = { viewModel.togglePlayPause() },
            onNext = { viewModel.nextPhoto() },
            onPrev = { viewModel.prevPhoto() },
            onToggleFavorite = { currentPhoto?.let { viewModel.toggleFavorite(it.id) } },
            onSelectDuration = { sec ->
                viewModel.updateSettings(settings.copy(slideDurationSeconds = sec))
            },
            onOpenGooglePhotos = { GooglePhotosLauncher.openGooglePhotos(context) },
            onOpenScreensaverGuide = { viewModel.setScreensaverGuideVisible(true) },
            onOpenManagePhotos = { viewModel.setPhotosSheetVisible(true) },
            onOpenSettings = { viewModel.setSettingsDialogVisible(true) },
            onOpenSignIn = { viewModel.setSignInDialogVisible(true) }
        )

        // 6. DIALOGS & OVERLAYS
        if (showSignIn) {
            GoogleSignInDialog(
                currentUser = currentUser,
                onSignIn = { viewModel.signInWithGoogle(context) },
                onSignOut = { viewModel.signOut() },
                onStartSlideshow = { viewModel.startGooglePhotosSlideshow() },
                onDismiss = { viewModel.setSignInDialogVisible(false) }
            )
        }

        if (showSettings) {
            SettingsDialog(
                settings = settings,
                currentUser = currentUser,
                onSettingsChanged = { viewModel.updateSettings(it) },
                onOpenSignIn = {
                    viewModel.setSettingsDialogVisible(false)
                    viewModel.setSignInDialogVisible(true)
                },
                onDismiss = { viewModel.setSettingsDialogVisible(false) },
                onOpenScreensaverGuide = {
                    viewModel.setSettingsDialogVisible(false)
                    viewModel.setScreensaverGuideVisible(true)
                }
            )
        }

        if (showPhotosSheet) {
            PhotoManagerSheet(
                photos = photos,
                currentIndex = currentIndex,
                onSelectIndex = { index -> viewModel.selectPhotoIndex(index) },
                onToggleFavorite = { id -> viewModel.toggleFavorite(id) },
                onRemovePhoto = { id -> viewModel.removePhoto(id) },
                onAddPhotos = { uris -> viewModel.addPhotos(uris) },
                onDismiss = { viewModel.setPhotosSheetVisible(false) }
            )
        }

        if (showGuide) {
            TvScreensaverGuideDialog(
                onDismiss = { viewModel.setScreensaverGuideVisible(false) }
            )
        }
    }
}
