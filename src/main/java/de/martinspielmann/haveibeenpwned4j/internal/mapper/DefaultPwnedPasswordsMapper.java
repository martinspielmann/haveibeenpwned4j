package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import de.martinspielmann.haveibeenpwned4j.HaveIBeenPwnedException;
import de.martinspielmann.haveibeenpwned4j.Status;
import de.martinspielmann.haveibeenpwned4j.StringHelper;
import de.martinspielmann.haveibeenpwned4j.mapper.PwnedPasswordsMapper;
import de.martinspielmann.haveibeenpwned4j.model.HashMode;

public class DefaultPwnedPasswordsMapper implements PwnedPasswordsMapper {

  private static final DefaultPwnedPasswordsMapper INSTANCE = new DefaultPwnedPasswordsMapper();

  public static DefaultPwnedPasswordsMapper get() {
    return INSTANCE;
  }

  @Override
  public boolean map(int responseStatus, String responseBody, String password) {
    return mapCount(responseStatus, responseBody, password, HashMode.SHA1) > 0;
  }

  @Override
  public long mapCount(int responseStatus, String responseBody, String password, HashMode hashMode) {
    Status status = Status.of(responseStatus);
    if (status == Status.NOT_FOUND) {
      return 0L;
    }
    if (status == Status.OK) {
      if (responseBody == null || responseBody.isBlank()) {
        return 0L;
      }
      String hashSuffix = hashMode == HashMode.NTLM
          ? StringHelper.getNtlmHashSuffix(password)
          : StringHelper.getHashSuffix(password);
      return responseBody.lines()
          .map(String::trim)
          .filter(line -> !line.isEmpty())
          .mapToLong(line -> parseMatchingCount(line, hashSuffix))
          .filter(count -> count > 0)
          .findFirst()
          .orElse(0L);
    }
    throw new HaveIBeenPwnedException("Error checking for pwned password", status);
  }

  private static long parseMatchingCount(String line, String hashSuffix) {
    int colonIndex = line.indexOf(':');
    if (colonIndex <= 0 || colonIndex == line.length() - 1) {
      return 0L;
    }
    String suffix = line.substring(0, colonIndex).trim();
    if (!suffix.equalsIgnoreCase(hashSuffix)) {
      return 0L;
    }
    try {
      return Long.parseLong(line.substring(colonIndex + 1).trim());
    } catch (NumberFormatException _) {
      return 0L;
    }
  }
}

