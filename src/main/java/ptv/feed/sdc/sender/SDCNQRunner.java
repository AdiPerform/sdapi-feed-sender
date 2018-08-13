package ptv.feed.sdc.sender;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class SDCNQRunner {

  private static final Logger LOGGER = LoggerFactory.getLogger(SDCNQRunner.class);

  /** Spring active profile properties. */
  private static final String PROPERTY_SPRING_PROFILE = "spring.profiles.active";
  /** Default spring profile. */
  private static final String DEFAULT_SPRING_PROFILE = "dev";
  /** CC-NQ context. */
  private static final String CONTEXT = "context/sdcnq-context.xml";
  /** Return status from SDCNQ process when error occurs. */
  private static final int ERROR_STATUS = 1;

  /**
   * Starts SDCNQ process.
   * 
   * @param args process arguments
   */
  public static void main(final String[] args) {
    LOGGER.info("Starting SDC-NQ... ");
    setDefaultSpringProfile();
    try {
      final ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(CONTEXT);
      for (final String profile : applicationContext.getEnvironment().getActiveProfiles()) {
        LOGGER.info("Profiles {}", profile);
      }

      applicationContext.registerShutdownHook();
      applicationContext.start();
      LOGGER.info("SDC-NQ started");
    } catch (final Exception e) {
      LOGGER.error("Exception occurred during launching SDC-NQ", e);
      System.exit(ERROR_STATUS);
    }
  }

  /**
   * Sets default spring profile.
   */
  private static void setDefaultSpringProfile() {
    final String profile = System.getProperty(PROPERTY_SPRING_PROFILE);
    if (StringUtils.isEmpty(profile)) {
      System.setProperty(PROPERTY_SPRING_PROFILE, DEFAULT_SPRING_PROFILE);
    }
  }

}
