package ptv.feed.sdc.sender.receiver;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import ptv.feed.sdc.sender.messaging.FormatConverter;
import ptv.feed.sdc.sender.exceptions.InfrastructureException;
import ptv.feed.sdc.sender.exceptions.ServiceException;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static ptv.feed.sdc.sender.receiver.valde.enums.ValdeHeaders.VALDE_HEADERS_PREFIX;
import static ptv.feed.sdc.sender.receiver.valde.enums.ValdeHeaders.VALDE_HEADERS_REGEX;

public abstract class AbstractRestServiceDelegate {

  /** Logger. */
  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractRestServiceDelegate.class);

  public static final String STAMP_UUID_HDR_NAME = "stamp-uuid";

  private static final String VALDE_CONTENT = "content";

  /** Default content type. */
  private static final MediaType DEFAULT_CONTENT_TYPE = new MediaType("application", "xml", Charset.forName("UTF-8"));

  /** Rest template. */
  private transient final RestTemplate restTemplate;

  /** URL to be requested. */
  private URL url;

  /** Format XLM or use String approach */
  private transient FormatConverter formatConverter;

  private final List<HttpStatus> httpStatusesToRequeue;

  public AbstractRestServiceDelegate(final RestTemplate restTemplate,
                                     final FormatConverter formatConverter,
                                     final URL url,
                                     final List<HttpStatus> httpStatusesToRequeue) {
    this.restTemplate = checkNotNull(restTemplate);
    this.formatConverter = formatConverter;
    this.httpStatusesToRequeue = checkNotNull(httpStatusesToRequeue);
    this.url = url;
  }

  /**
   * Executes POST request.
   *
   * @param <T> the generic type
   * @param body request body
   * @param resourcePattern request pattern for placeholders substitutions
   * @param args arguments for placeholders substitutions
   * @return response object of he request
   * @throws ServiceException the service exception
   * @throws InfrastructureException the infrastructure exception
   */
  protected <T> ResponseEntity<String> post(final T body, final String resourcePattern, final Object... args)
      throws ServiceException,
      InfrastructureException {
    return executeHttpRequest(HttpMethod.POST, newHttpBody(body), resourcePattern, args);
  }

  /**
   * Executes POST request.
   *
   * @param <T> the generic type
   * @param body request body
   * @param headers request headers
   * @param resourcePattern request pattern for placeholders substitutions
   * @param args arguments for placeholders substitutions
   * @return response object of he request
   * @throws ServiceException the service exception
   * @throws InfrastructureException the infrastructure exception
   */
  protected <T> ResponseEntity<String> post(final T body, Map<String, String> headers, final String resourcePattern,
                                            final Object... args)
      throws ServiceException,
      InfrastructureException {
    return executeHttpRequest(HttpMethod.POST, newHttpBody(body, headers), resourcePattern, args);
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

  /**
   * Executes PUT request.
   *
   * @param <T> the generic type
   * @param body request body
   * @param resourcePattern request pattern for placeholders substitutions
   * @param args arguments for placeholders substitutions
   * @return response object of he request
   * @throws ServiceException the service exception
   * @throws InfrastructureException the infrastructure exception
   */
  protected <T> ResponseEntity<String> put(final T body, final String resourcePattern, final Object... args)
      throws ServiceException,
      InfrastructureException {
    return executeHttpRequest(HttpMethod.PUT, newHttpBody(body), resourcePattern, args);
  }

  /**
   * Executes DELETE request.
   *
   * @param resourcePattern request pattern for placeholders substitutions
   * @param args arguments for placeholders substitutions
   * @return response object of he request
   * @throws ServiceException the service exception
   * @throws InfrastructureException the infrastructure exception
   */
  protected ResponseEntity<String> delete(final String resourcePattern, final Object... args)
      throws ServiceException,
      InfrastructureException {
    return executeHttpRequest(HttpMethod.DELETE, newHttpBody(null), resourcePattern, args);
  }

  /**
   * Executes DELETE request.
   *
   * @param resourcePattern request pattern for placeholders substitutions
   * @param args arguments for placeholders substitutions
   * @param headers request headers
   * @return response object of he request
   * @throws ServiceException the service exception
   * @throws InfrastructureException the infrastructure exception
   */
  protected ResponseEntity<String> delete(final String resourcePattern, final Object[] args,
      final Map<String, String> headers) throws ServiceException,
      InfrastructureException {
    return executeHttpRequest(HttpMethod.DELETE, newHttpBody(null, headers), resourcePattern, args);
  }

  /**
   * Executes REST request.
   *
   * @param method http method
   * @param requestEntity request entity
   * @param resourcePattern request pattern for placeholders substitutions
   * @param args arguments for placeholders substitutions
   * @return response object of he request
   * @throws ServiceException the service exception
   * @throws InfrastructureException the infrastructure exception
   */
  protected ResponseEntity<String> executeHttpRequest(final HttpMethod method, final HttpEntity<String> requestEntity,
      final String resourcePattern, final Object[] args) throws ServiceException,
      InfrastructureException {
    try {
      final String requestUrl = getUrl(format(resourcePattern, args));

      if (requestEntity.getHeaders().containsKey(STAMP_UUID_HDR_NAME)) {
        LOGGER.debug("Message {} - Executing http request url: {} method: {}",
            requestEntity.getHeaders().get(STAMP_UUID_HDR_NAME).get(0), requestUrl, method);
      } else {
        LOGGER.debug("Executing http request url: {} method: {}", requestUrl, method);
      }

      final ResponseEntity<String> response = restTemplate.exchange(requestUrl, method, requestEntity, String.class);

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

  /**
   * Creates request body.
   *
   * @param <T> the generic type
   * @param body object to be put into the request body
   * @return request body
   */
  private <T> HttpEntity<String> newHttpBody(final T body) {
    return newHttpBody(body, new HashMap<String, String>());
  }

  /**
   * New http body.
   *
   * @param <T> the generic type
   * @param body the body
   * @param headers the headers
   * @return the http entity
   */
  private <T> HttpEntity<String> newHttpBody(final T body, final Map<String, String> headers) {
    final HttpHeaders httpHeaders = new HttpHeaders();
    httpHeaders.setContentType(DEFAULT_CONTENT_TYPE);
    httpHeaders.setAll(headers);

    return new HttpEntity<String>(formatConverter.convert(body), httpHeaders);
  }

  /**
   * Formats pattern by substitution placeholders with values.
   *
   * @param resourcePattern request pattern for placeholders substitutions
   * @param args arguments for placeholders substitutions
   * @return the string
   */
  protected String format(final String resourcePattern, final Object... args) {
    String formatted = resourcePattern;
    for (int i = 0; i < args.length; i++) {
      formatted = formatted.replace("{" + (i + 1) + "}", String.valueOf(args[i]));
    }
    return formatted;
  }

  /**
   * Gets URL value for resource.
   *
   * @param resource resource
   * @return URL value
   */
  protected String getUrl(final String resource) {
    try {
      return new URL(url + resource).toString();
    } catch (final MalformedURLException e) {
      throw new IllegalArgumentException(e);
    }
  }

  protected <T> void logInfo(final T body, final String format, Object... args) {
      LOGGER.info(format, args);
  }
}
