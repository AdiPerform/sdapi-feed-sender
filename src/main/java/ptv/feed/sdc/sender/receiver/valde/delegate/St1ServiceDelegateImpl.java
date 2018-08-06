package ptv.feed.sdc.sender.receiver.valde.delegate;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;
import ptv.feed.sdc.sender.messaging.FormatConverter;
import ptv.feed.sdc.sender.receiver.valde.enums.ValdeHeaders;
import ptv.feed.sdc.sender.exceptions.InfrastructureException;
import ptv.feed.sdc.sender.exceptions.ServiceException;

import java.net.URL;
import java.util.List;
import java.util.Map;


public class St1ServiceDelegateImpl extends AbstractRestServiceDelegate implements St1ServiceDelegate {

  public St1ServiceDelegateImpl(final RestTemplate restTemplate,
                                final FormatConverter formatConverter,
                                final URL url,
                                final List<HttpStatus> httpStatusesToRequeue) {
    super(restTemplate, formatConverter, url, httpStatusesToRequeue);
  }

  @Override
  public void updateSt1(String feed, Map<String, String> headers) throws ServiceException, InfrastructureException {
    logInfo(feed, "Updating ST1 feed for game id: {}", headers.get(ValdeHeaders.VALDE_GAME_ID.getHeaderName()));
    postToValde(feed, headers);
    logInfo(feed, "Updated ST1 feed for game id: {}", headers.get(ValdeHeaders.VALDE_GAME_ID.getHeaderName()));
  }
}
