package ptv.feed.sdc.sender.valdeFromSoccer.exceptions;

/**
 * The Class ServiceException.
 */
public class ServiceException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5496836886307332576L;

  /**
   * Instantiates a new service exception.
   */
  public ServiceException() {
    super();
  }

  /**
   * Instantiates a new service exception.
   * 
   * @param message the message
   */
  public ServiceException(final String message) {
    super(message);
  }

  /**
   * Instantiates a new service exception.
   * 
   * @param message the message
   * @param cause the cause
   */
  public ServiceException(final String message, final Throwable cause) {
    super(message, cause);
  }

  /**
   * Instantiates a new service exception.
   * 
   * @param cause the cause
   */
  public ServiceException(final Throwable cause) {
    super(cause);
  }
}
