package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import java.util.ArrayList;
import java.util.List;

import java.util.Map;
import java.util.stream.Collectors;
import javax.script.ScriptException;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.json.ScriptEngineHolder;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.json.SimpleJsonMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.BreachListMapper;
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
    return parseJsonToJavaList(json).stream().map(SimpleJsonMapper::parseBreach)
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
