package de.martinspielmann.haveibeenpwned4j;

import java.util.Optional;
import java.util.OptionalLong;

public class HaveIBeenPwnedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  private final Status status;
  private final Long retryAfterSeconds;

  public HaveIBeenPwnedException(String message, Throwable cause) {
    super(message, cause);
    HaveIBeenPwnedException nested = findNested(cause);
    this.status = nested != null ? nested.status : null;
    this.retryAfterSeconds = nested != null ? nested.retryAfterSeconds : null;
  }

  private static HaveIBeenPwnedException findNested(Throwable throwable) {
    Throwable current = throwable;
    while (current != null) {
      if (current instanceof HaveIBeenPwnedException hibpException) {
        return hibpException;
      }
      current = current.getCause();
    }
    return null;
  }

  public HaveIBeenPwnedException(String message, Status status) {
    this(message, status, null);
  }

  public HaveIBeenPwnedException(String message, Status status, Long retryAfterSeconds) {
    super(formatMessage(message, status, retryAfterSeconds));
    this.status = status;
    this.retryAfterSeconds = retryAfterSeconds;
  }

  public Optional<Status> getStatus() {
    return Optional.ofNullable(status);
  }

  public OptionalLong getRetryAfterSeconds() {
    return retryAfterSeconds != null ? OptionalLong.of(retryAfterSeconds) : OptionalLong.empty();
  }

  private static String formatMessage(String message, Status status, Long retryAfterSeconds) {
    if (retryAfterSeconds != null) {
      return "%s [Response status: %s, retry-after: %ds]".formatted(message, status, retryAfterSeconds);
    }
    return "%s[Response status: %s]".formatted(message, status);
  }
}

