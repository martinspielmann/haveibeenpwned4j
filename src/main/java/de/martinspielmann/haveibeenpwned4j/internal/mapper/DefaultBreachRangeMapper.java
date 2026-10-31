package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import java.util.Collections;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import de.martinspielmann.haveibeenpwned4j.HaveIBeenPwnedException;
import de.martinspielmann.haveibeenpwned4j.Status;
import de.martinspielmann.haveibeenpwned4j.StringHelper;
import de.martinspielmann.haveibeenpwned4j.mapper.BreachRangeMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.ResponseMapper;

public class DefaultBreachRangeMapper implements BreachRangeMapper {

  private static final DefaultBreachRangeMapper INSTANCE = new DefaultBreachRangeMapper();

  @JsonIgnoreProperties(ignoreUnknown = true)
  private record RangeEntry(
      @JsonAlias("HashSuffix") String hashSuffix,
      @JsonAlias("Websites") List<String> websites) {
  }

  public static DefaultBreachRangeMapper get() {
    return INSTANCE;
  }

  @Override
  public List<String> map(int responseStatus, String responseBody, String account) {
    Status status = Status.of(responseStatus);
    if (status == Status.NOT_FOUND) {
      return Collections.emptyList();
    }
    if (status == Status.OK) {
      if (responseBody == null || responseBody.isBlank()) {
        return Collections.emptyList();
      }
      String targetSuffix = StringHelper.getAccountHashSuffix(account);
      try {
        RangeEntry[] entries = ResponseMapper.mapType(responseBody, RangeEntry[].class);
        for (RangeEntry entry : entries) {
          if (entry != null && entry.hashSuffix() != null
              && entry.hashSuffix().equalsIgnoreCase(targetSuffix)) {
            return entry.websites() != null ? List.copyOf(entry.websites()) : Collections.emptyList();
          }
        }
        return Collections.emptyList();
      } catch (JsonProcessingException e) {
        throw new IllegalStateException("Error parsing json string to breach range list", e);
      }
    }
    throw new HaveIBeenPwnedException("Error checking for breached account range", status);
  }
}
