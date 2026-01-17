package com.hamhub.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * N2YO API response for satellite positions.
 * API Documentation: https://www.n2yo.com/api/
 */
@Serializable
data class N2yoPositionResponse(
    val info: N2yoSatelliteInfo = N2yoSatelliteInfo(),
    val positions: List<N2yoPosition> = emptyList()
)

@Serializable
data class N2yoSatelliteInfo(
    val satid: Int = 0,
    val satname: String = "",
    val transactionscount: Int = 0
)

@Serializable
data class N2yoPosition(
    val satlatitude: Double = 0.0,
    val satlongitude: Double = 0.0,
    val sataltitude: Double = 0.0,
    val azimuth: Double = 0.0,
    val elevation: Double = 0.0,
    @SerialName("ra")
    val rightAscension: Double = 0.0,
    @SerialName("dec")
    val declination: Double = 0.0,
    val timestamp: Long = 0,
    val eclipsed: Boolean = false
)

/**
 * N2YO API response for radio passes (visual passes).
 */
@Serializable
data class N2yoPassesResponse(
    val info: N2yoSatelliteInfo = N2yoSatelliteInfo(),
    val passes: List<N2yoPass> = emptyList()
)

@Serializable
data class N2yoPass(
    val startAz: Double = 0.0,
    val startAzCompass: String = "",
    val startEl: Double = 0.0,
    val startUTC: Long = 0,
    val maxAz: Double = 0.0,
    val maxAzCompass: String = "",
    val maxEl: Double = 0.0,
    val maxUTC: Long = 0,
    val endAz: Double = 0.0,
    val endAzCompass: String = "",
    val endEl: Double = 0.0,
    val endUTC: Long = 0,
    val mag: Double = 0.0,
    val duration: Int = 0
)
