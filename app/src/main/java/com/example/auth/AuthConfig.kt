package com.example.auth

object AuthConfig {
    const val OAUTH_CLIENT_ID = "205748904663-0pculj7rrnp330si8n6ainkt37l598uh.apps.googleusercontent.com"
    const val PROJECT_ID = "gen-lang-client-0759551863"
    const val PHOTOS_LIBRARY_SCOPE = "https://www.googleapis.com/auth/photoslibrary.readonly"
}

data class GoogleUser(
    val email: String,
    val displayName: String,
    val photoUrl: String? = null,
    val idToken: String? = null,
    val isSignedIn: Boolean = true,
    val totalSyncedPhotos: Int = 0
)
