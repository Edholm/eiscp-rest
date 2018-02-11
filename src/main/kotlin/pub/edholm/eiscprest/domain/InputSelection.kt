package pub.edholm.eiscprest.domain

import com.fasterxml.jackson.annotation.JsonValue

data class InputSelection(@JsonValue val input: String) {
  companion object {
    val AVAILABLE = listOf(
      InputSelection("CBL/SAT"),
      InputSelection("AUX"),
      InputSelection("PC"),
      InputSelection("GAME"),
      InputSelection("BD/DVD"),
      InputSelection("STRM_BOX"),
      InputSelection("TV"),
      InputSelection("PHONO"),
      InputSelection("CD"),
      InputSelection("TUNER"),
      InputSelection("NET"),
      InputSelection("Bluetooth")
    );
  }
}