package de.martinspielmann.haveibeenpwned4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the status of HIBP API response.
 *
 * @author Martin Spielmann
 */
public enum Status {

  UNKNOWN_API_ERROR(-1, true), 
  OK(200, false), 
  BAD_REQUEST(400, true), 
  UNAUTHORIZED(401, true), 
  FORBIDDEN(403, true), 
  NOT_FOUND(404, false), 
  TOO_MANY_REQUESTS(429, true),
  SERVICE_UNAVAILABLE(503, true);

  private int code;
  private boolean isError;

  private static final Map<Integer, Status> map = new HashMap<>();

  static {
    for (Status s : Status.values()) {
      map.put(s.code, s);
    }
  }

  Status(int code, boolean isError) {
    this.code = code;
    this.isError = isError;
  }

  public int getCode() {
    return code;
  }
  
  public boolean isError() {
    return isError;
  }

  public static Status of(int code) {
    Status s = map.get(code);
    if (s == null) {
      s = UNKNOWN_API_ERROR;
    }
    return s;
  }
}
