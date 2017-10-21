package pub.edholm.eiscprest.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pub.edholm.eiscprest.queues.InputQueue
import pub.edholm.eiscprest.queues.OutputQueue
import pub.edholm.eiscprest.eiscp.ISCPCommand

@RestController
@RequestMapping("/power")
class PowerController(private val outputQueue: OutputQueue,
                      private val inputQueue: InputQueue) {

  @GetMapping("/query")
  fun getPowerStatus(): String {
    outputQueue.put(ISCPCommand(command = "!1PWR", payload = "QSTN"))
    return inputQueue.pop().toString()
  }

  @PostMapping("/on")
  fun powerOn() {
    outputQueue.put(ISCPCommand(command = "!1PWR", payload = "01"))
  }

  @PostMapping("/off")
  fun powerOff() {
    outputQueue.put(ISCPCommand(command = "!1PWR", payload = "00"))
  }
}