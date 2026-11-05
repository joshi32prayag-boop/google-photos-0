package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.R
import com.example.model.AmbientSettings
import com.example.model.DisplayMode
import com.example.model.PhotoItem
import com.example.model.TransitionEffect
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

class PhotoRepository(private val context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("ambient_frame_prefs", Context.MODE_PRIVATE)

    private val _photos = MutableStateFlow<List<PhotoItem>>(emptyList())
    val photos: StateFlow<List<PhotoItem>> = _photos.asStateFlow()

    private val _settings = MutableStateFlow(AmbientSettings())
    val settings: StateFlow<AmbientSettings> = _settings.asStateFlow()

    private val _isGooglePhotosConnected = MutableStateFlow(false)
    val isGooglePhotosConnected: StateFlow<Boolean> = _isGooglePhotosConnected.asStateFlow()

    init {
        loadSettings()
        loadPhotos()
    }

    private fun loadSettings() {
        val duration = prefs.getInt("duration", 8)
        val effectName = prefs.getString("effect", TransitionEffect.KEN_BURNS.name)
            ?: TransitionEffect.KEN_BURNS.name
        val modeName = prefs.getString("mode", DisplayMode.FULL_BLEED.name)
            ?: DisplayMode.FULL_BLEED.name
        val showClock = prefs.getBoolean("show_clock", true)
        val showWeather = prefs.getBoolean("show_weather", true)
        val showInfo = prefs.getBoolean("show_info", true)
        val dimmer = prefs.getFloat("dimmer", 1.0f)
        val format24h = prefs.getBoolean("format_24h", false)
        val shuffle = prefs.getBoolean("shuffle", false)
        val autoStart = prefs.getBoolean("auto_start", true)

        val effect = try {
            TransitionEffect.valueOf(effectName)
        } catch (e: Exception) {
            TransitionEffect.KEN_BURNS
        }

        val mode = try {
            DisplayMode.valueOf(modeName)
        } catch (e: Exception) {
            DisplayMode.FULL_BLEED
        }

        _settings.value = AmbientSettings(
            slideDurationSeconds = duration,
            transitionEffect = effect,
            displayMode = mode,
            showClock = showClock,
            showWeather = showWeather,
            showPhotoInfo = showInfo,
            screenDimmerPercent = dimmer,
            clockFormat24h = format24h,
            isShuffle = shuffle,
            autoStartOnOpen = autoStart
        )
    }

    fun updateSettings(newSettings: AmbientSettings) {
        _settings.value = newSettings
        prefs.edit().apply {
            putInt("duration", newSettings.slideDurationSeconds)
            putString("effect", newSettings.transitionEffect.name)
            putString("mode", newSettings.displayMode.name)
            putBoolean("show_clock", newSettings.showClock)
            putBoolean("show_weather", newSettings.showWeather)
            putBoolean("show_info", newSettings.showPhotoInfo)
            putFloat("dimmer", newSettings.screenDimmerPercent)
            putBoolean("format_24h", newSettings.clockFormat24h)
            putBoolean("shuffle", newSettings.isShuffle)
            putBoolean("auto_start", newSettings.autoStartOnOpen)
            apply()
        }
    }

    private fun loadPhotos() {
        val defaultList = listOf(
            PhotoItem(
                id = "def_1",
                title = "Alpine Sunset Mirror",
                location = "Yosemite National Park, CA",
                date = "October 14, 2024",
                memoryTag = "2 years ago today",
                drawableResId = R.drawable.ambient_mountains,
                isFavorite = true,
                album = "Google Photos Favorites"
            ),
            PhotoItem(
                id = "def_2",
                title = "Golden Hour Ocean Vista",
                location = "Big Sur Coastline, California",
                date = "July 22, 2025",
                memoryTag = "Summer Vacation Highlight",
                drawableResId = R.drawable.ambient_coastal,
                isFavorite = true,
                album = "Google Photos Favorites"
            ),
            PhotoItem(
                id = "def_3",
                title = "Emerald Aurora Sky",
                location = "Tromsø, Northern Norway",
                date = "December 30, 2025",
                memoryTag = "Bucket List Memory",
                drawableResId = R.drawable.ambient_aurora,
                isFavorite = true,
                album = "Google Photos Favorites"
            )
        )

        val customPhotosJson = prefs.getString("custom_photos", null)
        val customList = mutableListOf<PhotoItem>()
        if (!customPhotosJson.isNullOrEmpty()) {
            try {
                val array = JSONArray(customPhotosJson)
                for (i in 0 until array.length()) {
                    val obj = array.getJSONObject(i)
                    customList.add(
                        PhotoItem(
                            id = obj.getString("id"),
                            title = obj.optString("title", "My Photo"),
                            location = obj.optString("location", "Google Photos Favorite"),
                            date = obj.optString("date", "Recent Memory"),
                            memoryTag = obj.optString("memoryTag", "Favorite Memory"),
                            imageUri = obj.optString("imageUri", null),
                            isFavorite = obj.optBoolean("isFavorite", true),
                            album = obj.optString("album", "Favorites")
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        _photos.value = defaultList + customList
    }

    fun syncGooglePhotos(userEmail: String) {
        _isGooglePhotosConnected.value = true
        val googleFavorites = GooglePhotosService.getGooglePhotosFavorites(userEmail)
        val currentList = _photos.value.toMutableList()

        // Replace or prepend Google Photos favorites
        for (item in googleFavorites) {
            if (currentList.none { it.id == item.id }) {
                currentList.add(0, item)
            }
        }
        _photos.value = currentList
    }

    fun disconnectGooglePhotos() {
        _isGooglePhotosConnected.value = false
        // Keep non-Google specific items or reload defaults
        loadPhotos()
    }

    fun addCustomPhotos(uris: List<String>) {
        val currentList = _photos.value.toMutableList()
        val newCustomItems = uris.mapIndexed { index, uriStr ->
            PhotoItem(
                id = "custom_${System.currentTimeMillis()}_$index",
                title = "Photo ${currentList.size + index + 1}",
                location = "Google Photos Library",
                date = "Added to Favorites",
                memoryTag = "Favorite Memory",
                imageUri = uriStr,
                isFavorite = true,
                album = "Google Photos Favorites"
            )
        }
        currentList.addAll(newCustomItems)
        _photos.value = currentList
        saveCustomPhotos(currentList)
    }

    fun toggleFavorite(photoId: String) {
        val updated = _photos.value.map {
            if (it.id == photoId) it.copy(isFavorite = !it.isFavorite) else it
        }
        _photos.value = updated
        saveCustomPhotos(updated)
    }

    fun removePhoto(photoId: String) {
        val updated = _photos.value.filterNot { it.id == photoId }
        _photos.value = updated
        saveCustomPhotos(updated)
    }

    private fun saveCustomPhotos(list: List<PhotoItem>) {
        val customOnly = list.filter { it.imageUri != null }
        val array = JSONArray()
        for (item in customOnly) {
            val obj = JSONObject().apply {
                put("id", item.id)
                put("title", item.title)
                put("location", item.location)
                put("date", item.date)
                put("memoryTag", item.memoryTag)
                put("imageUri", item.imageUri)
                put("isFavorite", item.isFavorite)
                put("album", item.album)
            }
            array.put(obj)
        }
        prefs.edit().putString("custom_photos", array.toString()).apply()
    }
}
