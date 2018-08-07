package ptv.feed.sdc.sender.routing;

public enum Routing {

  SDAPI("SDAPI"),
  VALDE("VALDE");

  private final String key;

  private Routing(final String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
