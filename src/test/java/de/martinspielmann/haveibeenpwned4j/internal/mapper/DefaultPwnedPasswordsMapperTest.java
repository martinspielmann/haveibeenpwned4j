package de.martinspielmann.haveibeenpwned4j.internal.mapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import de.martinspielmann.haveibeenpwned4j.HaveIBeenPwnedException;
import de.martinspielmann.haveibeenpwned4j.model.HashMode;

class DefaultPwnedPasswordsMapperTest {

  @Test
  void testGet() {
    assertNotNull(DefaultPwnedPasswordsMapper.get());
  }

  @Test
  void testMapPasswordFound() {
    DefaultPwnedPasswordsMapper mapper = DefaultPwnedPasswordsMapper.get();
    String body = "7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33:3\r\n7B5EA3F0FDBC95D0DD47F3C5BC275DA8A32:1";
    assertTrue(mapper.map(200, body, "foo"));
    assertEquals(3L, mapper.mapCount(200, body, "foo"));
  }

  @Test
  void testMapPasswordPaddedWithZeroCount() {
    DefaultPwnedPasswordsMapper mapper = DefaultPwnedPasswordsMapper.get();
    String body = "7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33:0\n7B5EA3F0FDBC95D0DD47F3C5BC275DA8A32:1";
    assertFalse(mapper.map(200, body, "foo"));
    assertEquals(0L, mapper.mapCount(200, body, "foo"));
  }

  @Test
  void testMapNtlmPasswordFound() {
    DefaultPwnedPasswordsMapper mapper = DefaultPwnedPasswordsMapper.get();
    String body = "57F83DF82BEEA5D43BDAF7800CC:42\n000000000000000000000000000:0";
    assertEquals(42L, mapper.mapCount(200, body, "foo", HashMode.NTLM));
  }

  @Test
  void testMapPasswordNotInResultSetOrMalformedLines() {
    DefaultPwnedPasswordsMapper mapper = DefaultPwnedPasswordsMapper.get();
    String body = "\nmalformed\n7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33:notanumber\n7B5EA3F0FDBC95D0DD47F3C5BC275DA8A34:3";
    assertFalse(mapper.map(200, body, "foo"));
    assertFalse(mapper.map(200, "", "foo"));
    assertFalse(mapper.map(200, null, "foo"));
  }

  @Test
  void testMapPasswordNothingFoundAtAll() {
    DefaultPwnedPasswordsMapper mapper = DefaultPwnedPasswordsMapper.get();
    assertFalse(mapper.map(404, null, "foo"));
    assertEquals(0L, mapper.mapCount(404, null, "foo"));
  }

  @Test
  void testMapSomeError() {
    DefaultPwnedPasswordsMapper mapper = DefaultPwnedPasswordsMapper.get();
    assertThrows(HaveIBeenPwnedException.class, () -> mapper.map(401, null, "foo"));
  }
}

