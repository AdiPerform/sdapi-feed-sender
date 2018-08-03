package ptv.feed.sdc.sender.spec

import org.springframework.beans.factory.annotation.Autowired
import ptv.feed.sdc.sender.amqp.EmbeddedAMQPBroker
import ptv.feed.sdc.shared.test.rabbit.client.RabbitMessageBuilder
import ptv.feed.sdc.shared.test.rabbit.client.RabbitMqClient
import ptv.feed.sdc.shared.test.rabbit.dsl.MqSpecification
import spock.lang.Shared

abstract class ConsumerBaseSpecification extends MqSpecification {

  @Shared
  EmbeddedAMQPBroker broker = new EmbeddedAMQPBroker()

  RabbitMqClient rabbit

  @Autowired
  RabbitMessageBuilder messageBuilder

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