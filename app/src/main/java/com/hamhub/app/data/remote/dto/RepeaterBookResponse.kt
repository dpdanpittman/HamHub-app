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
    val stateId: String = "",

    @SerialName("Rptr ID")
    val repeaterId: String = "",

    @SerialName("Frequency")
    val frequency: String = "",

    @SerialName("Input Freq")
    val inputFrequency: String = "",

    @SerialName("PL")
    val pl: String = "",

    @SerialName("TSQ")
    val tsq: String = "",

    @SerialName("Callsign")
    val callsign: String = "",

    @SerialName("Location")
    val location: String = "",

    @SerialName("County")
    val county: String = "",

    @SerialName("State")
    val state: String = "",

    @SerialName("Country")
    val country: String = "",

    @SerialName("Lat")
    val latitude: String = "",

    @SerialName("Long")
    val longitude: String = "",

    @SerialName("Landmark")
    val landmark: String = "",

    @SerialName("Use")
    val use: String = "",

    @SerialName("Operational Status")
    val operationalStatus: String = "",

    @SerialName("FM Analog")
    val fmAnalog: String = "",

    @SerialName("ARES")
    val ares: String = "",

    @SerialName("RACES")
    val races: String = "",

    @SerialName("SKYWARN")
    val skywarn: String = "",

    @SerialName("CANWARN")
    val canwarn: String = "",

    @SerialName("AllStar Node")
    val allstarNode: String = "",

    @SerialName("EchoLink Node")
    val echolinkNode: String = "",

    @SerialName("IRLP Node")
    val irlpNode: String = "",

    @SerialName("Wires Node")
    val wiresNode: String = "",

    @SerialName("DMR")
    val dmr: String = "",

    @SerialName("DMR Color Code")
    val dmrColorCode: String = "",

    @SerialName("DMR ID")
    val dmrId: String = "",

    @SerialName("D-Star")
    val dstar: String = "",

    @SerialName("NXDN")
    val nxdn: String = "",

    @SerialName("APCO P-25")
    val apcoP25: String = "",

    @SerialName("P-25 NAC")
    val p25Nac: String = "",

    @SerialName("M17")
    val m17: String = "",

    @SerialName("M17 CAN")
    val m17Can: String = "",

    @SerialName("Tetra")
    val tetra: String = "",

    @SerialName("Tetra MCC")
    val tetraMcc: String = "",

    @SerialName("Tetra MNC")
    val tetraMnc: String = "",

    @SerialName("System Fusion")
    val systemFusion: String = "",

    @SerialName("YSF DG ID Uplink")
    val ysfDgIdUplink: String = "",

    @SerialName("YSF DG ID Downlink")
    val ysfDgIdDownlink: String = "",

    @SerialName("YSF DSQ")
    val ysfDsq: String = "",

    @SerialName("Notes")
    val notes: String = "",

    @SerialName("Last Update")
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
