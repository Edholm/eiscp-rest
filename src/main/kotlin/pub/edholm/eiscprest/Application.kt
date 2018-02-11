package pub.edholm.eiscprest

import org.slf4j.LoggerFactory
import org.springframework.boot.ApplicationRunner
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.codec.ServerSentEvent
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.reactive.config.CorsRegistry
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.config.WebFluxConfigurerComposite
import pub.edholm.eiscprest.domain.State
import pub.edholm.eiscprest.queues.CommandProcessor
import pub.edholm.eiscprest.services.StateService
import reactor.core.publisher.ReplayProcessor
import java.net.Socket

@SpringBootApplication
@EnableAsync
@Configuration
class Application {

  private val log = LoggerFactory.getLogger(Application::class.java)

  @Bean(name = ["receiverSocket"])
  fun getSocket(properties: Properties): Socket {
    log.trace("Connecting to ${properties.receiver.hostname}:${properties.receiver.port}")
    return Socket(properties.receiver.hostname, properties.receiver.port)
  }

  @Bean
  fun initThreads(receiver: ReceiverThread, sender: SenderThread, commandProcessor: CommandProcessor) =
    CommandLineRunner {
      log.trace("Starting receiver thread")
      receiver.start()
      log.trace("Starting sender thread")
      sender.start()
      log.trace("Starting command processor")
      commandProcessor.start()
    }

  @Bean
  fun onApplicationStart(stateService: StateService) = ApplicationRunner {
    if (stateService.current().hasMissingFields()) {
      log.info("No current state saved, requesting update")
      stateService.requestFullStateUpdate()
    }
  }

  @Bean
  fun executor() = ThreadPoolTaskExecutor()

  @Bean
  fun getSSESEmitter(): ReplayProcessor<ServerSentEvent<State>> = ReplayProcessor.create(1)

  @Bean
  fun corsConfigurer(): WebFluxConfigurer {
    return object : WebFluxConfigurerComposite() {
      override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
          .allowedOrigins("*")
          .allowedHeaders("*")
          .allowedMethods("*")
      }
    }
  }
}

fun main(args: Array<String>) {
  SpringApplication.run(Application::class.java, *args)
}
