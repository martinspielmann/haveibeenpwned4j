package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.JsonProcessingException;
import de.martinspielmann.haveibeenpwned4j.mapper.ResponseMapper;
import de.martinspielmann.haveibeenpwned4j.mapper.SubscribedDomainsListMapper;
import de.martinspielmann.haveibeenpwned4j.model.SubscribedDomain;

public class DefaultSubscribedDomainsListMapper implements SubscribedDomainsListMapper {

  private static final DefaultSubscribedDomainsListMapper INSTANCE =
      new DefaultSubscribedDomainsListMapper();

  public static DefaultSubscribedDomainsListMapper get() {
    return INSTANCE;
  }

  @Override
  public List<SubscribedDomain> map(String json) {
    if (json == null || json.isBlank()) {
      return new ArrayList<>();
    }
    try {
      return List.of(ResponseMapper.mapType(json, SubscribedDomain[].class));
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Error parsing json string to subscribed domains list", e);
    }
  }
}
