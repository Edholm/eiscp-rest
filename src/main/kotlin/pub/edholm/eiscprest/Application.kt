package pub.edholm.eiscprest

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import pub.edholm.eiscprest.queues.CommandProcessor
import java.net.Socket

@SpringBootApplication
@EnableAsync
class Application {

  private val log = LoggerFactory.getLogger(Application::class.java)

  @Bean(name = ["receiverSocket"])
  fun getSocket(properties: Properties): Socket {
    log.trace("Connecting to ${properties.receiver.hostname}:${properties.receiver.port}")
    return Socket(properties.receiver.hostname, properties.receiver.port)
  }

  @Bean
  fun initThreads(receiver: ReceiverThread, sender: SenderThread, commandProcessor: CommandProcessor) = CommandLineRunner {
    log.trace("Starting receiver thread")
    receiver.start()
    log.trace("Starting sender thread")
    sender.start()
    log.trace("Starting command processor")
    commandProcessor.start()
  }

  @Bean
  fun executor() = ThreadPoolTaskExecutor()
}

fun main(args: Array<String>) {
  SpringApplication.run(Application::class.java, *args)
}
