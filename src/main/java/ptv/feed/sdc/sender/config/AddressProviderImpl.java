package ptv.feed.sdc.sender.config;

public class AddressProviderImpl implements AddressProvider {

  private String address;

  @Override
  public String getAddress() {
    return address;
  }

  /**
   * @param address
   *          the address to set
   */
  public void setAddress(final String address) {
    this.address = address;
  }

}
