package pub.edholm.eiscprest

import org.apache.log4j.Logger
import org.springframework.stereotype.Component
import pub.edholm.eiscprest.eiscp.ISCPHeader
import java.net.Socket

@Component
class SenderThread(private val socket: Socket,
                   private val outputQueue: OutputQueue,
                   private val log: Logger = Logger.getLogger(SenderThread::class.java)) : Thread("msgSender") {
  override fun run() {
    val output = socket.getOutputStream()
    while (!socket.isClosed) {
      val nextCmdToSend = outputQueue.pop()
      log.debug("Sending $nextCmdToSend")
      output.write(ISCPHeader(messageSize = nextCmdToSend.size()).toByteArray())
      output.write(nextCmdToSend.toByteArray())
      output.flush()
    }
  }
}