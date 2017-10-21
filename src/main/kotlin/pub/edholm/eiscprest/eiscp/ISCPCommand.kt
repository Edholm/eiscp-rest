package pub.edholm.eiscprest.eiscp

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

data class ISCPCommand(val command: String, val payload: String = "") {
  companion object {
    private const val SUFFIX_LEN = 2
    private const val CMD_LENGTH = 5
    private const val CR: Byte = 0x0D
    private const val LF: Byte = 0x0A
    fun valueOf(bytes: ByteArray): ISCPCommand {
      val cmd = String(bytes.copyOfRange(0, CMD_LENGTH), StandardCharsets.UTF_8)
      val payload = String(bytes.copyOfRange(CMD_LENGTH, bytes.size - SUFFIX_LEN - 1), StandardCharsets.UTF_8)
      return ISCPCommand(cmd, payload)
    }
  }

  fun toByteArray(): ByteArray {
    assertValid()
    val bb = ByteBuffer.allocate(CMD_LENGTH + payload.length + SUFFIX_LEN)
    bb.put(command.toByteArray())
    bb.put(payload.toByteArray())
    bb.put(CR)
    bb.put(LF)
    return bb.array()
  }

  fun isValid(): Boolean {
    return command.length == CMD_LENGTH &&
      command.substring(0, 2) == "!1"
  }

  fun getCommandWithoutPrefix(): String {
    assertValid()
    return command.substring(2, CMD_LENGTH)
  }

  fun size() = command.length + payload.length

  private fun assertValid() {
    if (!isValid()) {
      throw IllegalArgumentException("This command is invalid: $this")
    }
  }
}