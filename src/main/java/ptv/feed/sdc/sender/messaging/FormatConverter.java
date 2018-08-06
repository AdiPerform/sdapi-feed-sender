package ptv.feed.sdc.sender.messaging;

public interface FormatConverter {
  public <T> String convert(final T body);
}