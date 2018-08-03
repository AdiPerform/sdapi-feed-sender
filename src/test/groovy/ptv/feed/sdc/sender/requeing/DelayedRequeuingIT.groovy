package ptv.feed.sdc.sender.requeing

import com.performfeeds.enums.Format
import org.springframework.amqp.core.Message
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.messaging.support.ErrorMessage
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import ptv.feed.sdc.shared.api.util.ResourceUtils
import ptv.feed.sdc.sender.valdeFromSoccer.enums.OcPushHeaders
import ptv.feed.sdc.sender.spec.ConsumerBaseSpecification
import ptv.feed.sdc.sender.spring.LifecycleSupportingXmlContextLoader

import java.time.Instant

import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE
import static ptv.feed.sdc.sender.messaging.MessageStamper.STAMP_UUID_HEADER

@ContextConfiguration(locations = ["classpath*:/context/sdcnq-test-application-context.xml"], loader = LifecycleSupportingXmlContextLoader)
@ActiveProfiles("dev")
class DelayedRequeuingIT extends ConsumerBaseSpecification {

  private static final Instant TEST_INSTANT_LAST_MODIFIED = Instant.parse("2015-10-30T12:11:32Z")
  private static final String TEST_LAST_MODIFIED_TIMESTAMP = TEST_INSTANT_LAST_MODIFIED.toEpochMilli()

  final STAMP_UUID = '123'

  @Autowired
  @Qualifier("testSdcnqRabbitTemplate")
  RabbitTemplate rabbitTemplate

  @Value('${exc.sdc.oc}')
  String exchange

  @Value('${routing.oc.soccer.playercareer}')
  String routingKey

  def cleanup() {
    printMessageHistory()
  }

  def "should requeue message until response code is 503"() {
    given:
    http {
      onRequestIn("/sdc") {
        sendResponse(SERVICE_UNAVAILABLE.value(), SERVICE_UNAVAILABLE.reasonPhrase)
      }
    }

    and:
    String payload = ResourceUtils.getResourceAsString("feeds/oc/playercareer/oc_player_career.xml")
    Message msg = messageBuilder
            .asString(payload)
            .withHeader(OcPushHeaders.OC_TIMESTAMP.getHeaderName(), TEST_LAST_MODIFIED_TIMESTAMP)
            .withHeader(OcPushHeaders.OC_FORMAT.getHeaderName(), Format.XML.getName())
            .withHeader(STAMP_UUID_HEADER, STAMP_UUID)
            .build()

    when:
    rabbit
            .withTemplate(rabbitTemplate)
            .withExchange(exchange)
            .withRoutingKey(routingKey)
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
