package de.martinspielmann.haveibeenpwned4j.mapper;

public interface PwnedPasswordsMapper {

  boolean map(int responseStatus, String responseBody, String password);

}
