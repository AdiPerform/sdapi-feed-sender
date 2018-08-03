package ptv.feed.sdc.sender.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.amqp.AmqpHeaders;
import org.springframework.integration.annotation.Transformer;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

public class RabbitMessageRequeuingHeadersTransformer {
  private static final Logger LOGGER = LoggerFactory.getLogger(RabbitMessageRequeuingHeadersTransformer.class);

  private final String requeueCountHeaderName;
  private final String expirationTimeHeaderName;
  private final int messageExpirationInitialTime;
  private final int messageExpirationTimeFactor;

  public RabbitMessageRequeuingHeadersTransformer(String requeueCountHeaderName, String expirationTimeHeaderName, int messageExpirationInitialTime, int messageExpirationTimeFactor) {
    this.requeueCountHeaderName = requeueCountHeaderName;
    this.expirationTimeHeaderName = expirationTimeHeaderName;
    this.messageExpirationInitialTime = messageExpirationInitialTime;
    this.messageExpirationTimeFactor = messageExpirationTimeFactor;
  }

  @Transformer
  public Message<?> updateRequeingHeaders(Message<?> message) {
    int requeueCount = (int) message.getHeaders().getOrDefault(requeueCountHeaderName, 0);
    int expirationTime;

    Object currentExpirationTime = message.getHeaders().get(expirationTimeHeaderName);
    if (currentExpirationTime == null) {
      expirationTime = messageExpirationInitialTime;
    } else {
      expirationTime = getNextExpirationTime((int) currentExpirationTime);
    }

    return MessageBuilder.fromMessage(message)
        .copyHeaders(message.getHeaders())
        .setHeader(requeueCountHeaderName, requeueCount + 1)
        .setHeader(expirationTimeHeaderName, expirationTime)
        .setHeader(AmqpHeaders.EXPIRATION, String.valueOf(expirationTime))
        .build();
  }

  private int getNextExpirationTime(int currentExpirationTime) {
    return currentExpirationTime * messageExpirationTimeFactor;
  }
}
