package ptv.feed.sdc.sender.routing;

import org.springframework.messaging.MessageHeaders;
import ptv.feed.sdc.sender.jmx.SendMessagesToValde;

import java.util.Map;

import static ptv.feed.sdc.sender.valdeFromSoccer.enums.OcPushHeaders.OC_TYPE;

public class RoutingResolver {

  private static final String CLOUD_HEADER_KEY = "cloud";

  private SendMessagesToValde sendMessagesToValde;

  private Map<String, String> feedRoutings;

  public RoutingResolver(final SendMessagesToValde sendMessagesToValde, final Map<String, String> feedRoutings) {
    this.sendMessagesToValde = sendMessagesToValde;
    this.feedRoutings = feedRoutings;
  }

  public String getRouting(final MessageHeaders headers) {
    if (headers.containsKey(CLOUD_HEADER_KEY)) {
      return headers.get(CLOUD_HEADER_KEY, String.class);
    }

    String feedCode = headers.get(OC_TYPE.getHeaderName(), String.class);
    if (!sendMessagesToValde.isEnabled() && Routing.VALDE.getKey().equals(feedRoutings.get(feedCode))) {
      throw new IllegalArgumentException("Sending to VALDE DISABLED - Message is not allowed to process to VALDE");
    }

    return (sendMessagesToValde.isEnabled() && feedRoutings.containsKey(feedCode)) ? feedRoutings.get(feedCode) : Routing.SDAPI.getKey();
  }

  public SendMessagesToValde getSendMessagesToValde() {
    return sendMessagesToValde;
  }

  public void setSendMessagesToValde(final SendMessagesToValde sendMessagesToValde) {
    this.sendMessagesToValde = sendMessagesToValde;
  }

  public Map<String, String> getFeedRoutings() {
    return feedRoutings;
  }

  public void setFeedRoutings(final Map<String, String> feedRoutings) {
    this.feedRoutings = feedRoutings;
  }
}
