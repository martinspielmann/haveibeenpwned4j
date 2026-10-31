package de.martinspielmann.haveibeenpwned4j;

import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Locale;
import java.util.Objects;

/**
 * Provides common string, hashing, and encoding utilities for HIBP API requests.
 */
public final class StringHelper {

  private static final String SHA_1_ALGORITHM = "SHA-1";
  private static final HexFormat UPPER_HEX = HexFormat.of().withUpperCase();

  private StringHelper() {
  }

  /**
   * Computes the uppercase SHA-1 hex digest of the given string (UTF-8).
   *
   * @param string the input string
   * @return the 40-character uppercase SHA-1 hex string
   */
  public static String sha1(String string) {
    Objects.requireNonNull(string, "string must not be null");
    try {
      MessageDigest digest = MessageDigest.getInstance(SHA_1_ALGORITHM);
      byte[] encodedHash = digest.digest(string.getBytes(StandardCharsets.UTF_8));
      return toHexString(encodedHash);
    } catch (NoSuchAlgorithmException e) {
      throw new HaveIBeenPwnedException("Unable to get digest for " + SHA_1_ALGORITHM, e);
    }
  }

  /**
   * Computes the uppercase NTLM (MD4 over UTF-16LE) hex digest of the given string.
   *
   * @param string the input string
   * @return the 32-character uppercase NTLM hex string
   */
  public static String ntlm(String string) {
    Objects.requireNonNull(string, "string must not be null");
    return toHexString(md4(string.getBytes(StandardCharsets.UTF_16LE)));
  }

  /**
   * Formats a byte array as an uppercase hexadecimal string.
   *
   * @param bytes the bytes
   * @return the uppercase hex string
   */
  public static String toHexString(byte[] bytes) {
    Objects.requireNonNull(bytes, "bytes must not be null");
    return UPPER_HEX.formatHex(bytes);
  }

  /**
   * Gets the 5-character SHA-1 hash prefix for Pwned Passwords k-anonymity range queries.
   *
   * @param password the password
   * @return the 5-character hash prefix
   */
  public static String getHashPrefix(String password) {
    return sha1(password).substring(0, 5);
  }

  /**
   * Gets the 35-character SHA-1 hash suffix for Pwned Passwords k-anonymity range queries.
   *
   * @param password the password
   * @return the 35-character hash suffix
   */
  public static String getHashSuffix(String password) {
    return sha1(password).substring(5);
  }

  /**
   * Gets the 5-character NTLM hash prefix for Pwned Passwords NTLM range queries.
   *
   * @param password the password
   * @return the 5-character NTLM hash prefix
   */
  public static String getNtlmHashPrefix(String password) {
    return ntlm(password).substring(0, 5);
  }

  /**
   * Gets the 27-character NTLM hash suffix for Pwned Passwords NTLM range queries.
   *
   * @param password the password
   * @return the 27-character NTLM hash suffix
   */
  public static String getNtlmHashSuffix(String password) {
    return ntlm(password).substring(5);
  }

  /**
   * Gets the 6-character SHA-1 hash prefix for email address k-anonymity range queries.
   *
   * @param account the email address
   * @return the 6-character SHA-1 hash prefix
   */
  public static String getAccountHashPrefix(String account) {
    return sha1(normalizeAccount(account)).substring(0, 6);
  }

  /**
   * Gets the 34-character SHA-1 hash suffix for email address k-anonymity range queries.
   *
   * @param account the email address
   * @return the 34-character SHA-1 hash suffix
   */
  public static String getAccountHashSuffix(String account) {
    return sha1(normalizeAccount(account)).substring(6);
  }

  /**
   * URL-encodes the given input using UTF-8.
   *
   * @param input the input string
   * @return the URL-encoded string
   */
  public static String urlEncode(String input) {
    Objects.requireNonNull(input, "input must not be null");
    return URLEncoder.encode(input, StandardCharsets.UTF_8).replace("+", "%20");
  }

  private static String normalizeAccount(String account) {
    Objects.requireNonNull(account, "account must not be null");
    return account.trim().toLowerCase(Locale.ROOT);
  }

  private static byte[] md4(byte[] input) {
    int origLen = input.length;
    int tailLen = origLen & 63;
    int padLen = (tailLen < 56) ? (56 - tailLen) : (120 - tailLen);
    byte[] padded = new byte[origLen + padLen + 8];
    System.arraycopy(input, 0, padded, 0, origLen);
    padded[origLen] = (byte) 0x80;
    long bitLen = (long) origLen << 3;
    ByteBuffer.wrap(padded, origLen + padLen, 8).order(ByteOrder.LITTLE_ENDIAN).putLong(bitLen);

    int a = 0x67452301;
    int b = 0xefcdab89;
    int c = 0x98badcfe;
    int d = 0x10325476;
    int[] x = new int[16];

    for (int offset = 0; offset < padded.length; offset += 64) {
      ByteBuffer.wrap(padded, offset, 64).order(ByteOrder.LITTLE_ENDIAN).asIntBuffer().get(x);
      int aa = a;
      int bb = b;
      int cc = c;
      int dd = d;

      int[] r1 = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15};
      int[] s1 = {3, 7, 11, 19};
      for (int i = 0; i < 16; i++) {
        int f = (b & c) | (~b & d);
        int temp = Integer.rotateLeft(a + f + x[r1[i]], s1[i & 3]);
        a = d;
        d = c;
        c = b;
        b = temp;
      }

      int[] r2 = {0, 4, 8, 12, 1, 5, 9, 13, 2, 6, 10, 14, 3, 7, 11, 15};
      int[] s2 = {3, 5, 9, 13};
      for (int i = 0; i < 16; i++) {
        int g = (b & c) | (b & d) | (c & d);
        int temp = Integer.rotateLeft(a + g + x[r2[i]] + 0x5a827999, s2[i & 3]);
        a = d;
        d = c;
        c = b;
        b = temp;
      }

      int[] r3 = {0, 8, 4, 12, 2, 10, 6, 14, 1, 9, 5, 13, 3, 11, 7, 15};
      int[] s3 = {3, 9, 11, 15};
      for (int i = 0; i < 16; i++) {
        int h = b ^ c ^ d;
        int temp = Integer.rotateLeft(a + h + x[r3[i]] + 0x6ed9eba1, s3[i & 3]);
        a = d;
        d = c;
        c = b;
        b = temp;
      }

      a += aa;
      b += bb;
      c += cc;
      d += dd;
    }

    byte[] out = new byte[16];
    ByteBuffer.wrap(out).order(ByteOrder.LITTLE_ENDIAN).putInt(a).putInt(b).putInt(c).putInt(d);
    return out;
  }
}

