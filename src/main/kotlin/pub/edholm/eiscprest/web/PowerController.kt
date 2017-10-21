package pub.edholm.eiscprest.web

import org.apache.log4j.Logger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pub.edholm.eiscprest.CurrentState
import pub.edholm.eiscprest.eiscp.CommonCommands
import pub.edholm.eiscprest.queues.OutputQueue

@RestController
@RequestMapping("/power")
class PowerController(private val outputQueue: OutputQueue,
                      private val currentState: CurrentState,
                      private val log: Logger = Logger.getLogger(PowerController::class.java)) {

  @GetMapping
  fun getPowerStatus(): Map<String, Any?> {
    log.trace("Power query")
    outputQueue.put(CommonCommands.powerStatus())
    return mapOf("powered" to currentState["powered"])
  }

  @PostMapping("/on")
  fun powerOn() {
    log.trace("Power on")
    outputQueue.put(CommonCommands.powerOn())
  }

  @PostMapping("/off")
  fun powerOff() {
    log.trace("Power off")
    outputQueue.put(CommonCommands.powerOff())
  }
}