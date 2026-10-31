package de.martinspielmann.haveibeenpwned4j.model;

/**
 * Hash mode supported by the Pwned Passwords range API.
 *
 * @see <a href="https://haveibeenpwned.com/API/v3#PwnedPasswordsNTLM">Searching for NTLM hashes</a>
 */
public enum HashMode {

  SHA1("sha1"),
  NTLM("ntlm");

  private final String queryValue;

  HashMode(String queryValue) {
    this.queryValue = queryValue;
  }

  public String queryValue() {
    return queryValue;
  }
}
