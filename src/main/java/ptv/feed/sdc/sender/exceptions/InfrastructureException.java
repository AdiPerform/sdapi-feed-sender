package ptv.feed.sdc.sender.exceptions;

public class InfrastructureException extends Exception {

  private static final long serialVersionUID = -4592426360518650532L;

  public InfrastructureException() {
    super();
  }

  public InfrastructureException(final Throwable cause) {
    super(cause);
  }

  public InfrastructureException(final String message) {
    super(message);
  }

  public InfrastructureException(final String message, final Throwable cause) {
    super(message, cause);
  }
}
