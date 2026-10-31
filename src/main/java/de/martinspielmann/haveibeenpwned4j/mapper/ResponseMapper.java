package de.martinspielmann.haveibeenpwned4j.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public final class ResponseMapper {

  private static final ObjectMapper MAPPER = JsonMapper.builder()
      .addModule(new JavaTimeModule())
      .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
      .build();

  private ResponseMapper() {
  }

  public static <T> T mapType(String content, Class<T> valueType)
      throws JsonProcessingException {
    return MAPPER.readValue(content, valueType);
  }

  public static <T> T mapType(String content, TypeReference<T> valueTypeRef)
      throws JsonProcessingException {
    return MAPPER.readValue(content, valueTypeRef);
  }

  public static String toJson(Object value) throws JsonProcessingException {
    return MAPPER.writeValueAsString(value);
  }
}
