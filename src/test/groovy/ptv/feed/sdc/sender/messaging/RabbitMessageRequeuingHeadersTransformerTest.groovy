package ptv.feed.sdc.sender.messaging

import org.springframework.messaging.Message
import org.springframework.messaging.support.MessageBuilder
import spock.lang.Specification

class RabbitMessageRequeuingHeadersTransformerTest extends Specification {

  public static final String REQUEUE_COUNT_HEADER_NAME = "requeue_count_header";
  public static final String EXPIRATION_TIME_HEADER_NAME = "expiration_time_header";
  private static final int MSG_EXPIRATION_INITIAL_TIME = 1000
  private static final int MSG_EXPIRATION_TIME_FACTOR = 2

  RabbitMessageRequeuingHeadersTransformer objectUnderTest =
      new RabbitMessageRequeuingHeadersTransformer(REQUEUE_COUNT_HEADER_NAME, EXPIRATION_TIME_HEADER_NAME, MSG_EXPIRATION_INITIAL_TIME, MSG_EXPIRATION_TIME_FACTOR)

  def 'should set requeue count to 1 and initial message expiration time when there is no requeing headers present yet'() {
    given:
    def msg = aMsg(null, null)
    when:
    Message transformedMsg = objectUnderTest.updateRequeingHeaders(msg)
    then:
    transformedMsg.headers.get(REQUEUE_COUNT_HEADER_NAME) == 1
    transformedMsg.headers.get(EXPIRATION_TIME_HEADER_NAME) == MSG_EXPIRATION_INITIAL_TIME
  }

  def 'should increment requeue count and multiply message expiration time by expiration time factor'() {
    given:
    def msg = aMsg(1, 2000)
    when:
    Message transformedMsg = objectUnderTest.updateRequeingHeaders(msg)
    then:
    transformedMsg.headers.get(REQUEUE_COUNT_HEADER_NAME) == 2
    transformedMsg.headers.get(EXPIRATION_TIME_HEADER_NAME) == 2000 * MSG_EXPIRATION_TIME_FACTOR
  }

  def aMsg(requeueCount, expirationTime) {
    MessageBuilder.withPayload('')
        .setHeader(REQUEUE_COUNT_HEADER_NAME, requeueCount)
        .setHeader(EXPIRATION_TIME_HEADER_NAME, expirationTime)
        .build()
  }

}
