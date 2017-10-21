package pub.edholm.eiscprest.queues

import org.apache.log4j.Logger
import org.springframework.stereotype.Component
import pub.edholm.eiscprest.CurrentState

@Component
class CommandProcessor(private val inputQueue: InputQueue,
                       private val currentState: CurrentState,
                       private val log: Logger = Logger.getLogger(CommandProcessor::class.java)) : Thread("commandProcessor") {

  override fun run() {
    while (true) {
      val lastCommand = inputQueue.pop()
      log.trace("Processing $lastCommand")
      currentState.updateState(lastCommand)
    }
  }
}