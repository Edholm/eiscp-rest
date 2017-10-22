package pub.edholm.eiscprest

import org.apache.log4j.Logger
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import pub.edholm.eiscprest.queues.CommandProcessor
import java.net.Socket
import java.util.concurrent.locks.ReentrantLock

@SpringBootApplication
class Application {

  private val log = Logger.getLogger(Application::class.java)

  @Bean(name = arrayOf("receiverSocket"))
  fun getSocket(properties: Properties): Socket {
    log.trace("Connecting to ${properties.receiver.hostname}:${properties.receiver.port}")
    return Socket(properties.receiver.hostname, properties.receiver.port)
  }

  @Bean
  fun getCurrentState(reentrantLock: ReentrantLock) = CurrentState(update = reentrantLock)

  @Bean(name = arrayOf("updateStateLock"))
  fun getUpdateStateLock() = ReentrantLock()

  @Bean
  fun initThreads(receiver: ReceiverThread, sender: SenderThread, commandProcessor: CommandProcessor) = CommandLineRunner {
    log.trace("Starting receiver thread")
    receiver.start()
    log.trace("Starting sender thread")
    sender.start()
    log.trace("Starting command processor")
    commandProcessor.start()
  }
}

fun main(args: Array<String>) {
  SpringApplication.run(Application::class.java, *args)
}
