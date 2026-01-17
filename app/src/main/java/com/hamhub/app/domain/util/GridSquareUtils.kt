package com.hamhub.app.domain.util

/**
 * Utility functions for Maidenhead grid square calculations.
 * Grid squares are used in amateur radio to specify locations.
 *
 * Format: AA00aa (e.g., FN31pr)
 * - First 2 chars: Field (A-R, A-R) - 20° x 10° squares
 * - Next 2 digits: Square (0-9, 0-9) - 2° x 1° squares
 * - Optional 2 chars: Subsquare (a-x, a-x) - 5' x 2.5' squares
 */
object GridSquareUtils {

    data class LatLng(val latitude: Double, val longitude: Double)

    /**
     * Convert a grid square to latitude/longitude coordinates.
     * Returns the center point of the grid square.
     */
    fun gridToLatLng(grid: String): LatLng? {
        val cleanGrid = grid.uppercase().trim()

        if (cleanGrid.length < 4) return null

        return try {
            // Field (first 2 characters)
            val fieldLon = cleanGrid[0] - 'A'
            val fieldLat = cleanGrid[1] - 'A'

            if (fieldLon !in 0..17 || fieldLat !in 0..17) return null

            // Square (next 2 digits)
            val squareLon = cleanGrid[2].digitToInt()
            val squareLat = cleanGrid[3].digitToInt()

            if (squareLon !in 0..9 || squareLat !in 0..9) return null

            // Calculate base coordinates
            var longitude = (fieldLon * 20.0) + (squareLon * 2.0) - 180.0
            var latitude = (fieldLat * 10.0) + (squareLat * 1.0) - 90.0

            // Subsquare (optional 2 characters)
            if (cleanGrid.length >= 6) {
                val subLon = cleanGrid[4].uppercaseChar() - 'A'
                val subLat = cleanGrid[5].uppercaseChar() - 'A'

                if (subLon in 0..23 && subLat in 0..23) {
                    longitude += (subLon * 5.0 / 60.0)
                    latitude += (subLat * 2.5 / 60.0)

                    // Center of subsquare
                    longitude += 2.5 / 60.0
                    latitude += 1.25 / 60.0
                }
            } else {
                // Center of square (no subsquare)
                longitude += 1.0
                latitude += 0.5
            }

            LatLng(latitude, longitude)
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Convert latitude/longitude to a grid square.
     */
    fun latLngToGrid(latitude: Double, longitude: Double, precision: Int = 6): String {
        var lat = latitude + 90.0
        var lon = longitude + 180.0

        val builder = StringBuilder()

        // Field
        builder.append(('A' + (lon / 20.0).toInt()))
        builder.append(('A' + (lat / 10.0).toInt()))

        if (precision >= 4) {
            lon %= 20.0
            lat %= 10.0

            // Square
            builder.append((lon / 2.0).toInt())
            builder.append((lat / 1.0).toInt())
        }

        if (precision >= 6) {
            lon %= 2.0
            lat %= 1.0

            // Subsquare
            builder.append(('a' + (lon * 12.0).toInt()))
            builder.append(('a' + (lat * 24.0).toInt()))
        }

        return builder.toString()
    }

    /**
     * Validate a grid square string.
     */
    fun isValidGrid(grid: String): Boolean {
        val cleanGrid = grid.uppercase().trim()

        return when (cleanGrid.length) {
            4 -> {
                cleanGrid[0] in 'A'..'R' &&
                cleanGrid[1] in 'A'..'R' &&
                cleanGrid[2].isDigit() &&
                cleanGrid[3].isDigit()
            }
            6 -> {
                cleanGrid[0] in 'A'..'R' &&
                cleanGrid[1] in 'A'..'R' &&
                cleanGrid[2].isDigit() &&
                cleanGrid[3].isDigit() &&
                cleanGrid[4] in 'A'..'X' &&
                cleanGrid[5] in 'A'..'X'
            }
            else -> false
        }
    }

    /**
     * Calculate approximate distance between two grid squares in kilometers.
     */
    fun distanceBetweenGrids(grid1: String, grid2: String): Double? {
        val pos1 = gridToLatLng(grid1) ?: return null
        val pos2 = gridToLatLng(grid2) ?: return null

        return haversineDistance(pos1.latitude, pos1.longitude, pos2.latitude, pos2.longitude)
    }

    /**
     * Calculate distance between two points using Haversine formula.
     * Returns distance in kilometers.
     */
    private fun haversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0 // Earth's radius in kilometers

        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)

        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)

        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))

        return R * c
    }
}
