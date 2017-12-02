package pub.edholm.eiscprest.queues

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pub.edholm.eiscprest.services.StateService

@Component
class CommandProcessor(private val inputQueue: InputQueue,
                       private val currentState: StateService,
                       private val log: Logger = LoggerFactory.getLogger(CommandProcessor::class.java)) : Thread("commandProcessor") {

  override fun run() {
    while (true) {
      val lastCommand = inputQueue.pop()
      log.trace("Processing $lastCommand")
      currentState.updateState(lastCommand)
    }
  }
}