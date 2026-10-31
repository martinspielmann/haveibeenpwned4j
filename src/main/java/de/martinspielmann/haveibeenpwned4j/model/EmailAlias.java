package de.martinspielmann.haveibeenpwned4j.model;

/**
 * Allowable email aliases for HIBP domain verification via email.
 *
 * @see <a href="https://haveibeenpwned.com/API/v3#VerifyDomainViaEmail">Verifying domains via email</a>
 */
public enum EmailAlias {

  ADMIN("admin"),
  HOSTMASTER("hostmaster"),
  INFO("info"),
  SECURITY("security"),
  WEBMASTER("webmaster");

  private final String value;

  EmailAlias(String value) {
    this.value = value;
  }

  public String value() {
    return value;
  }
}
