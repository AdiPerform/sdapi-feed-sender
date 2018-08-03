package ptv.feed.sdc.sender.spec

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import ptv.feed.sdc.sender.amqp.EmbeddedAMQPBroker
import ptv.feed.sdc.shared.test.rabbit.client.RabbitMessageBuilder
import ptv.feed.sdc.shared.test.rabbit.client.RabbitMqClient
import ptv.feed.sdc.shared.test.rabbit.dsl.ValdeMqSpecification
import spock.lang.Shared

abstract class ValdeConsumerBaseSpecification extends ValdeMqSpecification {

  @Shared
  EmbeddedAMQPBroker broker = new EmbeddedAMQPBroker()

  RabbitMqClient rabbit

  @Autowired
  RabbitMessageBuilder messageBuilder

  @Autowired
  @Qualifier("testOcRabbitTemplate")
  RabbitTemplate rabbitTemplate

  def setupSpec() {
    broker.startup()
  }

  def setup() {
    rabbit = new RabbitMqClient(rabbitTemplate)
  }

  def cleanupSpec() {
    broker.shutdown()
  }

  def printMessageHistory() {
    println 'message history:'
    mqInstrumentationInterceptor.messageHistory.each {
      if (it.channelName != 'messageTracker') {
        println "\t-------------"
        println "\tchannel: ${it.channelName}"
        println "\tmessage(${it.message.class.simpleName}):"
        println "\t\theaders: ${it.message.headers}"
        println "\t\tpayload: ${it.message.payload}"
      }
    }
  }
}