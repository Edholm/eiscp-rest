package pub.edholm.eiscprest.web

import org.apache.log4j.Logger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pub.edholm.eiscprest.eiscp.Command
import pub.edholm.eiscprest.eiscp.ISCPCommand
import pub.edholm.eiscprest.queues.OutputQueue

@RestController
@RequestMapping("/volume")
class VolumeController(private val outputQueue: OutputQueue,
                       private val log: Logger = Logger.getLogger(VolumeController::class.java)) {
  @GetMapping
  fun currentVolume(): Int {
    log.trace("Fetch current volume")
    outputQueue.put(ISCPCommand(Command.MASTER_VOLUME, "QSTN"))
    return 0 // Return actual value
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
    return false // TODO: return actual value
  }

  @PostMapping("/toggle-mute")
  fun toggleMute() {
    log.trace("Toggle mute")
    outputQueue.put(ISCPCommand(Command.AUDIO_MUTING, "TG"))
  }

}