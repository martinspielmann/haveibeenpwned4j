package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;

import de.martinspielmann.haveibeenpwned4j.mapper.PasteListMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.ResponseMapper;
import de.martinspielmann.haveibeenpwned4j.model.Paste;

public class DefaultPasteListMapper implements PasteListMapper {

  private static DefaultPasteListMapper instance;

  public static DefaultPasteListMapper get() {
    if (instance == null) {
      instance = new DefaultPasteListMapper();
    }
    return instance;
  }

  @Override
  public List<Paste> map(String json) {
    if (json == null || "".equals(json)) {
      return new ArrayList<>();
    }
    try {
      return List.of(ResponseMapper.mapType(json, Paste[].class));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Error parsing json string to paste list", e);
    }    
  }
}
