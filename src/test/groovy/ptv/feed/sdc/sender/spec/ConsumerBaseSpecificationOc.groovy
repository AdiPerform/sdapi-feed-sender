package ptv.feed.sdc.sender.spec

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier

class ConsumerBaseSpecificationOc extends ConsumerBaseSpecification {

  @Autowired
  @Qualifier("testOcRabbitTemplate")
  RabbitTemplate rabbitTemplate
}