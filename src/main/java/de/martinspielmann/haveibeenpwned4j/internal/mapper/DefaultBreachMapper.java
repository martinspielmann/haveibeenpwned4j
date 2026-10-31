package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import de.martinspielmann.haveibeenpwned4j.mapper.BreachMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.ResponseMapper;
import de.martinspielmann.haveibeenpwned4j.model.Breach;

public class DefaultBreachMapper implements BreachMapper {

  private static final DefaultBreachMapper INSTANCE = new DefaultBreachMapper();

  public static DefaultBreachMapper get() {
    return INSTANCE;
  }

  @Override
  public Breach map(String json) {
    if (json == null || json.isBlank()) {
      return null;
    }
    try {
      return ResponseMapper.mapType(json, Breach.class);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Error parsing json string to breach", e);
    }
  }
}

