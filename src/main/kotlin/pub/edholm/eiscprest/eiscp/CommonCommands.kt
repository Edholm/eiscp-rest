package pub.edholm.eiscprest.eiscp

class CommonCommands {
  companion object {
    fun powerOff() = ISCPCommand("!1PWR", "00")
    fun powerOn() = ISCPCommand("!1PWR", "01")
    fun powerStatus() = ISCPCommand("!1PWR", "QSTN")
  }
}


