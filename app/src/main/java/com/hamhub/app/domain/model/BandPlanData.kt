package com.hamhub.app.domain.model

data class BandAllocation(
    val startFreq: Double,
    val endFreq: Double,
    val modes: List<String>,
    val notes: String = "",
    val license: List<String> = listOf("Extra", "Advanced", "General", "Technician")
)

data class BandPlan(
    val name: String,
    val startFreq: Double,
    val endFreq: Double,
    val allocations: List<BandAllocation>
)

object BandPlanData {

    val hfBands = listOf(
        BandPlan(
            name = "160m",
            startFreq = 1.800,
            endFreq = 2.000,
            allocations = listOf(
                BandAllocation(1.800, 2.000, listOf("CW", "Phone", "Image"), "General, Advanced, Extra"),
                BandAllocation(1.800, 1.810, listOf("CW"), "Digital Modes"),
                BandAllocation(1.843, 2.000, listOf("SSB", "Phone"), "LSB"),
            )
        ),
        BandPlan(
            name = "80m",
            startFreq = 3.500,
            endFreq = 4.000,
            allocations = listOf(
                BandAllocation(3.500, 3.600, listOf("CW"), "Extra Only"),
                BandAllocation(3.525, 3.600, listOf("CW"), "General"),
                BandAllocation(3.600, 3.700, listOf("CW", "RTTY", "Data")),
                BandAllocation(3.700, 3.800, listOf("CW", "Phone"), "Extra Only"),
                BandAllocation(3.800, 3.900, listOf("CW", "Phone"), "General"),
                BandAllocation(3.900, 4.000, listOf("CW", "Phone", "Image"), "Extra, Advanced, General"),
            )
        ),
        BandPlan(
            name = "60m",
            startFreq = 5.332,
            endFreq = 5.405,
            allocations = listOf(
                BandAllocation(5.332, 5.405, listOf("CW", "Phone", "Data"), "USB, 100W ERP max"),
            )
        ),
        BandPlan(
            name = "40m",
            startFreq = 7.000,
            endFreq = 7.300,
            allocations = listOf(
                BandAllocation(7.000, 7.025, listOf("CW"), "Extra Only"),
                BandAllocation(7.025, 7.125, listOf("CW"), "General"),
                BandAllocation(7.125, 7.175, listOf("CW", "Phone"), "Extra Only"),
                BandAllocation(7.175, 7.300, listOf("CW", "Phone", "Image")),
            )
        ),
        BandPlan(
            name = "30m",
            startFreq = 10.100,
            endFreq = 10.150,
            allocations = listOf(
                BandAllocation(10.100, 10.150, listOf("CW", "RTTY", "Data"), "200W max"),
            )
        ),
        BandPlan(
            name = "20m",
            startFreq = 14.000,
            endFreq = 14.350,
            allocations = listOf(
                BandAllocation(14.000, 14.025, listOf("CW"), "Extra Only"),
                BandAllocation(14.025, 14.150, listOf("CW"), "General"),
                BandAllocation(14.150, 14.175, listOf("CW", "Phone"), "Extra Only"),
                BandAllocation(14.175, 14.225, listOf("CW", "Phone")),
                BandAllocation(14.225, 14.350, listOf("CW", "Phone", "Image")),
            )
        ),
        BandPlan(
            name = "17m",
            startFreq = 18.068,
            endFreq = 18.168,
            allocations = listOf(
                BandAllocation(18.068, 18.110, listOf("CW")),
                BandAllocation(18.110, 18.168, listOf("CW", "Phone", "Image")),
            )
        ),
        BandPlan(
            name = "15m",
            startFreq = 21.000,
            endFreq = 21.450,
            allocations = listOf(
                BandAllocation(21.000, 21.025, listOf("CW"), "Extra Only"),
                BandAllocation(21.025, 21.200, listOf("CW"), "General"),
                BandAllocation(21.200, 21.225, listOf("CW", "Phone"), "Extra Only"),
                BandAllocation(21.225, 21.275, listOf("CW", "Phone")),
                BandAllocation(21.275, 21.450, listOf("CW", "Phone", "Image")),
            )
        ),
        BandPlan(
            name = "12m",
            startFreq = 24.890,
            endFreq = 24.990,
            allocations = listOf(
                BandAllocation(24.890, 24.930, listOf("CW")),
                BandAllocation(24.930, 24.990, listOf("CW", "Phone", "Image")),
            )
        ),
        BandPlan(
            name = "10m",
            startFreq = 28.000,
            endFreq = 29.700,
            allocations = listOf(
                BandAllocation(28.000, 28.070, listOf("CW")),
                BandAllocation(28.070, 28.150, listOf("CW", "RTTY")),
                BandAllocation(28.150, 28.190, listOf("CW")),
                BandAllocation(28.190, 28.225, listOf("CW", "Beacons")),
                BandAllocation(28.225, 28.300, listOf("CW", "Beacons")),
                BandAllocation(28.300, 29.000, listOf("CW", "Phone")),
                BandAllocation(29.000, 29.200, listOf("CW", "Phone")),
                BandAllocation(29.200, 29.300, listOf("CW", "Phone"), "AM"),
                BandAllocation(29.300, 29.510, listOf("CW", "Phone", "Satellite")),
                BandAllocation(29.520, 29.590, listOf("CW", "Phone")),
                BandAllocation(29.600, 29.700, listOf("CW", "Phone", "FM", "Repeater")),
            )
        ),
    )

