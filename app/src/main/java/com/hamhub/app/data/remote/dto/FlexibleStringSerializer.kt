package com.hamhub.app.data.remote.dto

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

/**
 * A serializer that accepts any JSON primitive (string, number, boolean) and converts it to a String.
 * This handles cases where an API returns numbers where we expect strings.
 */
object FlexibleStringSerializer : KSerializer<String> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("FlexibleString", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): String {
        return when (val jsonDecoder = decoder as? JsonDecoder) {
            null -> decoder.decodeString()
            else -> {
                val element = jsonDecoder.decodeJsonElement()
                when {
                    element is JsonPrimitive -> element.content
                    else -> element.toString()
                }
            }
        }
    }

    override fun serialize(encoder: Encoder, value: String) {
        encoder.encodeString(value)
    }
}
