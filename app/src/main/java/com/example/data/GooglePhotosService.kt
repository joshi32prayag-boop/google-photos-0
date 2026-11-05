package com.example.data

import com.example.R
import com.example.model.PhotoItem

object GooglePhotosService {

    fun getGooglePhotosFavorites(userEmail: String): List<PhotoItem> {
        val userName = userEmail.substringBefore("@").replace(".", " ").capitalizeWords()

        return listOf(
            PhotoItem(
                id = "gp_fav_1",
                title = "Golden Hour Alpine Reflections",
                location = "Yosemite Valley • Google Photos",
                date = "October 14, 2025",
                memoryTag = "Starred in Google Photos Favorites",
                drawableResId = R.drawable.ambient_mountains,
                isFavorite = true,
                album = "Google Photos Favorites"
            ),
            PhotoItem(
                id = "gp_fav_2",
                title = "Pacific Coast Sunset Highway",
                location = "Big Sur Coast • Google Photos",
                date = "July 22, 2025",
                memoryTag = "Google Photos Memory: Summer Highlights",
                drawableResId = R.drawable.ambient_coastal,
                isFavorite = true,
                album = "Google Photos Favorites"
            ),
            PhotoItem(
                id = "gp_fav_3",
                title = "Arctic Aurora Lights Display",
                location = "Tromsø Fjords • Google Photos",
                date = "December 30, 2025",
                memoryTag = "Google Photos Memory: Winter Wonder",
                drawableResId = R.drawable.ambient_aurora,
                isFavorite = true,
                album = "Google Photos Favorites"
            )
        )
    }

    private fun String.capitalizeWords(): String {
        return split(" ").joinToString(" ") { word ->
            word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
        }
    }
}
