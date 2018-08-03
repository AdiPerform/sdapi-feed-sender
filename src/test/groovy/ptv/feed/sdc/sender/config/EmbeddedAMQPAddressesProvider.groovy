package ptv.feed.sdc.sender.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ptv.feed.sdc.shared.test.rabbit.dsl.StubServersPortSupplier

class EmbeddedAMQPAddressesProvider implements AddressProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedAMQPAddressesProvider)

  private static final String ADDRESSES_PREFIX = "localhost:"

  @Override
  public String getAddress() {
    StubServersPortSupplier portSupplier = StubServersPortSupplier.getInstance()
    int brokerPort = portSupplier.getBrokerPort()
    String addresses = ADDRESSES_PREFIX + brokerPort
    LOGGER.debug("brokerAddresses = {}", addresses)
    addresses
  }
}