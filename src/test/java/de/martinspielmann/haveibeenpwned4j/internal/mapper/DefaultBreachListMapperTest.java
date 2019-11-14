package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import static org.junit.jupiter.api.Assertions.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

  @Test
  void testMap() {
    DefaultBreachListMapper mapper = new DefaultBreachListMapper() {
      @Override
      protected List<Map<String, Object>> parseJsonToJavaList(String json) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Name", "foo");
        return List.of(map);
      }
    };
    // size should be 0 but list not null
    assertEquals("foo", mapper.map(null).get(0).getName());
  }

//  @Test
//  void testParseJsonToJavaList() {
//    fail("Not yet implemented");
//  }

}
