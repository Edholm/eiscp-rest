package pub.edholm.eiscprest.web

import org.apache.log4j.Logger
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pub.edholm.eiscprest.CurrentState
import pub.edholm.eiscprest.eiscp.CommonCommands
import pub.edholm.eiscprest.queues.OutputQueue

@RestController
@RequestMapping("/input")
class InputSelectorController(private val outputQueue: OutputQueue,
                              private val currentState: CurrentState,
                              private val log: Logger = Logger.getLogger(InputSelectorController::class.java)) {
  @GetMapping
  fun getCurrentInput(): String {
    log.trace("Get current input")
    outputQueue.put(CommonCommands.inputSelectorQuery())
    return currentState.currentInput()
  }
}