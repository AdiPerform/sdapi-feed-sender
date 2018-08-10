package ptv.feed.sdc.sender.requeing

import com.performfeeds.enums.Format
import org.junit.Ignore
import org.springframework.amqp.core.Message
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.support.ErrorMessage
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import ptv.feed.sa.api.enums.Feed
import ptv.feed.sdc.sender.optacore.OcPushHeaders
import ptv.feed.sdc.sender.valde.enums.ValdeHeaders
import ptv.feed.sdc.sender.spec.ValdeConsumerBaseSpecification
import ptv.feed.sdc.sender.spring.LifecycleSupportingXmlContextLoader
import ptv.feed.sdc.shared.api.util.ResourceUtils

import java.time.Instant

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import static ptv.feed.sdc.sender.messaging.MessageStamper.STAMP_UUID_HEADER

@ContextConfiguration(locations = ["classpath*:/context/sdcnq-test-application-context.xml"], loader = LifecycleSupportingXmlContextLoader)
@ActiveProfiles("dev")
@Ignore
class DelayedRequeuingIT extends ValdeConsumerBaseSpecification {

  private static final Instant TEST_INSTANT_LAST_MODIFIED = Instant.parse("2015-10-30T12:11:32Z")
  private static final String TEST_LAST_MODIFIED_TIMESTAMP = TEST_INSTANT_LAST_MODIFIED.toEpochMilli()

  final STAMP_UUID = '123'

  @Value('${exc.oc.soccer}')
  String exchange

  @Value('${routing.oc.soccer.st1}')
  String rabbitRoutingKey


  def "should requeue message until response code is 503"() {
    given:
    http {
      onRequestIn("/dove/feed") {
        sendResponse(SERVICE_UNAVAILABLE.value(), SERVICE_UNAVAILABLE.reasonPhrase)
      }
    }

    and:
    String payload = ResourceUtils.getResourceAsString("feeds/st1/st1.xml")
    Message msg = messageBuilder
        .asString(payload)
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
        assert receivedRequests.every { it.method == "POST" }
      }
    }

    and:
    mq {
      verify {
        message {
          that instanceof ErrorMessage && that.payload.failedMessage.headers[STAMP_UUID_HEADER] == STAMP_UUID
        }
        visitedChannel("senderRequeueChannel")
        atLeast once
      }
    }
  }
}
