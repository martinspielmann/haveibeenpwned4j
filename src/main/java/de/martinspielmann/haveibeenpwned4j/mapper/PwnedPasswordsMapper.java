package de.martinspielmann.haveibeenpwned4j.mapper;

import de.martinspielmann.haveibeenpwned4j.model.HashMode;

public interface PwnedPasswordsMapper {

  boolean map(int responseStatus, String responseBody, String password);

  default long mapCount(int responseStatus, String responseBody, String password) {
    return mapCount(responseStatus, responseBody, password, HashMode.SHA1);
  }

  default long mapCount(int responseStatus, String responseBody, String password, HashMode hashMode) {
    return map(responseStatus, responseBody, password) ? 1L : 0L;
  }
}

