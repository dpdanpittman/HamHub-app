package com.hamhub.app.ui.screens.contests

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hamhub.app.data.remote.api.ContestsApi
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

data class ContestEvent(
    val title: String,
    val start: Date?,
    val end: Date?,
    val description: String?,
    val url: String?
)

data class ContestsUiState(
    val events: List<ContestEvent> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class ContestsViewModel @Inject constructor(
    private val contestsApi: ContestsApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(ContestsUiState())
    val uiState: StateFlow<ContestsUiState> = _uiState.asStateFlow()

    init {
        loadContests()
    }

    fun loadContests() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            try {
                val icalData = contestsApi.getContestCalendar()
                val events = parseICal(icalData)
                _uiState.value = ContestsUiState(
                    events = events,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "Failed to load contests"
                )
            }
        }
    }

    fun refresh() {
        loadContests()
    }

    /**
     * Parse iCal format for contests.
     * Based on the web app's parseICal function.
     */
    private fun parseICal(ical: String): List<ContestEvent> {
        val events = mutableListOf<ContestEvent>()
        val eventRegex = Regex("BEGIN:VEVENT([\\s\\S]*?)END:VEVENT")

        for (match in eventRegex.findAll(ical)) {
            val eventData = match.groupValues[1]

            val summary = getICalValue(eventData, "SUMMARY")
            val dtstart = getICalValue(eventData, "DTSTART")
            val dtend = getICalValue(eventData, "DTEND")
            val description = getICalValue(eventData, "DESCRIPTION")
            val url = getICalValue(eventData, "URL")

            if (summary != null && dtstart != null) {
                val startDate = parseICalDate(dtstart)
                val endDate = dtend?.let { parseICalDate(it) }

                events.add(
                    ContestEvent(
                        title = summary
                            .replace("\\,", ",")
                            .replace("\\n", " ")
                            .trim(),
                        start = startDate,
                        end = endDate,
                        description = description
                            ?.replace("\\n", "\n")
                            ?.replace("\\,", ",")
                            ?.trim(),
                        url = url
                    )
                )
            }
        }

        // Sort by start date
        events.sortBy { it.start }

        // Filter to upcoming events only
        val now = Date()
        return events.filter { it.start != null && it.start >= now }
    }

    private fun getICalValue(eventData: String, prop: String): String? {
        // Handle both simple properties and those with parameters (like DTSTART;VALUE=DATE:20240115)
        val regex = Regex("$prop[^:]*:([^\\r\\n]+)")
        val match = regex.find(eventData)
        return match?.groupValues?.get(1)?.trim()
    }

    private fun parseICalDate(dateStr: String): Date? {
        return try {
            if (dateStr.contains("T")) {
                // DateTime format: 20240115T180000Z
                val year = dateStr.substring(0, 4).toInt()
                val month = dateStr.substring(4, 6).toInt() - 1
                val day = dateStr.substring(6, 8).toInt()
                val hour = dateStr.substring(9, 11).toInt()
                val minute = dateStr.substring(11, 13).toInt()

                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.set(year, month, day, hour, minute, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.time
            } else {
                // Date only format: 20240115
                val year = dateStr.substring(0, 4).toInt()
                val month = dateStr.substring(4, 6).toInt() - 1
                val day = dateStr.substring(6, 8).toInt()

                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.set(year, month, day, 0, 0, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.time
            }
        } catch (e: Exception) {
            null
        }
    }
}
