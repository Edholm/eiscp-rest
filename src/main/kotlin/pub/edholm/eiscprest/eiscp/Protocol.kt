package pub.edholm.eiscprest.eiscp

import java.nio.ByteBuffer
import java.nio.ByteOrder

enum class Type(val payload: String) {
    POWER_OFF("!1PWR00"),
    POWER_ON("!1PWR01"),
    POWER_QUERY("!1PWRQSTN")
}

data class Command(val cmdType: Type = Type.POWER_QUERY) {
    companion object {
        private const val MESSAGE_HEADER_SIZE = 16
        private const val CR: Byte = 0x0D
        private const val LF: Byte = 0x0A
    }
    fun toByteArray(): ByteArray {
        val payloadByteArray = cmdType.payload.toByteArray()
        val payloadSize: Byte = (payloadByteArray.size + 2).toByte()

        val buffer = ByteBuffer
                .allocate(payloadSize + MESSAGE_HEADER_SIZE)
                .order(ByteOrder.BIG_ENDIAN)
        buffer.put("ISCP".toByteArray())
        buffer.put(0)
        buffer.put(0)
        buffer.put(0)
        buffer.put(0x10)
        // Data size
        buffer.put(0)
        buffer.put(0)
        buffer.put(0)
        buffer.put(payloadSize)
        // Protocol version
        buffer.put(1)
        buffer.put(0)
        buffer.put(0)
        buffer.put(0)
        buffer.put(payloadByteArray)
        buffer.put(CR)
        buffer.put(LF)

        return buffer.array()
    }
}
