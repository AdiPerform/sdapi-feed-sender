package ptv.feed.sdc.sender.feeds.st1

import com.performfeeds.enums.Format
import org.springframework.amqp.core.Message
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import ptv.feed.sa.api.enums.Feed
import ptv.feed.sdc.sender.optacore.OcPushHeaders
import ptv.feed.sdc.sender.valde.enums.ValdeHeaders
import ptv.feed.sdc.sender.config.Routing
import ptv.feed.sdc.sender.config.RoutingResolver
import ptv.feed.sdc.sender.spec.ValdeConsumerBaseSpecification
import ptv.feed.sdc.sender.spring.LifecycleSupportingXmlContextLoader
import ptv.feed.sdc.shared.test.rabbit.dsl.ValdeMqSpecification

import java.time.Instant

@ContextConfiguration(locations = ["classpath*:/context/sdcnq-test-application-context.xml"], loader = LifecycleSupportingXmlContextLoader)
@ActiveProfiles("dev")
class St1UpdateIT extends ValdeConsumerBaseSpecification {

  private static final Instant TEST_INSTANT_LAST_MODIFIED = Instant.parse('2017-03-23T09:59:13Z')
  private static final String TEST_LAST_MODIFIED_TIMESTAMP = TEST_INSTANT_LAST_MODIFIED.toEpochMilli()

  @Value('${exc.oc.soccer}')
  String exchange
  @Value('${routing.oc.soccer.st1}')
  String rabbitRoutingKey

  @Autowired
  RoutingResolver routingResolver


  def 'should not send POST to SDAPI when ST1 feed is bind to VALDE'() {
    given:
    Map<String,String> feedRoutings = new HashMap();
    feedRoutings.put(Feed.ST1.getName(), Routing.VALDE.getKey())
    routingResolver.setFeedRoutings(feedRoutings)

    String payload = 'payload'
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
        assert receivedRequests.size() == 0
      }
    }
  }


  def 'should send single POST with ST1 feed when feed is binded to VALDE'() {
    given:
    Map<String,String> feedRoutings = new HashMap();
    feedRoutings.put(Feed.ST1.getName(), Routing.VALDE.getKey())
    routingResolver.setFeedRoutings(feedRoutings)

    String payload = 'payload'
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
        assert receivedRequests.size() == 1
        assert receivedRequests.every { it.uri == ValdeMqSpecification.VALDE_ENDPOINT}
        assert receivedRequests.every { it.bodyAsString.contains("content=payload&feedType=ST1&gameId=324123")}
        assert receivedRequests.every { it.method == 'POST' }
      }
    }
  }

}