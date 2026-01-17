package com.hamhub.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

/**
 * Utility for safely opening URLs in the browser.
 * Validates URL scheme to prevent opening potentially dangerous URIs
 * like javascript:, data:, file:, etc.
 */
object SafeUrlOpener {

    private val ALLOWED_SCHEMES = setOf("http", "https")

    /**
     * Safely open a URL in the default browser.
     * Only allows http:// and https:// URLs.
     *
     * @param context Android context
     * @param url The URL to open
     * @return true if the URL was opened, false if it was blocked or invalid
     */
    fun openUrl(context: Context, url: String?): Boolean {
        if (url.isNullOrBlank()) {
            return false
        }

        val uri = try {
            Uri.parse(url)
        } catch (e: Exception) {
            Toast.makeText(context, "Invalid URL", Toast.LENGTH_SHORT).show()
            return false
        }

        val scheme = uri.scheme?.lowercase()
        if (scheme !in ALLOWED_SCHEMES) {
            Toast.makeText(context, "Cannot open this type of link", Toast.LENGTH_SHORT).show()
            return false
        }

        return try {
            val intent = Intent(Intent.ACTION_VIEW, uri)
            context.startActivity(intent)
            true
        } catch (e: Exception) {
            Toast.makeText(context, "Could not open link", Toast.LENGTH_SHORT).show()
            false
        }
    }
}
