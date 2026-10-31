package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import de.martinspielmann.haveibeenpwned4j.mapper.DataClassDescriptionsListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.ResponseMapper;
import de.martinspielmann.haveibeenpwned4j.model.DataClassDescription;

public class DefaultDataClassDescriptionsListMapper implements DataClassDescriptionsListMapper {

  private static final DefaultDataClassDescriptionsListMapper INSTANCE =
      new DefaultDataClassDescriptionsListMapper();

  public static DefaultDataClassDescriptionsListMapper get() {
    return INSTANCE;
  }

  @Override
  public List<DataClassDescription> map(String json) {
    if (json == null || json.isBlank()) {
      return new ArrayList<>();
    }
    try {
      return List.of(ResponseMapper.mapType(json, DataClassDescription[].class));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Error parsing json string to data class descriptions list", e);
    }
  }
}
