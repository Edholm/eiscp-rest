package pub.edholm.eiscprest

import org.apache.log4j.Logger
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import java.net.Socket

@SpringBootApplication
class Application {

  private val log = Logger.getLogger(Application::class.java)

  @Bean
  fun getSocket() = Socket("10.10.10.57", 60128)

  @Bean
  fun initThreads(receiver: ReceiverThread, sender: SenderThread) = CommandLineRunner {
    log.trace("Starting receiver thread")
    receiver.start()
    log.trace("Starting sender thread")
    sender.start()
  }
}

fun main(args: Array<String>) {
  SpringApplication.run(Application::class.java, *args)
}
