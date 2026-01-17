package com.hamhub.app.domain.model

/**
 * Channel information for radio services.
 */
data class RadioChannel(
    val channel: String,
    val frequency: String,
    val notes: String = ""
)

/**
 * Radio service information.
 */
data class RadioService(
    val name: String,
    val description: String,
    val frequencyRange: String,
    val power: String,
    val licenseRequired: Boolean,
    val licenseInfo: String,
    val channels: List<RadioChannel> = emptyList(),
    val additionalInfo: String = ""
)

/**
 * All other radio services data.
 */
object OtherServicesData {

    val services = listOf(
        RadioService(
            name = "CB Radio",
            description = "Citizens Band radio for personal and business short-distance communications",
            frequencyRange = "26.965 - 27.405 MHz",
            power = "4W AM / 12W SSB",
            licenseRequired = false,
            licenseInfo = "No license required in the US",
            channels = listOf(
                RadioChannel("1", "26.965 MHz"),
                RadioChannel("2", "26.975 MHz"),
                RadioChannel("3", "26.985 MHz"),
                RadioChannel("4", "27.005 MHz"),
                RadioChannel("5", "27.015 MHz"),
                RadioChannel("6", "27.025 MHz"),
                RadioChannel("7", "27.035 MHz"),
                RadioChannel("8", "27.055 MHz"),
                RadioChannel("9", "27.065 MHz", "Emergency channel"),
                RadioChannel("10", "27.075 MHz"),
                RadioChannel("11", "27.085 MHz"),
                RadioChannel("12", "27.105 MHz"),
                RadioChannel("13", "27.115 MHz"),
                RadioChannel("14", "27.125 MHz"),
                RadioChannel("15", "27.135 MHz"),
                RadioChannel("16", "27.155 MHz"),
                RadioChannel("17", "27.165 MHz"),
                RadioChannel("18", "27.175 MHz"),
                RadioChannel("19", "27.185 MHz", "Truckers channel"),
                RadioChannel("20", "27.205 MHz"),
                RadioChannel("21", "27.215 MHz"),
                RadioChannel("22", "27.225 MHz"),
                RadioChannel("23", "27.255 MHz"),
                RadioChannel("24", "27.235 MHz"),
                RadioChannel("25", "27.245 MHz"),
                RadioChannel("26", "27.265 MHz"),
                RadioChannel("27", "27.275 MHz"),
                RadioChannel("28", "27.285 MHz"),
                RadioChannel("29", "27.295 MHz"),
                RadioChannel("30", "27.305 MHz"),
                RadioChannel("31", "27.315 MHz"),
                RadioChannel("32", "27.325 MHz"),
                RadioChannel("33", "27.335 MHz"),
                RadioChannel("34", "27.345 MHz"),
                RadioChannel("35", "27.355 MHz"),
                RadioChannel("36", "27.365 MHz", "SSB calling"),
                RadioChannel("37", "27.375 MHz"),
                RadioChannel("38", "27.385 MHz", "SSB calling"),
                RadioChannel("39", "27.395 MHz"),
                RadioChannel("40", "27.405 MHz")
            ),
            additionalInfo = "Channel 9 is the emergency channel. Channel 19 is popular among truckers."
        ),
        RadioService(
            name = "FRS (Family Radio Service)",
            description = "License-free UHF radio service for families and recreational use",
            frequencyRange = "462 - 467 MHz",
            power = "2W max (0.5W on shared channels)",
            licenseRequired = false,
            licenseInfo = "No license required in the US",
            channels = listOf(
                RadioChannel("1", "462.5625 MHz", "Shared with GMRS"),
                RadioChannel("2", "462.5875 MHz", "Shared with GMRS"),
                RadioChannel("3", "462.6125 MHz", "Shared with GMRS"),
                RadioChannel("4", "462.6375 MHz", "Shared with GMRS"),
                RadioChannel("5", "462.6625 MHz", "Shared with GMRS"),
                RadioChannel("6", "462.6875 MHz", "Shared with GMRS"),
                RadioChannel("7", "462.7125 MHz", "Shared with GMRS"),
                RadioChannel("8", "467.5625 MHz", "FRS only, 0.5W"),
                RadioChannel("9", "467.5875 MHz", "FRS only, 0.5W"),
                RadioChannel("10", "467.6125 MHz", "FRS only, 0.5W"),
                RadioChannel("11", "467.6375 MHz", "FRS only, 0.5W"),
                RadioChannel("12", "467.6625 MHz", "FRS only, 0.5W"),
                RadioChannel("13", "467.6875 MHz", "FRS only, 0.5W"),
                RadioChannel("14", "467.7125 MHz", "FRS only, 0.5W"),
                RadioChannel("15", "462.5500 MHz", "Shared with GMRS"),
                RadioChannel("16", "462.5750 MHz", "Shared with GMRS"),
                RadioChannel("17", "462.6000 MHz", "Shared with GMRS"),
                RadioChannel("18", "462.6250 MHz", "Shared with GMRS"),
                RadioChannel("19", "462.6500 MHz", "Shared with GMRS"),
                RadioChannel("20", "462.6750 MHz", "Shared with GMRS"),
                RadioChannel("21", "462.7000 MHz", "Shared with GMRS"),
                RadioChannel("22", "462.7250 MHz", "Shared with GMRS")
            ),
            additionalInfo = "FRS radios have fixed antennas and cannot connect to repeaters."
        ),
        RadioService(
            name = "GMRS (General Mobile Radio Service)",
            description = "UHF radio service for families and businesses with higher power than FRS",
            frequencyRange = "462 - 467 MHz",
            power = "Up to 50W",
            licenseRequired = true,
            licenseInfo = "FCC license required (\$35, 10-year term). Covers immediate family.",
            channels = listOf(
                RadioChannel("1", "462.5625 MHz", "Shared with FRS"),
                RadioChannel("2", "462.5875 MHz", "Shared with FRS"),
                RadioChannel("3", "462.6125 MHz", "Shared with FRS"),
                RadioChannel("4", "462.6375 MHz", "Shared with FRS"),
                RadioChannel("5", "462.6625 MHz", "Shared with FRS"),
                RadioChannel("6", "462.6875 MHz", "Shared with FRS"),
                RadioChannel("7", "462.7125 MHz", "Shared with FRS"),
                RadioChannel("15R", "462.5500 MHz", "Repeater output"),
                RadioChannel("16R", "462.5750 MHz", "Repeater output"),
                RadioChannel("17R", "462.6000 MHz", "Repeater output"),
                RadioChannel("18R", "462.6250 MHz", "Repeater output"),
                RadioChannel("19R", "462.6500 MHz", "Repeater output"),
                RadioChannel("20R", "462.6750 MHz", "Repeater output"),
                RadioChannel("21R", "462.7000 MHz", "Repeater output"),
                RadioChannel("22R", "462.7250 MHz", "Repeater output")
            ),
            additionalInfo = "GMRS allows repeater use with 5 MHz offset. External antennas permitted."
        ),
        RadioService(
            name = "MURS (Multi-Use Radio Service)",
            description = "License-free VHF radio service for business and personal use",
            frequencyRange = "151 - 154 MHz",
            power = "2W max",
            licenseRequired = false,
            licenseInfo = "No license required in the US",
            channels = listOf(
                RadioChannel("1", "151.820 MHz"),
                RadioChannel("2", "151.880 MHz"),
                RadioChannel("3", "151.940 MHz"),
                RadioChannel("4", "154.570 MHz", "Blue Dot"),
                RadioChannel("5", "154.600 MHz", "Green Dot")
            ),
            additionalInfo = "VHF signals can travel further than UHF in open terrain. No repeaters allowed."
        ),
        RadioService(
            name = "NOAA Weather Radio",
            description = "Continuous broadcast of weather information from the National Weather Service",
            frequencyRange = "162.400 - 162.550 MHz",
            power = "N/A (Receive only)",
            licenseRequired = false,
            licenseInfo = "Receive only - no license needed",
            channels = listOf(
                RadioChannel("WX1", "162.550 MHz"),
                RadioChannel("WX2", "162.400 MHz"),
                RadioChannel("WX3", "162.475 MHz"),
                RadioChannel("WX4", "162.425 MHz"),
                RadioChannel("WX5", "162.450 MHz"),
                RadioChannel("WX6", "162.500 MHz"),
                RadioChannel("WX7", "162.525 MHz")
            ),
            additionalInfo = "SAME (Specific Area Message Encoding) alerts target specific geographic areas."
        ),
        RadioService(
            name = "NIST Time Stations",
            description = "Official US time and frequency broadcasts",
            frequencyRange = "2.5 - 20 MHz (HF)",
            power = "N/A (Receive only)",
            licenseRequired = false,
            licenseInfo = "Receive only - no license needed",
            channels = listOf(
                RadioChannel("WWV", "2.5 MHz", "Fort Collins, CO"),
                RadioChannel("WWV", "5 MHz", "Fort Collins, CO"),
                RadioChannel("WWV", "10 MHz", "Fort Collins, CO"),
                RadioChannel("WWV", "15 MHz", "Fort Collins, CO"),
                RadioChannel("WWV", "20 MHz", "Fort Collins, CO"),
                RadioChannel("WWVH", "2.5 MHz", "Hawaii"),
                RadioChannel("WWVH", "5 MHz", "Hawaii"),
                RadioChannel("WWVH", "10 MHz", "Hawaii"),
                RadioChannel("WWVH", "15 MHz", "Hawaii"),
                RadioChannel("WWVB", "60 kHz", "LF time code")
            ),
            additionalInfo = "WWV uses male voice, WWVH uses female voice. Broadcasts include GPS status and propagation info."
        ),
        RadioService(
            name = "Marine VHF",
            description = "VHF radio service for maritime communications",
            frequencyRange = "156 - 162 MHz",
            power = "1W / 25W",
            licenseRequired = true,
            licenseInfo = "Ship Station License required for documented vessels",
            channels = listOf(
                RadioChannel("6", "156.300 MHz", "Intership safety"),
                RadioChannel("9", "156.450 MHz", "Calling (commercial)"),
                RadioChannel("12", "156.600 MHz", "Port operations"),
                RadioChannel("13", "156.650 MHz", "Bridge-to-bridge navigation"),
                RadioChannel("14", "156.700 MHz", "Port operations"),
                RadioChannel("16", "156.800 MHz", "Distress, safety, calling"),
                RadioChannel("22A", "157.100 MHz", "Coast Guard liaison"),
                RadioChannel("68", "156.425 MHz", "Non-commercial"),
                RadioChannel("69", "156.475 MHz", "Non-commercial"),
                RadioChannel("70", "156.525 MHz", "Digital Selective Calling"),
                RadioChannel("71", "156.575 MHz", "Non-commercial"),
                RadioChannel("72", "156.625 MHz", "Non-commercial (popular)"),
                RadioChannel("78A", "156.925 MHz", "Non-commercial")
            ),
            additionalInfo = "Channel 16 is the international distress and calling channel. Monitor it at all times when underway."
        )
    )
}
