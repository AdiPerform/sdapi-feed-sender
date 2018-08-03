package ptv.feed.sdc.sender.valdeFromSoccer.converter;

public interface FormatConverter {
  public <T> String convert(final T body);
}