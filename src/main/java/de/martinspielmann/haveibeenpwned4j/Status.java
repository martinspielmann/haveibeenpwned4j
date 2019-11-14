package de.martinspielmann.haveibeenpwned4j;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the status of HIBP API response.
 *
 * @author Martin Spielmann
 */
public enum Status {

  UNKNOWN_API_ERROR(-1), 
  OK(200), 
  BAD_REQUEST(400), 
  UNAUTHORIZED(401), 
  FORBIDDEN(403), 
  NOT_FOUND(404), 
  TOO_MANY_REQUESTS(429),
  SERVICE_UNAVAILABLE(503);

  private int code;
  private static final Map<Integer, Status> map = new HashMap<>();

  static {
    for (Status s : Status.values()) {
      map.put(s.code, s);
    }
  }

  Status(int code) {
    this.code = code;
  }

  public int getCode() {
    return code;
  }

  public static Status of(int code) {
    Status s = map.get(code);
    if (s == null) {
      s = UNKNOWN_API_ERROR;
    }
    return s;
  }
}
