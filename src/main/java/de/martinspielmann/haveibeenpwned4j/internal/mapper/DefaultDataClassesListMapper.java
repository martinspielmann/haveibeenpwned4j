package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import java.util.ArrayList;
import java.util.List;

import javax.script.ScriptException;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.json.ScriptEngineHolder;
import de.martinspielmann.haveibeenpwned4j.mapper.DataClassesListMapper;

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
    return parseJsonToJavaList(json);
  }

  @SuppressWarnings("unchecked")
  protected List<String> parseJsonToJavaList(String json) {
    String script = "Java.asJSONCompatible(" + json + ")";
    try {
      return (List<String>) ScriptEngineHolder.get().getScriptEngine().eval(script);
    } catch (ScriptException e) {
      throw new IllegalStateException("Error parsing json string to list", e);
    }
  }

}
