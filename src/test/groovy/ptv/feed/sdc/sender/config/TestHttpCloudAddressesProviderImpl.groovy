package ptv.feed.sdc.sender.config

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ptv.feed.sdc.shared.test.rabbit.dsl.StubServersPortSupplier

class TestHttpCloudAddressesProviderImpl implements AddressProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(TestHttpCloudAddressesProviderImpl)

  private static final String ADDRESSES_PREFIX = "http://localhost:"

  @Override
  public String getAddress() {
    StubServersPortSupplier portSupplier = StubServersPortSupplier.getInstance()
    int httpPort = portSupplier.getHttpPort()
    String addresses = ADDRESSES_PREFIX + httpPort
    LOGGER.debug("httpAddresses = {}", addresses)
    addresses
  }
}