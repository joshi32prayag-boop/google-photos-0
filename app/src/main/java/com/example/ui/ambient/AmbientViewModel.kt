package com.example.ui.ambient

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.auth.GoogleAuthManager
import com.example.auth.GoogleUser
import com.example.data.PhotoRepository
import com.example.model.AmbientSettings
import com.example.model.PhotoItem
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AmbientViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = PhotoRepository(application)
    private val authManager = GoogleAuthManager(application)

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isPlaying = MutableStateFlow(true)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _isHudVisible = MutableStateFlow(true)
    val isHudVisible: StateFlow<Boolean> = _isHudVisible.asStateFlow()

    val photos: StateFlow<List<PhotoItem>> = repository.photos
    val settings: StateFlow<AmbientSettings> = repository.settings
    val currentUser: StateFlow<GoogleUser?> = authManager.currentUser
    val isGooglePhotosConnected: StateFlow<Boolean> = repository.isGooglePhotosConnected

    private val _currentTimeString = MutableStateFlow("")
    val currentTimeString: StateFlow<String> = _currentTimeString.asStateFlow()

    private val _currentDateString = MutableStateFlow("")
    val currentDateString: StateFlow<String> = _currentDateString.asStateFlow()

    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showPhotosSheet = MutableStateFlow(false)
    val showPhotosSheet: StateFlow<Boolean> = _showPhotosSheet.asStateFlow()

    private val _showScreensaverGuide = MutableStateFlow(false)
    val showScreensaverGuide: StateFlow<Boolean> = _showScreensaverGuide.asStateFlow()

    private val _showSignInDialog = MutableStateFlow(false)
    val showSignInDialog: StateFlow<Boolean> = _showSignInDialog.asStateFlow()

    private val _bannerMessage = MutableStateFlow<String?>(null)
    val bannerMessage: StateFlow<String?> = _bannerMessage.asStateFlow()

    private var slideshowJob: Job? = null
    private var autoHideHudJob: Job? = null
    private var clockJob: Job? = null

    init {
        startClock()

        // Observe user authentication and sync photos
        viewModelScope.launch {
            authManager.currentUser.collect { user ->
                if (user != null && user.isSignedIn) {
                    repository.syncGooglePhotos(user.email)
                    _bannerMessage.value = "Streaming Google Photos Favorites for ${user.displayName}"
                    // Auto-start slideshow directly from Google Photos on sign in
                    startGooglePhotosSlideshow()
                } else {
                    repository.disconnectGooglePhotos()
                }
            }
        }

        // Start slideshow immediately on launch as requested
        startSlideshow()
        scheduleHudAutoHide()
    }

    private fun startClock() {
        clockJob?.cancel()
        clockJob = viewModelScope.launch {
            while (isActive) {
                val now = Date()
                val is24h = settings.value.clockFormat24h
                val timeFormat = if (is24h) {
                    SimpleDateFormat("HH:mm", Locale.getDefault())
                } else {
                    SimpleDateFormat("h:mm a", Locale.getDefault())
                }
                val dateFormat = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())

                _currentTimeString.value = timeFormat.format(now)
                _currentDateString.value = dateFormat.format(now)
                delay(1000)
            }
        }
    }

    private fun startSlideshow() {
        slideshowJob?.cancel()
        slideshowJob = viewModelScope.launch {
            while (isActive) {
                val durationSec = settings.value.slideDurationSeconds.coerceAtLeast(3)
                delay(durationSec * 1000L)
                if (_isPlaying.value) {
                    advanceNext()
                }
            }
        }
    }

    fun startGooglePhotosSlideshow() {
        _currentIndex.value = 0
        _isPlaying.value = true
        _showSignInDialog.value = false
        startSlideshow()
        scheduleHudAutoHide()
    }

    private fun advanceNext() {
        val currentPhotos = getDisplayPhotos()
        if (currentPhotos.isNotEmpty()) {
            if (settings.value.isShuffle && currentPhotos.size > 1) {
                var nextIndex: Int
                do {
                    nextIndex = (currentPhotos.indices).random()
                } while (nextIndex == _currentIndex.value && currentPhotos.size > 1)
                _currentIndex.value = nextIndex
            } else {
                _currentIndex.value = (_currentIndex.value + 1) % currentPhotos.size
            }
        }
    }

    fun getDisplayPhotos(): List<PhotoItem> {
        val all = photos.value
        val favorites = all.filter { it.isFavorite }
        return if (favorites.isNotEmpty()) favorites else all
    }

    fun currentPhoto(): PhotoItem? {
        val list = getDisplayPhotos()
        if (list.isEmpty()) return null
        val safeIndex = _currentIndex.value.coerceIn(0, list.lastIndex)
        return list[safeIndex]
    }

    fun togglePlayPause() {
        _isPlaying.value = !_isPlaying.value
        showHud()
    }

    fun nextPhoto() {
        advanceNext()
        showHud()
    }

    fun prevPhoto() {
        val list = getDisplayPhotos()
        if (list.isNotEmpty()) {
            _currentIndex.value = if (_currentIndex.value - 1 < 0) list.size - 1 else _currentIndex.value - 1
        }
        showHud()
    }

    fun selectPhotoIndex(index: Int) {
        val list = getDisplayPhotos()
        if (index in list.indices) {
            _currentIndex.value = index
        }
    }

    fun toggleHud() {
        if (_isHudVisible.value) {
            hideHud()
        } else {
            showHud()
        }
    }

    fun showHud() {
        _isHudVisible.value = true
        scheduleHudAutoHide()
    }

    fun hideHud() {
        _isHudVisible.value = false
        autoHideHudJob?.cancel()
    }

    private fun scheduleHudAutoHide() {
        autoHideHudJob?.cancel()
        autoHideHudJob = viewModelScope.launch {
            delay(5000)
            _isHudVisible.value = false
        }
    }

    fun signInWithGoogle(context: Context) {
        viewModelScope.launch {
            authManager.signInWithCredentialManager(context)
            _showSignInDialog.value = false
            startGooglePhotosSlideshow()
        }
    }

    fun signInDirectly() {
        authManager.signInDirectly()
        _showSignInDialog.value = false
        startGooglePhotosSlideshow()
    }

    fun signOut() {
        authManager.signOut()
        _bannerMessage.value = "Signed out of Google Photos"
    }

    fun updateSettings(newSettings: AmbientSettings) {
        repository.updateSettings(newSettings)
        startSlideshow()
    }

    fun toggleFavorite(photoId: String) {
        repository.toggleFavorite(photoId)
    }

    fun removePhoto(photoId: String) {
        repository.removePhoto(photoId)
        val currentPhotos = getDisplayPhotos()
        if (_currentIndex.value >= currentPhotos.size) {
            _currentIndex.value = 0
        }
    }

    fun addPhotos(uris: List<String>) {
        repository.addCustomPhotos(uris)
    }

    fun setSettingsDialogVisible(visible: Boolean) {
        _showSettingsDialog.value = visible
        if (visible) showHud()
    }

    fun setPhotosSheetVisible(visible: Boolean) {
        _showPhotosSheet.value = visible
        if (visible) showHud()
    }

    fun setScreensaverGuideVisible(visible: Boolean) {
        _showScreensaverGuide.value = visible
        if (visible) showHud()
    }

    fun setSignInDialogVisible(visible: Boolean) {
        _showSignInDialog.value = visible
        if (visible) showHud()
    }

    fun dismissBanner() {
        _bannerMessage.value = null
    }
}
