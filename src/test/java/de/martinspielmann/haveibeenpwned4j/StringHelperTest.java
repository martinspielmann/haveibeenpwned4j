package de.martinspielmann.haveibeenpwned4j;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class StringHelperTest {

  @Test
  void testSha1() {
    assertEquals("0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33", StringHelper.sha1("foo"));
  }

  @Test
  void testToHexString() {
    assertEquals("18191A1B", StringHelper.toHexString(new byte[] {24, 25, 26, 27}));
  }

  @Test
  void testGetHashPrefix() {
    assertEquals("0BEEC", StringHelper.getHashPrefix("foo"));
  }

  @Test
  void testGetHashSuffix() {
    assertEquals("7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33", StringHelper.getHashSuffix("foo"));
  }

  @Test
  void testUrlEncode() {
    assertEquals("foo%40bar.baz", StringHelper.urlEncode("foo@bar.baz"));
  }

}
