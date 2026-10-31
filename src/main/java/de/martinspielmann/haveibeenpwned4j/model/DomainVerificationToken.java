package de.martinspielmann.haveibeenpwned4j.model;

import java.io.Serializable;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Represents a DNS TXT record verification token returned by the HIBP domain verification API.
 *
 * @param txtRecordValue TXT record value to be set in DNS (e.g., "hibp-verify=dweb_...")
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record DomainVerificationToken(
    @JsonAlias("TxtRecordValue") String txtRecordValue) implements Serializable {
}
