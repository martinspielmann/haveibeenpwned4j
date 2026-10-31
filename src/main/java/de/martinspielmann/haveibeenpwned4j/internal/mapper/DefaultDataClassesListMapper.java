package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import de.martinspielmann.haveibeenpwned4j.mapper.DataClassesListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.ResponseMapper;

public class DefaultDataClassesListMapper implements DataClassesListMapper {

  private static final DefaultDataClassesListMapper INSTANCE = new DefaultDataClassesListMapper();

  public static DefaultDataClassesListMapper get() {
    return INSTANCE;
  }

  @Override
  public List<String> map(String json) {
    if (json == null || json.isBlank()) {
      return new ArrayList<>();
    }
    try {
      return List.of(ResponseMapper.mapType(json, String[].class));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Error parsing json string to list", e);
    }
  }
}

