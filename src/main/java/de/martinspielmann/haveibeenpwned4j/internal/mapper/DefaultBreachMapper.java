package de.martinspielmann.haveibeenpwned4j.internal.mapper;


import java.util.Map;
import javax.script.ScriptException;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.json.ScriptEngineHolder;
import de.martinspielmann.haveibeenpwned4j.internal.mapper.json.SimpleJsonMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.BreachMapper;
import de.martinspielmann.haveibeenpwned4j.model.Breach;

public class DefaultBreachMapper implements BreachMapper {

  private static DefaultBreachMapper instance;

  public static DefaultBreachMapper get() {
    if (instance == null) {
      instance = new DefaultBreachMapper();
    }
    return instance;
  }

  @Override
  public Breach map(String json) {
    if (json == null || "".equals(json)) {
      return null;
    }
    return SimpleJsonMapper.parseBreach(parseJsonToJavaObject(json));
  }

  @SuppressWarnings("unchecked")
  protected Map<String, Object> parseJsonToJavaObject(String json) {
    String script = "Java.asJSONCompatible(" + json + ")";
    try {
      return (Map<String, Object>) ScriptEngineHolder.get().getScriptEngine().eval(script);
    } catch (ScriptException e) {
      throw new IllegalStateException("Error parsing json string to object", e);
    }
  }

}
