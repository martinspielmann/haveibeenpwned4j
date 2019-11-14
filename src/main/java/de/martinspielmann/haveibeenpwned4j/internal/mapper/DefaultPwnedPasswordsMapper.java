package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import de.martinspielmann.haveibeenpwned4j.HaveIBeenPwnedException;
import de.martinspielmann.haveibeenpwned4j.Status;
import de.martinspielmann.haveibeenpwned4j.StringHelper;
import de.martinspielmann.haveibeenpwned4j.mapper.PwnedPasswordsMapper;

public class DefaultPwnedPasswordsMapper implements PwnedPasswordsMapper {

  private static DefaultPwnedPasswordsMapper instance;

  public static DefaultPwnedPasswordsMapper get() {
    if (instance == null) {
      instance = new DefaultPwnedPasswordsMapper();
    }
    return instance;
  }

  @Override
  public boolean map(int responseStatus, String responseBody, String password) {
    Status status = Status.of(responseStatus);
    // if nothing is found or everything is OK
    if (status.equals(Status.NOT_FOUND)) {
      return false;
    } else if (status.equals(Status.OK)) {
      // hash prefix is found. check if the actual password is included
      // if there were results, check if your pw hash was pwned
      String hashSuffix = StringHelper.getHashSuffix(password);
      String[] lines = responseBody.split("\\r?\\n");
      for (String line : lines) {
        if (line.split(":")[0].equals(hashSuffix)) {
          return true;
        }
      }
      return false;
    } else {
      throw new HaveIBeenPwnedException("Error checking for pwned password", status);
    }
  }

}
