package com.hamhub.app.domain.util

import kotlin.math.ln
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Antenna and electrical calculations for ham radio.
 */
object AntennaCalculations {

    // Speed of light in meters per second
    private const val SPEED_OF_LIGHT = 299792458.0

    /**
     * Calculate half-wave dipole antenna length.
     * Formula: Length (feet) = 468 / frequency (MHz)
     * @param frequencyMhz Frequency in MHz
     * @return Total length in feet
     */
    fun dipoleLengthFeet(frequencyMhz: Double): Double {
        return if (frequencyMhz > 0) 468.0 / frequencyMhz else 0.0
    }

    /**
     * Calculate half-wave dipole antenna length in meters.
     * @param frequencyMhz Frequency in MHz
     * @return Total length in meters
     */
    fun dipoleLengthMeters(frequencyMhz: Double): Double {
        return if (frequencyMhz > 0) 142.65 / frequencyMhz else 0.0
    }

    /**
     * Calculate quarter-wave vertical antenna length.
     * Formula: Length (feet) = 234 / frequency (MHz)
     * @param frequencyMhz Frequency in MHz
     * @return Length in feet
     */
    fun quarterWaveVerticalFeet(frequencyMhz: Double): Double {
        return if (frequencyMhz > 0) 234.0 / frequencyMhz else 0.0
    }

    /**
     * Calculate quarter-wave vertical antenna length in meters.
     * @param frequencyMhz Frequency in MHz
     * @return Length in meters
     */
    fun quarterWaveVerticalMeters(frequencyMhz: Double): Double {
        return if (frequencyMhz > 0) 71.32 / frequencyMhz else 0.0
    }

    /**
     * Calculate full-wave loop antenna length.
     * Formula: Length (feet) = 1005 / frequency (MHz)
     * @param frequencyMhz Frequency in MHz
     * @return Total loop length in feet
     */
    fun fullWaveLoopFeet(frequencyMhz: Double): Double {
        return if (frequencyMhz > 0) 1005.0 / frequencyMhz else 0.0
    }

    /**
     * Calculate full-wave loop antenna length in meters.
     * @param frequencyMhz Frequency in MHz
     * @return Total loop length in meters
     */
    fun fullWaveLoopMeters(frequencyMhz: Double): Double {
        return if (frequencyMhz > 0) 306.4 / frequencyMhz else 0.0
    }

    /**
     * Calculate J-Pole antenna dimensions.
     * @param frequencyMhz Frequency in MHz
     * @return Pair of (radiator length, matching stub length) in feet
     */
    fun jPoleDimensionsFeet(frequencyMhz: Double): Pair<Double, Double> {
        if (frequencyMhz <= 0) return Pair(0.0, 0.0)
        val radiator = 468.0 / frequencyMhz  // 3/4 wave
        val matchingStub = 234.0 / frequencyMhz  // 1/4 wave
        return Pair(radiator, matchingStub)
    }

    /**
     * Calculate wavelength in meters.
     * @param frequencyMhz Frequency in MHz
     * @return Wavelength in meters
     */
    fun wavelengthMeters(frequencyMhz: Double): Double {
        return if (frequencyMhz > 0) SPEED_OF_LIGHT / (frequencyMhz * 1_000_000) else 0.0
    }

    // Electrical Calculations

    /**
     * Ohm's Law: Calculate voltage.
     * V = I * R
     * @param currentAmps Current in amps
     * @param resistanceOhms Resistance in ohms
     * @return Voltage in volts
     */
    fun voltage(currentAmps: Double, resistanceOhms: Double): Double {
        return currentAmps * resistanceOhms
    }

    /**
     * Ohm's Law: Calculate current.
     * I = V / R
     * @param volts Voltage in volts
     * @param resistanceOhms Resistance in ohms
     * @return Current in amps
     */
    fun current(volts: Double, resistanceOhms: Double): Double {
        return if (resistanceOhms > 0) volts / resistanceOhms else 0.0
    }

    /**
     * Ohm's Law: Calculate resistance.
     * R = V / I
     * @param volts Voltage in volts
     * @param currentAmps Current in amps
     * @return Resistance in ohms
     */
    fun resistance(volts: Double, currentAmps: Double): Double {
        return if (currentAmps > 0) volts / currentAmps else 0.0
    }

    /**
     * Calculate power.
     * P = V * I
     * @param volts Voltage in volts
     * @param currentAmps Current in amps
     * @return Power in watts
     */
    fun power(volts: Double, currentAmps: Double): Double {
        return volts * currentAmps
    }

    /**
     * Calculate power from voltage and resistance.
     * P = V² / R
     * @param volts Voltage in volts
     * @param resistanceOhms Resistance in ohms
     * @return Power in watts
     */
    fun powerFromVoltage(volts: Double, resistanceOhms: Double): Double {
        return if (resistanceOhms > 0) (volts * volts) / resistanceOhms else 0.0
    }

    /**
     * Convert watts to dBm.
     * dBm = 10 * log10(watts * 1000)
     * @param watts Power in watts
     * @return Power in dBm
     */
    fun wattsTodBm(watts: Double): Double {
        return if (watts > 0) 10 * kotlin.math.log10(watts * 1000) else Double.NEGATIVE_INFINITY
    }

    /**
     * Convert dBm to watts.
     * watts = 10^(dBm/10) / 1000
     * @param dBm Power in dBm
     * @return Power in watts
     */
    fun dBmToWatts(dBm: Double): Double {
        return 10.0.pow(dBm / 10.0) / 1000.0
    }

    /**
     * Convert watts to dBW.
     * dBW = 10 * log10(watts)
     * @param watts Power in watts
     * @return Power in dBW
     */
    fun wattsTodBW(watts: Double): Double {
        return if (watts > 0) 10 * kotlin.math.log10(watts) else Double.NEGATIVE_INFINITY
    }

    /**
     * Calculate SWR from forward and reflected power.
     * SWR = (1 + sqrt(Pr/Pf)) / (1 - sqrt(Pr/Pf))
     * @param forwardPower Forward power in watts
     * @param reflectedPower Reflected power in watts
     * @return SWR ratio
     */
    fun swr(forwardPower: Double, reflectedPower: Double): Double {
        if (forwardPower <= 0 || reflectedPower < 0) return 1.0
        if (reflectedPower >= forwardPower) return Double.POSITIVE_INFINITY
        val ratio = sqrt(reflectedPower / forwardPower)
        return (1 + ratio) / (1 - ratio)
    }

    /**
     * Calculate return loss from SWR.
     * Return Loss (dB) = -20 * log10((SWR - 1) / (SWR + 1))
     * @param swr SWR ratio
     * @return Return loss in dB
     */
    fun returnLoss(swr: Double): Double {
        if (swr <= 1) return Double.POSITIVE_INFINITY
        return -20 * kotlin.math.log10((swr - 1) / (swr + 1))
    }

    /**
     * Calculate reflection coefficient from SWR.
     * Gamma = (SWR - 1) / (SWR + 1)
     * @param swr SWR ratio
     * @return Reflection coefficient (0 to 1)
     */
    fun reflectionCoefficient(swr: Double): Double {
        if (swr <= 1) return 0.0
        return (swr - 1) / (swr + 1)
    }
}
