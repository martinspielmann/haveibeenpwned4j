package de.martinspielmann.haveibeenpwned4j;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Represents the HTTP status of a Have I Been Pwned API response.
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

  private static final Map<Integer, Status> BY_CODE =
      Arrays.stream(values()).collect(Collectors.toUnmodifiableMap(Status::getCode, Function.identity()));

  private final int code;
  private final boolean isError;

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
    return BY_CODE.getOrDefault(code, UNKNOWN_API_ERROR);
  }
}

