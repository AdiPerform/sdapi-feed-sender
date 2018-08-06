package ptv.feed.sdc.sender.exceptions;

public class InfrastructureException extends Exception {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4592426360518650532L;

  /**
   * Instantiates a new infrastructure exception.
   */
  public InfrastructureException() {
    super();
  }

  /**
   * Instantiates a new infrastructure exception.
   * 
   * @param cause the cause
   */
  public InfrastructureException(final Throwable cause) {
    super(cause);
  }

  /**
   * Instantiates a new infrastructure exception.
   * 
   * @param message the message
   */
  public InfrastructureException(final String message) {
    super(message);
  }

  /**
   * Instantiates a new infrastructure exception.
   * 
   * @param message the message
   * @param cause the cause
   */
  public InfrastructureException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
