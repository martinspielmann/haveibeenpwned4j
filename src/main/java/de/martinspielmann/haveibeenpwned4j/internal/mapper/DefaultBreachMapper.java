package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.martinspielmann.haveibeenpwned4j.mapper.BreachMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.ResponseMapper;
import de.martinspielmann.haveibeenpwned4j.model.Breach;

public class DefaultBreachMapper implements BreachMapper {

  private static DefaultBreachMapper instance;

  public static DefaultBreachMapper get() {
    if (instance == null) {
      instance = new DefaultBreachMapper();
    }
    return instance;
  }

  @Override
  public Breach map(String json) {
    if (json == null || "".equals(json)) {
      return null;
    }
    try {
      return ResponseMapper.mapType(json, Breach.class);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Error parsing json string to breach", e);
    }
  }
}
