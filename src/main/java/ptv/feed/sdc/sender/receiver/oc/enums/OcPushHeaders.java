package ptv.feed.sdc.sender.receiver.oc.enums;

public enum OcPushHeaders {
  OC_COMMAND("oc_command"),
  OC_TYPE("oc_type"),
  OC_TIMESTAMP("oc_timestamp"),
  OC_LAST_UPDATED("oc_last_updated"),
  OC_FORMAT("oc_format"),
  OC_STAMP("stamp_uuid"),
  OC_UUID("oc_uuid"),
  OC_PARAMS("oc_params"),

  OC_OPERATION("oc_operation"),
  OC_HEADERS_PATTERN("oc_*");

  private final String headerName;

  private OcPushHeaders(final String headerName) {
    this.headerName = headerName;
  }

  public String getHeaderName() {
    return headerName;
  }
}
