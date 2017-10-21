package pub.edholm.eiscprest

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import pub.edholm.eiscprest.eiscp.ISCPCommand
import pub.edholm.eiscprest.eiscp.ISCPHeader
import java.io.DataInputStream
import java.net.Socket

@SpringBootApplication
class EiscpRestApplication {
  @Bean
  fun cmdRunner() = CommandLineRunner {
    val socket = Socket("10.10.10.57", 60128)
    val out = socket.getOutputStream()
    //println("Sending power query")
    //val msg = Command(Type.POWER_QUERY).toByteArray()
    //println(msg.contentToString())
    //out.write(msg)
    //out.flush()

    val connected = true
    val input = DataInputStream(socket.getInputStream())

    while (connected) {
      val headerBytes = ByteArray(ISCPHeader.DEFAULT_SIZE)
      val readHeaderBytes = input.readNBytes(headerBytes, 0, ISCPHeader.DEFAULT_SIZE)
      if (readHeaderBytes != ISCPHeader.DEFAULT_SIZE) {
        println("Could not read message header from socket. Expected ${ISCPHeader.DEFAULT_SIZE}, got $readHeaderBytes")
        continue
      }
      val header = ISCPHeader.valueOf(headerBytes)

      val messageBytes = ByteArray(header.messageSize)
      val readMessageBytes = input.readNBytes(messageBytes, 0, messageBytes.size)
      if (readHeaderBytes != ISCPHeader.DEFAULT_SIZE) {
        println("Could not read message data from socket. Expected ${header.messageSize}, got $readMessageBytes")
        continue
      }
      val msg = ISCPCommand.valueOf(messageBytes)

      println("Got the following message")
      println(header)
      println(msg)
    }

    println("Closing socket")
    socket.close()
  }
}

fun main(args: Array<String>) {
  SpringApplication.run(EiscpRestApplication::class.java, *args)
}
