package com.example.ui.ambient

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.model.PhotoItem
import com.example.ui.theme.AmbientAccentGold
import com.example.ui.theme.AmbientPrimary
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun PhotoManagerSheet(
    photos: List<PhotoItem>,
    currentIndex: Int,
    onSelectIndex: (Int) -> Unit,
    onToggleFavorite: (String) -> Unit,
    onRemovePhoto: (String) -> Unit,
    onAddPhotos: (List<String>) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Android zero-permission Photo Picker for choosing photos from Google Photos or device
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 20)
    ) { uris ->
        if (uris.isNotEmpty()) {
            val uriStrings = uris.map { it.toString() }
            onAddPhotos(uriStrings)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131824)),
            border = androidx.compose.foundation.BorderStroke(1.dp, GlassBorder),
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(max = 640.dp)
                .testTag("dialog_manage_photos")
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Favorites & Photo Library",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${photos.size} photos • Tap any to show on TV",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Add Photos Button
                ElevatedButton(
                    onClick = {
                        photoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    },
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = AmbientPrimary,
                        contentColor = Color(0xFF1E1300)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("btn_add_photos")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Add Photos",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Add Photos from Device / Google Photos",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Photos Grid
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 130.dp),
                    contentPadding = PaddingValues(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(photos, key = { _, item -> item.id }) { index, item ->
                        val isCurrent = index == currentIndex
                        val imageModel = item.imageUri ?: item.drawableResId

                        Box(
                            modifier = Modifier
                                .aspectRatio(16f / 10f)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    width = if (isCurrent) 2.dp else 1.dp,
                                    color = if (isCurrent) AmbientPrimary else Color(0x33FFFFFF),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    onSelectIndex(index)
                                    onDismiss()
                                }
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data(imageModel)
                                    .crossfade(true)
                                    .build(),
                                contentDescription = item.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Gradient overlay for bottom title
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .align(Alignment.BottomCenter)
                                    .background(
                                        Brush.verticalGradient(
                                            colors = listOf(Color.Transparent, Color(0xDD000000))
                                        )
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = item.title,
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 1
                                )
                            }

                            // Star favorite button in top-left
                            Box(
                                modifier = Modifier
                                    .padding(6.dp)
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(Color(0x99000000))
                                    .clickable { onToggleFavorite(item.id) }
                                    .align(Alignment.TopStart),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (item.isFavorite) Icons.Default.Star else Icons.Outlined.StarBorder,
                                    contentDescription = "Favorite",
                                    tint = if (item.isFavorite) AmbientAccentGold else TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }

                            // Delete button if custom user photo
                            if (item.imageUri != null) {
                                Box(
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x99000000))
                                        .clickable { onRemovePhoto(item.id) }
                                        .align(Alignment.TopEnd),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "Delete Photo",
                                        tint = Color(0xFFFF6B6B),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
