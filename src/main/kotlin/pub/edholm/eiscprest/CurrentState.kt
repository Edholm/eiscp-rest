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

class CurrentState(private val state: MutableMap<String, Any> = mutableMapOf("lastUpdated" to Instant.now()),
                   @Qualifier("updateStateLock")
                   private val update: ReentrantLock,
                   private val valueUpdatedLock: BlockingQueue<Boolean> = ArrayBlockingQueue(1),
                   private val log: Logger = Logger.getLogger(CurrentState::class.java)) {

  companion object {
    private const val MASTER_VOLUME = "masterVolume"
    private const val IS_MUTED = "isMuted"
    private const val IS_POWERED = "isPowered"
    private const val CURRENT_INPUT = "currentInput"
    private const val LAST_UPDATED = "lastUpdated"
  }

  fun current(): Map<String, Any> {
    waitUntilValuesExists(MASTER_VOLUME, IS_MUTED, IS_POWERED, CURRENT_INPUT)
    log.trace("Returning current state: $state")
    return state.toMap()
  }

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

  fun clearState(vararg keysToClear: String) {
    if (keysToClear.size == 0) {
      log.debug("Clear previous state")
      state.clear()
      return
    }

    log.debug("Clearing the following states: $keysToClear")
    keysToClear.forEach {
      state.remove(it)
    }
  }

  private fun updateMasterVolume(volume: ISCPCommand) {
    state[MASTER_VOLUME] = volume.payload.toInt(16)
  }

  private fun updateMuted(muted: ISCPCommand) {
    state[IS_MUTED] = muted.payload == "01"
  }

  private fun updatePower(powerState: ISCPCommand) {
    state[IS_POWERED] = powerState.payload == "01"
  }

  private fun updateInputSelector(inputSelector: ISCPCommand) {
    state[CURRENT_INPUT] = inputSelector.payloadDescription() ?: "UNKNOWN_INPUT"
  }

  private fun updateLastUpdatedTimestamp() {
    state[LAST_UPDATED] = Instant.now()
  }

  fun masterVolume() = this[MASTER_VOLUME] as Int
  fun isMuted() = this[IS_MUTED] as Boolean
  fun isPowered() = this[IS_POWERED] as Boolean
  fun currentInput() = this[CURRENT_INPUT] as String
  fun lastUpdated() = this[LAST_UPDATED] as String

  private fun waitUntilValuesExists(vararg keys: String) {
    keys.forEach {
      waitUntilValueExists(it)
    }
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

  private operator fun get(key: String): Any {
    return waitUntilValueExists(key)
  }
}