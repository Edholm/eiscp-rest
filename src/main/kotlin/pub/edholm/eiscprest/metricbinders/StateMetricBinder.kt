package pub.edholm.eiscprest.metricbinders

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Tag
import io.micrometer.core.instrument.binder.MeterBinder
import org.springframework.stereotype.Component
import pub.edholm.eiscprest.domain.InputSelection
import pub.edholm.eiscprest.services.StateService

@Component
class CurrentStateMeterBinder(private val stateService: StateService) : MeterBinder {

  private fun Boolean?.toDouble(): Double = if (this == true) 1.0 else 0.0

  override fun bindTo(registry: MeterRegistry) {
    registry.gauge("eiscp-rest.state.volume", stateService, { it.current().masterVolume?.level?.toDouble() ?: 0.0 })
    registry.gauge("eiscp-rest.state.muted", stateService, { it.current().isMuted.toDouble() })
    registry.gauge("eiscp-rest.state.powered", stateService, { it.current().isPowered.toDouble() })

    InputSelection.AVAILABLE.forEach { selection ->
      registry.gauge(
        "eiscp-rest.state.input.selection",
        listOf(Tag.of("inputSelection", selection.input)),
        stateService,
        { (it.current().currentInput == selection).toDouble() })
    }
  }
}