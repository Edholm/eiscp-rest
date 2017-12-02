package pub.edholm.eiscprest.services

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.codec.ServerSentEvent
import org.springframework.stereotype.Service
import pub.edholm.eiscprest.domain.InputSelection
import pub.edholm.eiscprest.domain.State
import pub.edholm.eiscprest.domain.Volume
import pub.edholm.eiscprest.eiscp.Command
import pub.edholm.eiscprest.eiscp.ISCPCommand
import pub.edholm.eiscprest.queues.OutputQueue
import reactor.core.publisher.Flux
import reactor.core.publisher.ReplayProcessor
import java.time.Instant

@Service
class StateService(
  private var currentState: State = State(),
  private val outputQueue: OutputQueue,
  private val sseStateEmitter: ReplayProcessor<ServerSentEvent<State>>,
  private val log: Logger = LoggerFactory.getLogger(StateService::class.java)) {

  fun current(): State {
    log.trace("Returning current state: $currentState")
    return currentState
  }

  fun updatesStream(): Flux<ServerSentEvent<State>> = sseStateEmitter

  fun requestStateUpdate() {
    log.trace("Request full state update")
    outputQueue.put(ISCPCommand(Command.AUDIO_MUTING, "QSTN"))
    outputQueue.put(ISCPCommand(Command.MASTER_VOLUME, "QSTN"))
    outputQueue.put(ISCPCommand(Command.POWER, "QSTN"))
    outputQueue.put(ISCPCommand(Command.INPUT_SELECTOR, "QSTN"))

  }

  fun updateStateFromCommand(cmd: ISCPCommand) {
    currentState = when (cmd.command) {
      Command.MASTER_VOLUME -> currentState.copy(masterVolume = Volume.valueOf(cmd.payload))
      Command.AUDIO_MUTING -> currentState.copy(isMuted = cmd.payload == "01")
      Command.POWER -> currentState.copy(isPowered = cmd.payload == "01")
      Command.INPUT_SELECTOR -> currentState.copy(currentInput = InputSelection(cmd.payloadDescription() ?: "UNKNOWN"))
      else -> {
        log.trace("Ignoring unknown command: $cmd")
        return
      }
    }.copy(lastUpdated = Instant.now())
    sseStateEmitter.onNext(ServerSentEvent.builder(currentState).event("state_update").build())
  }
}