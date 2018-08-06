package ptv.feed.sdc.sender.receiver.valde.delegate;

import ptv.feed.sdc.sender.exceptions.InfrastructureException;
import ptv.feed.sdc.sender.exceptions.ServiceException;

import java.util.Map;

public interface St1ServiceDelegate {
  void updateSt1(String feed, Map<String, String> headers) throws ServiceException, InfrastructureException;
}