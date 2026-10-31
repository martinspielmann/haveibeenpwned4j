package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.martinspielmann.haveibeenpwned4j.mapper.ResponseMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.SubscriptionStatusMapper;
import de.martinspielmann.haveibeenpwned4j.model.SubscriptionStatus;

public class DefaultSubscriptionStatusMapper implements SubscriptionStatusMapper {

  private static final DefaultSubscriptionStatusMapper INSTANCE =
      new DefaultSubscriptionStatusMapper();

  public static DefaultSubscriptionStatusMapper get() {
    return INSTANCE;
  }

  @Override
  public SubscriptionStatus map(String json) {
    if (json == null || json.isBlank()) {
      return null;
    }
    try {
      return ResponseMapper.mapType(json, SubscriptionStatus.class);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Error parsing json string to subscription status", e);
    }
  }
}
