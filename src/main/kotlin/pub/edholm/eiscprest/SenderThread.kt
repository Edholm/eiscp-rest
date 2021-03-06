package pub.edholm.eiscprest

import io.micrometer.core.instrument.MeterRegistry
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pub.edholm.eiscprest.eiscp.ISCPHeader
import pub.edholm.eiscprest.queues.OutputQueue
import java.net.Socket

@Component
class SenderThread(
  private val socket: Socket,
  private val outputQueue: OutputQueue,
  private val meterRegistry: MeterRegistry
) : Thread("msgSender") {

  private val log: Logger = LoggerFactory.getLogger(SenderThread::class.java)

  override fun run() {
    val output = socket.getOutputStream()
    while (!socket.isClosed) {
      val nextCmdToSend = outputQueue.pop()
      log.debug("Sending $nextCmdToSend")
      output.write(ISCPHeader(messageSize = nextCmdToSend.size()).toByteArray())
      output.write(nextCmdToSend.toByteArray())
      output.flush()

      meterRegistry.counter(
        "eiscp-rest.messages.outgoing",
        "command",
        nextCmdToSend.rawCommand,
        "parsedCmd",
        nextCmdToSend.command.name
      ).increment()
    }
  }
}