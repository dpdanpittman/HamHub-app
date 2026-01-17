package com.hamhub.app.domain.model

/**
 * Q-Code data with question and answer forms.
 */
data class QCode(
    val code: String,
    val question: String,
    val answer: String
)

/**
 * Morse code character representation.
 */
data class MorseCode(
    val character: String,
    val morse: String
)

/**
 * CW Prosign representation.
 */
data class Prosign(
    val sign: String,
    val morse: String,
    val meaning: String
)

/**
 * RST scale item.
 */
data class RstItem(
    val value: Int,
    val description: String
)

/**
 * Phonetic alphabet entry.
 */
data class PhoneticEntry(
    val letter: String,
    val word: String
)

/**
 * All resources data for ham radio reference.
 */
object ResourcesData {

    val qCodes = listOf(
        QCode("QRA", "What is the name of your station?", "The name of my station is..."),
        QCode("QRG", "What is my exact frequency?", "Your exact frequency is... kHz/MHz"),
        QCode("QRH", "Does my frequency vary?", "Your frequency varies"),
        QCode("QRI", "How is the tone of my transmission?", "The tone of your transmission is..."),
        QCode("QRK", "What is the readability of my signals?", "The readability of your signals is... (1-5)"),
        QCode("QRL", "Are you busy?", "I am busy, please do not interfere"),
        QCode("QRM", "Is my transmission being interfered with?", "Your transmission is being interfered with"),
        QCode("QRN", "Are you troubled by static?", "I am troubled by static"),
        QCode("QRO", "Shall I increase power?", "Increase power"),
        QCode("QRP", "Shall I decrease power?", "Decrease power"),
        QCode("QRQ", "Shall I send faster?", "Send faster (... words per minute)"),
        QCode("QRS", "Shall I send more slowly?", "Send more slowly (... words per minute)"),
        QCode("QRT", "Shall I stop sending?", "Stop sending"),
        QCode("QRU", "Have you anything for me?", "I have nothing for you"),
        QCode("QRV", "Are you ready?", "I am ready"),
        QCode("QRX", "When will you call again?", "I will call you again at... hours"),
        QCode("QRZ", "Who is calling me?", "You are being called by..."),
        QCode("QSA", "What is the strength of my signals?", "The strength of your signals is... (1-5)"),
        QCode("QSB", "Are my signals fading?", "Your signals are fading"),
        QCode("QSK", "Can you hear me between your signals?", "I can hear you between my signals (break-in)"),
        QCode("QSL", "Can you acknowledge receipt?", "I acknowledge receipt"),
        QCode("QSO", "Can you communicate with... direct?", "I can communicate with... direct"),
        QCode("QSP", "Will you relay to...?", "I will relay to..."),
        QCode("QST", "General call preceding a message addressed to all amateurs", "General call to all radio amateurs"),
        QCode("QSX", "Will you listen on... kHz?", "I am listening on... kHz"),
        QCode("QSY", "Shall I change frequency?", "Change to transmission on... kHz"),
        QCode("QTC", "How many messages have you to send?", "I have... messages for you"),
        QCode("QTH", "What is your location?", "My location is..."),
        QCode("QTR", "What is the correct time?", "The correct time is... hours")
    )

    val morseLetters = listOf(
        MorseCode("A", "·−"),
        MorseCode("B", "−···"),
        MorseCode("C", "−·−·"),
        MorseCode("D", "−··"),
        MorseCode("E", "·"),
        MorseCode("F", "··−·"),
        MorseCode("G", "−−·"),
        MorseCode("H", "····"),
        MorseCode("I", "··"),
        MorseCode("J", "·−−−"),
        MorseCode("K", "−·−"),
        MorseCode("L", "·−··"),
        MorseCode("M", "−−"),
        MorseCode("N", "−·"),
        MorseCode("O", "−−−"),
        MorseCode("P", "·−−·"),
        MorseCode("Q", "−−·−"),
        MorseCode("R", "·−·"),
        MorseCode("S", "···"),
        MorseCode("T", "−"),
        MorseCode("U", "··−"),
        MorseCode("V", "···−"),
        MorseCode("W", "·−−"),
        MorseCode("X", "−··−"),
        MorseCode("Y", "−·−−"),
        MorseCode("Z", "−−··")
    )

    val morseNumbers = listOf(
        MorseCode("0", "−−−−−"),
        MorseCode("1", "·−−−−"),
        MorseCode("2", "··−−−"),
        MorseCode("3", "···−−"),
        MorseCode("4", "····−"),
        MorseCode("5", "·····"),
        MorseCode("6", "−····"),
        MorseCode("7", "−−···"),
        MorseCode("8", "−−−··"),
        MorseCode("9", "−−−−·")
    )

