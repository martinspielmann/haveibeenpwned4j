package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.martinspielmann.haveibeenpwned4j.mapper.DataClassesListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.ResponseMapper;

public class DefaultDataClassesListMapper implements DataClassesListMapper {

  private static DefaultDataClassesListMapper instance;

  public static DefaultDataClassesListMapper get() {
    if (instance == null) {
      instance = new DefaultDataClassesListMapper();
    }
    return instance;
  }

  @Override
  public List<String> map(String json) {
    if (json == null || "".equals(json)) {
      return new ArrayList<>();
    }
    try {
      return List.of(ResponseMapper.mapType(json, String[].class));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Error parsing json string to list", e);
    }
  }
}
