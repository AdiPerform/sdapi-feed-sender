package ptv.feed.sdc.sender.messaging;

import com.performfeeds.utils.UUIDBase36Helper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.converter.MessageConversionException;
import org.springframework.amqp.support.converter.SimpleMessageConverter;

import java.nio.charset.Charset;
import java.util.Optional;

import static ptv.feed.sdc.sender.error.MessageLogger.logInfo;


public class MessageStamper extends SimpleMessageConverter {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageStamper.class);
  public static final String STAMP_UUID_HEADER = "stamp_uuid";

  public static Optional<String> getStampUuid(final org.springframework.messaging.Message<?> message) {
    return Optional.ofNullable((String) message.getHeaders().get(STAMP_UUID_HEADER));
  }

  @Override
  public Object fromMessage(final Message message) throws MessageConversionException {
    stampAndLogMessage(message);
    return super.fromMessage(message);
  }

  public void stampAndLogMessage(final Message message) {
    if (!message.getMessageProperties().getHeaders().containsKey(STAMP_UUID_HEADER)) {
      final String stampUuid = generateStamp();

      logInfo(LOGGER, Optional.of(stampUuid), String.format("received headers %s and body %s",
          message.getMessageProperties().getHeaders(), convertToString(message.getBody())));

      message.getMessageProperties().setHeader(STAMP_UUID_HEADER, stampUuid);
    }
  }

  private String convertToString(final Object payload) {
    final String result = new String((byte[]) payload, Charset.forName("UTF-8"));
    return result.replaceAll("\r", "").replaceAll("\n", "");
  }

  private String generateStamp() {
    return UUIDBase36Helper.randomUUID();
  }
}
