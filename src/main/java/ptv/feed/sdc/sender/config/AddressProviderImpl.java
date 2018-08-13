package ptv.feed.sdc.sender.config;

public class AddressProviderImpl implements AddressProvider {

  private String address;

  @Override
  public String getAddress() {
    return address;
  }

  public void setAddress(final String address) {
    this.address = address;
  }

}
