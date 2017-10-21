package pub.edholm.eiscprest.web

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pub.edholm.eiscprest.eiscp.CommonCommands
import pub.edholm.eiscprest.queues.InputQueue
import pub.edholm.eiscprest.queues.OutputQueue

@RestController
@RequestMapping("/power")
class PowerController(private val outputQueue: OutputQueue,
                      private val inputQueue: InputQueue) {

  @GetMapping("/query")
  fun getPowerStatus(): String {
    outputQueue.put(CommonCommands.powerStatus())
    return inputQueue.pop().toString()
  }

  @PostMapping("/on")
  fun powerOn() {
    outputQueue.put(CommonCommands.powerOn())
  }

  @PostMapping("/off")
  fun powerOff() {
    outputQueue.put(CommonCommands.powerOff())
  }
}