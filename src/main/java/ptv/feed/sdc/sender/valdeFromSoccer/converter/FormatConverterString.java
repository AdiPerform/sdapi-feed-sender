package ptv.feed.sdc.sender.valdeFromSoccer.converter;

public class FormatConverterString implements FormatConverter {

  public <T> String convert(final T body) {
    if (body == null) {
      return null;
    }
    return body.toString();
  }
}
