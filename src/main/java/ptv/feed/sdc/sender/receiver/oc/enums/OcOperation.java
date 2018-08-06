package ptv.feed.sdc.sender.receiver.oc.enums;

public enum OcOperation {
  DELETE("delete");

  private final String operation;

  OcOperation(final String operation) {
    this.operation = operation;
  }

  public String operation() {
    return operation;
  }

  public static boolean isDelete(String operation) {
    return DELETE.operation().equals(operation);
  }
}
