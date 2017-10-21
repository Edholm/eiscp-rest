package pub.edholm.eiscprest

import org.apache.log4j.Logger
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import pub.edholm.eiscprest.eiscp.ISCPCommand
import pub.edholm.eiscprest.eiscp.ISCPHeader
import java.io.DataInputStream
import java.net.Socket

@SpringBootApplication
class Application {

  private val log = Logger.getLogger(Application::class.java)

  @Bean
  fun getSocket() = Socket("10.10.10.57", 60128)

  @Bean(name = arrayOf("senderThread"))
  fun getSenderThread(socket: Socket, outputQueue: OutputQueue): Thread {
    val thread = Thread {
      val output = socket.getOutputStream()
      while (!socket.isClosed) {
        val nextCmdToSend = outputQueue.pop()
        log.debug("Sending $nextCmdToSend")
        output.write(ISCPHeader(messageSize = nextCmdToSend.size()).toByteArray())
        output.write(nextCmdToSend.toByteArray())
        output.flush()
      }
    }
    thread.name = "msgSender"
    return thread
  }

  @Bean(name = arrayOf("receiverThread"))
  fun getReceiverThread(socket: Socket, inputQueue: InputQueue): Thread {
    val receiverThread = Thread {
      val input = DataInputStream(socket.getInputStream())
      while (!socket.isClosed) {
        val headerBytes = ByteArray(ISCPHeader.DEFAULT_SIZE)
        val readHeaderBytes = input.readNBytes(headerBytes, 0, ISCPHeader.DEFAULT_SIZE)
        if (readHeaderBytes != ISCPHeader.DEFAULT_SIZE) {
          log.warn("Could not read message header from socket. Expected ${ISCPHeader.DEFAULT_SIZE}, got $readHeaderBytes")
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
          continue
        }
        val msg = ISCPCommand.valueOf(messageBytes)

        log.trace("Message header: $header")
        log.debug("Received $msg")
        inputQueue.put(msg)
      }
    }
    receiverThread.name = "msgReceiver"
    return receiverThread
  }

  @Bean
  fun initThreads(@Qualifier("receiverThread") receiver: Thread,
                  @Qualifier("senderThread") sender: Thread) = CommandLineRunner {
    receiver.start()
    sender.start()
  }
}

fun main(args: Array<String>) {
  SpringApplication.run(Application::class.java, *args)
}
