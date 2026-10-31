package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.martinspielmann.haveibeenpwned4j.mapper.DomainVerificationTokenMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.ResponseMapper;
import de.martinspielmann.haveibeenpwned4j.model.DomainVerificationToken;

public class DefaultDomainVerificationTokenMapper implements DomainVerificationTokenMapper {

  private static final DefaultDomainVerificationTokenMapper INSTANCE =
      new DefaultDomainVerificationTokenMapper();

  public static DefaultDomainVerificationTokenMapper get() {
    return INSTANCE;
  }

  @Override
  public DomainVerificationToken map(String json) {
    if (json == null || json.isBlank()) {
      return null;
    }
    try {
      return ResponseMapper.mapType(json, DomainVerificationToken.class);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Error parsing json string to domain verification token", e);
    }
  }
}