    val morsePunctuation = listOf(
        MorseCode(".", "·−·−·−"),
        MorseCode(",", "−−··−−"),
        MorseCode("?", "··−−··"),
        MorseCode("'", "·−−−−·"),
        MorseCode("!", "−·−·−−"),
        MorseCode("/", "−··−·"),
        MorseCode("(", "−·−−·"),
        MorseCode(")", "−·−−·−"),
        MorseCode("&", "·−···"),
        MorseCode(":", "−−−···"),
        MorseCode(";", "−·−·−·"),
        MorseCode("=", "−···−"),
        MorseCode("+", "·−·−·"),
        MorseCode("-", "−····−"),
        MorseCode("_", "··−−·−"),
        MorseCode("\"", "·−··−·"),
        MorseCode("$", "···−··−"),
        MorseCode("@", "·−−·−·")
    )

    val prosigns = listOf(
        Prosign("AR", "·−·−·", "End of message"),
        Prosign("AS", "·−···", "Wait, stand by"),
        Prosign("BK", "−···−·−", "Break (invitation to transmit)"),
        Prosign("BT", "−···−", "Break/separator (new paragraph)"),
        Prosign("CL", "−·−··−··", "Closing station"),
        Prosign("CT", "−·−·−", "Attention/commencing transmission"),
        Prosign("SK", "···−·−", "End of contact (silent key)"),
        Prosign("SN", "···−·", "Understood/verified"),
        Prosign("SOS", "···−−−···", "Distress signal")
    )

    val rstReadability = listOf(
        RstItem(1, "Unreadable"),
        RstItem(2, "Barely readable, occasional words distinguishable"),
        RstItem(3, "Readable with considerable difficulty"),
        RstItem(4, "Readable with practically no difficulty"),
        RstItem(5, "Perfectly readable")
    )

    val rstSignalStrength = listOf(
        RstItem(1, "Faint, signals barely perceptible"),
        RstItem(2, "Very weak signals"),
        RstItem(3, "Weak signals"),
        RstItem(4, "Fair signals"),
        RstItem(5, "Fairly good signals"),
        RstItem(6, "Good signals"),
        RstItem(7, "Moderately strong signals"),
        RstItem(8, "Strong signals"),
        RstItem(9, "Extremely strong signals")
    )

    val rstTone = listOf(
        RstItem(1, "Extremely rough, hissing note"),
        RstItem(2, "Very rough AC, harsh, broad note"),
        RstItem(3, "Rough AC tone, rectified but not filtered"),
        RstItem(4, "Rough note, some trace of filtering"),
        RstItem(5, "Filtered rectified AC, strongly ripple-modulated"),
        RstItem(6, "Filtered tone, definite ripple modulation"),
        RstItem(7, "Near pure DC tone, smooth ripple"),
        RstItem(8, "Near perfect DC tone, slight ripple"),
        RstItem(9, "Perfect DC tone, no ripple or modulation")
    )

    val commonReports = listOf(
        "59" to "Perfect phone report (readability 5, signal strength 9)",
        "599" to "Perfect CW report (readability 5, signal strength 9, tone 9)",
        "55" to "Good phone report under marginal conditions",
        "559" to "Good CW report, signal not quite full scale",
        "57" to "Typical strong signal phone report",
        "579" to "Strong CW signal, not quite perfect readability"
    )

    val phoneticAlphabet = listOf(
        PhoneticEntry("A", "Alpha"),
        PhoneticEntry("B", "Bravo"),
        PhoneticEntry("C", "Charlie"),
        PhoneticEntry("D", "Delta"),
        PhoneticEntry("E", "Echo"),
        PhoneticEntry("F", "Foxtrot"),
        PhoneticEntry("G", "Golf"),
        PhoneticEntry("H", "Hotel"),
        PhoneticEntry("I", "India"),
        PhoneticEntry("J", "Juliet"),
        PhoneticEntry("K", "Kilo"),
        PhoneticEntry("L", "Lima"),
        PhoneticEntry("M", "Mike"),
        PhoneticEntry("N", "November"),
        PhoneticEntry("O", "Oscar"),
        PhoneticEntry("P", "Papa"),
        PhoneticEntry("Q", "Quebec"),
        PhoneticEntry("R", "Romeo"),
        PhoneticEntry("S", "Sierra"),
        PhoneticEntry("T", "Tango"),
        PhoneticEntry("U", "Uniform"),
        PhoneticEntry("V", "Victor"),
        PhoneticEntry("W", "Whiskey"),
        PhoneticEntry("X", "X-ray"),
        PhoneticEntry("Y", "Yankee"),
        PhoneticEntry("Z", "Zulu")
    )

    val phoneticNumbers = listOf(
        PhoneticEntry("0", "Zero"),
        PhoneticEntry("1", "One"),
        PhoneticEntry("2", "Two"),
        PhoneticEntry("3", "Three"),
        PhoneticEntry("4", "Four"),
        PhoneticEntry("5", "Five"),
        PhoneticEntry("6", "Six"),
        PhoneticEntry("7", "Seven"),
        PhoneticEntry("8", "Eight"),
        PhoneticEntry("9", "Niner")
    )
}
