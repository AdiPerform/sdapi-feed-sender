package ptv.feed.sdc.sender.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.Lifecycle;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;


public class CloudSenderConfigurer implements ApplicationContextAware, InitializingBean, Lifecycle {

  private static final Logger LOGGER = LoggerFactory.getLogger(CloudSenderConfigurer.class);

  /** The Constant DEFAULT_QUEUE_NAME_PREFIX. */
  private static final String DEFAULT_QUEUE_NAME_PREFIX = "que.sdc.sender.";

  /** The config locations. */
  private transient String[] configLocations;

  /** The cloud urls. */
  private transient List<URI> cloudUrls;

  /** The parent application context. */
  private transient ApplicationContext parentApplicationContext;

  /** The sender contexts. */
  private transient Map<URI, ConfigurableApplicationContext> senderContexts;

  /** The sender exchange. */
  private transient String senderExchange;

  /** The sender error exchange. */
  private transient String senderExchangeError;

  /** The sender context path. */
  private transient String valdeSenderContextPath;

  /** The sender queue prefix. */
  private transient String senderQueuePrefix = DEFAULT_QUEUE_NAME_PREFIX;

  /** The sender error queue exclusive */
  private transient boolean senderQueueErrorExclusive = false;

  /** The running. */
  private transient boolean running = false;

  /** The cloud request connection timeout */
  private transient int cloudRequestConnectTimeout = 0;

  /** The cloud request read timeout */
  private transient int cloudRequestReadTimeout = 0;

  /** The cloud http server to binding map */
  private transient Map<String, String> cloudBindings;

  @Override
  public void start() {
    if (!running) {
      startSenderContexts();
    }
    running = true;
  }

  @Override
  public void stop() {
    if (running) {
      stopSenderContexts();
    }
    running = false;
  }

  private void stopSenderContexts() {
    for (final Map.Entry<URI, ConfigurableApplicationContext> entry : senderContexts.entrySet()) {
      final URI cloudUrl = entry.getKey();
      LOGGER.info("Stopping {} sender context", cloudUrl);
      final ConfigurableApplicationContext ctx = entry.getValue();
      if (ctx.isActive()) {
        ctx.close();
        LOGGER.info("{} sender context stopped", cloudUrl);
      } else {
        LOGGER.info("{} sender context already stopped", cloudUrl);
      }
    }
  }

  @Override
  public boolean isRunning() {
    return running;
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    checkNotNull(valdeSenderContextPath);
    checkNotNull(senderExchange);
    checkNotNull(senderExchangeError);
    checkNotNull(cloudUrls);
  }

  private void startSenderContexts() {
    LOGGER.info("Starting sender contexts");
    senderContexts = new HashMap<>();
    for (final URI cloudUrl : cloudUrls) {
      final ConfigurableApplicationContext ctx = new ClassPathXmlApplicationContext(configLocations,
          false, parentApplicationContext);
      senderContexts.put(cloudUrl, ctx);
      setEnvironmentForSender(ctx, cloudUrl);
      ctx.refresh();
      ctx.start();
      LOGGER.info("Context for {} sender created", cloudUrl);
    }
    running = true;
  }

  private void setEnvironmentForSender(final ConfigurableApplicationContext ctx, final URI cloudUrl) {
    final StandardEnvironment env = new StandardEnvironment();
    final Properties props = new Properties();
    props.setProperty("sender.url", cloudUrl.toString());
    props.setProperty("sender.host", cloudUrl.getHost());
    props.setProperty("sender.exchange", senderExchange);
    props.setProperty("sender.exchange.error", senderExchangeError);
    props.setProperty("valde.sender.context.path", valdeSenderContextPath);
    props.setProperty("sender.queue", senderQueuePrefix + cloudUrl.getHost());
    props.setProperty("sender.queue.error", senderQueuePrefix + cloudUrl.getHost() + ".error");
    props.setProperty("sender.queue.error.undelivered", senderQueuePrefix + cloudUrl.getHost() + ".error.undelivered");
    props.setProperty("sender.queue.error.exclusive", String.valueOf(senderQueueErrorExclusive));
    props.setProperty("sender.request.connect.timeout", String.valueOf(cloudRequestConnectTimeout));
    props.setProperty("sender.request.read.timeout", String.valueOf(cloudRequestReadTimeout));
    props.setProperty("sender.binding", cloudBindings.get(cloudUrl.getHost()));

    final PropertiesPropertySource pps = new PropertiesPropertySource("child", props);
    env.getPropertySources().addLast(pps);
    ctx.setEnvironment(env);
  }

  public Map<URI, ConfigurableApplicationContext> getSenderContexts() {
    return senderContexts;
  }

  @Override
  public void setApplicationContext(final ApplicationContext applicationContext) throws BeansException {
    this.parentApplicationContext = applicationContext;
  }

  public void setCloudUrls(final String[] urls) throws URISyntaxException {
    checkNotNull(urls);
    checkArgument(urls.length > 0);
    cloudUrls = new ArrayList<>(urls.length);
    for (final String url : urls) {
      cloudUrls.add(new URI(url));
    }
  }

  public void setValdeSenderContextPath(final String valdeSenderContextPath) {
    this.valdeSenderContextPath = valdeSenderContextPath;
  }

  public void setSenderExchange(final String senderExchange) {
    this.senderExchange = senderExchange;
  }

  public void setSenderExchangeError(final String senderExchangeError) {
    this.senderExchangeError = senderExchangeError;
  }

  public void setSenderQueuePrefix(final String senderQueuePrefix) {
    this.senderQueuePrefix = senderQueuePrefix;
  }

  public void setSenderQueueErrorExclusive(final boolean senderQueueErrorExclusive) {
    this.senderQueueErrorExclusive = senderQueueErrorExclusive;
  }

  public void setConfigLocations(final String[] configLocations) {
    checkNotNull(configLocations);
    checkArgument(configLocations.length > 0);
    this.configLocations = Arrays.copyOf(configLocations, configLocations.length);
  }

  public void setCloudRequestConnectTimeout(final int cloudRequestConnectTimeout) {
    this.cloudRequestConnectTimeout = cloudRequestConnectTimeout;
  }

  public void setCloudRequestReadTimeout(final int cloudRequestReadTimeout) {
    this.cloudRequestReadTimeout = cloudRequestReadTimeout;
  }

  public void setCloudBindings(final String[] bindings){
    cloudBindings = new HashMap<>();
    checkNotNull(bindings);
    Arrays.asList(bindings).forEach(binding -> {
      String[] hostBinding = binding.split(":");
      cloudBindings.put(hostBinding[0], hostBinding[1]);
    });
  }
}
