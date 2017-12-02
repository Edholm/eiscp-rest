package pub.edholm.eiscprest.web

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pub.edholm.eiscprest.services.StateService
import pub.edholm.eiscprest.domain.State
import pub.edholm.eiscprest.eiscp.Command
import pub.edholm.eiscprest.eiscp.ISCPCommand
import pub.edholm.eiscprest.queues.OutputQueue

@RestController
@RequestMapping("/state")
class StateController(private val outputQueue: OutputQueue,
                      private val stateManager: StateService,
                      private val log: Logger = LoggerFactory.getLogger(StateController::class.java)) {
  @GetMapping
  fun currentState(): State = stateManager.current()

  @PostMapping
  fun requestStateUpdate() {
    log.trace("Request state update")

    outputQueue.put(ISCPCommand(Command.AUDIO_MUTING, "QSTN"))
    outputQueue.put(ISCPCommand(Command.MASTER_VOLUME, "QSTN"))
    outputQueue.put(ISCPCommand(Command.POWER, "QSTN"))
    outputQueue.put(ISCPCommand(Command.INPUT_SELECTOR, "QSTN"))
  }
}