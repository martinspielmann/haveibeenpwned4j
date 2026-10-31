package de.martinspielmann.haveibeenpwned4j.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Represents a breach incident returned by the Have I Been Pwned API v3.
 *
 * @param name Pascal-cased unique name representing the breach
 * @param title Descriptive title suitable for displaying to end users
 * @param domain Domain of the primary website the breach occurred on
 * @param breachDate Date the breach originally occurred
 * @param addedDate Date and time the breach was added to HIBP
 * @param modifiedDate Date and time the breach was last modified in HIBP
 * @param pwnCount Total number of accounts loaded into the system for this breach
 * @param description HTML overview of the breach
 * @param dataClasses Alphabetically ordered list of compromised data classes
 * @param verified Whether the breach has been verified
 * @param fabricated Whether the breach is considered fabricated
 * @param sensitive Whether the breach is considered sensitive
 * @param retired Whether the breach has been retired
 * @param spamList Whether the breach is considered a spam list
 * @param malware Whether the breach is sourced from malware
 * @param subscriptionFree Whether the breach is subscription-free in domain searches
 * @param stealerLog Whether the breach is sourced from stealer logs
 * @param logoPath URI/filename of the breached service's PNG logo
 * @param attribution Attribution text if requested by the data provider
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public record Breach(
    String name,
    String title,
    String domain,
    LocalDate breachDate,
    ZonedDateTime addedDate,
    ZonedDateTime modifiedDate,
    Integer pwnCount,
    String description,
    List<String> dataClasses,
    @JsonProperty("IsVerified") Boolean verified,
    @JsonProperty("IsFabricated") Boolean fabricated,
    @JsonProperty("IsSensitive") Boolean sensitive,
    @JsonProperty("IsRetired") Boolean retired,
    @JsonProperty("IsSpamList") Boolean spamList,
    @JsonProperty("IsMalware") Boolean malware,
    @JsonProperty("IsSubscriptionFree") Boolean subscriptionFree,
    @JsonProperty("IsStealerLog") Boolean stealerLog,
    String logoPath,
    String attribution) implements Serializable {
}

