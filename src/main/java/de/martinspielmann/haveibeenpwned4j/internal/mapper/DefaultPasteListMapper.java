package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;
import javax.script.ScriptException;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.json.ScriptEngineHolder;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.json.SimpleJsonMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.PasteListMapper;
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
    return parseJsonToJavaList(json).stream().map(SimpleJsonMapper::parsePaste)
        .collect(Collectors.toList());
  }

  @SuppressWarnings("unchecked")
  protected List<Map<String, Object>> parseJsonToJavaList(String json) {
    String script = "Java.asJSONCompatible(" + json + ")";
    try {
      return (List<Map<String, Object>>) ScriptEngineHolder.get().getScriptEngine().eval(script);
    } catch (ScriptException e) {
      throw new IllegalStateException("Error parsing json string to list", e);
    }
  }

}
