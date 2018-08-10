package ptv.feed.sdc.sender.flows

import com.performfeeds.enums.Format
import org.springframework.amqp.core.Message
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import ptv.feed.sa.api.enums.Feed
import ptv.feed.sdc.sender.optacore.OcPushHeaders
import ptv.feed.sdc.sender.valde.enums.ValdeHeaders
import ptv.feed.sdc.sender.spec.ValdeConsumerBaseSpecification
import ptv.feed.sdc.sender.spring.LifecycleSupportingXmlContextLoader

import java.time.Instant

@ContextConfiguration(locations = ["classpath*:/context/sdcnq-test-application-context.xml"], loader = LifecycleSupportingXmlContextLoader)
@ActiveProfiles("dev")
class ErrorChannelIT extends ValdeConsumerBaseSpecification {

  private static final Instant TEST_INSTANT_LAST_MODIFIED = Instant.parse("2015-10-30T12:11:32Z")
  private static final String TEST_LAST_MODIFIED_TIMESTAMP = TEST_INSTANT_LAST_MODIFIED.toEpochMilli()

  @Value('${exc.oc.soccer}')
  String exchange

  @Value('${routing.oc.soccer.st1}')
  String rabbitRoutingKey

  def 'should catch bad message and send it to error channel'() {
    given:
    Message msg = messageBuilder
        .asString("invalidPayload")
        .withHeader(OcPushHeaders.OC_TYPE.getHeaderName(), Feed.ST1.getName())
        .withHeader(OcPushHeaders.OC_TIMESTAMP.getHeaderName(), TEST_LAST_MODIFIED_TIMESTAMP)
        .withHeader(OcPushHeaders.OC_FORMAT.getHeaderName(), Format.XML.getName())
        .withHeader(ValdeHeaders.VALDE_GAME_ID.getHeaderName(), 324123)
        .withHeader(ValdeHeaders.VALDE_FEED_TYPE.getHeaderName(), "ST1")
        .build()

    when:
    rabbit
        .withTemplate(rabbitTemplate)
        .withExchange(exchange)
        .withRoutingKey(rabbitRoutingKey)
        .send(msg)

    then:
    http {
      verify {
        assert receivedRequests.size() == 0
      }
    }
  }
}