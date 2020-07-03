package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import java.util.ArrayList;
import java.util.List;


import com.fasterxml.jackson.core.JsonProcessingException;

import de.martinspielmann.haveibeenpwned4j.mapper.BreachListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.ResponseMapper;
import de.martinspielmann.haveibeenpwned4j.model.Breach;

public class DefaultBreachListMapper implements BreachListMapper {

  private static DefaultBreachListMapper instance;

  public static DefaultBreachListMapper get() {
    if (instance == null) {
      instance = new DefaultBreachListMapper();
    }
    return instance;
  }

  @Override
  public List<Breach> map(String json) {
    if (json == null || "".equals(json)) {
      return new ArrayList<>();
    }
    try {
      return List.of(ResponseMapper.mapType(json, Breach[].class));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Error parsing json string to breach list", e);
    }
  }
}
