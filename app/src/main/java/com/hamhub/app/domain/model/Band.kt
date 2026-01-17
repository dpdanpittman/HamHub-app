package com.hamhub.app.domain.model

enum class Band(val display: String, val meters: String, val frequencyRange: String) {
    BAND_160M("160m", "160", "1.8-2.0 MHz"),
    BAND_80M("80m", "80", "3.5-4.0 MHz"),
    BAND_60M("60m", "60", "5.3-5.4 MHz"),
    BAND_40M("40m", "40", "7.0-7.3 MHz"),
    BAND_30M("30m", "30", "10.1-10.15 MHz"),
    BAND_20M("20m", "20", "14.0-14.35 MHz"),
    BAND_17M("17m", "17", "18.068-18.168 MHz"),
    BAND_15M("15m", "15", "21.0-21.45 MHz"),
    BAND_12M("12m", "12", "24.89-24.99 MHz"),
    BAND_10M("10m", "10", "28.0-29.7 MHz"),
    BAND_6M("6m", "6", "50-54 MHz"),
    BAND_2M("2m", "2", "144-148 MHz"),
    BAND_70CM("70cm", "70cm", "420-450 MHz");

    companion object {
        fun fromString(value: String): Band? {
            return entries.find {
                it.display.equals(value, ignoreCase = true) ||
                it.meters.equals(value, ignoreCase = true) ||
                it.name.equals(value, ignoreCase = true)
            }
        }

        val hfBands = listOf(BAND_160M, BAND_80M, BAND_60M, BAND_40M, BAND_30M, BAND_20M, BAND_17M, BAND_15M, BAND_12M, BAND_10M)
        val vhfUhfBands = listOf(BAND_6M, BAND_2M, BAND_70CM)
    }
}
