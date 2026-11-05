package com.example.ui.ambient

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DisplaySettings
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.AmbientSettings
import com.example.model.DisplayMode
import com.example.model.TransitionEffect
import com.example.ui.theme.AmbientAccentGold
import com.example.ui.theme.AmbientPrimary
import com.example.ui.theme.AmbientSurfaceCard
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.roundToInt

import com.example.auth.GoogleUser

@Composable
fun SettingsDialog(
    settings: AmbientSettings,
    currentUser: GoogleUser?,
    onSettingsChanged: (AmbientSettings) -> Unit,
    onOpenSignIn: () -> Unit,
    onDismiss: () -> Unit,
    onOpenScreensaverGuide: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF141926)),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 640.dp)
                .testTag("dialog_settings")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Ambient Display Settings",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Customize your TV background & slideshow",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("btn_close_settings")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section: Google Photos Account
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = if (currentUser?.isSignedIn == true) Color(0x224CAF50) else Color(0x3328324A),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (currentUser?.isSignedIn == true) Color(0x554CAF50) else Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (currentUser?.isSignedIn == true) "Google Photos (Synced)" else "Google Photos Sync",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                            }
                            Text(
                                text = if (currentUser?.isSignedIn == true)
                                    "Connected as ${currentUser.email}"
                                else
                                    "Sign in to stream your Favorites album",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        ElevatedButton(
                            onClick = onOpenSignIn,
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = if (currentUser?.isSignedIn == true) Color(0x33FFFFFF) else Color.White,
                                contentColor = if (currentUser?.isSignedIn == true) TextPrimary else Color(0xFF1E293B)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = if (currentUser?.isSignedIn == true) "Manage" else "Sign In",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Section: Google Photos App Action
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0x221B2338),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Google Photos TV App",
                                color = TextPrimary,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Launch standalone app on this device",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                        ElevatedButton(
                            onClick = { GooglePhotosLauncher.openGooglePhotos(context) },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = AmbientPrimary,
                                contentColor = Color(0xFF221400)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Open", fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section: Slide Interval Duration
                Text(
                    text = "SLIDESHOW TIMING",
                    color = AmbientAccentGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Slide Duration",
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${settings.slideDurationSeconds} seconds",
                        color = AmbientPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Slider(
                    value = settings.slideDurationSeconds.toFloat(),
                    onValueChange = { newVal ->
                        onSettingsChanged(settings.copy(slideDurationSeconds = newVal.roundToInt()))
                    },
                    valueRange = 3f..60f,
                    steps = 18,
                    colors = SliderDefaults.colors(
                        thumbColor = AmbientPrimary,
                        activeTrackColor = AmbientPrimary,
                        inactiveTrackColor = Color(0x33FFFFFF)
                    ),
                    modifier = Modifier.testTag("slider_duration")
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Section: Display Mode
                Text(
                    text = "DISPLAY STYLE",
                    color = AmbientAccentGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                DisplayMode.values().forEach { mode ->
                    val isSelected = settings.displayMode == mode
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0x33FFB74D) else Color(0x221B2336))
                            .clickable { onSettingsChanged(settings.copy(displayMode = mode)) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = mode.displayName,
                            color = if (isSelected) AmbientPrimary else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Section: Motion & Transition
                Text(
                    text = "MOTION TRANSITION",
                    color = AmbientAccentGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                TransitionEffect.values().forEach { effect ->
                    val isSelected = settings.transitionEffect == effect
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0x33FFB74D) else Color(0x221B2336))
                            .clickable { onSettingsChanged(settings.copy(transitionEffect = effect)) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = effect.displayName,
                            color = if (isSelected) AmbientPrimary else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Section: Ambient Widgets
                Text(
                    text = "AMBIENT OVERLAYS",
                    color = AmbientAccentGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Clock toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Ambient Clock", color = TextPrimary, fontSize = 14.sp)
                    Switch(
                        checked = settings.showClock,
                        onCheckedChange = { onSettingsChanged(settings.copy(showClock = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = AmbientPrimary)
                    )
                }

                // 24h format
                if (settings.showClock) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("24-Hour Time Format", color = TextSecondary, fontSize = 13.sp)
                        Switch(
                            checked = settings.clockFormat24h,
                            onCheckedChange = { onSettingsChanged(settings.copy(clockFormat24h = it)) },
                            colors = SwitchDefaults.colors(checkedThumbColor = AmbientPrimary)
                        )
                    }
                }

                // Weather toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Weather Glance", color = TextPrimary, fontSize = 14.sp)
                    Switch(
                        checked = settings.showWeather,
                        onCheckedChange = { onSettingsChanged(settings.copy(showWeather = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = AmbientPrimary)
                    )
                }

                // Photo info toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Show Location & Memories", color = TextPrimary, fontSize = 14.sp)
                    Switch(
                        checked = settings.showPhotoInfo,
                        onCheckedChange = { onSettingsChanged(settings.copy(showPhotoInfo = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = AmbientPrimary)
                    )
                }

                // Shuffle toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Shuffle Slideshow Order", color = TextPrimary, fontSize = 14.sp)
                    Switch(
                        checked = settings.isShuffle,
                        onCheckedChange = { onSettingsChanged(settings.copy(isShuffle = it)) },
                        colors = SwitchDefaults.colors(checkedThumbColor = AmbientPrimary)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Night Ambient Dimmer
                Text(
                    text = "NIGHT AMBIENT DIMMER",
                    color = AmbientAccentGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Brightness Level",
                        color = TextPrimary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${(settings.screenDimmerPercent * 100).roundToInt()}%",
                        color = AmbientPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Slider(
                    value = settings.screenDimmerPercent,
                    onValueChange = { newVal ->
                        onSettingsChanged(settings.copy(screenDimmerPercent = newVal))
                    },
                    valueRange = 0.35f..1.0f,
                    colors = SliderDefaults.colors(
                        thumbColor = AmbientPrimary,
                        activeTrackColor = AmbientPrimary
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Screensaver Guide button
                FilledTonalButton(
                    onClick = onOpenScreensaverGuide,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(imageVector = Icons.Default.Tv, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Android TV Screensaver Setup Guide")
                }
            }
        }
    }
}
