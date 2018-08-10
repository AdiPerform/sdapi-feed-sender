package ptv.feed.sdc.sender.valde.enums;

public enum ValdeHeaders {
  VALDE_VERSION("valde_version"),
  VALDE_FEED_PARAMETERS("valde_feedParameters"),
  VALDE_MESSAGE_DIGEST("valde_messageDigest"),
  VALDE_PRODUCTION_SERVER_MODULE("valde_productionServerModule"),
  VALDE_MATCHDAY("valde_matchday"),
  VALDE_GAME_SYSTEM_ID("valde_gameSystemId"),
  VALDE_SPORT_ID("valde_sportId"),
  VALDE_AWAY_TEAM_ID("valde_awayTeamId"),
  VALDE_HOME_TEAM_ID("valde_homeTeamId"),
  VALDE_SEASON_ID("valde_seasonId"),
  VALDE_ENCODING("valde_encoding"),
  VALDE_MIME_TYPE("valde_mimeType"),
  VALDE_PRODUCTION_SERVER("valde_productionServer"),
  VALDE_DELIVERY_TYPE("valde_deliveryType"),
  VALDE_GAME_ID("valde_gameId"),
  VALDE_LAST_UPDATED("valde_lastUpdated"),
  VALDE_COMPETITION_ID("valde_competitionId"),
  VALDE_GAME_STATUS("valde_gameStatus"),
  VALDE_DEFAULT_FILENAME("valde_defaultFilename"),
  VALDE_PRODUCTION_SERVER_TIME_STAMP("valde_productionServerTimeStamp"),
  VALDE_FEED_TYPE("valde_feedType"),
  VALDE_DATE_CREATED("valde_dateCreated"),
  VALDE_PRODUCTION_TIME_TAKEN("valde_productionTimeTaken");

  public static final String VALDE_HEADERS_PREFIX = "valde_";
  public static final String VALDE_HEADERS_REGEX = "^valde_*";

  private final String headerName;

  private ValdeHeaders(final String headerName) {
    this.headerName = headerName;
  }

  public String getHeaderName() {
    return headerName;
  }
}