    val vhfUhfBands = listOf(
        BandPlan(
            name = "6m",
            startFreq = 50.000,
            endFreq = 54.000,
            allocations = listOf(
                BandAllocation(50.000, 50.100, listOf("CW"), "Beacons"),
                BandAllocation(50.060, 50.080, listOf("CW"), "Beacons"),
                BandAllocation(50.100, 50.300, listOf("CW", "SSB")),
                BandAllocation(50.300, 50.600, listOf("All Modes")),
                BandAllocation(50.600, 51.000, listOf("All Modes"), "Non-channelized"),
                BandAllocation(51.000, 52.000, listOf("FM"), "Pacific DX Window"),
                BandAllocation(52.000, 54.000, listOf("All Modes")),
            )
        ),
        BandPlan(
            name = "2m",
            startFreq = 144.000,
            endFreq = 148.000,
            allocations = listOf(
                BandAllocation(144.000, 144.050, listOf("EME")),
                BandAllocation(144.050, 144.100, listOf("CW")),
                BandAllocation(144.100, 144.200, listOf("CW", "SSB"), "Weak Signal"),
                BandAllocation(144.200, 144.275, listOf("SSB")),
                BandAllocation(144.275, 144.300, listOf("SSB"), "Beacons"),
                BandAllocation(144.300, 144.500, listOf("Satellite")),
                BandAllocation(144.500, 144.600, listOf("Linear Translator")),
                BandAllocation(144.600, 144.900, listOf("FM"), "Packet"),
                BandAllocation(144.900, 145.100, listOf("Packet"), "Packet"),
                BandAllocation(145.100, 145.500, listOf("FM"), "Repeater Inputs"),
                BandAllocation(145.500, 145.800, listOf("FM"), "Miscellaneous"),
                BandAllocation(145.800, 146.000, listOf("Satellite")),
                BandAllocation(146.000, 146.400, listOf("FM"), "Repeater Inputs"),
                BandAllocation(146.400, 146.580, listOf("FM"), "Simplex"),
                BandAllocation(146.520, 146.520, listOf("FM"), "National Simplex"),
                BandAllocation(146.580, 147.000, listOf("FM"), "Repeater Outputs"),
                BandAllocation(147.000, 147.390, listOf("FM"), "Repeater I/O"),
                BandAllocation(147.420, 147.570, listOf("FM"), "Simplex"),
                BandAllocation(147.600, 148.000, listOf("FM"), "Repeater Outputs"),
            )
        ),
        BandPlan(
            name = "1.25m",
            startFreq = 222.000,
            endFreq = 225.000,
            allocations = listOf(
                BandAllocation(222.000, 222.150, listOf("EME", "Weak Signal")),
                BandAllocation(222.000, 222.025, listOf("EME")),
                BandAllocation(222.050, 222.060, listOf("Beacons")),
                BandAllocation(222.100, 222.100, listOf("SSB"), "SSB Calling"),
                BandAllocation(222.100, 222.150, listOf("SSB")),
                BandAllocation(222.150, 222.250, listOf("CW", "SSB"), "Weak Signal"),
                BandAllocation(222.250, 223.380, listOf("FM"), "Packet, Repeaters"),
                BandAllocation(223.380, 223.520, listOf("FM"), "Simplex"),
                BandAllocation(223.500, 223.500, listOf("FM"), "Simplex Calling"),
                BandAllocation(223.520, 224.980, listOf("FM"), "Repeaters"),
                BandAllocation(224.980, 225.000, listOf("FM"), "Repeaters"),
            )
        ),
        BandPlan(
            name = "70cm",
            startFreq = 420.000,
            endFreq = 450.000,
            allocations = listOf(
                BandAllocation(420.000, 426.000, listOf("ATV")),
                BandAllocation(426.000, 432.000, listOf("ATV")),
                BandAllocation(432.000, 432.070, listOf("EME")),
                BandAllocation(432.070, 432.100, listOf("CW"), "Weak Signal"),
                BandAllocation(432.100, 432.100, listOf("CW", "SSB"), "Calling"),
                BandAllocation(432.100, 433.000, listOf("CW", "SSB"), "Mixed Mode"),
                BandAllocation(433.000, 435.000, listOf("All Modes"), "Aux/Repeater Links"),
                BandAllocation(435.000, 438.000, listOf("Satellite")),
                BandAllocation(438.000, 444.000, listOf("All Modes"), "ATV, Repeater"),
                BandAllocation(444.000, 445.000, listOf("FM"), "Repeater Inputs"),
                BandAllocation(445.000, 446.000, listOf("FM"), "Shared Non-Protected"),
                BandAllocation(446.000, 446.000, listOf("FM"), "Simplex Calling"),
                BandAllocation(446.000, 447.000, listOf("FM"), "Simplex"),
                BandAllocation(447.000, 450.000, listOf("FM"), "Repeater Outputs"),
            )
        ),
        BandPlan(
            name = "33cm",
            startFreq = 902.000,
            endFreq = 928.000,
            allocations = listOf(
                BandAllocation(902.000, 903.000, listOf("Weak Signal")),
                BandAllocation(903.000, 906.000, listOf("Digital", "Packet")),
                BandAllocation(906.000, 909.000, listOf("FM"), "Repeaters"),
                BandAllocation(909.000, 915.000, listOf("All Modes")),
                BandAllocation(915.000, 918.000, listOf("Digital")),
                BandAllocation(918.000, 921.000, listOf("FM"), "Repeaters"),
                BandAllocation(921.000, 928.000, listOf("All Modes")),
            )
        ),
        BandPlan(
            name = "23cm",
            startFreq = 1240.000,
            endFreq = 1300.000,
            allocations = listOf(
                BandAllocation(1240.000, 1246.000, listOf("ATV")),
                BandAllocation(1246.000, 1252.000, listOf("FM"), "Repeaters"),
                BandAllocation(1252.000, 1258.000, listOf("ATV")),
                BandAllocation(1258.000, 1260.000, listOf("FM")),
                BandAllocation(1260.000, 1270.000, listOf("Satellite")),
                BandAllocation(1270.000, 1276.000, listOf("FM"), "Repeaters"),
                BandAllocation(1276.000, 1282.000, listOf("ATV")),
                BandAllocation(1282.000, 1288.000, listOf("FM")),
                BandAllocation(1288.000, 1294.000, listOf("Weak Signal")),
                BandAllocation(1294.000, 1295.000, listOf("FM")),
                BandAllocation(1295.000, 1297.000, listOf("Narrow Band")),
                BandAllocation(1297.000, 1300.000, listOf("FM")),
            )
        ),
    )

    val allBands = hfBands + vhfUhfBands
}
