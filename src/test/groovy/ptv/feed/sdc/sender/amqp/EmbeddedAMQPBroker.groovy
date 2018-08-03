package ptv.feed.sdc.sender.amqp

import com.google.common.io.Files
import org.apache.qpid.server.Broker
import org.apache.qpid.server.BrokerOptions
import ptv.feed.sdc.shared.test.rabbit.dsl.StubServersPortSupplier


class EmbeddedAMQPBroker {

  private static final String AMQP_CONFIG_PATH = 'config/amqp'

  private final Broker broker = new Broker();

  private final BrokerOptions brokerOptions;

  public EmbeddedAMQPBroker() throws Exception {

    final String configFileName = "qpid-config.json";

    final String passwordFileName = "passwd.properties";

    StubServersPortSupplier portSupplier = StubServersPortSupplier.getInstance()
    int brokerPort = portSupplier.getBrokerPort()

    brokerOptions = new BrokerOptions();

    brokerOptions.setConfigProperty("qpid.amqp_port", String.valueOf(brokerPort));

    brokerOptions.setConfigProperty("qpid.pass_file", findResourcePath(passwordFileName));

    brokerOptions.setConfigProperty("qpid.work_dir", Files.createTempDir().getAbsolutePath());

    brokerOptions.setInitialConfigurationLocation(findResourcePath(configFileName));

    brokerOptions.setSkipLoggingConfiguration(true);
  }

  public void startup() {
    broker.startup(brokerOptions);
  }

  public void shutdown() {
    broker.shutdown()
  }

  private String findResourcePath(final String file) throws IOException {
    return getClass().getClassLoader().getResource("${AMQP_CONFIG_PATH}/${file}").toURI().getPath()
  }
}
