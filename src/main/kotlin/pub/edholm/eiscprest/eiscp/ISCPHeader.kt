package pub.edholm.eiscprest.eiscp

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

data class ISCPHeader(val magicStr: String = DEFAULT_MAGIC_STR,
                      val headerSize: Int = DEFAULT_SIZE,
                      val messageSize: Int,
                      val version: Byte = DEFAULT_VERSION) {
  companion object {
    const val DEFAULT_MAGIC_STR = "ISCP"
    const val DEFAULT_SIZE = 16
    const val DEFAULT_VERSION: Byte = 0x01

    fun valueOf(headerBytes: ByteArray): ISCPHeader {
      val bb = ByteBuffer.wrap(headerBytes)
      val magicStr = String(headerBytes.copyOfRange(0, 4), StandardCharsets.UTF_8)
      val headerSize = bb.getInt(4)
      val messageSize = bb.getInt(8)
      val version = bb.get(12)

      return ISCPHeader(magicStr, headerSize, messageSize, version)
    }
  }

  /** Includes three reserved bytes at the end */
  fun toByteArray(): ByteArray {
    val bb = ByteBuffer.allocate(headerSize)
    magicStr.forEach { bb.put(it.toByte()) }
    bb.putInt(headerSize)
    bb.putInt(messageSize)
    bb.put(version)
    bb.put(0x0) // Reserved bytes
    bb.put(0x0)
    bb.put(0x0)
    return bb.array()
  }

  fun isValid(): Boolean {
    return headerSize == DEFAULT_SIZE &&
      version == DEFAULT_VERSION &&
      magicStr == DEFAULT_MAGIC_STR
  }
}