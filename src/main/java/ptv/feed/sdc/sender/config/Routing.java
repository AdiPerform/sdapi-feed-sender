package ptv.feed.sdc.sender.config;

public enum Routing {

  SDAPI("SDAPI"),
  OPTA("OPTA"),
  VALDE("VALDE");

  private final String key;

  private Routing(final String key) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }
}
