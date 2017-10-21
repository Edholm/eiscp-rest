package pub.edholm.eiscprest.queues

import pub.edholm.eiscprest.eiscp.ISCPCommand
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

open class AbstractQueue(size: Int) {
  private val queue: BlockingQueue<ISCPCommand>

  init {
    queue = ArrayBlockingQueue<ISCPCommand>(size)
  }

  fun put(cmd: ISCPCommand) = queue.put(cmd)
  fun pop(): ISCPCommand = queue.take()
}