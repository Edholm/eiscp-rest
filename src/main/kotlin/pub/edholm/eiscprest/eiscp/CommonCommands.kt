package pub.edholm.eiscprest.eiscp

class CommonCommands {
  companion object {
    fun powerOff() = ISCPCommand(Command.POWER, "00")
    fun powerOn() = ISCPCommand(Command.POWER, "01")
    fun powerStatus() = ISCPCommand(Command.POWER, "QSTN")
  }
}


