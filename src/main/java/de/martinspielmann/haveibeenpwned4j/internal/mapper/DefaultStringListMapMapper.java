package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import de.martinspielmann.haveibeenpwned4j.mapper.ResponseMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.StringListMapMapper;

public class DefaultStringListMapMapper implements StringListMapMapper {

  private static final DefaultStringListMapMapper INSTANCE = new DefaultStringListMapMapper();
  private static final TypeReference<Map<String, List<String>>> MAP_TYPE = new TypeReference<>() {};

  public static DefaultStringListMapMapper get() {
    return INSTANCE;
  }

  @Override
  public Map<String, List<String>> map(String json) {
    if (json == null || json.isBlank()) {
      return Collections.emptyMap();
    }
    try {
      return ResponseMapper.mapType(json, MAP_TYPE);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Error parsing json string to map", e);
    }
  }
}
