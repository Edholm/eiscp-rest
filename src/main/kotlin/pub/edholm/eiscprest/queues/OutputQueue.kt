package pub.edholm.eiscprest.queues

import org.springframework.stereotype.Component

@Component
class OutputQueue : AbstractQueue(100)
