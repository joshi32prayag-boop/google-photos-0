package com.example.ui.ambient

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import com.example.model.PhotoItem
import com.example.ui.theme.AmbientAccentGold
import com.example.ui.theme.AmbientPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun AmbientClockWidget(
    timeString: String,
    dateString: String,
    showWeather: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .testTag("ambient_clock_widget")
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xB310141E),
                        Color(0x800A0D14)
                    )
                )
            )
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = timeString,
                    color = TextPrimary,
                    fontSize = 44.sp,
                    fontWeight = FontWeight.Light,
                    letterSpacing = (-1).sp
                )

                if (showWeather) {
                    Row(
                        modifier = Modifier
                            .padding(bottom = 8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0x40FFFFFF))
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Weather",
                            tint = AmbientAccentGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "72° Clear",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Text(
                text = dateString,
                color = TextSecondary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Normal
            )
        }
    }
}

@Composable
fun PhotoMetadataBadge(
    photo: PhotoItem?,
    modifier: Modifier = Modifier
) {
    if (photo == null) return

    Box(
        modifier = modifier
            .testTag("photo_metadata_badge")
            .clip(RoundedCornerShape(18.dp))
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xB310141E),
                        Color(0x800A0D14)
                    )
                )
            )
            .padding(horizontal = 18.dp, vertical = 10.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (photo.isFavorite) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Favorite",
                        tint = AmbientAccentGold,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Text(
                    text = photo.title,
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = AmbientPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "${photo.location} • ${photo.date}",
                    color = TextSecondary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            if (photo.memoryTag.isNotBlank()) {
                Text(
                    text = photo.memoryTag,
                    color = AmbientAccentGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
