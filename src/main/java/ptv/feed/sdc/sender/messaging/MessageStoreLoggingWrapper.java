package ptv.feed.sdc.sender.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.integration.store.MessageStore;
import org.springframework.messaging.Message;

import java.util.UUID;

import static com.google.common.base.Preconditions.checkNotNull;


public class MessageStoreLoggingWrapper {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageStoreLoggingWrapper.class);

  private final MessageStore messageStore;

  public MessageStoreLoggingWrapper(final MessageStore messageStore) {
    super();
    checkNotNull(messageStore);
    this.messageStore = messageStore;
  }

  public Message<?> removeMessage(final UUID id) {
    LOGGER.info("Removing message {} from MessageStore", id);
    final Message<?> message = messageStore.removeMessage(id);
    if (message == null) {
      LOGGER.warn("Message {} is not present in message store", id);
      return null;
    }
    else {
      LOGGER.info("Message {} removed from message store", id);
      return message;
    }
  }

}
