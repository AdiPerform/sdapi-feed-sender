package ptv.feed.sdc.sender.routing;

import org.springframework.messaging.MessageHeaders;

import java.util.Map;

import static ptv.feed.sdc.sender.receiver.oc.enums.OcPushHeaders.OC_TYPE;

public class RoutingResolver {

  private static final String CLOUD_HEADER_KEY = "cloud";

  private Map<String, String> feedRoutings;

  public RoutingResolver(final Map<String, String> feedRoutings) {
    this.feedRoutings = feedRoutings;
  }

  public String getRouting(final MessageHeaders headers) {
    if (headers.containsKey(CLOUD_HEADER_KEY)) {
      return headers.get(CLOUD_HEADER_KEY, String.class);
    }

    String feedCode = headers.get(OC_TYPE.getHeaderName(), String.class);

    if(feedRoutings.containsKey(feedCode)){
      return feedRoutings.get(feedCode);
    }
    else{
      throw new IllegalArgumentException("Unsupported routing for feed: " + feedCode);
    }
  }

  public Map<String, String> getFeedRoutings() {
    return feedRoutings;
  }

  public void setFeedRoutings(final Map<String, String> feedRoutings) {
    this.feedRoutings = feedRoutings;
  }
}
