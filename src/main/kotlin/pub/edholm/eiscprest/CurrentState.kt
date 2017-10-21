package pub.edholm.eiscprest

import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Qualifier
import pub.edholm.eiscprest.eiscp.Command
import pub.edholm.eiscprest.eiscp.ISCPCommand
import java.nio.channels.InterruptedByTimeoutException
import java.time.Instant
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

class CurrentState(val state: MutableMap<String, Any> = mutableMapOf("lastUpdated" to Instant.now()),
                   @Qualifier("updateStateLock")
                   private val update: ReentrantLock,
                   private val valueUpdatedLock: BlockingQueue<Boolean> = ArrayBlockingQueue(1),
                   private val log: Logger = Logger.getLogger(CurrentState::class.java)) {

  fun updateState(cmd: ISCPCommand) {
    update.withLock {
      when (cmd.command) {
        Command.MASTER_VOLUME -> updateMasterVolume(cmd)
        Command.AUDIO_MUTING -> updateMuted(cmd)
        Command.POWER -> updatePower(cmd)
        Command.INPUT_SELECTOR -> updateInputSelector(cmd)
        else -> {
          log.trace("Ignoring unknown command: $cmd")
          return
        }
      }
      valueUpdatedLock.offer(true, 0, TimeUnit.SECONDS)
      updateLastUpdatedTimestamp()
    }
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

  private fun waitUntilValueExists(key: String): Any {
    while (state[key] == null) {
      log.trace("Value for key=$key does not exist yet, blocking...")
      if (valueUpdatedLock.poll(5, TimeUnit.SECONDS) == null) {
        throw InterruptedByTimeoutException()
      }
    }
    val value = state[key]
    log.trace("Found $key=$value")
    return value as Any
  }

  operator fun get(key: String): Any {
    return waitUntilValueExists(key)
  }
}