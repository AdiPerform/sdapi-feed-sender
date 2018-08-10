package ptv.feed.sdc.sender.feeds.st1

import org.springframework.messaging.MessageHeaders
import ptv.feed.sa.api.enums.Feed
import ptv.feed.sdc.sender.optacore.OcPushHeaders
import ptv.feed.sdc.sender.config.Routing
import ptv.feed.sdc.sender.config.RoutingResolver
import spock.lang.Specification

class RoutingResolverTest extends Specification {

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

  def 'should throw exception when no routings configuration is empty'() {
    given:
    Map<String, String> headers = new HashMap<>();
    headers.put(OcPushHeaders.OC_TYPE.getHeaderName(), Feed.ST1.getName())
    MessageHeaders messageHeaders = new MessageHeaders(headers)

    Map<String, String> feedRoutings = new HashMap<>()

    RoutingResolver routingResolver = new RoutingResolver(feedRoutings)

    when:
    def resoultRouting = routingResolver.getRouting(messageHeaders)

    then:
    thrown(IllegalArgumentException)
  }

  def 'should throw exception when no routing is provided for MA2 feed'() {
    given:
    Map<String, String> headers = new HashMap<>();
    headers.put(OcPushHeaders.OC_TYPE.getHeaderName(), Feed.MA2.getName())
    MessageHeaders messageHeaders = new MessageHeaders(headers)

    Map<String, String> feedRoutings = new HashMap<>()
    feedRoutings.put(Feed.ST1.getName(), Routing.VALDE.getKey())

    RoutingResolver routingResolver = new RoutingResolver(feedRoutings)

    when:
    def resoultRouting = routingResolver.getRouting(messageHeaders)

    then:
    thrown(IllegalArgumentException)
  }


}