package de.martinspielmann.haveibeenpwned4j.model;

import java.io.Serializable;
import java.time.ZonedDateTime;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

/**
 * Represents the status and capabilities of the current HIBP API subscription.
 *
 * @param subscriptionName Name representing the current plan and level (e.g., "Pro 2")
 * @param description Human-readable sentence explaining the scope of the subscription
 * @param subscribedUntil Date and time the current subscription ends
 * @param rpm Rate limit in requests per minute
 * @param domainSearchMaxBreachedAccounts Size of the largest domain the subscription can search
 * @param maxBreachedDomains Number of domains the subscription can add, or null if unlimited
 * @param includesStealerLogs Whether the subscription includes access to stealer logs APIs
 * @param includesBulkDomainAdd Whether the subscription includes access to bulk domain verification APIs
 * @param includesAutoSubdomainVerification Whether subdomains are automatically added when the apex domain is verified
 * @param includesCustomerDomains Whether the domains of customers can be added
 * @param includesKAnon Whether the subscription includes access to k-anonymity email address search
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.UpperCamelCaseStrategy.class)
public record SubscriptionStatus(
    String subscriptionName,
    String description,
    ZonedDateTime subscribedUntil,
    Integer rpm,
    Integer domainSearchMaxBreachedAccounts,
    Integer maxBreachedDomains,
    Boolean includesStealerLogs,
    Boolean includesBulkDomainAdd,
    Boolean includesAutoSubdomainVerification,
    Boolean includesCustomerDomains,
    Boolean includesKAnon) implements Serializable {
}
