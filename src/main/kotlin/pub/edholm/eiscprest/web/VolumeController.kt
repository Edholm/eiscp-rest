package pub.edholm.eiscprest.web

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import pub.edholm.eiscprest.services.StateService
import pub.edholm.eiscprest.eiscp.Command
import pub.edholm.eiscprest.eiscp.ISCPCommand
import pub.edholm.eiscprest.queues.OutputQueue

@RestController
@RequestMapping("/volume")
class VolumeController(private val outputQueue: OutputQueue,
                       private val currentState: StateService,
                       private val log: Logger = LoggerFactory.getLogger(VolumeController::class.java)) {
  @GetMapping
  fun currentVolume(): Int {
    log.trace("Fetch current volume")
    return currentState.current().masterVolume?.level ?: 50
  }

  @PostMapping("/{newVolume}")
  fun setVolume(@PathVariable newVolume: Int) {
    val newVolumeHex = newVolume.toTwoCharHex()
    log.trace("Set volume to $newVolume, as hex: $newVolumeHex")
    if (newVolume < 0 || newVolume > 100) {
      throw IllegalArgumentException("Invalid volume. Expected 0<x<100, got $newVolume")
    }
    outputQueue.put(ISCPCommand(Command.MASTER_VOLUME, newVolumeHex))
  }

  @PostMapping("/increase")
  fun increaseVolume() {
    log.trace("Increase volume")
    outputQueue.put(ISCPCommand(Command.MASTER_VOLUME, "UP"))
  }

  @PostMapping("/decrease")
  fun decreaseVolume() {
    log.trace("decrease volume")
    outputQueue.put(ISCPCommand(Command.MASTER_VOLUME, "DOWN"))
  }

  @GetMapping("/is-muted")
  fun isMuted(): Boolean {
    log.trace("Is muted")
    outputQueue.put(ISCPCommand(Command.AUDIO_MUTING, "QSTN"))
    return currentState.current().isPowered ?: false
  }

  @PostMapping("/toggle-mute")
  fun toggleMute() {
    log.trace("Toggle mute")
    outputQueue.put(ISCPCommand(Command.AUDIO_MUTING, "TG"))
  }

  private fun Int.toTwoCharHex(): String {
    val asHex = this.toString(16)
    return when {
      asHex.length < 2 -> "0" + asHex
      else -> asHex
    }
  }
}