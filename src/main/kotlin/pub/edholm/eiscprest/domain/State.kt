package pub.edholm.eiscprest.domain

import java.time.Instant

enum class StateField {
  MASTER_VOLUME, MUTED, POWERED, CURRENT_INPUT, ALL
}

data class State(val masterVolume: Volume? = null,
                 val isMuted: Boolean? = null,
                 val isPowered: Boolean? = null,
                 val currentInput: InputSelection? = null,
                 val lastUpdated: Instant = Instant.now()) {
  /** @return true if some values are null, else false */
  fun hasMissingFields() = masterVolume == null || isMuted == null || isPowered == null || currentInput == null
}


