package ptv.feed.sdc.sender.valdeFromSoccer.converter;

import org.springframework.oxm.Marshaller;

import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;

public class FormatConverterModel implements FormatConverter {

  private transient Marshaller marshaller;

  public FormatConverterModel(Marshaller marshaller) {
    this.marshaller = marshaller;
  }

  public <T> String convert(final T body) {
    if (body == null) {
      return null;
    } else {
      final StringWriter writer = new StringWriter();
      final StreamResult result = new StreamResult(writer);
      try {
        marshaller.marshal(body, result);
      } catch (final IOException e) {
        throw new IllegalArgumentException(e);
      }

      return writer.toString();
    }
  }
}
