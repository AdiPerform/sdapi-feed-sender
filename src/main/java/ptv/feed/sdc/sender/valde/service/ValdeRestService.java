package ptv.feed.sdc.sender.valde.service;

import ptv.feed.sdc.sender.exceptions.InfrastructureException;
import ptv.feed.sdc.sender.exceptions.ServiceException;

import java.util.Map;

public interface ValdeRestService {
  void updateSt1(String feed, Map<String, String> headers) throws ServiceException, InfrastructureException;
}