package ptv.feed.sdc.sender.error;

import com.google.common.collect.Sets;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessagingException;
import org.springframework.web.client.HttpStatusCodeException;
import ptv.feed.sdc.sender.messaging.MessageStamper;

import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;
import static org.apache.commons.lang.exception.ExceptionUtils.getStackTrace;
import static org.springframework.http.HttpStatus.CONFLICT;

public class MessageLogger {

  private static final Logger LOGGER = LoggerFactory.getLogger(MessageLogger.class);
  private static final String MESSAGE_PREFIX = "Message {} - ";
  private static final Set INFO_STATUS_CODES;

  static {
    INFO_STATUS_CODES = Sets.newHashSet(CONFLICT);
  }

  private void logError(final Message<MessagingException> message) {
    final Message<?> failedMessage = message.getPayload().getFailedMessage();
    final String dumpedMessage = dumpMessage(failedMessage);
    final String stacktrace = dumpDetailedStackTrace(message);
    logError(LOGGER, MessageStamper.getStampUuid(failedMessage),
        format("error occurred: %s in message: %s", stacktrace, dumpedMessage));
  }

  private void logInfo(final Message<MessagingException> message) {
    final Message<?> failedMessage = message.getPayload().getFailedMessage();
    final String dumpedMessage = dumpMessage(failedMessage);
    final String stacktrace = dumpDetailedStackTrace(message);
    logInfo(LOGGER, MessageStamper.getStampUuid(failedMessage),
        format("error occurred: %s in message: %s", stacktrace, dumpedMessage));
  }

  public Message<?> logAndUnwrap(final Message<MessagingException> message) {
    handleMessage(message);
    return message.getPayload().getFailedMessage();
  }

  private void handleMessage(final Message<MessagingException> message) {
    if (isInfoLevel(message)) {
      logInfo(message);
    } else {
      logError(message);
    }
  }

  private boolean isInfoLevel(final Message<MessagingException> message){
    final Object cause = message.getPayload().getCause().getCause();
    if (cause instanceof HttpStatusCodeException) {
      final HttpStatusCodeException statusCodeException = (HttpStatusCodeException) cause;
      if (INFO_STATUS_CODES.contains(statusCodeException.getStatusCode())) {
        return true;
      }
    }
    return false;
  }

  public static void logInfo(Logger logger, Optional<String> stampUuid, String message) {
    checkNotNull(logger);
    checkNotNull(message);
    logger.info(message(message), stampUuid(stampUuid));
  }

  public static void logError(Logger logger, Optional<String> stampUuid, String message) {
    checkNotNull(logger);
    checkNotNull(message);
    logger.error(message(message), stampUuid(stampUuid));
  }

  private static String message(String message) {
    return MESSAGE_PREFIX + message;
  }

  private static String stampUuid(final Optional<String> stampUuid) {
    return stampUuid.orElse("<missing-stamp-uuid>");
  }

  private static String dumpMessage(Message<?> message) {
    final String headers = message.getHeaders().toString();
    final String payload = message.getPayload().toString();
    return String.format("headers: %s and payload: %s", headers, payload);
  }

  private static String dumpDetailedStackTrace(final Message<MessagingException> message) {
    final Throwable cause = message.getPayload().getCause();
    final String detailedEx = getFullStackTrace(cause.getCause());
    return StringUtils.isNotEmpty(detailedEx) ? detailedEx : getStackTrace(cause);
  }
}
