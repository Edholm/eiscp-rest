package pub.edholm.eiscprest.web

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pub.edholm.eiscprest.domain.State
import pub.edholm.eiscprest.services.StateService

@RestController
@RequestMapping("/state")
class StateController(private val stateService: StateService) {
  @GetMapping
  fun currentState(): State = stateService.current()

  @GetMapping("/updates", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
  fun updateStream() = stateService.updatesStream()

  @PostMapping
  fun requestStateUpdate() = stateService.requestStateUpdate()
}