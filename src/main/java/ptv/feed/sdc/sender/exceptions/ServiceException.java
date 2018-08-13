package ptv.feed.sdc.sender.exceptions;


public class ServiceException extends Exception {

  private static final long serialVersionUID = 5496836886307332576L;

  public ServiceException() {
    super();
  }

  public ServiceException(final String message) {
    super(message);
  }

  public ServiceException(final String message, final Throwable cause) {
    super(message, cause);
  }

  public ServiceException(final Throwable cause) {
    super(cause);
  }
}
