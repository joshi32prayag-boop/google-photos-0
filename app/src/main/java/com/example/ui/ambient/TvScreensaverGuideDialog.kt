package com.example.ui.ambient

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.AmbientAccentGold
import com.example.ui.theme.AmbientPrimary
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun TvScreensaverGuideDialog(
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131825)),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 620.dp)
                .testTag("dialog_screensaver_guide")
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tv,
                            contentDescription = "TV",
                            tint = AmbientPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                        Column {
                            Text(
                                text = "TV Ambient & Google Photos",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Dynamic background display on your TV",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Steps Card
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0x3328334E),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "HOW TO ENABLE AUTOMATIC TV SLIDESHOW:",
                            color = AmbientAccentGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        GuideStepItem(
                            number = "1",
                            title = "Automatic Launch",
                            description = "This app immediately begins your Favorites dynamic background slideshow whenever opened."
                        )

                        GuideStepItem(
                            number = "2",
                            title = "Set as Android TV Screen Saver",
                            description = "Go to TV Settings > Device Preferences / System > Screen Saver. Choose 'Ambient Frame' or 'Google Photos'."
                        )

                        GuideStepItem(
                            number = "3",
                            title = "Choose Favorites Album",
                            description = "In Google Photos ambient settings, select 'Your Favorites' to sync starred photos from your account."
                        )

                        GuideStepItem(
                            number = "4",
                            title = "Keep Screen On",
                            description = "Ambient Frame keeps your TV screen alive automatically while the slideshow plays."
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // TV Remote shortcuts
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0x221B2338),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "TV REMOTE SHORTCUTS:",
                            color = AmbientAccentGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.5.sp
                        )

                        RemoteShortcutRow(keyName = "OK / Center", action = "Play / Pause Slideshow")
                        RemoteShortcutRow(keyName = "Left / Right", action = "Previous / Next Photo")
                        RemoteShortcutRow(keyName = "Up", action = "Toggle Ambient Clock & Weather")
                        RemoteShortcutRow(keyName = "Down / Back", action = "Show / Hide Controls HUD")
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ElevatedButton(
                        onClick = { GooglePhotosLauncher.openTvScreensaverSettings(context) },
                        colors = ButtonDefaults.elevatedButtonColors(
                            containerColor = AmbientPrimary,
                            contentColor = Color(0xFF1E1300)
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.Settings, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("TV Settings", fontWeight = FontWeight.Bold)
                    }

                    FilledTonalButton(
                        onClick = { GooglePhotosLauncher.openGooglePhotos(context) },
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(imageVector = Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Google Photos")
                    }
                }
            }
        }
    }
}

@Composable
private fun GuideStepItem(
    number: String,
    title: String,
    description: String
) {
    Row(
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = AmbientPrimary,
            modifier = Modifier.size(24.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = number,
                    color = Color(0xFF1A1100),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
            Text(
                text = description,
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
private fun RemoteShortcutRow(
    keyName: String,
    action: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Surface(
            shape = RoundedCornerShape(6.dp),
            color = Color(0x44FFFFFF)
        ) {
            Text(
                text = keyName,
                color = TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
            )
        }
        Text(
            text = action,
            color = TextSecondary,
            fontSize = 13.sp
        )
    }
}
