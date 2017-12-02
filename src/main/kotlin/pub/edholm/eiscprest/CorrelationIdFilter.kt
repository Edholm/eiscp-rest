package pub.edholm.eiscprest

import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.security.SecureRandom

@Component
class CorrelationIdFilter : WebFilter {
  companion object {
    private const val REQUEST_ID_LENGTH = 8
  }

  override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
    val wrappedId = randomString(REQUEST_ID_LENGTH).wrapWithParenthesis()
    MDC.put("reqId", wrappedId)
    return chain.filter(exchange)
  }

  internal fun String.wrapWithParenthesis(): String {
    return " [$this] "
  }

  internal fun randomString(length: Int): String {
    val random = SecureRandom()
    val sb = StringBuilder()
    while (sb.length < length) {
      sb.append(Integer.toHexString(random.nextInt(16)))
    }
    return sb.toString()
  }
}
