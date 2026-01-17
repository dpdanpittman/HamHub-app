package com.hamhub.app.data.remote.dto

data class PropagationData(
    val solarFluxIndex: Int = 0,
    val sunspotNumber: Int = 0,
    val aIndex: Int = 0,
    val kIndex: Int = 0,
    val xrayFlare: String = "",
    val geomagField: String = "",
    val signalNoise: String = "",
    val updated: String = "",
    val bandConditions: Map<String, BandCondition> = emptyMap()
)

data class BandCondition(
    val band: String,
    val dayCondition: String,
    val nightCondition: String
)

/**
 * Parser for the HamQSL solar XML data.
 */
object PropagationParser {

    fun parse(xmlContent: String): PropagationData {
        return try {
            val sfi = extractValue(xmlContent, "solarflux")?.toIntOrNull() ?: 0
            val ssn = extractValue(xmlContent, "sunspots")?.toIntOrNull() ?: 0
            val aIndex = extractValue(xmlContent, "aindex")?.toIntOrNull() ?: 0
            val kIndex = extractValue(xmlContent, "kindex")?.toIntOrNull() ?: 0
            val xray = extractValue(xmlContent, "xray") ?: ""
            val geoMag = extractValue(xmlContent, "geomagfield") ?: ""
            val signalNoise = extractValue(xmlContent, "signalnoise") ?: ""
            val updated = extractValue(xmlContent, "updated") ?: ""

            // Parse band conditions
            val bandConditions = mutableMapOf<String, BandCondition>()

            // Extract band data from calculatedconditions
            val bands = listOf("80m-40m", "30m-20m", "17m-15m", "12m-10m")
            bands.forEach { band ->
                val dayPattern = """<band name="$band" time="day">([^<]+)</band>""".toRegex()
                val nightPattern = """<band name="$band" time="night">([^<]+)</band>""".toRegex()

                val day = dayPattern.find(xmlContent)?.groupValues?.get(1) ?: "Unknown"
                val night = nightPattern.find(xmlContent)?.groupValues?.get(1) ?: "Unknown"

                bandConditions[band] = BandCondition(band, day, night)
            }

            PropagationData(
                solarFluxIndex = sfi,
                sunspotNumber = ssn,
                aIndex = aIndex,
                kIndex = kIndex,
                xrayFlare = xray,
                geomagField = geoMag,
                signalNoise = signalNoise,
                updated = updated,
                bandConditions = bandConditions
            )
        } catch (e: Exception) {
            PropagationData()
        }
    }

    private fun extractValue(xml: String, tag: String): String? {
        val pattern = """<$tag>([^<]*)</$tag>""".toRegex()
        return pattern.find(xml)?.groupValues?.get(1)
    }
}
