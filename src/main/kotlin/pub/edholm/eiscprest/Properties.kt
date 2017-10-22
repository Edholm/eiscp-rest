package pub.edholm.eiscprest

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@ConfigurationProperties("eiscp")
@Component
class Properties {
  val receiver = Receiver()

  class Receiver {
    var hostname: String = "localhost"
    var port: Int = 60128
  }
}
