package de.martinspielmann.haveibeenpwned4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * The Class StringHelper provides common utilities to work with strings.
 */
public class StringHelper {

  private static final String SHA_1_ALGORITHM = "SHA-1";
  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  private StringHelper() {

  }

  /**
   * Get the SHA-1 hash of the given string.
   *
   * @param string the string
   * @return the SHA-1 hash of the string
   */
  public static String sha1(String string) {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance(SHA_1_ALGORITHM);
    } catch (NoSuchAlgorithmException e) {
      throw new HaveIBeenPwnedException("Unable to get digest for " + SHA_1_ALGORITHM, e);
    }
    byte[] encodedhash = digest.digest(string.getBytes(StandardCharsets.UTF_8));
    return toHexString(encodedhash);
  }

  /**
   * To hex string.
   *
   * @param bytes the bytes
   * @return the string
   */
  public static String toHexString(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  /**
   * Gets the hash prefix.
   *
   * @param password the password
   * @return the hash prefix
   */
  public static String getHashPrefix(String password) {
    return sha1(password).substring(0, 5);
  }

  /**
   * Gets the hash suffix.
   *
   * @param password the password
   * @return the hash suffix
   */
  public static String getHashSuffix(String password) {
    return sha1(password).substring(5);
  }

  /**
   * Url encode.
   *
   * @param input the input
   * @return the string
   */
  public static String urlEncode(String input) {
    return URLEncoder.encode(input, StandardCharsets.UTF_8);
  }
}
