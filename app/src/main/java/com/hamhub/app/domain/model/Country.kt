package com.hamhub.app.domain.model

/**
 * Common countries for amateur radio logging.
 * Based on DXCC entity list with most commonly worked countries.
 */
enum class Country(val displayName: String, val prefix: String) {
    UNITED_STATES("United States", "K/W/N"),
    CANADA("Canada", "VE"),
    MEXICO("Mexico", "XE"),
    UNITED_KINGDOM("United Kingdom", "G"),
    GERMANY("Germany", "DL"),
    FRANCE("France", "F"),
    ITALY("Italy", "I"),
    SPAIN("Spain", "EA"),
    NETHERLANDS("Netherlands", "PA"),
    BELGIUM("Belgium", "ON"),
    SWITZERLAND("Switzerland", "HB"),
    AUSTRIA("Austria", "OE"),
    POLAND("Poland", "SP"),
    CZECH_REPUBLIC("Czech Republic", "OK"),
    SWEDEN("Sweden", "SM"),
    NORWAY("Norway", "LA"),
    FINLAND("Finland", "OH"),
    DENMARK("Denmark", "OZ"),
    IRELAND("Ireland", "EI"),
    PORTUGAL("Portugal", "CT"),
    GREECE("Greece", "SV"),
    HUNGARY("Hungary", "HA"),
    ROMANIA("Romania", "YO"),
    UKRAINE("Ukraine", "UR"),
    RUSSIA("Russia", "UA"),
    JAPAN("Japan", "JA"),
    SOUTH_KOREA("South Korea", "HL"),
    CHINA("China", "BY"),
    TAIWAN("Taiwan", "BV"),
    AUSTRALIA("Australia", "VK"),
    NEW_ZEALAND("New Zealand", "ZL"),
    BRAZIL("Brazil", "PY"),
    ARGENTINA("Argentina", "LU"),
    CHILE("Chile", "CE"),
    VENEZUELA("Venezuela", "YV"),
    COLOMBIA("Colombia", "HK"),
    PERU("Peru", "OA"),
    SOUTH_AFRICA("South Africa", "ZS"),
    INDIA("India", "VU"),
    THAILAND("Thailand", "HS"),
    PHILIPPINES("Philippines", "DU"),
    INDONESIA("Indonesia", "YB"),
    ISRAEL("Israel", "4X"),
    TURKEY("Turkey", "TA"),
    EGYPT("Egypt", "SU"),
    PUERTO_RICO("Puerto Rico", "KP4"),
    HAWAII("Hawaii", "KH6"),
    ALASKA("Alaska", "KL7"),
    US_VIRGIN_ISLANDS("US Virgin Islands", "KP2"),
    GUAM("Guam", "KH2");

    companion object {
        fun fromDisplayName(name: String): Country? {
            return entries.find { it.displayName.equals(name, ignoreCase = true) }
        }

        val commonCountries = listOf(
            UNITED_STATES,
            CANADA,
            MEXICO,
            UNITED_KINGDOM,
            GERMANY,
            FRANCE,
            JAPAN,
            AUSTRALIA
        )

        val allDisplayNames = entries.map { it.displayName }
    }
}
