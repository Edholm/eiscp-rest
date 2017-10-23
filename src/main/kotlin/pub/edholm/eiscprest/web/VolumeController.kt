package pub.edholm.eiscprest.web

import org.apache.log4j.Logger
import org.springframework.web.bind.annotation.*
import pub.edholm.eiscprest.CurrentState
import pub.edholm.eiscprest.eiscp.Command
import pub.edholm.eiscprest.eiscp.ISCPCommand
import pub.edholm.eiscprest.queues.OutputQueue

@RestController
@RequestMapping("/volume")
class VolumeController(private val outputQueue: OutputQueue,
                       private val currentState: CurrentState,
                       private val log: Logger = Logger.getLogger(VolumeController::class.java)) {
  @GetMapping
  fun currentVolume(): Int {
    log.trace("Fetch current volume")
    outputQueue.put(ISCPCommand(Command.MASTER_VOLUME, "QSTN"))
    return currentState.masterVolume()
  }

  @PostMapping("/{newVolume}")
  fun setVolume(@PathVariable newVolume: Int): Int {
    val newVolumeHex = newVolume.toTwoCharHex()
    log.trace("Set volume to $newVolume, as hex: $newVolumeHex")
    if (newVolume < 0 || newVolume > 100) {
      throw IllegalArgumentException("Invalid volume. Expected 0<x<100, got $newVolume")
    }
    outputQueue.put(ISCPCommand(Command.MASTER_VOLUME, newVolumeHex))
    return currentState.masterVolume()
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
    return currentState.isMuted()
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