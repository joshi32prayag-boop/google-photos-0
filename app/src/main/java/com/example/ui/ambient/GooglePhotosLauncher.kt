package com.example.ui.ambient

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

object GooglePhotosLauncher {

    const val GOOGLE_PHOTOS_PACKAGE = "com.google.android.apps.photos"
    const val GOOGLE_PHOTOS_FAVORITES_WEB = "https://photos.google.com/search/_tra_"

    fun openGooglePhotos(context: Context) {
        val pm = context.packageManager
        val launchIntent = pm.getLaunchIntentForPackage(GOOGLE_PHOTOS_PACKAGE)

        if (launchIntent != null) {
            launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(launchIntent)
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Fallback: Open web link to Google Photos favorites or Play Store
        try {
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(GOOGLE_PHOTOS_FAVORITES_WEB)).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(webIntent)
        } catch (e: Exception) {
            try {
                val marketIntent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("market://details?id=$GOOGLE_PHOTOS_PACKAGE")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(marketIntent)
            } catch (e2: Exception) {
                Toast.makeText(context, "Could not open Google Photos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun openTvScreensaverSettings(context: Context) {
        // Tries standard Android TV Daydream / Ambient settings
        val intentsToTry = listOf(
            Intent("android.settings.DREAM_SETTINGS"),
            Intent("android.settings.SCREENSAVER_SETTINGS"),
            Intent("android.settings.DISPLAY_SETTINGS"),
            Intent(android.provider.Settings.ACTION_SETTINGS)
        )

        for (intent in intentsToTry) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            try {
                context.startActivity(intent)
                return
            } catch (_: Exception) {
            }
        }
        Toast.makeText(context, "Open TV Settings > Device Preferences > Screen Saver", Toast.LENGTH_LONG).show()
    }
}
