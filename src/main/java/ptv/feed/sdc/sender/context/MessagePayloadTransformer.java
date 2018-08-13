package ptv.feed.sdc.sender.context;

import org.springframework.integration.transformer.AbstractTransformer;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import java.nio.charset.Charset;


public class MessagePayloadTransformer extends AbstractTransformer {

  @Override
  protected Object doTransform(final Message<?> message) throws Exception {
    final Object payload = transformPayload(message.getPayload());
    if (payload instanceof String) {
      final MessageBuilder<String> messageBuilder = MessageBuilder.withPayload((String) payload);
      messageBuilder.copyHeaders(message.getHeaders());
      return messageBuilder.build();
    }
    return message;
  }

  private Object transformPayload(final Object payload) {
    if (payload instanceof byte[]) {
      return new String((byte[]) payload, Charset.forName("UTF-8"));
    }
    return payload;
  }

}
