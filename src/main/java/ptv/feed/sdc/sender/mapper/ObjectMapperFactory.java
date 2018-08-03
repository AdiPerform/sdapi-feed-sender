package ptv.feed.sdc.sender.mapper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.integration.support.json.Jackson2JsonObjectMapper;

public class ObjectMapperFactory {

  public static Jackson2JsonObjectMapper getMapper() {
    final ObjectMapper mapper = new ObjectMapper();
    mapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
    mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return new Jackson2JsonObjectMapper(mapper);
  }
}
