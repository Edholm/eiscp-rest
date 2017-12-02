package pub.edholm.eiscprest.web

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import pub.edholm.eiscprest.services.StateService
import pub.edholm.eiscprest.domain.InputSelection
import pub.edholm.eiscprest.eiscp.Command
import pub.edholm.eiscprest.eiscp.ISCPCommand
import pub.edholm.eiscprest.queues.OutputQueue

@RestController
@RequestMapping("/input")
class InputSelectorController(private val outputQueue: OutputQueue,
                              private val stateService: StateService,
                              private val log: Logger = LoggerFactory.getLogger(InputSelectorController::class.java)) {
  @GetMapping
  fun getCurrentInput(): InputSelection? {
    log.trace("Get current input")
    return stateService.current().currentInput
  }

  @PostMapping("/next")
  fun switchInputUp() {
    log.trace("Switch to next input")
    outputQueue.put(ISCPCommand(Command.INPUT_SELECTOR, "UP"))
  }

  @PostMapping("/previous")
  fun switchInputDown() {
    log.trace("Switch to previous input")
    outputQueue.put(ISCPCommand(Command.INPUT_SELECTOR, "DOWN"))
  }

  @PostMapping("/{newInput}")
  fun switchInput(@PathVariable newInput: String) {
    log.trace("Switch input to $newInput")
    val input = lookupInputFromDescription(newInput)
    outputQueue.put(ISCPCommand(Command.INPUT_SELECTOR, input))
  }

  @GetMapping("/available")
  fun getAvailableInputs(): Collection<String> {
    log.trace("Get available inputs")
    return Command.INPUT_SELECTOR.payloadDesc.values
  }

  private fun lookupInputFromDescription(inputDesc: String): String {
    return Command.INPUT_SELECTOR.payloadDesc
      .entries
      .filter {
        it.value == inputDesc
      }
      .reduce { _, _ ->
        throw IllegalArgumentException("$inputDesc corresponds to multiple inputs")
      }
      .key
  }
}