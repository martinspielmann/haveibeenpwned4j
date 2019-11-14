package de.martinspielmann.haveibeenpwned4j;

public class HaveIBeenPwnedException extends RuntimeException {

  private static final long serialVersionUID = 1L;

  public HaveIBeenPwnedException(String message, Throwable cause) {
    super(message, cause);
  }

  public HaveIBeenPwnedException(String message, Status status) {
    super(message + String.format("[Response status: %s]", status));
  }

}
