package ptv.feed.sdc.sender.spring

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigUtils
import org.springframework.context.support.GenericApplicationContext
import org.springframework.test.context.MergedContextConfiguration
import org.springframework.test.context.support.AbstractContextLoader
import org.springframework.util.StringUtils


class LifecycleSupportingXmlContextLoader extends AbstractContextLoader {

  private static final Logger LOGGER = LoggerFactory.getLogger(LifecycleSupportingXmlContextLoader);

  @Override
  protected String getResourceSuffix() {
    return "-context.xml";
  }

  @Override
  ApplicationContext loadContext(final MergedContextConfiguration mergedConfig) throws Exception {
    LOGGER.debug("Loading ApplicationContext for merged context configuration {}.", mergedConfig)
    GenericApplicationContext context = new GenericApplicationContext();
    context.getEnvironment().setActiveProfiles(mergedConfig.getActiveProfiles());
    new XmlBeanDefinitionReader(context).loadBeanDefinitions(mergedConfig.getLocations());
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    context.refresh();
    context.start();
    context.registerShutdownHook();
    return context;
  }

  @Override
  ApplicationContext loadContext(final String... locations) throws Exception {
    LOGGER.debug("Loading ApplicationContext for locations {}.", StringUtils.arrayToCommaDelimitedString(locations))
    GenericApplicationContext context = new GenericApplicationContext();
    new XmlBeanDefinitionReader(context).loadBeanDefinitions(locations);
    AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
    context.refresh();
    context.start();
    context.registerShutdownHook();
    return context;
  }
}
