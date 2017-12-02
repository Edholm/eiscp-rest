package pub.edholm.eiscprest.domain

import com.fasterxml.jackson.annotation.JsonValue

data class Volume(@JsonValue val level: Int) {
  companion object {
    /** value is a hexadecimal representation of the volume */
    fun valueOf(value: String): Volume {
      return Volume(value.toInt(16))
    }
  }

  init {
    if (level < 0 || level > 100) {
      throw IllegalArgumentException("Invalid volume: Expected 0<level<100, got $level")
    }
  }

  fun toHex(): String {
    val asHex = level.toString(16)
    return when {
      asHex.length < 2 -> "0" + asHex
      else -> asHex
    }
  }
}