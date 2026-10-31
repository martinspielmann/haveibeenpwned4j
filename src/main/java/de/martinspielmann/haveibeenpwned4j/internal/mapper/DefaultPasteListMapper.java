package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import de.martinspielmann.haveibeenpwned4j.mapper.PasteListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.ResponseMapper;
import de.martinspielmann.haveibeenpwned4j.model.Paste;

public class DefaultPasteListMapper implements PasteListMapper {

  private static final DefaultPasteListMapper INSTANCE = new DefaultPasteListMapper();

  public static DefaultPasteListMapper get() {
    return INSTANCE;
  }

  @Override
  public List<Paste> map(String json) {
    if (json == null || json.isBlank()) {
      return new ArrayList<>();
    }
    try {
      return List.of(ResponseMapper.mapType(json, Paste[].class));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Error parsing json string to paste list", e);
    }
  }
}

