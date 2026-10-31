package de.martinspielmann.haveibeenpwned4j.model;

import java.io.Serializable;
import java.time.ZonedDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Represents a verified domain associated with an HIBP subscription.
 *
 * @param domainName Full domain name that has been verified
 * @param pwnCount Total number of breached email addresses found on the domain at last search
 * @param pwnCountExcludingSpamLists Breached email addresses on the domain excluding spam lists
 * @param pwnCountExcludingSpamListsAtLastSubscriptionRenewal Breached email addresses at last renewal
 * @param nextSubscriptionRenewal Date and time the current subscription ends
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public record SubscribedDomain(
    String domainName,
    Integer pwnCount,
    Integer pwnCountExcludingSpamLists,
    Integer pwnCountExcludingSpamListsAtLastSubscriptionRenewal,
    ZonedDateTime nextSubscriptionRenewal) implements Serializable {
}
