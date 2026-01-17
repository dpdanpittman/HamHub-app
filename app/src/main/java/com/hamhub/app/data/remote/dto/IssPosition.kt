package com.hamhub.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IssPosition(
    val message: String = "",
    val timestamp: Long = 0,
    @SerialName("iss_position")
    val issPosition: IssCoordinates = IssCoordinates()
)

@Serializable
data class IssCoordinates(
    val latitude: String = "0",
    val longitude: String = "0"
) {
    val lat: Double get() = latitude.toDoubleOrNull() ?: 0.0
    val lon: Double get() = longitude.toDoubleOrNull() ?: 0.0
}
