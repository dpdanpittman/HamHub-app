package com.hamhub.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CallookResponse(
    val status: String = "",
    val type: String = "",
    val current: CallookCurrent? = null,
    val previous: CallookPrevious? = null,
    val name: String = "",
    val address: CallookAddress? = null,
    val location: CallookLocation? = null,
    val otherInfo: CallookOtherInfo? = null
)

@Serializable
data class CallookCurrent(
    val callsign: String = "",
    @SerialName("operClass")
    val operClass: String = ""
)

@Serializable
data class CallookPrevious(
    val callsign: String = "",
    @SerialName("operClass")
    val operClass: String = ""
)

@Serializable
data class CallookAddress(
    val line1: String = "",
    val line2: String = "",
    val attn: String = ""
)

@Serializable
data class CallookLocation(
    val latitude: String = "",
    val longitude: String = "",
    val gridsquare: String = ""
)

@Serializable
data class CallookOtherInfo(
    val grantDate: String = "",
    val expiryDate: String = "",
    val lastActionDate: String = "",
    val frn: String = "",
    val ulsUrl: String = ""
)
