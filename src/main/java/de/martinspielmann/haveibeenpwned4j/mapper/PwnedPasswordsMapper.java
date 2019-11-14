package de.martinspielmann.haveibeenpwned4j.mapper;

import de.martinspielmann.haveibeenpwned4j.HaveIBeenPwnedException;

public interface PwnedPasswordsMapper {

  boolean map(int responseStatus, String responseBody, String password)
      throws HaveIBeenPwnedException;

}
