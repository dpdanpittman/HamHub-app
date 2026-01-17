package com.hamhub.app.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RepeaterBookResponse(
    val count: Int = 0,
    val results: List<Repeater> = emptyList()
)

@Serializable
data class Repeater(
    @SerialName("State ID")
    @Serializable(with = FlexibleStringSerializer::class)
    val stateId: String = "",

    @SerialName("Rptr ID")
    @Serializable(with = FlexibleStringSerializer::class)
    val repeaterId: String = "",

    @SerialName("Frequency")
    @Serializable(with = FlexibleStringSerializer::class)
    val frequency: String = "",

    @SerialName("Input Freq")
    @Serializable(with = FlexibleStringSerializer::class)
    val inputFrequency: String = "",

    @SerialName("PL")
    @Serializable(with = FlexibleStringSerializer::class)
    val pl: String = "",

    @SerialName("TSQ")
    @Serializable(with = FlexibleStringSerializer::class)
    val tsq: String = "",

    @SerialName("Callsign")
    @Serializable(with = FlexibleStringSerializer::class)
    val callsign: String = "",

    @SerialName("Location")
    @Serializable(with = FlexibleStringSerializer::class)
    val location: String = "",

    @SerialName("County")
    @Serializable(with = FlexibleStringSerializer::class)
    val county: String = "",

    @SerialName("State")
    @Serializable(with = FlexibleStringSerializer::class)
    val state: String = "",

    @SerialName("Country")
    @Serializable(with = FlexibleStringSerializer::class)
    val country: String = "",

    @SerialName("Lat")
    @Serializable(with = FlexibleStringSerializer::class)
    val latitude: String = "",

    @SerialName("Long")
    @Serializable(with = FlexibleStringSerializer::class)
    val longitude: String = "",

    @SerialName("Landmark")
    @Serializable(with = FlexibleStringSerializer::class)
    val landmark: String = "",

    @SerialName("Use")
    @Serializable(with = FlexibleStringSerializer::class)
    val use: String = "",

    @SerialName("Operational Status")
    @Serializable(with = FlexibleStringSerializer::class)
    val operationalStatus: String = "",

    @SerialName("FM Analog")
    @Serializable(with = FlexibleStringSerializer::class)
    val fmAnalog: String = "",

    @SerialName("ARES")
    @Serializable(with = FlexibleStringSerializer::class)
    val ares: String = "",

    @SerialName("RACES")
    @Serializable(with = FlexibleStringSerializer::class)
    val races: String = "",

    @SerialName("SKYWARN")
    @Serializable(with = FlexibleStringSerializer::class)
    val skywarn: String = "",

    @SerialName("CANWARN")
    @Serializable(with = FlexibleStringSerializer::class)
    val canwarn: String = "",

    @SerialName("AllStar Node")
    @Serializable(with = FlexibleStringSerializer::class)
    val allstarNode: String = "",

    @SerialName("EchoLink Node")
    @Serializable(with = FlexibleStringSerializer::class)
    val echolinkNode: String = "",

    @SerialName("IRLP Node")
    @Serializable(with = FlexibleStringSerializer::class)
    val irlpNode: String = "",

    @SerialName("Wires Node")
    @Serializable(with = FlexibleStringSerializer::class)
    val wiresNode: String = "",

    @SerialName("DMR")
    @Serializable(with = FlexibleStringSerializer::class)
    val dmr: String = "",

    @SerialName("DMR Color Code")
    @Serializable(with = FlexibleStringSerializer::class)
    val dmrColorCode: String = "",

    @SerialName("DMR ID")
    @Serializable(with = FlexibleStringSerializer::class)
    val dmrId: String = "",

    @SerialName("D-Star")
    @Serializable(with = FlexibleStringSerializer::class)
    val dstar: String = "",

    @SerialName("NXDN")
    @Serializable(with = FlexibleStringSerializer::class)
    val nxdn: String = "",

    @SerialName("APCO P-25")
    @Serializable(with = FlexibleStringSerializer::class)
    val apcoP25: String = "",

    @SerialName("P-25 NAC")
    @Serializable(with = FlexibleStringSerializer::class)
    val p25Nac: String = "",

    @SerialName("M17")
    @Serializable(with = FlexibleStringSerializer::class)
    val m17: String = "",

    @SerialName("M17 CAN")
    @Serializable(with = FlexibleStringSerializer::class)
    val m17Can: String = "",

    @SerialName("Tetra")
    @Serializable(with = FlexibleStringSerializer::class)
    val tetra: String = "",

    @SerialName("Tetra MCC")
    @Serializable(with = FlexibleStringSerializer::class)
    val tetraMcc: String = "",

    @SerialName("Tetra MNC")
    @Serializable(with = FlexibleStringSerializer::class)
    val tetraMnc: String = "",

    @SerialName("System Fusion")
    @Serializable(with = FlexibleStringSerializer::class)
    val systemFusion: String = "",

    @SerialName("YSF DG ID Uplink")
    @Serializable(with = FlexibleStringSerializer::class)
    val ysfDgIdUplink: String = "",

    @SerialName("YSF DG ID Downlink")
    @Serializable(with = FlexibleStringSerializer::class)
    val ysfDgIdDownlink: String = "",

    @SerialName("YSF DSQ")
    @Serializable(with = FlexibleStringSerializer::class)
    val ysfDsq: String = "",

    @SerialName("Notes")
    @Serializable(with = FlexibleStringSerializer::class)
    val notes: String = "",

    @SerialName("Last Update")
    @Serializable(with = FlexibleStringSerializer::class)
    val lastUpdate: String = ""
) {
    val band: String
        get() {
            val freq = frequency.toDoubleOrNull() ?: return "Unknown"
            return when {
                freq in 28.0..29.7 -> "10m"
                freq in 50.0..54.0 -> "6m"
                freq in 144.0..148.0 -> "2m"
                freq in 222.0..225.0 -> "1.25m"
                freq in 420.0..450.0 -> "70cm"
                freq in 902.0..928.0 -> "33cm"
                freq in 1240.0..1300.0 -> "23cm"
                else -> "Unknown"
            }
        }

    val offset: String
        get() {
            val outFreq = frequency.toDoubleOrNull() ?: return ""
            val inFreq = inputFrequency.toDoubleOrNull() ?: return ""
            val diff = inFreq - outFreq
            return when {
                diff > 0 -> "+%.3f MHz".format(diff)
                diff < 0 -> "%.3f MHz".format(diff)
                else -> "Simplex"
            }
        }

    val modes: List<String>
        get() = buildList {
            if (fmAnalog == "Yes") add("FM")
            if (dmr == "Yes") add("DMR")
            if (dstar == "Yes") add("D-Star")
            if (nxdn == "Yes") add("NXDN")
            if (apcoP25 == "Yes") add("P-25")
            if (m17 == "Yes") add("M17")
            if (systemFusion == "Yes") add("Fusion")
            if (tetra == "Yes") add("Tetra")
        }

    val linkedNodes: List<String>
        get() = buildList {
            if (allstarNode.isNotBlank()) add("AllStar: $allstarNode")
            if (echolinkNode.isNotBlank()) add("EchoLink: $echolinkNode")
            if (irlpNode.isNotBlank()) add("IRLP: $irlpNode")
            if (wiresNode.isNotBlank()) add("WIRES-X: $wiresNode")
        }

    val isOnline: Boolean
        get() = operationalStatus.lowercase() in listOf("on-air", "on air", "online")
}
