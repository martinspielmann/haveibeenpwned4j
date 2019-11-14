package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import de.martinspielmann.haveibeenpwned4j.HaveIBeenPwnedException;

class DefaultPwnedPasswordsMapperTest {

  @Test
  void testGet() {
    assertNotNull(DefaultPwnedPasswordsMapper.get());
  }

  @Test
  void testMapPasswordFound() {
    DefaultPwnedPasswordsMapper mapper = DefaultPwnedPasswordsMapper.get();
    boolean mapped = mapper.map(200,
        "7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33:3\n7B5EA3F0FDBC95D0DD47F3C5BC275DA8A32:1", "foo");
    assertTrue(mapped);
  }

  @Test
  void testMapPasswordNotInResultSet() {
    DefaultPwnedPasswordsMapper mapper = DefaultPwnedPasswordsMapper.get();
    boolean mapped = mapper.map(200,
        "7B5EA3F0FDBC95D0DD47F3C5BC275DA8A34:3\n7B5EA3F0FDBC95D0DD47F3C5BC275DA8A32:1", "foo");
    assertFalse(mapped);
  }

  @Test
  void testMapPasswordNothingFoundAtAll() {
    DefaultPwnedPasswordsMapper mapper = DefaultPwnedPasswordsMapper.get();
    boolean mapped = mapper.map(404, null, "foo");
    assertFalse(mapped);
  }

  @Test
  void testMapSomeError() {
    DefaultPwnedPasswordsMapper mapper = DefaultPwnedPasswordsMapper.get();
    assertThrows(HaveIBeenPwnedException.class, () -> mapper.map(401, null, "foo"));
  }
}
