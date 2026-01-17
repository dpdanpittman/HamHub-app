package com.hamhub.app.ui.screens.news

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamhub.app.data.remote.api.NewsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class NewsItem(
    val title: String,
    val link: String?,
    val description: String?,
    val pubDate: Date?,
    val category: String?
)

data class NewsUiState(
    val arrlNews: List<NewsItem> = emptyList(),
    val dxBulletins: List<NewsItem> = emptyList(),
    val isLoadingNews: Boolean = true,
    val isLoadingDx: Boolean = true,
    val newsError: String? = null,
    val dxError: String? = null
)

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsApi: NewsApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()

    init {
        loadNews()
        loadDxBulletins()
    }

    fun loadNews() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingNews = true, newsError = null)
            try {
                val rssData = newsApi.getArrlNews()
                val items = parseRSS(rssData)
                _uiState.value = _uiState.value.copy(
                    arrlNews = items,
                    isLoadingNews = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingNews = false,
                    newsError = e.message ?: "Failed to load news"
                )
            }
        }
    }

    fun loadDxBulletins() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoadingDx = true, dxError = null)
            try {
                val rssData = newsApi.getDxBulletins()
                val items = parseRSS(rssData)
                _uiState.value = _uiState.value.copy(
                    dxBulletins = items,
                    isLoadingDx = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoadingDx = false,
                    dxError = e.message ?: "Failed to load DX bulletins"
                )
            }
        }
    }

    fun refresh() {
        loadNews()
        loadDxBulletins()
    }

    /**
     * Parse RSS XML to NewsItem list.
     * Based on the web app's parseRSS function.
     */
    private fun parseRSS(xml: String): List<NewsItem> {
        val items = mutableListOf<NewsItem>()
        val itemRegex = Regex("<item>([\\s\\S]*?)</item>")

        for (match in itemRegex.findAll(xml)) {
            val itemXml = match.groupValues[1]

            val title = getRssValue(itemXml, "title")
            val link = getRssValue(itemXml, "link")
            val description = getRssValue(itemXml, "description")
            val pubDate = getRssValue(itemXml, "pubDate")
            val category = getRssValue(itemXml, "category")

            if (title != null) {
                items.add(
                    NewsItem(
                        title = cleanHtml(title),
                        link = link,
                        description = description?.let { cleanHtml(it) },
                        pubDate = pubDate?.let { parseRssDate(it) },
                        category = category
                    )
                )
            }
        }

        return items
    }

    private fun getRssValue(xml: String, tag: String): String? {
        // Handle CDATA sections: <tag><![CDATA[content]]></tag>
        val cdataRegex = Regex("<$tag><!\\[CDATA\\[([\\s\\S]*?)\\]\\]></$tag>")
        val cdataMatch = cdataRegex.find(xml)
        if (cdataMatch != null) {
            return cdataMatch.groupValues[1].trim()
        }

        // Handle simple tags: <tag>content</tag>
        val simpleRegex = Regex("<$tag>([^<]*)</$tag>")
        val simpleMatch = simpleRegex.find(xml)
        return simpleMatch?.groupValues?.get(1)?.trim()
    }

    private fun parseRssDate(dateStr: String): Date? {
        // RSS date format: "Thu, 15 Jan 2024 12:00:00 GMT"
        val formats = listOf(
            "EEE, dd MMM yyyy HH:mm:ss z",
            "EEE, dd MMM yyyy HH:mm:ss Z",
            "dd MMM yyyy HH:mm:ss z",
            "yyyy-MM-dd'T'HH:mm:ss'Z'"
        )

        for (format in formats) {
            try {
                val sdf = SimpleDateFormat(format, Locale.US)
                return sdf.parse(dateStr)
            } catch (_: Exception) {
                // Try next format
            }
        }
        return null
    }

    private fun cleanHtml(text: String): String {
        return text
            .replace(Regex("<[^>]+>"), "") // Remove HTML tags
            .replace("&amp;", "&")
            .replace("&lt;", "<")
            .replace("&gt;", ">")
            .replace("&quot;", "\"")
            .replace("&#39;", "'")
            .replace("&nbsp;", " ")
            .trim()
    }
}
