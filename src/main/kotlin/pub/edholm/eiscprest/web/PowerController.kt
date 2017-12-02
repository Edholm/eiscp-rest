package pub.edholm.eiscprest.web

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pub.edholm.eiscprest.services.StateService
import pub.edholm.eiscprest.eiscp.CommonCommands
import pub.edholm.eiscprest.queues.OutputQueue

@RestController
@RequestMapping("/power")
class PowerController(private val outputQueue: OutputQueue,
                      private val currentState: StateService,
                      private val log: Logger = LoggerFactory.getLogger(PowerController::class.java)) {

  @GetMapping
  fun getPowerStatus(): Boolean? {
    log.trace("Power query")
    return currentState.current().isPowered
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

  @PostMapping("/toggle")
  fun togglePower() {
    log.trace("Toggle power")

    outputQueue.put(CommonCommands.powerQuery())
    if (currentState.current().isPowered == true) {
      powerOff()
    } else {
      powerOn()
    }
  }
}