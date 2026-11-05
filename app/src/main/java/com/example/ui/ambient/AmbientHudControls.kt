package com.example.ui.ambient

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Collections
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.auth.GoogleUser
import com.example.model.PhotoItem
import com.example.ui.theme.AmbientAccentGold
import com.example.ui.theme.AmbientPrimary
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurface
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AmbientHudControls(
    isVisible: Boolean,
    isPlaying: Boolean,
    currentPhoto: PhotoItem?,
    currentIndex: Int,
    totalPhotos: Int,
    slideDurationSeconds: Int,
    currentUser: GoogleUser?,
    onTogglePlay: () -> Unit,
    onNext: () -> Unit,
    onPrev: () -> Unit,
    onToggleFavorite: () -> Unit,
    onSelectDuration: (Int) -> Unit,
    onOpenGooglePhotos: () -> Unit,
    onOpenScreensaverGuide: () -> Unit,
    onOpenManagePhotos: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenSignIn: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        0.0f to Color(0xAA080A10),
                        0.25f to Color.Transparent,
                        0.70f to Color.Transparent,
                        1.0f to Color(0xDD080A10)
                    )
                )
                .padding(24.dp)
        ) {
            // TOP BAR
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // App Branding badge & Google Photos status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = Color(0xB3141A28),
                        border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                        modifier = Modifier.testTag("app_brand_badge")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Favorites",
                                tint = AmbientAccentGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Favorites Slideshow",
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                        }
                    }

                    // Google Account button / indicator
                    if (currentUser != null && currentUser.isSignedIn) {
                        FilledTonalButton(
                            onClick = onOpenSignIn,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = Color(0x334CAF50),
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("btn_account_chip")
                        ) {
                            Surface(shape = CircleShape, color = Color(0xFF4285F4), modifier = Modifier.size(18.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("G", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = currentUser.displayName.substringBefore(" "),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Synced",
                                tint = Color(0xFF4CAF50),
                                modifier = Modifier.size(15.dp)
                            )
                        }
                    } else {
                        ElevatedButton(
                            onClick = onOpenSignIn,
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = Color.White,
                                contentColor = Color(0xFF1E293B)
                            ),
                            shape = RoundedCornerShape(20.dp),
                            modifier = Modifier.testTag("btn_open_signin")
                        ) {
                            Surface(shape = CircleShape, color = Color(0xFF4285F4), modifier = Modifier.size(18.dp)) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text("G", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                }
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Sign in with Google", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }

                // Action buttons: Open Google Photos & Config
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Open Google Photos on TV Button
                    ElevatedButton(
                        onClick = onOpenGooglePhotos,
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = AmbientPrimary,
                            contentColor = Color(0xFF201200)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier.testTag("btn_open_google_photos")
                    ) {
                        Icon(
                            imageVector = Icons.Default.OpenInNew,
                            contentDescription = "Open Google Photos",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Open Google Photos",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    // TV Ambient Screensaver Guide
                    FilledTonalIconButton(
                        onClick = onOpenScreensaverGuide,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = GlassSurface,
                            contentColor = TextPrimary
                        ),
                        modifier = Modifier.testTag("btn_screensaver_guide")
                    ) {
                        Icon(
                            imageVector = Icons.Default.HelpOutline,
                            contentDescription = "TV Screensaver Setup"
                        )
                    }

                    // Manage Photos Library
                    FilledTonalIconButton(
                        onClick = onOpenManagePhotos,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = GlassSurface,
                            contentColor = TextPrimary
                        ),
                        modifier = Modifier.testTag("btn_manage_photos")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Collections,
                            contentDescription = "Manage Photos"
                        )
                    }

                    // Ambient Settings
                    FilledTonalIconButton(
                        onClick = onOpenSettings,
                        colors = IconButtonDefaults.filledTonalIconButtonColors(
                            containerColor = GlassSurface,
                            contentColor = TextPrimary
                        ),
                        modifier = Modifier.testTag("btn_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings"
                        )
                    }
                }
            }

            // BOTTOM CONTROLS BAR
            Surface(
                shape = RoundedCornerShape(28.dp),
                color = Color(0xCC121724),
                border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .testTag("slideshow_bottom_hud")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Counter badge (e.g. "1 / 4")
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = Color(0x55FFFFFF)
                    ) {
                        Text(
                            text = "${if (totalPhotos > 0) currentIndex + 1 else 0} / $totalPhotos",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    // Favorite Toggle
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier.testTag("btn_toggle_favorite")
                    ) {
                        Icon(
                            imageVector = if (currentPhoto?.isFavorite == true) Icons.Default.Star else Icons.Outlined.StarBorder,
                            contentDescription = "Toggle Favorite",
                            tint = if (currentPhoto?.isFavorite == true) AmbientAccentGold else TextSecondary,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    // Previous Photo
                    IconButton(
                        onClick = onPrev,
                        modifier = Modifier.testTag("btn_prev_photo")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipPrevious,
                            contentDescription = "Previous Photo",
                            tint = TextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Play / Pause prominent button
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(AmbientPrimary)
                            .clickable { onTogglePlay() }
                            .testTag("btn_play_pause"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause Slideshow" else "Play Slideshow",
                            tint = Color(0xFF1E1300),
                            modifier = Modifier.size(30.dp)
                        )
                    }

                    // Next Photo
                    IconButton(
                        onClick = onNext,
                        modifier = Modifier.testTag("btn_next_photo")
                    ) {
                        Icon(
                            imageVector = Icons.Default.SkipNext,
                            contentDescription = "Next Photo",
                            tint = TextPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Speed Interval selector chips
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        listOf(5, 8, 15, 30).forEach { sec ->
                            val isSelected = slideDurationSeconds == sec
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (isSelected) AmbientPrimary else Color(0x33FFFFFF)
                                    )
                                    .clickable { onSelectDuration(sec) }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${sec}s",
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) Color(0xFF1A1100) else TextPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
