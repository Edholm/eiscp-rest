package pub.edholm.eiscprest.queues

import org.springframework.stereotype.Component

@Component
class InputQueue : AbstractQueue(100)