package pub.edholm.eiscprest

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import pub.edholm.eiscprest.eiscp.ISCPCommand
import pub.edholm.eiscprest.eiscp.ISCPHeader
import pub.edholm.eiscprest.queues.InputQueue
import java.io.DataInputStream
import java.net.Socket

@Component
class ReceiverThread(
  private val socket: Socket,
  private val inputQueue: InputQueue,
  private val log: Logger = LoggerFactory.getLogger(ReceiverThread::class.java)
) : Thread("msgReceiver") {

  private fun assertSocketNotClosed() {
    Thread.sleep(2500)
    if (socket.isClosed) {
      throw IllegalStateException("Receiver socket has been closed...")
    }
  }

  override fun run() {
    val input = DataInputStream(socket.getInputStream())
    while (!socket.isClosed) {
      val headerBytes = ByteArray(ISCPHeader.DEFAULT_SIZE)
      val readHeaderBytes = input.readNBytes(headerBytes, 0, ISCPHeader.DEFAULT_SIZE)
      if (readHeaderBytes != ISCPHeader.DEFAULT_SIZE) {
        log.warn("Could not read message header from socket. Expected ${ISCPHeader.DEFAULT_SIZE}, got $readHeaderBytes")
        assertSocketNotClosed()
        continue
      }
      val header = ISCPHeader.valueOf(headerBytes)
      if (!header.isValid()) {
        log.warn("Got invalid Header: $header")
        continue
      }

      val messageBytes = ByteArray(header.messageSize)
      val readMessageBytes = input.readNBytes(messageBytes, 0, messageBytes.size)
      if (readHeaderBytes != ISCPHeader.DEFAULT_SIZE) {
        log.warn("Could not read message data from socket. Expected ${header.messageSize}, got $readMessageBytes")
        assertSocketNotClosed()
        continue
      }
      val msg = ISCPCommand.valueOf(messageBytes)

      log.trace("Message header: $header")
      log.debug("Received $msg")
      inputQueue.put(msg)
    }
  }
}