package de.martinspielmann.haveibeenpwned4j.internal.mapper.json;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

public class ScriptEngineHolder {

  private static ScriptEngineHolder instance;
  private ScriptEngine engine;

  private ScriptEngineHolder() {
    this.engine = new ScriptEngineManager().getEngineByName("javascript");
  }

  public static ScriptEngineHolder get() {
    if (instance == null) {
      instance = new ScriptEngineHolder();
    }
    return instance;
  }

  public ScriptEngine getScriptEngine() {
    return this.engine;
  }

}
