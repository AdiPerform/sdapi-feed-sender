package ptv.feed.sdc.sender.valdeFromSoccer.delegate;

import ptv.feed.sdc.sender.valdeFromSoccer.exceptions.InfrastructureException;
import ptv.feed.sdc.sender.valdeFromSoccer.exceptions.ServiceException;

import java.util.Map;

public interface St1ServiceDelegate {
  void updateSt1(String feed, Map<String, String> headers) throws ServiceException, InfrastructureException;
}