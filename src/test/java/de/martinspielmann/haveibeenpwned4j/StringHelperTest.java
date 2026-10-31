package de.martinspielmann.haveibeenpwned4j;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class StringHelperTest {

  @Test
  void testSha1() {
    assertEquals("0BEEC7B5EA3F0FDBC95D0DD47F3C5BC275DA8A33", StringHelper.sha1("foo"));
  }

  @Test
  void testNtlm() {
    assertEquals("31D6CFE0D16AE931B73C59D7E0C089C0", StringHelper.ntlm(""));
    assertEquals("AC8E657F83DF82BEEA5D43BDAF7800CC", StringHelper.ntlm("foo"));
    assertEquals("8846F7EAEE8FB117AD06BDD830B7586C", StringHelper.ntlm("password"));
    assertEquals(32, StringHelper.ntlm("a-very-long-password-that-exceeds-fifty-six-bytes-in-utf16le").length());
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
  void testGetNtlmHashPrefixAndSuffix() {
    assertEquals("AC8E6", StringHelper.getNtlmHashPrefix("foo"));
    assertEquals("57F83DF82BEEA5D43BDAF7800CC", StringHelper.getNtlmHashSuffix("foo"));
  }

  @Test
  void testGetAccountHashPrefixAndSuffix() {
    String email = "  Multiple-Breaches@hibp-integration-tests.com ";
    assertEquals("6B5917", StringHelper.getAccountHashPrefix(email));
    assertEquals("C6C0AADE0C085843D66E4944E108C4A4CD", StringHelper.getAccountHashSuffix(email));
  }

  @Test
  void testUrlEncode() {
    assertEquals("foo%40bar.baz", StringHelper.urlEncode("foo@bar.baz"));
    assertEquals("foo%20bar%2Bbaz%40example.com", StringHelper.urlEncode("foo bar+baz@example.com"));
  }

  @Test
  void testNullChecks() {
    assertThrows(NullPointerException.class, () -> StringHelper.sha1(null));
    assertThrows(NullPointerException.class, () -> StringHelper.ntlm(null));
    assertThrows(NullPointerException.class, () -> StringHelper.toHexString(null));
    assertThrows(NullPointerException.class, () -> StringHelper.urlEncode(null));
    assertThrows(NullPointerException.class, () -> StringHelper.getAccountHashPrefix(null));
  }
}

