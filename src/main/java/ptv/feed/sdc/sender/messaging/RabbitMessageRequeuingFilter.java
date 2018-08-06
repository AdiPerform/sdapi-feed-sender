package ptv.feed.sdc.sender.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static ptv.feed.sdc.sender.error.MessageLogger.logInfo;
import static ptv.feed.sdc.sender.receiver.oc.enums.IntegrationFlowHeaders.STAMP_UUID;


public class RabbitMessageRequeuingFilter {
  private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMessageRequeuingFilter.class);

  private final String requeueCountHeaderName;
  private final int maxRequeueCount;

  public RabbitMessageRequeuingFilter(String requeueCountHeaderName, int maxRequeueCount) {
    checkArgument(maxRequeueCount >= 0);
    this.requeueCountHeaderName = checkNotNull(requeueCountHeaderName);
    this.maxRequeueCount = maxRequeueCount;
  }

  public boolean isEligibleForRequeue(Message<?> message) {
    int requeueCount = (int) message.getHeaders().get(requeueCountHeaderName);
    String stampUuid = message.getHeaders().getOrDefault(STAMP_UUID, "").toString();

    boolean result = requeueCount <= maxRequeueCount;
    if(!result) {
      logInfo(LOGGER, Optional.of(stampUuid), "moved to undelivered queue for further investigation");
    }
    return result;
  }
}
