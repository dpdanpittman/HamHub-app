package com.hamhub.app.domain.model

enum class Mode(val display: String, val category: ModeCategory) {
    SSB("SSB", ModeCategory.PHONE),
    LSB("LSB", ModeCategory.PHONE),
    USB("USB", ModeCategory.PHONE),
    AM("AM", ModeCategory.PHONE),
    FM("FM", ModeCategory.PHONE),
    CW("CW", ModeCategory.CW),
    FT8("FT8", ModeCategory.DIGITAL),
    FT4("FT4", ModeCategory.DIGITAL),
    JS8("JS8", ModeCategory.DIGITAL),
    RTTY("RTTY", ModeCategory.DIGITAL),
    PSK31("PSK31", ModeCategory.DIGITAL),
    PSK63("PSK63", ModeCategory.DIGITAL),
    OLIVIA("Olivia", ModeCategory.DIGITAL),
    SSTV("SSTV", ModeCategory.IMAGE),
    DIGITAL("Digital", ModeCategory.DIGITAL);

    companion object {
        fun fromString(value: String): Mode? {
            return entries.find {
                it.display.equals(value, ignoreCase = true) ||
                it.name.equals(value, ignoreCase = true)
            }
        }

        val commonModes = listOf(SSB, CW, FT8, FT4, FM, RTTY)
    }
}

enum class ModeCategory {
    PHONE, CW, DIGITAL, IMAGE
}
