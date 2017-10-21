package pub.edholm.eiscprest.eiscp

import org.apache.log4j.Logger
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

enum class Command(val asString: String, val payloadDesc: Map<String, String> = mapOf()) {
  POWER("!1PWR", mapOf("00" to "off", "01" to "on", "QSTN" to "query")),
  MASTER_VOLUME("!1MVL"),
  AUDIO_MUTING("!1AMT", mapOf("00" to "off", "01" to "on", "QSTN" to "query", "TG" to "toggle")),
  INPUT_SELECTOR("!1SLI", mapOf("05" to "PC", "11" to "STRM_BOX", "QSTN" to "query")),
  LISTENING_MODE("!1LMD"),
  DIMMER_LEVEL("!1DIM"),
  MUSIC_OPTIMIZER("!1MOT"),
  CINEMA_FILTER("!1RAS"),
  PHASE_CONTROL("!1PCT"),
  INTELLI_VOLUME("!1ITV"),
  NET_DEVICE_STATUS("!1NDS"),
  NET_JACKET_ART("!1NJA"),
  NET_MENU_STATUS("!1NMS"),
  ZONE2_POWER("!1ZPW"),
  ZONE2_SELECTOR("!1SLZ"),
  UPDATE("!1UPD", mapOf("00" to "NO_NEW_FIRMWARE", "01" to "EXIST_NEW_FIRMWARE", "CMP" to "UPDATE_COMPLETE", "QSTN" to "query")),

  UNKNOWN("<N/A>");

  companion object {

    fun parse(cmd: String) = Command
      .values()
      .firstOrNull {
        it.asString == cmd
      } ?: UNKNOWN
  }
}

data class ISCPCommand(val command: Command = Command.UNKNOWN,
                       val payload: String = "") {
  companion object {
    private val log: Logger = Logger.getLogger(ISCPCommand::class.java)
    private const val SUFFIX_LEN = 2
    private const val CMD_LENGTH = 5
    private const val CR: Byte = 0x0D
    private const val LF: Byte = 0x0A

    fun valueOf(bytes: ByteArray): ISCPCommand {
      val cmd = String(bytes.copyOfRange(0, CMD_LENGTH), StandardCharsets.UTF_8)
      val payload = String(bytes.copyOfRange(CMD_LENGTH, bytes.size - SUFFIX_LEN - 1), StandardCharsets.UTF_8)
      val parsedCmd = Command.parse(cmd)
      if (parsedCmd == Command.UNKNOWN) {
        log.debug("Unknown command: '$cmd'")
      }

      return ISCPCommand(parsedCmd, payload)
    }
  }

  fun toByteArray(): ByteArray {
    assertValid()
    val bb = ByteBuffer.allocate(CMD_LENGTH + payload.length + SUFFIX_LEN)
    bb.put(command.asString.toByteArray())
    bb.put(payload.toByteArray())
    bb.put(CR)
    bb.put(LF)
    return bb.array()
  }

  fun isValid(): Boolean {
    return command != Command.UNKNOWN
  }

  fun size() = command.asString.length + payload.length

  fun payloadDescription() = command.payloadDesc[payload]

  fun assertValid() {
    if (!isValid()) {
      throw IllegalArgumentException("This command is invalid: $this")
    }
  }

  override fun toString(): String {
    return "ISCPCommand($command, payload=$payload, description=${payloadDescription()})"
  }
}