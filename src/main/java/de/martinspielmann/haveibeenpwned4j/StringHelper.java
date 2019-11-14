package de.martinspielmann.haveibeenpwned4j;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class StringHelper {

  private static final String SHA_1_ALGORITHM = "SHA-1";
  private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

  private StringHelper() {

  }

  /**
   * The API allows to check passwords directly. To avoid url encoding issues with complicated
   * passwords, we use the possibility to check sha1 hashes
   *
   * @param pw the password
   * @return sha1 hash of the given password
   * @throws HaveIBeenPwnedException
   * @throws NoSuchAlgorithmException if SHA-1 digest not available
   */
  public static String sha1(String pw) throws HaveIBeenPwnedException {
    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance(SHA_1_ALGORITHM);
    } catch (NoSuchAlgorithmException e) {
      throw new HaveIBeenPwnedException("Unable to get digest for " + SHA_1_ALGORITHM, e);
    }
    byte[] encodedhash = digest.digest(pw.getBytes(StandardCharsets.UTF_8));
    return toHexString(encodedhash);
  }

  public static String toHexString(byte[] bytes) {
    char[] hexChars = new char[bytes.length * 2];
    for (int j = 0; j < bytes.length; j++) {
      int v = bytes[j] & 0xFF;
      hexChars[j * 2] = HEX_ARRAY[v >>> 4];
      hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
    }
    return new String(hexChars);
  }

  public static String getHashPrefix(String password) throws HaveIBeenPwnedException {
    return sha1(password).substring(0, 5);
  }

  public static String getHashSuffix(String password) throws HaveIBeenPwnedException {
    return sha1(password).substring(5);
  }

  public static String urlEncode(String input) {
    return URLEncoder.encode(input, StandardCharsets.UTF_8);
  }
}
