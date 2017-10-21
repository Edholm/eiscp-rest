package pub.edholm.eiscprest

import org.apache.log4j.Logger
import org.springframework.stereotype.Component
import pub.edholm.eiscprest.eiscp.Command
import pub.edholm.eiscprest.eiscp.ISCPCommand
import java.time.Instant

@Component
class CurrentState(private val state: MutableMap<String, Any> = mutableMapOf("lastUpdated" to Instant.now()),
                   private val log: Logger = Logger.getLogger(CurrentState::class.java)) {

  fun updateState(cmd: ISCPCommand) {
    when (cmd.command) {
      Command.MASTER_VOLUME -> updateMasterVolume(cmd)
      Command.POWER -> updatePower(cmd)
      Command.INPUT_SELECTOR -> updateInputSelector(cmd)
      else -> log.debug("Unknown cmd: $cmd")
    }

    updateLastUpdatedTimestamp()
  }

  private fun updateMasterVolume(volume: ISCPCommand) {
    state["masterVolume"] = volume.payload.toInt(16)
  }

  private fun updateMuted(muted: ISCPCommand) {
    state["isMuted"] = muted.payload == "01"
  }

  private fun updatePower(powerState: ISCPCommand) {
    state["powered"] = powerState.payload == "01"
  }

  private fun updateInputSelector(inputSelector: ISCPCommand) {
    state["input"] = inputSelector.payloadDescription() ?: "UNKNOWN_INPUT"
  }

  private fun updateLastUpdatedTimestamp() {
    state["lastUpdated"] = Instant.now()
  }

  operator fun get(key: String): Any? {
    return this.state[key]
  }
}