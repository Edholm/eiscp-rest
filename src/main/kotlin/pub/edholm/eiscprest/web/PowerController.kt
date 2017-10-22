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
    outputQueue.put(CommonCommands.powerQuery())
    return mapOf("powered" to currentState.isPowered())
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
  fun togglePower(): Map<String, Any?> {
    log.trace("Toggle power")

    currentState.clearState(CurrentState.IS_POWERED)
    outputQueue.put(CommonCommands.powerQuery())
    if (currentState.isPowered()) {
      currentState.clearState(CurrentState.IS_POWERED)
      powerOff()
    } else {
      currentState.clearState(CurrentState.IS_POWERED)
      powerOn()
    }

    return mapOf("powered" to currentState.isPowered())
  }
}