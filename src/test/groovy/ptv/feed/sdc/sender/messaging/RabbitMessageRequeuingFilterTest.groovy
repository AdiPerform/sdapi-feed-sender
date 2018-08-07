package ptv.feed.sdc.sender.messaging

import org.springframework.messaging.support.MessageBuilder
import spock.lang.Specification

class RabbitMessageRequeuingFilterTest extends Specification{

  public static final String REQUEUE_COUNT_HEADER_NAME = "requeue_count_header";
  private static final int MAX_REQUEUE_COUNT = 10

  RabbitMessageRequeuingFilter objectUnderTest = new RabbitMessageRequeuingFilter(REQUEUE_COUNT_HEADER_NAME, MAX_REQUEUE_COUNT)

  def 'should classify message for requeue when current requeue count is less than max requeue count'() {
    given:
    def msg = aMsg(MAX_REQUEUE_COUNT - 1)
    when:
    def result = objectUnderTest.isValidForRequeue(msg)
    then:
    result
  }

  def 'should classify message for requeue when current requeue count is equal to max requeue count'() {
    given:
    def msg = aMsg(MAX_REQUEUE_COUNT)
    when:
    def result = objectUnderTest.isValidForRequeue(msg)
    then:
    result
  }

  def 'should discard message for requeue when current requeue count is greater than max requeue count'() {
    given:
    def msg = aMsg(MAX_REQUEUE_COUNT + 1)
    when:
    def result = objectUnderTest.isValidForRequeue(msg)
    then:
    !result
  }

  def aMsg(requeueCount) {
    MessageBuilder.withPayload('')
        .setHeader(REQUEUE_COUNT_HEADER_NAME, requeueCount)
        .build()
  }
}
