package pub.edholm.eiscprest

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.regex.Pattern

internal class CorrelationIdFilterTest {

  private fun getRandomString(length: Int) = CorrelationIdFilter().randomString(length)

  @Test
  fun `Length of random string is correct`() {
    val len = 1024
    assertThat(getRandomString(len))
      .hasSize(len)
  }

  @Test
  fun `Zero length`() {
    val len = 0
    assertThat(getRandomString(len))
      .hasSize(len)
  }

  @Test
  fun `Negative length`() {
    val len = -1
    assertThat(getRandomString(len))
      .hasSize(0)
  }

  @Test
  fun `Is only hexadecimal values`() {
    val randomString = getRandomString(10000)

    assertThat(randomString)
      .matches(Pattern.compile("[a-f0-9]{10000}"))
  }
}