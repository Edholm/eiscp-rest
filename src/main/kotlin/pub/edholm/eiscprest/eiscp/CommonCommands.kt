package pub.edholm.eiscprest.eiscp

class CommonCommands {
  companion object {
    private const val QSTN = "QSTN"
    fun powerOff() = ISCPCommand(Command.POWER, "00")
    fun powerOn() = ISCPCommand(Command.POWER, "01")
    fun powerQuery() = ISCPCommand(Command.POWER, QSTN)

    fun inputSelectorQuery() = ISCPCommand(Command.INPUT_SELECTOR, QSTN)
  }
}


