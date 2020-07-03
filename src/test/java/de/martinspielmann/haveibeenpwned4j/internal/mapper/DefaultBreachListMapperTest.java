package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class DefaultBreachListMapperTest {

  @Test
  void testGet() {
    assertNotNull(DefaultBreachListMapper.get());
  }

  @Test
  void testMapNull() {
    DefaultBreachListMapper mapper = DefaultBreachListMapper.get();
    // size should be 0 but list not null
    assertEquals(0, mapper.map(null).size());
  }

}
