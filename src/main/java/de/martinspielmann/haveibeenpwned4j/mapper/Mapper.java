package de.martinspielmann.haveibeenpwned4j.mapper;

public interface Mapper<T> {

  T map(String json);
}
