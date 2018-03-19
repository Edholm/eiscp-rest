package pub.edholm.eiscprest.eiscp

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

enum class Command(val asString: String, val payloadDesc: Map<String, String> = mapOf()) {
  POWER("!1PWR", mapOf("00" to "off", "01" to "on", "QSTN" to "query")),
  MASTER_VOLUME("!1MVL"),
  AUDIO_MUTING("!1AMT", mapOf("00" to "off", "01" to "on", "QSTN" to "query", "TG" to "toggle")),
  INPUT_SELECTOR(
    "!1SLI",
    mapOf(
      "01" to "CBL/SAT",
      "03" to "AUX",
      "05" to "PC",
      "0D" to "GAME",
      "10" to "BD/DVD",
      "11" to "STRM_BOX",
      "12" to "TV",
      "22" to "PHONO",
      "23" to "CD",
      "24" to "TUNER",
      "2B" to "NET",
      "2E" to "Bluetooth"
    )
  ),
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
  UPDATE(
    "!1UPD",
    mapOf("00" to "NO_NEW_FIRMWARE", "01" to "EXIST_NEW_FIRMWARE", "CMP" to "UPDATE_COMPLETE", "QSTN" to "query")
  ),

  UNKNOWN("<N/A>");

  companion object {

    fun parse(cmd: String) = Command
      .values()
      .firstOrNull {
        it.asString == cmd
      } ?: UNKNOWN
  }
}

data class ISCPCommand(
  val command: Command = Command.UNKNOWN,
  val payload: String = "",
  val rawCommand: String = command.asString
) {
  companion object {
    private val log: Logger = LoggerFactory.getLogger(ISCPCommand::class.java)
    private const val SUFFIX_LEN = 2
    private const val CMD_LENGTH = 5
    private const val CR: Byte = 0x0D
    private const val LF: Byte = 0x0A

    fun valueOf(bytes: ByteArray): ISCPCommand {
      val rawCmd = String(bytes.copyOfRange(0, CMD_LENGTH), StandardCharsets.UTF_8)
      val payload = String(bytes.copyOfRange(CMD_LENGTH, bytes.size - SUFFIX_LEN - 1), StandardCharsets.UTF_8)
      val cmd = Command.parse(rawCmd)
      if (cmd == Command.UNKNOWN) {
        log.debug("Unknown command: '$rawCmd'")
      }

      return ISCPCommand(cmd, payload, rawCmd)
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