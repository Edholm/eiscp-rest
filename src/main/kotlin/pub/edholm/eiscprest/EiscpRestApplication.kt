package pub.edholm.eiscprest

import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
import pub.edholm.eiscprest.eiscp.Command
import pub.edholm.eiscprest.eiscp.Type
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.Socket

@SpringBootApplication
class EiscpRestApplication {
    @Bean
    fun cmdRunner() = CommandLineRunner {
        val socket = Socket("10.10.10.57", 60128)
        val out = socket.getOutputStream()
        println("Sending power query")
        val msg = Command(Type.POWER_QUERY).toByteArray()
        println(msg.contentToString())
        out.write(msg)
        out.flush()

        val input = BufferedReader(InputStreamReader(socket.getInputStream()))
        input.lines().forEach {
            println(it)
        }
    }
}

fun main(args: Array<String>) {
    SpringApplication.run(EiscpRestApplication::class.java, *args)
}
