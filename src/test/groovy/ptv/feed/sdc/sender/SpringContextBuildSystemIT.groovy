package ptv.feed.sdc.sender

import org.springframework.amqp.rabbit.core.RabbitAdmin
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import ptv.feed.sdc.sender.spring.LifecycleSupportingXmlContextLoader
import spock.lang.Ignore
import spock.lang.Specification

@ContextConfiguration(locations = ["classpath*:/context/sdcnq-context.xml"], loader = LifecycleSupportingXmlContextLoader)
@ActiveProfiles("dev")
@Ignore
class SpringContextBuildSystemIT extends Specification {

  @Autowired
  @Qualifier("ocRabbitAdmin")
  RabbitAdmin rabbitAdmin

  def 'context should start without any errors'(){
    expect:
    rabbitAdmin != null
  }
}
