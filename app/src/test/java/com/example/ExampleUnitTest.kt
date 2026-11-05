package com.example

import com.example.auth.AuthConfig
import com.example.auth.GoogleUser
import com.example.data.GooglePhotosService
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun `verify google photos favorites generation`() {
    val favorites = GooglePhotosService.getGooglePhotosFavorites("joshi32prayag@gmail.com")
    assertTrue("Favorites should not be empty", favorites.isNotEmpty())
    assertTrue("All items should be marked as favorite", favorites.all { it.isFavorite })
    assertTrue("All items should belong to Google Photos album", favorites.all { it.album.contains("Google Photos") })
  }

  @Test
  fun `verify google auth config parameters`() {
    assertEquals("gen-lang-client-0759551863", AuthConfig.PROJECT_ID)
    assertTrue("Client ID should contain googleusercontent", AuthConfig.OAUTH_CLIENT_ID.contains("googleusercontent.com"))
    assertEquals("https://www.googleapis.com/auth/photoslibrary.readonly", AuthConfig.PHOTOS_LIBRARY_SCOPE)
  }

  @Test
  fun `verify google user model`() {
    val user = GoogleUser(
      email = "joshi32prayag@gmail.com",
      displayName = "Prayag Joshi",
      isSignedIn = true
    )
    assertEquals("joshi32prayag@gmail.com", user.email)
    assertTrue(user.isSignedIn)
  }
}
