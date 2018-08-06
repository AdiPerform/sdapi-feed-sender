package ptv.feed.sdc.sender.receiver.valde.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.annotation.Filter;
import org.springframework.messaging.Message;
import ptv.feed.sdc.sender.messaging.MessageStamper;
import ptv.feed.sdc.sender.receiver.oc.enums.OcPushHeaders;
import ptv.feed.sdc.sender.receiver.valde.enums.ValdeHeaders;

import java.util.Set;

import static java.lang.String.format;
import static java.util.Optional.of;
import static ptv.feed.sdc.sender.error.MessageLogger.logError;

public class ValdeRequiredHeadersValidator {

  private static final Logger LOGGER = LoggerFactory.getLogger(ValdeRequiredHeadersValidator.class);

  private Set<ValdeHeaders> valdeRequiredHeaders;
  private Set<OcPushHeaders> ocRequiredHeaders;

  public ValdeRequiredHeadersValidator(Set<ValdeHeaders> valdeRequiredHeaders, Set<OcPushHeaders> ocRequiredHeaders) {
    this.valdeRequiredHeaders = valdeRequiredHeaders;
    this.ocRequiredHeaders = ocRequiredHeaders;
  }

  @Filter
  public boolean accept(Message<?> message) {
    boolean valid = true;
    String stampUuid = MessageStamper.getStampUuid(message).get();
    for (ValdeHeaders valdeHeader : valdeRequiredHeaders) {
      if (!message.getHeaders().containsKey(valdeHeader.getHeaderName())) {
        logError(LOGGER, of(stampUuid), format("Valde header %s is required but wasn't sent.", valdeHeader.getHeaderName()));
        valid = false;
      }
    }
    for (OcPushHeaders ocHeader : ocRequiredHeaders) {
      if (!message.getHeaders().containsKey(ocHeader.getHeaderName())) {
        logError(LOGGER, of(stampUuid), format("Opta Core header %s is required but wasn't sent.", ocHeader.getHeaderName()));
        valid = false;
      }
    }

    return valid;
  }
}
