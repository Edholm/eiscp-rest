package pub.edholm.eiscprest.eiscp

enum class Type(val cmd: String) {
  POWER_OFF("!1PWR00"),
  POWER_ON("!1PWR01"),
  POWER_QUERY("!1PWRQSTN")
}
