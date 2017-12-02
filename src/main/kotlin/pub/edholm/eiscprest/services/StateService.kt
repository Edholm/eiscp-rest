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

  fun requestStateUpdateOn(vararg commands: Command) {
    if (commands.isEmpty()) {
      return
    }
    log.trace("Request state update using ${commands.asList()}")
    commands.forEach {
      outputQueue.put(ISCPCommand(it, "QSTN"))
    }
  }

  fun requestFullStateUpdate() {
    requestStateUpdateOn(Command.AUDIO_MUTING, Command.MASTER_VOLUME, Command.POWER, Command.INPUT_SELECTOR)
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