package ptv.feed.sdc.sender.flows

import org.springframework.amqp.core.Message
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import ptv.feed.sdc.sender.spec.ConsumerBaseSpecificationOc
import ptv.feed.sdc.sender.spring.LifecycleSupportingXmlContextLoader


@ContextConfiguration(locations = ["classpath*:/context/sdcnq-test-application-context.xml"], loader = LifecycleSupportingXmlContextLoader)
@ActiveProfiles("dev")
class ErrorChannelIT extends ConsumerBaseSpecificationOc {

  @Value('${routing.key.F13}')
  String f13RoutingKey

  def 'should catch bad message and send it to error channel'() {
    given:
    Message msg = messageBuilder
        .asString("Invalid payload")
        .withHeader(OptaPushHeaders.X_META_MATCH_UUID.getHeaderName(), TEST_MATCH_ID)
        .withHeader(OptaPushHeaders.X_META_LAST_UPDATED.getHeaderName(), "Sun Jul 13 17:18:19 BST 2014")
        .withHeader(OptaPushHeaders.X_META_FEED_TYPE.getHeaderName(), "F13")
        .withHeader(OptaPushHeaders.X_META_GAME_ID.getHeaderName(), TEST_MATCH_ID)
        .build()

    when:
    rabbit
        .withTemplate(rabbitTemplate)
        .withExchange(optaBridgeExchange)
        .withRoutingKey(f13RoutingKey)
        .send(msg)

    then:
    http {
      verify {
        assert receivedRequests.size() == 0
      }
    }
  }
}