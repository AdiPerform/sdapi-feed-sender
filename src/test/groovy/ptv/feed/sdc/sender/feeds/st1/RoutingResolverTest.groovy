package ptv.feed.sdc.sender.feeds.st1

import org.springframework.messaging.MessageHeaders
import ptv.feed.sa.api.enums.Feed
import ptv.feed.sdc.sender.receiver.oc.enums.OcPushHeaders
import ptv.feed.sdc.sender.routing.Routing
import ptv.feed.sdc.sender.routing.RoutingResolver
import spock.lang.Specification
import spock.lang.Unroll

class RoutingResolverTest extends Specification {

  @Unroll
  def 'should resolve Valde routing key when ST1 feed is routed to VALDE'() {
    given:
    Map<String, String> headers = new HashMap<>();
    headers.put(OcPushHeaders.OC_TYPE.getHeaderName(), Feed.ST1.getName())
    MessageHeaders messageHeaders = new MessageHeaders(headers)

    Map<String, String> feedRoutings = new HashMap<>()
    feedRoutings.put(Feed.ST1.getName(), Routing.VALDE.getKey())

    RoutingResolver routingResolver = new RoutingResolver(feedRoutings)

    when:
    def resoultRouting =  routingResolver.getRouting(messageHeaders)

    then:
    resoultRouting == Routing.VALDE.getKey()
  }

  def 'should throw exception when feeds is binded to VALDE and valde sending is turned off'() {
    given:
    Map<String, String> headers = new HashMap<>();
    headers.put(OcPushHeaders.OC_TYPE.getHeaderName(), Feed.ST1.getName())
    MessageHeaders messageHeaders = new MessageHeaders(headers)

    Map<String, String> feedRoutings = new HashMap<>()
    feedRoutings.put(Feed.ST1.getName(), Routing.SDAPI.getKey())

    RoutingResolver routingResolver = new RoutingResolver(feedRoutings)

    when:
    routingResolver.getRouting(messageHeaders)

    then:
    thrown(IllegalArgumentException)
  }
}