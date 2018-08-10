package ptv.feed.sdc.sender.valde.service;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ptv.feed.sdc.sender.messaging.FormatConverter;
import ptv.feed.sdc.sender.config.AbstractRestServiceDelegate;
import ptv.feed.sdc.sender.valde.enums.ValdeHeaders;
import ptv.feed.sdc.sender.exceptions.InfrastructureException;
import ptv.feed.sdc.sender.exceptions.ServiceException;

import java.net.URL;
import java.util.List;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static ptv.feed.sdc.sender.valde.enums.ValdeHeaders.VALDE_HEADERS_PREFIX;
import static ptv.feed.sdc.sender.valde.enums.ValdeHeaders.VALDE_HEADERS_REGEX;


public class ValdeRestServiceImpl implements ValdeRestService {

  /** URL to be requested. */
  private URL url;

  /** Format XLM or use String approach */
  private transient FormatConverter formatConverter;

  private transient final RestTemplate restTemplate;

  private static final String VALDE_CONTENT = "content";

  private final List<HttpStatus> httpStatusesToRequeue;

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRestServiceDelegate.class);

  public ValdeRestServiceImpl(final RestTemplate restTemplate,
                              final FormatConverter formatConverter,
                              final URL url,
                              final List<HttpStatus> httpStatusesToRequeue) {
    this.restTemplate = checkNotNull(restTemplate);
    this.formatConverter = formatConverter;
    this.httpStatusesToRequeue = checkNotNull(httpStatusesToRequeue);
    this.url = url;
  }

  @Override
  public void updateSt1(String feed, Map<String, String> headers) throws ServiceException, InfrastructureException {
    logInfo(feed, "Updating ST1 feed for game id: {}", headers.get(ValdeHeaders.VALDE_GAME_ID.getHeaderName()));
    postToValde(feed, headers);
    logInfo(feed, "Updated ST1 feed for game id: {}", headers.get(ValdeHeaders.VALDE_GAME_ID.getHeaderName()));
  }


  /**
   * Executes HTTP VALDE POST request.
   */
  protected ResponseEntity<String> postToValde(final String body, Map<String, String> headers)
      throws ServiceException,
      InfrastructureException {
    MultiValueMap<String, String> entityBody = createHttpEntityBody(body, headers);

    HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(entityBody, null);
    return executeValdeHttpPost(HttpMethod.POST, request);
  }

  private MultiValueMap<String, String> createHttpEntityBody(final String body, Map<String, String> headers) {
    MultiValueMap<String, String> requestVariables = new LinkedMultiValueMap<>();
    requestVariables.add(VALDE_CONTENT, formatConverter.convert(body));

    headers.forEach((key, value) -> {
      if(key.startsWith(VALDE_HEADERS_PREFIX)){
        requestVariables.add(key.replaceFirst(VALDE_HEADERS_REGEX, ""), value);
      }
    });

    return requestVariables;
  }

  private ResponseEntity<String> executeValdeHttpPost(final HttpMethod method, final HttpEntity<MultiValueMap<String, String>> requestEntity) throws ServiceException,
      InfrastructureException {
    try {
      final String requestUrl = url.toString();
      LOGGER.debug("Executing http request url: {} method: {}", requestUrl, method);

      restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
      final ResponseEntity<String> response = restTemplate.postForEntity(requestUrl, requestEntity, String.class);
      if(HttpStatus.OK.value() == response.getStatusCode().value()){
        String valdeId = StringUtils.substringBetween(response.getBody(), "</div>{\"id\":\"", "\",\"version\"");
        LOGGER.info("Created Valde document id: {}", valdeId);
      }
      return response;
    } catch (final ResourceAccessException e) {
      throw new InfrastructureException(e);
    } catch (final HttpStatusCodeException e) {
      if (httpStatusesToRequeue.contains(e.getStatusCode())) {
        throw new InfrastructureException(e);
      }
      throw new ServiceException(e);
    }
  }

  private void logInfo(final String format, Object... args) {
    LOGGER.info(format, args);
  }
}
